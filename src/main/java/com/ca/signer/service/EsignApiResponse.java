package com.ca.signer.service;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "statusCode", "statusText", "datatime", "status", "data", "message", "path" })
@Generated("jsonschema2pojo")
public class EsignApiResponse {

	@JsonProperty("statusCode")
	private Integer statusCode;
	@JsonProperty("statusText")
	private Object statusText;
	@JsonProperty("datatime")
	private String datatime;
	@JsonProperty("status")
	private Boolean status;
	@JsonProperty("data")
	private Object data;
	@JsonProperty("message")
	private String message;
	@JsonProperty("path")
	private Object path;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("statusCode")
	public Integer getStatusCode() {
		return statusCode;
	}

	@JsonProperty("statusCode")
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	@JsonProperty("statusText")
	public Object getStatusText() {
		return statusText;
	}

	@JsonProperty("statusText")
	public void setStatusText(Object statusText) {
		this.statusText = statusText;
	}

	@JsonProperty("datatime")
	public String getDatatime() {
		return datatime;
	}

	@JsonProperty("datatime")
	public void setDatatime(String datatime) {
		this.datatime = datatime;
	}

	@JsonProperty("status")
	public Boolean getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(Boolean status) {
		this.status = status;
	}

	@JsonProperty("data")
	public Object getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(Object data) {
		this.data = data;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("path")
	public Object getPath() {
		return path;
	}

	@JsonProperty("path")
	public void setPath(Object path) {
		this.path = path;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return "EsignApiResponse [statusCode=" + statusCode + ", statusText=" + statusText + ", datatime=" + datatime
				+ ", status=" + status + ", data=" + data + ", message=" + message + ", path=" + path
				+ ", additionalProperties=" + additionalProperties + "]";
	}

}