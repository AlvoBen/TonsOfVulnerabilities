/**
 * Destination.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.destination;

import java.util.Map;
import com.sap.jms.util.compat.concurrent.ConcurrentHashMap;

import com.sap.jms.util.JNDIHelper;

public class JMSDestination implements javax.jms.Destination, java.io.Serializable {

	static final long serialVersionUID = -1215450645697662945L;

	private static final Map/*<Integer, String>*/ destinationsCache = new ConcurrentHashMap/*<Integer, String>*/();

	protected String name = null;
	protected String instanceName = null;
	protected int destinationID = 0;
	protected boolean isQueue;
	protected boolean isTemporary = false;

	protected JMSDestination(String name, int destinationID) {
		this.destinationID = destinationID;    
		this.name = name;
	}

	public final static String getNameForID(int destinationID) {
		return (String)destinationsCache.get(new Integer(destinationID));
	}

	public final static void setIDNameMapping(int destinationID, String name) {
		if (!destinationsCache.containsKey(new Integer(destinationID))) {  
			destinationsCache.put(new Integer(destinationID), name);
		}
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public int getDestinationID() {
		return destinationID;
	}

	public String getName() {
		return name;
	}

	public boolean isQueue() {
		return isQueue;
	}

	public boolean isTemporaryQueue() {
		return isQueue && isTemporary;
	}

	public boolean isTopic() {
		return !isQueue;
	}

	public boolean isTemporaryTopic() {
		return !isQueue && isTemporary;
	}

	public boolean isTemporary() {
		return isTemporary;
	}

	public boolean equals(Object ob) {
		return ((ob instanceof JMSDestination) && ((((JMSDestination) ob).destinationID) == destinationID));
	}

	public int hashCode() {
		return destinationID;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		if (isTemporary) {
			buffer.append("Temporary ");
		}
		
		if (isQueue) {
			buffer.append("Queue ");
		} else {
			buffer.append("Topic ");
		}

		if (name != null) {
			buffer.append("with name: ");
			buffer.append(name);
			buffer.append(" ");
		}

		buffer.append("with destination id: ");
		buffer.append(destinationID);

		return buffer.toString();
	}

}
