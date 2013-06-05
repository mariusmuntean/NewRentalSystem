package de.tum.os.drs.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.sun.java.swing.plaf.windows.resources.windows;

import de.tum.os.drs.client.helpers.CookieHelper;
import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.FacebookAuthenticator;
import de.tum.os.drs.client.model.GoogleAuthenticator;
import de.tum.os.drs.client.model.OAuthAuthorities;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.SerializableRenter;
import de.tum.os.drs.client.model.TwitterAuthenticator;
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

	Callback<String, Throwable> facebookCallback = new Callback<String, Throwable>() {
		@Override
		public void onSuccess(String token) {

			System.out.println("Facebook Token: " + token);
			CookieHelper.resetOAuthAuthority();
			CookieHelper.resetAuthToken();

			CookieHelper.setAuthCookie(OAuthAuthorities.facebook, token);

			// Window.alert("Token: "+token);
			loadMainPage();
		}

		@Override
		public void onFailure(Throwable caught) {
			// The authentication process failed for some reason, see caught.getMessage()
			Window.alert("error: " + caught.getMessage());
		}
	};

	Callback<String, Throwable> googleCallback = new Callback<String, Throwable>() {
		@Override
		public void onSuccess(String token) {

			System.out.println("Google Token: " + token);

			CookieHelper.resetOAuthAuthority();
			CookieHelper.resetAuthToken();

			CookieHelper.setAuthCookie(OAuthAuthorities.google, token);

			// Window.alert("Token: "+token);
			loadMainPage();
		}

		@Override
		public void onFailure(Throwable caught) {
			// The authentication process failed for some reason, see caught.getMessage()
			Window.alert("error: " + caught.getMessage());
		}
	};

	Callback<String, Throwable> twitterCallback = new Callback<String, Throwable>() {
		@Override
		public void onSuccess(String token) {

			System.out.println("Twitter Token: " + token);

			CookieHelper.resetOAuthAuthority();
			CookieHelper.resetAuthToken();

			CookieHelper.setAuthCookie(OAuthAuthorities.twitter, token);

			// Window.alert("Token: "+token);
			loadMainPage();
		}

		@Override
		public void onFailure(Throwable caught) {
			// The authentication process failed for some reason, see caught.getMessage()
			Window.alert("error: " + caught.getMessage());
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

		loginPageBinder = new LoginPageBinder(facebookCallback, googleCallback,
				twitterCallback);

		RootLayoutPanel.get().add(loginPageBinder);
	}

	private void loadMainPage() {
		// You now have the OAuth2 token needed to sign authenticated requests.
		serviceDef.setServiceEntryPoint(addr);

		mainPageBinder = new MainPageBinder(NewRentalSystem.this,
				availableDevicesDataProvider, unavailableDevicesDataProvider,
				eventsHistoryDataProvider, eventsFilteredHistoryDataProvider,
				displayableRentersListStore, availableDevicesListStore,
				displayableRentersFilterListStore, displayableDevicesFilterListStore,
				allRentersDataProvider, allRentedDevicesDataProvider, deviceNamesSuggestOracle);
		fetchData();

		RootLayoutPanel.get().remove(loginPageBinder);
		RootLayoutPanel.get().removeFromParent();
		RootPanel.get().add(mainPageBinder);
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
				fetchAllRenters();
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
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};

		service.addRenter(sr, addNewRenterCallback);

	}

	public void returnDevices(String renterMatrNr, String[] imeiCodes, String comments,
			String signatureHTML) {
		AsyncCallback<Boolean> returnDevicesCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				fetchEventsHistory();
				fetchAvailableDevices();
				fetchUnavailableDevices();
				fetchAllRenters();

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};
		service.returnDevices(renterMatrNr, imeiCodes, comments, signatureHTML,
				returnDevicesCallback);
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

		if (mainPageBinder != null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList()
					.size(), unavailableDevicesDataProvider.getList().size());

	}
	
	public void addNewDevice(PersistentDevice pd){
		if(pd == null)
			return;
		
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				fetchAvailableDevices();
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		};
		
		service.addNewDevice(pd, callback);
		
	}
	
}
