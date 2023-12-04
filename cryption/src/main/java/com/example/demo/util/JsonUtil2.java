package com.example.demo.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
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
		TestVO member = new TestVO();
		
		member.setBool(false);
		member.setStr("test1");
		member.setStr2("test2");
		member.setStr3("test3");
		member.setStr4("test4");
		member.setInt1(111);
		member.setInt2(222);
		member.setInt3(333);
		
		List<String> list = new ArrayList<>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		
		member.setLst(list);
		Map<String,String> map = new HashMap<>();
		
		map.put("a","1");
		map.put("b","2");
		map.put("c","3");
		member.setMap(map);
		
		getToString(member);
		
	}
	
	
	public static void getToString(Object member ) {
		List<String> errorList = new ArrayList<>();
			
	   Field[] memberList = member.getClass().getDeclaredFields();        
        for(Field mem : memberList){
            mem.setAccessible(true); // private 도 접근 가능하도록 설정
            try {
            	if( mem.get(member) != null ) {
            		System.out.print("[" + mem.get(member).getClass().getName() + "] ");
            		System.out.print(mem.getName() + " : ");
            		System.out.println(mem.get(member));
            		/*
            		if( mem.get(member).getClass().equals(String.class) ) {
            			System.out.println(mem.get(member));
            		} else if ( mem.get(member).getClass().equals(Integer.class) ) {
            			System.out.println(mem.get(member));
            		} else if ( mem.get(member).getClass().equals(Boolean.class) ) {
            			System.out.println(mem.get(member));
            		} else if ( mem.get(member).getClass().equals(ArrayList.class) ) {
            			System.out.println(((ArrayList)mem.get(member)));
            		} else if ( mem.get(member).getClass().equals(HashMap.class) ) {
            			System.out.println(((HashMap)mem.get(member)));
            		} else {
            			System.out.println(mem.get(member));
            		}
            		*/
            	}
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				try {
					errorList.add(mem.get(member).getClass().getName());
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
        }
        
        if( errorList.size() > 0 ) {
        	System.out.println(errorList.toString());
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
