package com.ca.signer.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateUtils {

	private static final Logger logger = LoggerFactory.getLogger(CertificateUtils.class);
	private final static String COUNTRY = "2.5.4.6";
	private final static String STATE = "2.5.4.8";
	private final static String LOCALE = "2.5.4.7";
	private final static String ORGANIZATION = "2.5.4.10";
	private final static String ORGANIZATION_UNIT = "2.5.4.11";
	private final static String COMMON_NAME = "2.5.4.3";
	private static final String TEST_POLICY_OID = "2.5.29.32";

	public static String SERNIAL_NUMBER_PREFIX = "NID";
	public static String SUBJECT_DN_O_DEFAULT = "personal";
	public static String SUBJECT_DN_C_DEFAULT = "BD";

	public static PKCS10CertificationRequest convertPemToPKCS10CertificationRequest(String pem) throws IOException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		PKCS10CertificationRequest csr = null;

		ByteArrayInputStream pemStream = null;
		try {
			pemStream = new ByteArrayInputStream(pem.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			logger.error("UnsupportedEncodingException, convertPemToPublicKey" + ex);
		}

		Reader pemReader = new BufferedReader(new InputStreamReader(pemStream));

		try (PEMParser pemParser = new PEMParser(pemReader)) {
			Object parsedObj = pemParser.readObject();

			if (parsedObj instanceof PKCS10CertificationRequest) {
				csr = (PKCS10CertificationRequest) parsedObj;
			}
		} catch (IOException ex) {
			logger.error("IOException, convertPemToPublicKey" + ex);
			throw ex;
		}

		return csr;
	}

	@Deprecated
	private static String getX500FieldOld(String asn1ObjectIdentifier, X500Name x500Name) {
		RDN[] rdnArray = x500Name.getRDNs();
		String retVal = null;
		for (RDN item : rdnArray) {
			AttributeTypeAndValue at = item.getFirst();
			logger.debug(at.getType().toString() + " " + at.getValue().toString());
			retVal = item.getFirst().getValue().toString();
		}
		return retVal;
	}

	public static String getX500Field(ASN1ObjectIdentifier asn1ObjectIdentifier, X500Name x500Name) {
		RDN[] rdnArray = x500Name.getRDNs(asn1ObjectIdentifier);
		String retVal = null;
		for (RDN item : rdnArray) {
			retVal = item.getFirst().getValue().toString();
		}

		return retVal;
	}

	public static String getStrFromPKCS10CertificationRequest(PKCS10CertificationRequest csr) throws IOException {
		String str = "-----BEGIN CERTIFICATE REQUEST-----\n";
		str += new String(Base64.getEncoder().encodeToString(csr.getEncoded()));
		str += "\n-----END CERTIFICATE REQUEST-----";
		return str;
	}

	public static String getFormattedCertStr(String pemCertStr) throws IOException {
		String certificateStr = "-----BEGIN CERTIFICATE-----\n";
		certificateStr += pemCertStr;
		certificateStr += "\n-----END CERTIFICATE-----";
		return certificateStr;
	}

	public static String getPemFormattedCertStr(Certificate cert) throws IOException, CertificateEncodingException {
		return new String(Base64.getEncoder().encodeToString(cert.getEncoded()));
	}

	public static String removeNewLine(String str) throws IOException {
		return str.replaceAll("\\r\\n", "");
	}

	public static String cleanAndFormatCertStr(String pemCertStr) throws IOException {
		return getFormattedCertStr(removeNewLine(pemCertStr));
	}

	public static String getSha1FromStr(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static String getKeyStrFormKey(Key key) {
		String b64 = new String(Base64.getEncoder().encodeToString(key.getEncoded()));
		b64 = "-----BEGIN PRIVATE/PUBLIC KEY-----" + b64 + "-----END PRIVATE/PUBLIC KEY-----";
		logger.debug(b64);
		return b64;
	}

	public static String getContentFromCertificatePath(String certPath) throws IOException {
		File f = new File(certPath);
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		in.readFully(buffer);
		in.close();
		return new String(buffer, 0, (int) f.length(), StandardCharsets.UTF_8);
	}

	public static X509Certificate getX09FromP7bPath(String p7bFilePath)
			throws CMSException, CertificateException, IOException {
		File f = new File(p7bFilePath);
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		in.readFully(buffer);
		in.close();

		// Get digital certificate with type: 2
		// Corresponding class of signed_data is CMSSignedData
		CMSSignedData signature = new CMSSignedData(org.bouncycastle.util.encoders.Base64.decode(buffer));

		Store cs = signature.getCertificates();
		Collection<X509CertificateHolder> allCerts = cs.getMatches(null);

		Iterator<X509CertificateHolder> holders = allCerts.iterator();
		X509Certificate[] chains = new X509Certificate[allCerts.size()];
		int i = 0;
		while (holders.hasNext()) {
			X509CertificateHolder holder = holders.next();
			chains[i++] = new JcaX509CertificateConverter().getCertificate(holder);
		}
		// logger.debug("chain length: " + chains.length);

		X509Certificate chain = chains[0];

		return chain;
	}

	public static X509Certificate convertPemStrToX509(String pem) throws CertificateException, IOException {
		InputStream targetStream = new ByteArrayInputStream(pem.getBytes());
		return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(targetStream);
	}

	public static String getPemFormattedCertStr(X509Certificate x509)
			throws CertificateEncodingException, CertificateException, CMSException, IOException {
		org.apache.commons.codec.binary.Base64 encoder = new org.apache.commons.codec.binary.Base64(64);
		byte[] data = x509.getEncoded();
		String x509str = new String(encoder.encode(data));
		return x509str;
	}

	public static String getCertificatePolicyId(X509Certificate cert, int certificatePolicyPos, int policyIdentifierPos)
			throws IOException {
		byte[] extPolicyBytes = cert.getExtensionValue(TEST_POLICY_OID);
		if (extPolicyBytes == null) {
			return null;
		}

		DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(extPolicyBytes))
				.readObject());
		ASN1Sequence seq = (ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(oct.getOctets())).readObject();

		if (seq.size() <= (certificatePolicyPos)) {
			return null;
		}

		CertificatePolicies certificatePolicies = new CertificatePolicies(
				PolicyInformation.getInstance(seq.getObjectAt(certificatePolicyPos)));
		if (certificatePolicies.getPolicyInformation().length <= policyIdentifierPos) {
			return null;
		}

		PolicyInformation[] policyInformation = certificatePolicies.getPolicyInformation();
		return policyInformation[policyIdentifierPos].getPolicyIdentifier().getId();
	}

	public static byte[] addPadding(byte[] digest) {
		byte[] sha256bytes = new byte[] { 0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03,
				0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20 };
		return ArrayUtils.addAll(sha256bytes, digest);
	}

	//================added===========
	public static Certificate[] getCertListFromHexStrList(List<String> certs) throws CertificateException {
		Certificate[] chain = new Certificate[certs.size()];
		int i = 0;
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		for (String certHex : certs) {
			Certificate myCert = cf.generateCertificate(
					// string encoded with default charset
					new ByteArrayInputStream(Hex.decode(certHex)));
			chain[i++] = myCert;
		}
		return chain;
	}

	public static Certificate[] getChainFromsigningServerCert(String certStr) throws IOException, CertificateException {
		String formattedCert = cleanAndFormatCertStr(certStr);

		InputStream targetStream = new ByteArrayInputStream(formattedCert.getBytes());
		Certificate certificate = CertificateFactory.getInstance("X509").generateCertificate(targetStream);
		Certificate[] chain = new Certificate[] { certificate };
		return chain;
	}

	public static Certificate[] getCertListFromsigningServerList(List<String> certs) throws CertificateException, IOException {
		Certificate[] chain = new Certificate[certs.size()];
		int i = 0;
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		for (String certStr : certs) {
			Certificate myCert = cf
					.generateCertificate(new ByteArrayInputStream(cleanAndFormatCertStr(certStr).getBytes()));
			chain[i++] = myCert;
		}
		return chain;
	}

	public static Certificate[] getCertChainFromHexStr(String certHexStr) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		Certificate myCert = cf.generateCertificate(new ByteArrayInputStream(Hex.decode(certHexStr)));
		Certificate[] chain = new Certificate[] { myCert };
		return chain;
	}

	public static String getHexStrFromCertChain(Certificate[] chain) throws CertificateException {
		byte[] hex = chain[0].getEncoded();
		String hexText = DatatypeConverter.printHexBinary(hex);
		return hexText;
	}

	public static String getThumbPrint(Certificate chain)
			throws NoSuchAlgorithmException, CertificateEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] der = chain.getEncoded();
		md.update(der);
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest);
	}

	public static String hexify(byte bytes[]) {

		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

		StringBuffer buf = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; ++i) {
			buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			buf.append(hexDigits[bytes[i] & 0x0f]);
		}

		return buf.toString().toUpperCase();
	}

}
