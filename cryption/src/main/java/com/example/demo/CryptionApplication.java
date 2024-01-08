package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dreamsecurity.jcaos.jce.provider.JCAOSProvider;

@SpringBootApplication
public class CryptionApplication {
	static {
		JCAOSProvider.installProvider();	
	}
	public static void main(String[] args) {
		SpringApplication.run(CryptionApplication.class, args);
	}

}
