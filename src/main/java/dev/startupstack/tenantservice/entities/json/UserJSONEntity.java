package dev.startupstack.tenantservice.entities.json;

import java.util.HashMap;
import java.util.Map;

/**
 * ListUsers
 */
public class UserJSONEntity {

    private String uid;
    private String email;
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

    public Map<String, Object> getCustomClaims() {
        return customClaims;
    }
    public void setCustomClaims(Map<String, Object> claims) {
        this.customClaims = claims;
    }
}