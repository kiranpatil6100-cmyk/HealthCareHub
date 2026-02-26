package nimblix.in.HealthCareHub.dto;

import lombok.Getter;
import lombok.Setter;
import nimblix.in.HealthCareHub.model.Role;

@Getter
@Setter
public class AuthRegisterRequest {
    private String email;
    private String password;
    private Role role;
}
