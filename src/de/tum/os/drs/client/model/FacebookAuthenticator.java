package de.tum.os.drs.client.model;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

public class FacebookAuthenticator implements IAuthenticator {

	/*
	 * OAuth2 stuff
	 */
	String AUTH_URL = "https://www.facebook.com/dialog/oauth";
	String CLIENT_ID = "384544281662712";
	String FB_EMAIL_SCOPE = "email";
	String FB_ABOUTME_SCOPE = "user_about_me";
	String FB_PICTURE_SCOPE = "picture";

	AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(FB_EMAIL_SCOPE,
			FB_ABOUTME_SCOPE);

	Callback<String, Throwable> callback;

	public FacebookAuthenticator(Callback<String, Throwable> callback) {
		this.callback = callback;
	}

	@Override
	public void login() {
		Auth auth = Auth.get();
		// auth.setOAuthWindowUrl("http://localhost:8888/NewRentalSystem.html?gwt.codesvr=127.0.0.1:9997");
		if (this.callback != null) {
			auth.get().login(req, this.callback);
		}

	}
}
