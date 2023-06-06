package com.ca.signer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ca.signer.constants.Constants;
import com.ca.signer.model.PdfDocument;
import com.ca.signer.pdf.PdfSignatureHandler;
import com.ca.signer.utils.CertificateUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfSignatureHandlerTest {
	private static final Logger logger = LoggerFactory.getLogger(PdfSignatureHandlerTest.class);

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSign() throws Exception {

		// client
		PdfSignatureHandler.loadKeys();

		String certStr = "MIIExDCCA6ygAwIBAgIUZjzoZtxL8rELqB0xIhN86OlZLJowDQYJKoZIhvcNAQELBQAwgbgxCzAJBgNVBAYTAkJEMQ0wCwYDVQQREwQxMjA3MQ4wDAYDVQQHEwVEaGFrYTEfMB0GCSqGSIb3DQEJARYQZXNpZ25AYmNjLmdvdi5iZDEqMCgGA1UEChMhQmFuZ2xhZGVzaCBDb21wdXRlciBDb3VuY2lsIChCQ0MpMSYwJAYDVQQLEx1CQ0MgQ2VydGlmeWluZyBBdXRob3JpdHkgKENBKTEVMBMGA1UEAxMMQkNDIGVTaWduIENBMB4XDTIzMDUxMDA5MzQwOFoXDTI0MDUxMDA5MzQwOFowgYYxCzAJBgNVBAYTAkJEMREwDwYDVQQHEwhEaW5hanB1cjERMA8GA1UEChMIcGVyc29uYWwxNDAyBgNVBAUTK05JRDEyNjJiMmY3YmU2M2FkZGFiZjUyYTJiODM3MTlmYzk3NGM2NDQ5ZmYxGzAZBgNVBAMTEk1ELiBTSEFSSUZVTCBJU0xBTTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKaB0C8SU9OIOMTQasORWlLA6rKLLmLVBNnqZL2BzEbhfec6bggtridmQcn+Hwcjus44bhLP2DbHzRNiQpIiZXAYpKVKpt0vMSMPPx8LGssFMvgP2CitcJIjzqO4fPdRjxscGmrS8OwLVEZFzwDhRBNQW4iAZNUbngSIyC0Gi6MYuVQa+8HUEDzzXlvwb7YVvdF4J+DK60dc+T5Mkn1IqBt3GYYBkYxG+k7KYDAc3CRnL6RjPn0SyhfJNAEDNOLtTsUCR1cVdlyKItK9cuqbqJqDm8lBTGfsn747ytxQORqUBzs+GDcseSPkhVbm0joAhAG83pOM2GSlxSk1yq7IbFUCAwEAAaOB9TCB8jAOBgNVHQ8BAf8EBAMCBsAwDAYDVR0TAQH/BAIwADBGBggrBgEFBQcBAQQ6MDgwNgYIKwYBBQUHMAGGKmh0dHA6Ly9zYW5kYm94LmVzaWduLmdvdi5iZDo4Nzc3L2Fkc3Mvb2NzcDAfBgNVHSMEGDAWgBTTO+g3vPZ/H6XzbbKfWQDLd4jQoTBKBgNVHR8EQzBBMD+gPaA7hjlodHRwOi8vY3JsLmVzaWduLmdvdi5iZDo4MDkwL2NybHMvYmNjX2VzaWduX2lzc3Vlcl9jYS5jcmwwHQYDVR0OBBYEFJ/tgAe7B8GtxB36Ur1UHjzMajokMA0GCSqGSIb3DQEBCwUAA4IBAQAp/jTJ5LGbt8WgM+YJWT0EejXSwpNdYVI6mZuEfJHkIyRf9p43keKIG6ROgKIjx15+xFpHvAzJopxUTHpJHnga8L8TxxR/hB0ykjNAY+oiS2/wNThCNoRO+tojPrtRMJZZp8C7s4BUEqYwPeP7L7zppmxG7WX+Vab9dN3XH+cPqhPQFVfnqNHNYXEjRDD/9cFmzgv606C1YxWyfcQpOd0o5tKbE8A5ZXhchTca6cPai5F+Est2+8S9Xz/4e6a/XURgTDWihHdYkIE8tO5bjclY4CCnGGexk/XuDbR1ypMHpoJxyaMmA1KuEWM1Voetp/aagd9MPF+xZUjUVgtWE9kj";
		//	PdfSignatureHandler.loadCertificate(certStr);
		// server
		PdfDocument doc = new PdfDocument();

		String baseFilePath = "E:/test-docs/jd.pdf";
		File baseFile = new File(baseFilePath);
		Path source = Paths.get(baseFile.getAbsolutePath());

		String toDoSign = System.currentTimeMillis() + "_" + baseFile.getName();
		Path toDoPath = Paths.get(source.getParent() + File.separator + toDoSign);
		Files.copy(source, toDoPath, StandardCopyOption.REPLACE_EXISTING);

		Path toBeSigned = Paths.get(source.getParent() + File.separator + Constants.PREFIX_TO_SIGN + toDoSign);
		//Files.copy(toDoPath, toBeSigned, StandardCopyOption.REPLACE_EXISTING);

		doc.setId(baseFile.getName());
		doc.setName(toDoPath.getFileName().toString());
		doc.setPath(toDoPath.toAbsolutePath().toString());
		doc.setToBeSignedName(toBeSigned.getFileName().toString());
		doc.setToBeSignedPath(toBeSigned.toAbsolutePath().toString());

		System.out.println("=========inputs========");
		System.out.println(doc);
		System.out.println("=========inputs========");

		System.out.println("=========createHash========");
		String hash = PdfSignatureHandler.createHash(doc);
		doc.setDigest(hash);
		System.out.println("Hash data:" + hash);
		System.out.println("=========createHash========");
		// client
		System.out.println("=========signedData========");
		String signedData = PdfSignatureHandler.signHashDoc(doc);

		// "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";
		System.out.println("signed data:" + signedData);
		doc.setSignedEncoded(signedData);
		System.out.println("=========signedData========");

		// server
		Path signedPath = Paths.get(source.getParent() + File.separator + "signed_" + toDoSign);
		doc.setPath(signedPath.toAbsolutePath().toString());

		PdfSignatureHandler.mergeSignaturePdf(doc);
		System.out.println("Success");

	}
}
