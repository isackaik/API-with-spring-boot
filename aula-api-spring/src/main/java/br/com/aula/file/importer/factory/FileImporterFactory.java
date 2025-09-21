package br.com.aula.file.importer.factory;

import br.com.aula.exceptions.BadRequestException;
import br.com.aula.file.importer.contract.FileImporter;
import br.com.aula.file.importer.impl.CsvImporter;
import br.com.aula.file.importer.impl.XlsxImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);
    private ApplicationContext context;

    public FileImporterFactory(ApplicationContext context) {
        this.context = context;
    }


    public FileImporter getImporter(String fileName) throws Exception {
        if (fileName.endsWith(".xlsx")) {
            return context.getBean(XlsxImporter.class);
        } else if (fileName.endsWith(".csv")) {
            return context.getBean(CsvImporter.class);
        } else {
            throw new BadRequestException("Invalid File Format!");
        }
    }

}
