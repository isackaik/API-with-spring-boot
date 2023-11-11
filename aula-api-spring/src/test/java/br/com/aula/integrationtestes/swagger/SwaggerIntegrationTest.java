package br.com.aula.integrationtestes.swagger;

import static io.restassured.RestAssured.given;

import br.com.aula.config.TestConfigs;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldDisplaySwaggerUiPage(){
        var content = given().basePath("/swagger-ui/index.html").port(TestConfigs.SERVER_PORT)
                .get().then().statusCode(200)
                .extract().body().asString();
        assertTrue(content.contains("Swagger UI"));
    }

}
