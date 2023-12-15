package com.example.demo.interceptor;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class InitSettingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ReadableRequestBodyWrapper cachedBodyHttpServletWrapper = new ReadableRequestBodyWrapper(httpRequest);
        chain.doFilter(cachedBodyHttpServletWrapper, response);
    }

    @Override
    public void destroy() {
    }
}