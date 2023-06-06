package com.ca.signer.model;

public class SignRequest {
	int leftX;
	int leftY;
	int rightX;
	int rightY;
	int pageNumber;
	String userId;
	String reason;
	String location;
	String certStr;

	public int getLeftX() {
		return leftX;
	}

	public int getLeftY() {
		return leftY;
	}

	public int getRightX() {
		return rightX;
	}

	public int getRightY() {
		return rightY;
	}

	public String getUserId() {
		return userId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setLeftX(int leftX) {
		this.leftX = leftX;
	}

	public void setLeftY(int leftY) {
		this.leftY = leftY;
	}

	public void setRightX(int rightX) {
		this.rightX = rightX;
	}

	public void setRightY(int rightY) {
		this.rightY = rightY;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getReason() {
		return reason;
	}

	public String getLocation() {
		return location;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCertStr() {
		return certStr;
	}

	public void setCertStr(String certStr) {
		this.certStr = certStr;
	}

	@Override
	public String toString() {
		return "SignRequest [leftX=" + leftX + ", leftY=" + leftY + ", rightX=" + rightX + ", rightY=" + rightY
				+ ", pageNumber=" + pageNumber + ", userId=" + userId + ", reason=" + reason + ", location=" + location
				+ ", certStr=" + certStr + "]";
	}

}
