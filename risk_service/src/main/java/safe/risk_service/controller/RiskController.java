package safe.risk_service.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import safe.risk_service.boundaries.*;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;
import safe.risk_service.service.RiskService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API for risks:
 * - Create / Read / Update / Delete
 * - Filtering
 * - Statistics
 */
@RestController
@RequestMapping("/api/risks")
public class RiskController {

    private final RiskService service;

    public RiskController(RiskService service) {
        this.service = service;
    }

    @PostMapping
    public RiskBoundary create(@Valid @RequestBody CreateRiskBoundary input) {
        return service.create(input);
    }

    @GetMapping("/{riskId}")
    public RiskBoundary getById(@PathVariable UUID riskId) {
        return service.getById(riskId);
    }

    /**
     * Example:
     * GET /api/risks?orgId=...&status=IN_TREATMENT&classification=EXTREME_RED&minScore=8
     */
    @GetMapping
    public List<RiskBoundary> list(
            @RequestParam @NotNull UUID orgId,
            @RequestParam(required = false) UUID divisionId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID riskManagerUserId,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) RiskStatus status,
            @RequestParam(required = false) RiskClassification classification,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore
    ) {
        return service.list(orgId, divisionId, departmentId, riskManagerUserId, categoryCode, status, classification, minScore, maxScore);
    }

    @PatchMapping("/{riskId}")
    public RiskBoundary update(@PathVariable UUID riskId, @Valid @RequestBody UpdateRiskBoundary input) {
        return service.update(riskId, input);
    }

    @PatchMapping("/{riskId}/status")
    public RiskBoundary updateStatus(@PathVariable UUID riskId, @Valid @RequestBody UpdateRiskStatusBoundary input) {
        return service.updateStatus(riskId, input);
    }

    @DeleteMapping("/{riskId}")
    public void delete(@PathVariable UUID riskId) {
        service.delete(riskId);
    }

    // -------- Stats --------

    @GetMapping("/stats/by-status")
    public Map<RiskStatus, Long> countByStatus(@RequestParam UUID orgId) {
        return service.countByStatus(orgId);
    }

    @GetMapping("/stats/by-classification")
    public Map<RiskClassification, Long> countByClassification(@RequestParam UUID orgId) {
        return service.countByClassification(orgId);
    }
}

