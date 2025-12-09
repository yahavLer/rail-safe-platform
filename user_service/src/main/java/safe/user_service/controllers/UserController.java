package safe.user_service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import safe.user_service.dto.UserCreateRequest;
import safe.user_service.dto.UserResponse;
import safe.user_service.dto.UserUpdateRequest;
import safe.user_service.servicesInterfaces.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserResponse> getUsersByOrganization(
            @RequestParam(name = "organizationId", required = false) Long organizationId
    ) {
        if (organizationId == null) {
            // בעתיד אפשר להחזיר את כל המשתמשים, כרגע נשאיר פשוט:
            throw new IllegalArgumentException("organizationId is required for this endpoint");
        }
        return userService.getUsersByOrganization(organizationId);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
