package de.tum.os.drs.client.model;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

public class FacebookAuthenticator implements IAuthenticator {

	/*
	 * OAuth2 stuff
	 */
	String AUTH_URL = "https://www.facebook.com/dialog/oauth";
	String CLIENT_ID = "445564562207916"; // Available in the Facebook developers dashboard: https://developers.facebook.com/apps/445564562207916/summary
	String FB_EMAIL_SCOPE = "email";
	String FB_ABOUTME_SCOPE = "user_about_me";
	String FB_PICTURE_SCOPE = "picture";

	AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(FB_EMAIL_SCOPE,
			FB_ABOUTME_SCOPE);

	Callback<Tuple<String, OAuthAuthorities>, Throwable> callback;

	public FacebookAuthenticator(
			Callback<Tuple<String, OAuthAuthorities>, Throwable> callback) {
		this.callback = callback;
	}

	@Override
	public void login() {
		Auth auth = Auth.get();
		// auth.setOAuthWindowUrl("http://localhost:8888/NewRentalSystem.html?gwt.codesvr=127.0.0.1:9997");
		if (this.callback != null) {
			Callback<String, Throwable> simpleCallback = new Callback<String, Throwable>() {

				@Override
				public void onSuccess(String result) {
					FacebookAuthenticator.this.callback
							.onSuccess(new Tuple<String, OAuthAuthorities>(result,
									OAuthAuthorities.facebook));

				}

				@Override
				public void onFailure(Throwable reason) {
					FacebookAuthenticator.this.callback.onFailure(reason);

				}
			};
			auth.get().login(req, simpleCallback);
		}

	}
}
