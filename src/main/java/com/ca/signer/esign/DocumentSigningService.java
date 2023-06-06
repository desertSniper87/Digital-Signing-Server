package com.ca.signer.esign;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ca.signer.constants.Constants;
import com.ca.signer.files.FilesStorageService;
import com.ca.signer.model.Document;
import com.ca.signer.model.MergeRequest;
import com.ca.signer.model.PdfDocument;
import com.ca.signer.model.SignRequest;
import com.ca.signer.model.SignedDocument;
import com.ca.signer.pdf.PdfSignatureHandler;
import com.ca.signer.service.AuthSuccess;
import com.ca.signer.service.BccEsignApiService;
import com.ca.signer.service.CertificateRequest;
import com.ca.signer.service.UserCertResponse.CertData;
import com.ca.signer.utils.CertificateUtils;
import com.ca.signer.utils.StringUtils;

@Service
public class DocumentSigningService {
	private static final Logger logger = LoggerFactory.getLogger(DocumentSigningService.class);
	@Autowired
	FilesStorageService storageService;
	@Autowired
	BccEsignApiService eSignApiService;

	public Document uploadAndCalculateDigest(MultipartFile file, SignRequest signReq) throws Exception {
		String docId = storageService.save(file);
		File uploadedDoc = storageService.findByPrefix(docId);

		signReq.setCertStr(this.parseUserCertficate(signReq.getCertStr(), signReq.getUserId()));
		PdfDocument doc = this.digest(docId, signReq);

		Document resultDoc = new Document(docId, file.getOriginalFilename());
		resultDoc.setSigField(doc.getSigField());
		resultDoc.setDigest(doc.getDigest());
		resultDoc.setPath(uploadedDoc.getAbsolutePath());
		return resultDoc;
	}

	public String parseUserCertficate(String certStr, String userId) throws Exception {
		Certificate[] certChain;

		if (StringUtils.isBlank(certStr)) {
			certChain = this.getCertificateFromsigningServer(userId);
		} else {
			certChain = CertificateUtils.getChainFromsigningServerCert(certStr);
		}
		return CertificateUtils.getHexStrFromCertChain(certChain);
	}

	public Certificate[] getCertificateFromsigningServer(String userId) throws Exception {
		AuthSuccess authSuccess = eSignApiService.auth();

		CertData userCertResult = eSignApiService.getUserCertificate(authSuccess.getAccessToken(),
				new CertificateRequest(userId));
		//logger.debug("userCert :  {}", userCertResult.getData().getCertificateChain().get(0));
		//List<String> certs = userCertResult.getData().getCertificateChain();
		//return CertificateUtils.getCertListFromsigningServerList(certs);
		return CertificateUtils.getChainFromsigningServerCert(userCertResult.getCertificate());
	}

	public PdfDocument digest(String fileName, SignRequest signReq) throws Exception {
		File orgFile = storageService.findByPrefix(fileName);
		String tempFileName = storageService.copyToFile(orgFile, Constants.PREFIX_TO_SIGN); //todo: copy file or name
		logger.info("unsigned_file_name - {}", tempFileName);
		Path tempFile = Paths.get(tempFileName);

		PdfDocument doc = new PdfDocument();
		doc.setName(orgFile.getName());
		doc.setPath(orgFile.getAbsolutePath());//from file
		doc.setToBeSignedName(tempFileName);
		doc.setToBeSignedPath(tempFile.toAbsolutePath().toString());//from path
		doc.setSignReq(signReq);

		//	Certificate[] chain = CertificateUtils.getChainFromsigningServerCert(certChain);

		//	logger.info("getThumbPrint: " + CertificateUtils.getThumbPrint(certChain[0]));
		//	logger.info("getHexStrFromCertChain: " + CertificateUtils.getHexStrFromCertChain(certChain));
		//logger.info("cert {}", signReq.getCertStr());

		Certificate[] certChain = CertificateUtils.getCertChainFromHexStr(signReq.getCertStr());
		PdfSignatureHandler.emptySignature(certChain, doc);
		String hash = PdfSignatureHandler.makeHash(certChain, doc);
		//TODO: enable it 
		/*	String digest = PdfSignatureHandler.preSignHash(certChain, hash);
			doc.setDigest(digest);
			return doc;*/
		//Disable it
		doc.setDigest(hash);
		return doc;
	}

	public SignedDocument merge(MergeRequest mergeReq) throws Exception {
		logger.info("mergeReq.getSignedDigest() {}", mergeReq.getSignedDigest());
		String signedDigest = CertificateUtils.removeNewLine(mergeReq.getSignedDigest());
		logger.info("signedDigest - " + signedDigest);

		File orgFile = storageService.findByPrefix(mergeReq.getFileId());

		String toBeSigned = Constants.PREFIX_TO_SIGN + mergeReq.getFileId();
		File toBeSignedFile = storageService.findByPrefix(toBeSigned);
		logger.info("toBeSignedFile- {}", toBeSignedFile.getAbsolutePath());

		String signedName = Constants.PREFIX_SIGNED + orgFile.getName();
		String signedPath = storageService.copyNameWithPrefix(orgFile, Constants.PREFIX_SIGNED);
		logger.info("signedPath- {}", signedPath);

		mergeReq.setCertStr(this.parseUserCertficate(mergeReq.getCertStr(), mergeReq.getUserId()));
		Certificate[] certChain = CertificateUtils.getCertChainFromHexStr(mergeReq.getCertStr());

		//TODO: enable
		//	String signedEncoded = PdfSignatureHandler.preMergeSignature(certChain, mergeReq.getDigest(), signedDigest);

		//	logger.info("signedEncoded - {}", signedEncoded);

		PdfDocument pdfDoc = new PdfDocument();
		pdfDoc.setId(mergeReq.getFileId());
		pdfDoc.setName(signedName);
		pdfDoc.setPath(signedPath);
		pdfDoc.setToBeSignedName(toBeSignedFile.getName());
		pdfDoc.setToBeSignedPath(toBeSignedFile.getAbsolutePath());
		pdfDoc.setSigField(mergeReq.getSigField());
		pdfDoc.setDigest(mergeReq.getDigest());
		pdfDoc.setSignedEncoded(signedDigest);

		PdfSignatureHandler.mergeSignaturePdf(pdfDoc);

		if (toBeSignedFile.exists()) {
			try {
				//boolean deleted = toBeSignedFile.delete();
				//	Files.delete(Paths.get(toBeSignedFile.getAbsolutePath()));
				//FileUtils.forceDelete(toBeSignedFile);
				//logger.info("File {} is deleted : {}", toBeSignedFile.getName(), deleted);
			} catch (Exception e) {
				logger.error("File delete error  : {}", e.getMessage());
			}
		}

		SignedDocument signedDoc = new SignedDocument(pdfDoc);
		String url = storageService.getUrl(signedName);
		signedDoc.setUrl(url);
		return signedDoc;
	}
}
