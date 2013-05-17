package de.tum.os.drs.client.model;

import java.io.Serializable;


import com.extjs.gxt.ui.client.data.BaseModelData;
import com.flipthebird.gwthashcodeequals.HashCodeBuilder;

public class DisplayableRenter extends BaseModelData implements Serializable {

	public DisplayableRenter() {

	}

	/**
	 * Creates and returns an instance of DisplayableRenter. To be used in the client as a lightweight alternative to
	 * SerializableRenter.
	 * 
	 * @param name
	 *            - name of the new device.
	 * @param matriculationNr
	 *            - IMEI code of the new device.
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

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (!obj.getClass().equals(this.getClass()))
			return super.equals(obj);
		else {
			if (this.getName().toLowerCase().trim()
					.equals(((DisplayableRenter) obj).getName().toLowerCase().trim())
					&& this.getMatriculation()
							.toLowerCase()
							.trim()
							.equals(((DisplayableRenter) obj).getMatriculation()
									.toLowerCase().trim())) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n<->");
		if (this.getName() != null && this.getName().length() > 0)
			sb.append("Name: " + this.getName());
		if (this.getMatriculation() != null && this.getMatriculation().length() > 0)
			sb.append(" Matriculation: " + this.getMatriculation());
		sb.append("\n<->");

		return super.toString();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return new HashCodeBuilder(13, 17).append(this.getName()).append(this.getMatriculation()).toHashCode();
	}
	
}
