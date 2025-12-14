package safe.organization_service.service;

import safe.organization_service.boundary.*;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

    OrganizationBoundary createOrganization(CreateOrganizationBoundary input);

    OrganizationBoundary getOrganization(UUID orgId);

    RiskMatrixBoundary getRiskMatrix(UUID orgId);

    LevelDefinitionBoundary updateFrequencyDescription(UUID orgId, int level, UpdateDescriptionBoundary input);

    LevelDefinitionBoundary updateSeverityDescription(UUID orgId, int level, UpdateDescriptionBoundary input);

    // Optional: categories
    CategoryBoundary createCategory(UUID orgId, CreateCategoryBoundary input);
    List<CategoryBoundary> listCategories(UUID orgId);
    CategoryBoundary updateCategory(UUID orgId, UUID categoryId, UpdateCategoryBoundary input);
    void deleteCategory(UUID orgId, UUID categoryId);
}