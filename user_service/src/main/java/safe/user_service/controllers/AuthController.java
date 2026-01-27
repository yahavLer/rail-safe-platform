package safe.user_service.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import safe.user_service.boundary.LoginRequestBoundary;
import safe.user_service.boundary.UserBoundary;
import safe.user_service.entities.UserEntity;
import safe.user_service.repositories.UserRepository;

import static safe.user_service.boundary.UserMapper.toBoundary;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public UserBoundary login(@RequestBody @Valid LoginRequestBoundary req) {
        UserEntity user = userRepo.findByOrgIdAndEmail(req.getOrganizationId(), req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        return toBoundary(user);
    }
}
