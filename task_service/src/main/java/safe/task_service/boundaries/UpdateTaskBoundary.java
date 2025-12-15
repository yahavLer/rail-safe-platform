package safe.task_service.boundaries;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Partial update for task fields.
 * Any non-null field will be updated.
 */
@Getter @Setter
public class UpdateTaskBoundary {

    private UUID assigneeUserId;
    private String title;
    private String description;
    private Instant dueDate;
}
