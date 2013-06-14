package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.SerializableRenter;

@RemoteServiceRelativePath("rentalService")
public interface IClientService extends RemoteService {

	/**
	 * 
	 * @return - A list of all devices.
	 */
	ArrayList<PersistentDevice> getAllDevices();

	/**
	 * 
	 * @param imei
	 *            - IMEI code of the device to be returned.
	 * @return - Returns a device which matches the provided IMEI code.
	 */
	PersistentDevice getDeviceByImei(String imei);

	ArrayList<PersistentDevice> getDevicesbyImei(String[] imeiCodes);

	/**
	 * 
	 * @return - Returns a list of all device which are not currently rented.
	 */
	ArrayList<PersistentDevice> getAvailableDevices();

	/**
	 * 
	 * @return - Returns a list of all device which are currently rented.
	 */
	ArrayList<PersistentDevice> getRentedDevices();

	/**
	 * Adds a new device to the system.
	 * 
	 * @param device
	 *            - Device object containing all relevant data for new device.
	 */
	Boolean addNewDevice(PersistentDevice device);

	/**
	 * Updates information about an existing devices.
	 * 
	 * @param device
	 *            - Device object containing updated data.
	 */
	Boolean updateDeviceInfo(PersistentDevice device);

	/**
	 * Removes a device.
	 * 
	 * @param device
	 *            - Device to be removed.
	 */
	Boolean deleteDevice(PersistentDevice device);

	/**
	 * Removes a device.
	 * 
	 * @param imei
	 *            - IMEI code of the device to be removed.
	 */
	Boolean deleteDevice(String imei);

	/**
	 * Adds a new renter to the DB.
	 * 
	 * @param renter
	 *            - the SerializableRenter obj to be added.
	 * @return - true if successfully added, false otherwise.
	 */
	Boolean addRenter(SerializableRenter renter);

	/**
	 * Returns a SerializableRenter object which has the given matriculation number.
	 * 
	 * @param mattriculationNumber
	 *            -
	 * @return a SerializableRenter object.
	 */
	SerializableRenter getRenter(String mattriculationNumber);

	/**
	 * 
	 * @return - A list of all renters.
	 */
	ArrayList<SerializableRenter> getAllRenters();

	/**
	 * Deletes the given SerializableRenter object from the storage.
	 * 
	 * @param renter
	 *            - SerializableRenter object to be deleted.
	 * @return
	 */
	Boolean deleteRenter(SerializableRenter renter);

	/**
	 * Deletes a SerializableRenter identified by his matriculation number.
	 * 
	 * @param mattriculationNumber
	 * @return
	 */
	Boolean deleteRenter(String mattriculationNumber);

	/**
	 * Updates information on some renter.
	 * 
	 * @param matriculationNumber
	 *            - Matriculation number identifying the renter to be updated.
	 * @param renter
	 *            - A SerializableRenter object holding the updated info(including a new matriculation number if needed).
	 * @return
	 */
	Boolean updateRenter(String matriculationNumber, SerializableRenter renter);

	ArrayList<PersistentDevice> getDevicesRentedBy(String renterMatriculationNumber);

	/**
	 * Add a new device to the list of someone's rented devices.
	 * 
	 * @param renterMatrNr
	 *            - Renter's matriculation number.
	 * @param deviceImeiCode
	 *            - IMEI code of rented device.
	 * @return
	 */
	Boolean rentDeviceTo(String renterMatrNr, String deviceImeiCode,
			Date estimatedReturnDate, String comments, String signatureHTML);

	boolean rentDevicesTo(String renterMatrNr, String[] imeiCodes,
			Date estimatedReturnDate, String comments, String signatureHTML);

	/**
	 * Remove a device from someone's rented devices list.
	 * 
	 * @param renterMatrNr
	 *            - Renter's matriculation number.
	 * @param deviceImeiCode
	 *            - IMEI code of returned device.
	 * @return
	 */
	Boolean returnDevice(String renterMatrNr, String deviceImeiCode, String comments,
			String signatureHTML);

	/**
	 * Remove multiple devices from someone's rented device list.
	 * 
	 * @param renterMatrNr
	 *            - Renter's matriculation number.
	 * @param imeiCodes
	 *            - IMEI codes of returned devices.
	 * @return
	 */
	Boolean returnDevices(String renterMatrNr, String[] imeiCodes, String comments,
			String signatureHTML);

	/**
	 * Returns events that satisfy the given criteria. Any of which is optional.
	 * 
	 * @param personName
	 *            - Events where this person is involved.
	 * @param IMEI
	 *            - Events where this device is involved.
	 * @param from
	 *            - Events that took place after this date.
	 * @param to
	 *            - Events that took place before this date.
	 * @param maxResultSize
	 *            - The upper limit for the result set size.
	 * @param reverseChronologicalOrder
	 *            - Determines if the events should be returned in the order they happened(by default) or in reverse order.
	 * @return - A List of events.
	 */
	ArrayList<PersistentEvent> getEvents(String personName, String IMEI, Date from,
			Date to, Integer maxResultSize, Boolean reverseChronologicalOrder);
}
