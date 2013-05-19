package de.tum.os.drs.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class RenterTreeViewModel implements TreeViewModel {

	// private ArrayList<SerializableRenter> rentersModel;
	private ListDataProvider<SerializableRenter> rentersModel;
	private ListDataProvider<PersistentDevice> rentedDevices;

	/**
	 * This selection model is shared across all leaf nodes. A selection model can also be shared across all nodes in the tree, or each
	 * set of child nodes can have its own instance. This gives you flexibility to determine how nodes are selected.
	 */
	private final MultiSelectionModel<PersistentDevice> deviceSelectionModel = new MultiSelectionModel<PersistentDevice>();
	private final SingleSelectionModel<SerializableRenter> renterSelectionModel = new SingleSelectionModel<SerializableRenter>();

	public RenterTreeViewModel(ListDataProvider<SerializableRenter> rentersModel,
			ListDataProvider<PersistentDevice> rentedDevices,
			SelectionChangeEvent.Handler deviceSelectionHandler) {
		if (rentersModel != null)
			this.rentersModel = rentersModel;

		if (rentedDevices != null)
			this.rentedDevices = rentedDevices;

		deviceSelectionModel.addSelectionChangeHandler(deviceSelectionHandler);
	}

	/**
	 * 
	 * @return - The currently selected Renter or null.
	 */
	public SerializableRenter getSelectedRenter() {

		return renterSelectionModel.getSelectedObject();
	}

	/**
	 * 
	 * @return - A Set of currently selected devices.
	 */
	public HashSet<PersistentDevice> getSelectedDevices() {
		return (HashSet<PersistentDevice>) deviceSelectionModel.getSelectedSet();
	}

	/**
	 * 
	 * @param sr
	 *            - Marks the given SerializableRenter instance as selected.
	 */
	public void selectRenter(SerializableRenter sr) {
//		if (renterSelectionModel.getSelectedSet() == null
//				|| renterSelectionModel.getSelectedSet().size() <= 0
//				|| !renterSelectionModel.getSelectedSet().contains(sr) || sr == null) {
//			return;
//		}
		if(sr==null)
			return;
		renterSelectionModel.clear();
		renterSelectionModel.getSelectedSet().clear();
		renterSelectionModel.getSelectedSet().add(sr);
		renterSelectionModel.setSelected(sr, true);
	}

	/**
	 * 
	 * @param matriculation - Searches for an instance of SerializableRenter that has the same Matriculation number. If 
	 *            				one is found it marks it as selected.
	 */
	public void selectRenter(String matriculation) {
		SerializableRenter sr = getRenterByMatriculation(matriculation);
		if (sr == null)
			return;

		selectRenter(sr);
	}

	/**
	 * 
	 * @param matric
	 *            - A matriculation number.
	 * @return - A SerializableRenter instance from the view model that has the same matriculation number as the one provided. Null if
	 *         there's no such serializableRenter
	 */
	public SerializableRenter getRenterByMatriculation(String matric) {
		if (matric == null || matric.length() <= 0) {
			return null;
		}

		if (rentersModel == null || rentersModel.getList() == null
				|| rentersModel.getList().size() <= 0) {
			return null;
		}

		SerializableRenter sr = null;
		for (SerializableRenter _sr : rentersModel.getList()) {
			if (_sr.getMatriculationNumber().equals(matric)) {
				sr = _sr;
				break;
			}
		}
		return sr;
	}
	
	public int getRenterIndexByMatriculation(String matric) {
		if (matric == null || matric.length() <= 0) {
			return -1;
		}

		if (rentersModel == null || rentersModel.getList() == null
				|| rentersModel.getList().size() <= 0) {
			return -1;
		}

		int index = -1;
		for (SerializableRenter _sr : rentersModel.getList()) {
			index++;
			if (_sr.getMatriculationNumber().equals(matric)) {
				break;
			}
		}
		return index;
	}

	/**
	 * For a given SerializableRenter instance the method returns a list of rented devices.
	 * 
	 * @param sr
	 *            - The instance of SerializableRenter to get the rented devices for.
	 * @return - An ArrayList<PersistentDevices> representing the devices rented by sr.
	 */
	private ArrayList<PersistentDevice> getDevicesRentedBy(SerializableRenter sr) {
		ArrayList<PersistentDevice> devices = new ArrayList<PersistentDevice>();
		if (sr != null && rentedDevices != null && rentedDevices.getList().size() > 0) {
			HashMap<String, PersistentDevice> imeiToDeviceMapping = new HashMap<String, PersistentDevice>();
			for (PersistentDevice pd : rentedDevices.getList()) {
				imeiToDeviceMapping.put(pd.getIMEI(), pd);
			}

			for (String imei : sr.getRentedDevices()) {
				if (imeiToDeviceMapping.containsKey(imei)) {
					devices.add(imeiToDeviceMapping.get(imei));
				}
			}
		}
		return devices;
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {

		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the renters.
			// Create a data provider that contains the list of renters.
			// ListDataProvider<SerializableRenter> dataProvider = new ListDataProvider<SerializableRenter>(
			// rentersModel);
			// Create a cell to display a renter.
			Cell<SerializableRenter> cell = new AbstractCell<SerializableRenter>() {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context,
						SerializableRenter value, SafeHtmlBuilder sb) {
					Label lblName = new Label(value.getName());
					lblName.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					Label lblMatric = new Label(value.getMatriculationNumber());
					lblMatric.getElement().getStyle().setFontWeight(FontWeight.LIGHTER);
					sb.append(SafeHtmlUtils.fromTrustedString(lblName.getElement()
							.toString()));
					sb.append(SafeHtmlUtils.fromTrustedString(lblMatric.getElement()
							.toString()));

				}
			};
			// Return a node info that pairs the data provider and the cell.
			return new DefaultNodeInfo<SerializableRenter>(this.rentersModel, cell,
					renterSelectionModel, null);
		} else {
			if (value instanceof SerializableRenter) {
				// LEVEL 1 - LEAF.
				// We want the children of the renters. Return the rented devices.
				ListDataProvider<PersistentDevice> dataProvider = new ListDataProvider<PersistentDevice>(
						getDevicesRentedBy((SerializableRenter) value));
				// Use the shared selection model.

				Cell<PersistentDevice> cell = new AbstractCell<PersistentDevice>() {
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context,
							PersistentDevice value, SafeHtmlBuilder sb) {
						Label lblName = new Label(value.getName());
						lblName.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						Label lblImei = new Label(value.getIMEI());
						lblImei.getElement().getStyle().setFontWeight(FontWeight.LIGHTER);
						sb.append(SafeHtmlUtils.fromTrustedString(lblName.getElement()
								.toString()));
						sb.append(SafeHtmlUtils.fromTrustedString(lblImei.getElement()
								.toString()));
					}
				};

				return new DefaultNodeInfo<PersistentDevice>(dataProvider, cell,
						deviceSelectionModel, null);
			}
		}

		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		if (value instanceof PersistentDevice) {
			return true;
		}

		return false;
	}

}
