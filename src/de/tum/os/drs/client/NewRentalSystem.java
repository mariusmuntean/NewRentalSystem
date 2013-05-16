package de.tum.os.drs.client;

import java.util.ArrayList;
import java.util.Collection;

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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		serviceDef.setServiceEntryPoint(addr);
		
		mainPageBinder = new MainPageBinder(availableDevicesDataProvider,
				unavailableDevicesDataProvider, eventsHistoryDataProvider,
				eventsFilteredHistoryDataProvider, displayableRentersListStore);
		fetchData();
		
		RootLayoutPanel.get().add(mainPageBinder);
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
		if(result!= null && result.size()>0)
		{
			for(SerializableRenter sr:result){
				displayableRentersListStore.add(new DisplayableRenter(sr.getName(), sr.getMatriculationNumber()));
			}
		}
		
//		displayableRentersListStore
		
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
		
		if(mainPageBinder!= null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList().size(), unavailableDevicesDataProvider.getList().size());
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
		}

		availableDevicesDataProvider.refresh();
		
		if(mainPageBinder!= null)
			mainPageBinder.setRentedVsAvailable(availableDevicesDataProvider.getList().size(), unavailableDevicesDataProvider.getList().size());
	}
}
