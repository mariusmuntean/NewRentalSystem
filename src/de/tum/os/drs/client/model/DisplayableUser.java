package de.tum.os.drs.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DisplayableUser extends BaseModelData implements Serializable {

	private String nameField = "name";
	private String emaiField = "email";
	private String IDField = "id";
	private String tokenField = "token";

	/**
	 * Empty constructor for serialization
	 */
	public DisplayableUser() {
	}

	public DisplayableUser(String name, String email, String ID, String token) {
		
		setName(name);
		setEmail(email);
		setId(ID);
		setToken(token);
	}

	public void setName(String name) {
		set(nameField, name);
	}

	public String getName() {
		return get(nameField);
	}

	public void setEmail(String email) {
		set(emaiField, email);
	}

	public String getEmail() {
		return get(emaiField);
	}

	public void setId(String ID) {
		set(IDField, ID);
	}

	public String getId() {
		return get(IDField);
	}

	public void setToken(String token) {
		set(tokenField, token);
	}

	public String getToken() {
		return get(tokenField);
	}
}
