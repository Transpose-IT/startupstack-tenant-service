package dev.startupstack.tenantservice.dto.json;

import static dev.startupstack.tenantservice.Constants.CLAIM_NAME_ROLE;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.*;

/**
 * UpdateUserDTO
 */
public class UpdateUserDTO {

    @NotBlank(message="uid may not be empty")
    private String uid;

    @NotBlank(message="email may not be empty")
    @Email
    private String email;

    private String password;

    @NotBlank(message="role may not be empty")
    private String role;

    private Map<String, Object> customClaims = new HashMap<>();

    public UpdateUserDTO() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public String getRole() {
        return this.customClaims.get(CLAIM_NAME_ROLE).toString();
    }

    public void setRole(String role) {
        this.role = role;
        this.customClaims.put(CLAIM_NAME_ROLE, role);
    }

    public Map<String, Object> getCustomClaims() {
        return customClaims;
    }

    public void setCustomClaims(Map<String, Object> claims) {
        this.customClaims = claims;
    }

}