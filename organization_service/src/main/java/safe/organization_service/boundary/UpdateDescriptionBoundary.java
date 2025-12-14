package safe.organization_service.boundary;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for updating only the description field */
public class UpdateDescriptionBoundary {

    @NotBlank
    private String description;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}