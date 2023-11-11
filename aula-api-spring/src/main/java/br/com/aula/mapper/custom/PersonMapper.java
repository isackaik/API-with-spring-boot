package br.com.aula.mapper.custom;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.models.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonMapper {

    public PersonVO convertEntityToVO(Person person){
        PersonVO vo = new PersonVO();
        vo.setKey(person.getId());
        vo.setFirstname(person.getFirstname());
        vo.setLastName(person.getLastName());
        vo.setAddress(person.getAddress());
        vo.setGender(person.getGender());
        return vo;
    }

    public Person convertVOToEntity(PersonVO person){
        Person entity = new Person();
        entity.setId(person.getKey());
        entity.setFirstname(person.getFirstname());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        return entity;
    }

}
