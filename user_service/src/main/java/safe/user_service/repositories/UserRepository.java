package safe.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.user_service.entities.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Data access layer for users table.
 * Spring Data generates implementations automatically.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /** Find by external identity (e.g., Firebase UID). */
    Optional<UserEntity> findByExternalAuthId(String externalAuthId);

    /** List all users in an organization. */
    List<UserEntity> findByOrgId(UUID orgId);

    /** List all users in a division. */
    List<UserEntity> findByOrgIdAndDivisionId(UUID orgId, UUID divisionId);

    /** List all users in a department. */
    List<UserEntity> findByOrgIdAndDivisionIdAndDepartmentId(UUID orgId, UUID divisionId, UUID departmentId);
    Optional<UserEntity> findByOrgIdAndEmail(UUID orgId, String email);

}