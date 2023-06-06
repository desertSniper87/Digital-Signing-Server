package com.ca.signer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class EsignContainerServerApp {

	private static final Logger log = LoggerFactory.getLogger(EsignContainerServerApp.class);

	public static void main(String[] args) {
		SpringApplication.run(EsignContainerServerApp.class, args);

	}
}
