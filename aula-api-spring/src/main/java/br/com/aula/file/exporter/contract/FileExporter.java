package br.com.aula.file.exporter.contract;

import br.com.aula.data.vo.v1.PersonVO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileExporter {

    Resource exportFile(List<PersonVO> people) throws Exception;

}
