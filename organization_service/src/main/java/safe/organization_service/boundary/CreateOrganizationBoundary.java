package safe.organization_service.boundary;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for creating an organization */
public class CreateOrganizationBoundary {

    @NotBlank
    private String name;
    @NotBlank
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}