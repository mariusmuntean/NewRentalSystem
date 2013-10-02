package de.tum.os.drs.client.view;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import de.tum.os.drs.client.model.PersistentEvent;

public class DeviceNameAndImeiCell extends AbstractCell<PersistentEvent>
		implements Cell<PersistentEvent> {

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			PersistentEvent value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<div style=\"padding:0px; text-align: center\">");
		if (value != null) {
			sb.appendHtmlConstant("<b style=\"font-size:13px\">"
					+ value.getDevName() + "</b>");
			sb.appendHtmlConstant(" - <i>" + value.getImeiCode() + "</i>");
		}
		sb.appendHtmlConstant("</div>");

	}

}
