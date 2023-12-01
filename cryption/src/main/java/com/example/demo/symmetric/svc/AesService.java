package com.example.demo.symmetric.svc;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import org.json.simple.parser.ParseException;

public interface AesService {

	String aesDecode(String key, String encodeText, String cipherCd, String encCd, String paramIv) throws Exception;

	String aesEncode(String key, String plainText, String cipherCd, String encCd, String paramIv) throws Exception;

	String keySave(Map<String, String> map) throws NumberFormatException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException;

	List<Map<String, String>> keyRead(String searchStr) throws ParseException;

	Map<String, String> keyDelete(String delKey) throws ParseException;

	Map<String, String> keyUpdate(String updateKey, int keySize) throws NoSuchAlgorithmException;

}