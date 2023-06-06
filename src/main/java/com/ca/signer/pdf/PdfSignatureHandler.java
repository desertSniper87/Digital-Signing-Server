package com.ca.signer.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.signer.constants.Constants;
import com.ca.signer.files.FilesController;
import com.ca.signer.model.PdfDocument;
import com.ca.signer.model.SignRequest;
import com.ca.signer.utils.CertificateUtils;
import com.ca.signer.utils.StringUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RASInputStream;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.io.StreamUtil;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalBlankSignatureContainer;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

public class PdfSignatureHandler {
	private static final Logger logger = LoggerFactory.getLogger(PdfSignatureHandler.class);

	public static String hashAlgorithm = "SHA256"; // "SHA-256"
	public static String encryptionAlg = "RSA";

	public static void emptySignature(Certificate[] clientCertChain, PdfDocument document)
			throws IOException, DocumentException, GeneralSecurityException {
		try {
			PdfReader reader = new PdfReader(document.getPath());
			FileOutputStream os = new FileOutputStream(document.getToBeSignedPath());
			PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
			PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
			String fieldName = appearance.getFieldName();
			document.setSigField(fieldName);
			logger.info("Singnature fieldName: " + fieldName);
			appearance.setCertificate(clientCertChain[0]);

			if (document.getSignReq() != null) {
				SignRequest signReq = document.getSignReq();
				if (StringUtils.isNotBlank(signReq.getReason()))
					appearance.setReason(signReq.getReason());

				if (StringUtils.isNotBlank(signReq.getLocation()))
					appearance.setLocation(signReq.getLocation());
				int pageNumber = signReq.getPageNumber() == 0 ? 1 : signReq.getPageNumber();
				Rectangle rect = new Rectangle(signReq.getLeftX(), signReq.getLeftY(), signReq.getRightX(),
						signReq.getRightY());
				logger.info(rect.toString());
				appearance.setVisibleSignature(rect, pageNumber, fieldName);
			} else {
				appearance.setReason("This is digitally signed");
				appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, fieldName);
			}

			ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
					PdfName.ADBE_PKCS7_DETACHED);
			MakeSignature.signExternalContainer(appearance, external, 8192);

			stamper.close();
			os.close();
			reader.close();
		} catch (Exception e) {
			logger.error("Empty Signature error", e);
			throw e;
		} finally {
			//TODO close
		}
	}

	// rather than taking input stream, can it take hash ?
	public static String makeHash(Certificate[] clientCertChain, PdfDocument document) throws Exception {
		try {
			PdfReader reader = new PdfReader(document.getToBeSignedPath());
			AcroFields af = reader.getAcroFields();
			PdfDictionary v = af.getSignatureDictionary(document.getSigField());
			logger.info("Signature fieldname: " + document.getSigField());
			if (v == null)
				throw new DocumentException("No field");
			if (!af.signatureCoversWholeDocument(document.getSigField()))
				throw new DocumentException("Not the last signature");
			PdfArray b = v.getAsArray(PdfName.BYTERANGE);
			long[] gaps = b.asLongArray();
			if (b.size() != 4 || gaps[0] != 0)
				throw new DocumentException("Single exclusion space supported");
			RandomAccessSource readerSource = reader.getSafeFile().createSourceView();
			InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));

			// PrivateKeySignature signature = new PrivateKeySignature(pk, "SHA256",
			// "SunMSCAPI");
			String hashAlgorithm = "SHA256"; // "SHA-256"
			BouncyCastleDigest digest = new BouncyCastleDigest();
			// saving the hash for using later
			byte hash[] = DigestAlgorithms.digest(rg, digest.getMessageDigest(hashAlgorithm));

			// logger.debug("pre-hash: " + Arrays.toString(hash));
			String encodedhHash = Base64.getEncoder().encodeToString(hash);
			document.setDigest(encodedhHash);

			rg.close();
			reader.close();

			return encodedhHash;

		} catch (IOException ioe) {
			throw new ExceptionConverter(ioe);
		} finally {
			//TODO close
		}
	}

	public static void mergeSignaturePdf(PdfDocument document)
			throws IOException, DocumentException, GeneralSecurityException {

		PdfReader reader = new PdfReader(document.getToBeSignedPath());
		FileOutputStream os = new FileOutputStream(document.getPath());
		try {
			//ExternalSignatureContainer external = new MyExternalSignatureContainer(pk, chain);
			//MakeSignature.signDeferred(reader, fieldname, os, external);
			byte[] signedEncoded = Base64.getDecoder().decode(document.getSignedEncoded());
			// logger.debug("post-signedEncoded: " + Arrays.toString(signedEncoded));
			signDeferred(signedEncoded, reader, os, document);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Merge Error ", e);
			throw e;
		} finally {
			reader.close();
			os.close();
		}

	}

	public static void signDeferred(byte[] signedContent, PdfReader reader, OutputStream outs, PdfDocument document)
			throws DocumentException, IOException, GeneralSecurityException {
		try {
			logger.info("Signature fieldname: " + document.getSigField());
			AcroFields af = reader.getAcroFields();
			for (Map.Entry<String, Item> item : af.getFields().entrySet()) {
				//
			}
			PdfDictionary v = af.getSignatureDictionary(document.getSigField());
			if (v == null)
				throw new DocumentException("No field");
			if (!af.signatureCoversWholeDocument(document.getSigField()))
				throw new DocumentException("Not the last signature");
			PdfArray b = v.getAsArray(PdfName.BYTERANGE);
			long[] gaps = b.asLongArray();
			if (b.size() != 4 || gaps[0] != 0)
				throw new DocumentException("Single exclusion space supported");
			RandomAccessSource readerSource = reader.getSafeFile().createSourceView();

			int spaceAvailable = (int) (gaps[2] - gaps[1]) - 2;
			if ((spaceAvailable & 1) != 0)
				throw new DocumentException("Gap is not a multiple of 2");
			spaceAvailable /= 2;
			if (spaceAvailable < signedContent.length)
				throw new DocumentException("Not enough space");
			StreamUtil.CopyBytes(readerSource, 0, gaps[1] + 1, outs);
			ByteBuffer bb = new ByteBuffer(spaceAvailable * 2);
			for (byte bi : signedContent) {
				bb.appendHex(bi);
			}
			int remain = (spaceAvailable - signedContent.length) * 2;
			for (int k = 0; k < remain; ++k) {
				bb.append((byte) 48);
			}
			bb.writeTo(outs);
			StreamUtil.CopyBytes(readerSource, gaps[2] - 1, gaps[3] + 1, outs);
			reader.close();
			bb.close();
			outs.close();
		} catch (Exception e) {
			throw e;
		} finally {
			//TODO close
		}
	}

	//Test for client test purposes
	public static PrivateKey pk;
	public static Certificate[] serverCertChain;

	public static void loadKeys() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableKeyException {
		try {
			BouncyCastleProvider providerBC = new BouncyCastleProvider();
			Security.addProvider(providerBC);
			KeyStore ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			ks.load(null, null);

			String alias = null;
			Enumeration<String> enumeration = ks.aliases();
			while (enumeration.hasMoreElements()) {
				alias = enumeration.nextElement();
				logger.debug("alias name: " + alias);
				if (alias.equals("gosignsdk"))
					break;
			}

			pk = (PrivateKey) ks.getKey(alias, null);
			serverCertChain = ks.getCertificateChain(alias);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static void loadCertificate(String certStr) throws KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
		serverCertChain = CertificateUtils.getChainFromsigningServerCert(certStr);
		logger.info(serverCertChain[0].toString());
	}

	public static Certificate[] getCertChains() throws UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		if (serverCertChain == null || serverCertChain.length == 0)
			loadKeys();
		return serverCertChain;
	}

	public static PrivateKey getPK() throws UnrecoverableKeyException, KeyStoreException, NoSuchProviderException,
			NoSuchAlgorithmException, CertificateException, IOException {
		if (pk == null)
			loadKeys();
		return pk;
	}

	public static String preSignHash(Certificate[] serverCertChain, String hashEncoded)
			throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {

		byte[] hashBytes = Base64.getDecoder().decode(hashEncoded);

		BouncyCastleDigest bcDigest = new BouncyCastleDigest();
		PdfPKCS7 sgn = new PdfPKCS7(null, serverCertChain, hashAlgorithm, null, bcDigest, false);

		byte[] sh = sgn.getAuthenticatedAttributeBytes(hashBytes, null, null, CryptoStandard.CMS);
		String preSignHash = Base64.getEncoder().encodeToString(sh);
		return preSignHash;
	}

	public static String preMergeSignature(Certificate[] serverCertChain, String digest, String extSignature)
			throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {
		byte[] hashBytes = Base64.getDecoder().decode(digest);
		byte[] extSignatureBytes = Base64.getDecoder().decode(extSignature);

		BouncyCastleDigest bcDigest = new BouncyCastleDigest();
		PdfPKCS7 sgn = new PdfPKCS7(null, serverCertChain, hashAlgorithm, null, bcDigest, false);
		sgn.setExternalDigest(extSignatureBytes, null, encryptionAlg);
		byte[] signBytes = sgn.getEncodedPKCS7(hashBytes, null, null, null, CryptoStandard.CMS);
		String signedEncoded = Base64.getEncoder().encodeToString(signBytes);
		return signedEncoded;
	}

	// rather than taking input stream, can it take hash ?
	public static byte[] signHash(byte[] hash) throws Exception {
		PrivateKeySignature signature = new PrivateKeySignature(pk, "SHA256", "SunMSCAPI");
		//		InputStream is= getInputStream();
		//		
		BouncyCastleDigest digest = new BouncyCastleDigest();
		PdfPKCS7 sgn = new PdfPKCS7(null, serverCertChain, hashAlgorithm, null, digest, false);
		//saving the hash for using later
		//      hash = DigestAlgorithms.digest(is, digest.getMessageDigest(hashAlgorithm));
		//		Calendar cal = Calendar.getInstance();
		//      byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, cal, null, null, CryptoStandard.CMS);
		//      byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, null, null, CryptoStandard.CMS);
		//      return sh;

		byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, null, null, CryptoStandard.CMS);
		String shStr = Base64.getEncoder().encodeToString(sh);
		//Local Sign
		logger.debug("AuthenticatedAttrHash: \n" + shStr);
		byte[] extSignature = signature.sign(sh);
		//Remote sign
		/*	Scanner scanner = new Scanner(System.in); // Create a Scanner object
			System.out.println("Enter signature");
			String signaure = scanner.nextLine();
			byte[] extSignature = Base64.getDecoder().decode(StringUtils.escapeCharFromSignature(signaure));*/
		/*end*/

		System.out.println(signature.getEncryptionAlgorithm());
		sgn.setExternalDigest(extSignature, null, Constants.encryptionAlg);
		//		return sgn.getEncodedPKCS7(hash, cal, null, null, null, CryptoStandard.CMS);
		return sgn.getEncodedPKCS7(hash, null, null, null, CryptoStandard.CMS);
	}

	public static String createHash(PdfDocument doc) throws Exception {
		if (serverCertChain == null || serverCertChain.length == 0) {
			throw new Exception("Cert chain is not loaded");
		}
		logger.debug("Selected Cert: " + Base64.getEncoder().encodeToString(serverCertChain[0].getEncoded()));
		emptySignature(serverCertChain, doc);
		return makeHash(serverCertChain, doc);
	}

	public static String signHashDoc(PdfDocument doc) throws Exception {
		byte[] hash = Base64.getDecoder().decode(doc.getDigest());
		byte[] signedEncoded = signHash(hash);
		String signedBase64EncodedHash = Base64.getEncoder().encodeToString(signedEncoded);
		doc.setSignedEncoded(signedBase64EncodedHash);
		return doc.getSignedEncoded();
	}

}
