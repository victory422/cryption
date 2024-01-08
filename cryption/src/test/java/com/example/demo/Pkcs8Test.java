package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.dreamsecurity.jcaos.exception.AlgorithmException;
import com.dreamsecurity.jcaos.exception.ConfirmPasswordException;
import com.dreamsecurity.jcaos.exception.NoSuchModeException;
import com.dreamsecurity.jcaos.exception.ParsingException;
import com.dreamsecurity.jcaos.jce.provider.JCAOSProvider;
import com.dreamsecurity.jcaos.pkcs.PKCS8;
import com.dreamsecurity.jcaos.pkcs.PKCS8PrivateKeyInfo;
import com.dreamsecurity.jcaos.util.encoders.PEM;

public class Pkcs8Test {
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, AlgorithmException, NoSuchModeException, ParsingException, ConfirmPasswordException, InvalidKeySpecException {
		JCAOSProvider.installProvider();
		chk();

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024);
		KeyPair keyPair = keyPairGenerator.genKeyPair();

		PrivateKey pri = keyPair.getPrivate();

		PKCS8PrivateKeyInfo pkcs8Info = new PKCS8PrivateKeyInfo(pri);
		PKCS8 pkcs8 = new PKCS8("123".getBytes());
		byte[] priEnc = pkcs8.encrypt(pkcs8Info);
		pkcs8.decrypt(priEnc);
		
		PKCS8 pkcs8d = new PKCS8("1234".getBytes());
		PKCS8PrivateKeyInfo info = pkcs8.decrypt(priEnc);
		
		PrivateKey prid = info.getPrivateKey();
		
		byte[] priPem = PEM.encode(PEM.TYPE_ENC_PRIKEY, priEnc);
		System.out.println(new String(priPem));
	}
	
	
	public static void chk() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException {
        byte[] bytes = Files.readAllBytes(Paths.get("C:/Users/jgn/documents/rsa/123/private.pem"));
        PKCS8 pkcs8 = new PKCS8("123".getBytes());
        PKCS8PrivateKeyInfo info = pkcs8.decrypt(PEM.decode(bytes));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(info.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey pk = keyFactory.generatePrivate(spec);
	}
	
}
