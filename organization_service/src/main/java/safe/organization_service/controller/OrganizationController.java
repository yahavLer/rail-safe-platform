package safe.organization_service.controller;

import safe.organization_service.boundary.*;
import safe.organization_service.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
/**
 * REST controller for organization configuration:
 * - Create organization (initializes risk matrix)
 * - Read/Update risk matrix descriptions
 * - Manage organization categories
 */
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public OrganizationBoundary create(@Valid @RequestBody CreateOrganizationBoundary input) {
        return service.createOrganization(input);
    }

    @GetMapping("/{orgId}")
    public OrganizationBoundary getOrg(@PathVariable UUID orgId) {
        return service.getOrganization(orgId);
    }

    @GetMapping("/{orgId}/risk-matrix")
    public RiskMatrixBoundary getRiskMatrix(@PathVariable UUID orgId) {
        return service.getRiskMatrix(orgId);
    }

    /** Update frequency description for a fixed level (1..4) */
    @PatchMapping("/{orgId}/risk-matrix/frequency/{level}")
    public LevelDefinitionBoundary updateFrequencyDescription(
            @PathVariable UUID orgId,
            @PathVariable int level,
            @Valid @RequestBody UpdateDescriptionBoundary input
    ) {
        return service.updateFrequencyDescription(orgId, level, input);
    }

    /** Update severity description for a fixed level (1..4) */
    @PatchMapping("/{orgId}/risk-matrix/severity/{level}")
    public LevelDefinitionBoundary updateSeverityDescription(
            @PathVariable UUID orgId,
            @PathVariable int level,
            @Valid @RequestBody UpdateDescriptionBoundary input
    ) {
        return service.updateSeverityDescription(orgId, level, input);
    }

    // ---------------- Categories (optional but recommended) ----------------

    @PostMapping("/{orgId}/categories")
    public CategoryBoundary createCategory(@PathVariable UUID orgId, @Valid @RequestBody CreateCategoryBoundary input) {
        return service.createCategory(orgId, input);
    }

    @GetMapping("/{orgId}/categories")
    public List<CategoryBoundary> listCategories(@PathVariable UUID orgId) {
        return service.listCategories(orgId);
    }

    @PatchMapping("/{orgId}/categories/{categoryId}")
    public CategoryBoundary updateCategory(
            @PathVariable UUID orgId,
            @PathVariable UUID categoryId,
            @RequestBody UpdateCategoryBoundary input
    ) {
        return service.updateCategory(orgId, categoryId, input);
    }

    @DeleteMapping("/{orgId}/categories/{categoryId}")
    public void deleteCategory(@PathVariable UUID orgId, @PathVariable UUID categoryId) {
        service.deleteCategory(orgId, categoryId);
    }
}