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
@JsonPropertyOrder({ "id", "username", "email", "roles", "accessToken", "tokenType" })
@Generated("jsonschema2pojo")
public class AuthSuccess {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("username")
	private String username;
	@JsonProperty("email")
	private String email;
	@JsonProperty("roles")
	private List<String> roles;
	@JsonProperty("accessToken")
	private String accessToken;
	@JsonProperty("tokenType")
	private String tokenType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Integer id) {
		this.id = id;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("roles")
	public List<String> getRoles() {
		return roles;
	}

	@JsonProperty("roles")
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	@JsonProperty("accessToken")
	public String getAccessToken() {
		return accessToken;
	}

	@JsonProperty("accessToken")
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@JsonProperty("tokenType")
	public String getTokenType() {
		return tokenType;
	}

	@JsonProperty("tokenType")
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
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
		return "AuthSuccess [id=" + id + ", username=" + username + ", email=" + email + ", roles=" + roles
				+ ", accessToken=" + accessToken + ", tokenType=" + tokenType + ", additionalProperties="
				+ additionalProperties + "]";
	}

}