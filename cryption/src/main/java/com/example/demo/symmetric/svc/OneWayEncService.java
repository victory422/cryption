package com.example.demo.symmetric.svc;

import java.util.Map;

public interface OneWayEncService {

	Map<String, String> oneWayEncrypt(String pwd, String string);

}
