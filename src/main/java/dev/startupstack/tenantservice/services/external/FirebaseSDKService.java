package dev.startupstack.tenantservice.services.external;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dev.startupstack.tenantservice.Constants;

/**
 * FirebaseSDKService
 */
//@Singleton
@Dependent
public class FirebaseSDKService {

    private static final Logger LOG = Logger.getLogger(FirebaseSDKService.class);


    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    //TODO: Figure out why this injection is not working
    static String serviceAccountFile = "/home/jargelo/firebase-sa.key.json";

    public static FirebaseToken verifyToken(String token) throws WebApplicationException {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException exception) {
            throw new WebApplicationException(exception.getMessage());
        }
    }

    public static void initialize() {
        LOG.info(serviceAccountFile);
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
    
                FirebaseApp.initializeApp(options);
            } catch (IOException | NullPointerException exception) {
                throw new WebApplicationException(exception.getMessage(), exception);
            }
        }
    }

    @PreDestroy
    void predestroy() {
        FirebaseApp.getInstance().delete();
    }
    
}