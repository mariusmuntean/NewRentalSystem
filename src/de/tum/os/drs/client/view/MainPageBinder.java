package de.tum.os.drs.client.view;

import java.util.Comparator;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
// import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;

public class MainPageBinder extends Composite implements HasText {

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

	PieChart pChartRentedVsAvailable;
	/*
	 * Stores region
	 */
	private ListStore<DisplayableRenter> displayableRentersListStore;

	/*
	 * Data providers
	 */
	private ListDataProvider<PersistentDevice> availableDevicesDataProvider;
	private ListDataProvider<PersistentDevice> unavailableDevicesDataProvider;
	private ListDataProvider<PersistentEvent> eventsHistoryDataProvider;
	private ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider;

	public MainPageBinder(
			ListDataProvider<PersistentDevice> availableDevicesDataProvider,
			ListDataProvider<PersistentDevice> unavailableDevicesDataProvider,
			ListDataProvider<PersistentEvent> eventsHistoryDataProvider,
			ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider) {

		this.availableDevicesDataProvider = availableDevicesDataProvider;
		this.unavailableDevicesDataProvider = unavailableDevicesDataProvider;
		this.eventsHistoryDataProvider = eventsHistoryDataProvider;
		this.eventsFilteredHistoryDataProvider = eventsFilteredHistoryDataProvider;

		instantiateControls();
		initWidget(uiBinder.createAndBindUi(this));
		injectPieChart();
		deckPanelActualView.showWidget(0);
	}

	public MainPageBinder(String firstName) {
		instantiateControls();

		initWidget(uiBinder.createAndBindUi(this));
	}

	private void instantiateControls() {
		displayableRentersListStore = new ListStore<DisplayableRenter>();
		// cBoxRegisteredStudent = new ComboBox<DisplayableRenter>();
		// cBoxRegisteredStudent.setStore(displayableRentersListStore);

		this.tableAvailableDevices = getDevicesTableFromDataprovider(availableDevicesDataProvider);
		this.tableUnavailableDevices = getDevicesTableFromDataprovider(unavailableDevicesDataProvider);

		this.tableHistoryOverview = getCellTableHistory(eventsHistoryDataProvider);

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

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

}
