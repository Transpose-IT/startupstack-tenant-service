package dev.startupstack.tenantservice.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * LoginDTO
 */
public class LoginModel {

    @NotBlank(message="email may not be empty")
    @Email
    private String email;

    @NotBlank(message="password may not be empty")
    @Size(min = 8, message="password must be at least 8 characters")
    private String password;

    private Boolean returnSecureToken = true;

    public LoginModel() {

    }


    public Boolean isReturnSecureToken() {
        return this.returnSecureToken;
    }

    public Boolean getReturnSecureToken() {
        return this.returnSecureToken;
    }

    public void setReturnSecureToken(Boolean returnSecureToken) {
        this.returnSecureToken = returnSecureToken;
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
