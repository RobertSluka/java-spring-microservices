import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

public class RegisterPatientTest {

    private static String token;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";

        // login to get a valid token (assuming /auth/login endpoint exists)
        String loginPayload = """
        {
           "email": "testuser@test.com",
            "password": "password123"
        }
        """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .post("/auth/login");

        token = response
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }

    @Test
    @Sql(scripts = "classpath:sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCreatePatientSuccessfully() {
        String patientPayload = """
        {
            "name": "John D",
            "email": "john.dop@example.com",
            "address": "123 Main St",
            "dateOfBirth": "1990-01-01",
            "registeredDate": "2024-01-01"
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(patientPayload)
                .post("/api/patients");

        response
                .then()
                .contentType(ContentType.JSON)
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("John D"));

        String patientId = response.jsonPath().getString("id");

        // Fetch created patient
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("email", "john.dop@example.com")
                .get("/api/patients/email")
//                .get("/api/patients/email/" + URLEncoder.encode("john.dodeq1@example.com", StandardCharsets.UTF_8))
                .then()
                .statusCode(200)
                .body("email", equalTo("john.dop@example.com"));
    }
}
