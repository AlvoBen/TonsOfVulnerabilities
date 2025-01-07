package com.sap.engine.services.dc.cm.web_disp;

import com.sap.engine.services.dc.cm.dscr.impl.ServerPort;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class WDServerPort extends ServerPort {

	private static final long serialVersionUID = 6641726966382188632L;

	public enum LB {
		not_active, active;
	}

	protected final String host;
	protected LB lb;

	private int hashCode = -1;
	private String toString = null;

	public WDServerPort(Type type, String host, int port, LB lb) {
		super(type, port);
		if (host == null) {
			throw new NullPointerException("The 'host' field cannot be null.");
		}
		this.host = host;
		if (lb == null) {
			throw new NullPointerException("The 'lb' field cannot be null.");
		}
		this.lb = lb;
	}

	public String getHost() {
		return host;
	}

	public LB getLb() {
		return lb;
	}

	public void setLb(LB lb) {
		if (this.getLb().equals(lb)) {
			// do nothing
		} else {
			hashCode = -1;
			toString = null;
			this.lb = lb;
		}
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

		final WDServerPort otherServerPort = (WDServerPort) obj;

		if (!super.equals(otherServerPort)) {
			return false;
		}

		if (!this.getHost().equals(otherServerPort.getHost())) {
			return false;
		}

		if (!this.getLb().equals(otherServerPort.getLb())) {
			return false;
		}

		return true;
	}

	// ****************************************//

	private int generateHashCode() {
		int result = 17 + super.hashCode();
		result = result * 59 + getHost().hashCode();
		result = result * 59 + getLb().hashCode();
		return result;
	}

	private String generateToString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getType());
		sb.append(Constants.TAB);
		sb.append(getHost());
		sb.append(Constants.TAB);
		sb.append(getPort());
		sb.append(Constants.TAB);
		sb.append(getLb());
		sb.append(Constants.EOL);
		return sb.toString();
	}

}
