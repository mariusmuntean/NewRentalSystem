package de.tum.os.drs.client.helpers;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

import de.tum.os.drs.client.model.OAuthAuthorities;

public class CookieHelper {
	static final long oneDayInMillis = 1000L * 60L * 60L * 24L;

	/**
	 * Puts the email of an authenticated user in a cookie.
	 * 
	 * @param userEmail
	 *            - the user's email address
	 */
	public static void setAuthenticatedUserEmail(String userEmail) {
		if (userEmail == null && userEmail.length() <= 0) {
			return;
		}

		Cookies.setCookie("authenticatedUserEmail", userEmail,
				new Date(System.currentTimeMillis() + oneDayInMillis));
	}

	/**
	 * Deletes the cookie with the authenticated user's email. Call when authentication fails.
	 */
	public static void resetAuthenticatedUserEmail() {
		Cookies.setCookie("authenticatedUserEmail", "");
		Cookies.removeCookie("authenticatedUserEmail");
	}

	/**
	 * 
	 * @return - Returns a string with the user's email or an empty string if no authenticated user.
	 */
	public static String getAuthenticatedUserEmail() {
		String userEmail = Cookies.getCookie("authenticatedUserEmail");
		if (userEmail == null || userEmail.isEmpty()) {
			return "";
		}
		return userEmail;
	}

	/**
	 * Puts the name of an authenticated user in a cookie.
	 * 
	 * @param username
	 *            - the user's name
	 */
	public static void setAuthenticatedUsername(String username) {
		if (username == null && username.length() <= 0) {
			return;
		}
		Cookies.setCookie("authenticatedUserName", username,
				new Date(System.currentTimeMillis() + oneDayInMillis));
	}

	/**
	 * Deletes the cookie with the authenticated user name. Call when authentication fails.
	 */
	public static void resetAuthenticatedUsername() {
		Cookies.setCookie("authenticatedUserName", "");
		Cookies.removeCookie("authenticatedUserName");
	}

	/**
	 * 
	 * @return - Returns a string with the user name of the authenticated user or an empty string if no authenticated user.
	 */
	public static String getAuthenticatedUsername() {
		String username = Cookies.getCookie("authenticatedUserName");
		if (username == null || username.isEmpty()) {
			return "";
		}
		return username;
	}

	/**
	 * Sets a cookie with the ID of the authenticated user.
	 * 
	 * @param userID
	 */
	public static void setAuthenticatedUserID(String userID) {
		if (userID == null && userID.length() <= 0) {
			return;
		}
		Cookies.setCookie("authenticatedUserName", userID,
				new Date(System.currentTimeMillis() + oneDayInMillis));
	}

	/**
	 * Removes the cookie with the user ID.
	 */
	public static void resetAuthenticatedUserID() {
		Cookies.setCookie("authenticatedUserID", "");
		Cookies.removeCookie("authenticatedUserID");
	}

	/**
	 * 
	 * @return - Returns a string with the authenticated user's ID.
	 */
	public static String getAuthenticatedUserID() {
		String userID = Cookies.getCookie("authenticatedUserID");
		if (userID == null && userID.isEmpty()) {
			return "";
		}
		return userID;
	}

	/**
	 * Sets two cookies. One with the name of the authentication authority and the other with the access token.
	 * 
	 * @param authority
	 *            - The auth authority that produced this token.
	 * @param token
	 *            - The access token.
	 */
	public static void setAuthCookie(OAuthAuthorities authority, String token) {
		if (token == null && token.length() <= 0) {
			return;
		}

		Cookies.removeCookie("authenticatorName"); // Values can be: google, facebook, TUM or twitter
		Cookies.removeCookie("authenticatorToken");

		Cookies.setCookie("authenticatorName", authority.toString(),
				new Date(System.currentTimeMillis() + oneDayInMillis));
		Cookies.setCookie("authenticatorToken", token,
				new Date(System.currentTimeMillis() + oneDayInMillis));
	}

	/**
	 * 
	 * @return - Returns the authentication authority that authenticated the user last.
	 */
	public static OAuthAuthorities getOAuthAuthority() {
		String authenticatorName = Cookies.getCookie("authenticatorName");
		// Try to cast the string from the cookie to a known Authorization Authority.
		OAuthAuthorities authorityFromCookie;
		try {
			authorityFromCookie = OAuthAuthorities.valueOf(authenticatorName.trim());
		} catch (Exception e) {
			authorityFromCookie = OAuthAuthorities.none;
		}

		return authorityFromCookie;
	}

	public static void resetOAuthAuthority() {
		Cookies.setCookie("authenticatorName", "");
		Cookies.removeCookie("authenticatorName");
	}

	/**
	 * 
	 * @return - Returns the access token or an empty string if none found..
	 */
	public static String getAuthToken() {
		String token = Cookies.getCookie("authenticatorToken");
		if (token == null || token.length() <= 0) {
			return "";
		} else {
			return token;
		}
	}

	public static void resetAuthToken() {
		Cookies.setCookie("authenticatorToken", "");
		Cookies.removeCookie("authenticatorToken");
	}
}
