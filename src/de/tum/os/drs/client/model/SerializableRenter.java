package de.tum.os.drs.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class SerializableRenter implements Serializable{
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

	ArrayList<String> rentedDevices;
	
	/**
	 * Empty constructor for serialization.
	 */
	public SerializableRenter(){
	}
	
	public SerializableRenter(String name, String matriculationNumber, String email,
			String phoneNUmber, String comments, ArrayList<String> rentedDevices) {
		super();
		this.name = name;
		this.matriculationNumber = matriculationNumber;
		this.email = email;
		this.phoneNumber = phoneNUmber;
		this.comments = comments;
		this.rentedDevices = rentedDevices;
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

	public String getPhoneNUmber() {
		return phoneNumber;
	}

	public void setPhoneNUmber(String phoneNUmber) {
		this.phoneNumber = phoneNUmber;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public ArrayList<String> getRentedDevices() {
		return rentedDevices;
	}

	public void setRentedDevices(ArrayList<String> rentedDevices) {
		this.rentedDevices = rentedDevices;
	}

	@Override
	public String toString() {
			return "Name: "+name+" Matr Nr: "+matriculationNumber+" Email: "+email;
	}
	
	

}
