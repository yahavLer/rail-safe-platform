package safe.user_service.dto;

import safe.user_service.enums.SystemRole;
import safe.user_service.enums.OrgRole;

import java.util.UUID;

public class UserCreateRequest {

    private String email;
    private String password;       // בצד ה-service נהפוך ל-hash
    private String firstName;
    private String lastName;
    private String phone;

    private SystemRole systemRole = SystemRole.BASIC_USER;

    // אופציונלי – יצירת חברות ראשונית בארגון
    private Long organizationId;
    private OrgRole orgRole;

    // getters & setters...

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrgRole getOrgRole() {
        return orgRole;
    }

    public void setOrgRole(OrgRole orgRole) {
        this.orgRole = orgRole;
    }
}