package safe.user_service.controllers;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import safe.user_service.boundary.*;
import safe.user_service.servicesInterfaces.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
/**
 * REST controller exposing HTTP endpoints for user management.
 * Delegates business logic to UserService.
 */
public class UserController {
    /** Service dependency that holds business logic */
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /** Create a new user */
    @PostMapping
    public UserBoundary create(@Valid @RequestBody CreateUserBoundary input) {
        return service.create(input);
    }

    /** Get a user by internal UUID */
    @GetMapping("/{id}")
    public UserBoundary getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    /** Get a user by external auth identifier (e.g., Firebase UID) */
    @GetMapping("/by-external/{externalAuthId}")
    public UserBoundary getByExternalAuthId(@PathVariable String externalAuthId) {
        return service.getByExternalAuthId(externalAuthId);
    }


    /**
     * List users with optional filtering by org/division/department.
     * Example: /api/users?orgId=...&divisionId=...&departmentId=...
     */
    @GetMapping
    public List<UserBoundary> list(
            @RequestParam(required = false) UUID orgId,
            @RequestParam(required = false) UUID divisionId,
            @RequestParam(required = false) UUID departmentId
    ) {
        return service.list(orgId, divisionId, departmentId);
    }

    /** Update basic user details (PATCH semantics) */
    @PatchMapping("/{id}")
    public UserBoundary update(@PathVariable UUID id, @Valid @RequestBody UpdateUserBoundary input) {
        return service.update(id, input);
    }

    /** Update user role only */
    @PatchMapping("/{id}/role")
    public UserBoundary updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateRoleBoundary input) {
        return service.updateRole(id, input);
    }

    /** Assign/move user across organization units */
    @PatchMapping("/{id}/org-unit")
    public UserBoundary assignOrgUnit(@PathVariable UUID id, @Valid @RequestBody AssignOrgUnitBoundary input) {
        return service.assignOrgUnit(id, input);
    }

    /** Delete a user by UUID */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
