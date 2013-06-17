package de.tum.os.drs.client.helpers;

import de.tum.os.drs.client.model.OAuthAuthorities;

public class OAuthApiHelper {

	public static final String googleAuthTokenCheckURL = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=";
	public static final String facebookAuthTokenCheckURL = "https://graph.facebook.com/me?access_token=";
	public static final String windowsliveAuthTokenCheckUrl = "https://apis.live.net/v5.0/me?access_token=";
	public static final String tumonlineAuthTokenCheckUrl = "https://campus.tum.de/tumonline/wbservicesbasic.isTokenConfirmed?pToken=";

	public static String getAuthUrlFromAuthority(OAuthAuthorities oAuthAuthority) {

		switch (oAuthAuthority) {
		case facebook: {
			return facebookAuthTokenCheckURL;
		}
		case google: {
			return googleAuthTokenCheckURL;
		}
		case windowslive: {
			return windowsliveAuthTokenCheckUrl;
		}
		case linkedin: {
			return null;
		}
		case tum: {
			return null;
		}
		case twitter: {
			return null;
		}
		default: {
			return null;
		}
		}
	}
}
