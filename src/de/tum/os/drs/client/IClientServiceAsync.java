package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.SerializableRenter;

public interface IClientServiceAsync {

	/**
	 * 
	 * @return - A list of all devices.
	 */
	void getAllDevices(AsyncCallback<ArrayList<PersistentDevice>> callback);

	/**
	 * 
	 * @param imei
	 *            - IMEI code of the device to be returned.
	 * @return - Returns a device which matches the provided IMEI code.
	 */
	void getDeviceByImei(String imei, AsyncCallback<PersistentDevice> callback);

	/**
	 * 
	 * @return - Returns a list of all device which are not currently rented.
	 */
	void getAvailableDevices(AsyncCallback<ArrayList<PersistentDevice>> callback);

	/**
	 * 
	 * @return - Returns a list of all device which are currently rented.
	 */
	void getRentedDevices(AsyncCallback<ArrayList<PersistentDevice>> callback);

	/**
	 * Adds a new device to the system.
	 * 
	 * @param device
	 *            - Device object containing all relevant data for new device.
	 */
	void addNewDevice(PersistentDevice device, AsyncCallback<Boolean> callback);

	/**
	 * Updates information about an existing devices.
	 * 
	 * @param device
	 *            - Device object containing updated data.
	 */
	void updateDeviceInfo(PersistentDevice device, AsyncCallback<Boolean> callback);

	/**
	 * Removes a device.
	 * 
	 * @param device
	 *            - Device to be removed.
	 */
	void deleteDevice(PersistentDevice device, AsyncCallback<Boolean> callback);

	/**
	 * Removes a device.
	 * 
	 * @param imei
	 *            - IMEI code of the device to be removed.
	 */
	void deleteDevice(String imei, AsyncCallback<Boolean> callback);

	void getRenter(String mattriculationNumber, AsyncCallback<SerializableRenter> callback);

	void addRenter(SerializableRenter renter, AsyncCallback<Boolean> callback);

	void deleteRenter(String mattriculationNumber, AsyncCallback<Boolean> callback);

	void getAllRenters(AsyncCallback<ArrayList<SerializableRenter>> callback);

	void updateRenter(String matriculationNumber, SerializableRenter renter, AsyncCallback<Boolean> callback);

	void deleteRenter(SerializableRenter renter, AsyncCallback<Boolean> callback);

	void getDevicesbyImei(String[] imeiCodes,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void getDevicesRentedBy(String renterMatriculationNumber,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void rentDevicesTo(String renterMatrNr, String[] imeiCodes, Date estimatedReturnDate,
			String comments, String signatureHTML, AsyncCallback<Boolean> callback);

	void rentDeviceTo(String renterMatrNr, String deviceImeiCode,
			Date estimatedReturnDate, String comments, String signatureHTML,
			AsyncCallback<Boolean> callback);

	void returnDevices(String renterMatrNr, String[] imeiCodes, String comments,
			String signatureHTML, AsyncCallback<Boolean> callback);

	void returnDevice(String renterMatrNr, String deviceImeiCode, String comments,
			String signatureHTML, AsyncCallback<Boolean> callback);

	void getEvents(String personName, String IMEI, Date from, Date to,
			Integer maxResultSize, Boolean reverseChronologicalOrder,
			AsyncCallback<ArrayList<PersistentEvent>> callback);
}
