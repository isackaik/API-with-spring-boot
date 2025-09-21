package br.com.aula.services;

import br.com.aula.controllers.PersonController;
import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.exceptions.*;
import br.com.aula.file.exporter.contract.FileExporter;
import br.com.aula.file.exporter.factory.FileExporterFactory;
import br.com.aula.file.importer.contract.FileImporter;
import br.com.aula.file.importer.factory.FileImporterFactory;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;

import static br.com.aula.mapper.DozerMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.com.aula.util.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PersonService {

    private PersonRepository repository;
    private PagedResourcesAssembler<PersonVO> assembler;
    private FileImporterFactory importer;
    private FileExporterFactory exporter;
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public PersonService(PersonRepository repository,
                         PagedResourcesAssembler<PersonVO> assembler,
                         FileImporterFactory importer,
                         FileExporterFactory exporter){
        this.repository = repository;
        this.assembler = assembler;
        this.importer = importer;
        this.exporter = exporter;
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable){
        logger.info("Findind one person!");

        var personPage = repository.findAll(pageable);
        return buildPagedModel(pageable, personPage);
    }

    public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable){
        logger.info("Findind one person!");

        var personPage = repository.findPersonByName(firstName, pageable);
        return buildPagedModel(pageable, personPage);
    }

    public PersonVO findById(Long id){
        logger.info("Findind one people!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        var vo = parseObject(entity, PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting a Peaple Page!");

        var people = repository.findAll(pageable)
                .map(person -> parseObject(person, PersonVO.class))
                .getContent();
        try {
            FileExporter fileExporter = exporter.getImporter(acceptHeader);
            return fileExporter.exportFile(people);
        } catch (Exception e) {
            logger.severe("Error exporting file: " + e.getMessage());
            throw new FileExporterException("An error occurred while exporting the file.", e);
        }
    }

    public PersonVO create(PersonVO person){
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");
        var entity = parseObject(person, Person.class);
        var vo = parseObject(repository.save(entity), PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    public List<PersonVO> massCreation(MultipartFile file) {
        logger.info("Importing people from file!");

        if(file.isEmpty()){
            throw new BadRequestException("Please, set a valid file!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String filename = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));
            FileImporter fileImporter = importer.getImporter(filename);

            List<Person> entities = fileImporter.importFile(inputStream).stream()
                    .map(dto ->
                            repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, PersonVO.class);
                        addHateoasLinks(dto);
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            logger.severe("Error importing file: " + e.getMessage());
            throw new FileStorageException("An error occurred while processing the file.", e);
        }
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
        var vo = parseObject(repository.save(entity), PersonVO.class);
        addHateoasLinks(vo);
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id){
        logger.info("Disabling one people!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        repository.disablePerson(id);
        var vo = parseObject(entity, PersonVO.class);
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
        vo.add(linkTo(methodOn(PersonController.class).findPersonByName("", 1, 12, "asc")).withRel("findPersonByName").withType("GET"));
        vo.add(linkTo(methodOn(PersonController.class).create(vo)).withRel("create").withType("POST"));
        vo.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        vo.add(linkTo(methodOn(PersonController.class).update(vo)).withRel("update").withType("PUT"));
        vo.add(linkTo(methodOn(PersonController.class).disablePerson(vo.getKey())).withRel("disable").withType("PATCH"));
        vo.add(linkTo(methodOn(PersonController.class).delete(vo.getKey())).withRel("delete").withType("DELETE"));
        vo.add(linkTo(methodOn(PersonController.class)
                .exportPage(1, 12, "asc", null))
                .withRel("exportPage").withType("GET")
                .withTitle("Export people."));
    }

    private PagedModel<EntityModel<PersonVO>> buildPagedModel(Pageable pageable, Page<Person> personPage) {
        var personVOsPage = personPage.map(p -> {
            var vo = parseObject(p, PersonVO.class);
            addHateoasLinks(vo);
            return vo;
        });

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();
        return assembler.toModel(personVOsPage, link);
    }

}
