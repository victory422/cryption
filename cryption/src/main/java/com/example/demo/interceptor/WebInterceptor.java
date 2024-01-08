package com.example.demo.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;


public class WebInterceptor implements HandlerInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger( WebInterceptor.class );
	
	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		String bestMatchingPattern = (String) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern");
		Object matchingHandler = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
		Object uriTemplateVariables = request.getAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
		
		String bestMatchingHandler = matchingHandler == null ? "" : matchingHandler.toString();
		uriTemplateVariables = uriTemplateVariables == null ? "" : uriTemplateVariables.toString();
//		System.out.println(bestMatchingPattern);
//		System.out.println(matchedHandler);
//		System.out.println(bestMatchingHandler);
		if( bestMatchingHandler.indexOf("CTL#") > -1 ) {
			// request 값 로그를 위한 설정. 기능에는 상관없음
			bestMatchingHandler = bestMatchingHandler.substring(bestMatchingHandler.indexOf("CTL#")+4);
			logger.info("bestMatchingPattern : {}", request.getHeader("host") + bestMatchingPattern + this.getQueryString(request));
			logger.info("bestMatchingHandler : {}", bestMatchingHandler);
			if( uriTemplateVariables != null && !"{}".equalsIgnoreCase((String) uriTemplateVariables) ) {
				logger.info("uriTemplateVariables : {}", uriTemplateVariables);
			}
			if (request.getContentType() != null && request.getContentType().contains("application/json")) {
				ReadableRequestBodyWrapper wrapper = new ReadableRequestBodyWrapper((HttpServletRequest) request);
				wrapper.setAttribute("requestBody", wrapper.getRequestBody());
				logger.info("requestBody : {}", request.getAttribute("requestBody"));
			}
		}
		

//		Enumeration params = request.getAttributeNames();
//		System.out.println("----------------------------");
//		while (params.hasMoreElements()){
//		    String name = (String)params.nextElement();
//		    System.out.println(name + " : " +request.getAttribute(name));
//		}
//		System.out.println("----------------------------");
		
		
		return true;
	}
	
	
	private String getQueryString(HttpServletRequest request) {
		Enumeration<String> params = request.getParameterNames();
		int cnt = 0;
		String queryString = "";
		while (params.hasMoreElements()){
		    String name = (String)params.nextElement();
		    if( cnt == 0) {
		    	queryString += "?" + name + "=" +request.getParameter(name);
		    } else {
		    	queryString += "&" + name + "=" +request.getParameter(name);
		    }
		    cnt++;
		}
		return queryString;
	}
	
}
