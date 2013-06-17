package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.RentalSession;
import de.tum.os.drs.client.model.SerializableRenter;

public interface IClientServiceAsync {

	void getAllDevices(int sessionIdHash,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void getDeviceByImei(int sessionIdHash, String imei,
			AsyncCallback<PersistentDevice> callback);

	void getAvailableDevices(int sessionIdHash,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void getRentedDevices(int sessionIdHash,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void addNewDevice(int sessionIdHash, PersistentDevice device,
			AsyncCallback<Boolean> callback);

	void updateDeviceInfo(int sessionIdHash, PersistentDevice device,
			AsyncCallback<Boolean> callback);

	void deleteDevice(int sessionIdHash, PersistentDevice device,
			AsyncCallback<Boolean> callback);

	void deleteDevice(int sessionIdHash, String imei, AsyncCallback<Boolean> callback);

	void getRenter(int sessionIdHash, String mattriculationNumber,
			AsyncCallback<SerializableRenter> callback);

	void addRenter(int sessionIdHash, SerializableRenter renter,
			AsyncCallback<Boolean> callback);

	void deleteRenter(int sessionIdHash, String mattriculationNumber,
			AsyncCallback<Boolean> callback);

	void getAllRenters(int sessionIdHash,
			AsyncCallback<ArrayList<SerializableRenter>> callback);

	void updateRenter(int sessionIdHash, String matriculationNumber,
			SerializableRenter renter, AsyncCallback<Boolean> callback);

	void deleteRenter(int sessionIdHash, SerializableRenter renter, AsyncCallback<Boolean> callback);

	void getDevicesbyImei(int sessionIdHash, String[] imeiCodes,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void getDevicesRentedBy(int sessionIdHash, String renterMatriculationNumber,
			AsyncCallback<ArrayList<PersistentDevice>> callback);

	void rentDevicesTo(int sessionIdHash, String renterMatrNr, String[] imeiCodes,
			Date estimatedReturnDate, String comments, String signatureHTML,
			AsyncCallback<Boolean> callback);

	void rentDeviceTo(int sessionIdHash, String renterMatrNr, String deviceImeiCode,
			Date estimatedReturnDate, String comments, String signatureHTML,
			AsyncCallback<Boolean> callback);

	void returnDevices(int sessionIdHash, String renterMatrNr, String[] imeiCodes,
			String comments, String signatureHTML, AsyncCallback<Boolean> callback);

	void returnDevice(int sessionIdHash, String renterMatrNr, String deviceImeiCode,
			String comments, String signatureHTML, AsyncCallback<Boolean> callback);

	void getEvents(int sessionIdHash, String personName, String IMEI, Date from, Date to,
			Integer maxResultSize, Boolean reverseChronologicalOrder,
			AsyncCallback<ArrayList<PersistentEvent>> callback);

	void login(String token, OAuthAuthorities authority,
			AsyncCallback<RentalSession> callback);

	void logout(AsyncCallback<Boolean> callback);
}
