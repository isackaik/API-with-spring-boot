package br.com.aula.file.exporter.factory;

import br.com.aula.exceptions.BadRequestException;
import br.com.aula.file.exporter.contract.FileExporter;
import br.com.aula.file.exporter.impl.CsvExporter;
import br.com.aula.file.exporter.impl.PdfExporter;
import br.com.aula.file.exporter.impl.XlsxExporter;
import br.com.aula.util.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);
    private ApplicationContext context;

    public FileExporterFactory(ApplicationContext context) {
        this.context = context;
    }


    public FileExporter getImporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaType.APPLICATION_XLSX)) {
            return context.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaType.APPLICATION_CSV)) {
            return context.getBean(CsvExporter.class);
        } else if (acceptHeader.equalsIgnoreCase(MediaType.APPLICATION_PDF)) {
            return context.getBean(PdfExporter.class);
        } else {
            throw new BadRequestException("Invalid File Format!");
        }
    }

}
