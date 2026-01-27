package safe.user_service.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import safe.user_service.boundary.*;
import safe.user_service.entities.UserEntity;
import safe.user_service.enums.UserRole;
import safe.user_service.repositories.UserRepository;
import safe.user_service.servicesInterfaces.UserService;
import safe.user_service.exception.BadRequestException;
import safe.user_service.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
/**
 * Default implementation of UserService.
 * Contains business rules (role scope validation) + persistence operations.
 */
public class UserServiceImpl implements UserService {
    /** Repository dependency used to access the database */
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public UserBoundary create(CreateUserBoundary input) {
        // Validate organizational scope matches role requirements
        validateRoleScope(input.getRole(), input.getDivisionId(), input.getDepartmentId());

        // Prevent duplicate external identity (if provided)
        if (input.getExternalAuthId() != null && repo.findByExternalAuthId(input.getExternalAuthId()).isPresent()) {
            throw new BadRequestException("externalAuthId already exists");
        }

        // Build entity from request DTO
        UserEntity e = new UserEntity();
        e.setExternalAuthId(input.getExternalAuthId());
        e.setOrgId(input.getOrgId());
        e.setDivisionId(input.getDivisionId());
        e.setDepartmentId(input.getDepartmentId());
        e.setRole(input.getRole());
        e.setFirstName(input.getFirstName());
        e.setLastName(input.getLastName());
        e.setEmail(input.getEmail());
        e.setPasswordHash(encoder.encode(input.getPassword()));


        // Save + map to response DTO
        return UserMapper.toBoundary(repo.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary getById(UUID id) {
        UserEntity e = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toBoundary(e);
    }

    @Override
    @Transactional(readOnly = true)
    public UserBoundary getByExternalAuthId(String externalAuthId) {
        UserEntity e = repo.findByExternalAuthId(externalAuthId)
                .orElseThrow(() -> new UserNotFoundException("externalAuthId=" + externalAuthId));
        return UserMapper.toBoundary(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBoundary> list(UUID orgId, UUID divisionId, UUID departmentId) {
        // This method supports filtering by organization units.
        if (orgId == null) {
            // אפשר גם לזרוק BadRequest, אבל לפעמים נוח למנהלת מערכת לראות הכל
            return repo.findAll().stream().map(UserMapper::toBoundary).toList();
        }

        if (divisionId == null) {
            return repo.findByOrgId(orgId).stream().map(UserMapper::toBoundary).toList();
        }

        if (departmentId == null) {
            return repo.findByOrgIdAndDivisionId(orgId, divisionId).stream().map(UserMapper::toBoundary).toList();
        }

        return repo.findByOrgIdAndDivisionIdAndDepartmentId(orgId, divisionId, departmentId)
                .stream().map(UserMapper::toBoundary).toList();
    }

    @Override
    public UserBoundary update(UUID id, UpdateUserBoundary input) {
        UserEntity e = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        // Apply only provided fields (PATCH semantics)
        if (input.getFirstName() != null) e.setFirstName(input.getFirstName());
        if (input.getLastName() != null) e.setLastName(input.getLastName());
        if (input.getEmail() != null) e.setEmail(input.getEmail());
        if (input.getActive() != null) e.setActive(input.getActive());

        return UserMapper.toBoundary(repo.save(e));
    }

    @Override
    public UserBoundary updateRole(UUID id, UpdateRoleBoundary input) {
        UserEntity e = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        // Validate role is compatible with existing org scope
        validateRoleScope(input.getRole(), e.getDivisionId(), e.getDepartmentId());
        e.setRole(input.getRole());

        return UserMapper.toBoundary(repo.save(e));
    }

    @Override
    public UserBoundary assignOrgUnit(UUID id, AssignOrgUnitBoundary input) {
        UserEntity e = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        // Move user to different org/division/department
        e.setOrgId(input.getOrgId());
        e.setDivisionId(input.getDivisionId());
        e.setDepartmentId(input.getDepartmentId());

        // Ensure role still makes sense after moving
        validateRoleScope(e.getRole(), e.getDivisionId(), e.getDepartmentId());

        return UserMapper.toBoundary(repo.save(e));
    }

    @Override
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repo.deleteById(id);
    }

    /**
     * Validates that the user role matches the organizational scope provided.
     * This prevents inconsistent states (e.g., department manager without department).
     */
    private void validateRoleScope(UserRole role, UUID divisionId, UUID departmentId) {
        switch (role) {
            case CHIEF_RISK_MANAGER -> {
                // No division/department required
            }
            case DIVISION_RISK_MANAGER -> {
                if (divisionId == null) {
                    throw new BadRequestException("DIVISION_RISK_MANAGER חייב divisionId");
                }
                // departmentId לא חובה. אם תרצי לאסור:
                // if (departmentId != null) throw new BadRequestException("DIVISION manager cannot have departmentId");
            }
            case DEPARTMENT_RISK_MANAGER -> {
                if (divisionId == null || departmentId == null) {
                    throw new BadRequestException("DEPARTMENT_RISK_MANAGER חייב divisionId וגם departmentId");
                }
            }
            case EMPLOYEE -> {
                // Usually only org is required; division/department are optional
            }
        }
    }
}
