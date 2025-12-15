package safe.task_service.repositories;

import org.springframework.data.jpa.domain.Specification;
import safe.task_service.entity.TaskEntity;
import safe.task_service.enums.TaskStatus;

import java.util.UUID;

/**
 * Dynamic query filters for tasks listing.
 */
public class TaskSpecifications {

    public static Specification<TaskEntity> orgId(UUID orgId) {
        return (root, query, cb) -> cb.equal(root.get("organizationId"), orgId);
    }

    public static Specification<TaskEntity> riskId(UUID riskId) {
        return (root, query, cb) -> cb.equal(root.get("riskId"), riskId);
    }

    public static Specification<TaskEntity> assignee(UUID assigneeUserId) {
        return (root, query, cb) -> cb.equal(root.get("assigneeUserId"), assigneeUserId);
    }

    public static Specification<TaskEntity> status(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}

