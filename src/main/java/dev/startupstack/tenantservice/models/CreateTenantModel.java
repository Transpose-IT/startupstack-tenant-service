package dev.startupstack.tenantservice.models;

import javax.validation.constraints.*;

/**
 * CreateTenantModel represents the data needed to create a tenant
 */
public class CreateTenantModel {

    @NotBlank(message="Name may not be empty")
    private String name;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}