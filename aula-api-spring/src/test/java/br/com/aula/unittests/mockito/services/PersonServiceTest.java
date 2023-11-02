package br.com.aula.unittests.mockito.services;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.exceptions.RequiredObjectIsNullException;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;
import br.com.aula.services.PersonService;
import br.com.aula.unittests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    MockPerson input;
    @Mock
    PersonRepository repository;

    @InjectMocks
    private PersonService service;

    @BeforeEach
    void setUpMocks() throws Exception{
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Person> list = input.mockEntityList();

        when(repository.findAll()).thenReturn(list);

        var people = service.findAll();
        assertNotNull(people);
        assertEquals(14, people.size());

        var personOne = people.get(1);

        assertNotNull(personOne);
        assertNotNull(personOne.getKey());
        assertNotNull(personOne.getLinks());
        assertTrue(personOne.toString().contains("links: [</person/1>;rel=\"self\"]"));
        assertEquals("Endereço Teste 1", personOne.getAddress());
        assertEquals("First Name 1", personOne.getFirstname());
        assertEquals("Last Name 1", personOne.getLastName());
        assertEquals("Female", personOne.getGender());

        var personFour = people.get(4);

        assertNotNull(personFour);
        assertNotNull(personFour.getKey());
        assertNotNull(personFour.getLinks());
        assertTrue(personFour.toString().contains("links: [</person/4>;rel=\"self\"]"));
        assertEquals("Endereço Teste 4", personFour.getAddress());
        assertEquals("First Name 4", personFour.getFirstname());
        assertEquals("Last Name 4", personFour.getLastName());
        assertEquals("Male", personFour.getGender());

        var personNine = people.get(9);

        assertNotNull(personNine);
        assertNotNull(personNine.getKey());
        assertNotNull(personNine.getLinks());
        assertTrue(personNine.toString().contains("links: [</person/9>;rel=\"self\"]"));
        assertEquals("Endereço Teste 9", personNine.getAddress());
        assertEquals("First Name 9", personNine.getFirstname());
        assertEquals("Last Name 9", personNine.getLastName());
        assertEquals("Female", personNine.getGender());
    }

    @Test
    void findById() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</person/1>;rel=\"self\"]"));
        assertEquals("Endereço Teste 1", result.getAddress());
        assertEquals("First Name 1", result.getFirstname());
        assertEquals("Last Name 1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void create() {
        Person entity = input.mockEntity(1);
        Person persisted = entity;
        persisted.setId(1L);
        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.save(entity)).thenReturn(persisted);
        var result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</person/1>;rel=\"self\"]"));
        assertEquals("Endereço Teste 1", result.getAddress());
        assertEquals("First Name 1", result.getFirstname());
        assertEquals("Last Name 1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void update() {
        Person entity = input.mockEntity(1);
        Person persisted = entity;
        persisted.setId(1L);
        PersonVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);
        var result = service.update(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</person/1>;rel=\"self\"]"));
        assertEquals("Endereço Teste 1", result.getAddress());
        assertEquals("First Name 1", result.getFirstname());
        assertEquals("Last Name 1", result.getLastName());
        assertEquals("Female", result.getGender());
    }

    @Test
    void delete() {
        Person entity = input.mockEntity(1);
        entity.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);
    }

    @Test
    void testCreateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "Não é permitido persistir um objeto nulo";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });
        String expectedMessage = "Não é permitido persistir um objeto nulo";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}