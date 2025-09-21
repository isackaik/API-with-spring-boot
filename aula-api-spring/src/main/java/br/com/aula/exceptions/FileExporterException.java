package br.com.aula.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileExporterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileExporterException(String message) {
		super(message);
	}

	public FileExporterException(String message, Throwable cause) {
		super(message, cause);
	}

}
