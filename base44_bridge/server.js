import express from "express";
import cors from "cors";
import * as Base44Sdk from "@base44/sdk";

const createClient =
  Base44Sdk.createClient ?? Base44Sdk.default?.createClient;

if (!createClient) {
  console.error("Could not find createClient in @base44/sdk exports");
  console.error("Available exports:", Object.keys(Base44Sdk));
  process.exit(1);
}

const app = express();

app.use(cors());
app.use(express.json({ limit: "20mb" }));

const {
  BASE44_APP_ID,
  BASE44_SERVER_URL,
  BASE44_TOKEN,
  BASE44_FUNCTIONS_VERSION,
  PORT = 3001,
} = process.env;

if (!BASE44_APP_ID || !BASE44_SERVER_URL || !BASE44_TOKEN) {
  console.error("Missing Base44 environment variables");
  process.exit(1);
}

const base44 = createClient({
  appId: BASE44_APP_ID,
  serverUrl: BASE44_SERVER_URL,
  token: BASE44_TOKEN,
  functionsVersion: BASE44_FUNCTIONS_VERSION || undefined,
  requiresAuth: false,
});

function base64ToFile(
  imageBase64,
  fileName = "risk-image.jpg",
  contentType = "image/jpeg"
) {
  const cleaned = imageBase64.includes(",")
    ? imageBase64.split(",")[1]
    : imageBase64;

  const buffer = Buffer.from(cleaned, "base64");
  const blob = new Blob([buffer], { type: contentType });
  return new File([blob], fileName, { type: contentType });
}

app.get("/health", (_req, res) => {
  res.json({ ok: true, service: "base44-bridge" });
});

app.post("/api/analyze-risk-image", async (req, res) => {
  try {
    const { prompt, imageBase64, fileName, contentType } = req.body;

    if (!prompt || !imageBase64) {
      return res.status(400).json({
        message: "prompt and imageBase64 are required",
      });
    }

    const file = base64ToFile(
      imageBase64,
      fileName || "risk-image.jpg",
      contentType || "image/jpeg"
    );

    const uploadResult = await base44.integrations.Core.UploadFile({ file });
    const fileUrl = uploadResult?.file_url;

    if (!fileUrl) {
      return res.status(500).json({
        message: "Failed to upload file to Base44",
      });
    }

    const aiResult = await base44.integrations.Core.InvokeLLM({
      prompt,
      file_urls: [fileUrl],
      response_json_schema: {
        type: "object",
        properties: {
          hazardDetected: { type: "boolean" },
          title: { type: "string" },
          description: { type: "string" },
          categoryCode: { type: "string" },
          categoryName: { type: "string" },
          severityLevel: { type: "integer" },
          frequencyLevel: { type: "integer" },
          confidence: { type: "number" },
          suggestedMitigations: {
            type: "array",
            items: { type: "string" },
          },
        },
        required: [
          "hazardDetected",
          "title",
          "description",
          "confidence",
          "suggestedMitigations"
        ],
      },
    });

    return res.json({
      hazardDetected: Boolean(aiResult?.hazardDetected),
      title: aiResult?.title ?? "סיכון מזוהה מתמונה",
      description: aiResult?.description ?? "",
      categoryCode: aiResult?.categoryCode ?? null,
      categoryName: aiResult?.categoryName ?? null,
      severityLevel: Number(aiResult?.severityLevel ?? 1),
      frequencyLevel: Number(aiResult?.frequencyLevel ?? 1),
      suggestedMitigations: Array.isArray(aiResult?.suggestedMitigations)
        ? aiResult.suggestedMitigations
        : [],
      confidence: Number(aiResult?.confidence ?? 0),
      fileUrl,
      rawJson: JSON.stringify(aiResult ?? {}),
    });
  } catch (error) {
    console.error("Base44 bridge error:", error);
    return res.status(500).json({
      message: "Failed to analyze image with Base44",
      error: error?.message || "Unknown error",
    });
  }
});

app.listen(PORT, () => {
  console.log(`Base44 bridge listening on port ${PORT}`);
});