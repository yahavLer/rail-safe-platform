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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
private static final Logger log = LoggerFactory.getLogger(RiskServiceImpl.class);

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
    log.info(
            "START createRisk: received request. orgId={}, divisionId={}, departmentId={}, riskManagerUserId={}, title={}, categoryCode={}. goal=create risk, calculate score and classification, then save to DB",
            input.getOrgId(),
            input.getDivisionId(),
            input.getDepartmentId(),
            input.getRiskManagerUserId(),
            input.getTitle(),
            input.getCategoryCode()
    );
    try {
         if (input.getOrgId() == null) {
            log.warn("VALIDATION FAILED createRisk: orgId is null");
            throw new IllegalArgumentException("orgId is required");
        }
        RiskEntity e = toEntity(input);
        log.debug(
            "MAPPING SUCCESSFUL createRisk: mapped input to entity. orgId={}, divisionId={}, departmentId={}, riskManagerUserId={}, title={}, categoryCode={}",
            e.getOrgId(),
            e.getDivisionId(),
            e.getDepartmentId(),
            e.getRiskManagerUserId(),
            e.getTitle(),
            e.getCategoryCode()
        );
        applyComputedFields(e);
        log.debug(
                "COMPUTE SUCCESS createRisk: computed fields applied. riskScore={}, classification={}, scoreAfter={}, classificationAfter={}",
                e.getRiskScore(),
                e.getClassification(),
                e.getScoreAfter(),
                e.getClassificationAfter()
        );
        RiskEntity saved = repo.save(e);
        log.info(
                "SUCCESS createRisk: risk saved successfully. riskId={}, orgId={}, categoryCode={}, status={}",
                saved.getId(),
                saved.getOrgId(),
                saved.getCategoryCode(),
                saved.getStatus()
        );
        return toBoundary(saved);
    }
    catch (IllegalArgumentException ex) {
        log.warn(
                "FAILED createRisk: business/validation error. orgId={}, title={}, categoryCode={}, reason={}",
                input.getOrgId(),
                input.getTitle(),
                input.getCategoryCode(),
                ex.getMessage()
        );
        throw ex;
    } catch (Exception ex) {
        log.error(
                "FAILED createRisk: unexpected error while creating risk. orgId={}, title={}, categoryCode={}, errorType={}, errorMessage={}",
                input.getOrgId(),
                input.getTitle(),
                input.getCategoryCode(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );
        throw new RuntimeException("Failed to create risk: " + ex.getMessage(), ex);
    }

    @Override
    public RiskBoundary getById(UUID riskId) {
        log.info("START getRiskById: received riskId={}. goal=fetch risk by id", riskId);

        try {
            RiskEntity e = repo.findById(riskId)
                    .orElseThrow(() -> {
                        log.warn("FAILED getRiskById: risk not found. riskId={}", riskId);
                        return new IllegalArgumentException("Risk not found: " + riskId);
                    });

            log.info("SUCCESS getRiskById: risk found. riskId={}, orgId={}, status={}", e.getId(), e.getOrgId(), e.getStatus());
            return toBoundary(e);

        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("FAILED getRiskById: unexpected error. riskId={}, errorType={}, errorMessage={}",
                    riskId,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }

    @Override
    public List<RiskBoundary> list(UUID orgId,
                                   UUID divisionId,
                                   UUID departmentId,
                                   UUID riskManagerUserId,
                                   String categoryCode,
                                   RiskStatus status,
                                   RiskClassification classification,
                                   Integer minScore,
                                   Integer maxScore) {

        if (orgId == null) {
            throw new IllegalArgumentException("orgId is required");
        }

        // חשוב: לא לעשות and(null) אף פעם
        Specification<RiskEntity> spec = Specification.where(RiskSpecifications.orgId(orgId));

        if (divisionId != null) {
            spec = spec.and(RiskSpecifications.divisionId(divisionId));
        }

        if (departmentId != null) {
            spec = spec.and(RiskSpecifications.departmentId(departmentId));
        }

        if (riskManagerUserId != null) {
            spec = spec.and(RiskSpecifications.riskManagerUserId(riskManagerUserId));
        }

        if (categoryCode != null && !categoryCode.isBlank()) {
            spec = spec.and(RiskSpecifications.categoryCode(categoryCode));
        }

        if (status != null) {
            spec = spec.and(RiskSpecifications.status(status));
        }

        if (classification != null) {
            spec = spec.and(RiskSpecifications.classification(classification));
        }

        // score filters - בטוח גם אם אחד מהם null
        if (minScore != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("riskScore"), minScore));
        }

        if (maxScore != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("riskScore"), maxScore));
        }

        return repo.findAll(spec)
                .stream()
                .map(this::toBoundary)
                .toList();
    }

    @Override
    public RiskBoundary update(UUID riskId, UpdateRiskBoundary input) {
        RiskEntity e = repo.findById(riskId)
                .orElseThrow(() -> new IllegalArgumentException("Risk not found: " + riskId));

        // Update only provided fields
        applyUpdates(e, input);
    
        // Recompute
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
        log.info("START deleteRisk: received riskId={}. goal=delete risk from DB", riskId);

        try {
            if (!repo.existsById(riskId)) {
                log.warn("FAILED deleteRisk: risk not found. riskId={}", riskId);
                throw new IllegalArgumentException("Risk not found: " + riskId);
            }

            repo.deleteById(riskId);
            log.info("SUCCESS deleteRisk: risk deleted successfully. riskId={}", riskId);

        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("FAILED deleteRisk: unexpected error. riskId={}, errorType={}, errorMessage={}",
                    riskId,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex);
            throw ex;
        }
    }
    
    @Override
    public Map<RiskStatus, Long> countByStatus(UUID orgId) {
        if (orgId == null) throw new IllegalArgumentException("orgId is required");

        return repo.findAll(RiskSpecifications.orgId(orgId))
                .stream()
                .collect(Collectors.groupingBy(RiskEntity::getStatus, Collectors.counting()));
    }

    @Override
    public Map<RiskClassification, Long> countByClassification(UUID orgId) {
        if (orgId == null) throw new IllegalArgumentException("orgId is required");

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
        b.setOrgId(e.getOrgId());
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
        b.setSourceImageUrl(e.getSourceImageUrl());

        b.setSeverityAfter(e.getSeverityAfter());
        b.setFrequencyAfter(e.getFrequencyAfter());
        b.setScoreAfter(e.getScoreAfter());
        b.setClassificationAfter(e.getClassificationAfter());

        b.setCreatedAt(e.getCreatedAt());
        b.setUpdatedAt(e.getUpdatedAt());
        return b;
    }
    private RiskEntity toEntity(CreateRiskBoundary input) {
    return RiskEntity.builder()
            .orgId(input.getOrgId())
            .divisionId(input.getDivisionId())
            .departmentId(input.getDepartmentId())
            .riskManagerUserId(input.getRiskManagerUserId())
            .title(input.getTitle())
            .categoryCode(input.getCategoryCode())
            .description(input.getDescription())
            .severityLevel(input.getSeverityLevel())
            .frequencyLevel(input.getFrequencyLevel())
            .location(input.getLocation())
            .sourceImageUrl(input.getSourceImageUrl())
            .notes(input.getNotes())
            .status(RiskStatus.DRAFT)
            .severityAfter(input.getSeverityAfter())
            .frequencyAfter(input.getFrequencyAfter())
            .build();
    }
    private void applyUpdates(RiskEntity e, UpdateRiskBoundary input) {
        if (input.getRiskManagerUserId() != null) e.setRiskManagerUserId(input.getRiskManagerUserId());
        if (input.getTitle() != null) e.setTitle(input.getTitle());
        if (input.getCategoryCode() != null) e.setCategoryCode(input.getCategoryCode());
        if (input.getDescription() != null) e.setDescription(input.getDescription());
        if (input.getSeverityLevel() != null) e.setSeverityLevel(input.getSeverityLevel());
        if (input.getFrequencyLevel() != null) e.setFrequencyLevel(input.getFrequencyLevel());
        if (input.getLocation() != null) e.setLocation(input.getLocation());
        if (input.getSourceImageUrl() != null) e.setSourceImageUrl(input.getSourceImageUrl());
        if (input.getNotes() != null) e.setNotes(input.getNotes());
        if (input.getSeverityAfter() != null) e.setSeverityAfter(input.getSeverityAfter());
        if (input.getFrequencyAfter() != null) e.setFrequencyAfter(input.getFrequencyAfter());
    }
}
