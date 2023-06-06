package com.ca.signer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// exception class if the entity is not found in the db
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class InternalServerError extends Exception {

	private static final long serialVersionUID = 1L;
	String path;

	public InternalServerError(final String message) {
		super(message);
	}

}