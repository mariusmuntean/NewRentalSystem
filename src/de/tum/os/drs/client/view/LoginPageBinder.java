package de.tum.os.drs.client.view;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.helpers.OAuthApiHelper;
import de.tum.os.drs.client.helpers.OAuthParser;
import de.tum.os.drs.client.model.FacebookAuthenticator;
import de.tum.os.drs.client.model.GoogleAuthenticator;
import de.tum.os.drs.client.model.IAuthenticator;
import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.TwitterAuthenticator;

public class LoginPageBinder extends Composite implements HasText {

	private static LoginPageBinderUiBinder uiBinder = GWT
			.create(LoginPageBinderUiBinder.class);
	@UiField
	Button btnLoginFacebook;
	@UiField
	Button btnLoginGoogle;
	@UiField
	Button btnLoginTwitter;
	@UiField
	Button btnLoginTUM;

	DialogBox modalLoader;

	/*
	 * Callbacks
	 */
	Callback<String, Throwable> fbCallback;
	Callback<String, Throwable> ggCallback;
	Callback<String, Throwable> twCallback;

	interface LoginPageBinderUiBinder extends UiBinder<Widget, LoginPageBinder> {
	}

	IAuthenticator googleAuthenticator, facebookAuthenticator, twitterAuthenticator;

	public LoginPageBinder(Callback<String, Throwable> facebookCallback,
			Callback<String, Throwable> googleCallback,
			Callback<String, Throwable> twitterCallback) {

		instantiateControls();
		initWidget(uiBinder.createAndBindUi(this));

		this.ggCallback = googleCallback;
		this.fbCallback = facebookCallback;
		this.twCallback = twitterCallback;

		showModalLoader();
		authenticateIfValidTokenFound();
	}

	private void enableLoginScreenIfNotAuthenticated() {
		this.googleAuthenticator = new GoogleAuthenticator(ggCallback);
		this.facebookAuthenticator = new FacebookAuthenticator(fbCallback);
		this.twitterAuthenticator = new TwitterAuthenticator(twCallback);

		wireUpControls();
		hideModalLoader();

	}

	private void authenticateIfValidTokenFound() {

		// Values can be: google, facebook, TUM, twitter or none.
		OAuthAuthorities authenticator = CookieHelper.getOAuthAuthority();
		if (authenticator == OAuthAuthorities.none) {
			enableLoginScreenIfNotAuthenticated();
			return;
		}

		String token = CookieHelper.getAuthToken();
		if (token == null || token.length() <= 0) {
			enableLoginScreenIfNotAuthenticated();
			return;
		}

		checkTokenValidity(token, authenticator);
	}

	private void checkTokenValidity(final String token, OAuthAuthorities oAuthAuthority) {
		String url = OAuthApiHelper.getAuthUrlFromAuthority(oAuthAuthority);
		final Callback<String, Throwable> callback = getCallbackFromAuthrity(oAuthAuthority);

		// Leave if any problem occurs. Don't bother the user.
		if (url == null || url.length() <= 0 || callback == null) {
			enableLoginScreenIfNotAuthenticated();
			return;
		}

		// Append token
		url += token;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		try {
			// Check token validity
			Request req = rb.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					// Window.alert("Response: " + response.getText());
					String jsonString = response.getText();
					if (OAuthParser.hasError(jsonString)) {
						enableLoginScreenIfNotAuthenticated();
					} else {
						// Hide the modal loader
						hideModalLoader();

						// Put user name and ID in cookies
						String username = OAuthParser
								.getAuthenticatedUsername(jsonString);
						CookieHelper.setAuthenticatedUsername(username);
						callback.onSuccess(token);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					Window.alert("An error occured: " + request.toString());
					enableLoginScreenIfNotAuthenticated();

				}
			});
		} catch (Exception e) {

		}

	}

	private Callback<String, Throwable> getCallbackFromAuthrity(
			OAuthAuthorities oAuthAuthority) {
		switch (oAuthAuthority) {
		case facebook: {
			return fbCallback;
		}
		case google: {
			return ggCallback;
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

	private void instantiateControls() {
		// Init modal loader wheel
		this.modalLoader = new DialogBox();
		VerticalPanel dialogContent = new VerticalPanel();
		Image imgLoader = new Image("../../../../../../Resources/Images/loader.gif");
		Label lblModalText = new Label("Trying to authenticate you.");
		dialogContent.add(lblModalText);
		dialogContent.add(imgLoader);
		dialogContent.setCellHorizontalAlignment(lblModalText,
				HasHorizontalAlignment.ALIGN_CENTER);
		dialogContent.setCellHorizontalAlignment(imgLoader,
				HasHorizontalAlignment.ALIGN_CENTER);
		this.modalLoader.setWidget(dialogContent);
		this.modalLoader.setAnimationEnabled(true);
		this.modalLoader.setGlassEnabled(true);
		this.modalLoader.setModal(true);

	}

	private void showModalLoader() {
		if (this.modalLoader != null) {
			this.modalLoader.center();
			this.modalLoader.show();
		}

	}

	private void hideModalLoader() {
		if (this.modalLoader != null)
			this.modalLoader.hide();
	}

	private void wireUpControls() {
		btnLoginGoogle.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (googleAuthenticator != null) {
					googleAuthenticator.login();
				}

			}
		});

		btnLoginFacebook.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (facebookAuthenticator != null) {
					facebookAuthenticator.login();
				}

			}
		});

		btnLoginTwitter.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (twitterAuthenticator != null) {
					twitterAuthenticator.login();
				}

			}
		});
	}

	public LoginPageBinder(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

}
