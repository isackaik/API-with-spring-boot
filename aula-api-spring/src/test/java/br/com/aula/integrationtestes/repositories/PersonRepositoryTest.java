package br.com.aula.integrationtestes.repositories;

import br.com.aula.integrationtestes.testcontainers.AbstractIntegrationTest;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository personRepository;
    private static Person person;

    @BeforeAll
    public static void setup(){
        person = new Person();
    }

    @Test
    @Order(1)
    void testFindPersonByName() throws IOException {

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "firstName"));
        person = personRepository.findPersonByName("isahella", pageable).getContent().get(0);

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());

        assertEquals(321, person.getId());

        assertEquals("Isahella", person.getFirstName());
        assertEquals("Lumb", person.getLastName());
        assertEquals("01386 Mayfield Street", person.getAddress());
        assertEquals("Female", person.getGender());
        assertTrue(person.getEnabled());

    }

    @Test
    @Order(2)
    void testDisablePerson() throws IOException {
        personRepository.disablePerson(person.getId());
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "firstName"));
        person = personRepository.findPersonByName("isahella", pageable).getContent().get(0);

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());

        assertEquals(321, person.getId());

        assertEquals("Isahella", person.getFirstName());
        assertEquals("Lumb", person.getLastName());
        assertEquals("01386 Mayfield Street", person.getAddress());
        assertEquals("Female", person.getGender());
        assertFalse(person.getEnabled());

    }

}
