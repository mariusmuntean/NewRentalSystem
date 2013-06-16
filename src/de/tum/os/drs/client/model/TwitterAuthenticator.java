package de.tum.os.drs.client.model;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

public class TwitterAuthenticator implements IAuthenticator {

	/*
	 * OAuth2 stuff
	 */
	String AUTH_URL = "https://api.twitter.com/oauth/request_token";
	String CLIENT_ID = "Tl3HC2nRf1Xda8NdzDJNaA";
	String SCOPE1 = "caca";

	// AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(GOOGLE_EMAIL);
	AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(SCOPE1);

	Callback<Tuple<String, OAuthAuthorities>, Throwable> callback;

	public TwitterAuthenticator(
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
					TwitterAuthenticator.this.callback
							.onSuccess(new Tuple<String, OAuthAuthorities>(result,
									OAuthAuthorities.twitter));

				}

				@Override
				public void onFailure(Throwable reason) {
					TwitterAuthenticator.this.callback.onFailure(reason);

				}
			};
			auth.get().login(req, simpleCallback);
		}

	}

}
