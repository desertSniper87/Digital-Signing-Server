package com.ca.signer.web;

import java.security.cert.Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ca.signer.esign.DocumentSigningService;
import com.ca.signer.model.ApiResponse;
import com.ca.signer.model.Document;
import com.ca.signer.model.MergeRequest;
import com.ca.signer.model.SignRequest;
import com.ca.signer.service.CertificateRequest;
import com.ca.signer.utils.CertificateUtils;
import com.ca.signer.utils.StringUtils;

@RestController
@RequestMapping({ "/web" })
public class RestESignWebController {
	private static final Logger logger = LoggerFactory.getLogger(RestESignWebController.class);
	@Autowired
	DocumentSigningService signingService;

	@Autowired
	WebPdfSignerService webPdfService;

	@RequestMapping("/select-cert")
	public ResponseEntity<ApiResponse> selectCert(@ModelAttribute CertificateRequest request) {
		ApiResponse response = new ApiResponse();
		try {
			if (StringUtils.isBlank(request.getUserId())) {
				throw new Exception("UserId  must not be empty");
			}
			logger.debug("request {}", request.toString());
			Certificate[] certList = signingService.getCertificateFromsigningServer(request.getUserId());
			response.setData(CertificateUtils.getPemFormattedCertStr(certList[0]));
			response.setStatus(HttpStatus.OK);
			response.setMessage("ESign Certificate for User - " + request.getUserId());

			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			logger.error("Error in fetching user certificate", e);
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(HttpStatus.BAD_REQUEST);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@RequestMapping("/sign")
	public ResponseEntity<ApiResponse> uploadAndSign(@RequestParam("document") MultipartFile file,
			@ModelAttribute SignRequest signRequest) {
		ApiResponse response = new ApiResponse();
		try {
			if (StringUtils.isBlank(signRequest.getUserId())) {
				throw new Exception("UserId  must not be empty");
			}
			logger.debug("request {}", signRequest.toString());
			Document doc = webPdfService.signDigestWeb(file, signRequest);
			logger.info("Pdf is signed successfully- userId: {}, fileId: {}", signRequest.getUserId(), doc.getId());
			response.setData(doc);
			response.setStatus(HttpStatus.OK);
			response.setMessage("Pdf is signed successfully");

			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			logger.error("Pdf signing is failed", e);
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(HttpStatus.BAD_REQUEST);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
}