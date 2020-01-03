package dev.startupstack.tenantservice.integrationtests;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import static dev.startupstack.tenantservice.shared.TestConstants.testTenantID;
import static dev.startupstack.tenantservice.shared.TestConstants.testEmail;
import static dev.startupstack.tenantservice.shared.TestConstants.testPassword;
import static dev.startupstack.tenantservice.shared.TestConstants.testDefaultRole;

import static dev.startupstack.tenantservice.shared.TestUtils.*;


import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord.CreateRequest;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.CreateUserModel;
import dev.startupstack.tenantservice.models.UpdateUserModel;
import dev.startupstack.tenantservice.services.UserEntityService;
import dev.startupstack.tenantservice.services.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * UserResourceTests
 */
@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)
public class UserResourceFirebaseTest {

    final String baseUrl = API_URL_PREFIX + "/" + "user";

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    @Inject
    UserService userService;

    @Inject
    UserEntityService userEntityService;

    @BeforeAll
    void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Test
    public void testThatUserCanBeCreated() throws Exception {
        CreateUserModel testUser = new CreateUserModel(testTenantID, testDefaultRole, testEmail, testPassword);
        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.CREATED.getStatusCode()).and()
                .body("responseObject.email", equalTo(testEmail)).and()
                .body("responseObject.customClaims.tenant_id", equalTo(testTenantID)).and()
                .body("responseObject.customClaims.role", equalTo(testDefaultRole));
    }
    @Test
    public void testUserCreateModelValidation() throws Exception {
        CreateUserModel testUser = new CreateUserModel();

        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        testUser.setEmail("validationtest@nonexistent.com");
        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());

        testUser.setTenantID(testTenantID);
        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
        
        testUser.setPassword(testPassword);
        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
    
        testUser.setRole(testDefaultRole);
        given().body(testUser).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then()
                .statusCode(Status.CREATED.getStatusCode());

        deleteUser("validationtest@nonexistent.com");
    }

    @Test
    public void testThatAllUsersCanBeRetrieved() throws Exception {
        createTempUser("test1@fake.net");
        createTempUser("test2@fake.net");

        try {
            given()
                .when().get(baseUrl)
                .then()
                    .statusCode(200)
                    .body("responseObject.email", hasItems("test1@fake.net", "test2@fake.net"));
        } finally {
            deleteUser("test1@fake.net");
            deleteUser("test2@fake.net");    
        }
    }

    @Test
    public void OnCreateTestThatUserIsNotCreatedWhenEmailAlreadyExists() throws Exception {
        String email = "duplicate@fake.net";
        createTempUser(email);

        CreateUserModel testUser = new CreateUserModel(testTenantID, testDefaultRole, email, testPassword);

        try {
            given().body(testUser).contentType(ContentType.JSON)
                .when().post(baseUrl)
                .then()
                    .statusCode(Status.FORBIDDEN.getStatusCode());
        } finally {
            deleteUser(email);
        }
    }

    @Test
    public void onCreateTestThatFirebaseThrowsExceptionOnInvalidInput() throws Exception {
        String email = "invalid-email";
        CreateUserModel testUser = new CreateUserModel(testTenantID, testDefaultRole, email, "x");

        Assertions.assertThrows(WebApplicationException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void testThatFirebaseThrowsExceptionOnDuplicateInput() throws Exception {
        String email = "duplicate-user-service@fake.net";
        CreateUserModel testUser = new CreateUserModel(testTenantID, testDefaultRole, email, testPassword);

        try {
            CreateRequest request = new CreateRequest();
            request.setEmail(email);
            FirebaseAuth.getInstance().createUser(request);
            Assertions.assertThrows(WebApplicationException.class, () -> userService.createUser(testUser));
        } finally {
            deleteUser(email);
        }
    }

    @Test
    public void onCreateTestThatUserCreationFailsWhenTenantIsInvalid() throws Exception {
        String email = "notenant@fake.net";
        CreateUserModel testUser = new CreateUserModel("invalidtenant", testDefaultRole, email, testPassword);
        try {
            given().body(testUser).contentType(ContentType.JSON)
                .when().post(baseUrl)
                .then()
                    .statusCode(Status.FORBIDDEN.getStatusCode());
        } finally {
            deleteUser(email);
        }
    }
    
    @Test
    public void testThatUsercanBeDeleted() throws Exception {
        String email = "delete@fake.net";
        createTempUser(email);

        String id = getUserIDByEmail(email);

        try {
            given()
                .basePath(baseUrl)
                .pathParam("id", id)
            .when().delete("/{id}")
            .then()
                .statusCode(Status.NO_CONTENT.getStatusCode());
        } finally {
            deleteUser(email);
        }
    }

    @Test
    public void onDeleteTestThatAnInvalidDeleteCallThrowsException() {
        Assertions.assertThrows(WebApplicationException.class, () -> userService.deleteUser("invalid-id"));
    }

    @Test
    public void testThatUsercanBeUpdated() throws Exception {
        String email = "preupdate@fake.net";
        createTempUser(email);

        String id = getUserIDByEmail(email);

        UpdateUserModel updateUser = new UpdateUserModel();
        updateUser.setid(id);
        updateUser.setEmail("update@fake.com");
        updateUser.setPassword("fakepassword123");
        updateUser.setRole("User");

        try {
            given()
                .body(updateUser).contentType(ContentType.JSON)
                .basePath(baseUrl)
                .pathParam("id", id)
            .when().put("/{id}")
            .then()
                .statusCode(Status.ACCEPTED.getStatusCode());

            given()
                .basePath(baseUrl)
                .pathParam("id", id)
            .when().get("/{id}")
            .then()
                .statusCode(Status.OK.getStatusCode()).and()
                .body("responseObject.email", equalTo("update@fake.com")).and()
                .body("responseObject.id", equalTo(id)).and()
                .body("responseObject.customClaims.role", equalTo("User"));
            
        } finally {
            deleteUser(email);
            deleteUser("update@fake.com");
        }

    }

    @Test
    public void onUpdateTestThatAnEmptyUpdateModelReturnsABadRequest() throws Exception {
        UpdateUserModel updateUser = new UpdateUserModel();
        
        try {
            given()
                .body(updateUser).contentType(ContentType.JSON)
                .basePath(baseUrl)
                .pathParam("id", "invalid-id")
            .when().put("/{id}")
            .then()
                .statusCode(Status.BAD_REQUEST.getStatusCode());
        } finally {
            deleteUser("bla");
        }
    }

    @Test
    public void onUpdateTestThatAnInvalidIDReturnsAnException() throws Exception {
        UpdateUserModel updateUser = new UpdateUserModel();
        updateUser.setid("invalid-id");
        Assertions.assertThrows(WebApplicationException.class, () -> userService.updateUser(updateUser));
    }

    
    @Test
    public void onGetTestThatAnUnknownUserReturnsAnError() {
        String id = "fake-id";
        
        given()
            .basePath(baseUrl)
            .pathParam("id", id)
        .when().get("/{id}")
        .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());
            
    }

    @AfterAll
    void teardown() throws Exception {
        deleteUser(testEmail);
    }
}