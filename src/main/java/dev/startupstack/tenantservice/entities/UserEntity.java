package dev.startupstack.tenantservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * UserEntity has the DB representation of a user. Used primarily to store the
 * refresh token of a logged in user, as well as having some persistent cache
 * for commonly used user data.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @OneToOne
    @JoinColumn
    private TenantEntity tenant;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String role;

    private String refresh_token;

    public UserEntity() {

    }

    public UserEntity(String id, String email, TenantEntity tenant, String role) {
        this.id = id;
        this.email = email;
        this.tenant = tenant;
        this.role = role;
    }

    public UserEntity(String id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TenantEntity getTenant() {
        return this.tenant;
    }

    public void setTenant(TenantEntity tenant) {
        this.tenant = tenant;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getid() {
        return this.id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" + " id='" + getid() + "'" + ", email='" + getEmail() + "'" + ", role='" + getRole() + "'"
                + ", refresh_token='" + getRefresh_token() + "'" + "}";
    }

}