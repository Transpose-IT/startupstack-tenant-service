package dev.startupstack.tenantservice.models;

/**
 * LoginModel
 */
public class LoginModel {

    private String email;
    private String password;
    private Boolean returnSecureToken = true;

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