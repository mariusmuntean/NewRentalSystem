package de.tum.os.drs.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DisplayableDevice extends BaseModelData implements Serializable{

	public DisplayableDevice() {

	}

	/**
	 * Creates and returns an instance of DisplayableDevice. To be used in the client as a lightweight alternative to PersistentDevice.
	 * 
	 * @param name - name of the new device.
	 * @param imei - IMEI code of the new device.
	 * @param imgName - image representing the new device.
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
}
