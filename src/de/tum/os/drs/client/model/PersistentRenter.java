package de.tum.os.drs.client.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PersistentRenter {

	/**
	 * Full name of the person having rented a device.
	 */
	String name;

	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	String matriculationNumber;

	String email;

	String phoneNumber;

	String comments;

	/**
	 * Stores a list of the IMEI codes of the rented devices
	 */

	@ElementCollection
	List<String> rentedDevices;

	public PersistentRenter(String name, String matriculationNumber, String email,
			String phoneNUmber, String comments, List<String> rentedDevices) {
		super();
		this.name = name;
		this.matriculationNumber = matriculationNumber;
		this.email = email;
		this.phoneNumber = phoneNUmber;
		this.comments = comments == null ? "" : comments;
		this.rentedDevices = rentedDevices == null ? new ArrayList<String>(0) : rentedDevices;
	}

	public PersistentRenter() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMatriculationNumber() {
		return matriculationNumber;
	}

	public void setMatriculationNumber(String matriculationNumber) {
		this.matriculationNumber = matriculationNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNUmber) {
		this.phoneNumber = phoneNUmber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments == null ? "" : comments;
	}

	public List<String> getRentedDevices() {
		return rentedDevices;
	}

	public void setRentedDevices(List<String> rentedDevices) {
		this.rentedDevices = rentedDevices == null ? new ArrayList<String>(0) : rentedDevices;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Matr Nr: " + matriculationNumber + " Email: " + email;
	}

}
