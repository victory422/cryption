package com.example.demo.symmetric.svc;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.dreamsecurity.jcaos.exception.AlgorithmException;
import com.dreamsecurity.jcaos.exception.ConfirmPasswordException;
import com.dreamsecurity.jcaos.exception.NoSuchModeException;
import com.dreamsecurity.jcaos.exception.ParsingException;

public interface RsaService {
	
	/**
	 * public 키로 암호화를 한다.
	 */
	String encryptRSA(String plainText, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException;
	
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

	Map<String, String> digitalSignVeify(String plainText, String signText, String keyName)
			throws NoSuchPaddingException, InvalidKeySpecException, IOException;

	

	/**
	 * 키를 새로 생성한다.
	 * @throws NoSuchModeException 
	 * @throws AlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 */
	String keySave(String keyName, int keySize, byte[] password) throws InvalidKeyException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, AlgorithmException, NoSuchModeException;

	Map<String, String> keyUpdate(String keyName, int keySize, byte[] password)
			throws NoSuchAlgorithmException, IOException;

	/**
	 * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
	 * @throws ConfirmPasswordException 
	 * @throws NoSuchModeException 
	 * @throws AlgorithmException 
	 * @throws ParsingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 */
	PrivateKey getPrivateKey(String keyName, byte[] password)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException;

	/**
	 * private 키로 복호화를 한다.
	 * @throws ConfirmPasswordException 
	 * @throws NoSuchModeException 
	 * @throws AlgorithmException 
	 * @throws ParsingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchProviderException 
	 */
	String decryptRSA(String encrypted, String keyName, byte[] password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException;

	Map<String, String> digitalSign(String plainText, String keyName, byte[] password)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException;

}
