package br.com.aula.services;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.exceptions.ResourceNotFoundException;
import br.com.aula.mapper.DozerMapper;
import br.com.aula.mapper.custom.PersonMapper;
import br.com.aula.models.Person;
import br.com.aula.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }
    public PersonVO findById(Long id){
        logger.info("Findind all people!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO create(PersonVO person){
        logger.info("Creating one person!");
        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        return vo;
    }

    public PersonVO update(PersonVO person){
        logger.info("Updating one person!");

        var entity = repository.findById(person.getId()).orElseThrow(() ->
                            new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        entity.setFirstname(person.getFirstname());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one person!");
        var entity = repository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Nenhum registro encontrado para este ID."));
        repository.delete(entity);
    }

}
