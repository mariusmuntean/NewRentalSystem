package de.tum.os.drs.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class RenterTreeViewModel implements TreeViewModel {

//	private ArrayList<SerializableRenter> rentersModel;
	private ListDataProvider<SerializableRenter> rentersModel;
	private ListDataProvider<PersistentDevice> rentedDevices;

	/**
	 * This selection model is shared across all leaf nodes. A selection model can also be shared across all nodes in the tree, or each
	 * set of child nodes can have its own instance. This gives you flexibility to determine how nodes are selected.
	 */
	private final SingleSelectionModel<PersistentDevice> selectionModel = new SingleSelectionModel<PersistentDevice>();

	public RenterTreeViewModel(ListDataProvider<SerializableRenter> rentersModel,
			ListDataProvider<PersistentDevice> rentedDevices) {
		if (rentersModel != null)
			this.rentersModel = rentersModel;

		if (rentedDevices != null)
			this.rentedDevices = rentedDevices;
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
//			ListDataProvider<SerializableRenter> dataProvider = new ListDataProvider<SerializableRenter>(
//					rentersModel);
			// Create a cell to display a renter.
			Cell<SerializableRenter> cell = new AbstractCell<SerializableRenter>() {
				@Override
				public void render(com.google.gwt.cell.client.Cell.Context context,
						SerializableRenter value, SafeHtmlBuilder sb) {
					sb.appendEscaped(value.getName());

				}
			};
			// Return a node info that pairs the data provider and the cell.
			return new DefaultNodeInfo<SerializableRenter>(this.rentersModel, cell);
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
						sb.appendEscaped(value.getName() + "(" + value.getIMEI() + ")");

					}
				};

				return new DefaultNodeInfo<PersistentDevice>(dataProvider, cell,
						selectionModel, null);
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
