package de.tum.os.drs.client.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class PersistentDevice implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	/**
	 * The IMEI code of the device.
	 */
	String IMEI;

	String name;
	/**
	 * Short description of the device. Could include device specs, more about the state of the device (like how it came to have a dent
	 * or scratched display) or number/type/nature of available accessories.
	 */

	String description;

	/**
	 * The state in which the device is currently in, e.g. brand new, used but in good condition, damaged
	 */
	String state;

	/**
	 * The type of the current device: Smartphone, Tablet, Notebook, DesktopPC, Other
	 */
	@Enumerated(EnumType.STRING)
	DeviceType deviceType;

	/**
	 * Stores the name of a picture of the device.
	 */
	String pictureName;

	/**
	 * False if the device was rented and not yet brought back. True otherwise.
	 */
	Boolean isAvailable = true;

	/**
	 * The approximate date when the device will be available.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	Date estimatedReturnDate = new Date();

	/**
	 * Empty constructor needed for serialization.
	 */
	public PersistentDevice() {
	}

	/**
	 * 
	 * @param IMEI
	 *            - The IMEI code of the device.
	 * @param name
	 *            - The name of the device.
	 * @param description
	 *            - A description for the device.
	 * @param state
	 *            - The state of the device, e.g. used, new, broken.
	 * @param deviceType
	 *            - The type of the device, e.g. smartphone, laptop.
	 * @param pictureName
	 * @param isAvailable
	 *            - True if the device is available, false otherwise.
	 * @param estimatedReturnDate
	 *            - An approximate date when the device will be available(can be null in which case it is six weeks from the moment of
	 *            creation).
	 */
	public PersistentDevice(String IMEI, String name, String description, String state,
			DeviceType deviceType, String pictureName, Boolean isAvailable,
			Date estimatedReturnDate) {
		super();
		this.IMEI = IMEI;
		this.name = name;
		this.description = description;
		this.state = state;
		this.deviceType = deviceType;
		this.pictureName = pictureName;
		this.isAvailable = isAvailable;
		if (estimatedReturnDate != null) {
			this.estimatedReturnDate = estimatedReturnDate;
		} else {
			long sixWeeksInMillis = (1000 * 60 * 60 * 24 * 7) * 6;
			this.estimatedReturnDate = new Date(System.currentTimeMillis()
					+ sixWeeksInMillis);
		}
	}

	public Date getEstimatedReturnDate() {
		return estimatedReturnDate;
	}

	public void setEstimatedReturnDate(Date estimatedReturnDate) {
		if (estimatedReturnDate != null) {
			this.estimatedReturnDate = estimatedReturnDate;
		} else {
			long sixWeeksInMillis = (1000 * 60 * 60 * 24 * 7) * 6;
			this.estimatedReturnDate = new Date(System.currentTimeMillis()
					+ sixWeeksInMillis);
		}
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		return "Name: " + this.name + " IMEI: " + this.IMEI + " State: " + this.state;
	}
}
