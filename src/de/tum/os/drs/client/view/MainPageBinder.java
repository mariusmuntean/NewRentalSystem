package de.tum.os.drs.client.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.state.Provider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.ChartArea;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.TextStyle;
import de.tum.os.drs.client.NewRentalSystem;
import de.tum.os.drs.client.model.DeviceType;
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
	RadioButton rBtnOverview, rBtnRent, rBtnReturn, rBtnHistory, rBtnManage, rBtnManageStudents;

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

	Path currentRentSignaturePath;
	boolean signingRent = false;

	@UiField
	Button btnRentClearSignature;

	@UiField
	Grid gridRentDevices;

	@UiField
	Button btnSubmitRentEvent;

	/*
	 * History controls
	 */
	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxHistoryFilterName;

	@UiField(provided = true)
	ComboBox<DisplayableDevice> cBoxHistoryFilterImei;
	
	@UiField
	com.google.gwt.user.client.ui.Button btnHistoryresetFilters;

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

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxReturnRegisteredStudentName;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxReturnRegisteredStudentMatriculation;

	@UiField
	TextArea txtAreaReturnComments;

	@UiField(provided = true)
	DrawingArea canvasReturnSignature;

	Path currentReturnSignaturePath;
	boolean signingReturn = false;

	@UiField
	Button btnReturnSubmit;

	@UiField
	Button btnReturnClearCanvas;

	/*
	 * Manage devices controls
	 */
	// Add
	@UiField
	DecoratedTabPanel decoratedTabPanelDeviceManagement;

	@UiField
	ListBox cBoxManageDevicesAddType;

	@UiField(provided = true)
	SuggestBox cBoxManageDevicesAddName;

	MultiWordSuggestOracle manageDevicesAddNamesOracle;

	@UiField
	TextBox txtBoxManageDevicesAddIMEI;

	@UiField
	ListBox cBoxManageDevicesAddState;

	@UiField
	com.google.gwt.user.client.ui.Button btnManageDevicesAddNewDevice;

	@UiField
	TextArea txtAreaManageDevicesAddComments;

	// View
	@UiField(provided = true)
	ComboBox<DisplayableDevice> cBoxManageDevicesViewDevName;

	@UiField(provided = true)
	ComboBox<DisplayableDevice> cBoxManageDevicesViewDevIMEI;

	@UiField
	TextBox txtBoxManageDevicesViewDevName;
	@UiField
	TextBox txtBoxManageDevicesViewDevImei;
	@UiField
	ListBox cBoxManageDevicesViewDevState;
	@UiField
	TextArea txtBoxManageDevicesViewDevComments;
	@UiField
	Image imgManageDevicesViewDevImage;
	@UiField
	CheckBox checkBoxManageDevicesViewEnableEdit;
	@UiField
	com.google.gwt.user.client.ui.Button btnManageDevicesViewUpdate;
	PersistentDevice currentlyDIsplayedPD;
	@UiField
	com.google.gwt.user.client.ui.Button btnManageDevicesViewUpdateDelete;

	/*
	 * Manage Students region
	 */
	// View
	
	// Update
	@UiField
	DecoratedTabPanel decoratedTabPanelStudentsManagement;
	
	@UiField(provided=true)
	ComboBox<DisplayableRenter> cBoxManageStudentsViewStudentName;
	
	@UiField(provided=true)
	ComboBox<DisplayableRenter> cBoxManageStudentsViewStudentMatriculation;
	
	/*
	 * ViewModels
	 */
	private RenterTreeViewModel rtvm;

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

	/*
	 * Stores region
	 */
	private ListStore<DisplayableRenter> displayableRentersListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> availableDevicesListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableDevice> selectedDevicesListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableRenter> displayableRentersFilterListStore = new ListStore<DisplayableRenter>();
	private ListStore<DisplayableDevice> displayableDevicesFilterListStore = new ListStore<DisplayableDevice>();
	private ListStore<DisplayableDevice> allDisplayableDevicesListStore = new ListStore<DisplayableDevice>();

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
			+ "<tr><td><strong>{name}</strong></td></tr>"
			+ "<tr><td>{matriculation}</td></tr>" + "</table>");

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
			ListDataProvider<PersistentDevice> allRentedDevicesDataProvider,
			MultiWordSuggestOracle deviceNamesOracle,
			ListStore<DisplayableDevice> allDisplayableDevicesListStore) {

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
		this.manageDevicesAddNamesOracle = deviceNamesOracle;
		this.allDisplayableDevicesListStore = allDisplayableDevicesListStore;

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
		this.tableHistoryOverview = getCellTableHistory(eventsHistoryDataProvider);
		this.tableAvailableDevices = getDevicesTableFromDataprovider(availableDevicesDataProvider);
		this.tableUnavailableDevices = getDevicesTableFromDataprovider(unavailableDevicesDataProvider);

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
		cBoxReturnRegisteredStudentName = new ComboBox<DisplayableRenter>();
		cBoxReturnRegisteredStudentName.setSimpleTemplate(rentStudentComboTemplate);
		cBoxReturnRegisteredStudentName.setStore(displayableRentersListStore);
		cBoxReturnRegisteredStudentName.setTriggerAction(TriggerAction.ALL);

		cBoxReturnRegisteredStudentMatriculation = new ComboBox<DisplayableRenter>();
		cBoxReturnRegisteredStudentMatriculation
				.setSimpleTemplate(rentStudentComboTemplate);
		cBoxReturnRegisteredStudentMatriculation
				.setStore(displayableRentersFilterListStore);
		cBoxReturnRegisteredStudentMatriculation.setTriggerAction(TriggerAction.ALL);

		SelectionChangeEvent.Handler deviceSelectionHandler = new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (rtvm.getSelectedDevices() != null
						&& rtvm.getSelectedDevices().size() > 0) {
					StringBuilder sb = new StringBuilder();
					List<PersistentDevice> devices = new ArrayList<PersistentDevice>(
							rtvm.getSelectedDevices());
					for (PersistentDevice pd : devices) {
						sb.append(pd.getName() + "(" + pd.getIMEI() + "), ");
					}
					lblRentSelectedDevices.setText(rtvm.getSelectedRenter().getName()
							+ " -> " + sb.toString());
				}

			}
		};
		rtvm = new RenterTreeViewModel(allRentersDataProvider,
				allRentedDevicesDataProvider, deviceSelectionHandler);
		cellBrowserReturn = new CellBrowser(rtvm, null);

		canvasReturnSignature = new DrawingArea(500, 300);

		// History page
		cBoxHistoryFilterName = new ComboBox<DisplayableRenter>();
		cBoxHistoryFilterName.setSimpleTemplate(rentStudentComboTemplate);
		cBoxHistoryFilterName.setStore(displayableRentersFilterListStore);
		cBoxHistoryFilterName.setTriggerAction(TriggerAction.ALL);

		cBoxHistoryFilterImei = new ComboBox<DisplayableDevice>();
		cBoxHistoryFilterImei.setStore(displayableDevicesFilterListStore);
		cBoxHistoryFilterImei.setTriggerAction(TriggerAction.ALL);
		tableHistoryEventsFiltered = getCellTableHistory(eventsFilteredHistoryDataProvider);

		// Manage devices page
		cBoxManageDevicesAddName = new SuggestBox(this.manageDevicesAddNamesOracle);

		cBoxManageDevicesViewDevIMEI = new ComboBox<DisplayableDevice>();
		cBoxManageDevicesViewDevIMEI.setSimpleTemplate(displayableDeviceTemplate);
		cBoxManageDevicesViewDevIMEI.setStore(allDisplayableDevicesListStore);
		cBoxManageDevicesViewDevIMEI.setForceSelection(true);
		cBoxManageDevicesViewDevIMEI.setTriggerAction(TriggerAction.ALL);

		cBoxManageDevicesViewDevName = new ComboBox<DisplayableDevice>();
		cBoxManageDevicesViewDevName.setSimpleTemplate(displayableDeviceTemplate);
		cBoxManageDevicesViewDevName.setStore(allDisplayableDevicesListStore);
		cBoxManageDevicesViewDevName.setForceSelection(true);
		cBoxManageDevicesViewDevName.setTriggerAction(TriggerAction.ALL);
		
		//Manage students page
		cBoxManageStudentsViewStudentName = new ComboBox<DisplayableRenter>();
		cBoxManageStudentsViewStudentName.setSimpleTemplate(rentStudentComboTemplate);
		cBoxManageStudentsViewStudentName.setStore(displayableRentersListStore);
		cBoxManageStudentsViewStudentName.setTriggerAction(TriggerAction.ALL);

		cBoxManageStudentsViewStudentMatriculation = new ComboBox<DisplayableRenter>();
		cBoxManageStudentsViewStudentMatriculation
				.setSimpleTemplate(rentStudentComboTemplate);
		cBoxManageStudentsViewStudentMatriculation.setStore(displayableRentersListStore);
		cBoxManageStudentsViewStudentMatriculation.setTriggerAction(TriggerAction.ALL);

	}

	private void wireUpControls() {
		// Radio buttons for changing between pages
		rBtnHistory.addClickHandler(this);
		rBtnManage.addClickHandler(this);
		rBtnOverview.addClickHandler(this);
		rBtnRent.addClickHandler(this);
		rBtnReturn.addClickHandler(this);
		rBtnManageStudents.addClickHandler(this);

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

		btnSubmitRentEvent.addListener(Events.OnClick, this);

		// Return page
		cBoxReturnRegisteredStudentMatriculation.addSelectionChangedListener(rsc);
		cBoxReturnRegisteredStudentName.addSelectionChangedListener(rsc);
		canvasReturnSignature.addMouseDownHandler(this);
		canvasReturnSignature.addMouseMoveHandler(this);
		canvasReturnSignature.addMouseUpHandler(this);
		canvasReturnSignature.addMouseOverHandler(this);
		canvasReturnSignature.addMouseOutHandler(this);
		btnReturnSubmit.addListener(Events.OnClick, this);
		btnReturnClearCanvas.addListener(Events.OnClick, this);

		// History page
		cBoxHistoryFilterName.addSelectionChangedListener(rsc);
		rentedDeviceChanged rdc = new rentedDeviceChanged();
		cBoxHistoryFilterImei.addSelectionChangedListener(rdc);
		btnHistoryresetFilters.addClickHandler(this);

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

		// Manage devices
		// Add
		btnManageDevicesAddNewDevice.addClickHandler(this);
		for (DeviceType dt : DeviceType.values()) {
			cBoxManageDevicesAddType.addItem(dt.toString());
		}

		decoratedTabPanelDeviceManagement.selectTab(0);

		// View
		cBoxManageDevicesViewDevIMEI
				.addSelectionChangedListener(new SelectionChangedListener<DisplayableDevice>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<DisplayableDevice> se) {
						DisplayableDevice dd = se.getSelectedItem();
						if (dd != null) {
							cBoxManageDevicesViewDevName.setRawValue(dd.getName());
							displayDeviceDetails(dd.getImei());
						}

					}
				});
		cBoxManageDevicesViewDevName
				.addSelectionChangedListener(new SelectionChangedListener<DisplayableDevice>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<DisplayableDevice> se) {
						DisplayableDevice dd = se.getSelectedItem();
						if (dd != null) {
							cBoxManageDevicesViewDevIMEI.setRawValue(dd.getImei());
							displayDeviceDetails(dd.getImei());
						}
					}
				});
		checkBoxManageDevicesViewEnableEdit.addClickHandler(this);
		// Update
		btnManageDevicesViewUpdate.addClickHandler(this);

		// Delete
		btnManageDevicesViewUpdateDelete.addClickHandler(this);

	}

	protected void displayDeviceDetails(String imei) {
		if (imei == null || imei.isEmpty())
			return;

		AsyncCallback<PersistentDevice> getPDcallback = new AsyncCallback<PersistentDevice>() {

			@Override
			public void onSuccess(PersistentDevice result) {
				currentlyDIsplayedPD = result;
				if (result == null)
					return;

				String devPictureName = deviceNameToImageNameMap.get(result.getName()
						.toLowerCase().trim());
				if (devPictureName == null) {
					devPictureName = deviceNotFoundImage;
				}
				imgManageDevicesViewDevImage.setUrl("images/devices/" + devPictureName);
				txtBoxManageDevicesViewDevName.setText(result.getName());
				txtBoxManageDevicesViewDevImei.setText(result.getIMEI());
				boolean found = false;
				for (int i = 0; i < cBoxManageDevicesViewDevState.getItemCount(); i++) {
					if (cBoxManageDevicesViewDevState.getValue(i).toLowerCase()
							.equals(result.getState())) {
						found = true;
						cBoxManageDevicesViewDevState.setSelectedIndex(i);
						break;
					}
				}
				if (!found) {
					cBoxManageDevicesViewDevState.insertItem(result.getState(), 0);
					cBoxManageDevicesViewDevState.setSelectedIndex(0);
				}
				txtBoxManageDevicesViewDevComments.setText(result.getDescription());

			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};

		client.getPersistentDeviceByImei(imei, getPDcallback);
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

		Path path2 = new Path(0, 0);
		path2.lineRelativelyTo(canvasReturnSignature.getWidth(), 0);
		path2.lineRelativelyTo(0, canvasReturnSignature.getHeight());
		path2.lineRelativelyTo(-canvasReturnSignature.getWidth(), 0);
		path2.close();
		path2.setStrokeWidth(3);
		path2.setStrokeColor("#7CFC00");
		canvasReturnSignature.add(path2);

	}

	private void injectPieChart() {

		Runnable onVizApiLoadedCallback = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DataTable data = getDevicePieChartData(4, 4);
				PieOptions options = getDevicePieChartOptions(300, 300);
				pChartRentedVsAvailable = new PieChart(data, options);
				// pChartRentedVsAvailable.getElement().setAttribute("vertical-align", "middle");

				hPanelOverview.insert(pChartRentedVsAvailable, 0);
				hPanelOverview
						.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				hPanelOverview.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onVizApiLoadedCallback, PieChart.PACKAGE);
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
		ca.setWidth("80%");
		ca.setHeight("80%");
		options.setChartArea(ca);
		options.setLegend(LegendPosition.BOTTOM);

		TextStyle ts = TextStyle.create();
		ts.set("text-align", "center");
		ts.setFontName("Verdana");
		ts.setFontSize(16);

		options.setColors(new String[] { "red", "blue" });

		options.setTitleTextStyle(ts);
		options.setTitle("Available vs. Rented");
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

		ListHandler<PersistentDevice> imeiSortHandler = new ListHandler<PersistentDevice>(
				list);
		columnSortHandler.setComparator(imeiColumn, new Comparator<PersistentDevice>() {
			public int compare(PersistentDevice p1, PersistentDevice p2) {
				if (p1 == p2) {
					return 0;
				}

				// Compare the imei columns.
				if (p1 != null) {
					return (p2 != null) ? p1.getIMEI().compareTo(p2.getIMEI()) : 1;
				}
				return -1;
			}
		});
		newTable.addColumnSortHandler(imeiSortHandler);

		ListHandler<PersistentDevice> typeSortHandler = new ListHandler<PersistentDevice>(
				list);
		columnSortHandler.setComparator(deviceTypeColumn,
				new Comparator<PersistentDevice>() {
					public int compare(PersistentDevice p1, PersistentDevice p2) {
						if (p1 == p2) {
							return 0;
						}

						// Compare the type columns.
						if (p1 != null) {
							return (p2 != null) ? p1.getDeviceType().compareTo(
									p2.getDeviceType()) : 1;
						}
						return -1;
					}
				});
		newTable.addColumnSortHandler(typeSortHandler);

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
		columnSortHandler.setComparator(eventTypeColumn,
				new Comparator<PersistentEvent>() {
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
							return (p2 != null) ? ((p1.getEventType().toString()
									.compareTo(p2.getEventType().toString()))) : 1;
						}
						return -1;
					}
				});
		newTable.addColumnSortHandler(eventTypeColumnSortHandler);

		return newTable;
	}

	public void setRentedVsAvailable(final int availableDevices, final int rentedDevices) {
		Runnable updatePieChartBallback = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DataTable newData = getDevicePieChartData(availableDevices, rentedDevices);
				PieOptions options = getDevicePieChartOptions(300, 300);
				pChartRentedVsAvailable.draw(newData, options);
			}
		};

		if (pChartRentedVsAvailable == null) {
			VisualizationUtils.loadVisualizationApi(updatePieChartBallback,
					PieChart.PACKAGE);
		} else {
			updatePieChartBallback.run();
		}

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
		if (sender == rBtnManage) {
			String parentWidth = String.valueOf(deckPanelActualView.getElement()
					.getClientWidth()) + "px";
			decoratedTabPanelDeviceManagement.setWidth(parentWidth);
			deckPanelActualView.showWidget(4);
		}
		if(sender == rBtnManageStudents){
			String parentWidth = String.valueOf(deckPanelActualView.getElement()
					.getClientWidth()) + "px";
			decoratedTabPanelStudentsManagement.setWidth(parentWidth);
			deckPanelActualView.showWidget(5);
		}
		
		// History region
		if(sender == btnHistoryresetFilters){
			resetHistoryFilters();
		}

		// Manage Devices region
		if (sender == btnManageDevicesAddNewDevice) {
			addNewDevice();
		}
		if (sender == checkBoxManageDevicesViewEnableEdit) {
			toggleDeviceDataFields(checkBoxManageDevicesViewEnableEdit.isChecked());
		}
		if (sender == btnManageDevicesViewUpdate) {
			updateDevice();
		}
		if (sender == btnManageDevicesViewUpdateDelete) {
			deleteDevice();
		}

	}

	private void resetHistoryFilters() {
		cBoxHistoryFilterImei.setRawValue("");
		cBoxHistoryFilterName.setRawValue("");
		datePickerHistoryFilterFrom.clearState();
		datePickerHistoryFilterTo.clearState();
		txtBoxHistoryFilterFrom.setText("");
		txtBoxHistoryFilterTo.setText("");
		fetchEventsHistoryFiltered();
		
	}

	private void deleteDevice() {
		AsyncCallback<Boolean> deleteDeviceCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					// Clear fields
					txtBoxManageDevicesViewDevComments.setText("");
					txtBoxManageDevicesViewDevImei.setText("");
					txtBoxManageDevicesViewDevName.setText("");
					cBoxManageDevicesViewDevState.setSelectedIndex(0);
					imgManageDevicesViewDevImage.setUrl("images/devices/"
							+ deviceNotFoundImage);
					
					Info.display("Success!", "Deleted " + currentlyDIsplayedPD.getName());
				} else {
					Info.display("Server Error!", "Error deleting "
							+ currentlyDIsplayedPD.getName());
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network Error!",
						"Error deleting " + currentlyDIsplayedPD.getName());

			}
		};

		client.deleteDevice(currentlyDIsplayedPD, deleteDeviceCallback);
	}

	private void updateDevice() {
		if (currentlyDIsplayedPD == null)
			return;

		String devName = txtBoxManageDevicesViewDevName.getText();
		String devImei = txtBoxManageDevicesViewDevImei.getText();
		String devState = cBoxManageDevicesViewDevState
				.getValue(cBoxManageDevicesViewDevState.getSelectedIndex());
		String devComments = txtBoxManageDevicesViewDevComments.getText();
		if (devName == null || devImei == null || devState == null || devComments == null) {
			Info.display("Error", "Please review fields");
			return;
		}

		currentlyDIsplayedPD.setDescription(devComments);
		currentlyDIsplayedPD.setName(devName);
		currentlyDIsplayedPD.setIMEI(devImei);
		currentlyDIsplayedPD.setState(devState);

		AsyncCallback<Boolean> updateDeviceCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Success!", currentlyDIsplayedPD.getName() + " updated.");
					// // Clear fields
					// txtBoxManageDevicesViewDevComments.setText("");
					// txtBoxManageDevicesViewDevImei.setText("");
					// txtBoxManageDevicesViewDevName.setText("");
					// cBoxManageDevicesViewDevState.setSelectedIndex(0);
				} else {
					Info.display("Server Error", "Could not update "
							+ currentlyDIsplayedPD.getName() + " info.");
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network Error",
						"Could not update " + currentlyDIsplayedPD.getName() + " info.");

			}
		};

		client.updateExistingDevice(currentlyDIsplayedPD, updateDeviceCallback);

	}

	private void toggleDeviceDataFields(boolean b) {
		txtBoxManageDevicesViewDevName.setEnabled(b);
		txtBoxManageDevicesViewDevImei.setEnabled(b);
		txtBoxManageDevicesViewDevComments.setEnabled(b);
		cBoxManageDevicesViewDevState.setEnabled(b);
		btnManageDevicesViewUpdate.setEnabled(b);
	}

	private void addNewDevice() {
		int devTypeSelectedIndex = cBoxManageDevicesAddType.getSelectedIndex();
		DeviceType devType = null;
		try {
			devType = DeviceType.valueOf(cBoxManageDevicesAddType
					.getItemText(devTypeSelectedIndex));
		} catch (Exception e) {
			return;
		}

		String devName = cBoxManageDevicesAddName.getText();
		String devState = cBoxManageDevicesAddState.getItemText(cBoxManageDevicesAddState
				.getSelectedIndex());
		String devIMEI = txtBoxManageDevicesAddIMEI.getText();
		String devComments = txtAreaManageDevicesAddComments.getText();
		if (devType == null || devName == null || devName.isEmpty() || devState == null
				|| devState.isEmpty() || devIMEI == null || devIMEI.isEmpty()) {
			return;
		}
		String devPictureName = deviceNameToImageNameMap
				.get(devName.toLowerCase().trim());
		if (devPictureName == null) {
			devPictureName = deviceNotFoundImage;
		}

		PersistentDevice pd = new PersistentDevice(devIMEI, devName, devComments,
				devState, devType, devPictureName, new Boolean(true));
		client.addNewDevice(pd);

		// Clear fields
		cBoxManageDevicesAddType.setSelectedIndex(0);
		cBoxManageDevicesAddName.setText("");
		cBoxManageDevicesAddState.setSelectedIndex(0);
		txtBoxManageDevicesAddIMEI.setText("");
		txtAreaManageDevicesAddComments.setText("");

		// Inform the user
		Info.display("Success!", "Added a new {0}", pd.getName());

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
				clearRentSignatureCanvas();
			}

			if (sender == btnSubmitRentEvent) {
				submitRentEvent();
			}

			if (sender == btnReturnClearCanvas) {
				clearReturnSignatureCanvas();
			}

			if (sender == btnReturnSubmit) {
				submitReturnEvent();
			}
		}

	}

	private void submitReturnEvent() {
		// Check if renter selected
		SerializableRenter selectedRenter = rtvm.getSelectedRenter();
		if (selectedRenter == null)
			return;

		// Check if devices selected
		HashSet<PersistentDevice> selectedDevices = rtvm.getSelectedDevices();
		if (selectedDevices == null || selectedDevices.size() <= 0)
			return;

		// Collect data
		Iterator<PersistentDevice> selectedDevicesIt = selectedDevices.iterator();
		String[] selectedImeiCodes = new String[selectedDevices.size()];
		int index = -1;
		while (selectedDevicesIt.hasNext()) {
			index++;
			selectedImeiCodes[index] = selectedDevicesIt.next().getIMEI();
		}
		String comments = txtAreaReturnComments.getText();
		String signature = canvasReturnSignature.toString();

		// Submit
		client.returnDevices(selectedRenter.getMatriculationNumber(), selectedImeiCodes,
				comments, signature);

		// Clear Return page
		clearReturnSignatureCanvas();
		txtAreaReturnComments.setText("");

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
		clearRentSignatureCanvas();

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

	private void clearRentSignatureCanvas() {
		canvasRentSignature.clear();
		injectSignatureCanvasFrame();
	}

	private void clearReturnSignatureCanvas() {
		canvasReturnSignature.clear();
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
		Object sender = event.getSource();
		if (sender == canvasRentSignature) {
			currentRentSignaturePath = new Path(event.getX(), event.getY());
			currentRentSignaturePath.setStrokeColor("#4169E1");
			currentRentSignaturePath.setStrokeWidth(4);
			canvasRentSignature.add(currentRentSignaturePath);
			signingRent = true;
			event.preventDefault();
		}

		if (sender == canvasReturnSignature) {
			currentReturnSignaturePath = new Path(event.getX(), event.getY());
			currentReturnSignaturePath.setStrokeColor("#4169E1");
			currentReturnSignaturePath.setStrokeWidth(4);
			canvasReturnSignature.add(currentReturnSignaturePath);
			signingReturn = true;
			event.preventDefault();
		}

	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		Object sender = event.getSource();
		if (sender == canvasRentSignature) {
			if (currentRentSignaturePath != null && signingRent)
				currentRentSignaturePath.lineTo(event.getX(), event.getY());
		}

		if (sender == canvasReturnSignature) {
			if (currentReturnSignaturePath != null && signingReturn)
				currentReturnSignaturePath.lineTo(event.getX(), event.getY());
		}

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		Object sender = event.getSource();
		if (sender == canvasRentSignature) {
			signingRent = false;
		}

		if (sender == canvasReturnSignature) {
			signingReturn = false;
		}
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// RootPanel.get().getElement().getStyle().setCursor());

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// RootPanel.get().getElement().getStyle().setCursor(Cursor.AUTO);
		Object sender = event.getSource();
		if (sender == canvasRentSignature) {
			signingRent = false;
		}
		if (sender == canvasReturnSignature) {
			signingReturn = false;
		}
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

			if (sender == cBoxReturnRegisteredStudentMatriculation
					&& cBoxReturnRegisteredStudentMatriculation.getSelection().size() > 0) {
				if (cBoxReturnRegisteredStudentMatriculation.getSelection().get(0) != null) {
					rtvm.selectRenter(cBoxReturnRegisteredStudentMatriculation
							.getSelection().get(0).getMatriculation());
					cBoxReturnRegisteredStudentName
							.setValue(cBoxReturnRegisteredStudentMatriculation
									.getSelection().get(0));
					int nodeIndex = rtvm
							.getRenterIndexByMatriculation(cBoxReturnRegisteredStudentMatriculation
									.getSelection().get(0).getMatriculation());
					cellBrowserReturn.getRootTreeNode().setChildOpen(nodeIndex, true);

				}
			}

			if (sender == cBoxReturnRegisteredStudentName
					&& cBoxReturnRegisteredStudentName.getSelection().size() > 0) {
				if (cBoxReturnRegisteredStudentName.getSelection().get(0) != null) {
					rtvm.selectRenter(cBoxReturnRegisteredStudentName.getSelection()
							.get(0).getMatriculation());
					cBoxReturnRegisteredStudentMatriculation
							.setValue(cBoxReturnRegisteredStudentName.getSelection().get(
									0));
					int nodeIndex = rtvm
							.getRenterIndexByMatriculation(cBoxReturnRegisteredStudentName
									.getSelection().get(0).getMatriculation());
					cellBrowserReturn.getRootTreeNode().setChildOpen(nodeIndex, true);
				}
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
