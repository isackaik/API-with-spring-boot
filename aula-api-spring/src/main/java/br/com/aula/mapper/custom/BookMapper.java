package br.com.aula.mapper.custom;

import br.com.aula.data.vo.v1.BookVO;
import br.com.aula.models.Book;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public BookVO convertEntityToVO(Book book){
        BookVO vo = new BookVO();
        vo.setKey(book.getId());
        vo.setAuthor(book.getAuthor());
        vo.setLaunchDate(book.getLaunchDate());
        vo.setPrice(book.getPrice());
        vo.setTitle(book.getTitle());
        return vo;
    }

    public Book convertVOToEntity(BookVO book){
        Book entity = new Book();
        entity.setId(book.getKey());
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        return entity;
    }

}
