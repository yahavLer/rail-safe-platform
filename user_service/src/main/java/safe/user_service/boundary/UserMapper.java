package safe.user_service.boundary;


import safe.user_service.entities.UserEntity;

/**
 * Simple mapper between database entity and API response DTO.
 * Keeps mapping logic in one place (cleaner services/controllers).
 */
public final class UserMapper {

    private UserMapper() {
        // Utility class - prevent instantiation
    }

    public static UserBoundary toBoundary(UserEntity e) {
        UserBoundary b = new UserBoundary();
        b.setId(e.getId());
        b.setExternalAuthId(e.getExternalAuthId());
        b.setOrgId(e.getOrgId());
        b.setDivisionId(e.getDivisionId());
        b.setDepartmentId(e.getDepartmentId());
        b.setRole(e.getRole());
        b.setFirstName(e.getFirstName());
        b.setLastName(e.getLastName());
        b.setEmail(e.getEmail());
        b.setActive(e.isActive());
        b.setCreatedAt(e.getCreatedAt());
        b.setUpdatedAt(e.getUpdatedAt());
        return b;
    }
}
