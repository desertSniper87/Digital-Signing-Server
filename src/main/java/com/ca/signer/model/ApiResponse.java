package com.ca.signer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ApiResponse {
	private HttpStatus status = HttpStatus.OK;
	private List<String> errors = new ArrayList<String>();

	private Object data;
	private String message;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a")
	private Date datetime = new Date();

	public ApiResponse() {
	}

	public ApiResponse(Object data) {
		this.data = data;
	}

	public ApiResponse(String data, String message) {
		this.data = data;
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}