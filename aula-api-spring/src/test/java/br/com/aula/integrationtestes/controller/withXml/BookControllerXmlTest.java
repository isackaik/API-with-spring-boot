package br.com.aula.integrationtestes.controller.withXml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.aula.config.TestConfigs;
import br.com.aula.data.vo.v1.security.TokenVO;
import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.integrationtestes.vo.AccountCredentialsVO;
import br.com.aula.integrationtestes.vo.BookVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();
    }

    @Test
    @Order(0)
    void authorization() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("isac", "admin123");

        var accessToken = given()
                .basePath("/auth/signin").port(TestConfigs.SERVER_PORT)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(user)
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
    void testCreate() throws JsonMappingException, JsonProcessingException {
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
        mockBook();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    private void mockBook() {
        book.setAuthor("Cassandra Clare");
        book.setTitle("Cidade das Almas Perdidas");
        book.setPrice(100D);
        book.setLaunchDate(new Date());
    }

}