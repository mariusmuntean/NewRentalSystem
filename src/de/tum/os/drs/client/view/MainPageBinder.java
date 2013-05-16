package de.tum.os.drs.client.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
// import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;

public class MainPageBinder extends Composite implements HasText, ClickHandler,
		Listener<BaseEvent>, ChangeHandler {

	private static MainPageBinderUiBinder uiBinder = GWT
			.create(MainPageBinderUiBinder.class);

	interface MainPageBinderUiBinder extends UiBinder<Widget, MainPageBinder> {
	}

	/*
	 * UiFields region
	 */

	@UiField(provided = true)
	CellTable<PersistentDevice> tableAvailableDevices;

	@UiField(provided = true)
	CellTable<PersistentDevice> tableUnavailableDevices;

	// @UiField(provided = true)
	// PieChart pieChartAvailableVsRentedDevices;

	@UiField(provided = true)
	CellTable<PersistentEvent> tableHistoryOverview;

	// @UiField(provided = true)
	// ComboBox<DisplayableRenter> cBoxRegisteredStudent;

	@UiField
	DeckPanel deckPanelActualView;

	@UiField
	HorizontalPanel hPanelOverview;

	@UiField
	RadioButton rBtnOverview, rBtnRent, rBtnReturn, rBtnHistory, rBtnManage;

	PieChart pChartRentedVsAvailable;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxRentRegisteredStudentName;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxRentRegisteredStudentMatriculation;

	@UiField
	TextBox tBoxRentNewStudentName;

	@UiField
	TextBox tBoxRentNewStudentEmail;

	@UiField
	TextBox tBoxRentNewStudentPhone;

	@UiField
	TextBox tBoxRentNewStudentMatric;
	
	@UiField(provided=true)
	ComboBox<DisplayableDevice> cBoxRentSelectDevice;
	
	@UiField(provided=true)
	ListView<DisplayableDevice> lstViewRentSelectedDevices;

	/*
	 * Stores region
	 */
	private ListStore<DisplayableRenter> displayableRentersListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> availableDevicesListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableDevice> selectedDevicesListStore = new ListStore<DisplayableDevice>();

	/*
	 * Data providers
	 */
	private ListDataProvider<PersistentDevice> availableDevicesDataProvider;
	private ListDataProvider<PersistentDevice> unavailableDevicesDataProvider;
	private ListDataProvider<PersistentEvent> eventsHistoryDataProvider;
	private ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider;

	final String rentStudentComboTemplate = new String("<table>"
			+ "<tr><td>{name}</td></tr>" + "<tr><td>{matriculation}</td></tr>"
			+ "</table>");
	
	final String displayableDeviceTemplate = new String(
			"<table>"
			+ "<tr>"
			+ "<td rowspan=\"2\"><img src=\"images/devices/{imgName}\" width=\"80\" height=\"80\"></td>"
			+ "<td>{name}</td>" + "</tr>" + "<tr>" + "<td>{imei}</td>" + "</tr>"
			+ "</table>");

	public MainPageBinder(
			ListDataProvider<PersistentDevice> availableDevicesDataProvider,
			ListDataProvider<PersistentDevice> unavailableDevicesDataProvider,
			ListDataProvider<PersistentEvent> eventsHistoryDataProvider,
			ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider,
			ListStore<DisplayableRenter> displayableRentersListStore,
			ListStore<DisplayableDevice> availableDevicesListStore) {

		this.availableDevicesDataProvider = availableDevicesDataProvider;
		this.unavailableDevicesDataProvider = unavailableDevicesDataProvider;
		this.eventsHistoryDataProvider = eventsHistoryDataProvider;
		this.eventsFilteredHistoryDataProvider = eventsFilteredHistoryDataProvider;
		this.displayableRentersListStore = displayableRentersListStore;
		this.availableDevicesListStore = availableDevicesListStore;

		instantiateControls();
		initWidget(uiBinder.createAndBindUi(this));
		wireUpControls();

		injectPieChart();

		deckPanelActualView.showWidget(0);
	}

	public MainPageBinder(String firstName) {
		instantiateControls();

		initWidget(uiBinder.createAndBindUi(this));
	}

	private void instantiateControls() {
		// Overview page
		this.tableAvailableDevices = getDevicesTableFromDataprovider(availableDevicesDataProvider);
		this.tableUnavailableDevices = getDevicesTableFromDataprovider(unavailableDevicesDataProvider);
		this.tableHistoryOverview = getCellTableHistory(eventsHistoryDataProvider);

		// Rent page
		cBoxRentRegisteredStudentName = new ComboBox<DisplayableRenter>();
		cBoxRentRegisteredStudentName.setSimpleTemplate(rentStudentComboTemplate);
		cBoxRentRegisteredStudentName.setStore(displayableRentersListStore);
		cBoxRentRegisteredStudentName.setTriggerAction(TriggerAction.ALL);

		cBoxRentRegisteredStudentMatriculation = new ComboBox<DisplayableRenter>();
		cBoxRentRegisteredStudentMatriculation.setSimpleTemplate(rentStudentComboTemplate);
		cBoxRentRegisteredStudentMatriculation.setStore(displayableRentersListStore);
		cBoxRentRegisteredStudentMatriculation.setTriggerAction(TriggerAction.ALL);
		
		cBoxRentSelectDevice = new ComboBox<DisplayableDevice>();
		cBoxRentSelectDevice.setSimpleTemplate(displayableDeviceTemplate);
		cBoxRentSelectDevice.setStore(availableDevicesListStore);
		cBoxRentSelectDevice.setTriggerAction(TriggerAction.ALL);
		
		lstViewRentSelectedDevices = new ListView<DisplayableDevice>();
		lstViewRentSelectedDevices.setSimpleTemplate(displayableDeviceTemplate);
//		selectedDevicesListStore.add(new ArrayList<DisplayableDevice>());
		lstViewRentSelectedDevices.setStore(selectedDevicesListStore);
	}

	private void wireUpControls() {
		// Radio buttons for changing between pages
		rBtnHistory.addClickHandler(this);
		rBtnManage.addClickHandler(this);
		rBtnOverview.addClickHandler(this);
		rBtnRent.addClickHandler(this);
		rBtnReturn.addClickHandler(this);

		// Rent page
		cBoxRentRegisteredStudentMatriculation.addListener(Events.OnChange, this);
		cBoxRentRegisteredStudentName.addListener(Events.OnChange, this);
		tBoxRentNewStudentEmail.addChangeHandler(this);

	}

	private void injectPieChart() {
		DataTable data = getDevicePieChartData(4, 4);
		PieOptions options = getDevicePieChartOptions(400, 300);
		pChartRentedVsAvailable = new PieChart(data, options);

		hPanelOverview.insert(pChartRentedVsAvailable, 0);
		hPanelOverview.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	}

	private DataTable getDevicePieChartData(int availableQuantity, int rentedQuantity) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Devices");
		data.addColumn(ColumnType.NUMBER, "Rented vs. Available");
		data.addRows(2);
		data.setValue(0, 0, "Rented");
		data.setValue(0, 1, Math.abs(rentedQuantity));
		data.setValue(1, 0, "Available");
		data.setValue(1, 1, Math.abs(availableQuantity));

		return data;
	}

	private PieOptions getDevicePieChartOptions(int width, int height) {
		PieOptions options = PieChart.PieOptions.create();
		options.setHeight(height);
		options.setWidth(width);
		options.setTitle("Available vs. rented devices");

		TextStyle ts = TextStyle.create();
		ts.setFontSize(24);

		options.setColors(new String[] { "red", "blue" });

		options.setTitleTextStyle(ts);
		options.set3D(true);

		return options;
	}

	private CellTable<PersistentDevice> getDevicesTableFromDataprovider(
			ListDataProvider<PersistentDevice> lstDataProvider) {
		CellTable<PersistentDevice> newTable = new CellTable<PersistentDevice>();
		newTable.setTableLayoutFixed(true);

		// Create name column.
		TextColumn<PersistentDevice> nameColumn = new TextColumn<PersistentDevice>() {
			@Override
			public String getValue(PersistentDevice persistentDevice) {
				return persistentDevice.getName();
			}
		};

		// Create IMEI column.
		TextColumn<PersistentDevice> imeiColumn = new TextColumn<PersistentDevice>() {
			@Override
			public String getValue(PersistentDevice persistentDevice) {
				return persistentDevice.getIMEI();
			}
		};

		// Create state column.
		TextColumn<PersistentDevice> stateColumn = new TextColumn<PersistentDevice>() {
			@Override
			public String getValue(PersistentDevice persistentDevice) {
				return persistentDevice.getState();
			}
		};

		// Create Description column.
		TextColumn<PersistentDevice> descColumn = new TextColumn<PersistentDevice>() {
			@Override
			public String getValue(PersistentDevice persistentDevice) {
				return persistentDevice.getDescription();
			}
		};

		// Create name column.
		TextColumn<PersistentDevice> deviceTypeColumn = new TextColumn<PersistentDevice>() {
			@Override
			public String getValue(PersistentDevice persistentDevice) {
				return persistentDevice.getDeviceType().toString();
			}
		};

		// Make columns sortable
		nameColumn.setSortable(true);
		imeiColumn.setSortable(true);
		deviceTypeColumn.setSortable(true);

		// Add columns to table
		newTable.addColumn(nameColumn, "Name");
		newTable.addColumn(imeiColumn, "IMEI");
		newTable.addColumn(deviceTypeColumn, "Type");
		newTable.addColumn(stateColumn, "Condition");
		newTable.addColumn(descColumn, "Description");

		// Create a data provider.
		// moved up as a class member.

		// Connect the table to the data provider.
		lstDataProvider.addDataDisplay(newTable);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<PersistentDevice> list = lstDataProvider.getList();
		// for (PersistentDevice pd : getDummyDeviceList()) {
		// list.add(pd);
		// }

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentDevice> columnSortHandler = new ListHandler<PersistentDevice>(
				list);
		columnSortHandler.setComparator(nameColumn, new Comparator<PersistentDevice>() {
			public int compare(PersistentDevice p1, PersistentDevice p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the name columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getName().compareTo(p2.getName()) : 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(columnSortHandler);

		// We know that the data is sorted alphabetically by default.
		newTable.getColumnSortList().push(nameColumn);

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentDevice> imeiSortHandler = new ListHandler<PersistentDevice>(
				list);
		columnSortHandler.setComparator(imeiColumn, new Comparator<PersistentDevice>() {
			public int compare(PersistentDevice p1, PersistentDevice p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the name columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getIMEI().compareTo(p2.getIMEI()) : 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(imeiSortHandler);

		return newTable;
	}

	private CellTable<PersistentEvent> getCellTableHistory(
			ListDataProvider<PersistentEvent> lstDataProvider) {
		CellTable<PersistentEvent> newTable = new CellTable<PersistentEvent>();
		newTable.setTableLayoutFixed(true);

		// Create name column.
		TextColumn<PersistentEvent> nameColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getPersName() + "("
						+ persistentEvent.getPersMatriculationNumber() + ")";
			}
		};

		// Create Email column.
		TextColumn<PersistentEvent> emailColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getEmail();
			}
		};

		// Create Date column.
		TextColumn<PersistentEvent> dateColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getEventDate().toString();
			}
		};

		// Create EventType column.
		TextColumn<PersistentEvent> eventTypeColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getEventType().toString();
			}
		};

		// Create Device name column.
		TextColumn<PersistentEvent> deviceNameColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getDevName() + "(" + persistentEvent.getImeiCode()
						+ ")";
			}
		};

		// Make columns sortable
		nameColumn.setSortable(true);
		emailColumn.setSortable(true);
		dateColumn.setSortable(true);
		eventTypeColumn.setSortable(true);

		// Add columns to table
		newTable.addColumn(nameColumn, "Student Name");
		newTable.addColumn(emailColumn, "Email");
		newTable.addColumn(dateColumn, "Date");
		newTable.addColumn(eventTypeColumn, "Event");
		newTable.addColumn(deviceNameColumn, "Device name");

		// Create a data provider.
		// moved up as a class member.

		// Connect the table to the data provider.
		lstDataProvider.addDataDisplay(newTable);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<PersistentEvent> list = lstDataProvider.getList();
		// for (PersistentDevice pd : getDummyDeviceList()) {
		// list.add(pd);
		// }

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentEvent> columnSortHandler = new ListHandler<PersistentEvent>(
				list);
		columnSortHandler.setComparator(nameColumn, new Comparator<PersistentEvent>() {
			public int compare(PersistentEvent p1, PersistentEvent p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the name columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getPersName().compareTo(p2.getPersName())
							: 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(columnSortHandler);

		// We know that the data is sorted alphabetically by default.
		newTable.getColumnSortList().push(nameColumn);

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentEvent> emailColumnSortHandler = new ListHandler<PersistentEvent>(
				list);
		columnSortHandler.setComparator(emailColumn, new Comparator<PersistentEvent>() {
			public int compare(PersistentEvent p1, PersistentEvent p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the name columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getEmail().compareTo(p2.getEmail()) : 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(emailColumnSortHandler);

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentEvent> dateColumnSortHandler = new ListHandler<PersistentEvent>(
				list);
		columnSortHandler.setComparator(dateColumn, new Comparator<PersistentEvent>() {
			public int compare(PersistentEvent p1, PersistentEvent p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the name columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getEventDate().compareTo(p2.getEventDate())
							: 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(dateColumnSortHandler);

		// Add a ColumnSortEvent.ListHandler to connect sorting to the
		// java.util.List.
		ListHandler<PersistentEvent> eventTypeColumnSortHandler = new ListHandler<PersistentEvent>(
				list);
		columnSortHandler.setComparator(emailColumn, new Comparator<PersistentEvent>() {
			public int compare(PersistentEvent p1, PersistentEvent p2) {
				if (p1 == p2) {
					return 0;
				}

				// if (p1 != null) {
				// return (p2 != null) ? ((p1.getEventType().index() < p2.getEventType()
				// .index()) ? -1 : (p1.getEventType().index() == p2
				// .getEventType().index() ? 0 : 1)) : 1;
				// }
				if (p1 != null) {
					return (p2 != null) ? ((p1.getEventType().toString().compareTo(p2
							.getEventType().toString()))) : 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(eventTypeColumnSortHandler);

		return newTable;
	}

	public void setRentedVsAvailable(int availableDevices, int rentedDevices) {
		DataTable newData = getDevicePieChartData(availableDevices, rentedDevices);
		PieOptions options = getDevicePieChartOptions(400, 300);
		pChartRentedVsAvailable.draw(newData, options);
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = null;
		try {
			sender = (Widget) event.getSource();
		} catch (Exception e) {

		}
		if (sender == null)
			return;

		// A little unsafe. Make sure there really exist enough widgets such that the index is right.
		if (sender == rBtnOverview)
			deckPanelActualView.showWidget(0);
		if (sender == rBtnRent)
			deckPanelActualView.showWidget(1);
		if (sender == rBtnReturn)
			deckPanelActualView.showWidget(2);
		if (sender == rBtnHistory)
			deckPanelActualView.showWidget(3);
		if (sender == rBtnManage)
			deckPanelActualView.showWidget(4);

	}

	@Override
	public void handleEvent(BaseEvent be) {
		Object sender = be.getSource();
		if (be.getType() == Events.OnChange) {
			if (sender == cBoxRentRegisteredStudentMatriculation) {
				cBoxRentRegisteredStudentName
						.setValue(cBoxRentRegisteredStudentMatriculation.getSelection()
								.get(0));
				resetNewStudentFields();
			}
			if (sender == cBoxRentRegisteredStudentName) {
				cBoxRentRegisteredStudentMatriculation
						.setValue(cBoxRentRegisteredStudentName.getSelection().get(0));
				resetNewStudentFields();
			}
		}

	}

	private void resetNewStudentFields() {
		tBoxRentNewStudentEmail.setText("");
		tBoxRentNewStudentMatric.setText("");
		tBoxRentNewStudentName.setText("");
		tBoxRentNewStudentPhone.setText("");
	}

	@Override
	public void onChange(ChangeEvent event) {
		Object sender = event.getSource();
		if (sender == tBoxRentNewStudentEmail || sender == tBoxRentNewStudentMatric
				|| sender == tBoxRentNewStudentName || sender == tBoxRentNewStudentPhone) {
			resetSelectedStudent();
		}

	}

	private void resetSelectedStudent() {
		cBoxRentRegisteredStudentMatriculation.setRawValue("");
		cBoxRentRegisteredStudentName.setRawValue("");
		
	}
}
