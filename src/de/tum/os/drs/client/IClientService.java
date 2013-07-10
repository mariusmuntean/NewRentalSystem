package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.RentalSession;
import de.tum.os.drs.client.model.SerializableRenter;

@RemoteServiceRelativePath("rentalService")
public interface IClientService extends RemoteService {

	/**
	 * Log in on the Rental Server
	 * @param token - the token to be checked for validity.
	 * @param authority - the authority that issued this token.
	 * @return - A RentalSession instance that can be either valid or invalid.
	 */
	RentalSession login(String token, OAuthAuthorities authority);
	
/**
 * Invalidates the current session.
 * 
 * @param sessionIdHash - session id hash code that identifies the session to be terminated.
 * @return - Returns true if the logout was successful or false otherwise.
 */
	Boolean logout(int sessionIdHash);
	
	/**
	 * 
	 * @return - A list of all devices.
	 */
	ArrayList<PersistentDevice> getAllDevices(int sessionIdHash);

	/**
	 * 
	 * @param imei
	 *            - IMEI code of the device to be returned.
	 * @return - Returns a device which matches the provided IMEI code.
	 */
	PersistentDevice getDeviceByImei(int sessionIdHash, String imei);

	ArrayList<PersistentDevice> getDevicesbyImei(int sessionIdHash, String[] imeiCodes);

	/**
	 * 
	 * @return - Returns a list of all device which are not currently rented.
	 */
	ArrayList<PersistentDevice> getAvailableDevices(int sessionIdHash);

	/**
	 * 
	 * @return - Returns a list of all device which are currently rented.
	 */
	ArrayList<PersistentDevice> getRentedDevices(int sessionIdHash);

	/**
	 * Adds a new device to the system.
	 * 
	 * @param device
	 *            - Device object containing all relevant data for new device.
	 */
	Boolean addNewDevice(int sessionIdHash, PersistentDevice device);

	/**
	 * Updates information about an existing devices.
	 * 
	 * @param device
	 *            - Device object containing updated data.
	 */
	Boolean updateDeviceInfo(int sessionIdHash, PersistentDevice device);

	/**
	 * Removes a device.
	 * 
	 * @param device
	 *            - Device to be removed.
	 */
	Boolean deleteDevice(int sessionIdHash, PersistentDevice device);

	/**
	 * Removes a device.
	 * 
	 * @param imei
	 *            - IMEI code of the device to be removed.
	 */
	Boolean deleteDevice(int sessionIdHash, String imei);

	/**
	 * Adds a new renter to the DB.
	 * 
	 * @param renter
	 *            - the SerializableRenter obj to be added.
	 * @return - true if successfully added, false otherwise.
	 */
	Boolean addRenter(int sessionIdHash, SerializableRenter renter);

	/**
	 * Returns a SerializableRenter object which has the given matriculation number.
	 * 
	 * @param mattriculationNumber
	 *            -
	 * @return a SerializableRenter object.
	 */
	SerializableRenter getRenter(int sessionIdHash, String mattriculationNumber);

	/**
	 * 
	 * @return - A list of all renters.
	 */
	ArrayList<SerializableRenter> getAllRenters(int sessionIdHash);

	/**
	 * Deletes the given SerializableRenter object from the storage.
	 * 
	 * @param renter
	 *            - SerializableRenter object to be deleted.
	 * @return
	 */
	Boolean deleteRenter(int sessionIdHash, SerializableRenter renter);

	/**
	 * Deletes a SerializableRenter identified by his matriculation number.
	 * 
	 * @param mattriculationNumber
	 * @return
	 */
	Boolean deleteRenter(int sessionIdHash, String mattriculationNumber);

	/**
	 * Updates information on some renter.
	 * 
	 * @param matriculationNumber
	 *            - Matriculation number identifying the renter to be updated.
	 * @param renter
	 *            - A SerializableRenter object holding the updated info(including a new matriculation number if needed).
	 * @return
	 */
	Boolean updateRenter(int sessionIdHash, String matriculationNumber, SerializableRenter renter);

	ArrayList<PersistentDevice> getDevicesRentedBy(int sessionIdHash, String renterMatriculationNumber);

	/**
	 * Add a new device to the list of someone's rented devices.
	 * 
	 * @param renterMatrNr
	 *            - Renter's matriculation number.
	 * @param deviceImeiCode
	 *            - IMEI code of rented device.
	 * @return
	 */
	Boolean rentDeviceTo(int sessionIdHash, String renterMatrNr, String deviceImeiCode,
			Date estimatedReturnDate, String comments, String signatureHTML);

	boolean rentDevicesTo(int sessionIdHash, String renterMatrNr, String[] imeiCodes,
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
	Boolean returnDevice(int sessionIdHash, String renterMatrNr, String deviceImeiCode, String comments,
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
	Boolean returnDevices(int sessionIdHash, String renterMatrNr, String[] imeiCodes, String comments,
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
	ArrayList<PersistentEvent> getEvents(int sessionIdHash, String personName, String IMEI, Date from,
			Date to, Integer maxResultSize, Boolean reverseChronologicalOrder);
}
