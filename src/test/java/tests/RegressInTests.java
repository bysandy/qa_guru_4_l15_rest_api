package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.FileUtils.readStringFromFile;

public class RegressInTests {

    @BeforeAll
    static void setup() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://reqres.in/";
    }


    @Test
    @DisplayName("Check that GET https://reqres.in/api/users?page=2 has status 200 and support.text ")
    void successUserListTest() {
        given()
                .when()
                .get("api/users?page=2")
                .then()
                .statusCode(200)
                .log().body()
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Check that POST with Auth https://reqres.in/api/login has status 200")
    void successLoginTest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }")
                .when()
                .post("api/login")
                .then()
                .statusCode(200)
                .log().body()
                .body("token", is(notNullValue()));
    }

    @Test
    @DisplayName("Check that POST with Auth https://reqres.in/api/login has status 200 using file with data")
    void successLoginWithDataInFileTest() {
        String data = readStringFromFile("./src/test/resources/login_data.txt");
        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("api/login")
                .then()
                .statusCode(200)
                .log().body()
                .body("token", is(notNullValue()));
    }

    @Test
    @DisplayName("Check that POST with wrongAuth https://reqres.in/api/login has status 400")
    void unSuccessLoginTest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"sydney@fife\" }")
                .when()
                .post("api/login")
                .then()
                .statusCode(400)
                .log().body()
                .body("error", is("Missing password"));
    }

    @Test
    @DisplayName("Success create new user")
    @Feature("Homework")
    void successfulyCreateNewUser() {
        String data = readStringFromFile("./src/test/resources/new_user.txt");
        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("api/users")
                .then()
                .statusCode(201)
                .log().body()
                .body("name", is("morpheus"), "job", is("leader"));
    }

    @Test
    @DisplayName("Success update user's job")
    @Feature("Homework")
    void successUpdateExistedUser() {
        String data = readStringFromFile("./src/test/resources/update_user.txt");
        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("api/users/2")
                .then()
                .statusCode(201)
                .log().body()
                .body("job", is("zion resident"), "name", is("morpheus"));
    }

    @Test
    @DisplayName("Success delete the user")
    @Feature("Homework")
    void successDeleteExistedUser() {
        given()
                .when()
                .delete("api/users/2")
                .then()
                .statusCode(204)
                .log().body();
    }

    @Test
    @DisplayName("Check that user#23 doesn't exist ")
    @Feature("Homework")
    void successUnknownUserTest() {
        given()
                .when()
                .get("api/unknown/23")
                .then()
                .statusCode(404)
                .log().body();
    }

    @Test
    @DisplayName("Check delayed response with status 200 ")
    @Feature("Homework")
    void successDelayedResponseTest() {
        //It was the test for delayed answer using https://github.com/awaitility/awaitility
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> this.getStatus("api/users?delay=3") == 200);
    }

    public int getStatus(String path) {
        return given()
                .get(path)
                .then()
                .extract()
                .statusCode();
    }
}
