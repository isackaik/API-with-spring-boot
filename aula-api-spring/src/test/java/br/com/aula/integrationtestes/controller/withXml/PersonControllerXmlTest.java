package br.com.aula.integrationtestes.controller.withXml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import br.com.aula.integrationtestes.vo.PersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
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
                .setBasePath("api/person").setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString();
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Lucas", persistedPerson.getFirstName());
        assertEquals("Oliveira", persistedPerson.getLastName());
        assertEquals("Sergipe", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(2)
    void testUpdate() throws JsonMappingException, JsonProcessingException {
        person.setLastName("Oliveira Santos");

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().body().asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Lucas", persistedPerson.getFirstName());
        assertEquals("Oliveira Santos", persistedPerson.getLastName());
        assertEquals("Sergipe", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(3)
    void testFindByID() throws JsonMappingException, JsonProcessingException {
        mockPerson();

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .pathParam("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .extract().body().asString();
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Lucas", persistedPerson.getFirstName());
        assertEquals("Oliveira Santos", persistedPerson.getLastName());
        assertEquals("Sergipe", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    void testDelete() throws JsonMappingException, JsonProcessingException {

        given().spec(specification)
           .contentType(TestConfigs.CONTENT_TYPE_XML)
           .accept(TestConfigs.CONTENT_TYPE_XML)
           .pathParam("id", person.getId())
           .when()
           .delete("{id}")
           .then()
           .statusCode(204);
    }

    private void mockPerson() {
        person.setFirstName("Lucas");
        person.setLastName("Oliveira");
        person.setAddress("Sergipe");
        person.setGender("Male");
    }

    @Test
    @Order(5)
    void testFindAll() throws JsonMappingException, JsonProcessingException {

        var content = given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body()
                .asString();

        List<PersonVO> people = objectMapper.readValue(content, new TypeReference<List<PersonVO>>() {});

        PersonVO foundPersonOne = people.get(0);
        person = foundPersonOne;

        assertNotNull(foundPersonOne.getId());
        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());

        assertEquals(5,foundPersonOne.getId());

        assertEquals("Pessoa v2", foundPersonOne.getFirstName());
        assertEquals("Oliveira Santos", foundPersonOne.getLastName());
        assertEquals("Cidade 1", foundPersonOne.getAddress());
        assertEquals("Female", foundPersonOne.getGender());

        PersonVO foundPersonTwo = people.get(1);
        person = foundPersonTwo;

        assertNotNull(foundPersonTwo.getId());
        assertNotNull(foundPersonTwo.getFirstName());
        assertNotNull(foundPersonTwo.getLastName());
        assertNotNull(foundPersonTwo.getAddress());
        assertNotNull(foundPersonTwo.getGender());

        assertEquals(6,foundPersonTwo.getId());

        assertEquals("Pessoa v1", foundPersonTwo.getFirstName());
        assertEquals("Oliveira Santos", foundPersonTwo.getLastName());
        assertEquals("Cidade 1", foundPersonTwo.getAddress());
        assertEquals("Male", foundPersonTwo.getGender());
    }

    @Test
    @Order(6)
    void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("api/person").setPort(TestConfigs.SERVER_PORT)
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

}
