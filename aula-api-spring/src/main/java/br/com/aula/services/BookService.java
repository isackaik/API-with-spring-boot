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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    @Autowired
    BookRepository repository;
    @Autowired
    PagedResourcesAssembler<BookVO> assembler;
    @Autowired
    BookMapper mapper;
    private Logger logger = Logger.getLogger(BookService.class.getName());

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){
        logger.info("Findind one book!");

        var bookPage = repository.findAll(pageable);
        var bookVOsPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
        bookVOsPage.map(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();
        return assembler.toModel(bookVOsPage, link);
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
