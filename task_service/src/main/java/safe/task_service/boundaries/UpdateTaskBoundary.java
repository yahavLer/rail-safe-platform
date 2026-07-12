package safe.task_service.boundaries;

import lombok.Getter;
import lombok.Setter;

import safe.task_service.enums.RecurrenceUnit;

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
    private Boolean recurring;
    private Integer recurrenceInterval;
    private RecurrenceUnit recurrenceUnit;
}

