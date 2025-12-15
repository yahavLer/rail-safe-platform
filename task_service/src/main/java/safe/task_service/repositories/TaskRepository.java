package safe.task_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import safe.task_service.entity.TaskEntity;

import java.util.UUID;

/**
 * Main tasks repository.
 * Using Specifications enables flexible filtering.
 */
public interface TaskRepository extends JpaRepository<TaskEntity, UUID>, JpaSpecificationExecutor<TaskEntity> {
}
