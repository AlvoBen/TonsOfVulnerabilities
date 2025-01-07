/*
 * Created on 2005-4-20 by radoslav-i
 */
package com.sap.engine.services.dc.cmd.telnet.impl.util;

import com.sap.engine.services.dc.util.Constants;

/**
 * @author radoslav-i
 */
final public class Argument {

	private final static int OFFSET = 17;
	private final static int MULTIPLIER = 59;

	private final String key;
	private final String value;
	private final int baseHashCode;
	private final String toString;

	public Argument(String key, String value) {
		if (value == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3458] Argument with key '" + key
							+ "' cannot have value null.");
		}

		this.key = key;
		this.value = value;
		this.baseHashCode = generateBaseHashCode();
		this.toString = getToString();
	}

	public Argument(String value) {
		this(null, value);
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Argument) {
			Argument objArg = (Argument) obj;
			if (this.key == objArg.getKey()) {
				return this.value.equals(objArg.getValue());
			}
			if (this.key != null && this.key.equals(objArg.getKey())) {
				return this.value.equals(objArg.getValue());
			}
			return false;
		}
		return false;
	}

	public int hashCode() {
		return this.baseHashCode;
	}

	private int generateBaseHashCode() {
		int result = OFFSET + this.value.hashCode();
		if (this.key != null) {
			result = result * MULTIPLIER + this.key.hashCode();
		}
		return result;
	}

	/*
	 * todo (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.toString;
	}

	private String getToString() {
		final StringBuffer sbTosTring = new StringBuffer();
		sbTosTring.append("argument key: ").append(this.getKey()).append(
				Constants.EOL).append("argument value: ").append(
				this.getValue()).append(Constants.EOL);

		return sbTosTring.toString();
	}
}
