package safe.organization_service.serviceImpl;


import org.springframework.data.domain.Sort;
import safe.organization_service.boundary.*;
import safe.organization_service.entity.*;
import safe.organization_service.exception.BadRequestException;
import safe.organization_service.exception.OrganizationNotFoundException;
import safe.organization_service.repository.*;
import safe.organization_service.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
/**
 * Implements organization configuration logic:
 * - Create org
 * - Initialize fixed risk matrix levels (1..4) with fixed labels
 * - Update only descriptions (not level number / not label)
 */
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository orgRepo;
    private final FrequencyLevelRepository freqRepo;
    private final SeverityLevelRepository sevRepo;
    private final RiskCategoryRepository categoryRepo;
    private final PasswordEncoder passwordEncoder;

    public OrganizationServiceImpl(
            OrganizationRepository orgRepo,
            FrequencyLevelRepository freqRepo,
            SeverityLevelRepository sevRepo,
            RiskCategoryRepository categoryRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.orgRepo = orgRepo;
        this.freqRepo = freqRepo;
        this.sevRepo = sevRepo;
        this.categoryRepo = categoryRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OrganizationBoundary createOrganization(CreateOrganizationBoundary input) {
        OrganizationEntity org = new OrganizationEntity();
        org.setName(input.getName());
        org.setPasswordHash(passwordEncoder.encode(input.getPassword()));
        org = orgRepo.save(org);

        // Create default fixed labels with empty descriptions.
        initDefaultFrequencyLevels(org);
        initDefaultSeverityLevels(org);

        return toBoundary(org);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationBoundary getOrganization(UUID orgId) {
        OrganizationEntity org = orgRepo.findById(orgId).orElseThrow(() -> new OrganizationNotFoundException(orgId));
        return toBoundary(org);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskMatrixBoundary getRiskMatrix(UUID orgId) {
        assertOrgExists(orgId);

        List<LevelDefinitionBoundary> freq = freqRepo.findByOrganization_IdOrderByLevelAsc(orgId).stream()
                .map(e -> toLevelBoundary(e.getLevel(), e.getLabel(), e.getDescription()))
                .toList();

        List<LevelDefinitionBoundary> sev = sevRepo.findByOrganization_IdOrderByLevelAsc(orgId).stream()
                .map(e -> toLevelBoundary(e.getLevel(), e.getLabel(), e.getDescription()))
                .toList();

        // Safety check: ensure exactly 4 levels exist
        if (freq.size() != 4 || sev.size() != 4) {
            throw new BadRequestException("Risk matrix is not initialized properly (expected 4 levels for each dimension)");
        }

        RiskMatrixBoundary out = new RiskMatrixBoundary();
        out.setFrequencyLevels(freq);
        out.setSeverityLevels(sev);
        return out;
    }

    @Override
    public LevelDefinitionBoundary updateFrequencyDescription(UUID orgId, int level, UpdateDescriptionBoundary input) {
        validateLevel(level);
        FrequencyLevelDefinitionEntity e = freqRepo.findByOrganization_IdAndLevel(orgId, level)
                .orElseThrow(() -> new BadRequestException("Frequency level not found for orgId=" + orgId + ", level=" + level));

        // Only description is editable
        e.setDescription(input.getDescription());
        e = freqRepo.save(e);

        return toLevelBoundary(e.getLevel(), e.getLabel(), e.getDescription());
    }

    @Override
    public LevelDefinitionBoundary updateSeverityDescription(UUID orgId, int level, UpdateDescriptionBoundary input) {
        validateLevel(level);
        SeverityLevelDefinitionEntity e = sevRepo.findByOrganization_IdAndLevel(orgId, level)
                .orElseThrow(() -> new BadRequestException("Severity level not found for orgId=" + orgId + ", level=" + level));

        e.setDescription(input.getDescription());
        e = sevRepo.save(e);

        return toLevelBoundary(e.getLevel(), e.getLabel(), e.getDescription());
    }

    // ------------------- Categories (optional but useful) -------------------

    @Override
    public CategoryBoundary createCategory(UUID orgId, CreateCategoryBoundary input) {
        OrganizationEntity org = orgRepo.findById(orgId).orElseThrow(() -> new OrganizationNotFoundException(orgId));
        int nextOrder = categoryRepo.findMaxDisplayOrderByOrganizationId(orgId).orElse(0) + 1;
        String normalizedCode = input.getCode().trim().toUpperCase(Locale.ROOT);

        validateCategoryCode(normalizedCode);

        if (categoryRepo.findByOrganization_IdAndCode(orgId, normalizedCode).isPresent()) {
            throw new BadRequestException("Category code already exists: " + normalizedCode);
        }

        RiskCategoryDefinitionEntity c = new RiskCategoryDefinitionEntity();
        c.setOrganization(org);
        c.setCode(normalizedCode);
        c.setName(input.getName());
        c.setDisplayOrder(nextOrder);
        c.setActive(true);

        return toCategoryBoundary(categoryRepo.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryBoundary> listCategories(UUID orgId) {
        assertOrgExists(orgId);
        return categoryRepo.findByOrganization_IdOrderByDisplayOrderAsc(orgId)
                .stream().map(this::toCategoryBoundary).toList();
    }

    @Override
    public CategoryBoundary updateCategory(UUID orgId, UUID categoryId, UpdateCategoryBoundary input) {
        assertOrgExists(orgId);

        RiskCategoryDefinitionEntity c = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category not found: " + categoryId));

        // Ensure category belongs to the org (basic multi-tenant safety)
        if (!c.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Category does not belong to orgId=" + orgId);
        }

        if (input.getName() != null) c.setName(input.getName());
        if (input.getDisplayOrder() != null) c.setDisplayOrder(input.getDisplayOrder());
        if (input.getActive() != null) c.setActive(input.getActive());

        return toCategoryBoundary(categoryRepo.save(c));
    }

    @Override
    public void deleteCategory(UUID orgId, UUID categoryId) {
        assertOrgExists(orgId);

        RiskCategoryDefinitionEntity c = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category not found: " + categoryId));

        if (!c.getOrganization().getId().equals(orgId)) {
            throw new BadRequestException("Category does not belong to orgId=" + orgId);
        }

        categoryRepo.delete(c);
    }

    // ------------------- Helpers -------------------

    private void initDefaultFrequencyLevels(OrganizationEntity org) {
        // Fixed mapping you requested:
        // 4 - מדי פעם, 3 - תדירות נמוכה, 2 - לא סביר, 1 - נדיר
        createFreq(org, 4, "מדי פעם");
        createFreq(org, 3, "תדירות נמוכה");
        createFreq(org, 2, "לא סביר");
        createFreq(org, 1, "נדיר");
    }

    private void initDefaultSeverityLevels(OrganizationEntity org) {
        // Fixed mapping you requested:
        // 4 - אסון, 3 - קריטי גבוה, 2 - בינוני גבולי, 1 - זניח
        createSev(org, 4, "אסון");
        createSev(org, 3, "קריטי גבוה");
        createSev(org, 2, "בינוני גבולי");
        createSev(org, 1, "זניח");
    }

    private void createFreq(OrganizationEntity org, int level, String label) {
        FrequencyLevelDefinitionEntity e = new FrequencyLevelDefinitionEntity();
        e.setOrganization(org);
        e.setLevel(level);
        e.setLabel(label);
        e.setDescription(""); // start empty; manager will fill later
        freqRepo.save(e);
    }

    private void createSev(OrganizationEntity org, int level, String label) {
        SeverityLevelDefinitionEntity e = new SeverityLevelDefinitionEntity();
        e.setOrganization(org);
        e.setLevel(level);
        e.setLabel(label);
        e.setDescription("");
        sevRepo.save(e);
    }

    private void validateLevel(int level) {
        if (level < 1 || level > 4) {
            throw new BadRequestException("Level must be between 1 and 4");
        }
    }

    private void validateCategoryCode(String code) {
        // 1..10 chars, starts with letter, allowed: A-Z 0-9 _ -
        if (code == null || !code.matches("^[A-Za-z][A-Za-z0-9_-]{0,9}$")) {
            throw new BadRequestException("Category code must be 1-10 chars, start with a letter, and contain only letters/digits/_/-");
        }
    }

    private void assertOrgExists(UUID orgId) {
        if (!orgRepo.existsById(orgId)) {
            throw new OrganizationNotFoundException(orgId);
        }
    }

    private OrganizationBoundary toBoundary(OrganizationEntity org) {
        OrganizationBoundary b = new OrganizationBoundary();
        b.setId(org.getId());
        b.setName(org.getName());
        b.setCreatedAt(org.getCreatedAt());
        b.setUpdatedAt(org.getUpdatedAt());
        return b;
    }

    private LevelDefinitionBoundary toLevelBoundary(int level, String label, String description) {
        LevelDefinitionBoundary b = new LevelDefinitionBoundary();
        b.setLevel(level);
        b.setLabel(label);
        b.setDescription(description);
        return b;
    }

    private CategoryBoundary toCategoryBoundary(RiskCategoryDefinitionEntity c) {
        CategoryBoundary b = new CategoryBoundary();
        b.setId(c.getId());
        b.setCode(c.getCode());
        b.setName(c.getName());
        b.setDisplayOrder(c.getDisplayOrder());
        b.setActive(c.isActive());
        return b;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationBoundary> listOrganizations() {
        return orgRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toBoundary)
                .toList();
    }
}