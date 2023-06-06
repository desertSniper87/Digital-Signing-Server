package com.ca.signer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonErrorController {

	private static final Logger logger = LoggerFactory.getLogger(CommonErrorController.class);

	@GetMapping("/403")
	public String error403() {
		return "error/403";
	}

	@GetMapping("/404")
	public String error404() {
		return "error/404";
	}

	@GetMapping("/500")
	public String error500() {
		return "error/500";
	}

}
