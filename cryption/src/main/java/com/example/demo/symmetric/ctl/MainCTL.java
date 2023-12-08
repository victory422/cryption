
package com.example.demo.symmetric.ctl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainCTL {
	
	@GetMapping("/aes")
	public String aes(HttpServletRequest request) {
		return "aes";
	}
	
	@GetMapping("/rsa")
	public String rsa() {
		return "rsa";
	}

	@GetMapping("/sha")
	public String sha() {
		return "sha";
	}
	
	@GetMapping("/scraping")
	public String scraping() {
		return "scraping";
	}
	
	
}
