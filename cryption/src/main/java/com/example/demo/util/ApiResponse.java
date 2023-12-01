package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse<T> extends ResponseEntity<Object> {

    public ApiResponse(Object body, HttpStatus status) {
		super(body, status);
		body = checkRes(body);
	}
    
    public Object checkRes(Object m) {
    	if ( m instanceof HashMap ) {
    		String rstCd = ((Map<String, String>) m).get("result");
    		switch( rstCd ) {
    			case "S" : ((Map<String, String>) m).put("resultMsg","성공"); break;
    			case "E" : ((Map<String, String>) m).put("resultMsg","에러발생"); break;
    			case "D" : ((Map<String, String>) m).put("resultMsg","키이름 중복"); break;
    			case "K" : ((Map<String, String>) m).put("resultMsg","키 중복"); break;
    			case "T" : ((Map<String, String>) m).put("resultMsg","인증성공"); break;
    			case "F" : ((Map<String, String>) m).put("resultMsg","인증실패"); break;
    			default : ((Map<String, String>) m).put("resultMsg","성공"); break;
    		}
    		
    	} else {
    		System.out.println("TTTTTT");
    	}
    	
    	return m;
    }
    
}