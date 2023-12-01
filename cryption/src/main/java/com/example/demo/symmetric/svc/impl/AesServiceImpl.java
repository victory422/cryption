package com.example.demo.symmetric.svc.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.symmetric.svc.AesService;
import com.example.demo.util.Common;
import com.example.demo.util.JsonUtil;

@Service
public class AesServiceImpl implements AesService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);
	private String ECB;//AES-256는 256비트(32바이트)의 키
	private String CBC;//AES-256는 256비트(32바이트)의 키
	private String iv = "aesiv12345678912";
	
	JsonUtil JsonUtil = new JsonUtil();
	
	
	AesServiceImpl() {
		Common cm;
		try {
			cm = new Common();
			this.ECB = cm.getKeyVal("ECB");
			this.CBC = cm.getKeyVal("CBC");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
    //AES ECB PKCS5Padding 암호화(Hex | Base64)
	@Override
	public String aesEncode(String key, String plainText, String cipherCd, String encCd, String paramIv) throws Exception {
		// 결과값
		String rst = "";
		Cipher c = null;
		
		if( paramIv != null && !"".equals(paramIv)) {
			iv = paramIv;
		}

		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		
		
		switch( cipherCd ) {
			case "E" :	//ECB 방식
				c = Cipher.getInstance(this.ECB);
				//Cipher 객체 초기화
				c.init(Cipher.ENCRYPT_MODE, secretKey);
				break;
			case "C" :	//CBC
				c = Cipher.getInstance(this.CBC);
				//Cipher 객체 초기화
				c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
				break;
			default : 
				new Exception("Input chipher param wrong.");
				break;
		}
		
		//Encrpytion/Decryption
		byte[] encrpytionByte = c.doFinal(plainText.getBytes("UTF-8"));
		
		switch( encCd ) {
			case "B" :	//Base64 Encode
				rst = Base64.encodeBase64String(encrpytionByte);
				break;
			case "H" :	//Hex Encode
				rst = Hex.encodeHexString(encrpytionByte);
				break;
			default : 
				new Exception("Input param wrong.");
				break;
		}
		
		return rst;
	}


    //AES ECB PKCS5Padding 복호화(Hex | Base64)
	@Override
	public String aesDecode(String key, String encodeText, String cipherCd, String encCd, String paramIv) throws Exception {
		// 결과값
		String rst = "";
		byte[] decodeByte;
		Cipher c = null;

		if( paramIv != null && !"".equals(paramIv)) {
			iv = paramIv;
		}
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
		
		
		switch( cipherCd ) {
		case "E" :	//ECB 방식
			c = Cipher.getInstance(this.ECB);
			//Cipher 객체 초기화
			c.init(Cipher.DECRYPT_MODE, secretKey);
			break;
		case "C" :	//CBC
			c = Cipher.getInstance(this.CBC);
			//Cipher 객체 초기화
			c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
			break;
		default : 
			new Exception("Input chipher param wrong.");
			break;
	}
		
		switch( encCd ) {
			case "B" :	//Decode Base64
				decodeByte = Base64.decodeBase64(encodeText);
				rst = new String(c.doFinal(decodeByte), "UTF-8");
				break;
			case "H" :	//Decode Hex
				decodeByte = Hex.decodeHex(encodeText.toCharArray());
				rst = new String(c.doFinal(decodeByte), "UTF-8");
				break;
			default : 
				new Exception("Input param wrong.");
				break;
		}
	
	return rst;
		
	}
	
	
	@Override
	public String keySave(Map<String,String> map) throws NumberFormatException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException {
		String rst = "";
		boolean nmDplChk = false;
		boolean kdplChk = false;
		try {
			List<Map<String,String>> list = JsonUtil.readJson();
			
			String key = new String(this.getRandomKey(Integer.parseInt(map.get("keySize"))));
			
			if( list != null && list.size() > 0 ) {
				for( Map<String,String> m : list ) {
					if( m.get("keyName").equalsIgnoreCase(map.get("keyName")) ) {
						nmDplChk = true;
					}
					if( m.get("key").equalsIgnoreCase(key) ) {
						kdplChk = true;
					}
				}
			}
			map.put("key", key);
			//중복체크
			if( nmDplChk ) return "D";
			else if ( kdplChk ) return "K";
			else {
				JsonUtil.writeJson(map);
				rst = "S";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			rst = "E";
		}
		
		return rst;
	}
	
	@Override
	public List<Map<String, String>> keyRead(String searchStr) throws ParseException {
		List<Map<String, String>> list = JsonUtil.readJson() ;
		List<Map<String, String>> rst = new ArrayList<>();
		if( searchStr != null && !"".equals(searchStr) ) {
			for( Map<String, String> map : list ) {
				if( map.get("keyName").indexOf(searchStr) > -1 ) {
					rst.add(map);
				}
			}
		} else {
			rst = list;
		}
		return rst;
	}
	
	@Override
	public Map<String, String> keyDelete(String delKey) throws ParseException {
		Map<String,String> rst = new HashMap<>();
		
		try {
			JsonUtil.deleteJson(delKey);
			rst.put("result", "S");
		} catch (Exception e) {
			rst.put("result", "E");
		}
		
		return rst;
	}
	
	
	@Override
	public Map<String, String> keyUpdate(String updateKey, int keySize) throws NoSuchAlgorithmException {
		Map<String,String> rst = new HashMap<>();
		String newKey = this.getRandomKey(keySize);
		try {
			JsonUtil.updateJson(updateKey, newKey, keySize);
			rst.put("result", "S");
		} catch (Exception e) {
			rst.put("result", "E");
		}
		
		return rst;
	}
	
	
	private String getRandomKey(int keySize) throws NoSuchAlgorithmException {
		SecureRandom r = new SecureRandom();
		byte[] salt = "".getBytes();
		
		switch ( keySize ) {
			case 128 : salt = new byte[8]; break; 
			case 192 : salt = new byte[12]; break; 
			case 256 : salt = new byte[16]; break; 
		}
		
		r.nextBytes(salt);
		
		StringBuffer sb = new StringBuffer();
		
		for( byte b : salt ) {
			sb.append(String.format("%02x", b));
		}
		
		return sb.toString();
	}


}
