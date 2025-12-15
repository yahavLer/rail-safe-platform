package safe.task_service.entity;

import jakarta.persistence.*;
import lombok.*;
import safe.task_service.enums.TaskStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Task (Mitigation) entity.
 * Represents an action item linked to a specific risk.
 */
@Entity
@Table(name = "tasks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Multi-tenant: to filter and secure tasks by organization.
     */
    @Column(nullable = false)
    private UUID organizationId;

    /**
     * Foreign reference (not DB FK) to risk_service risk id.
     */
    @Column(nullable = false)
    private UUID riskId;

    /**
     * Optional: the user responsible for completing the task.
     */
    private UUID assigneeUserId;

    /**
     * Short title for UI lists.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Detailed instructions / mitigation details.
     */
    @Column(nullable = false, length = 4000)
    private String description;

    /**
     * Task execution status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    /**
     * Optional due date as timestamp (could also be LocalDate).
     */
    private Instant dueDate;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        // Default status for new tasks
        if (this.status == null) {
            this.status = TaskStatus.TODO;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
