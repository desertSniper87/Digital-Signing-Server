package com.ca.signer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// exception class if the entity is not found in the db
@ResponseStatus(code = HttpStatus.NO_CONTENT)
public class CertificateNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public CertificateNotExistException(final String message) {
		super(message);
	}

	public CertificateNotExistException(String message, Throwable cause) {
		super(message, cause);
	}
}