package com.example.demo.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil2 {
	
	private static String path ;
	private static String folderPath ;
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil2.class);
	
	
	public static void main(String[] args) {
		
		folderPath = "c:/Users/jgn/Downloads/";
		path = folderPath + "Auth.postman_collection.json";
		
		Map<String,Object> map = new HashMap<String, Object>();
		JSONParser parser = new JSONParser();
		List<Map<String,Object>> items = new ArrayList<>();
		
		try {
			FileReader reader = new FileReader(path);
			String tmp = "";
			int chkStr = reader.read(); 
			if( chkStr == -1 ) {
			} else {
				char tt = (char)chkStr;
				int ch;
				while ((ch = reader.read()) != -1) {
					tmp = tmp + (char) ch;
				}
			}
			
			
			map = (Map<String, Object>) parser.parse((char)chkStr + tmp);
			items = (List<Map<String, Object>>) map.get("item");
			
			System.out.println(map);
			int i = 0;
			for( Map<String,Object> m : items ) {
				System.out.print(m.get("name"));
				System.out.print("\t");
				System.out.print(m.get("name"));
				System.out.print(m.get("name"));
				System.out.print(m.get("name"));
				
				i++;
				if( i > 0 ) break;
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		
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
		List<Map<String,String>> list = JsonUtil2.readJson();
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
		List<Map<String,String>> list = JsonUtil2.readJson();
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
