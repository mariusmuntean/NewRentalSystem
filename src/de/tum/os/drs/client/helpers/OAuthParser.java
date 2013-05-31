package de.tum.os.drs.client.helpers;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Helper class to retrieve data from OAuth response JSONs.
 * 
 * @author Marius
 * 
 */
public class OAuthParser {

	public static String getAuthenticatedUsername(String json){
		String username = "";
		
		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();
		if (obj != null && obj.get("name") != null && obj.get("name").isString()!=null) {
			username = obj.get("name").isString().stringValue();
		}
		
		
		return username;
		
	}
	
	
	/**
	 * Determines if the JSON response contains an error.
	 * 
	 * @param json - The JSON string to be checked for errors.
	 * @return - true if error present false otherwise.
	 */
	public static final boolean hasError(String json) {

		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();
		if (obj != null) {
//			if (obj.get("error")!=null && obj.get("error").isString() != null) {
			if (obj.get("error")!=null) {
				return true;
			} else {
				return false;
			}
		} else {
			if (val.isString() != null && val.isString().stringValue().contains("error")) {
				return true;
			} else {
				return false;
			}
		}
	}

}
