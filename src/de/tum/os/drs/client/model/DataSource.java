package de.tum.os.drs.client.model;

import java.util.ArrayList;
import java.util.List;


public class DataSource {

	  private final List<PersistentDevice> devices;
	  private List<String> header;

	  public DataSource(List<PersistentDevice> devices) {
	    header = new ArrayList<String>();
	    header.add("Name");
	    header.add("Description");
	    header.add("Imei");
	    this.devices = devices;
	  }

	  public List<PersistentDevice> getDevices() {
	    return devices;
	  }

	  public List<String> getTableHeader() {
	    return header;
	  }

	}