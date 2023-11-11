package br.com.aula.services;

import br.com.aula.controllers.BookController;
import br.com.aula.data.vo.v1.BookVO;
import br.com.aula.exceptions.RequiredObjectIsNullException;
import br.com.aula.exceptions.ResourceNotFoundException;
import br.com.aula.mapper.DozerMapper;
import br.com.aula.mapper.custom.BookMapper;
import br.com.aula.models.Book;
import br.com.aula.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    @Autowired
    BookRepository repository;
    @Autowired
    BookMapper mapper;
    private Logger logger = Logger.getLogger(BookService.class.getName());

    public List<BookVO> findAll(){
        logger.info("Findind one book!");
        var book = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
        book.stream().forEach(p -> p.add(
                linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()
        ));
        return book;
    }
    public BookVO findById(Long id){
        logger.info("Findind one book!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        var vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }

    public BookVO create(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one book!");
        Book entity = DozerMapper.parseObject(book, Book.class);
        var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public BookVO update(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one book!");
        var entity = repository.findById(book.getKey()).orElseThrow(() ->
                new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one book!");
        var entity = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        repository.delete(entity);
    }

}
