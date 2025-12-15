package safe.task_service.services;


import safe.task_service.boundaries.*;
import safe.task_service.enums.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaskService {

    TaskBoundary create(CreateTaskBoundary input);

    TaskBoundary getById(UUID taskId);

    List<TaskBoundary> list(
            UUID orgId,
            UUID riskId,
            UUID assigneeUserId,
            TaskStatus status
    );

    TaskBoundary update(UUID taskId, UpdateTaskBoundary input);

    TaskBoundary updateStatus(UUID taskId, UpdateTaskStatusBoundary input);

    TaskBoundary updateAssignee(UUID taskId, UUID assigneeUserId);

    void delete(UUID taskId);

    Map<TaskStatus, Long> countByStatus(UUID orgId);
}
