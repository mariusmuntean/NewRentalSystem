package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;

import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.SerializableRenter;
import de.tum.os.drs.client.view.MainPageBinder;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NewRentalSystem implements EntryPoint {

	/*
	 * Service client
	 */
	IClientServiceAsync service = (IClientServiceAsync) GWT.create(IClientService.class);
	ServiceDefTarget serviceDef = (ServiceDefTarget) service;
	String addr = GWT.getModuleBaseURL() + "rentalService";

	MainPageBinder mainPageBinder;
	ListDataProvider<PersistentDevice> availableDevicesDataProvider = new ListDataProvider<PersistentDevice>();
	ListDataProvider<PersistentDevice> unavailableDevicesDataProvider = new ListDataProvider<PersistentDevice>();

	ListDataProvider<PersistentEvent> eventsHistoryDataProvider = new ListDataProvider<PersistentEvent>();
	ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider = new ListDataProvider<PersistentEvent>();

	ListStore<DisplayableRenter> displayableRentersListStore = new ListStore<DisplayableRenter>();
	ListStore<DisplayableDevice> availableDevicesListStore = new ListStore<DisplayableDevice>();

	private ListStore<DisplayableRenter> displayableRentersFilterListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> displayableDevicesFilterListStore = new ListStore<DisplayableDevice>();

	private HashMap<String, String> deviceNameToImageNameMap = new HashMap<String, String>() {
		private static final long serialVersionUID = -4645423715285941470L;
		{
			put("nexus one", "nexus one.jpg");
			put("nexus s", "nexus s.jpg");
			put("galaxy nexus", "galaxy nexus.jpg");
			put("nexus 4", "nexus 4.jpg");
			put("nexus 7", "nexus 7.jpg");
			put("htc one", "htc one.jpg");
			put("htc one x", "htc one x.jpg");
			put("htc one x+", "htc one x+.jpg");
			put("nexus 10", "nexus 10.jpg");
		}
	};

	private static final String deviceNotFoundImage = "android question.jpg";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		serviceDef.setServiceEntryPoint(addr);

		mainPageBinder = new MainPageBinder(this, availableDevicesDataProvider,
				unavailableDevicesDataProvider, eventsHistoryDataProvider,
				eventsFilteredHistoryDataProvider, displayableRentersListStore,
				availableDevicesListStore, displayableRentersFilterListStore,
				displayableDevicesFilterListStore);
		fetchData();

		RootLayoutPanel.get().add(mainPageBinder);
	}

	public void rentDevicesToExistingRenter(String renterMatrNr,
			String[] deviceImeiCodes, String comments, String signatureHTML) {

		AsyncCallback<Boolean> rentDevicesCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				// TODO Auto-generated method stub
				fetchEventsHistory();
				fetchAvailableDevices();
				fetchUnavailableDevices();

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};
		service.rentDevicesTo(renterMatrNr, deviceImeiCodes, comments, signatureHTML,
				rentDevicesCallback);

	}

	public void rentDevicesToNewRenter(SerializableRenter sr, final String renterMatrNr,
			final String[] deviceImeiCodes, final String comments,
			final String signatureHTML) {

		AsyncCallback<Boolean> addNewRenterCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				rentDevicesToExistingRenter(renterMatrNr, deviceImeiCodes, comments,
						signatureHTML);
				fetchAllRenters();

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};

		service.addRenter(sr, addNewRenterCallback);

	}

	private void fetchData() {
		fetchAvailableDevices();
		fetchUnavailableDevices();
		fetchEventsHistory();

		fetchAllRenters();

	}

	private void fetchAllRenters() {
		AsyncCallback<ArrayList<SerializableRenter>> callback = new AsyncCallback<ArrayList<SerializableRenter>>() {

			@Override
			public void onSuccess(ArrayList<SerializableRenter> result) {
				updateAllRenters(result);

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};
		service.getAllRenters(callback);
	}

	protected void updateAllRenters(ArrayList<SerializableRenter> result) {
		displayableRentersListStore.removeAll();
		if (result != null && result.size() > 0) {
			for (SerializableRenter sr : result) {
				displayableRentersListStore.add(new DisplayableRenter(sr.getName(), sr
						.getMatriculationNumber()));
			}
		}

		// displayableRentersListStore

	}

	public void fetchEventsHistoryFiltered(String personName, String IMEI, Date from,
			Date to, Integer maxResultSize, Boolean reverseChronologicalOrder) {
		AsyncCallback<ArrayList<PersistentEvent>> callback = new AsyncCallback<ArrayList<PersistentEvent>>() {

			@Override
			public void onSuccess(ArrayList<PersistentEvent> result) {
				updateEventsHistoryFiltered(result);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		};
		service.getEvents(personName, IMEI, from, to, maxResultSize,
				reverseChronologicalOrder, callback);
	}

	protected void updateEventsHistoryFiltered(ArrayList<PersistentEvent> result) {

		eventsFilteredHistoryDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			eventsFilteredHistoryDataProvider.getList().addAll(result);
		}

	}

	private void fetchEventsHistory() {
		AsyncCallback<ArrayList<PersistentEvent>> callback = new AsyncCallback<ArrayList<PersistentEvent>>() {

			@Override
			public void onSuccess(ArrayList<PersistentEvent> result) {
				updateEventsHistory(result);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		};
		service.getEvents(null, null, null, null, Integer.MAX_VALUE, true, callback);

	}

	protected void updateEventsHistory(ArrayList<PersistentEvent> result) {
		this.eventsFilteredHistoryDataProvider.getList().clear();
		this.eventsHistoryDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			eventsFilteredHistoryDataProvider.getList().addAll(result);
			if (result.size() <= 10) {
				eventsHistoryDataProvider.getList().addAll(result);
			} else {
				eventsHistoryDataProvider.getList().addAll(result.subList(0, 10));
			}
		}
		eventsFilteredHistoryDataProvider.refresh();
		eventsHistoryDataProvider.refresh();

		// Populate/refresh history filters
		// First adding data to HashSets to make sure each item is unique
		HashSet<DisplayableRenter> renters = new HashSet<DisplayableRenter>();
		HashSet<DisplayableDevice> devices = new HashSet<DisplayableDevice>();
		for (PersistentEvent pe : result) {
			// Populate devices filter
			String imgName = deviceNameToImageNameMap.get("");
			if (imgName == null)
				imgName = deviceNotFoundImage;
			DisplayableDevice dd = new DisplayableDevice(pe.getDevName(),
					pe.getImeiCode(), imgName);
			// displayableDevicesFilterListStore.add(dd);
			devices.add(dd);
			// Populate renters filter.
			DisplayableRenter dr = new DisplayableRenter(pe.getPersName(),
					pe.getPersMatriculationNumber());
			// displayableRentersFilterListStore.add(dr);
			renters.add(dr);
		}
		if (devices.size() > 0) {
			ArrayList<DisplayableDevice> devicesList = new ArrayList<DisplayableDevice>(
					devices);
			displayableDevicesFilterListStore.add(devicesList);
		}

		if (renters.size() > 0) {
			ArrayList<DisplayableRenter> rentersList = new ArrayList<DisplayableRenter>(
					renters);
			displayableRentersFilterListStore.add(rentersList);
		}

	}

	private void fetchUnavailableDevices() {
		AsyncCallback<ArrayList<PersistentDevice>> callback = new AsyncCallback<ArrayList<PersistentDevice>>() {

			@Override
			public void onSuccess(ArrayList<PersistentDevice> result) {
				updateUnavailableDevices(result);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		};
		service.getRentedDevices(callback);

	}

	protected void updateUnavailableDevices(ArrayList<PersistentDevice> result) {
		unavailableDevicesDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			unavailableDevicesDataProvider.getList().addAll(result);
		}

		unavailableDevicesDataProvider.refresh();

		if (mainPageBinder != null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList()
					.size(), unavailableDevicesDataProvider.getList().size());
	}

	private void fetchAvailableDevices() {
		AsyncCallback<ArrayList<PersistentDevice>> callback = new AsyncCallback<ArrayList<PersistentDevice>>() {

			@Override
			public void onSuccess(ArrayList<PersistentDevice> result) {
				updateAvailableDevices(result);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		};
		service.getAvailableDevices(callback);

	}

	protected void updateAvailableDevices(ArrayList<PersistentDevice> result) {
		availableDevicesDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			availableDevicesDataProvider.getList().addAll(result);

			for (PersistentDevice pd : result) {
				String imgName = deviceNameToImageNameMap.get(pd.getName().toLowerCase());
				if (imgName == null)
					imgName = deviceNotFoundImage;
				availableDevicesListStore.add(new DisplayableDevice(pd.getName(), pd
						.getIMEI(), imgName));
			}
		}

		availableDevicesDataProvider.refresh();

		if (mainPageBinder != null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList()
					.size(), unavailableDevicesDataProvider.getList().size());

	}
}
