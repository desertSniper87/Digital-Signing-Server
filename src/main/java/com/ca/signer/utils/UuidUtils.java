package com.ca.signer.utils;

import java.util.Random;
import java.util.UUID;

public class UuidUtils {

	public static String generateToken() {
		String hash = UuidUtils.getNewUUID();

		return hash;
	}

	public static String getNewUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static String generateOTPNumber() {
		Random rnd = new Random();
		int n = 100000 + rnd.nextInt(900000);
		return new String(n + "");
	}

	public static String generateOtpString(int length) {
		final String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid.substring(uuid.length() - length);
	}

}
