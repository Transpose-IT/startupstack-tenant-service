package dev.startupstack.tenantservice.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.models.FirebaseLoginResponseModel;
import dev.startupstack.tenantservice.models.LoginModel;

/**
 * SessionServiceFirebaseImpl
 */
@Dependent
public class SessionServiceFirebaseImpl implements SessionService  {

    private static final Logger LOG = Logger.getLogger(SessionServiceFirebaseImpl.class);

    @ConfigProperty(name = "startupstack.tenantservice.firebase.webapikey")
    public String webApiKey;

    @Inject
    FirebaseRestService firebaseRestService;

    @Override
    public Response login(LoginModel login) {

        FirebaseLoginResponseModel restResponse = firebaseRestService.login(login);
        System.out.println(restResponse);

        return Response.ok().entity(restResponse).build();

    }
    
}