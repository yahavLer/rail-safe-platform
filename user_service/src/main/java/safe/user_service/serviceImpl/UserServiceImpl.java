package safe.user_service.serviceImpl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import safe.user_service.dto.OrganizationMembershipDto;
import safe.user_service.dto.UserCreateRequest;
import safe.user_service.dto.UserResponse;
import safe.user_service.dto.UserUpdateRequest;
import safe.user_service.entities.OrganizationMembershipEntity;
import safe.user_service.entities.UserEntity;
import safe.user_service.enums.OrgRole;
import safe.user_service.repositories.OrganizationMembershipRepository;
import safe.user_service.repositories.UserRepository;
import safe.user_service.servicesInterfaces.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository,
                           OrganizationMembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setSystemRole(request.getSystemRole());

        // אם נשלח ארגון – ניצור Membership ראשוני
        if (request.getOrganizationId() != null && request.getOrgRole() != null) {
            OrganizationMembershipEntity membership = new OrganizationMembershipEntity();
            membership.setUser(user);
            membership.setOrganizationId(request.getOrganizationId());
            membership.setOrgRole(request.getOrgRole());
            membership.setPrimaryForUser(true);
            user.getMemberships().add(membership);
        }

        UserEntity saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByOrganization(Long organizationId) {
        List<OrganizationMembershipEntity> memberships =
                membershipRepository.findByOrganizationId(organizationId);

        return memberships.stream()
                .map(OrganizationMembershipEntity::getUser)
                .distinct()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getSystemRole() != null) {
            user.setSystemRole(request.getSystemRole());
        }

        return toDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ======== Mapper ========

    private UserResponse toDto(UserEntity entity) {
        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhone(entity.getPhone());
        dto.setActive(entity.isActive());
        dto.setSystemRole(entity.getSystemRole());

        List<OrganizationMembershipDto> memberships = entity.getMemberships().stream()
                .map(m -> {
                    OrganizationMembershipDto md = new OrganizationMembershipDto();
                    md.setOrganizationId(m.getOrganizationId());
                    md.setOrgRole(m.getOrgRole());
                    md.setPrimaryForUser(m.isPrimaryForUser());
                    return md;
                })
                .collect(Collectors.toList());

        dto.setMemberships(memberships);
        return dto;
    }
}
