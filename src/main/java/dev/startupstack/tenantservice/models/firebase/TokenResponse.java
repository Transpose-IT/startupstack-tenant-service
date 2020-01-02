package dev.startupstack.tenantservice.models.firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * TokenResponse represents a response from the Firebase REST API when exchanging tokens.
 */
public class TokenResponse {
    private String refresh_token;
    private String id_token;
    private String user_id;
    private String project_id;
    private String token_type;
    private String expires_in;
    private String access_token;

    private Map<String, Object> error = new HashMap<>();

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Map<String, Object> getError() {
        return this.error;
    }

    public void setError(Map<String, Object> error) {
        this.error = error;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getId_token() {
        return this.id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProject_id() {
        return this.project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getToken_type() {
        return this.token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

}