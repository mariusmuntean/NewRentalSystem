package de.tum.os.drs.client.view;

import com.google.gwt.user.cellview.client.Column;

import de.tum.os.drs.client.model.PersistentEvent;

public class NameAndIdColumn extends Column<PersistentEvent, PersistentEvent> {

	public NameAndIdColumn() {
		super(new NameAndIdCell());
	}

	@Override
	public PersistentEvent getValue(PersistentEvent object) {
		// TODO Auto-generated method stub
		return object;
	}

}
