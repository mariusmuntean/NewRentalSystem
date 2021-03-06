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

	/**
	 * Determines if the JSON response contains an error.
	 * 
	 * @param json
	 *            - The JSON string to be checked for errors.
	 * @return - true if error present false otherwise.
	 */
	public static final boolean hasError(String json) {
		JSONValue val;
		JSONObject obj;
		try {
			val = JSONParser.parseStrict(json);
			obj = val.isObject();
		} catch (NullPointerException e) {
			return true;
		} catch (IllegalArgumentException e) {
			return true;
		}
		if (obj != null) {
			// if (obj.get("error")!=null && obj.get("error").isString() != null) {
			if (obj.get("error") != null) {
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

	public static String getAuthenticatedUsername(String json) {
		String username = "";

		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();
		if (obj != null && obj.get("name") != null && obj.get("name").isString() != null) {
			username = obj.get("name").isString().stringValue();
		}

		return username;

	}

	public static String getAuthenticatedUserEmail(String json) {
		String userEmail = "";

		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();
		if (obj != null && obj.get("email") != null
				&& obj.get("email").isString() != null) {
			userEmail = obj.get("email").isString().stringValue();
		}

		return userEmail;

	}

	public static String getAuthenticatedUserID(String json) {
		String userID = "";

		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();
		if (obj != null && obj.get("id") != null && obj.get("id").isString() != null) {
			userID = obj.get("id").isString().stringValue();
		}

		return userID;

	}

	public static String getAuthenticatedUserPictureUrl(String json) {
		String userPictureUrl = "";

		JSONValue val = JSONParser.parseStrict(json);
		JSONObject obj = val.isObject();

		// Google response
		if (obj != null && obj.get("picture") != null
				&& obj.get("picture").isString() != null) {
			userPictureUrl = obj.get("picture").isString().stringValue();
		}
		if (userPictureUrl.length() > 1) {
			return userPictureUrl;
		}

		// Facebook response
		userPictureUrl = "";
		String username = "";
		if (obj != null && obj.get("username") != null
				&& obj.get("username").isString() != null) {
			username = obj.get("username").isString().stringValue();
		}
		if (username.length() > 1) {
			userPictureUrl = "https://graph.facebook.com/" + username
					+ "/picture?type=large";
			return userPictureUrl;
		}
		
		

		return userPictureUrl;

	}

}
