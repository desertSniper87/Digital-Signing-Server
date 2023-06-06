package com.ca.signer.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupComponent implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(StartupComponent.class);

	@Value("${esign.upload.path:'uploads'}")
	private String ESIGN_UPLOAD_PATH;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Override
	public void run(String... args) throws Exception {

		logger.info("Current Env - {}", activeProfile);

		Path root = Paths.get(ESIGN_UPLOAD_PATH);
		if (!Files.exists(root)) {
			logger.info("Upload directory is created successfully- {}", root.getFileName().toString());
			Files.createDirectory(root);
		} else {
			logger.info("Upload directory exists - {}", root.getFileName().toString());
		}

	}
}