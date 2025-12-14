package safe.user_service.servicesInterfaces;

import safe.user_service.boundary.*;

import java.util.List;

import java.util.UUID;

/**
 * Business-facing interface for user operations.
 * Controllers depend on this interface (clean architecture).
 */
public interface UserService {
    UserBoundary create(CreateUserBoundary input);

    UserBoundary getById(UUID id);

    UserBoundary getByExternalAuthId(String externalAuthId);

    List<UserBoundary> list(UUID orgId, UUID divisionId, UUID departmentId);

    UserBoundary update(UUID id, UpdateUserBoundary input);

    UserBoundary updateRole(UUID id, UpdateRoleBoundary input);

    UserBoundary assignOrgUnit(UUID id, AssignOrgUnitBoundary input);

    void delete(UUID id);
}