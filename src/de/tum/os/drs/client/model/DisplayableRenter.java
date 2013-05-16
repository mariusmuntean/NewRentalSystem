package de.tum.os.drs.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DisplayableRenter extends BaseModelData implements Serializable{

	public DisplayableRenter() {

	}

	/**
	 * Creates and returns an instance of DisplayableRenter. 
	 * To be used in the client as a lightweight alternative to SerializableRenter.
	 * 
	 * @param name - name of the new device.
	 * @param matriculationNr - IMEI code of the new device.
	 */
	public DisplayableRenter(String name, String matriculationNr) {
		setName(name);
		setMatriculation(matriculationNr);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getMatriculation() {
		return get("matriculation");
	}

	public void setMatriculation(String imei) {
		set("matriculation", imei);
	}

}
