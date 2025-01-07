package com.sap.engine.deployment;

import javax.enterprise.deploy.spi.Target;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

/**
 * A Target interface represents a single logical core server of one instance of
 * a J2EE platform product. It is a designator for a server and the implied
 * location to copy a configured application for the server to access.
 * 
 * @author Mariela Todorova
 */
public class SAPTarget implements Target {
	private static final Location location = Location
			.getLocation(SAPTarget.class);
	private String name = null;
	private int id = 0;

	public SAPTarget(String nodeName, int nodeID) {
		this.name = nodeName;
		this.id = nodeID;
		Logger.trace(location, Severity.DEBUG, "SAP target " + name + " - "
				+ id);
	}

	/**
	 * Retrieve the name of the target server.
	 */
	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	/**
	 * Retrieve other descriptive information about the target.
	 */
	public String getDescription() {
		return "AS Java";
	}

	public String toString() {
		return name + " - " + id;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SAPTarget)) {
			return false;
		}

		SAPTarget target = (SAPTarget) obj;

		if (this.id != target.id) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id;
	}

}
