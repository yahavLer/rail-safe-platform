package safe.user_service.servicesInterfaces;

import safe.user_service.dto.UserCreateRequest;
import safe.user_service.dto.UserUpdateRequest;
import safe.user_service.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUser(Long id);

    List<UserResponse> getUsersByOrganization(Long organizationId);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}
