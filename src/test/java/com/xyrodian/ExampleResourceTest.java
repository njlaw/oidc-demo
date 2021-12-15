package com.xyrodian;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.quarkus.test.security.oidc.UserInfo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ExampleResourceTest {

    @Test
    public void indexPageRequiresAuthentication() {
        given()
            .when()
                .redirects().follow(false)
                .get("/")
            .then()
                .statusCode(302)
                .header("Location", containsString("https://accounts.google.com/"));
    }

    @Test
    @TestSecurity(user = "oidcUser")
    @OidcSecurity(claims = {@Claim(key = "name", value = "Test User")})
    public void indexPageGreetsUserWhenAuthenticated() {
        given()
            .when()
                .get("/")
            .then()
                .statusCode(200)
                .body(containsString("Hello, Test User!"));
    }
}
