package br.com.aula.services;

import br.com.aula.controllers.PersonController;
import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.exceptions.RequiredObjectIsNullException;
import br.com.aula.exceptions.ResourceNotFoundException;
import br.com.aula.mapper.DozerMapper;
import br.com.aula.mapper.custom.PersonMapper;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonService {

    @Autowired
    PersonRepository repository;
    @Autowired
    PersonMapper mapper;
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<PersonVO> findAll(){
        logger.info("Findind one person!");
        var persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
        persons.stream().forEach(p -> p.add(
                linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()
        ));
        return persons;
    }
    public PersonVO findById(Long id){
        logger.info("Findind one people!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person!");
        var entity = repository.findById(person.getKey()).orElseThrow(() ->
                            new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        entity.setFirstname(person.getFirstname());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one person!");
        var entity = repository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        repository.delete(entity);
    }

}
