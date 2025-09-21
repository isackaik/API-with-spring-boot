package br.com.aula.file.importer.impl;

import br.com.aula.data.vo.v1.PersonVO;
import br.com.aula.file.importer.contract.FileImporter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class XlsxImporter implements FileImporter {

    @Override
    public List<PersonVO> importFile(InputStream inputStream) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) {
                rows.next();
            }
            return parseRowsToPersonVOs(rows);
        }
    }

    private List<PersonVO> parseRowsToPersonVOs(Iterator<Row> rows) {
        List<PersonVO> people = new ArrayList<>();
        while(rows.hasNext()){
            Row row = rows.next();
            if(isRowInvalid(row)) {
                continue;
            }
            PersonVO person = new PersonVO();
            person.setFirstName(row.getCell(0).getStringCellValue());
            person.setLastName(row.getCell(1).getStringCellValue());
            person.setAddress(row.getCell(2).getStringCellValue());
            person.setGender(row.getCell(3).getStringCellValue());
            person.setEnabled(true);
            people.add(person);
        }
        return people;
    }

    private static boolean isRowInvalid(Row row) {
        return row.getCell(0) == null
                || row.getCell(0).getCellType() == CellType.BLANK;
    }

}
