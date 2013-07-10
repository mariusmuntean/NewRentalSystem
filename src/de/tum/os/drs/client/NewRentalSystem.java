package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Info;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;
import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.IAction;
import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.RentalSession;
import de.tum.os.drs.client.model.SerializableRenter;
import de.tum.os.drs.client.model.Tuple;
import de.tum.os.drs.client.view.LoginPageBinder;
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
	RentalSession currentSession;

	MainPageBinder mainPageBinder;
	LoginPageBinder loginPageBinder;

	MultiWordSuggestOracle deviceNamesSuggestOracle = new MultiWordSuggestOracle();

	ListDataProvider<PersistentDevice> availableDevicesDataProvider = new ListDataProvider<PersistentDevice>();
	ListDataProvider<PersistentDevice> unavailableDevicesDataProvider = new ListDataProvider<PersistentDevice>();

	ListDataProvider<PersistentEvent> eventsHistoryDataProvider = new ListDataProvider<PersistentEvent>();
	ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider = new ListDataProvider<PersistentEvent>();

	ListStore<DisplayableRenter> displayableRentersListStore = new ListStore<DisplayableRenter>();
	ListStore<DisplayableDevice> availableDevicesListStore = new ListStore<DisplayableDevice>();

	ListDataProvider<SerializableRenter> allRentersDataProvider = new ListDataProvider<SerializableRenter>(
			new ArrayList<SerializableRenter>());
	ListDataProvider<PersistentDevice> allRentedDevicesDataProvider = new ListDataProvider<PersistentDevice>(
			new ArrayList<PersistentDevice>());

	private ListStore<DisplayableRenter> displayableRentersFilterListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> displayableDevicesFilterListStore = new ListStore<DisplayableDevice>();

	private ListStore<DisplayableDevice> allDisplayableDevicesListStore = new ListStore<DisplayableDevice>();

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

	Callback<Tuple<String, OAuthAuthorities>, Throwable> genericAfterAuthCallback = new Callback<Tuple<String, OAuthAuthorities>, Throwable>() {
		@Override
		public void onSuccess(Tuple<String, OAuthAuthorities> result) {
			serviceDef.setServiceEntryPoint(addr);

			String token = result.x;
			OAuthAuthorities authority = result.y;

			// System.out.println(authority.toString() + " Token: " + token);
			// Window.alert(authority.toString() + " Token: " + token);
			// DialogBox db = new DialogBox();
			// db.add(new Label(token));
			// db.center();
			// db.show();

			CookieHelper.resetOAuthAuthority();
			CookieHelper.resetAuthToken();

			CookieHelper.setAuthCookie(authority, token);

			// Test authorization
			AsyncCallback<RentalSession> authorizationCallback = new AsyncCallback<RentalSession>() {

				@Override
				public void onSuccess(RentalSession result) {
					NewRentalSystem.this.currentSession = result;
					if (result.getIsValid()) {
						loadMainPage();
					} else {
						Info.display("Error!",
								"You must be authorized to acces the platform!");
						Auth.get().clearAllTokens();
						CookieHelper.resetAuthenticatedUserEmail();
						CookieHelper.resetAuthenticatedUserID();
						CookieHelper.resetAuthenticatedUsername();
						CookieHelper.resetAuthToken();
						CookieHelper.resetOAuthAuthority();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Info.display("Network Error!",
							"Could not communicate with the Rental Server");
				}
			};
			service.login(token, authority, authorizationCallback);
		}

		@Override
		public void onFailure(Throwable caught) {
			// The authentication process failed for some reason, see caught.getMessage()
			Window.alert("Network error: " + caught.getMessage());
		}
	};

	public final IAction logoutAction = new IAction() {

		@Override
		public void execute() {

			// Log out from the RentalServer
			AsyncCallback<Boolean> logOutCallback = new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					NewRentalSystem.this.currentSession = null;
					if (result) {
						Info.display("Success", "You are logged out from the server.");
					} else {
						Info.display("Server error",
								"You weren't logged out from the server.");
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					NewRentalSystem.this.currentSession = null;
					// Info.display("Network error!", "Server session is unknown.");

				}
			};
			if (service != null && NewRentalSystem.this.currentSession != null) {
				service.logout(NewRentalSystem.this.currentSession.getSessionIdHash(),
						logOutCallback);
			}

			// Delete local state
			CookieHelper.resetAuthenticatedUserEmail();
			CookieHelper.resetAuthToken();
			CookieHelper.resetOAuthAuthority();
			CookieHelper.resetAuthenticatedUsername();
			CookieHelper.resetAuthenticatedUserID();
			Auth.get().clearAllTokens();

			// Return to login screen
			Window.Location.reload();

		}
	};

	private static final String deviceNotFoundImage = "android question.jpg";

	// /**
	// * This is the entry point method.
	// */
	// public void onModuleLoad() {
	// serviceDef.setServiceEntryPoint(addr);
	//
	// mainPageBinder = new MainPageBinder(this, availableDevicesDataProvider,
	// unavailableDevicesDataProvider, eventsHistoryDataProvider,
	// eventsFilteredHistoryDataProvider, displayableRentersListStore,
	// availableDevicesListStore, displayableRentersFilterListStore,
	// displayableDevicesFilterListStore, allRentersDataProvider, allRentedDevicesDataProvider);
	// fetchData();
	//
	// RootPanel.get().add(mainPageBinder);
	// }
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		loginPageBinder = new LoginPageBinder(genericAfterAuthCallback);

		RootLayoutPanel.get().add(loginPageBinder);
	}

	private void loadMainPage() {

		mainPageBinder = new MainPageBinder(NewRentalSystem.this,
				availableDevicesDataProvider, unavailableDevicesDataProvider,
				eventsHistoryDataProvider, eventsFilteredHistoryDataProvider,
				displayableRentersListStore, availableDevicesListStore,
				displayableRentersFilterListStore, displayableDevicesFilterListStore,
				allRentersDataProvider, allRentedDevicesDataProvider,
				deviceNamesSuggestOracle, allDisplayableDevicesListStore);
		fetchData();

		RootLayoutPanel.get().remove(loginPageBinder);
		RootLayoutPanel.get().removeFromParent();
		RootPanel.get().add(mainPageBinder);
	}

	public void rentDevicesToExistingRenter(
			final AsyncCallback<Boolean> rentDevicesResultCallback, String renterMatrNr,
			String[] deviceImeiCodes, Date estimatedReturnDate, String comments,
			String signatureHTML) {

		AsyncCallback<Boolean> rentDevicesCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				// TODO Auto-generated method stub
				fetchEventsHistory();
				fetchAvailableDevices();
				fetchUnavailableDevices();
				fetchAllRenters();
				rentDevicesResultCallback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				rentDevicesResultCallback.onFailure(caught);

			}
		};
		if (this.currentSession != null)
			service.rentDevicesTo(currentSession.getSessionIdHash(), renterMatrNr,
					deviceImeiCodes, estimatedReturnDate, comments, signatureHTML,
					rentDevicesCallback);
	}

	public void rentDevicesToNewRenter(
			final AsyncCallback<Boolean> rentDevicesResultCallback,
			final SerializableRenter sr, final String renterMatrNr,
			final String[] deviceImeiCodes, final Date estimatedReturnDate,
			final String comments, final String signatureHTML) {

		AsyncCallback<Boolean> addNewRenterCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				rentDevicesToExistingRenter(rentDevicesResultCallback, renterMatrNr,
						deviceImeiCodes, estimatedReturnDate, comments, signatureHTML);
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error!", sr.getName() + " status is unknown!");
			}
		};

		if (this.currentSession != null)
			service.addRenter(currentSession.getSessionIdHash(), sr, addNewRenterCallback);

	}

	public void returnDevices(final AsyncCallback<Boolean> returnDevicesResultBCallback,
			String renterMatrNr, String[] imeiCodes, String comments, String signatureHTML) {
		AsyncCallback<Boolean> returnDevicesCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				fetchEventsHistory();
				fetchAvailableDevices();
				fetchUnavailableDevices();
				fetchAllRenters();
				returnDevicesResultBCallback.onSuccess(result);

			}

			@Override
			public void onFailure(Throwable caught) {
				returnDevicesResultBCallback.onFailure(caught);

			}
		};
		if (this.currentSession != null)
			service.returnDevices(currentSession.getSessionIdHash(), renterMatrNr,
					imeiCodes, comments, signatureHTML, returnDevicesCallback);
	}

	private void fetchData() {
		fetchAvailableDevices();
		fetchUnavailableDevices();
		fetchEventsHistory();

		fetchAllRenters();
		fetchAllDevices();

	}

	private void fetchAllDevices() {
		AsyncCallback<ArrayList<PersistentDevice>> callback = new AsyncCallback<ArrayList<PersistentDevice>>() {

			@Override
			public void onSuccess(ArrayList<PersistentDevice> result) {
				updateAllDevices(result);

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};
		if (this.currentSession != null)
			service.getAllDevices(currentSession.getSessionIdHash(), callback);
	}

	protected void updateAllDevices(ArrayList<PersistentDevice> result) {
		this.allDisplayableDevicesListStore.removeAll();
		if (result != null && result.size() > 0) {
			for (PersistentDevice pd : result) {
				String imgName = deviceNameToImageNameMap.get(pd.getName().toLowerCase()
						.trim());
				if (imgName == null)
					imgName = deviceNotFoundImage;
				DisplayableDevice dd = new DisplayableDevice(pd.getName(), pd.getIMEI(),
						imgName);
				this.allDisplayableDevicesListStore.add(dd);
			}
			this.allDisplayableDevicesListStore.fireEvent(Events.Update);
		}

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
		if (this.currentSession != null)
			service.getAllRenters(currentSession.getSessionIdHash(), callback);
	}

	protected void updateAllRenters(ArrayList<SerializableRenter> result) {
		displayableRentersListStore.removeAll();		
		allRentersDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			// allRentersDataProvider.getList().addAll(result);
			allRentersDataProvider.setList(result);
			for (SerializableRenter sr : result) {
				displayableRentersListStore.add(new DisplayableRenter(sr.getName(), sr
						.getMatriculationNumber()));
			}
		}
		allRentersDataProvider.refresh();
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
		if (this.currentSession != null)
			service.getEvents(currentSession.getSessionIdHash(), personName, IMEI, from,
					to, maxResultSize, reverseChronologicalOrder, callback);
	}

	protected void updateEventsHistoryFiltered(ArrayList<PersistentEvent> result) {

		eventsFilteredHistoryDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			eventsFilteredHistoryDataProvider.getList().addAll(result);
			CellTable<PersistentEvent> historyTable = (CellTable<PersistentEvent>) eventsFilteredHistoryDataProvider
					.getDataDisplays().toArray()[0];
			historyTable.setPageSize(eventsFilteredHistoryDataProvider.getList().size());
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
		if (this.currentSession != null)
			service.getEvents(currentSession.getSessionIdHash(), null, null, null, null,
					Integer.MAX_VALUE, true, callback);

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
			String imgName = deviceNameToImageNameMap.get(pe.getDevName().toLowerCase());
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
		displayableDevicesFilterListStore.removeAll();
		displayableRentersFilterListStore.removeAll();
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
		if (this.currentSession != null)
			service.getRentedDevices(currentSession.getSessionIdHash(), callback);

	}

	protected void updateUnavailableDevices(ArrayList<PersistentDevice> result) {
		unavailableDevicesDataProvider.getList().clear();
		allRentedDevicesDataProvider.getList().clear();
		if (result != null && result.size() > 0) {
			unavailableDevicesDataProvider.getList().addAll(result);
			allRentedDevicesDataProvider.getList().addAll(result);
			// allRentedDevicesDataProvider.setList(result);

			// Populate the device names oracle
			for (PersistentDevice pd : result) {
				deviceNamesSuggestOracle.add(pd.getName());
			}
		}
		allRentedDevicesDataProvider.refresh();
		unavailableDevicesDataProvider.refresh();
		// Update the rowcount of the table
		if (unavailableDevicesDataProvider.getDataDisplays().toArray()[0].getClass()
				.equals(CellTable.class)) {
			((CellTable<PersistentDevice>) unavailableDevicesDataProvider
					.getDataDisplays().toArray()[0])
					.setPageSize(unavailableDevicesDataProvider.getList().size());
		}

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
		if (this.currentSession != null)
			service.getAvailableDevices(currentSession.getSessionIdHash(), callback);

	}

	protected void updateAvailableDevices(ArrayList<PersistentDevice> result) {
		availableDevicesDataProvider.getList().clear();
		availableDevicesListStore.removeAll();
		if (result != null && result.size() > 0) {
			availableDevicesDataProvider.getList().addAll(result);

			for (PersistentDevice pd : result) {
				// Populate the device names oracle
				deviceNamesSuggestOracle.add(pd.getName());
				// Populate the available devices list store
				String imgName = deviceNameToImageNameMap.get(pd.getName().toLowerCase());
				if (imgName == null)
					imgName = deviceNotFoundImage;
				availableDevicesListStore.add(new DisplayableDevice(pd.getName(), pd
						.getIMEI(), imgName));
			}
		}

		availableDevicesDataProvider.refresh();
		// Update the rowcount of the table
		if (availableDevicesDataProvider.getDataDisplays().toArray()[0].getClass()
				.equals(CellTable.class)) {
			((CellTable<PersistentDevice>) availableDevicesDataProvider.getDataDisplays()
					.toArray()[0]).setPageSize(availableDevicesDataProvider.getList()
					.size());
		}

		if (mainPageBinder != null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList()
					.size(), unavailableDevicesDataProvider.getList().size());

	}

	public void addNewDevice(PersistentDevice pd,
			final AsyncCallback<Boolean> addDeviceResultCallback) {
		if (pd == null)
			return;

		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				fetchAvailableDevices();
				fetchAllDevices();
				addDeviceResultCallback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				addDeviceResultCallback.onFailure(caught);

			}
		};
		if (this.currentSession != null)
			service.addNewDevice(currentSession.getSessionIdHash(), pd, callback);

	}

	public void getPersistentDeviceByImei(String imei,
			final AsyncCallback<PersistentDevice> getPDcallback) {
		if (imei == null || imei.isEmpty() || getPDcallback == null)
			return;

		AsyncCallback<PersistentDevice> callback = new AsyncCallback<PersistentDevice>() {
			@Override
			public void onSuccess(PersistentDevice result) {
				getPDcallback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				getPDcallback.onFailure(caught);
			}
		};
		if (this.currentSession != null)
			service.getDeviceByImei(currentSession.getSessionIdHash(), imei, callback);
	}

	public void updateExistingDevice(PersistentDevice currentlyDIsplayedPD,
			final AsyncCallback<Boolean> updateDeviceCallback) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				updateDeviceCallback.onSuccess(result);
				fetchAllDevices();

			}

			@Override
			public void onFailure(Throwable caught) {
				updateDeviceCallback.onFailure(caught);

			}
		};
		if (this.currentSession != null)
			service.updateDeviceInfo(currentSession.getSessionIdHash(),
					currentlyDIsplayedPD, callback);
	}

	public void deleteDevice(PersistentDevice currentlyDIsplayedPD,
			final AsyncCallback<Boolean> deleteDeviceCallback) {
		if (currentlyDIsplayedPD == null || deleteDeviceCallback == null)
			return;

		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				fetchAllDevices();
				fetchUnavailableDevices();
				fetchAvailableDevices();
				deleteDeviceCallback.onSuccess(result);

			}

			@Override
			public void onFailure(Throwable caught) {
				deleteDeviceCallback.onFailure(caught);

			}
		};
		if (this.currentSession != null)
			service.deleteDevice(currentSession.getSessionIdHash(), currentlyDIsplayedPD,
					callback);
	}

	public void addNewStudent(SerializableRenter sr,
			final AsyncCallback<Boolean> addStudentCallback) {
		if (sr == null || addStudentCallback == null)
			return;

		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				addStudentCallback.onSuccess(result);
				fetchAllRenters();
			}

			@Override
			public void onFailure(Throwable caught) {
				addStudentCallback.onFailure(caught);
			}
		};
		if (this.currentSession != null)
			service.addRenter(currentSession.getSessionIdHash(), sr, callback);
	}

	public void getSerializableRenterByMatric(
			String matriculation,
			final AsyncCallback<SerializableRenter> displaySerializableRenterDetailsCallback) {
		if (matriculation == null || matriculation.isEmpty()
				|| displaySerializableRenterDetailsCallback == null) {
			return;
		}

		AsyncCallback<SerializableRenter> getSerializableRenterCallback = new AsyncCallback<SerializableRenter>() {

			@Override
			public void onSuccess(SerializableRenter result) {
				displaySerializableRenterDetailsCallback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				displaySerializableRenterDetailsCallback.onFailure(caught);
			}
		};
		if (this.currentSession != null)
			service.getRenter(currentSession.getSessionIdHash(), matriculation,
					getSerializableRenterCallback);
	}

	public void updateExistingRenter(String matriculationNumber, SerializableRenter sr,
			final AsyncCallback<Boolean> updateStudentInfocallback) {
		if (sr == null || updateStudentInfocallback == null) {
			return;
		}

		AsyncCallback<Boolean> updateRenterCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				updateStudentInfocallback.onSuccess(result);
				fetchAllRenters();
			}

			@Override
			public void onFailure(Throwable caught) {
				updateStudentInfocallback.onFailure(caught);
			}
		};
		if (this.currentSession != null)
			service.updateRenter(currentSession.getSessionIdHash(), matriculationNumber,
					sr, updateRenterCallback);
	}

}
