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
    private String provider;

    @SuppressWarnings("unused")
    private String organizationID;

    @SuppressWarnings("unused")
    private String role;

    public static final String CLAIM_NAME_ORGANIZATION_ID = "organization_id";
    public static final String CLAIM_NAME_ROLE = "role";

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
        return this.customClaims.get(CLAIM_NAME_ORGANIZATION_ID).toString();
    }

    public void setOrganizationID(String orgid) {
        this.organizationID = orgid;
        this.customClaims.put(CLAIM_NAME_ORGANIZATION_ID, orgid);
    }

    public String getRole() {
        return this.customClaims.get(CLAIM_NAME_ROLE).toString();
    }

    public void setRole(String role) {
        this.role = role;
        this.customClaims.put(CLAIM_NAME_ROLE, role);
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