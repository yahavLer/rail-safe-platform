package safe.task_service.ServiceImpl;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import safe.task_service.boundaries.*;
import safe.task_service.entity.TaskEntity;
import safe.task_service.enums.TaskStatus;
import safe.task_service.repositories.TaskRepository;
import safe.task_service.repositories.TaskSpecifications;
import safe.task_service.services.TaskService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements task logic:
 * - Create / Read / Update / Delete
 * - Filter tasks by org/risk/assignee/status
 * - Basic stats by status
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public TaskBoundary create(CreateTaskBoundary input) {
        validateRecurrence(input.isRecurring(), input.getRecurrenceInterval(), input.getRecurrenceUnit());

        TaskEntity e = TaskEntity.builder()
                .orgId(input.getOrgId())
                .riskId(input.getRiskId())
                .assigneeUserId(input.getAssigneeUserId())
                .title(input.getTitle())
                .description(input.getDescription())
                .dueDate(input.getDueDate())
                .recurring(input.isRecurring())
                .recurrenceInterval(input.getRecurrenceInterval())
                .recurrenceUnit(input.getRecurrenceUnit())
                .status(TaskStatus.TODO)
                .build();

        return toBoundary(repo.save(e));
    }

    @Override
    public TaskBoundary getById(UUID taskId) {
        TaskEntity e = repo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        return toBoundary(e);
    }

    @Override
    public List<TaskBoundary> list(UUID orgId, UUID riskId, UUID assigneeUserId, TaskStatus status) {

        Specification<TaskEntity> spec = Specification.where(TaskSpecifications.orgId(orgId));

        if (riskId != null) {
            spec = spec.and(TaskSpecifications.riskId(riskId));
        }
        if (assigneeUserId != null) {
            spec = spec.and(TaskSpecifications.assignee(assigneeUserId));
        }
        if (status != null) {
            spec = spec.and(TaskSpecifications.status(status));
        }

        return repo.findAll(spec).stream().map(this::toBoundary).toList();
    }

    @Override
    public TaskBoundary update(UUID taskId, UpdateTaskBoundary input) {
        TaskEntity e = repo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (input.getAssigneeUserId() != null) e.setAssigneeUserId(input.getAssigneeUserId());
        if (input.getTitle() != null) e.setTitle(input.getTitle());
        if (input.getDescription() != null) e.setDescription(input.getDescription());
        if (input.getDueDate() != null) e.setDueDate(input.getDueDate());
        if (input.getRecurring() != null) e.setRecurring(input.getRecurring());
        if (input.getRecurrenceInterval() != null) e.setRecurrenceInterval(input.getRecurrenceInterval());
        if (input.getRecurrenceUnit() != null) e.setRecurrenceUnit(input.getRecurrenceUnit());
        validateRecurrence(e.isRecurring(), e.getRecurrenceInterval(), e.getRecurrenceUnit());

        return toBoundary(repo.save(e));
    }

    @Override
    public TaskBoundary updateStatus(UUID taskId, UpdateTaskStatusBoundary input) {
        TaskEntity e = repo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        e.setStatus(input.getStatus());
        return toBoundary(repo.save(e));
    }

    @Override
    public TaskBoundary updateAssignee(UUID taskId, UUID assigneeUserId) {
        TaskEntity e = repo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        e.setAssigneeUserId(assigneeUserId);
        return toBoundary(repo.save(e));
    }

    @Override
    public void delete(UUID taskId) {
        if (!repo.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found: " + taskId);
        }
        repo.deleteById(taskId);
    }

    @Override
    public Map<TaskStatus, Long> countByStatus(UUID orgId) {
        return repo.findAll(TaskSpecifications.orgId(orgId))
                .stream()
                .collect(Collectors.groupingBy(TaskEntity::getStatus, Collectors.counting()));
    }

    private void validateRecurrence(boolean recurring, Integer interval, Object unit) {
        if (!recurring) return;
        if (interval == null || interval <= 0) {
            throw new IllegalArgumentException("recurrenceInterval must be positive when recurring is enabled");
        }
        if (unit == null) {
            throw new IllegalArgumentException("recurrenceUnit is required when recurring is enabled");
        }
    }

    // ------------------- mapper -------------------

    private TaskBoundary toBoundary(TaskEntity e) {
        TaskBoundary b = new TaskBoundary();
        b.setId(e.getId());
        b.setOrgId(e.getOrgId());
        b.setRiskId(e.getRiskId());
        b.setAssigneeUserId(e.getAssigneeUserId());
        b.setTitle(e.getTitle());
        b.setDescription(e.getDescription());
        b.setStatus(e.getStatus());
        b.setDueDate(e.getDueDate());
        b.setRecurring(e.isRecurring());
        b.setRecurrenceInterval(e.getRecurrenceInterval());
        b.setRecurrenceUnit(e.getRecurrenceUnit());
        b.setCreatedAt(e.getCreatedAt());
        b.setUpdatedAt(e.getUpdatedAt());
        return b;
    }
}
