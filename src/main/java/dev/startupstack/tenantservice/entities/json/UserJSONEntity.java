package dev.startupstack.tenantservice.entities.json;

import java.util.HashMap;
import java.util.Map;

/**
 * ListUsers
 */
public class UserJSONEntity {

    private String uid;
    private String email;
    private String password;
    private String organizationID;
    private String role;
    private String provider;

    private Map<String, Object> customClaims = new HashMap<>();

    public UserJSONEntity() {

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

    public String getOrganizationID() {
        return this.organizationID;
    }

    public void setOrganizationID(String orgid) {
        this.organizationID = orgid;
        this.customClaims.put("organization_id", orgid);
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
        this.customClaims.put("role", role);
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Object> getCustomClaims() {
        return customClaims;
    }
    public void setCustomClaims(Map<String, Object> claims) {
        this.customClaims = claims;
    }
}