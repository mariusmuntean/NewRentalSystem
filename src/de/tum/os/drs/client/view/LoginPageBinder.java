package de.tum.os.drs.client.view;

import java.lang.reflect.Method;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

import de.tum.os.drs.client.model.IAuthenticator;

public class LoginPageBinder extends Composite implements HasText {

	private static LoginPageBinderUiBinder uiBinder = GWT
			.create(LoginPageBinderUiBinder.class);
	@UiField
	Button btnLoginFacebook;
	@UiField
	Button btnLoginGoogle;
	@UiField
	Button btnLoginTwitter;

	interface LoginPageBinderUiBinder extends UiBinder<Widget, LoginPageBinder> {
	}

	IAuthenticator googleAuthenticator;

	public LoginPageBinder(IAuthenticator googleAuthenticator) {
		initWidget(uiBinder.createAndBindUi(this));

		this.googleAuthenticator = googleAuthenticator;

		wireUpControls();
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
