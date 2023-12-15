package com.example.demo.interceptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.Reader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class ReadableRequestBodyWrapper extends HttpServletRequestWrapper {
	  class ServletInputStreamImpl extends ServletInputStream {
	    private InputStream inputStream;

	    public ServletInputStreamImpl(final InputStream inputStream) {
	      this.inputStream = inputStream;
	    }

	    @Override
	    public boolean isFinished() {
	      // TODO Auto-generated method stub
	      return false;
	    }

	    @Override
	    public boolean isReady() {
	      // TODO Auto-generated method stub
	      return false;
	    }

	    @Override
	    public int read() throws IOException {
	      return this.inputStream.read();
	    }

	    @Override
	    public int read(final byte[] b) throws IOException {
	      return this.inputStream.read(b);
	    }

	    @Override
	    public void setReadListener(final ReadListener listener) {
	      // TODO Auto-generated method stub
	    }
	  }

	  private byte[] bytes;
	  private String requestBody;

	  public ReadableRequestBodyWrapper(final HttpServletRequest request) throws IOException {
		  super(request);

	    // request의 InputStream의 content를 byte array로 
	    StringBuilder textBuilder = new StringBuilder();
	    try {
	    	InputStream in = super.getInputStream();
	    	Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
	    	int c = 0;
	    	while ((c = reader.read()) != -1) {
	    		textBuilder.append((char) c);
	    	}
	    } catch (Exception e) {
	    	System.out.println("@@@@ err");
	    	
		}
	    this.bytes = textBuilder.toString().getBytes();
	    
	    // 그 데이터는 따로 저장한다
	    this.requestBody = textBuilder.toString();
	  }

	  @Override
	  public ServletInputStream getInputStream() throws IOException {
	    // InputStream을 반환해야하면 미리 구해둔 byte array 로
	    // 새 InputStream을 만들고 이걸로 ServletInputStream을 새로 만들어 반환
	    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bytes);
	    return new ServletInputStreamImpl(byteArrayInputStream);
	  }

	  public String getRequestBody() {
	    return this.requestBody;
	  }
	}