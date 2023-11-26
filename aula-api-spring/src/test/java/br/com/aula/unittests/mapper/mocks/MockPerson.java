package br.com.aula.unittests.mapper.mocks;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.models.Person;

import java.util.ArrayList;
import java.util.List;

public class MockPerson {

    public Person mockEntity(){
        return mockEntity(0);
    }

    public PersonVO mockVO(){
        return mockVO(0);
    }

    public List<Person> mockEntityList(){
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            persons.add(mockEntity(i));
        }
        return persons;
    }

    public List<PersonVO> mockVOList(){
        List<PersonVO> persons = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            persons.add(mockVO(i));
        }
        return persons;
    }

    public Person mockEntity(Integer number){
        Person person = new Person();
        person.setAddress("Endereço Teste " + number);
        person.setFirstName("First Name " + number);
        person.setLastName("Last Name " + number);
        person.setGender((number % 2 == 0) ? "Male" : "Female");
        person.setId(number.longValue());
        return person;
    }

    public PersonVO mockVO(Integer number){
        PersonVO person = new PersonVO();
        person.setAddress("Endereço Teste " + number);
        person.setFirstName("First Name " + number);
        person.setLastName("Last Name " + number);
        person.setGender((number % 2 == 0) ? "Male" : "Female");
        person.setKey(number.longValue());
        return person;
    }

}
