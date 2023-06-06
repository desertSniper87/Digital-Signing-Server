package com.ca.signer.model;

public class Document {
	protected String id;
	protected String name;
	protected String path;
	protected String digest;
	protected String sigField;

	public String getSigField() {
		return sigField;
	}

	public void setSigField(String sigField) {
		this.sigField = sigField;
	}

	public Document() {
		super();
	}

	public Document(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDigest() {
		return digest;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", name=" + name + ", path=" + path + ", digest=" + digest + ", sigField="
				+ sigField + "]";
	}

}
