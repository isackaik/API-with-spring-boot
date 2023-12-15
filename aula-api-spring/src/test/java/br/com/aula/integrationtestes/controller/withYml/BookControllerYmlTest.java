package br.com.aula.integrationtestes.controller.withYml;

import br.com.aula.config.TestConfigs;
import br.com.aula.data.vo.v1.security.TokenVO;
import br.com.aula.integrationtestes.controller.withYml.mapper.YMLMapper;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.integrationtestes.vo.AccountCredentialsVO;
import br.com.aula.integrationtestes.vo.BookVO;
import br.com.aula.integrationtestes.vo.pagedModels.PagedModelBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YMLMapper objectMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new YMLMapper();

        book = new BookVO();
    }

    @Test
    @Order(0)
    void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "admin123");

        var accessToken = given()
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, objectMapper)
                .when().post().then()
                .statusCode(200).extract()
                .body().as(TokenVO.class, objectMapper).getAccessToken();

        specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION,
                        "Bearer " + accessToken)
                .setBasePath("api/book").setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body().as(BookVO.class, objectMapper);
        book = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertTrue(persistedBook.getId() > 0);

        assertEquals("Cassandra Clare", persistedBook.getAuthor());
        assertEquals("Cidade das Almas Perdidas", persistedBook.getTitle());
        assertEquals(100D, persistedBook.getPrice());
    }

    @Test
    @Order(2)
    void testUpdate() throws IOException {
        book.setTitle("Cidade das Cinzas");

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(book, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body()
                .as(BookVO.class, objectMapper);

        book = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Cassandra Clare", persistedBook.getAuthor());
        assertEquals("Cidade das Cinzas", persistedBook.getTitle());
        assertEquals(100D, persistedBook.getPrice());
    }

    @Test
    @Order(3)
    void testFindByID() throws IOException {
        mockBook();

        var persistedBook = given().spec(specification)
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(BookVO.class, objectMapper);

        book = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getLaunchDate());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Cassandra Clare", persistedBook.getAuthor());
        assertEquals("Cidade das Cinzas", persistedBook.getTitle());
        assertEquals(100D, persistedBook.getPrice());
    }

    @Test
    @Order(4)
    void testDelete() throws IOException {

        given().spec(specification)
            .config(RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParam("id", book.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    @Test
    @Order(5)
    void testFindAll() throws IOException {

        var wrapper = given().spec(specification)
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page", 1, "size", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .as(PagedModelBook.class, objectMapper);

        var books = wrapper.getContent();

        BookVO foundBookOne = books.get(0);
        book = foundBookOne;

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getPrice());
        assertNotNull(foundBookOne.getLaunchDate());

        assertEquals(6,foundBookOne.getId());

        assertEquals("Martin Fowler e Kent Beck", foundBookOne.getAuthor());
        assertEquals("Refactoring", foundBookOne.getTitle());
        assertEquals(88.00D, foundBookOne.getPrice());

        BookVO foundBookTwo = books.get(1);
        book = foundBookTwo;

        assertNotNull(foundBookTwo.getId());
        assertNotNull(foundBookTwo.getAuthor());
        assertNotNull(foundBookTwo.getTitle());
        assertNotNull(foundBookTwo.getPrice());
        assertNotNull(foundBookTwo.getLaunchDate());

        assertEquals(7,foundBookTwo.getId());

        assertEquals("Eric Freeman, Elisabeth Freeman, Kathy Sierra, Bert Bates", foundBookTwo.getAuthor());
        assertEquals("Head First Design Patterns", foundBookTwo.getTitle());
        assertEquals(110.00D, foundBookTwo.getPrice());
    }

    @Test
    @Order(6)
    void testFindAllWithoutToken() throws IOException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("api/book").setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var content = given().spec(specificationWithoutToken)
                .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                        .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(7)
    void testHATEOAS() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .queryParams("page", 1, "size", 5, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .asString();

        assertTrue(content.contains("- rel: \"self\"\n    href: \"http://localhost:8888/api/book/7\""));
        assertTrue(content.contains("- rel: \"self\"\n    href: \"http://localhost:8888/api/book/8\""));
        assertTrue(content.contains("- rel: \"self\"\n    href: \"http://localhost:8888/api/book/9\""));

        assertTrue(content.contains("page:\n  size: 5\n  totalElements: 16\n  totalPages: 4\n  number: 1"));
        assertTrue(content.contains("- rel: \"first\"\n"+"  href: \"http://localhost:8888/api/book?direction=asc&page=0&size=5&sort=id,asc\""));
        assertTrue(content.contains("- rel: \"prev\"\n"+"  href: \"http://localhost:8888/api/book?direction=asc&page=0&size=5&sort=id,asc\""));
        assertTrue(content.contains("- rel: \"self\"\n"+"  href: \"http://localhost:8888/api/book?page=1&size=5&direction=asc\""));
        assertTrue(content.contains("- rel: \"next\"\n"+"  href: \"http://localhost:8888/api/book?direction=asc&page=2&size=5&sort=id,asc\""));
        assertTrue(content.contains("- rel: \"last\"\n"+"  href: \"http://localhost:8888/api/book?direction=asc&page=3&size=5&sort=id,asc\""));

    }

    private void mockBook() {
        book.setAuthor("Cassandra Clare");
        book.setTitle("Cidade das Almas Perdidas");
        book.setPrice(100D);
        book.setLaunchDate(new Date());
    }

}