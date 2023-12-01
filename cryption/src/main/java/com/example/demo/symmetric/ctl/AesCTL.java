package com.example.demo.symmetric.ctl;

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

import com.example.demo.symmetric.svc.AesService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/aes")
public class AesCTL {
	
	@Autowired(required=true)
	private AesService aesService;
	
	@PostMapping("encrypt")
	public ApiResponse<Map<String, String>> encrypt( @RequestBody Map<String, String> params
			) throws Exception {
		String key = params.get("key");
		String plainText = params.get("plainText");
		String cipherCd = params.get("cipherCd");
		String encCd = params.get("encCd");
		String paramIv  = params.get("paramIv");
		
		Map<String, String> map = new HashMap<>();
		String rst = aesService.aesEncode(key, plainText,  cipherCd, encCd, paramIv);
		map.put("result",rst);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}

	
	@PostMapping("decrypt")
	public ApiResponse<Map<String, String>> decrypt(@RequestBody Map<String, String> params 
			) throws Exception {
		Map<String, String> map = new HashMap<>();
		String key = params.get("key");
		String encodenText = params.get("encodeText");
		String cipherCd = params.get("cipherCd");
		String encCd = params.get("encCd");
		String paramIv = params.get("paramIv");
		String rst = aesService.aesDecode(key, encodenText,  cipherCd, encCd, paramIv);
		map.put("result",rst);
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}
	
	@PostMapping("keySave")
	public ApiResponse<Map<String, String>> keySave(@RequestBody Map<String, String> params 
			) throws Exception {
		Map<String, String> map = new HashMap<>();
		
		String rst = aesService.keySave(params);
		
		map.put("result",rst);
		
		return new ApiResponse<Map<String, String>>(map, HttpStatus.OK);
	}
	
	@GetMapping("keySearch")
	public ResponseEntity<List<Map<String,String>>> keySearch(@RequestParam(required = false) String searchStr) throws Exception {
		return new ResponseEntity<List<Map<String, String>>>(aesService.keyRead(searchStr), HttpStatus.OK);
	}
	
	@DeleteMapping("keyDelete/{delKey}")
	public ApiResponse<Map<String, String>> keyDelete(@PathVariable String delKey) throws Exception {
		return new ApiResponse<Map<String, String>>(aesService.keyDelete(delKey), HttpStatus.OK);
	}
	
	@PutMapping("keyUpdate/{updateKey}/keySize/{keySize}")
	public ApiResponse<Map<String, String>> keyUpdate(@PathVariable String updateKey, @PathVariable int keySize) throws Exception {
		return new ApiResponse<Map<String, String>>(aesService.keyUpdate(updateKey, keySize), HttpStatus.OK);
	}
	
	
	
}
