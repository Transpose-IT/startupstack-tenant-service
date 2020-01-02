package dev.startupstack.tenantservice.models;

import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_TENANT_ID;
import static dev.startupstack.tenantservice.utils.Constants.CLAIM_NAME_ROLE;

import java.util.HashMap;
import java.util.Map;

/**
 * UserModel represents the data of a user as used by the various services
 */
public class UserModel {

    private String id;
    private String email;
    private String password;
    private String provider;

    @SuppressWarnings("unused")
    private String tenantID;

    @SuppressWarnings("unused")
    private String role;

    private Map<String, Object> customClaims = new HashMap<>();

    public UserModel() {

    }

    public UserModel(String uid, String email, Map<String, Object> claims) {
        this.id = uid;
        this.email = email;
        this.customClaims = claims;
	}

	public UserModel(String uid, Map<String, Object> customClaims, String providerId) {
        this.id = uid;
        this.provider = providerId;
        this.customClaims = customClaims;
	}

	public UserModel(String uid, String email, Map<String, Object> customClaims, String providerId) {
        this.id = uid;
        this.email = email;
        this.provider = providerId;
        this.customClaims = customClaims;
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