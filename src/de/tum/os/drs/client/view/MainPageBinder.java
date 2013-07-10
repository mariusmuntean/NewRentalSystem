package de.tum.os.drs.client.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Path;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.flash.FlashComponent;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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
import de.tum.os.drs.client.helpers.barcodes.BitMatrix;
import de.tum.os.drs.client.helpers.barcodes.Decoder;
import de.tum.os.drs.client.helpers.barcodes.DecoderResult;
import de.tum.os.drs.client.helpers.barcodes.ReaderException;
import de.tum.os.drs.client.model.DeviceType;
import de.tum.os.drs.client.model.DisplayableDevice;
import de.tum.os.drs.client.model.DisplayableRenter;
import de.tum.os.drs.client.model.EventType;
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

	/**
	 * Defines resources for the devices tables in the overview page
	 * 
	 * @author Marius
	 */
	interface CellTableResources extends CellTable.Resources {
		@NotStrict
		@Source(value = { CellTable.Style.DEFAULT_CSS,
				"../../../../../../Resources/Styles/CellTableStyle.css" })
		CellTable.Style cellTableStyle();
	}

	/*
	 * UiFields region
	 */
	// Header region
	@UiField(provided = true)
	UserWidget userWidget;

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
	RadioButton rBtnOverview, rBtnRent, rBtnReturn, rBtnHistory, rBtnManage,
			rBtnManageStudents;

	PieChart pChartRentedVsAvailable;

	/*
	 * Rent Controls
	 */
	@UiField
	VerticalPanel vPanelRentPage;

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
	DatePicker datePickerRentEstimatedRD;

	@UiField
	com.google.gwt.user.client.ui.Button btnRent;

	@UiField
	com.google.gwt.user.client.ui.Button btnRentClearRentedList;

	@UiField
	TextArea txtAreaRentComments;

	@UiField(provided = true)
	DrawingArea canvasRentSignature;

	Path currentRentSignaturePath;
	boolean signingRent = false;

	@UiField
	com.google.gwt.user.client.ui.Button btnRentClearSignature;

	@UiField
	com.google.gwt.user.client.ui.Button btnSubmitRentEvent;

	/*
	 * Return controls
	 */
	@UiField
	CaptionPanel captionPanelReturnPage;

	/*
	 * History controls
	 */
	@UiField
	VerticalPanel vPanelHistoryPage;

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

	/*
	 * Return controls
	 */
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
	com.google.gwt.user.client.ui.Button btnReturnSubmit;

	@UiField
	com.google.gwt.user.client.ui.Button btnReturnClearCanvas;

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
	com.google.gwt.user.client.ui.Button btnManageDevicesAddScan;

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
	@UiField
	com.google.gwt.user.client.ui.Button btnManageStudentsAddNewStudent;

	@UiField
	TextBox txtBoxManageStudentsAddName;
	@UiField
	TextBox txtBoxManageStudentsAddMatriculation;
	@UiField
	TextBox txtBoxManageStudentsAddEmail;
	@UiField
	TextBox txtBoxManageStudentsAddPhone;
	@UiField
	TextArea txtAreaManageStudentsAddComments;

	// Update
	@UiField
	DecoratedTabPanel decoratedTabPanelStudentsManagement;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxManageStudentsViewStudentName;

	@UiField(provided = true)
	ComboBox<DisplayableRenter> cBoxManageStudentsViewStudentMatriculation;

	@UiField
	CheckBox checkBoxManageStudentsViewEnableEdit;

	@UiField
	com.google.gwt.user.client.ui.Button btnManageStudentsViewUpdate;

	@UiField
	TextBox txtBoxManageStudentsViewStudentName;

	@UiField
	TextBox txtBoxManageStudentsViewStudentMatriculation;

	@UiField
	TextBox txtBoxManageStudentsViewStudentEmail;

	@UiField
	TextBox txtBoxManageStudentsViewStudentPhone;

	@UiField
	TextArea txtBoxManageStudentsViewStudentComments;

	SerializableRenter currentlyDisplayedSRenter = null;

	// Delete
	@UiField
	com.google.gwt.user.client.ui.Button btnManageStudentsViewUpdateDelete;

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
			put("galaxy nexus 16gb", "galaxy nexus.jpg");
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

//	final String displayableDeviceTemplate = new String(
//					"<table>"
//					+ "<tr>"
//					+ "<td rowspan=\"2\"><img src=\"images/devices/{imgName}\" width=\"80\" height=\"80\"></td>"
//					+ "<td>{name}</td></tr>" 
//					+ "<tr><td>{imei}</td></tr>"
//					+ "</table>");
	
	final StringBuilder sbDisplayableDeviceTemplate = new StringBuilder();

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
		// Templates
		sbDisplayableDeviceTemplate.append("<table>\n");
		sbDisplayableDeviceTemplate.append("<tr>\n");
		sbDisplayableDeviceTemplate.append("<td rowspan=\"2\"><img src=\"images/devices/{imgName}\" width=\"80\" height=\"80\"></td>\n");
		sbDisplayableDeviceTemplate.append("<td><p style=\"font-size:20px\"><b>{name}</b></p></td>\n");
		sbDisplayableDeviceTemplate.append("</tr>\n");
		sbDisplayableDeviceTemplate.append("<tr><td><p style=\"font-size:15px\">{imei}</p></td></tr>\n");
		sbDisplayableDeviceTemplate.append("</table>\n");
		
		// User widget
		this.userWidget = new UserWidget(client.logoutAction);

		// Overview page
		this.tableHistoryOverview = getCellTableHistory(eventsHistoryDataProvider);
		this.tableAvailableDevices = getDevicesTableFromDataprovider(
				availableDevicesDataProvider, false);
		this.tableUnavailableDevices = getDevicesTableFromDataprovider(
				unavailableDevicesDataProvider, true);

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
		cBoxRentSelectDevice.setSimpleTemplate(sbDisplayableDeviceTemplate.toString());
		cBoxRentSelectDevice.setStore(availableDevicesListStore);
		cBoxRentSelectDevice.setTriggerAction(TriggerAction.ALL);

		lstViewRentSelectedDevices = new ListView<DisplayableDevice>();
		lstViewRentSelectedDevices.setSimpleTemplate(sbDisplayableDeviceTemplate.toString());
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
		cBoxHistoryFilterImei.setSimpleTemplate(sbDisplayableDeviceTemplate.toString());
		cBoxHistoryFilterImei.setStore(displayableDevicesFilterListStore);
		cBoxHistoryFilterImei.setTriggerAction(TriggerAction.ALL);
		tableHistoryEventsFiltered = getCellTableHistory(eventsFilteredHistoryDataProvider);

		// Manage devices page
		cBoxManageDevicesAddName = new SuggestBox(this.manageDevicesAddNamesOracle);

		cBoxManageDevicesViewDevIMEI = new ComboBox<DisplayableDevice>();
		cBoxManageDevicesViewDevIMEI.setSimpleTemplate(sbDisplayableDeviceTemplate.toString());
		cBoxManageDevicesViewDevIMEI.setStore(allDisplayableDevicesListStore);
		cBoxManageDevicesViewDevIMEI.setForceSelection(true);
		cBoxManageDevicesViewDevIMEI.setTriggerAction(TriggerAction.ALL);

		cBoxManageDevicesViewDevName = new ComboBox<DisplayableDevice>();
		cBoxManageDevicesViewDevName.setSimpleTemplate(sbDisplayableDeviceTemplate.toString());
		cBoxManageDevicesViewDevName.setStore(allDisplayableDevicesListStore);
		cBoxManageDevicesViewDevName.setForceSelection(true);
		cBoxManageDevicesViewDevName.setTriggerAction(TriggerAction.ALL);

		// Manage students page
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
		btnRent.addClickHandler(this);
		btnRentClearRentedList.addClickHandler(this);
		injectSignatureCanvasFrame();
		canvasRentSignature.addMouseDownHandler(this);
		canvasRentSignature.addMouseMoveHandler(this);
		canvasRentSignature.addMouseUpHandler(this);
		canvasRentSignature.addMouseOverHandler(this);
		canvasRentSignature.addMouseOutHandler(this);
		btnRentClearSignature.addClickHandler(this);
		datePickerRentEstimatedRD.setValue(new Date(System.currentTimeMillis()
				+ (1000L * 60L * 60L * 24L * 7L * 6L)));
		btnSubmitRentEvent.addClickHandler(this);

		cBoxRentSelectDevice.addSelectionChangedListener(new SelectionChangedListener<DisplayableDevice>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<DisplayableDevice> se) {
				ArrayList<DisplayableDevice> selection = new ArrayList<DisplayableDevice>();
				selection.add(se.getSelectedItem());
				cBoxRentSelectDevice.setSelection(selection);
			}
		});

		// Return page
		cBoxReturnRegisteredStudentMatriculation.addSelectionChangedListener(rsc);
		cBoxReturnRegisteredStudentName.addSelectionChangedListener(rsc);
		canvasReturnSignature.addMouseDownHandler(this);
		canvasReturnSignature.addMouseMoveHandler(this);
		canvasReturnSignature.addMouseUpHandler(this);
		canvasReturnSignature.addMouseOverHandler(this);
		canvasReturnSignature.addMouseOutHandler(this);
		btnReturnSubmit.addClickHandler(this);
		btnReturnClearCanvas.addClickHandler(this);
		// btnReturnClearCanvas.getElement().appendChild(
		// new Image(
		// "http://www.googleventures.com/img/social-media/google_plus.png")
		// .getElement());

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
						fetchEventsHistoryFiltered(false);
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
					fetchEventsHistoryFiltered(false);
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
						fetchEventsHistoryFiltered(false);
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
					fetchEventsHistoryFiltered(false);
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
		btnManageDevicesAddScan.addClickHandler(this);
		// Publish methods for Flash component
		publishDecodeMthod();
		publishShowResultMethod();

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

		/*
		 * Manage Students
		 */
		decoratedTabPanelStudentsManagement.selectTab(0);

		// Add
		btnManageStudentsAddNewStudent.addClickHandler(this);

		// View
		cBoxManageStudentsViewStudentName
				.addSelectionChangedListener(new SelectionChangedListener<DisplayableRenter>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<DisplayableRenter> se) {
						DisplayableRenter dr = se.getSelectedItem();
						if (dr != null) {
							cBoxManageStudentsViewStudentMatriculation.setValue(dr);
							displayStudentDetails(dr.getMatriculation());
						}
					}
				});
		cBoxManageStudentsViewStudentMatriculation
				.addSelectionChangedListener(new SelectionChangedListener<DisplayableRenter>() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent<DisplayableRenter> se) {
						DisplayableRenter dr = se.getSelectedItem();
						if (dr != null) {
							cBoxManageStudentsViewStudentName.setValue(dr);
							displayStudentDetails(dr.getMatriculation());
						}
					}
				});

		// Update
		// cBoxManageStudentsViewStudentMatriculation.addListener(com.extjs.gxt.ui.client.event.EventType., this);
		checkBoxManageStudentsViewEnableEdit.addClickHandler(this);
		btnManageStudentsViewUpdate.addClickHandler(this);

		// Delete
		btnManageStudentsViewUpdateDelete.addClickHandler(this);

	}

	protected void displayStudentDetails(String matriculation) {
		if (matriculation == null || matriculation.isEmpty())
			return;

		AsyncCallback<SerializableRenter> displaySerializableRenterDetailsCallback = new AsyncCallback<SerializableRenter>() {

			@Override
			public void onSuccess(SerializableRenter result) {
				if (result == null) {
					return;
				}

				txtBoxManageStudentsViewStudentName.setText(result.getName());
				txtBoxManageStudentsViewStudentMatriculation.setText(result
						.getMatriculationNumber());
				txtBoxManageStudentsViewStudentEmail.setText(result.getEmail());
				txtBoxManageStudentsViewStudentPhone.setText(result.getPhoneNUmber());
				txtBoxManageStudentsViewStudentComments.setText(result.getComments());

				currentlyDisplayedSRenter = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		};

		client.getSerializableRenterByMatric(matriculation,
				displaySerializableRenterDetailsCallback);
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

		TextStyle pieSliceTextStyle = TextStyle.create();
		pieSliceTextStyle.setColor("black");
		pieSliceTextStyle.setFontSize(16);
		options.setPieSliceTextStyle(pieSliceTextStyle);

		TextStyle legendTextStyle = TextStyle.create();
		legendTextStyle.setColor("black");
		legendTextStyle.setFontSize(16);
		options.setLegendTextStyle(legendTextStyle);

		TextStyle ts = TextStyle.create();
		ts.set("text-align", "center");
		ts.setFontName("Verdana");
		ts.setFontSize(16);

		// Rented - Available
		options.setColors(new String[] { "#FFFF00", "#7FFF00" });

		options.setTitleTextStyle(ts);
		options.setTitle("Available vs. Rented");
		options.set3D(true);

		return options;
	}

	private CellTable<PersistentDevice> getDevicesTableFromDataprovider(
			final ListDataProvider<PersistentDevice> lstDataProvider,
			boolean forRentedDevices) {
		CellTable<PersistentDevice> newTable = new CellTable<PersistentDevice>(15,
				(Resources) GWT.create(CellTableResources.class));
		newTable.setTableLayoutFixed(false);

		// Create the number column
		TextColumn<PersistentDevice> numberColumn = new TextColumn<PersistentDevice>() {

			@Override
			public String getValue(PersistentDevice object) {
				return String.valueOf(lstDataProvider.getList().indexOf(object));
			}
		};

		// In case it is the table for rented devices color overdue device rows differently
		if (forRentedDevices) {
			newTable.setRowStyles(new RowStyles<PersistentDevice>() {
				@Override
				public String getStyleNames(PersistentDevice row, int rowIndex) {
					if ((new Date()).after(row.getEstimatedReturnDate())) {
						return "overdueDeviceRowStyle";
					} else {
						return "inTimeDeviceRowStyle";
					}
				}
			});
		} else {
			newTable.setRowStyles(new RowStyles<PersistentDevice>() {
				@Override
				public String getStyleNames(PersistentDevice row, int rowIndex) {
					return "returnEventRowtyle";
				}
			});
		}

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

		// In case it is the table for rented devices add another column
		TextColumn<PersistentDevice> estimatedReturnDateColumn = null;
		if (forRentedDevices) {
			// Return date column
			estimatedReturnDateColumn = new TextColumn<PersistentDevice>() {
				@Override
				public String getValue(PersistentDevice persistentDevice) {
					return persistentDevice.getEstimatedReturnDate().toString();
				}
			};
		}

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
		if (forRentedDevices) {
			estimatedReturnDateColumn.setSortable(true);
		}

		// Add columns to table
		newTable.addColumn(numberColumn, "#");
		newTable.addColumn(nameColumn, "Name");
		newTable.addColumn(imeiColumn, "IMEI");
		if (forRentedDevices) {
			newTable.addColumn(estimatedReturnDateColumn, "Estimated Return Date");
		}
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

		ListHandler<PersistentDevice> returnDateSortHandler = new ListHandler<PersistentDevice>(
				list);
		columnSortHandler.setComparator(estimatedReturnDateColumn,
				new Comparator<PersistentDevice>() {
					public int compare(PersistentDevice p1, PersistentDevice p2) {
						if (p1 == p2) {
							return 0;
						}

						// Compare the return date columns.
						if (p1 != null) {
							return (p2 != null) ? p1.getEstimatedReturnDate().compareTo(
									p2.getEstimatedReturnDate()) : 1;
						}
						return -1;
					}
				});
		newTable.addColumnSortHandler(returnDateSortHandler);

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
			final ListDataProvider<PersistentEvent> lstDataProvider) {
		CellTable<PersistentEvent> newTable = new CellTable<PersistentEvent>(15,
				(Resources) GWT.create(CellTableResources.class));
		newTable.setTableLayoutFixed(false);

		// Set row styles based on rent event type (i.e. rent or return)
		newTable.setRowStyles(new RowStyles<PersistentEvent>() {
			@Override
			public String getStyleNames(PersistentEvent row, int rowIndex) {
				if (row.getEventType() == EventType.Rented) {
					return "rentEventRowStyle";
				} else {
					return "returnEventRowtyle";
				}
			}
		});

		// Create the number column
		TextColumn<PersistentEvent> numberColumn = new TextColumn<PersistentEvent>() {

			@Override
			public String getValue(PersistentEvent object) {
				return String.valueOf(lstDataProvider.getList().indexOf(object));
			}
		};

		// Create name column.
		TextColumn<PersistentEvent> nameColumn = new TextColumn<PersistentEvent>() {
			@Override
			public String getValue(PersistentEvent persistentEvent) {
				return persistentEvent.getPersName() + "\n("
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
				String niceDate = DateTimeFormat.getFormat(
						PredefinedFormat.DATE_TIME_MEDIUM).format(
						persistentEvent.getEventDate());
				return niceDate;
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
		newTable.addColumn(numberColumn, "#");
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
		String parentWidth = String.valueOf(deckPanelActualView.getElement()
				.getClientWidth()) + "px";
		if (sender == rBtnOverview)
			deckPanelActualView.showWidget(0);
		if (sender == rBtnRent) {
			vPanelRentPage.setWidth(parentWidth);
			deckPanelActualView.showWidget(1);
		}
		if (sender == rBtnReturn) {
			captionPanelReturnPage.setWidth(parentWidth);
			deckPanelActualView.showWidget(2);
		}
		if (sender == rBtnHistory) {
			vPanelHistoryPage.setWidth(parentWidth);
			deckPanelActualView.showWidget(3);
		}
		if (sender == rBtnManage) {
			decoratedTabPanelDeviceManagement.setWidth(parentWidth);
			deckPanelActualView.showWidget(4);
		}
		if (sender == rBtnManageStudents) {
			decoratedTabPanelStudentsManagement.setWidth(parentWidth);
			deckPanelActualView.showWidget(5);
		}

		// History region
		if (sender == btnHistoryresetFilters) {
			resetHistoryFilters();
		}

		// Rent region
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

		// Return region
		if (sender == btnReturnClearCanvas) {
			clearReturnSignatureCanvas();
		}
		if (sender == btnReturnSubmit) {
			submitReturnEvent();
		}

		// Manage Devices region
		if (sender == btnManageDevicesAddNewDevice) {
			addNewDevice();
		}
		if (sender == btnManageDevicesAddScan) {
			startBarcodeScanner();
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

		// Manage students region
		if (sender == btnManageStudentsAddNewStudent) {
			addNewStudent();
		}
		if (sender == checkBoxManageStudentsViewEnableEdit) {
			toggleStudentDataFields(checkBoxManageStudentsViewEnableEdit.isChecked());
		}
		if (sender == btnManageStudentsViewUpdateDelete) {

		}
		if (sender == btnManageStudentsViewUpdate) {
			updateStudentInfo();
		}

	}

	private void startBarcodeScanner() {

		final DialogBox dBox = new DialogBox();
		dBox.setText("Scanning ...");
		dBox.setModal(true);
		dBox.setGlassEnabled(true);
		VerticalPanel vPanelScannerContent = new VerticalPanel();
		dBox.setWidget(vPanelScannerContent);
		// Add a close btn
		com.google.gwt.user.client.ui.Button btn = new com.google.gwt.user.client.ui.Button(
				"Close");
		btn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (dBox != null)
					dBox.hide();
			}
		});
		vPanelScannerContent.add(btn);

		// Add flash component
		SWFWidget swfWidget = new SWFWidget("qrread/qrread.swf", 300, 300);
		// swfWidget.addFlashVar("bridgeName", "example");
		vPanelScannerContent.add(swfWidget);

		vPanelScannerContent.add(barcodeResult);

		dBox.center();
		dBox.show();

	}

	static Label barcodeResult = new Label();
	
	private static void showresult(String result){
		if(result==null)
			return;
		barcodeResult.setText(result);
	}
	
	private native void publishShowResultMethod()
	/*-{
	$wnd.showresult = $entry(@de.tum.os.drs.client.view.MainPageBinder::showresult(Ljava/lang/String;));  
	}-*/;

	/**
	 * 
	 * @param source
	 *            - A String encoding a 2D array: 1111111111011101010000+1111111111011101010000+ Where the "+" sign means a new line
	 * @throws ReaderException
	 */
	private static void decode(String source) throws ReaderException {

		@SuppressWarnings("unused")
		int t = 99;
		int dimension = source.indexOf("+");
		if (dimension == -1)
			return;

		int strIndex, i = 0, j = 0;
		BitMatrix bits = new BitMatrix(dimension);

		for (strIndex = 0; strIndex < source.length(); strIndex++) {
			if (source.substring(strIndex, strIndex).equals("1")) {
				bits.set(i, j);
				j++;
			}
			if (source.substring(strIndex, strIndex).equals("0")) {
//				bits.set(i, j);
				j++;
			}
			if (source.substring(strIndex, strIndex).equals("+")) {
				i++;
				j = 0;
			}
		}

		try{
		Decoder decoder = new Decoder();
		DecoderResult decoderResult = decoder.decode(bits);

		String decodedString = decoderResult.getText();

		if (decodedString != null && !decodedString.isEmpty()) {
			barcodeResult.setText(decodedString);
		}
		}
		catch(Exception e){
			barcodeResult.setText(e.toString());
		}


		// RootPanel.get("resultView").clear();
		// RootPanel.get("resultView").add(new HTML("<h2>Decoded string:</h2>"));
		// RootPanel.get("resultView").add(new HTML("<p class='decoded'>" + decodedString +"</p>"));
	}

	// private native void publishDecodeMthod()
	// /*-{
	// $wnd.decode = @de.tum.os.drs.client.view.MainPageBinder::decode([[I);
	// }-*/;

	private native void publishDecodeMthod()
	/*-{
	$wnd.decode = $entry(@de.tum.os.drs.client.view.MainPageBinder::decode(Ljava/lang/String;));  
	}-*/;

	private void updateStudentInfo() {
		String studName = txtBoxManageStudentsViewStudentName.getText();
		String studMatric = txtBoxManageStudentsViewStudentMatriculation.getText();
		String studEmail = txtBoxManageStudentsViewStudentEmail.getText();
		String studPhone = txtBoxManageStudentsViewStudentPhone.getText();
		String studComments = txtBoxManageStudentsViewStudentComments.getText();
		if (studName == null || studName.isEmpty() || studMatric == null
				|| studMatric.isEmpty() || studEmail == null || studEmail.isEmpty()) {
			Info.display("Error!", "Name/Matriculation/Email cannot be empty!");
			return;
		}

		final SerializableRenter sr = new SerializableRenter(studName, studMatric,
				studEmail, studPhone, studComments,
				currentlyDisplayedSRenter.getRentedDevices());
		AsyncCallback<Boolean> updateStudentInfocallback = new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Success!", "Updated " + sr.getName());
					cBoxManageStudentsViewStudentMatriculation.setRawValue(sr
							.getMatriculationNumber());
					cBoxManageStudentsViewStudentName.setRawValue(sr.getName());
				} else {
					Info.display("Server error!", "Could not update" + sr.getName());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error!", "Could not update" + sr.getName());

			}
		};

		client.updateExistingRenter(currentlyDisplayedSRenter.getMatriculationNumber(),
				sr, updateStudentInfocallback);
	}

	private void toggleStudentDataFields(boolean checked) {
		btnManageStudentsViewUpdate.setEnabled(checked);
		txtBoxManageStudentsViewStudentName.setReadOnly(!checked);
		txtBoxManageStudentsViewStudentMatriculation.setReadOnly(!checked);
		txtBoxManageStudentsViewStudentEmail.setReadOnly(!checked);
		txtBoxManageStudentsViewStudentPhone.setReadOnly(!checked);
		txtBoxManageStudentsViewStudentComments.setReadOnly(!checked);

	}

	private void addNewStudent() {
		String studentName = txtBoxManageStudentsAddName.getText();
		String studentMatric = txtBoxManageStudentsAddMatriculation.getText();
		String studentEmail = txtBoxManageStudentsAddEmail.getText();
		String studentPhone = txtBoxManageStudentsAddPhone.getText();
		String studentComments = txtAreaManageStudentsAddComments.getText();
		if (studentName == null || studentName.isEmpty() || studentMatric == null
				|| studentMatric.isEmpty() || studentEmail == null
				|| studentEmail.isEmpty()) {
			Info.display("Error!", "Name, Matriculation or Email cannot be empty.");
			return;
		}

		final SerializableRenter sr = new SerializableRenter(studentName, studentMatric,
				studentEmail, studentPhone, studentComments, null);
		AsyncCallback<Boolean> addStudentCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Success!", "Added " + sr.getName());
					// Clear fields
					txtBoxManageStudentsAddEmail.setText("");
					txtBoxManageStudentsAddMatriculation.setText("");
					txtBoxManageStudentsAddName.setText("");
					txtBoxManageStudentsAddPhone.setText("");
					txtAreaManageStudentsAddComments.setText("");
				} else {
					Info.display("Server error!", "Could not add " + sr.getName());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error!", "Could not add " + sr.getName());
			}
		};
		client.addNewStudent(sr, addStudentCallback);
	}

	private void resetHistoryFilters() {
		cBoxHistoryFilterImei.setRawValue("");
		cBoxHistoryFilterName.setRawValue("");
		datePickerHistoryFilterFrom.clearState();
		datePickerHistoryFilterTo.clearState();
		datePickerHistoryFilterFrom.setValue(new Date(System.currentTimeMillis()
				- (1000L * 3600L * 24L * 30L * 6L)));
		datePickerHistoryFilterTo.setValue(new Date(System.currentTimeMillis()
				+ (1000 * 60 * 60 * 24)));
		txtBoxHistoryFilterFrom.setText("");
		txtBoxHistoryFilterTo.setText("");
		fetchEventsHistoryFiltered(true);

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
		txtBoxManageDevicesViewDevName.setReadOnly(!b);
		txtBoxManageDevicesViewDevImei.setReadOnly(!b);
		txtBoxManageDevicesViewDevComments.setReadOnly(!b);
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

		final PersistentDevice pd = new PersistentDevice(devIMEI, devName, devComments,
				devState, devType, devPictureName, new Boolean(true), null);
		AsyncCallback<Boolean> addDeviceResultCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				// Inform the user
				if (result) {
					Info.display("Success!", "Added a new {0}", pd.getName());
				} else {
					Info.display("Server Error!", "{0} - unknown state", pd.getName());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error!", "Could not communicate with the server.");
			}
		};
		client.addNewDevice(pd, addDeviceResultCallback);

		// Clear fields
		cBoxManageDevicesAddType.setSelectedIndex(0);
		cBoxManageDevicesAddName.setText("");
		cBoxManageDevicesAddState.setSelectedIndex(0);
		txtBoxManageDevicesAddIMEI.setText("");
		txtAreaManageDevicesAddComments.setText("");

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
			// handle click events from ext gwt buttons
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
		final String[] selectedImeiCodes = new String[selectedDevices.size()];
		int index = -1;
		while (selectedDevicesIt.hasNext()) {
			index++;
			selectedImeiCodes[index] = selectedDevicesIt.next().getIMEI();
		}
		String comments = txtAreaReturnComments.getText();
		String signature = canvasReturnSignature.toString();

		// Create return result callback
		AsyncCallback<Boolean> returnResultCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Success!", "Returned " + selectedImeiCodes.length
							+ " devices!");
				} else {
					Info.display("Server error!", "Could not return devices.");
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error", "Device status unknown!");

			}
		};
		// Submit
		client.returnDevices(returnResultCallback,
				selectedRenter.getMatriculationNumber(), selectedImeiCodes, comments,
				signature);

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
		// Create a callback to report on success/failure
		AsyncCallback<Boolean> rentDevicesCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Success!", "Rented " + selectedDevicesImeis.length
							+ " device(s)!");
				} else {
					Info.display("Server!", "Could not rent devices!");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Network error!", "Devices inunknown state!");

			}
		};
		Date estimatedReturnDate = datePickerRentEstimatedRD.getValue();
		if (sr == null && renterMatr != null && !renterMatr.isEmpty()) {
			// Add devices to a registered student
			String rentEventComments = txtAreaRentComments.getText() != null ? txtAreaRentComments
					.getText() : " ";
			this.client.rentDevicesToExistingRenter(rentDevicesCallback, renterMatr,
					selectedDevicesImeis, estimatedReturnDate, rentEventComments,
					canvasRentSignature.toString());

		} else {
			final String rentEventComments = txtAreaRentComments.getText() != null ? txtAreaRentComments
					.getText() : " ";
			if (sr != null) {
				// Add new renter and add to his rented devices list.
				client.rentDevicesToNewRenter(rentDevicesCallback, sr,
						sr.getMatriculationNumber(), selectedDevicesImeis,
						estimatedReturnDate, rentEventComments,
						canvasRentSignature.toString());
			}
		}

		// Clean up.
		// Clean list of selected devices
		lstViewRentSelectedDevices.getStore().removeAll();

		// Set estimated return date to six weeks from now
		datePickerRentEstimatedRD.setValue(new Date(System.currentTimeMillis()
				+ (1000L * 60L * 60L * 24L * 7L * 6L)));

		// Clean Signature
		clearRentSignatureCanvas();

		// Clear comments
		txtAreaRentComments.setText("");

		// Clear new person data
		resetNewStudentFields();
		resetSelectedStudent();

	}

	private void fetchEventsHistoryFiltered(boolean ignoreFilters) {
		if (ignoreFilters) {
			client.fetchEventsHistoryFiltered(null, null, null, null, Integer.MAX_VALUE,
					true);
			return;
		}

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
		if (cBoxRentSelectDevice.getSelection()!=null && cBoxRentSelectDevice.getSelection().size() == 0)
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
				fetchEventsHistoryFiltered(false);
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
				fetchEventsHistoryFiltered(false);
			}
		}
	}
}
