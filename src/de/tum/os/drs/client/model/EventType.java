package de.tum.os.drs.client.model;

/**
 * Enum used to denote a rental event.
 * 
 * @author Marius
 * 
 */
public enum EventType {
	Rented(0), Returned(1);

	private final int index;

	EventType(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

}
