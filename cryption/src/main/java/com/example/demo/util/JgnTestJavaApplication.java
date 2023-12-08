package com.example.demo.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JgnTestJavaApplication {
	
	private static String path ;
	private static String folderPath ;
	private static final Logger LOGGER = LoggerFactory.getLogger(JgnTestJavaApplication.class);
	
	public static void main(String[] args) {
		System.out.println(getCPUProcess());
		System.out.println(getCPUsage());
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
		
		int a = 10;
		
		 KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128); // 128-bit key size
			System.out.println(keyGen.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
		//String j = String.format("%x", "13");
		

		
		
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
        	for(String t : errorList) {
        		System.out.println(t.toString());
        	}
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
		List<Map<String,String>> list = JgnTestJavaApplication.readJson();
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
		List<Map<String,String>> list = JgnTestJavaApplication.readJson();
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
	
	
	public static int getCPUProcess() {
	      OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

	        // Get the system load average over the last minute
	        double loadAverage = osBean.getSystemLoadAverage();
	        // Get the number of available processors
	        int availableProcessors = osBean.getAvailableProcessors();

	        // Calculate CPU usage as a percentage
	        double cpuUsage = (loadAverage / availableProcessors) * 100;
		return (int)cpuUsage ;
	}
	
	
    private static double getCPUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        // Check if the method is supported (not all JVMs provide this information)
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            // Returns the "recent cpu usage" for the whole system
            double cpuUsage = sunOsBean.getSystemCpuLoad() * 100.0;

            return cpuUsage;
        } else {
            System.out.println("CPU usage monitoring is not supported on this platform.");
            return -1.0;
        }
    }
	
}
