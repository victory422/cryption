package com.example.demo.symmetric.svc.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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

import com.example.demo.symmetric.exception.NoSuchDataException;
import com.example.demo.symmetric.svc.RsaService;
import com.example.demo.util.Common;


@Service
public class RsaServiceImpl implements RsaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaServiceImpl.class);

    private String path ;
    private final String algorithm = "RSA";
    
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
    
    public void afterPropertiesSet(String path, String keyName, int keySize) throws NoSuchAlgorithmException, IOException {
    	if (!keyFileCheck(keyName)) {
        	createKeyFile(keyName, keySize);
        }else{
            LOGGER.info("RSA 키가 존재하여 기존 키를 활용합니다.");
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
     */
    private void createKeyFile(String keyName, int keySize) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(this.algorithm);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        Key[] keys = new Key[] { keyPair.getPublic(), keyPair.getPrivate() };
        FileOutputStream fos = null;
        try {
            File folder = new File(this.path+keyName);
            if (!folder.exists()){
                folder.mkdir();
            }
            
            File[] files = folder.listFiles();
            for (File f : files) {
                f.delete();
            }
            for (Key key : keys) {
                String path = null;
                if (key.equals(keyPair.getPublic())) {
                    path = this.path + keyName + "/public.pem";
                } else {
                    path = this.path + keyName + "/private.pem";
                }
                File file = new File(path);
                fos = new FileOutputStream(file);
                fos.write(key.getEncoded());
                LOGGER.info("RSA 키를 새로 생성하였습니다.");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
                fos.flush();
            }
        }
    }

    /**
     * 키 파일을 읽어 리턴하는 메소드, 없을 경우 새로 생성한다.
     */
    @Override
    public PrivateKey getPrivateKey(String keyName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!keyFileCheck(keyName)) {
            //createKeyFile(keyName, keySize);
            throw new NoSuchDataException("키를 찾을 수 없습니다.");
        }
        byte[] bytes = Files.readAllBytes(Paths.get(this.path + keyName + "/private.pem"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
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
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
        PublicKey pk = keyFactory.generatePublic(spec);
        return pk;
    }
    
    /**
     * 키를 새로 생성한다.
     */
    @Override
    public String keySave(String keyName, int keySize) {
    	
    	if (!keyFileCheck(keyName)) {
    		try {
				createKeyFile(keyName, keySize);
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
    	LOGGER.info(searchStr);
    	LOGGER.info(bf);
    	File files = new File(this.path);
    	List<String> paths = new ArrayList<>();
    	for( String i : files.list() ) {
    		if( i.indexOf(searchStr) > -1 ) {
    			paths.add(i);
    		}
    	}
    	for( String keyName : paths ) {
    		byte[] bytes = Files.readAllBytes(Paths.get(this.path + keyName + "/" + bf.toLowerCase() + ".pem"));
    		X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
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
     */
    @Override
    public String decryptRSA(String encrypted, String keyName) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        PrivateKey privateKey = getPrivateKey(keyName);
        Cipher cipher = Cipher.getInstance(algorithm);
        byte[] byteEncrypted = Base64.getDecoder().decode(encrypted.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String decrypted = new String(cipher.doFinal(byteEncrypted), "utf-8");
        return decrypted;

    }

	@Override
	public Map<String, String> keyUpdate(String keyName, int keySize) throws NoSuchAlgorithmException, IOException {
		Map<String, String> rst = new HashMap<>();
		try {
			createKeyFile(keyName, keySize);
			rst.put("result", "S");
		} catch (Exception e) {
			rst.put("result", "E");
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
	public Map<String, String> digitalSign(String plainText, String keyName) 
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		Map<String, String> rst = new HashMap<>();
		
		PrivateKey prKey = getPrivateKey(keyName);
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

	@Override
	public String encryptRSA(String plainText, String keyName, int keySize)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> digitalSign(String plainText, String keyName, int keySize)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
}