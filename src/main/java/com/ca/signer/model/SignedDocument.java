package com.ca.signer.model;

public class SignedDocument extends Document {

	private String url;

	public SignedDocument() {
		super();
	}

	public SignedDocument(Document doc) {
		this.name = doc.getName();
		this.id = doc.getId();
		this.path = doc.getPath();
		this.digest = doc.getDigest();
		this.sigField = doc.getSigField();
	}

	public SignedDocument(String id, String name) {
		super(id, name);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
