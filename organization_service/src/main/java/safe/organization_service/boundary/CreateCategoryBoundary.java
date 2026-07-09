package safe.organization_service.boundary;

import jakarta.validation.constraints.NotBlank;

public class CreateCategoryBoundary {
    @NotBlank private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
