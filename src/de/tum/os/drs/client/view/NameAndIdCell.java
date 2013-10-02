package de.tum.os.drs.client.view;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import de.tum.os.drs.client.model.PersistentEvent;

public class NameAndIdCell extends AbstractCell<PersistentEvent> implements
		Cell<PersistentEvent> {

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			PersistentEvent value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<div style=\"padding:0px; text-align: center\">");
		if (value != null) {
			sb.appendHtmlConstant("<b style=\"font-size:16px\">"
					+ value.getPersName() + "</b>");
			sb.appendHtmlConstant("<br><i>"
					+ value.getPersMatriculationNumber() + "</i></br>");
		}
		sb.appendHtmlConstant("</div>");

	}

}
