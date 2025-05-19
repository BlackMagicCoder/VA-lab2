package de.berlin.htw.boundary;

import de.berlin.htw.entity.dto.Project;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectResourceTest {

    private static String projectId;
    private static String userId;

    @BeforeEach
    public void cleanDatabase() {
        // Benutzer löschen
        given().when().get("/api/v2/users")
                .then().statusCode(200)
                .extract().body().jsonPath().getList("id", String.class)
                .forEach(id ->
                        given().when().delete("/api/v2/users/{uid}", id)
                                .then().statusCode(204)
                );
    }

    @Test
    @Order(1)
    public void testCreateAndRetrieveProject() {
        Project p = new Project();
        p.setName("IntegrationTestProj");
        p.setDescription("Beschreibung");

        projectId = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(p)
                .when()
                .post("/api/v2/projects")
                .then()
                .statusCode(201)
                .body("name", equalTo("IntegrationTestProj"))
                .body("description", equalTo("Beschreibung"))
                .extract()
                .path("id");

        // GET by ID
        given()
                .when()
                .get("/api/v2/projects/{id}", projectId)
                .then()
                .statusCode(200)
                .body("id", equalTo(projectId));
    }

    @Test
    @Order(2)
    public void testUpdateProject() {
        Project updated = new Project();
        updated.setName("UpdatedName");
        updated.setDescription("UpdatedDesc");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updated)
                .when()
                .put("/api/v2/projects/{id}", projectId)
                .then()
                .statusCode(200)
                .body("name", equalTo("UpdatedName"))
                .body("description", equalTo("UpdatedDesc"));
    }

    @Test
    @Order(3)
    public void testGetAllProjects() {
        given()
                .when()
                .get("/api/v2/projects")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(4)
    public void testDeleteProject() {
        given()
                .when()
                .delete("/api/v2/projects/{id}", projectId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/v2/projects/{id}", projectId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    public void testAssignAndRemoveUser() {
        userId = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{ \"user_name\":\"TestUser\", \"user_email\":\"test@htw.de\" }")
                .when()
                .post("/api/v2/users")
                .then()
                .statusCode(201)
                .extract().header("Location")
                .replaceAll(".*/", "");

        // Erstelle wieder ein Projekt
        String pid = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{ \"name\":\"P\", \"description\":\"D\" }")
                .when()
                .post("/api/v2/projects")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Assign
        given()
                .when()
                .post("/api/v2/projects/{pid}/users/{uid}", pid, userId)
                .then()
                .statusCode(204);

        // List users
        given()
                .when()
                .get("/api/v2/projects/{pid}/users", pid)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(userId));

        // Remove
        given()
                .when()
                .delete("/api/v2/projects/{pid}/users/{uid}", pid, userId)
                .then()
                .statusCode(204);

        // Nun wieder leer
        given()
                .when()
                .get("/api/v2/projects/{pid}/users", pid)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }
}