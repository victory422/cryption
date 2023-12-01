package com.example.demo.symmetric.svc.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.symmetric.svc.OneWayEncService;

@Service
public class OneWayEncServiceImpl implements OneWayEncService {
	
	@Override
	public Map<String, String> oneWayEncrypt(String pwd, String bfCd) {
		Map<String, String> rstMap = new HashMap<>();
		String rst = "";
		try {
			if( bfCd != null && "salt".equalsIgnoreCase(bfCd) ) {
				rst = this.getEncrypt(pwd, this.getSalt());
			} else {
				rst = this.getEncrypt(pwd, "");
			}
			rstMap.put("result", "S");
			rstMap.put("resultVal", rst);
		} catch (Exception e) {
			rstMap.put("result", "E");
		}
		
		return rstMap;
	}
	
	private String getSalt() {
		SecureRandom r = new SecureRandom();
		byte[] salt = new byte[20];
		
		r.nextBytes(salt);
		
		
		StringBuffer sb = new StringBuffer();
		
		for( byte b : salt ) {
			sb.append(String.format("%02x", b));
		}
		
		return sb.toString();
	}
	
	
	private String getEncrypt(String pwd, String salt) {
		String rst = "";
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update((pwd+salt).getBytes());
			byte[] pwdsalt = md.digest();
			
			StringBuffer sb = new StringBuffer();
			for( byte b : pwdsalt ) {
				sb.append(String.format("%02x", b));
			}
			
			rst = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		
		return rst;
	}

}
