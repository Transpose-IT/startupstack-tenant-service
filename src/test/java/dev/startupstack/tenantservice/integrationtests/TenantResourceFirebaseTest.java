package dev.startupstack.tenantservice.integrationtests;

import static dev.startupstack.tenantservice.utils.Constants.API_URL_PREFIX;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import dev.startupstack.tenantservice.models.CreateTenantModel;
import dev.startupstack.tenantservice.services.TokenService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@Tag("integration")
@TestInstance(Lifecycle.PER_CLASS)

/**
 * TenantResourceFirebaseTest
 */
public class TenantResourceFirebaseTest {
    final String baseUrl = API_URL_PREFIX + "/" + "tenant";

    private static final Logger LOG = Logger.getLogger(TenantResourceFirebaseTest.class);

    @Inject
    @ConfigProperty(name = "startupstack.tenantservice.firebase.keyfile")
    String serviceAccountFile;

    @Inject
    TokenService tokenService;

    @Test
    void testThatTenantCanBeRetrieved() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());

        CreateTenantModel tenant = new CreateTenantModel();
        tenant.setName("get-test");
        String id = given().body(tenant).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then().extract().response().getBody().jsonPath().get("id");

        given()
            .basePath(baseUrl)
            .pathParam("id", id)
        .when().get("/{id}")
        .then()
            .statusCode(Status.OK.getStatusCode());        
    }

    @Test
    void onGetTestThatInvalidTenantReturns403Forbidden() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        given()
            .basePath(baseUrl)
            .pathParam("id", "invalid-id")
        .when().get("/{id}")
        .then()
            .statusCode(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void testThatTenantCanBeCreated() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());

        CreateTenantModel tenant = new CreateTenantModel();
        tenant.setName("testtenant");
        given().body(tenant).contentType(ContentType.JSON)
        .when().post(baseUrl)
        .then()
            .statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    void testThatTenantCanBeDeleted() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());

        CreateTenantModel tenant = new CreateTenantModel();
        tenant.setName("deleteme");
        String id = given().body(tenant).contentType(ContentType.JSON)
            .when().post(baseUrl)
            .then().extract().response().getBody().jsonPath().get("id");

        given()
            .basePath(baseUrl)
            .pathParam("id", id)
        .when().delete("/{id}")
        .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());

    }

    @Test
    void onDeleteTestThatInvalidTenantReturns403Forbidden() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        given()
            .basePath(baseUrl)
            .pathParam("id", "invalid-id")
        .when().delete("/{id}")
        .then()
            .statusCode(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void testThatAllTenantCanBeRetrieved() {
        LOG.infof("Running test: %s", new Object() {}.getClass().getEnclosingMethod().getName());
        given()
            .when().get(baseUrl)
        .then()
            .statusCode(200).and()
            .body("name", hasItem("default-tenant-name"));
    }
}