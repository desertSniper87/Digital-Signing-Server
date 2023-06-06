package com.ca.signer.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ca.signer.constants.Constants;
import com.ca.signer.esign.DocumentSigningService;
import com.ca.signer.files.FilesStorageService;
import com.ca.signer.model.Document;
import com.ca.signer.model.MergeRequest;
import com.ca.signer.model.PdfDocument;
import com.ca.signer.model.SignRequest;
import com.ca.signer.model.SignedDocument;
import com.ca.signer.pdf.PdfSignatureHandler;
import com.ca.signer.service.AuthRequest;
import com.ca.signer.service.AuthSuccess;
import com.ca.signer.service.CertificateRequest;
import com.ca.signer.service.SignDataReq;
import com.ca.signer.service.BccEsignApiService;
import com.ca.signer.service.UserCertResponse;
import com.ca.signer.service.SignDataReq.DataType;
import com.ca.signer.service.UserCertResponse.CertData;
import com.ca.signer.utils.CertificateUtils;
import com.ca.signer.utils.StringUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import eu.europa.esig.dss.utils.Utils;

@Service
public class WebPdfSignerService {
	private static final Logger logger = LoggerFactory.getLogger(WebPdfSignerService.class);
	@Autowired
	FilesStorageService storageService;

	@Autowired
	BccEsignApiService eSignApiService;

	@Autowired
	DocumentSigningService signingService;

	public Document signAndMergePdfDoc(MultipartFile file, SignRequest signReq) throws Exception {
		Document digestDoc = signingService.uploadAndCalculateDigest(file, signReq);

		String aaa = "ss"; //TODO
		MergeRequest mergeReq = new MergeRequest();
		mergeReq.setFileId(digestDoc.getId());
		mergeReq.setDigest(digestDoc.getDigest());
		mergeReq.setSigField(digestDoc.getSigField());
		mergeReq.setUserId(signReq.getUserId());
		mergeReq.setSignedDigest(aaa);

		SignedDocument signedDoc = signingService.merge(mergeReq);
		return signedDoc;
	}

	// rather than taking input stream, can it take hash ?
	public SignedDocument signDigestWeb(MultipartFile file, SignRequest signReq) throws Exception {
		Document digestDoc = signingService.uploadAndCalculateDigest(file, signReq);

		String token = eSignApiService.auth().getAccessToken();
		CertificateRequest certRequest = new CertificateRequest(signReq.getUserId());
		CertData certData = eSignApiService.getUserCertificate(token, certRequest);

		Certificate[] certs = CertificateUtils.getChainFromsigningServerCert(certData.getCertificate());

		BouncyCastleDigest bcDigest = new BouncyCastleDigest();
		PdfPKCS7 sgn = new PdfPKCS7(null, certs, Constants.hashAlgorithm, null, bcDigest, false);

		byte[] hash = Base64.getDecoder().decode(digestDoc.getDigest());
		byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, null, null, CryptoStandard.CMS);
		String shStr = Base64.getEncoder().encodeToString(sh);

		logger.debug("AuthenticatedAttrHash: \n" + shStr);
		//TODO: populate SignDataReq request

		SignDataReq dataRequest = new SignDataReq();
		dataRequest.setUserId(signReq.getUserId());
		dataRequest.setCredId(certData.getKeyAlias());
		dataRequest.setDataType(DataType.HASH);
		dataRequest.setDocumentName("sss");
		dataRequest.setInputData(shStr);

		String extSignature = eSignApiService.signDigest(token, dataRequest);
		String signedDigest = CertificateUtils.removeNewLine(extSignature);
		logger.info("signedDigest - " + signedDigest);

		//Get Bytes
		byte[] extBytes = Base64.getDecoder().decode(signedDigest);

		String escapedSignature = StringUtils.escapeCharFromSignature(extSignature);
		byte[] bytes = Utils.fromBase64(escapedSignature);

		sgn.setExternalDigest(bytes, null, Constants.encryptionAlg);
		byte[] siganture = sgn.getEncodedPKCS7(hash, null, null, null, CryptoStandard.CMS);

		MergeRequest mergeReq = new MergeRequest();
		mergeReq.setFileId(digestDoc.getId());
		mergeReq.setDigest(digestDoc.getDigest());
		mergeReq.setSigField(digestDoc.getSigField());
		mergeReq.setUserId(signReq.getUserId());
		mergeReq.setSignedDigest(Base64.getEncoder().encodeToString(siganture));

		SignedDocument signedDoc = signingService.merge(mergeReq);
		return signedDoc;
	}

}
