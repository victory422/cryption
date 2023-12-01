package com.example.demo.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {
	
	private static String path ;
	private static String folderPath ;
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	public JsonUtil() {
		Common cm;
		
		try {
			cm = new Common();
			folderPath = cm.getKeyVal("AESPATH");
			path = folderPath + cm.getKeyVal("AESFILENAME");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File folder = new File(folderPath);
        if (!folder.exists()){
            folder.mkdir();
        }
        
        LOGGER.info("AES FILE PATH : " + path);
	}
	
	public static JSONArray readJson() throws org.json.simple.parser.ParseException {
		
		JSONArray list = new JSONArray();
		JSONParser parser = new JSONParser();
		try {
			FileReader reader = new FileReader(path);
			String tmp = "";
			int chkStr = reader.read(); 
			if( chkStr == -1 ) {
				return null;
			} else {
				char tt = (char)chkStr;
				int ch;
				while ((ch = reader.read()) != -1) {
					tmp = tmp + (char) ch;
				}
			}
			
	        JSONArray arr = (JSONArray) parser.parse((char)chkStr + tmp);
			list = arr;
			
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	
	}
	
	public static void writeJson(Map<String,String> map) throws org.json.simple.parser.ParseException {
		
		JSONArray list = readJson();
		if( list == null ) list = new JSONArray();
		
		JSONObject obj = new JSONObject(map);
		
		list.add(obj);
		
		writeListJson(list);
		
	}
	
	public static void writeListJson(List<Map<String,String>> list) throws org.json.simple.parser.ParseException {
		
		try {
			FileWriter file = new FileWriter(path);
			file.write(list.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void deleteJson(String delKey) throws ParseException {
		List<Map<String,String>> list = JsonUtil.readJson();
		if( list == null ) list = new JSONArray();
		
		for( int i = 0 ; i < list.size(); i++ ) {
			if( delKey.equalsIgnoreCase(list.get(i).get("keyName")) ) {
				list.remove(i);
				break;
			}
		}
		writeListJson(list);
	}
	

	public static void updateJson(String updateKey, String newKey, int keySize) throws ParseException {
		List<Map<String,String>> list = JsonUtil.readJson();
		if( list == null ) list = new JSONArray();
		
		for( int i = 0 ; i < list.size(); i++ ) {
			if( updateKey.equalsIgnoreCase(list.get(i).get("keyName")) ) {
				list.get(i).put("key", newKey);
				list.get(i).put("keySize", Integer.toString(keySize) );
				break;
			}
		}
		
		writeListJson(list);		
	}
	
}
