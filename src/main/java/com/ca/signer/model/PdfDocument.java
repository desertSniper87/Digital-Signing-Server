package com.ca.signer.model;

public class PdfDocument extends Document {
	private String toBeSignedName;
	private String toBeSignedPath;
	private String signedEncoded;
	private SignRequest signReq;

	public PdfDocument() {
		super();
	}

	public PdfDocument(String id, String name) {
		super(id, name);
	}

	public String getToBeSignedPath() {
		return toBeSignedPath;
	}

	public void setToBeSignedPath(String toBeSignedPath) {
		this.toBeSignedPath = toBeSignedPath;
	}

	public String getSignedEncoded() {
		return signedEncoded;
	}

	public void setSignedEncoded(String signedEncoded) {
		this.signedEncoded = signedEncoded;
	}

	public String getToBeSignedName() {
		return toBeSignedName;
	}

	public void setToBeSignedName(String toBeSignedName) {
		this.toBeSignedName = toBeSignedName;
	}

	public SignRequest getSignReq() {
		return signReq;
	}

	public void setSignReq(SignRequest signReq) {
		this.signReq = signReq;
	}

}
