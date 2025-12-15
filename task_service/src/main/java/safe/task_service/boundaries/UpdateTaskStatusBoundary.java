package safe.task_service.boundaries;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import safe.task_service.enums.TaskStatus;

/**
 * Request body for updating task status.
 */
@Getter @Setter
public class UpdateTaskStatusBoundary {

    @NotNull
    private TaskStatus status;
}
