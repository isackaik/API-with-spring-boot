package br.com.aula.integrationtestes.controller.withJson;

import br.com.aula.config.TestConfigs;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.integrationtestes.vo.AccountCredentialsVO;
import br.com.aula.integrationtestes.vo.BookVO;
import br.com.aula.integrationtestes.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();
    }

    @Test
    @Order(0)
    void authorization() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "admin123");

        var accessToken = given()
                .basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_JSON).body(user)
                .when().post().then()
                .statusCode(200).extract()
                .body().as(TokenVO.class).getAccessToken();

        specification = new RequestSpecBuilder().addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION,
                        "Bearer " + accessToken)
                .setBasePath("api/book").setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws IOException {
        mockbook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString();
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
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

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
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
        mockbook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract().body().asString();
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
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
           .contentType(TestConfigs.CONTENT_TYPE_JSON)
           .pathParam("id", book.getId())
           .when()
           .delete("{id}")
           .then()
           .statusCode(204);
    }

    @Test
    @Order(5)
    void testFindAll() throws IOException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .asString();

        List<BookVO> books = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});
        BookVO foundBookOne = books.get(0);
        book = foundBookOne;

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getTitle());
        assertNotNull(foundBookOne.getPrice());
        assertNotNull(foundBookOne.getLaunchDate());

        assertEquals(1,foundBookOne.getId());

        assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
        assertEquals("Working effectively with legacy code", foundBookOne.getTitle());
        assertEquals(49.00D, foundBookOne.getPrice());

        BookVO foundBookTwo = books.get(1);
        book = foundBookTwo;

        assertNotNull(foundBookTwo.getId());
        assertNotNull(foundBookTwo.getAuthor());
        assertNotNull(foundBookTwo.getTitle());
        assertNotNull(foundBookTwo.getPrice());
        assertNotNull(foundBookTwo.getLaunchDate());

        assertEquals(2,foundBookTwo.getId());

        assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", foundBookTwo.getAuthor());
        assertEquals("Design Patterns", foundBookTwo.getTitle());
        assertEquals(45.00D, foundBookTwo.getPrice());
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    private void mockbook() {
        book.setAuthor("Cassandra Clare");
        book.setTitle("Cidade das Almas Perdidas");
        book.setPrice(100D);
        book.setLaunchDate(new Date());
    }

}
