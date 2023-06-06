package com.ca.signer.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class CommonUtils {
	static ObjectMapper mapper = new ObjectMapper();

	public static boolean isNull(Object obj) {
		return obj == null ? true : false;
	}

	public static boolean isNotNull(Object obj) {
		return !isNull(obj);
	}

	public static boolean isEmpty(String str) {
		return StringUtils.isBlank(str);
	}

	public static String toString(Object obj) {
		return obj.toString();
	}

	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return obj.toString();
		}
	}

	public static StringBuilder arrayListTOString(ArrayList<?> data) {
		StringBuilder dataString = new StringBuilder();
		int i = 0;
		dataString.append(" ( ");
		for (Object id : data) {
			i++;
			dataString.append(" " + id + " ");

			if (data.size() != i) {
				dataString.append(" , ");
			}

		}
		dataString.append(" ) ");
		return dataString;

	}

	public static String getBaseUrl(HttpServletRequest request) {
		String protocol = request.getScheme();
		String host = request.getServerName();
		String port = request.getServerPort() + "";
		String context = request.getContextPath();
		String basePath = protocol + "://" + host + ":" + port + context;

		return basePath;
	}

	public static String getHostName(String url) throws URISyntaxException {
		try {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			URI uri = new URI(url);
			String hostname = uri.getHost();
			if (hostname != null) {
				return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
			}
		} catch (Exception e) {
			throw e;
		}
		return "";
	}

	public static boolean verifyEmailDomain(String email, String domain) {
		try {
			String emailDomain = email.substring(email.indexOf("@") + 1);
			// InetAddress inetAddress = InetAddress.getByName(domain);
			return emailDomain.equals(getHostName(domain));
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}

	public static String getPickupPin() {
		Random rand = new Random();
		long x = rand.nextLong() % 1000000000;
		if (x < 0)
			x = 0 - x;
		String pin = String.valueOf(x);
		int pinSize = 9;
		for (int i = 0; i < pinSize - pin.length(); i++)
			pin += '0';

		return pin;

	}

	public static String getExtension(String fileName) {
		int lastIndexOf = fileName.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return fileName.substring(lastIndexOf);
	}

	public static String getCurrentDirectoryAsDate() {
		String format = "yyyy" + File.separator + "MM" + File.separator + "dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String subDirPath = sdf.format(System.currentTimeMillis());
		return subDirPath;
	}

}
