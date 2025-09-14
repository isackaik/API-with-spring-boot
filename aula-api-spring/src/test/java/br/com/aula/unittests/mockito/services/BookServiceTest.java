package br.com.aula.unittests.mockito.services;

import br.com.aula.data.vo.v1.BookVO;
import br.com.aula.exceptions.RequiredObjectIsNullException;
import br.com.aula.models.Book;
import br.com.aula.repositories.BookRepository;
import br.com.aula.services.BookService;
import br.com.aula.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;
    @Mock
    BookRepository repository;

    @InjectMocks
    private BookService service;

    @BeforeEach
    void setUpMocks() throws Exception{
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/1>;rel=\"self\";type=\"GET\""));
        assertEquals("Author 1", result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals( 1D, result.getPrice());
        assertEquals("Title 1", result.getTitle());
    }

    @Test
    void create() {
        Book entity = input.mockEntity(1);
        Book persisted = entity;
        persisted.setId(1L);
        BookVO vo = input.mockVO(1);

        when(repository.save(any(Book.class))).thenReturn(persisted);
        var result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/1>;rel=\"self\";type=\"GET\""));
        assertEquals("Author 1", result.getAuthor());
        assertEquals( 1D, result.getPrice());
        assertEquals("Title 1", result.getTitle());
    }

    @Test
    void update() {
        Book entity = input.mockEntity(1);
        Book persisted = entity;
        persisted.setId(1L);
        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);
        var result = service.update(vo);

        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/1>;rel=\"self\";type=\"GET\""));
        assertEquals("Author 1", result.getAuthor());
        assertNotNull(result.getLaunchDate());
        assertEquals( 1D, result.getPrice());
        assertEquals("Title 1", result.getTitle());
    }

    @Test
    void delete() {
        Book entity = input.mockEntity(1);
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