package com.ca.signer.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.ca.signer.constants.Constants;
import com.ca.signer.exception.AccessDeniedException;
import com.ca.signer.exception.CertificateNotExistException;
import com.ca.signer.service.UserCertResponse.CertData;
import com.ca.signer.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Service("BccEsignService")
public class BccEsignApiService {
	private static final Logger logger = LoggerFactory.getLogger(BccEsignApiService.class);

	@Value("${esign.base.url}")
	private String BCC_ESIGN_URL;
	@Value("${esign.auth.client:''}")
	private String username;
	@Value("${esign.auth.password:''}")
	private String password;

	public AuthSuccess auth() throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);

			AuthRequest authRequest = new AuthRequest(username, password);
			String ESIGN_AUTH_URL = BCC_ESIGN_URL + Constants.ESIGN_AUTH_URL;
			logger.debug("ESIGN_AUTH_URL : {}", ESIGN_AUTH_URL);

			HttpEntity<AuthRequest> requestEntity = new HttpEntity<>(authRequest, headers);
			ResponseEntity<AuthSuccess> resultEntity = restTemplate.exchange(ESIGN_AUTH_URL, HttpMethod.POST,
					requestEntity, AuthSuccess.class);

			AuthSuccess authSuccess = resultEntity.getBody();
			logger.info("ESign Authorization => {}", resultEntity.getStatusCode());
			if (StringUtils.isBlank(authSuccess.getAccessToken())) {
				throw new AccessDeniedException("Auth Error");
			}
			return authSuccess;

		} catch (HttpStatusCodeException ex) {
			//logger.error("ESign Authorization => HttpStatusCodeException:", ex);
			ObjectMapper objectMapper = new ObjectMapper();
			EsignApiResponse error = objectMapper.readValue(ex.getResponseBodyAsString(), EsignApiResponse.class);

			int statusCode = ex.getStatusCode().value();
			logger.error("HttpStatusCodeException => statusCode : {} Error : {}", statusCode, error);

			if (ex.getStatusCode().is4xxClientError()) {
				throw new AccessDeniedException(error.getMessage());
			}
			throw new Exception(error.getMessage());
		} catch (Exception e) {
			logger.error("ESign  Authorization =>  Exception: {}", e);
			throw e;
		}
	}

	public CertData getUserCertificate(String token, CertificateRequest userCertRequset) throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(token);

			String ESIGN_CERTIFICATE_URL = BCC_ESIGN_URL + Constants.ESIGN_CERTIFICATE_URL;
			logger.debug("ESIGN_CERTIFICATE_URL {}", ESIGN_CERTIFICATE_URL);

			HttpEntity<CertificateRequest> requestEntity = new HttpEntity<>(userCertRequset, headers);
			ResponseEntity<UserCertResponse> resultEntity = restTemplate.exchange(ESIGN_CERTIFICATE_URL,
					HttpMethod.POST, requestEntity, UserCertResponse.class);
			UserCertResponse userCertificate = resultEntity.getBody();

			logger.debug("ESign User Certificate => {} ", resultEntity.getStatusCode());
			return userCertificate.getCertdata();

		} catch (HttpStatusCodeException ex) {
			//logger.error("ESign User Certificate => Error: {}", ex);
			int statusCode = ex.getStatusCode().value();

			ObjectMapper objectMapper = new ObjectMapper();
			EsignApiResponse error = objectMapper.readValue(ex.getResponseBodyAsString(), EsignApiResponse.class);
			logger.info("statusCode : {} Error : {}", statusCode, error);
			throw new CertificateNotExistException(error.getMessage(), ex.getCause());
		} catch (Exception e) {
			logger.error("ESign User Certificate =>  Error: {}", e);
			throw e;
		}
	}

	public String signDigest(String token, SignDataReq request) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			String ESIGN_SIGN_URL = BCC_ESIGN_URL + Constants.ESIGN_SIGNING_URL;

			headers.setBearerAuth(token);

			HttpEntity<SignDataReq> requestEntity = new HttpEntity<>(request, headers);
			ResponseEntity<EsignApiResponse> resultEntity = restTemplate.exchange(ESIGN_SIGN_URL, HttpMethod.POST,
					requestEntity, EsignApiResponse.class);
			EsignApiResponse sigingResponse = resultEntity.getBody();

			logger.debug("signingServer Hash Signing => {} {}", resultEntity.getStatusCode(), resultEntity.getBody());
			return sigingResponse.getData().toString();

		} catch (HttpStatusCodeException ex) {
			int statusCode = ex.getStatusCode().value();
			ObjectMapper objectMapper = new ObjectMapper();
			EsignApiResponse error = objectMapper.readValue(ex.getResponseBodyAsString(), EsignApiResponse.class);
			logger.info("statusCode : {} Error : {}", statusCode, error);
			logger.error("signingServer Hash Signing => Error: {}", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception e) {
			logger.error("signingServer Hash Signing =>  Error: {}", e);
			throw e;
		}
	}

}
