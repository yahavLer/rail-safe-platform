package safe.task_service.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import safe.task_service.boundaries.*;
import safe.task_service.enums.TaskStatus;
import safe.task_service.services.TaskService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API for tasks (mitigations).
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public TaskBoundary create(@Valid @RequestBody CreateTaskBoundary input) {
        return service.create(input);
    }

    @GetMapping("/{taskId}")
    public TaskBoundary getById(@PathVariable UUID taskId) {
        return service.getById(taskId);
    }

    /**
     * Example:
     * GET /api/tasks?orgId=...&riskId=...&status=IN_PROGRESS
     */
    @GetMapping
    public List<TaskBoundary> list(
            @RequestParam @NotNull UUID orgId,
            @RequestParam(required = false) UUID riskId,
            @RequestParam(required = false) UUID assigneeUserId,
            @RequestParam(required = false) TaskStatus status
    ) {
        return service.list(orgId, riskId, assigneeUserId, status);
    }

    @PatchMapping("/{taskId}")
    public TaskBoundary update(@PathVariable UUID taskId, @Valid @RequestBody UpdateTaskBoundary input) {
        return service.update(taskId, input);
    }

    @PatchMapping("/{taskId}/status")
    public TaskBoundary updateStatus(@PathVariable UUID taskId, @Valid @RequestBody UpdateTaskStatusBoundary input) {
        return service.updateStatus(taskId, input);
    }

    /**
     * Simple endpoint if you want to change only assignee.
     */
    @PatchMapping("/{taskId}/assignee/{assigneeUserId}")
    public TaskBoundary updateAssignee(@PathVariable UUID taskId, @PathVariable UUID assigneeUserId) {
        return service.updateAssignee(taskId, assigneeUserId);
    }

    @DeleteMapping("/{taskId}")
    public void delete(@PathVariable UUID taskId) {
        service.delete(taskId);
    }

    @GetMapping("/stats/by-status")
    public Map<TaskStatus, Long> countByStatus(@RequestParam UUID orgId) {
        return service.countByStatus(orgId);
    }
}
