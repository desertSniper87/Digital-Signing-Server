package com.ca.signer.service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SignDataReq {
	private static final int MAX_INPUT_SIZE = 10000;

	@NotBlank(message = "User Id must be provided")
	private String userId;

	@NotNull(message = "Data Type must be provided")
	private DataType dataType;

	@NotBlank(message = "XML Data/Digest(Hash) must be provided")
	@Size(max = MAX_INPUT_SIZE, message = "Max input data size: " + MAX_INPUT_SIZE
			+ " characters. Please try with data/hash with shorter length.")
	private String inputData;

	@NotBlank(message = "Credential ID must be provided")
	private String credId;

	@NotBlank(message = "Document name is missing")
	private String documentName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getCredId() {
		return credId;
	}

	public void setCredId(String credId) {
		this.credId = credId;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getInputData() {
		return inputData;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	@Override
	public String toString() {
		return "SigningDataReq [userId=" + userId + ", dataType=" + dataType + ", inputData=" + inputData + ", credId="
				+ credId + ", documentName=" + documentName + "]";
	}

	public String toRequestStr() {
		return "SigningDataReq [userId=" + userId + ", dataType=" + dataType + ", credId=" + credId + ", documentName="
				+ documentName + "]";
	}

	public static enum DataType {
		XML, HASH
	}
}
