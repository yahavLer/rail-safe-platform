package safe.risk_service.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import safe.risk_service.boundaries.*;
import safe.risk_service.entities.RiskEntity;
import safe.risk_service.enums.RiskClassification;
import safe.risk_service.enums.RiskStatus;
import safe.risk_service.repository.RiskRepository;
import safe.risk_service.repository.RiskSpecifications;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main business logic:
 * - compute riskScore = severityLevel * frequencyLevel
 * - compute classification from score
 * - support filters and stats
 */
@Service
@Transactional
public class RiskServiceImpl implements safe.risk_service.service.RiskService {

    private final RiskRepository repo;

    public RiskServiceImpl(RiskRepository repo) {
        this.repo = repo;
    }

    @Override
    public RiskBoundary create(CreateRiskBoundary input) {
        // TODO (recommended): validate categoryCode exists for org via organization_service REST call.

        RiskEntity e = RiskEntity.builder()
                .organizationId(input.getOrganizationId())
                .divisionId(input.getDivisionId())
                .departmentId(input.getDepartmentId())
                .riskManagerUserId(input.getRiskManagerUserId())
                .title(input.getTitle())
                .categoryCode(input.getCategoryCode())
                .description(input.getDescription())
                .severityLevel(input.getSeverityLevel())
                .frequencyLevel(input.getFrequencyLevel())
                .location(input.getLocation())
                .notes(input.getNotes())
                .status(RiskStatus.DRAFT) // default
                .severityAfter(input.getSeverityAfter())
                .frequencyAfter(input.getFrequencyAfter())
                .build();

        applyComputedFields(e);

        return toBoundary(repo.save(e));
    }

    @Override
    public RiskBoundary getById(UUID riskId) {
        RiskEntity e = repo.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));
        return toBoundary(e);
    }

    @Override
    public List<RiskBoundary> list(UUID orgId, UUID divisionId, UUID departmentId, UUID riskManagerUserId,
                                   String categoryCode, RiskStatus status, RiskClassification classification,
                                   Integer minScore, Integer maxScore) {

        Specification<RiskEntity> spec = Specification.where(RiskSpecifications.orgId(orgId))
                .and(divisionId != null ? RiskSpecifications.divisionId(divisionId) : null)
                .and(departmentId != null ? RiskSpecifications.departmentId(departmentId) : null)
                .and(riskManagerUserId != null ? RiskSpecifications.riskManagerUserId(riskManagerUserId) : null)
                .and(categoryCode != null ? RiskSpecifications.categoryCode(categoryCode) : null)
                .and(status != null ? RiskSpecifications.status(status) : null)
                .and(classification != null ? RiskSpecifications.classification(classification) : null)
                .and(RiskSpecifications.scoreBetween(minScore, maxScore));

        return repo.findAll(spec).stream().map(this::toBoundary).toList();
    }

    @Override
    public RiskBoundary update(UUID riskId, UpdateRiskBoundary input) {
        RiskEntity e = repo.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));

        // Update only provided fields
        if (input.getRiskManagerUserId() != null) e.setRiskManagerUserId(input.getRiskManagerUserId());
        if (input.getTitle() != null) e.setTitle(input.getTitle());
        if (input.getCategoryCode() != null) e.setCategoryCode(input.getCategoryCode());
        if (input.getDescription() != null) e.setDescription(input.getDescription());
        if (input.getSeverityLevel() != null) e.setSeverityLevel(input.getSeverityLevel());
        if (input.getFrequencyLevel() != null) e.setFrequencyLevel(input.getFrequencyLevel());
        if (input.getLocation() != null) e.setLocation(input.getLocation());
        if (input.getNotes() != null) e.setNotes(input.getNotes());
        if (input.getSeverityAfter() != null) e.setSeverityAfter(input.getSeverityAfter());
        if (input.getFrequencyAfter() != null) e.setFrequencyAfter(input.getFrequencyAfter());

        // Recompute if needed
        applyComputedFields(e);

        return toBoundary(repo.save(e));
    }

    @Override
    public RiskBoundary updateStatus(UUID riskId, UpdateRiskStatusBoundary input) {
        RiskEntity e = repo.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));

        e.setStatus(input.getStatus());
        return toBoundary(repo.save(e));
    }

    @Override
    public void delete(UUID riskId) {
        if (!repo.existsById(riskId)) {
            throw new IllegalArgumentException("Risk not found: " + riskId);
        }
        repo.deleteById(riskId);
    }

    @Override
    public Map<RiskStatus, Long> countByStatus(UUID orgId) {
        // Simple way: fetch only org risks then count in memory (OK for MVP).
        // Later optimize with DB GROUP BY query.
        return repo.findAll(RiskSpecifications.orgId(orgId))
                .stream()
                .collect(Collectors.groupingBy(RiskEntity::getStatus, Collectors.counting()));
    }

    @Override
    public Map<RiskClassification, Long> countByClassification(UUID orgId) {
        return repo.findAll(RiskSpecifications.orgId(orgId))
                .stream()
                .collect(Collectors.groupingBy(RiskEntity::getClassification, Collectors.counting()));
    }

    // ------------------- Helpers -------------------

    private void applyComputedFields(RiskEntity e) {
        int score = e.getSeverityLevel() * e.getFrequencyLevel();
        e.setRiskScore(score);
        e.setClassification(classify(score));

        // After mitigation (only if both exist)
        if (e.getSeverityAfter() != null && e.getFrequencyAfter() != null) {
            int afterScore = e.getSeverityAfter() * e.getFrequencyAfter();
            e.setScoreAfter(afterScore);
            e.setClassificationAfter(classify(afterScore));
        } else {
            e.setScoreAfter(null);
            e.setClassificationAfter(null);
        }
    }

    private RiskClassification classify(int score) {
        if (score >= 12) return RiskClassification.EXTREME_RED;
        if (score >= 8) return RiskClassification.HIGH_ACTION_ORANGE;
        if (score >= 4) return RiskClassification.TOLERABLE_YELLOW;
        return RiskClassification.NEGLIGIBLE_GREEN;
    }

    private RiskBoundary toBoundary(RiskEntity e) {
        RiskBoundary b = new RiskBoundary();
        b.setId(e.getId());
        b.setOrganizationId(e.getOrganizationId());
        b.setDivisionId(e.getDivisionId());
        b.setDepartmentId(e.getDepartmentId());
        b.setRiskManagerUserId(e.getRiskManagerUserId());

        b.setTitle(e.getTitle());
        b.setCategoryCode(e.getCategoryCode());
        b.setDescription(e.getDescription());

        b.setSeverityLevel(e.getSeverityLevel());
        b.setFrequencyLevel(e.getFrequencyLevel());
        b.setRiskScore(e.getRiskScore());
        b.setClassification(e.getClassification());

        b.setStatus(e.getStatus());

        b.setLocation(e.getLocation());
        b.setNotes(e.getNotes());

        b.setSeverityAfter(e.getSeverityAfter());
        b.setFrequencyAfter(e.getFrequencyAfter());
        b.setScoreAfter(e.getScoreAfter());
        b.setClassificationAfter(e.getClassificationAfter());

        b.setCreatedAt(e.getCreatedAt());
        b.setUpdatedAt(e.getUpdatedAt());
        return b;
    }
}
