package br.com.aula.services;

import br.com.aula.controllers.PersonController;
import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.exceptions.RequiredObjectIsNullException;
import br.com.aula.exceptions.ResourceNotFoundException;
import br.com.aula.mapper.DozerMapper;
import br.com.aula.mapper.custom.PersonMapper;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class PersonService {

    private PersonRepository repository;
    private PagedResourcesAssembler<PersonVO> assembler;
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public PersonService(PersonRepository repository,
                         PagedResourcesAssembler<PersonVO> assembler){
        this.repository = repository;
        this.assembler = assembler;
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable){
        logger.info("Findind one person!");

        var personPage = repository.findAll(pageable);
        var personVOsPage = personPage.map(p -> {
            var vo = DozerMapper.parseObject(p, PersonVO.class);
            addHateoasLinks(vo);
            return vo;
        });

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(),
                         pageable.getPageSize(),
                        "asc")).withSelfRel();
        return assembler.toModel(personVOsPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable){
        logger.info("Findind one person!");

        var personPage = repository.findPersonByName(firstName, pageable);
        var personVOsPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVOsPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();
        return assembler.toModel(personVOsPage, link);
    }

    public PersonVO findById(Long id){
        logger.info("Findind one people!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    public PersonVO create(PersonVO person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    public PersonVO update(PersonVO person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person!");
        var entity = repository.findById(person.getKey()).orElseThrow(() ->
                            new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        entity.setEnabled(person.getEnabled());
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id){
        logger.info("Disabling one people!");
        repository.disablePerson(id);

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one person!");
        var entity = repository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        repository.delete(entity);
    }

    private void addHateoasLinks(PersonVO vo) {
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel().withType("GET"));
        vo.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        vo.add(linkTo(methodOn(PersonController.class).create(vo)).withRel("create").withType("POST"));
        vo.add(linkTo(methodOn(PersonController.class).update(vo)).withRel("update").withType("PUT"));
        vo.add(linkTo(methodOn(PersonController.class).disablePerson(vo.getKey())).withRel("disable").withType("PATCH"));
        vo.add(linkTo(methodOn(PersonController.class).delete(vo.getKey())).withRel("delete").withType("DELETE"));
    }

}
