package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static utils.FileUtils.readStringFromFile;

public class RegressInTests {

    @BeforeAll
    static void setup(){
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
}
