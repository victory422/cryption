package com.example.demo.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;


public class Common {
	private String AESPATH;
	private String RSAPATH;
	private String ECB;
	private String CBC;
	private String AESFILENAME;
	
	public Common() throws FileNotFoundException, IOException {
		String configPath = "./src/main/resources/static/config/config.properties";
		//server
		if ( !this.serverCheck(this.getServerName()) ) {
			configPath = "./config.properties";
		}
		/*
		File folder = new File(configPath);
		for( String t : folder.list()) {
			System.out.println(t);
		}
		*/
        Properties properties = new Properties();
		properties.load(new FileReader(configPath));
		
		// local
		if ( this.serverCheck(this.getServerName()) ) {
			this.AESPATH = properties.getProperty("AESPATH-LOCAL");
			this.RSAPATH = properties.getProperty("RSAPATH-LOCAL");
			
		// server
		} else {
			this.AESPATH = properties.getProperty("AESPATH-LINUX");
			this.RSAPATH = properties.getProperty("RSAPATH-LINUX");
		}
				
		
		this.ECB =  properties.getProperty("ECB");
		this.CBC =  properties.getProperty("CBC");
		this.AESFILENAME = properties.getProperty("AESFILENAME");
		/*
		System.out.println(this.RSAPATH);
		System.out.println(this.AESPATH);
		System.out.println(this.AESFILENAME);
		System.out.println(this.ECB);
		System.out.println(this.CBC);
		*/
	}
	
	
	public String getKeyVal(String key) {
		
		switch( key ) {
			case "AESPATH" : return this.AESPATH;
			case "RSAPATH" : return this.RSAPATH;
			case "ECB" : return this.ECB;
			case "CBC" : return this.CBC;
			case "AESFILENAME" : return this.AESFILENAME;
			default : return "";
		}
	}
	
	public String getServerName() {
		String rst = "";
		BufferedReader bf = null;
		try {
			Process p = Runtime.getRuntime().exec("uname -n");
			bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			while( (rst = bf.readLine()) != null ) {
				System.out.println(rst);
			}
			
		} catch (IOException e) {
			rst = "";
		}
		return rst;
	}
	
	public String getClientIp(HttpServletRequest request) {
	    String ip = request.getHeader("X-Forwarded-For");

	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("X-Real-IP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("X-RealIP");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("REMOTE_ADDR");
	    }
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
			
	    return ip;
	}
	
	public boolean serverCheck(String ip) {
		boolean rst = false;
		List<String> localChkIp = new ArrayList<>();
		localChkIp.add("");
		localChkIp.add("127.0.0.1");
		localChkIp.add("0:0:0:0:0:0:0:1");
		
		if( localChkIp.contains(ip) ) {
			rst = true;
		} else {
			rst = false;
		}
		
		return rst;
	}
	
		
	
}
