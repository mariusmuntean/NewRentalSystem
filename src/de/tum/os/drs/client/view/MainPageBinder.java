package de.tum.os.drs.client.view;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import de.tum.os.drs.client.NewRentalSystem;
import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.PersistentDevice;
import de.tum.os.drs.client.model.PersistentEvent;
import de.tum.os.drs.client.model.RenterTreeViewModel;
import de.tum.os.drs.client.model.SerializableRenter;

public class MainPageBinder extends Composite implements HasText, ClickHandler,
		Listener<BaseEvent>, ChangeHandler, MouseDownHandler, MouseMoveHandler,
		MouseUpHandler, MouseOutHandler, MouseOverHandler {

	private static MainPageBinderUiBinder uiBinder = GWT
			.create(MainPageBinderUiBinder.class);

	interface MainPageBinderUiBinder extends UiBinder<Widget, MainPageBinder> {
	}

	/*
	 * UiFields region
	 */

	// @UiField
	// DockLayoutPanel docLayoutPanelMain;

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

	@UiField(provided = true)
	ComboBox<DisplayableDevice> cBoxRentSelectDevice;

	@UiField(provided = true)
	ListView<DisplayableDevice> lstViewRentSelectedDevices;

	@UiField
	Button btnRent;

	@UiField
	Button btnRentClearRentedList;

	@UiField
	TextArea txtAreaRentComments;

	@UiField(provided = true)
	DrawingArea canvasRentSignature;

	Path currentSignaturePath;
	boolean signing = false;

	@UiField
	Button btnRentClearSignature;

	@UiField
	Grid gridRentDevices;

	@UiField
	Button btnSubmitRentEvent;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxHistoryFilterName;

	@UiField(provided = true)
	ComboBox<DisplayableDevice> cBoxHistoryFilterImei;

	@UiField
	DatePicker datePickerHistoryFilterTo;

	@UiField
	DatePicker datePickerHistoryFilterFrom;

	@UiField
	TextBox txtBoxHistoryFilterFrom;

	@UiField
	TextBox txtBoxHistoryFilterTo;

	@UiField(provided = true)
	CellTable tableHistoryEventsFiltered;

	@UiField(provided = true)
	CellBrowser cellBrowserReturn;

	@UiField
	Label lblRentSelectedDevices;

	/*
	 * Stores region
	 */
	private ListStore<DisplayableRenter> displayableRentersListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> availableDevicesListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableDevice> selectedDevicesListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableRenter> displayableRentersFilterListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> displayableDevicesFilterListStore = new ListStore<DisplayableDevice>();

	/*
	 * Data providers
	 */
	private ListDataProvider<PersistentDevice> availableDevicesDataProvider;
	private ListDataProvider<PersistentDevice> unavailableDevicesDataProvider;
	private ListDataProvider<PersistentEvent> eventsHistoryDataProvider;
	private ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider;
	private ListDataProvider<SerializableRenter> allRentersDataProvider;
	private ListDataProvider<PersistentDevice> allRentedDevicesDataProvider;

	/*
	 * Templates
	 */
	final String rentStudentComboTemplate = new String("<table>"
			+ "<tr><td>{name}</td></tr>" + "<tr><td>{matriculation}</td></tr>"
			+ "</table>");

	final String displayableDeviceTemplate = new String(
			"<table>"
					+ "<tr>"
					+ "<td rowspan=\"2\"><img src=\"images/devices/{imgName}\" width=\"80\" height=\"80\"></td>"
					+ "<td>{name}</td>" + "</tr>" + "<tr>" + "<td>{imei}</td>" + "</tr>"
					+ "</table>");

	private NewRentalSystem client;

	public MainPageBinder(NewRentalSystem client,
			ListDataProvider<PersistentDevice> availableDevicesDataProvider,
			ListDataProvider<PersistentDevice> unavailableDevicesDataProvider,
			ListDataProvider<PersistentEvent> eventsHistoryDataProvider,
			ListDataProvider<PersistentEvent> eventsFilteredHistoryDataProvider,
			ListStore<DisplayableRenter> displayableRentersListStore,
			ListStore<DisplayableDevice> availableDevicesListStore,
			ListStore<DisplayableRenter> displayableRentersFilterListStore,
			ListStore<DisplayableDevice> displayableDevicesFilterListStore,
			ListDataProvider<SerializableRenter> allRentersDataProvider,
			ListDataProvider<PersistentDevice> allRentedDevicesDataProvider) {

		this.client = client;
		this.availableDevicesDataProvider = availableDevicesDataProvider;
		this.unavailableDevicesDataProvider = unavailableDevicesDataProvider;
		this.eventsHistoryDataProvider = eventsHistoryDataProvider;
		this.eventsFilteredHistoryDataProvider = eventsFilteredHistoryDataProvider;
		this.displayableRentersListStore = displayableRentersListStore;
		this.availableDevicesListStore = availableDevicesListStore;
		this.displayableRentersFilterListStore = displayableRentersFilterListStore;
		this.displayableDevicesFilterListStore = displayableDevicesFilterListStore;
		this.allRentersDataProvider = allRentersDataProvider;
		this.allRentedDevicesDataProvider = allRentedDevicesDataProvider;

		instantiateControls();
		initWidget(uiBinder.createAndBindUi(this));
		// docLayoutPanelMain.getWidgetContainerElement(deckPanelActualView).getStyle().setOverflowY(Overflow.AUTO);
		// docLayoutPanelMain.getElement().getStyle().setOverflowY(Overflow.AUTO);
		// deckPanelActualView.setHeight("1000px");
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
		cBoxRentRegisteredStudentMatriculation
				.setSimpleTemplate(rentStudentComboTemplate);
		cBoxRentRegisteredStudentMatriculation.setStore(displayableRentersListStore);
		cBoxRentRegisteredStudentMatriculation.setTriggerAction(TriggerAction.ALL);

		cBoxRentSelectDevice = new ComboBox<DisplayableDevice>();
		cBoxRentSelectDevice.setSimpleTemplate(displayableDeviceTemplate);
		cBoxRentSelectDevice.setStore(availableDevicesListStore);
		cBoxRentSelectDevice.setTriggerAction(TriggerAction.ALL);

		lstViewRentSelectedDevices = new ListView<DisplayableDevice>();
		lstViewRentSelectedDevices.setSimpleTemplate(displayableDeviceTemplate);
		lstViewRentSelectedDevices.setStore(selectedDevicesListStore);
		canvasRentSignature = new DrawingArea(500, 300);

		// Return page
		RenterTreeViewModel rtvm = new RenterTreeViewModel(allRentersDataProvider,
				allRentedDevicesDataProvider);
		cellBrowserReturn = new CellBrowser(rtvm, null);

		// History page
		cBoxHistoryFilterName = new ComboBox<DisplayableRenter>();
		cBoxHistoryFilterName.setSimpleTemplate(rentStudentComboTemplate);
		cBoxHistoryFilterName.setStore(displayableRentersFilterListStore);

		cBoxHistoryFilterImei = new ComboBox<DisplayableDevice>();
		cBoxHistoryFilterImei.setStore(displayableDevicesFilterListStore);
		tableHistoryEventsFiltered = getCellTableHistory(eventsFilteredHistoryDataProvider);

	}

	private void wireUpControls() {
		// Radio buttons for changing between pages
		rBtnHistory.addClickHandler(this);
		rBtnManage.addClickHandler(this);
		rBtnOverview.addClickHandler(this);
		rBtnRent.addClickHandler(this);
		rBtnReturn.addClickHandler(this);

		// Rent page
		registeredStudentChanged rsc = new registeredStudentChanged();
		cBoxRentRegisteredStudentMatriculation.addSelectionChangedListener(rsc);
		cBoxRentRegisteredStudentName.addSelectionChangedListener(rsc);
		tBoxRentNewStudentEmail.addChangeHandler(this);
		btnRent.addListener(Events.OnClick, this);
		btnRentClearRentedList.addListener(Events.OnClick, this);
		injectSignatureCanvasFrame();
		canvasRentSignature.addMouseDownHandler(this);
		canvasRentSignature.addMouseMoveHandler(this);
		canvasRentSignature.addMouseUpHandler(this);
		canvasRentSignature.addMouseOverHandler(this);
		canvasRentSignature.addMouseOutHandler(this);
		btnRentClearSignature.addListener(Events.OnClick, this);

		// Return page
		// cellBrowserReturn.

		// Fix the colspan for the comments area and the signature area.
		Element element = gridRentDevices.getCellFormatter().getElement(1, 0);
		DOM.setElementAttribute(element, "colspan", "2");

		Element element2 = gridRentDevices.getCellFormatter().getElement(1, 1);
		DOM.setElementAttribute(element2, "colspan", "2");

		btnSubmitRentEvent.addListener(Events.OnClick, this);

		// History page
		cBoxHistoryFilterName.addSelectionChangedListener(rsc);
		rentedDeviceChanged rdc = new rentedDeviceChanged();
		cBoxHistoryFilterImei.addSelectionChangedListener(rdc);

		datePickerHistoryFilterFrom.addListener(Events.Select,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						String dateText = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
								.format(datePickerHistoryFilterFrom.getValue());
						txtBoxHistoryFilterFrom.setText(dateText);
						fetchEventsHistoryFiltered();
					}
				});
		txtBoxHistoryFilterFrom.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String dateTimeString = txtBoxHistoryFilterFrom.getText();
				System.out.println("Parsing text: " + dateTimeString);
				if (dateTimeString == null || dateTimeString.length() < 1)
					return;
				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
				Date newDate = null;
				try {
					newDate = dtf.parse(dateTimeString);
				} catch (Exception e) {

				}
				if (newDate != null) {
					datePickerHistoryFilterFrom.setValue(newDate, true);
					fetchEventsHistoryFiltered();
				}

			}
		});
		datePickerHistoryFilterTo.addListener(Events.Select,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						String dateText = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss")
								.format(datePickerHistoryFilterTo.getValue());
						txtBoxHistoryFilterTo.setText(dateText);
						fetchEventsHistoryFiltered();
					}
				});

		txtBoxHistoryFilterTo.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String dateTimeString = txtBoxHistoryFilterTo.getText();
				System.out.println("Parsing text: " + dateTimeString);
				if (dateTimeString == null || dateTimeString.length() < 1)
					return;
				DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
				Date newDate = null;
				try {
					newDate = dtf.parse(dateTimeString);
				} catch (Exception e) {

				}
				if (newDate != null) {
					datePickerHistoryFilterTo.setValue(newDate, true);
					fetchEventsHistoryFiltered();
				}

			}
		});
	}

	private void injectSignatureCanvasFrame() {
		Path path = new Path(0, 0);
		path.lineRelativelyTo(canvasRentSignature.getWidth(), 0);
		path.lineRelativelyTo(0, canvasRentSignature.getHeight());
		path.lineRelativelyTo(-canvasRentSignature.getWidth(), 0);
		path.close();
		path.setStrokeWidth(3);
		path.setStrokeColor("#7CFC00");
		canvasRentSignature.add(path);

	}

	private void injectPieChart() {
		DataTable data = getDevicePieChartData(4, 4);
		PieOptions options = getDevicePieChartOptions(300, 300);
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
		ChartArea ca = ChartArea.create();
		ca.setLeft("20");
		ca.setTop("20");
		ca.setWidth("100%");
		ca.setHeight("100%");
		options.setChartArea(ca);
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
		PieOptions options = getDevicePieChartOptions(300, 300);
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

		if (be.getType() == Events.OnClick) {
			if (sender == btnRent) {
				markDeviceForRent();
			}

			if (sender == btnRentClearRentedList) {
				unMarkDeviceForRent();
			}

			if (sender == btnRentClearSignature) {
				clearSignatureCanvas();
			}

			if (sender == btnSubmitRentEvent) {
				submitRentEvent();
			}
		}

	}

	private void submitRentEvent() {
		// Create or determine Renter
		SerializableRenter sr = null;
		boolean haveRenter = false;

		// Determine if an existing Renter was selected
		DisplayableRenter dr = null;
		if (cBoxRentRegisteredStudentMatriculation.getSelection() != null
				&& cBoxRentRegisteredStudentMatriculation.getSelection().size() > 0) {
			dr = cBoxRentRegisteredStudentMatriculation.getSelection().get(0);
		}
		String renterMatr = dr == null ? "" : dr.getMatriculation();
		if (!renterMatr.trim().equals("") && renterMatr.trim().length() > 0) {
			haveRenter = true;
		}

		// If no existing renter was selected maybe a new one should be created. Check the fields.
		String newRenterName = tBoxRentNewStudentName.getText();
		String newRenterEmail = tBoxRentNewStudentEmail.getText();
		final String newRenterMatriculation = tBoxRentNewStudentMatric.getText();
		String newRenterPhone = tBoxRentNewStudentPhone.getText();

		if (haveRenter == false && newRenterEmail != null && !newRenterEmail.isEmpty()
				&& newRenterMatriculation != null && !newRenterMatriculation.isEmpty()
				&& newRenterName != null && !newRenterName.isEmpty()
				&& newRenterPhone != null && !newRenterPhone.isEmpty()) {

			haveRenter = true;
			sr = new SerializableRenter();
			sr.setName(newRenterName);
			sr.setMatriculationNumber(newRenterMatriculation);
			sr.setEmail(newRenterEmail);
			sr.setPhoneNUmber(newRenterPhone);
		}

		// If no renter was selected and no new Renter added just leave. It was a prank call to this method :D.
		if (haveRenter == false)
			return;

		// OK, if I have a renter let's get a list of devices that should be added to his "rented list".
		// Leave if no devices were added to the list of devices to rent.
		if (lstViewRentSelectedDevices.getStore().getCount() == 0)
			return;

		List<DisplayableDevice> selectedDevices = lstViewRentSelectedDevices.getStore()
				.getModels();
		final String[] selectedDevicesImeis = new String[selectedDevices.size()];
		int i = 0;
		for (DisplayableDevice dd : selectedDevices) {
			selectedDevicesImeis[i] = dd.getImei();
			i++;
		}

		/*
		 * Either add the selected devices to an existing Renter's list or create a new renter and add the devices to his list.
		 */
		if (sr == null && renterMatr != null && !renterMatr.isEmpty()) {
			// Add devices to a registered student
			String rentEventComments = txtAreaRentComments.getText() != null ? txtAreaRentComments
					.getText() : " ";
			this.client.rentDevicesToExistingRenter(renterMatr, selectedDevicesImeis,
					rentEventComments, canvasRentSignature.toString());

		} else {
			final String rentEventComments = txtAreaRentComments.getText() != null ? txtAreaRentComments
					.getText() : " ";
			if (sr != null) {
				// Add new renter and add to his rented devices list.
				client.rentDevicesToNewRenter(sr, sr.getMatriculationNumber(),
						selectedDevicesImeis, rentEventComments,
						canvasRentSignature.toString());

			}
		}

		// Clean up.
		// Clean list of selected devices
		lstViewRentSelectedDevices.getStore().removeAll();

		// Clean Signature
		clearSignatureCanvas();

		// Clear comments
		txtAreaRentComments.setText("");

		// Clear new person data
		resetNewStudentFields();
		resetSelectedStudent();

	}

	private void fetchEventsHistoryFiltered() {
		String persName = null, devImei = null;
		Date from = null, to = null;

		if (cBoxHistoryFilterName.getSelection().size() > 0)
			persName = cBoxHistoryFilterName.getSelection().get(0).getName();

		if (cBoxHistoryFilterImei.getSelection().size() > 0)
			devImei = cBoxHistoryFilterImei.getSelection().get(0).getImei();

		from = datePickerHistoryFilterFrom.getValue();
		to = datePickerHistoryFilterTo.getValue();
		if (to != null && from != null && (from.compareTo(to) > 0)) {
			to = from;
		}

		client.fetchEventsHistoryFiltered(persName, devImei, from, to, Integer.MAX_VALUE,
				true);
	}

	private void clearSignatureCanvas() {
		canvasRentSignature.clear();
		injectSignatureCanvasFrame();
	}

	private void unMarkDeviceForRent() {
		List<DisplayableDevice> selectedItems = lstViewRentSelectedDevices
				.getSelectionModel().getSelectedItems();
		if (selectedItems != null && selectedItems.size() > 0) {
			for (DisplayableDevice dd : selectedItems) {
				lstViewRentSelectedDevices.getStore().remove(dd);
			}
			cBoxRentSelectDevice.getStore().add(selectedItems);
		}

	}

	private void markDeviceForRent() {
		if (cBoxRentSelectDevice.getSelectionLength() == 0)
			return;

		DisplayableDevice selectedDevice = cBoxRentSelectDevice.getSelection().get(0);

		if (selectedDevice == null)
			return;

		// Remove selected device from combobox
		cBoxRentSelectDevice.getStore().remove(selectedDevice);
		cBoxRentSelectDevice.reset();

		// Add device to the list of selected devices
		lstViewRentSelectedDevices.getStore().add(selectedDevice);

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
		// cBoxRentRegisteredStudentMatriculation.setRawValue("");
		// cBoxRentRegisteredStudentName.setRawValue("");
		//
		cBoxRentRegisteredStudentMatriculation.reset();
		cBoxRentRegisteredStudentName.reset();

	}

	/*
	 * Mouse Don/Move/Up/Over/Out Handlers
	 */
	@Override
	public void onMouseDown(MouseDownEvent event) {

		currentSignaturePath = new Path(event.getX(), event.getY());
		currentSignaturePath.setStrokeColor("#4169E1");
		currentSignaturePath.setStrokeWidth(4);
		canvasRentSignature.add(currentSignaturePath);
		signing = true;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (currentSignaturePath != null && signing)
			currentSignaturePath.lineTo(event.getX(), event.getY());

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		signing = false;
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// RootPanel.get().getElement().getStyle().setCursor());

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
		signing = false;
	}

	private class registeredStudentChanged extends
			SelectionChangedListener<DisplayableRenter> {

		@Override
		public void selectionChanged(SelectionChangedEvent<DisplayableRenter> se) {
			Object sender = se.getSource();
			if (sender == cBoxRentRegisteredStudentMatriculation
					&& cBoxRentRegisteredStudentMatriculation.getSelection().size() > 0) {
				cBoxRentRegisteredStudentName
						.setValue(cBoxRentRegisteredStudentMatriculation.getSelection()
								.get(0));
				resetNewStudentFields();
			}
			if (sender == cBoxRentRegisteredStudentName
					&& cBoxRentRegisteredStudentName.getSelection().size() > 0) {
				cBoxRentRegisteredStudentMatriculation
						.setValue(cBoxRentRegisteredStudentName.getSelection().get(0));
				resetNewStudentFields();
			}

			if (sender == cBoxHistoryFilterName) {
				fetchEventsHistoryFiltered();
			}
		}
	}

	private class rentedDeviceChanged extends SelectionChangedListener<DisplayableDevice> {

		@Override
		public void selectionChanged(SelectionChangedEvent<DisplayableDevice> se) {
			Object sender = se.getSource();

			if (sender == cBoxHistoryFilterImei) {
				fetchEventsHistoryFiltered();
			}
		}
	}
}
