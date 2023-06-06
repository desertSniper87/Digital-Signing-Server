
package com.ca.signer.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "statusCode", "statusText", "datetime", "status", "data", "message", "path" })
@Generated("jsonschema2pojo")
public class UserCertResponse {

	@JsonProperty("statusCode")
	private Integer statusCode;
	@JsonProperty("statusText")
	private String statusText;
	@JsonProperty("datetime")
	private String datetime;
	@JsonProperty("status")
	private Boolean status;
	@JsonProperty("data")
	private CertData data;
	@JsonProperty("message")
	private String message;
	@JsonProperty("path")
	private String path;
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
	public String getStatusText() {
		return statusText;
	}

	@JsonProperty("statusText")
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	@JsonProperty("datetime")
	public String getDatetime() {
		return datetime;
	}

	@JsonProperty("datetime")
	public void setDatetime(String datetime) {
		this.datetime = datetime;
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
	public CertData getCertdata() {
		return data;
	}

	@JsonProperty("data")
	public void setCertdata(CertData data) {
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
	public String getPath() {
		return path;
	}

	@JsonProperty("path")
	public void setPath(String path) {
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

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonPropertyOrder({ "user_id", "key_alias", "key_status", "certificate", "certificate_chain" })
	@Generated("jsonschema2pojo")
	public class CertData {

		@JsonProperty("user_id")
		private String userId;
		@JsonProperty("key_alias")
		private String keyAlias;
		@JsonProperty("key_status")
		private String keyStatus;
		@JsonProperty("certificate")
		private String certificate;
		@JsonProperty("certificate_chain")
		private List<String> certificateChain;
		@JsonIgnore
		private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

		@JsonProperty("user_id")
		public String getUserId() {
			return userId;
		}

		@JsonProperty("user_id")
		public void setUserId(String userId) {
			this.userId = userId;
		}

		@JsonProperty("key_alias")
		public String getKeyAlias() {
			return keyAlias;
		}

		@JsonProperty("key_alias")
		public void setKeyAlias(String keyAlias) {
			this.keyAlias = keyAlias;
		}

		@JsonProperty("key_status")
		public String getKeyStatus() {
			return keyStatus;
		}

		@JsonProperty("key_status")
		public void setKeyStatus(String keyStatus) {
			this.keyStatus = keyStatus;
		}

		@JsonProperty("certificate")
		public String getCertificate() {
			return certificate;
		}

		@JsonProperty("certificate")
		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}

		@JsonProperty("certificate_chain")
		public List<String> getCertificateChain() {
			return certificateChain;
		}

		@JsonProperty("certificate_chain")
		public void setCertificateChain(List<String> certificateChain) {
			this.certificateChain = certificateChain;
		}

		@JsonAnyGetter
		public Map<String, Object> getAdditionalProperties() {
			return this.additionalProperties;
		}

		@JsonAnySetter
		public void setAdditionalProperty(String name, Object value) {
			this.additionalProperties.put(name, value);
		}

	}

}