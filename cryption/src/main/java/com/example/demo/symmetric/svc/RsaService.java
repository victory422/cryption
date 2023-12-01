package com.example.demo.symmetric.svc;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface RsaService {
	
	/**
	 * private 키로 복호화를 한다.
	 */
	String decryptRSA(String encrypted, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;

	/**
	 * public 키로 암호화를 한다.
	 */
	String encryptRSA(String plainText, String keyName, int keySize) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;

	/**
	 * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
	 * @param keySize 
	 * @param keyName 
	 */
	PublicKey getPublicKey(String keyName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;

	/**
	 * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
	 */
	List<Map<String, String>> getKeyList(String searchStr, String bf)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;

	Map<String, String> keyDelete(String delKey);

	Map<String, String> keyUpdate(String keyName, int keySize) throws NoSuchAlgorithmException, IOException;

	Map<String, String> digitalSign(String plainText, String keyName, int keySize) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException;

	Map<String, String> digitalSignVeify(String plainText, String signText, String keyName)
			throws NoSuchPaddingException, InvalidKeySpecException, IOException;

	String keySave(String keyName, int keySize);

	String encryptRSA(String plainText, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;

	Map<String, String> digitalSign(String plainText, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException;

	/**
	 * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
	 */
	PrivateKey getPrivateKey(String keyName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;

}
