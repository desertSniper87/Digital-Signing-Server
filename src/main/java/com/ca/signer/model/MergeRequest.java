package com.ca.signer.model;

public class MergeRequest {
	String fileId;
	String digest;
	String signedDigest;
	String sigField;
	String userId;
	String certStr;

	public String getFileId() {
		return fileId;
	}

	public String getDigest() {
		return digest;
	}

	public String getSignedDigest() {
		return signedDigest;
	}

	public String getSigField() {
		return sigField;
	}

	public String getUserId() {
		return userId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public void setSignedDigest(String signedDigest) {
		this.signedDigest = signedDigest;
	}

	public void setSigField(String sigField) {
		this.sigField = sigField;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCertStr() {
		return certStr;
	}

	public void setCertStr(String certStr) {
		this.certStr = certStr;
	}

	@Override
	public String toString() {
		return "MergeRequest [fileId=" + fileId + ", digest=" + digest + ", signedDigest=" + signedDigest
				+ ", sigField=" + sigField + ", userId=" + userId + "]";
	}
}
