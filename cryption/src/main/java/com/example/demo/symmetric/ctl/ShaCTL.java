package com.example.demo.symmetric.ctl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.symmetric.svc.OneWayEncService;
import com.example.demo.util.ApiResponse;

@RestController
@RequestMapping("/sha")
public class ShaCTL {
	
    @Autowired
	private OneWayEncService oneWayEncService;

    @PostMapping("/oneWayEncrypt")
    public ApiResponse<Map<String, String>> oneWayEncrypt(
    		@RequestBody Map<String,String> params
    		) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return new ApiResponse<Map<String, String>>(oneWayEncService.oneWayEncrypt(params.get("pwd") , params.get("bfCd")), HttpStatus.OK);
    }
}
