package br.com.aula.file.importer.contract;

import br.com.aula.data.vo.v1.PersonVO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonVO> importFile(InputStream inputStream) throws Exception;

}
