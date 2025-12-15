package safe.task_service.boundaries;

import lombok.Getter;
import lombok.Setter;
import safe.task_service.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for task.
 */
@Getter @Setter
public class TaskBoundary {

    private UUID id;
    private UUID organizationId;
    private UUID riskId;

    private UUID assigneeUserId;

    private String title;
    private String description;

    private TaskStatus status;

    private Instant dueDate;

    private Instant createdAt;
    private Instant updatedAt;
}
