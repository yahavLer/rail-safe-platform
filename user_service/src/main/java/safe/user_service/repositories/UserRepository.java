package safe.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import safe.user_service.entities.UserEntity;

import java.lang.Long;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
