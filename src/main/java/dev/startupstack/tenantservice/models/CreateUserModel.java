package dev.startupstack.tenantservice.models;

import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_TENANT_ID;
import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_ROLE;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.*;

/**
 * ListUsers
 */
public class CreateUserModel {

    @NotBlank(message="email may not be empty")
    @Email
    private String email;

    @NotBlank(message="password may not be empty")
    @Size(min = 8, message="password must be at least 8 characters")
    private String password;

    @NotBlank(message="tenant may not be empty")
    private String tenantID;

    @NotBlank(message="role may not be empty")
    private String role;

    private Map<String, Object> customClaims = new HashMap<>();


    public CreateUserModel() {

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

    public String getTenantID() {
        return this.customClaims.get(CLAIM_NAME_TENANT_ID).toString();
    }

    public void setTenantID(String orgid) {
        this.tenantID = orgid;
        this.customClaims.put(CLAIM_NAME_TENANT_ID, orgid);
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