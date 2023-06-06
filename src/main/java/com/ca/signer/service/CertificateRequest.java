package com.ca.signer.service;

public class CertificateRequest {
	private String userId;

	public CertificateRequest(String userId) {
		super();
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static class Builder {
		private String userId;

		public Builder userId(String userId) {
			this.userId = userId;
			return this;
		}

		public CertificateRequest build() {
			return new CertificateRequest(userId);
		}
	}

}
