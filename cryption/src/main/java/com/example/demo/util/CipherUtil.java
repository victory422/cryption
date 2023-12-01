package com.example.demo.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Properties;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherSpi;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil extends Cipher {
	private final String ECB;
	private final String CBC;
	private final String RSA;
	private final String key_128;
	private final String key_192;
	private final String key_256;
	private String iv = "aesiv12345678912";
	
	public CipherUtil(CipherSpi cipherSpi, Provider provider, String transformation) throws IOException {
		super(cipherSpi, provider, transformation);
		String resource = "./src/main/resources/static/config/config.properties";
		 
        Properties properties = new Properties();
        Reader reader;
		reader = new FileReader(resource);
		properties.load(reader);
		System.out.println(properties.getProperty("1"));
		this.ECB =  properties.getProperty("ECB");
		this.CBC =  properties.getProperty("CBC");
		this.RSA =  properties.getProperty("RSA");
		this.key_128 =  properties.getProperty("key_128");
		this.key_192 =  properties.getProperty("key_256");
		this.key_256 =  properties.getProperty("key_256");
	}
	
	public Cipher setCipherInit(String cipherCd, int keySize) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
		Cipher c = null;
		
		SecretKeySpec secretKey = createSecretKey(keySize);
		
		switch( cipherCd ) {
		case "E" :	//ECB 방식
			c = Cipher.getInstance(ECB);
			//Cipher 객체 초기화
			c.init(Cipher.ENCRYPT_MODE, secretKey);
			break;
		case "C" :	//CBC
			c = Cipher.getInstance(CBC);
			//Cipher 객체 초기화
			c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
			break;
		default : 
			new Exception("Input chipher param wrong.");
			break;
		}
		
		return c;
	}
	
	
	private SecretKeySpec createSecretKey( int keySize ) 
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		// sercretKey 객체생성
		SecretKeySpec secretKey = null;
		
		switch ( keySize ) {
			case 128 : 
				secretKey = new SecretKeySpec(key_128.getBytes("UTF-8"), "AES");
				break;
			case 192 : 
				secretKey = new SecretKeySpec(key_192.getBytes("UTF-8"), "AES");
				break;
			case 256 :
				secretKey = new SecretKeySpec(key_256.getBytes("UTF-8"), "AES");
				break;
			default :	// 길이에 따른 예외처리
				new Exception("Input key length wrong.");
				break;
		}
		
		return secretKey;
	}
	
	

}
