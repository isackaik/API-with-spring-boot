package br.com.aula.integrationtestes.controller.withXml;

import br.com.aula.config.TestConfigs;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.integrationtestes.vo.AccountCredentialsVO;
import br.com.aula.integrationtestes.vo.TokenVO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;

    @Test
    @Order(1)
    void testeSignin() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "4562513");

        tokenVO = given()
                .basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML).body(user)
                .when().post().then()
                .statusCode(200).extract()
                .body().as(TokenVO.class);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    void testeRefresh() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "admin123");

        var newTokenVO = given()
                .basePath("/auth/refresh").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML).pathParam("username", tokenVO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .when().put("{username}").then()
                .statusCode(200).extract()
                .body().as(TokenVO.class);

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }

}
