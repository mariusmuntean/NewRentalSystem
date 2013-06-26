package de.tum.os.drs.server.helpers;

import java.util.ArrayList;
import java.util.Map;

import com.json.parsers.JsonParserFactory;
import com.json.parsers.JSONParser;

public class JSONHelper {

	static JsonParserFactory factory = JsonParserFactory.getInstance();
	static JSONParser parser = factory.newJsonParser();

	public static Boolean hasError(String jsonString) {
		if (jsonString == null || jsonString.isEmpty())
			return true;

		Map jsonData = parser.parseJson(jsonString);
		// Map rootMap = (Map) jsonData.get("root");

		ArrayList<Map> maps = new ArrayList<Map>();
		maps.add(jsonData);
		while (!maps.isEmpty()) {
			Map currentMap = maps.remove(0);
			for (Object currentVal : currentMap.values()) {
				if (currentVal instanceof Map) {
					maps.add((Map) currentVal);
				} else {
					if (((String) currentVal).toLowerCase().trim().equals("error")) {
						return true;
					}
				}
			}
		}

		// Map addressMap = rootMap.get("address");
		// String pcode = addressMap.get("postalCode");
		return false;
	}

	public static String getUserId(String jsonString) {
		if (jsonString == null || jsonString.isEmpty())
			return "";

		Map jsonData = parser.parseJson(jsonString);
		// Map rootMap = (Map) jsonData.get("root");

		ArrayList<Map> maps = new ArrayList<Map>();
		maps.add(jsonData);
		while (!maps.isEmpty()) {
			Map currentMap = maps.remove(0);
			for (Object currentKey : currentMap.keySet()) {
				if (currentMap.get(currentKey) instanceof Map) {
					maps.add((Map) currentMap.get(currentKey));
				} else {
					if (currentMap.get(currentKey) instanceof String) {
						String keyName = (String) currentKey;
						if (keyName.equals("id") || keyName.equals("id:")) {
							String val = ((String) currentMap.get(currentKey))
									.toLowerCase().trim();
							return val;
						}
					}
				}
			}
		}

		// Map addressMap = rootMap.get("address");
		// String pcode = addressMap.get("postalCode");
		return "";
	}
	
	public static String getUserEmail(String jsonString) {
		if (jsonString == null || jsonString.isEmpty())
			return "";

		Map jsonData = parser.parseJson(jsonString);
		// Map rootMap = (Map) jsonData.get("root");

		ArrayList<Map> maps = new ArrayList<Map>();
		maps.add(jsonData);
		while (!maps.isEmpty()) {
			Map currentMap = maps.remove(0);
			for (Object currentKey : currentMap.keySet()) {
				if (currentMap.get(currentKey) instanceof Map) {
					maps.add((Map) currentMap.get(currentKey));
				} else {
					if (currentMap.get(currentKey) instanceof String) {
						String keyName = (String) currentKey;
						if (keyName.equals("email")) {
							String val = ((String) currentMap.get(currentKey))
									.toLowerCase().trim();
							return val;
						}
					}
				}
			}
		}

		// Map addressMap = rootMap.get("address");
		// String pcode = addressMap.get("postalCode");
		return "";
	}

}
