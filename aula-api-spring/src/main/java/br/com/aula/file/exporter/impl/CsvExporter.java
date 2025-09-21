package br.com.aula.file.exporter.impl;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.file.exporter.contract.FileExporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements FileExporter {

    @Override
    public Resource exportFile(List<PersonVO> people) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setHeader("ID", "First Name", "Last Name", "Address", "Gender", "Enabled")
                .setSkipHeaderRecord(false)
                .get();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (PersonVO person : people) {
                csvPrinter.printRecord(
                        person.getKey(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getGender(),
                        person.getEnabled()
                );
            }
            csvPrinter.flush();
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}
