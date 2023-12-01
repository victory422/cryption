package com.example.demo.symmetric.ctl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.symmetric.svc.RsaService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/rsa")
public class RsaCTL {
	
    @Autowired
	private RsaService rsaService;

    @PostMapping("/keySave")
    public ApiResponse<Map<String, String>> keySave(
    		@RequestBody Map<String,String> params
    		) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    	String keyName = params.get("keyName");
    	int keySize = Integer.parseInt(params.get("keySize"));
        Map<String,String> rst = new HashMap<>();

        String rstCd = rsaService.keySave(keyName, keySize);
        
        rst.put("result", rstCd);
        rst.put("keyName", keyName);
        return new ApiResponse<Map<String, String>>(rst, HttpStatus.OK);
    }

    @GetMapping("keySearch")
	public ResponseEntity<List<Map<String,String>>> keySearch(
			@RequestParam(required = false) String searchStr,
			@RequestParam(required = false) String bf
			) throws Exception {
    	bf = "public";	// 퍼블릭키만 조회도도록 파라미터변조 방지
		return new ResponseEntity<List<Map<String, String>>>(rsaService.getKeyList(searchStr, bf), HttpStatus.OK);
	}
    
    @DeleteMapping("keyDelete/{delKey}")
	public ApiResponse<Map<String, String>> keyDelete(@PathVariable String delKey) throws Exception {
		return new ApiResponse<Map<String, String>>(rsaService.keyDelete(delKey), HttpStatus.OK);
	}
	
	@PutMapping("keyUpdate")
	public ApiResponse<Map<String,String>> keyUpdate(@RequestBody Map<String, Object> param) throws Exception {
				String updateKey = (String) param.get("updateKey");
				int keySize = Integer.parseInt((String) param.get("keySize"));
		return new ApiResponse<Map<String, String>>(rsaService.keyUpdate(updateKey, keySize), HttpStatus.OK);
	}
	
	
	@PostMapping("encrypt")
	public ApiResponse<Map<String, String>> encrypt(@RequestBody Map<String, String> params
			) throws Exception {
		String plainText = params.get("plainText");
		String keyName = params.get("keyName");
		Map<String, String> map = new HashMap<>();
		String rst = rsaService.encryptRSA(plainText,  keyName);
		map.put("result",rst);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}

	
	@PostMapping("decrypt")
	public ApiResponse<Map<String, String>> decrypt(@RequestBody Map<String, String> params 
			) throws Exception {
		Map<String, String> map = new HashMap<>();
		String encodenText = params.get("encodeText");
		String keyName = params.get("keyName");
		String rst = rsaService.decryptRSA(encodenText, keyName);
		map.put("result",rst);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}
	
	
	@PostMapping("digitalSign")
	public ApiResponse<Map<String, String>> digitalSign(@RequestBody Map<String, String> params 
			) throws Exception {
		String plainText = params.get("plainText");
		String keyName = params.get("keyName");
		Map<String, String> map = rsaService.digitalSign(plainText, keyName);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}
	
	
	@PostMapping("signVerification")
	public ApiResponse<Map<String, String>> signVerification(@RequestBody Map<String, String> params 
			) throws Exception {
		String plainText = params.get("plainText");
		String signText = params.get("signText");
		String keyName = params.get("keyName");
		Map<String, String> map = rsaService.digitalSignVeify(plainText, signText, keyName);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}
    
}
