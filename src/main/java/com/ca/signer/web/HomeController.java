package com.ca.signer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String home(Model model) {

		return "index";
	}

}
