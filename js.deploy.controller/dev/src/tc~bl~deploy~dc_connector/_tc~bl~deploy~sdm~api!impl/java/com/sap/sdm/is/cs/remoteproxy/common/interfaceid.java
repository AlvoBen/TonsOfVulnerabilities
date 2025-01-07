package com.sap.sdm.is.cs.remoteproxy.common;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class InterfaceID {

	private final String clientClassName;
	private final String ID;
	private final int myHashCode;
	private final String stringRep;

	InterfaceID(String clientClassName, String ID) {
		this.clientClassName = clientClassName;
		this.ID = ID;
		myHashCode = this.clientClassName.hashCode() + this.ID.hashCode();
		stringRep = "InterfaceID(" + ID + ", " + clientClassName + ")";
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;

		if (other == null)
			return false;

		// this is the only way to get the equals method symmetric, that is
		// to guarantee this.equals(other) == other.equals(this)
		// (don't use instanceof here!)
		if (this.getClass() != other.getClass())
			return false;
		InterfaceID otherIFID = (InterfaceID) other;
		if ((otherIFID.getClientClassName() == null)
				|| (this.getClientClassName() == null)) {
			return false;
		}
		if ((otherIFID.getInstanceID() == null)
				|| (this.getInstanceID() == null)) {
			return false;
		}
		if ((otherIFID.getInstanceID().equals(this.getInstanceID()))
				&& (otherIFID.getClientClassName().equals(this
						.getClientClassName()))) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		return myHashCode;
	}

	public String toString() {
		return stringRep;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.InterfaceID#getClientClassName()
	 */
	public String getClientClassName() {
		return this.clientClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.InterfaceID#getInstanceID()
	 */
	public String getInstanceID() {
		return this.ID;
	}

}
