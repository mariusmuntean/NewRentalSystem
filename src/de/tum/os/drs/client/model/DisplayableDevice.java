package de.tum.os.drs.client.model;

import java.io.Serializable;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.flipthebird.gwthashcodeequals.HashCodeBuilder;

public class DisplayableDevice extends BaseModelData implements Serializable {

	public DisplayableDevice() {

	}

	/**
	 * Creates and returns an instance of DisplayableDevice. To be used in the client as a lightweight alternative to PersistentDevice.
	 * 
	 * @param name
	 *            - name of the new device.
	 * @param imei
	 *            - IMEI code of the new device.
	 * @param imgName
	 *            - image representing the new device.
	 */
	public DisplayableDevice(String name, String imei, String imgName) {
		setName(name);
		setImei(imei);
		setImageName(imgName);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getImei() {
		return get("imei");
	}

	public void setImei(String imei) {
		set("imei", imei);
	}

	public String getImagaName() {
		return get("imgName");
	}

	public void setImageName(String imgName) {
		set("imgName", imgName);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (!obj.getClass().equals(this.getClass()))
			return super.equals(obj);
		else {
			if (this.getName().toLowerCase().trim()
					.equals(((DisplayableDevice) obj).getName().toLowerCase().trim())
					&& this.getImei()
							.toLowerCase()
							.trim()
							.equals(((DisplayableDevice) obj).getImei().toLowerCase()
									.trim())) {
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
		if (this.getImei() != null && this.getImei().length() > 0)
			sb.append(" IMEI: " + this.getImei());
		if (this.getImagaName() != null && this.getImagaName().length() > 0)
			sb.append(" Image:" + this.getImagaName());
		sb.append("\n<->");

		return super.toString();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return new HashCodeBuilder(13, 17).append(this.getName()).append(this.getImei())
				.toHashCode();
	}

}
