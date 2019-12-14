package dev.startupstack.tenantservice;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * TenantService
 */


 
@ApplicationScoped
public class TenantService {

    @ConfigProperty(name = "bla")
    static String serviceAccountFile;

    public static void main(final String args[]) {
        System.out.println(serviceAccountFile);
    }
    
}
