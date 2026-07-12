package safe.organization_service.serviceImpl;


import org.springframework.dao.DataIntegrityViolationException;
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
                .map(e -> toFrequencyLevelBoundary(e.getLevel(), e.getDescription()))
                .toList();

        List<LevelDefinitionBoundary> sev = sevRepo.findByOrganization_IdOrderByLevelAsc(orgId).stream()
                .map(e -> toSeverityLevelBoundary(e.getLevel(), e.getDescription()))
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
        String generatedCode = generateNextCategoryCode(orgId);

        RiskCategoryDefinitionEntity c = new RiskCategoryDefinitionEntity();
        c.setOrganization(org);
        c.setCode(generatedCode);
        c.setName(input.getName().trim());
        c.setDisplayOrder(nextOrder);
        c.setActive(true);

        try {
            return toCategoryBoundary(categoryRepo.save(c));
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Category code already exists. Please retry category creation.");
        }
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
        createFreq(org, 4, frequencyLabel(4), frequencyDescription(4));
        createFreq(org, 3, frequencyLabel(3), frequencyDescription(3));
        createFreq(org, 2, frequencyLabel(2), frequencyDescription(2));
        createFreq(org, 1, frequencyLabel(1), frequencyDescription(1));
    }

    private void initDefaultSeverityLevels(OrganizationEntity org) {
        createSev(org, 4, severityLabel(4), severityDescription(4));
        createSev(org, 3, severityLabel(3), severityDescription(3));
        createSev(org, 2, severityLabel(2), severityDescription(2));
        createSev(org, 1, severityLabel(1), severityDescription(1));
    }

    private void createFreq(OrganizationEntity org, int level, String label, String description) {
        FrequencyLevelDefinitionEntity e = new FrequencyLevelDefinitionEntity();
        e.setOrganization(org);
        e.setLevel(level);
        e.setLabel(label);
        e.setDescription(description);
        freqRepo.save(e);
    }

    private void createSev(OrganizationEntity org, int level, String label, String description) {
        SeverityLevelDefinitionEntity e = new SeverityLevelDefinitionEntity();
        e.setOrganization(org);
        e.setLevel(level);
        e.setLabel(label);
        e.setDescription(description);
        sevRepo.save(e);
    }

    private void validateLevel(int level) {
        if (level < 1 || level > 4) {
            throw new BadRequestException("Level must be between 1 and 4");
        }
    }

    private String generateNextCategoryCode(UUID orgId) {
        int maxCode = categoryRepo.findByOrganization_IdOrderByDisplayOrderAsc(orgId).stream()
                .map(RiskCategoryDefinitionEntity::getCode)
                .mapToInt(this::extractCategoryCodeNumber)
                .max()
                .orElse(0);
        return String.valueOf(maxCode + 1);
    }

    private int extractCategoryCodeNumber(String code) {
        if (code == null) return 0;
        String clean = code.trim();
        if (clean.matches("\\d+")) return Integer.parseInt(clean);

        String trailingDigits = clean.replaceFirst("^.*?(\\d+)$", "$1");
        if (trailingDigits.matches("\\d+")) return Integer.parseInt(trailingDigits);
        return 0;
    }

    private LevelDefinitionBoundary toFrequencyLevelBoundary(int level, String description) {
        return toLevelBoundary(level, frequencyLabel(level), hasText(description) ? description : frequencyDescription(level));
    }

    private LevelDefinitionBoundary toSeverityLevelBoundary(int level, String description) {
        return toLevelBoundary(level, severityLabel(level), hasText(description) ? description : severityDescription(level));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String frequencyLabel(int level) {
        return switch (level) {
            case 4 -> "מדי פעם";
            case 3 -> "נמוכה";
            case 2 -> "לא סביר";
            case 1 -> "נדיר";
            default -> "רמה " + level;
        };
    }

    private String frequencyDescription(int level) {
        return switch (level) {
            case 4 -> "X > 10^-3 — בערך אחת לחודש או יותר.";
            case 3 -> "10^-4 < X <= 10^-3 — בין אחת לחודש לאחת לשנה.";
            case 2 -> "10^-5 < X <= 10^-4 — בין אחת לשנה לאחת ל-10 שנים.";
            case 1 -> "X <= 10^-5 — מעל 10 שנים.";
            default -> "";
        };
    }

    private String severityLabel(int level) {
        return switch (level) {
            case 4 -> "אסון";
            case 3 -> "קריטי / גבוהה";
            case 2 -> "בינוני / גבולי";
            case 1 -> "זניח";
            default -> "רמה " + level;
        };
    }

    private String severityDescription(int level) {
        return switch (level) {
            case 4 -> "FWSI >= 10 — הרוגים מרובים ו/או נזק ישיר לרכוש מעל 65 מיליון ₪.";
            case 3 -> "1 <= FWSI < 10 — מספר נמוך של הרוגים ו/או נזק ישיר לרכוש בין 7 ל-65 מיליון ₪.";
            case 2 -> "0.1 <= FWSI < 1 — מספר פצועים קשה ו/או נזק ישיר לרכוש בין 1 ל-7 מיליון ₪.";
            case 1 -> "FWSI < 0.1 — פציעה קלה ו/או נזק ישיר נמוך.";
            default -> "";
        };
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