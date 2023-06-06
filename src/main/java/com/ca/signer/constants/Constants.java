package com.ca.signer.constants;

public class Constants {

	public static String PREFIX_TO_SIGN = "unsigned_";
	public static String PREFIX_COPIED = "unsigned_";
	public static String PREFIX_SIGNED = "signed_";
	public static String PREFIX_ORG_NAME = "_org_";

	public static String ESIGN_AUTH_URL = "/api/auth/signin";
	public static String ESIGN_CERTIFICATE_URL = "/api/v1/user-certificate";
	public static String ESIGN_SIGNING_URL = "/api/v1/sign";

	public static String hashAlgorithm = "SHA256"; // "SHA-256"
	public static String encryptionAlg = "RSA";
}
