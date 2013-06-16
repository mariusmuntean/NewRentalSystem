package de.tum.os.drs.client.model;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

/**
 * 
 * Initiates an OAuth2 authentication process with Google.
 * 
 * @author Marius
 * 
 */
public class GoogleAuthenticator implements IAuthenticator {

	/*
	 * OAuth2 stuff
	 */
	String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
	String CLIENT_ID = "717487426781.apps.googleusercontent.com"; // available from the APIs console
	String GOOGLE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
	String GOOGLE_USERINFO = "https://www.googleapis.com/auth/userinfo.profile";

	AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(GOOGLE_EMAIL,
			GOOGLE_USERINFO);

	Callback<Tuple<String, OAuthAuthorities>, Throwable> callback;

	public GoogleAuthenticator(
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
					GoogleAuthenticator.this.callback
							.onSuccess(new Tuple<String, OAuthAuthorities>(result,
									OAuthAuthorities.google));

				}

				@Override
				public void onFailure(Throwable reason) {
					GoogleAuthenticator.this.callback.onFailure(reason);

				}
			};
			auth.get().login(req, simpleCallback);
		}

	}
}
