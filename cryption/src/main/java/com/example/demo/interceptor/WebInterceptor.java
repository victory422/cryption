package com.example.demo.interceptor;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class WebInterceptor implements HandlerInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger( WebInterceptor.class );
	
	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		
		logger.info("bestMatchingPattern : {}", request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern"));
		logger.info("bestMatchingHandler : {}", request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler"));
		
		if (request.getContentType() != null && request.getContentType().contains("application/json")) {
			ReadableRequestBodyWrapper wrapper = new ReadableRequestBodyWrapper((HttpServletRequest) request);
			wrapper.setAttribute("requestBody", wrapper.getRequestBody());
			logger.info("requestBody : {}", request.getAttribute("requestBody"));
		}

//		Enumeration params = request.getHeaderNames();
//		System.out.println("----------------------------");
//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +request.getHeader(name));
//		}
//		System.out.println("----------------------------");
		
		
		return true;
	}
	
}
