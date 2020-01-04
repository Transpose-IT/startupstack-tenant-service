package dev.startupstack.tenantservice.models;

import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_ROLE;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * UpdateUser represents the data needed to update a user
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserModel {

    @NotBlank(message="id may not be empty")
    private String id;

    @NotBlank(message="email may not be empty")
    @Email
    private String email;

    private String password;

    @SuppressWarnings("unused")
    private String role;

    private Map<String, Object> customClaims = new HashMap<>();

    // Needed for Jackson so it doesn't NPE on non-existant keys in case the values
    // don't come from the JSON request body
    public UpdateUserModel() {
        customClaims.put(CLAIM_NAME_ROLE, "");
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
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