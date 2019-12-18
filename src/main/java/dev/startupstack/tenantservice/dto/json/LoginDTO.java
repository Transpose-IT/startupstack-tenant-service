package dev.startupstack.tenantservice.dto.json;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * LoginDTO
 */
public class LoginDTO {

    @NotBlank(message="email may not be empty")
    @Email
    private String email;

    @NotBlank(message="password may not be empty")
    @Size(min = 8, message="password must be at least 8 characters")
    private String password;

    public LoginDTO() {

    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
