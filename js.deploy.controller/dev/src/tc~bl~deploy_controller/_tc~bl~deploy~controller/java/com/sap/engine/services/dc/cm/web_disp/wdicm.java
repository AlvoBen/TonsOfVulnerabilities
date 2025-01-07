package com.sap.engine.services.dc.cm.web_disp;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class WDICM implements Serializable {

	private static final long serialVersionUID = -357936637374975650L;

	protected final String name;
	protected final Set<WDServerPort> wdServerPorts;

	private int hashCode = -1;
	private String toString = null;

	public WDICM(String name) {
		if (name == null) {
			throw new NullPointerException("The 'name' field cannot be -1.");
		}
		this.name = name;
		this.wdServerPorts = new LinkedHashSet<WDServerPort>();
	}

	public void addWDServerPort(WDServerPort serverPort) {
		wdServerPorts.add(serverPort);
	}

	public String getName() {
		return name;
	}

	public Set<WDServerPort> getWDServerPorts() {
		return wdServerPorts;
	}

	// ****************************************//
	public int hashCode() {
		if (hashCode == -1) {
			hashCode = generateHashCode();
		}
		return hashCode;
	}

	public String toString() {
		if (toString == null) {
			toString = generateToString();
		}
		return toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		final WDICM otherWDICM = (WDICM) obj;

		if (this.getName() != otherWDICM.getName()) {
			return false;
		}

		if (!this.getWDServerPorts().equals(otherWDICM.getWDServerPorts())) {
			return false;
		}

		return true;
	}

	// ****************************************//

	private int generateHashCode() {
		int result = 17 + getName().hashCode();
		result = result * 59 + getWDServerPorts().hashCode();
		return result;
	}

	private String generateToString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(Constants.EOL);
		sb.append(getWDServerPorts());
		sb.append(Constants.EOL);
		return sb.toString();
	}

}
