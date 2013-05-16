package de.tum.os.drs.client.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PersistentEvent implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	/*
	 * Data about the involved person.
	 */
	String persName;
	String persMatriculationNumber;
	String email;

	/*
	 * Data about the device.
	 */
	String devName;
	String devImeiCode;

	/*
	 * Data about the event
	 */
	@Temporal(TemporalType.TIMESTAMP)
	Date eventDate;
	EventType eventType;
	String eventComments;
	
	@Lob
	String signatureHtml;
	/*
	 * Constructors
	 */



	/**
	 * Default constructor
	 * 
	 * @param persName
	 *            - Name of the person renting or returning a device.
	 * @param persMatriculationNumber
	 *            - Matriculation number of the person renting or returning a device.
	 * @param email
	 *            - Email address of the person renting or returning a device.
	 * @param devName
	 *            - Name of the device rented or returned.
	 * @param imeiCode
	 *            - IMEI code of the device rented or returned.
	 * @param eventType
	 *            - Defines the type of this event. Either a device is returned or is rented.
	 * @param eventComments
	 *            - Additional comments regarding this event.
	 */
	public PersistentEvent(String persName, String persMatriculationNumber, String email,
			String devName, String imeiCode, EventType eventType, String eventComments, String signatureHtml) {
		super();
		this.persName = persName;
		this.persMatriculationNumber = persMatriculationNumber;
		this.email = email;
		this.devName = devName;
		this.devImeiCode = imeiCode;
		this.eventType = eventType;
		this.eventComments = eventComments;
		this.signatureHtml = signatureHtml;
		
		this.eventDate = new Date();
	}
	
	public PersistentEvent(){
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("******************************************************");
		sb.append("Event: " + id);
		sb.append("\nOn :" + eventDate.toString());
		sb.append("\n" + persName + " (" + persMatriculationNumber + ") "
				+ eventType.toString() + " " + devName+"("+devImeiCode+")");
		sb.append("******************************************************");
		
		return sb.toString();
	}

	/*
	 * Getters & Setters region
	 */

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPersName() {
		return persName;
	}

	public void setPersName(String persName) {
		this.persName = persName;
	}

	public String getPersMatriculationNumber() {
		return persMatriculationNumber;
	}

	public void setPersMatriculationNumber(String persMatriculationNumber) {
		this.persMatriculationNumber = persMatriculationNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getImeiCode() {
		return devImeiCode;
	}

	public void setImeiCode(String imeiCode) {
		this.devImeiCode = imeiCode;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getEventComments() {
		return eventComments;
	}

	public void setEventComments(String eventComments) {
		this.eventComments = eventComments;
	}
	
	public String getSignatureHtml() {
		return signatureHtml;
	}

	public void setSignatureHtml(String signatureHtml) {
		this.signatureHtml = signatureHtml;
	}
}
