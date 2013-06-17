package de.tum.os.drs.client.model;

import java.io.Serializable;
import java.util.Date;

public class RentalSession implements Serializable{

	String sessionId = "";
	int sessionIdHash = sessionId.hashCode();
	Date sessionStart;
	Boolean isValid;

	/**
	 * Empty constructor for serialization
	 */
	public RentalSession() {

	}

	/**
	 * Creates a new RentalSession instance. Sets the readonly sessionStart to the moment of creation of this object.
	 * 
	 * @param sessionId
	 *            - unique ID to identify the session
	 */
	public RentalSession(String sessionId) {
		if (sessionId == null || sessionId.isEmpty()) {
			throw new IllegalArgumentException("Session ID cannot be null or empty!");
		}

		this.sessionId = sessionId;
		this.sessionIdHash = this.sessionId.hashCode();
		this.sessionStart = new Date();
		this.isValid = true;
	}

	/**
	 * @return the isValid
	 */
	public Boolean getIsValid() {
		return isValid;
	}

	/**
	 * @param isValid
	 *            the isValid to set
	 */
	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @return the sessionStart
	 */
	public Date getSessionStart() {
		return sessionStart;
	}

	/**
	 * @return the sessionIdHash
	 */
	public int getSessionIdHash() {
		return sessionIdHash;
	}
}
