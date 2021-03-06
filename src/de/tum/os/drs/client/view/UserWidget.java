package de.tum.os.drs.client.view;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.helpers.OAuthApiHelper;
import de.tum.os.drs.client.helpers.OAuthParser;
import de.tum.os.drs.client.model.IAction;
import de.tum.os.drs.client.model.OAuthAuthorities;

public class UserWidget extends Composite {

	private static UserWidgetUiBinder uiBinder = GWT.create(UserWidgetUiBinder.class);
	private IAction logoutAction;

	@UiField
	Label lblUsername;

	@UiField
	Button btnLogout;

	@UiField
	Label lblLoggedInWith;

	@UiField
	Label lblLoggedInAs;

	@UiField
	Image imgUserPic;

	interface UserWidgetUiBinder extends UiBinder<Widget, UserWidget> {
	}

	public UserWidget(IAction logoutAction) {
		this.logoutAction = logoutAction;
		
		initWidget(uiBinder.createAndBindUi(this));

		lblUsername.setText(CookieHelper.getAuthenticatedUsername());
		getCurrentUserInfo();
	}

	private void getCurrentUserInfo() {
		// Values can be: google, facebook, TUM, twitter or none.
		OAuthAuthorities authenticator = CookieHelper.getOAuthAuthority();
		if (authenticator == OAuthAuthorities.none) {
			return;
		}

		String token = CookieHelper.getAuthToken();
		if (token == null || token.length() <= 0) {
			return;
		}

		String url = OAuthApiHelper.getAuthUrlFromAuthority(authenticator);

		// Leave if any problem occurs. Don't bother the user.
		if (url == null || url.length() <= 0) {
			return;
		}

		// Append token
		url += token;
		RequestBuilder rb;
		rb = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

		try {
			// Check token validity
			// rb.setHeader("Content-Type", "application/json"); // Windows live doesn't want a content-type header at all
			Request req = rb.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					// Window.alert("Response: " + response.getText());
					// System.out.println("Response: " + response.getText());
					String jsonString = response.getText();
					if (OAuthParser.hasError(jsonString)) {
						// Maybe redirect to first page?
					} else {
						// Put user name and ID in cookies
						String username = OAuthParser
								.getAuthenticatedUsername(jsonString);
						CookieHelper.setAuthenticatedUsername(username);
						lblUsername.setText(username);

						String userID = OAuthParser.getAuthenticatedUserID(jsonString);
						// Should be computed with SHA256 and only the hash should be sent to the server
						String userIdHash = String.valueOf(userID.hashCode());

						System.out.println("User id:" + userID);
						System.out.println("User id hash (SHA256):" + userIdHash);
						CookieHelper.setAuthenticatedUserID(userID);

						String userEmail = OAuthParser
								.getAuthenticatedUserEmail(jsonString);
						CookieHelper.setAuthenticatedUserEmail(userEmail);
						lblLoggedInAs.setText(userEmail);
						lblLoggedInWith.setText(CookieHelper.getOAuthAuthority()
								.toString());

						String userPicUrl = OAuthParser
								.getAuthenticatedUserPictureUrl(jsonString);
						imgUserPic.setUrl(userPicUrl);
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					Window.alert("An error occured: " + request.toString());
					// Maybe redirect to first page?

				}
			});
		} catch (Exception e) {

		}

	}

	@UiHandler("btnLogout")
	void onBtnLogoutClick(ClickEvent event) {
		if(this.logoutAction!=null){
			this.logoutAction.execute();
		}
		
//		CookieHelper.resetAuthenticatedUserEmail();
//		CookieHelper.resetAuthToken();
//		CookieHelper.resetOAuthAuthority();
//		CookieHelper.resetAuthenticatedUsername();
//		CookieHelper.resetAuthenticatedUserID();
//
//		Auth.get().clearAllTokens();
//
//		Window.Location.reload();
		// String redirectUrl = Window.Location.getHref();

		// Window.Location
		// .assign("https://127.0.0.1:8888/NewRentalSystem.html?gwt.codesvr=127.0.0.1:9997");
		// Window.Location.assign(redirectUrl);
	}
}
