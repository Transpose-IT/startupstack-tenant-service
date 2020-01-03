package dev.startupstack.tenantservice.integrationtests;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import static dev.startupstack.tenantservice.shared.TestUtils.*;
import static dev.startupstack.tenantservice.shared.TestConstants.*;

import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)
public class TokenResourceFirebaseTest {

    final String baseUrl = API_URL_PREFIX + "/" + "token";

    private static final Logger LOG = Logger.getLogger(TokenResourceFirebaseTest.class);

    private LoginResponse loginResponse;

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    @BeforeAll
    void initialize() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Test
    public void testThatTokenCanBeValidated() throws Exception {
        loginResponse = createTempUserAndLogin(testLoginEmail);
        try {
            given()
                .formParam("access_token", loginResponse.getIdToken())
                .formParam("id", loginResponse.getLocalId())
                .basePath(baseUrl)
            .when().post("/validate")
            .then().statusCode(Status.NO_CONTENT.getStatusCode());    
        } finally {
            deleteUser(testLoginEmail);
        }
    }
}