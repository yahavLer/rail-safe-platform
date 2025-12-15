package safe.task_service.boundaries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Request body for creating a task.
 */
@Getter @Setter
public class CreateTaskBoundary {

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID riskId;

    private UUID assigneeUserId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private Instant dueDate;
}
