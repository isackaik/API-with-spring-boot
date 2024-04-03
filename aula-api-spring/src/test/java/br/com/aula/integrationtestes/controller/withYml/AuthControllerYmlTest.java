package br.com.aula.integrationtestes.controller.withYml;

import br.com.aula.config.TestConfigs;
import br.com.aula.integrationtestes.controller.withYml.mapper.YMLMapper;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.integrationtestes.vo.AccountCredentialsVO;
import br.com.aula.integrationtestes.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYmlTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;
    private static YMLMapper objectMapper;

    @BeforeAll
    public static void setup(){
        objectMapper = new YMLMapper();
        tokenVO = new TokenVO();
    }

    @Test
    @Order(1)
    void testeSignin() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "4562513");

        RequestSpecification specification = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        tokenVO = given().spec(specification)
                .config(RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML).body(user, objectMapper)
                .when().post().then()
                .statusCode(200).extract()
                .body().as(TokenVO.class, objectMapper);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    void testeRefresh() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "admin123");

        var newTokenVO = given().config(RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/refresh").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML).pathParam("username", tokenVO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                .when().put("{username}").then()
                .statusCode(200).extract()
                .body().as(TokenVO.class, objectMapper);

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }

}
