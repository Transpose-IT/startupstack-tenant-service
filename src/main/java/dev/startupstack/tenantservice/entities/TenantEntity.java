package dev.startupstack.tenantservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TenantEntity
 */
@Entity
@Table(name = "tenants")
public class TenantEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    private String name;


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}