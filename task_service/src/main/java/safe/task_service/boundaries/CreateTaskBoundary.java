package safe.task_service.boundaries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import safe.task_service.enums.RecurrenceUnit;

import java.time.Instant;
import java.util.UUID;

/**
 * Request body for creating a task.
 */
@Getter @Setter
public class CreateTaskBoundary {

    @NotNull
    private UUID orgId;

    @NotNull
    private UUID riskId;

    private UUID assigneeUserId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private Instant dueDate;

    private boolean recurring = false;
    private Integer recurrenceInterval;
    private RecurrenceUnit recurrenceUnit;
}

