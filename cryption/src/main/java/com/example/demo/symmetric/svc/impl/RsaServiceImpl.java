package com.example.demo.symmetric.svc.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dreamsecurity.jcaos.exception.AlgorithmException;
import com.dreamsecurity.jcaos.exception.ConfirmPasswordException;
import com.dreamsecurity.jcaos.exception.NoSuchModeException;
import com.dreamsecurity.jcaos.exception.ParsingException;
import com.dreamsecurity.jcaos.jce.provider.JCAOSProvider;
import com.dreamsecurity.jcaos.pkcs.PKCS8;
import com.dreamsecurity.jcaos.pkcs.PKCS8PrivateKeyInfo;
import com.dreamsecurity.jcaos.util.encoders.PEM;
import com.example.demo.symmetric.exception.NoSuchDataException;
import com.example.demo.symmetric.svc.RsaService;
import com.example.demo.util.Common;


@Service
public class RsaServiceImpl implements RsaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);

    private String path ;
    private final String algorithm = "RSA";
    
    static {
		JCAOSProvider.installProvider();	
	}
    
    RsaServiceImpl() {
    	Common cm;
		try {
			cm = new Common();
			this.path = cm.getKeyVal("RSAPATH");
			LOGGER.info("RSA PATH : " + this.path);
			File folder = new File(this.path);
			if (!folder.exists()){
				folder.mkdir();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 키 파일이나 폴더가 존재하는지 체크하는 메소드
     */
    private boolean keyFileCheck(String keyName) {
        File folder = new File(this.path);
        if (!folder.exists()) {
            return false;
        } else {
            String[] files = new String[] { this.path + keyName + "/public.pem", this.path + keyName + "/private.pem" };
            for (String f : files) {
                File file = new File(f);
                if (!file.exists())
                    return false;
            }
        }
        return true;
    }

    /**
     * 키 파일을 생성하는 메소드, 무조건 파일을 모두 새로 생성한다.
     * @throws InvalidKeySpecException 
     * @throws NoSuchProviderException 
     * @throws NoSuchModeException 
     * @throws AlgorithmException 
     * @throws InvalidAlgorithmParameterException 
     * @throws IllegalBlockSizeException 
     * @throws BadPaddingException 
     * @throws NoSuchPaddingException 
     * @throws InvalidKeyException 
     */
    private void createKeyFile(String keyName, int keySize, byte[] password) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, AlgorithmException, NoSuchModeException {
    	
    	KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.algorithm);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        Key[] keys = new Key[] { keyPair.getPublic(), keyPair.getPrivate() };
        FileOutputStream fos = null;
        try {
            File folder = new File(this.path+keyName);
            if ( !folder.exists() ){
                folder.mkdir();
            } else {
            	File[] files = folder.listFiles();
            	for (File f : files) {
            		System.out.println(f.getName());
            		f.delete();
            	}
            }
            
            
            
            
            for (Key key : keys) {
                String path = null;
                if (key.equals(keyPair.getPrivate())) {
                	path = this.path + keyName + "/private.pem";
                	File file = new File(path);
                	fos = new FileOutputStream(file);
                	PKCS8PrivateKeyInfo pkcs8KeyInfo = new PKCS8PrivateKeyInfo(keyPair.getPrivate());
                	PKCS8 pkcs8 = new PKCS8( password );
                	byte[] encData = PEM.encode(PEM.TYPE_PRIKEY_INFO, pkcs8.encrypt(pkcs8KeyInfo));
                	LOGGER.info("RSA 키[private]를 새로 생성하였습니다.");
                	fos.write(encData);
                } else {
                	path = this.path + keyName + "/public.pem";
                	File file = new File(path);
                	fos = new FileOutputStream(file);
                	byte[] encData = PEM.encode(PEM.TYPE_X509_CERT, keyPair.getPublic().getEncoded());
                	fos.write(encData);
                	LOGGER.info("RSA 키[public]를 새로 생성하였습니다.");
                }
                
            }
        } catch (IOException e) {
        	e.printStackTrace();
    	} finally {
	        if (fos != null) {
	            fos.close();
	            fos.flush();
	        }
        }
    }

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
    @Override
    public PrivateKey getPrivateKey(String keyName, byte[] password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException {
        if (!keyFileCheck(keyName)) {
            //createKeyFile(keyName, keySize);
            throw new NoSuchDataException("키를 찾을 수 없습니다.");
        }
        byte[] bytes = Files.readAllBytes(Paths.get(this.path + keyName + "/private.pem"));
//        PKCS8 pkcs8 = new PKCS8(password);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(PEM.decode(bytes));
//        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
//        PrivateKey pk = keyFactory.generatePrivate(spec);
        PKCS8 pkcs8 = new PKCS8(password);
        PKCS8PrivateKeyInfo info = pkcs8.decrypt(PEM.decode(bytes));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(info.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey pk = keyFactory.generatePrivate(spec);
        return pk;
    }

    /**
     * 키 파일을 읽어 리턴하는 메소드
     */
    @Override
    public PublicKey getPublicKey(String keyName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!keyFileCheck(keyName)) {
        	// createKeyFile(keyName, keySize);
        	throw new NoSuchDataException("키를 찾을 수 없습니다.");
        	
        }
        byte[] bytes = Files.readAllBytes(Paths.get(this.path + keyName + "/public.pem"));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(PEM.decode(bytes));
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        PublicKey pk = keyFactory.generatePublic(spec);
        return pk;
    }
    
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
    @Override
    public String keySave(String keyName, int keySize, byte[] password) throws InvalidKeyException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, AlgorithmException, NoSuchModeException {
    	
    	if (!keyFileCheck(keyName)) {
    		try {
				createKeyFile(keyName, keySize, password);
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
				return "E";
			}
    		return "S";
    	} else {
    		return "E";
    	}
    }
    
    /**
     * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
     */
    @Override
    public List<Map<String,String>> getKeyList(String searchStr, String bf) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    	List<Map<String,String>> rstList = new ArrayList<>();
    	File files = new File(this.path);
    	List<String> paths = new ArrayList<>();
    	for( String i : files.list() ) {
    		if( i.indexOf(searchStr) > -1 ) {
    			paths.add(i);
    		}
    	}
    	for( String keyName : paths ) {
    		byte[] bytes = Files.readAllBytes(Paths.get(this.path + keyName + "/" + bf.toLowerCase() + ".pem"));
    		X509EncodedKeySpec spec = new X509EncodedKeySpec(PEM.decode(bytes));
    		KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
    		PublicKey pk = keyFactory.generatePublic(spec);
    		String strKey = Base64.getEncoder().encodeToString(pk.getEncoded());
    		
    		Map<String,String> rstMap = new HashMap<>();
    		rstMap.put("keyName", keyName);
    		rstMap.put("key", strKey);
    		rstList.add(rstMap);
    		
    	}
    	
        return rstList;
    }
    

    /**
     * public 키로 암호화를 한다.
     */
    @Override
    public String encryptRSA(String plainText, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        PublicKey publicKey = getPublicKey(keyName);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        return encrypted;
    }

    /**
     * private 키로 복호화를 한다.
     * @throws ConfirmPasswordException 
     * @throws NoSuchModeException 
     * @throws AlgorithmException 
     * @throws ParsingException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchProviderException 
     */
    @Override
    public String decryptRSA(String encrypted, String keyName, byte[] password) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException{
        PrivateKey privateKey = getPrivateKey(keyName, password);
        Cipher cipher = Cipher.getInstance(algorithm);
        byte[] byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String decrypted = new String(cipher.doFinal(byteEncrypted), "utf-8");
        return decrypted;

    }

	@Override
	public Map<String, String> keyUpdate(String keyName, int keySize, byte[] password) throws NoSuchAlgorithmException, IOException {
		Map<String, String> rst = new HashMap<>();
		try {
			getPrivateKey(keyName, password);
			createKeyFile(keyName, keySize, password);
			rst.put("result", "S");
		} catch (Exception e) {
			rst.put("result", "E");
			rst.put("resultMessage", e.getMessage());
			LOGGER.info(e.getMessage());
		}
		return rst;
	}

	@Override
	public Map<String, String> keyDelete(String delKey) {
	    String path = this.path + delKey;
	    File file = new File(path);
	    
	    Map<String, String> rst = new HashMap<>();
	    try {
	    	File[] fileList = file.listFiles();
	    	for (int j = 0; j < fileList.length; j++) {
	    		fileList[j].delete(); //파일 삭제 
			}
	    	file.delete();
	    	rst.put("result", "S");
		} catch (Exception e) {
			rst.put("result", "E");
		}
	    
	    return rst;
	    
	}
	
	@Override
	public Map<String, String> digitalSign(String plainText, String keyName, byte[] password) 
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, ParsingException, AlgorithmException, NoSuchModeException, ConfirmPasswordException {
		Map<String, String> rst = new HashMap<>();
		
		PrivateKey prKey = getPrivateKey(keyName, password);
		try {
			// sign
			Signature rsa = Signature.getInstance("SHA1withRSA"); 
			rsa.initSign(prKey);
			rsa.update(plainText.getBytes());
			byte[] ds = rsa.sign();
			
			String dsBase64 = Base64.getEncoder().encodeToString(ds) ;
			
			LOGGER.info("signature:"+dsBase64);
			rst.put("result", "S");
			rst.put("sign", dsBase64);

		} catch (NoSuchAlgorithmException e) {
			rst.put("result", "E");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			rst.put("result", "E");
			e.printStackTrace();
		} catch (SignatureException e) {
			rst.put("result", "E");
			e.printStackTrace();
		}
		
		return rst;
	}
	
	@Override
	public Map<String, String> digitalSignVeify(String plainText, String signText, String keyName) 
			throws NoSuchPaddingException, InvalidKeySpecException, IOException {
		Map<String, String> rst = new HashMap<>();
		
		try {
			PublicKey pbKey = getPublicKey(keyName);
			Signature rsa = Signature.getInstance("SHA1withRSA"); 
			
			rsa.initVerify(pbKey);
			rsa.update(plainText.getBytes());
			
			boolean bret = rsa.verify(Base64.getDecoder().decode(signText));
			LOGGER.info("verify:"+bret);
			if ( bret ) {
				rst.put("result", "T");
			} else {
				rst.put("result", "F");
			}
		} catch (NoSuchAlgorithmException e) {
			rst.put("result", "E");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			rst.put("result", "E");
			e.printStackTrace();
		} catch (SignatureException e) {
			rst.put("result", "E");
			e.printStackTrace();
		}
		
		return rst;
	}
	
}