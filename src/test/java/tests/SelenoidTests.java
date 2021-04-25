package tests;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SelenoidTests {
    // open https://selenoid.autotests.cloud/status
    // statusCode: 200
    // "errors":[]
    /*
    	"browsers": {
            "android": 0,
            "chrome": 0,
            "firefox": 0,
            "opera": 0,
            "safari": 0
    	},
     */
    @Test
    @DisplayName("https://selenoid.autotests.cloud/status available")
    void successStatusTest (){
        given()
                .when()
                .get("https://selenoid.autotests.cloud/status")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("https://selenoid.autotests.cloud:4444/wd/hub/status check without Given Then")
    void successStatusWithoutGivenWhenTest() {
        get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("https://user1:1234@selenoid.autotests.cloud/wd/hub/ available")
    void successStatusWithAuthTest (){
        given()
                .when()
                .get("https://user1:1234@selenoid.autotests.cloud/wd/hub/")
                .then()
                .statusCode(200);

    }

    @Test
    @DisplayName("https://selenoid.autotests.cloud:4444/wd/hub/status with basic auth as additional parameter")
    void successStatusWithBasicAuthTest() {
        given()
                .auth().basic("user1","1234")  //for basic auth you can use this param
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Extract response after status 200")
    void successStatusResponseTest() {
        Response response = given()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println(response);             // bad log - io.restassured.internal.RestAssuredResponseImpl@ed41518
        System.out.println(response.toString());  // bad log - io.restassured.internal.RestAssuredResponseImpl@ed41518
        System.out.println(response.asString());  // {"value":{"message":"Selenoid 98f495722e60da4b35c14814bae240fe6ec75abc built at 2020-09-02_11:14:20AM","ready":true}}
    }

    @Test
    @DisplayName("Check response and add logs")
    void successStatusResponseWithLogTest() {
        given()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();
    }

    @Test
    @DisplayName("Check that response body contain value.ready = true")
    void successStatusReadyTest() {
        given()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .log().body()
                .statusCode(200)
                .body("value.ready", is(true));
    }

    @Test
    @DisplayName("Check that response body contain value.ready = true with Assert")
    void successStatusReadyWithAssertThatTest() {
        Boolean result = given()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .log().body()
                .statusCode(200)
                .extract()
                .path("value.ready");

        assertThat(result, is(true));
    }

    @Test
    @DisplayName("Check that response body contain value.ready = true with Assert variant2")
    void successStatusReadyWithAssertThat1Test() {
        ExtractableResponse<Response> result = given()
                .auth().basic("user1", "1234")
                .when()
                .get("https://selenoid.autotests.cloud:4444/wd/hub/status")
                .then()
                .log().body()
                .statusCode(200)
                .extract();

        assertThat(result.path("value.ready"), is(true));
    }
}
