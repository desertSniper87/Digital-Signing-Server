package com.ca.signer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// exception class if the entity is not found in the db
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class AccessDeniedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AccessDeniedException(final String message) {
		super(message);
	}
}