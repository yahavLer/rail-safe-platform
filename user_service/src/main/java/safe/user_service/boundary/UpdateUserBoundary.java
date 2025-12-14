package safe.user_service.boundary;

import jakarta.validation.constraints.Email;


/**
 * Request DTO for partial updates of basic user fields.
 * All fields are optional (PATCH semantics).
 */
public class UpdateUserBoundary {

    private String firstName;
    private String lastName;

    @Email
    private String email;

    /** Optional enable/disable flag */
    private Boolean active;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}