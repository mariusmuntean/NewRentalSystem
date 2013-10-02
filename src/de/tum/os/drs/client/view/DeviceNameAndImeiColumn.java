package de.tum.os.drs.client.view;

import com.google.gwt.user.cellview.client.Column;

import de.tum.os.drs.client.model.PersistentEvent;

/**
 * Custom column for displaying the device name and IMEI code of a {@link PersistentEvent} in a table
 * @author marius
 *
 */
public class DeviceNameAndImeiColumn extends
		Column<PersistentEvent, PersistentEvent> {

	public DeviceNameAndImeiColumn() {
		super(new DeviceNameAndImeiCell());
	}

	@Override
	public PersistentEvent getValue(PersistentEvent object) {
		return object;
	}

}
