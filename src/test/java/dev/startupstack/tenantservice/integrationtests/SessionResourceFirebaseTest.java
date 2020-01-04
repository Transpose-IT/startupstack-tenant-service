package dev.startupstack.tenantservice.integrationtests;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static io.restassured.RestAssured.*;

import static dev.startupstack.tenantservice.shared.TestUtils.*;
import static dev.startupstack.tenantservice.shared.TestConstants.*;

import java.io.FileInputStream;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.LoginModel;
import dev.startupstack.tenantservice.models.firebase.LoginResponse;
import dev.startupstack.tenantservice.services.TokenService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)

/**
 * SessionResourceFirebaseTest
 */
public class SessionResourceFirebaseTest {

    final String baseUrl = API_URL_PREFIX + "/" + "session";

    private static final Logger LOG = Logger.getLogger(TokenResourceFirebaseTest.class);

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    @Inject
    TokenService tokenService;

    @BeforeAll
    void initialize() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
        }
        createTempUser(testLoginEmail);
    }

    @Test
    void onLoginTestThatUserGetsErrorOnInvalidCredentials() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());

        LoginModel loginModel = new LoginModel();
        loginModel.setEmail(testLoginEmail);
        loginModel.setPassword("invalid-password");

        given()
            .body(loginModel).contentType(ContentType.JSON)
        .when().post(loginUrl)
        .then().statusCode(Status.BAD_REQUEST.getStatusCode());
   }

   @Test
    void testThatUserCanLogout() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(testLoginEmail);

        given()
            .formParam("access_token", loginResponse.getIdToken())
            .formParam("id", loginResponse.getLocalId())
            .basePath(baseUrl)
        .when().post("/logout")
        .then().statusCode(Status.NO_CONTENT.getStatusCode());    
    }

    @Test
    void onLogoutTestIfInvalidTokenFailsLogoutAction() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(testLoginEmail);

        given()
            .formParam("access_token", "invalid-token")
            .formParam("id", loginResponse.getLocalId())
            .basePath(baseUrl)
        .when().post("/logout")
        .then().statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void onLogoutTestIfInvalidUserIDReturns403Forbidden() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        LoginResponse loginResponse = LoginTempUser(testLoginEmail);

        given()
            .formParam("access_token", loginResponse.getIdToken())
            .formParam("id", "invalid-user-id")
            .basePath(baseUrl)
        .when().post("/logout")
        .then().statusCode(Status.FORBIDDEN.getStatusCode());
    }

    


   @AfterAll
   void teardown() throws Exception {
       deleteUser(testLoginEmail);
   }  
    
}