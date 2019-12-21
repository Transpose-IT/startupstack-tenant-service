package dev.startupstack.tenantservice.models.firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * FirebaseRestLoginResponse
 */
public class LoginResponse {

    private String localId;
    private String email;
    private String idToken;
    private String refreshToken;
    private String expiresIn;
    private String kind;
    private String displayName;
    private String registered;

    private Map<String, Object> error = new HashMap<>();


    public Map<String,Object> getError() {
        return this.error;
    }

    public void setError(Map<String,Object> error) {
        this.error = error;
    }

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRegistered() {
        return this.registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
}