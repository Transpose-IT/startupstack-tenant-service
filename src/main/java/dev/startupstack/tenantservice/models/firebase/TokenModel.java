package dev.startupstack.tenantservice.models.firebase;

/**
 * TokenModel
 */
public class TokenModel {

    private String grant_type = "refresh_token";
    private String refresh_token;


    public String getGrant_type() {
        return this.grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

}