package br.com.aula.file.importer.impl;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.file.importer.contract.FileImporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvImporter implements FileImporter {

    @Override
    public List<PersonVO> importFile(InputStream inputStream) throws Exception {
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .get();
        Iterable<CSVRecord> records = format.parse(new InputStreamReader(inputStream));
        return parseRecordsToPersonVOs(records);
    }

    private List<PersonVO> parseRecordsToPersonVOs(Iterable<CSVRecord> records) {
        List<PersonVO> people = new ArrayList<>();
        for (CSVRecord record : records){
            PersonVO person = new PersonVO();
            person.setFirstName(record.get("first_name"));
            person.setLastName(record.get("last_name"));
            person.setAddress(record.get("address"));
            person.setGender(record.get("gender"));
            person.setEnabled(true);
            people.add(person);
        }
        return people;
    }

}
