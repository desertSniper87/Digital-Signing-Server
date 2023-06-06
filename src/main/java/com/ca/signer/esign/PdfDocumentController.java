package com.ca.signer.esign;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.ca.signer.constants.Constants;
import com.ca.signer.exception.AccessDeniedException;
import com.ca.signer.files.FilesStorageService;
import com.ca.signer.model.ApiResponse;
import com.ca.signer.model.Document;
import com.ca.signer.model.FileDetails;
import com.ca.signer.model.MergeRequest;
import com.ca.signer.model.PdfDocument;
import com.ca.signer.model.SignRequest;
import com.ca.signer.model.SignedDocument;
import com.ca.signer.pdf.PdfSignatureHandler;
import com.ca.signer.utils.CertificateUtils;
import com.ca.signer.utils.StringUtils;
import com.itextpdf.io.IOException;

@RestController
@RequestMapping({ "/document" })
public class PdfDocumentController {
	private static final Logger logger = LoggerFactory.getLogger(PdfDocumentController.class);

	@Autowired
	FilesStorageService storageService;

	@Autowired
	DocumentSigningService signingService;

	@PostMapping("/upload/hash")
	public ResponseEntity<ApiResponse> uploadAndCreateHash(@RequestParam("document") MultipartFile file,
			@ModelAttribute SignRequest signRequest) {
		ApiResponse response = new ApiResponse();
		try {
			if (StringUtils.isBlank(signRequest.getCertStr()) && StringUtils.isBlank(signRequest.getUserId())) {
				throw new Exception("UserId or CertStr must not be empty");
			}
			Document doc = signingService.uploadAndCalculateDigest(file, signRequest);
			response.setData(doc);
			response.setMessage("Document is uploaded and digested successfully");
			response.setStatus(HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (IOException e) {
			logger.error("IOException", e);
			response.setMessage("Could not upload the file: " + file.getOriginalFilename() + "!");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} catch (AccessDeniedException e) {
			logger.error("AccessDeniedException", e);
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} catch (Exception e) {
			logger.error("Exception", e);
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(HttpStatus.BAD_REQUEST);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@PostMapping("/merge")
	public ResponseEntity<ApiResponse> mergeSignedHash(@ModelAttribute MergeRequest mergeRequest) {
		ApiResponse response = new ApiResponse();
		try {
			if (StringUtils.isBlank(mergeRequest.getCertStr()) && StringUtils.isBlank(mergeRequest.getUserId())) {
				throw new Exception("UserId or CertStr must not be empty");
			}
			logger.debug("MergeRequest {}", mergeRequest.toString());
			Document doc = signingService.merge(mergeRequest);
			response.setData(doc);
			response.setStatus(HttpStatus.OK);
			response.setMessage("Signed hash is merged with orginal document successfully");

			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			logger.error("Failed to merged signed hash", e);
			response.setMessage(e.getLocalizedMessage());
			response.setStatus(HttpStatus.BAD_REQUEST);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

}