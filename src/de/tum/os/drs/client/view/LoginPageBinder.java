package de.tum.os.drs.client.view;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.tum.os.drs.client.Resources.Images.ResourcesImage;
import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.helpers.OAuthApiHelper;
import de.tum.os.drs.client.helpers.OAuthParser;
import de.tum.os.drs.client.model.FacebookAuthenticator;
import de.tum.os.drs.client.model.GoogleAuthenticator;
import de.tum.os.drs.client.model.IAuthenticator;
import de.tum.os.drs.client.model.LiveAuthenticator;
import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.Tuple;
import de.tum.os.drs.client.model.TwitterAuthenticator;

public class LoginPageBinder extends Composite implements HasText {

	private static LoginPageBinderUiBinder uiBinder = GWT
			.create(LoginPageBinderUiBinder.class);
	@UiField
	PushButton btnLoginFacebook;
	@UiField
	PushButton btnLoginGoogle;
	@UiField
	PushButton btnLoginLive;
	@UiField
	PushButton btnLoginTwitter;
	@UiField
	PushButton btnLoginTUM;

	/*
	 * Callback
	 */
	Callback<Tuple<String, OAuthAuthorities>, Throwable> genericAfterAuthCallback;

	interface LoginPageBinderUiBinder extends UiBinder<Widget, LoginPageBinder> {
	}

	IAuthenticator googleAuthenticator, facebookAuthenticator,
			liveAuthenticator, twitterAuthenticator;

	DialogBox modalLoader;

	// public LoginPageBinder(Callback<String, Throwable> facebookCallback,
	// Callback<String, Throwable> googleCallback,
	// Callback<String, Throwable> twitterCallback) {
	//
	// initWidget(uiBinder.createAndBindUi(this));
	// instantiateControls();
	//
	// // this.ggCallback = googleCallback;
	// // this.fbCallback = facebookCallback;
	// // this.twCallback = twitterCallback;
	//
	// showModalLoader();
	// // authenticateIfValidTokenFound();
	// }

	public LoginPageBinder(
			Callback<Tuple<String, OAuthAuthorities>, Throwable> genericAfterAuthCallback) {

		initWidget(uiBinder.createAndBindUi(this));
		instantiateControls();

		this.genericAfterAuthCallback = genericAfterAuthCallback;

		showModalLoader();
		authenticateIfValidTokenFound();
	}

	private void enableLoginScreenIfNotAuthenticated() {
		this.googleAuthenticator = new GoogleAuthenticator(
				genericAfterAuthCallback);
		this.facebookAuthenticator = new FacebookAuthenticator(
				genericAfterAuthCallback);
		this.twitterAuthenticator = new TwitterAuthenticator(
				genericAfterAuthCallback);
		this.liveAuthenticator = new LiveAuthenticator(genericAfterAuthCallback);

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

	private void checkTokenValidity(final String token,
			final OAuthAuthorities oAuthAuthority) {
		String url = OAuthApiHelper.getAuthUrlFromAuthority(oAuthAuthority);

		// Leave if any problem occurs. Don't bother the user.
		if (url == null || url.length() <= 0
				|| genericAfterAuthCallback == null) {
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
				public void onResponseReceived(Request request,
						Response response) {
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
						genericAfterAuthCallback
								.onSuccess(new Tuple<String, OAuthAuthorities>(
										token, oAuthAuthority));

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

	// private Callback<String, Throwable> getCallbackFromAuthrity(
	// OAuthAuthorities oAuthAuthority) {
	// switch (oAuthAuthority) {
	// case facebook: {
	// return fbCallback;
	// }
	// case google: {
	// return ggCallback;
	// }
	// case linkedin: {
	// return null;
	// }
	// case tum: {
	// return null;
	// }
	// case twitter: {
	// return null;
	// }
	// default: {
	// return null;
	// }
	// }
	// }

	private void instantiateControls() {

	}

	private DialogBox getModalLoader() {
		// Init modal loader wheel
		DialogBox poPanel = new DialogBox(false, true);
		VerticalPanel dialogContent = new VerticalPanel();
		dialogContent.setSpacing(5);

		// Image imgLoader = new Image("../../../../../../Resources/Images/loader.gif");
		Image imgLoader = new Image(ResourcesImage.INSTANCE.loader()
				.getSafeUri().asString());
		dialogContent.add(imgLoader);
		Label lblModalText = new Label("Trying to authenticate you.");
		dialogContent.add(lblModalText);
		dialogContent.setCellHorizontalAlignment(lblModalText,
				HasHorizontalAlignment.ALIGN_CENTER);
		dialogContent.setCellHorizontalAlignment(imgLoader,
				HasHorizontalAlignment.ALIGN_CENTER);
		poPanel.setWidget(dialogContent);
		poPanel.setAnimationEnabled(true);
		poPanel.setGlassEnabled(true);
		poPanel.setPopupPosition(
				Window.getClientWidth() / 2 - imgLoader.getWidth() / 2,
				Window.getClientHeight() / 2 - imgLoader.getHeight() / 2);

		return poPanel;
	}

	private void showModalLoader() {
		if (this.modalLoader != null) {
			this.modalLoader.hide();
			this.modalLoader = null;
		}

		this.modalLoader = getModalLoader();
		 this.modalLoader.getElement().getStyle().setZIndex(123);
		this.modalLoader.show();
		// this.modalLoader.showRelativeTo(btnLoginFacebook);
	}

	private void hideModalLoader() {
		if (this.modalLoader != null) {
			this.modalLoader.hide();
		}
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

		btnLoginLive.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (liveAuthenticator != null) {
					liveAuthenticator.login();
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

		btnLoginTUM.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DialogBox dBox = createDialogBox();
				dBox.setGlassEnabled(true);
				dBox.setAnimationEnabled(true);
				dBox.center();
				dBox.show();

			}
		});
	}

	private DialogBox createDialogBox() {
		// Create a dialog box and set the caption text
		final DialogBox dialogBox = new DialogBox();
		dialogBox.ensureDebugId("cwDialogBox");
		dialogBox.setText("TUM Login Instructions");

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		dialogBox.setWidget(dialogContents);

		// Add some text to the top of the dialog
		HTML details = new HTML("Coming soon.");
		dialogContents.add(details);
		dialogContents.setCellHorizontalAlignment(details,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add an image to the dialog
		Image image = new Image(
				"http://clubpenguincheatscitya4.files.wordpress.com/2011/08/1_token.jpg");
		dialogContents.add(image);
		dialogContents.setCellHorizontalAlignment(image,
				HasHorizontalAlignment.ALIGN_CENTER);

		// Add a close button at the bottom of the dialog
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		dialogContents.add(closeButton);
		if (LocaleInfo.getCurrentLocale().isRTL()) {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_LEFT);

		} else {
			dialogContents.setCellHorizontalAlignment(closeButton,
					HasHorizontalAlignment.ALIGN_RIGHT);
		}

		// Return the dialog box
		return dialogBox;
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
