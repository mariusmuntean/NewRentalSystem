package de.tum.os.drs.client.model;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;

/**
 * 
 * Initiates an OAuth2 authentication process with Windows Live.
 * 
 * @author Marius
 * 
 */
public class LiveAuthenticator implements IAuthenticator {

	/*
	 * OAuth2 stuff
	 */
	String AUTH_URL = "https://login.live.com/oauth20_authorize.srf";
	String CLIENT_ID = "000000004C0F5E29";
	String LIVE_USERINFO = "wl.basic";
	String LIVE_PHOTOS = "wl.photos";

	AuthRequest req = new AuthRequest(AUTH_URL, CLIENT_ID).withScopes(LIVE_USERINFO, LIVE_PHOTOS);

	Callback<Tuple<String, OAuthAuthorities>, Throwable> callback;

	public LiveAuthenticator(
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
					LiveAuthenticator.this.callback
							.onSuccess(new Tuple<String, OAuthAuthorities>(result,
									OAuthAuthorities.windowslive));

				}

				@Override
				public void onFailure(Throwable reason) {
					LiveAuthenticator.this.callback.onFailure(reason);

				}
			};
			auth.get().login(req, simpleCallback);
		}

	}
}

