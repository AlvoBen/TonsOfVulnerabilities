/**
 * Destination.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.destination;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.jms.client.connection.Connection;
import com.sap.jms.client.connection.DestinationInfo;
import com.sap.jms.client.connection.DestinationInfo.DestinationType;

public class JMSDestination implements javax.jms.Destination, java.io.Serializable {

	static final long serialVersionUID = -1215450645697662945L;

	private static final transient Map<Integer, DestinationInfo> destinationsCache = new ConcurrentHashMap<Integer, DestinationInfo>();

	protected String name = null;
	protected String vpName = null;
	protected int destinationId = 0;
	protected boolean isQueue;
	protected boolean isTemporary = false;

	protected JMSDestination(String name, int destinationId, String vpName, boolean isQueue, boolean isTemporary) {
		this.destinationId = destinationId;    
		this.name = name;
		this.vpName = vpName;
		this.isQueue = isQueue;
		this.isTemporary = isTemporary;
	}

	public final static DestinationInfo getDestination(int destinationId) {
		return destinationsCache.get(destinationId);
	}

	public final static void setDestination(DestinationInfo destination) {
		int destinationId = destination.getId();
		if (!destinationsCache.containsKey(destinationId)) {  
			destinationsCache.put(destinationId, destination);
		}
	}

	public static JMSDestination resolveDestination(DestinationInfo info, Connection connection) {
		JMSDestination destination = null;
	
		if (info.getType() == DestinationType.QUEUE) {
			if (info.isTemporary()) {
				if (connection != null) {
					destination = new JMSTemporaryQueue(info.getName(), info.getId(), info.getVpName(), connection);
				} else {
					destination = new JMSTemporaryQueue(info.getName(), info.getId(), info.getVpName());
				}
			} else {
				destination = new JMSQueue(info.getName(), info.getId(), info.getVpName());
			}
		} else {
			if (info.isTemporary()) {
				if (connection != null) {
					destination = new JMSTemporaryTopic(info.getName(), info.getId(), info.getVpName(), connection);
				} else {
					destination = new JMSTemporaryTopic(info.getName(), info.getId(), info.getVpName());
				}	
			} else {
				destination = new JMSTopic(info.getName(), info.getId(), info.getVpName());
			}						
		}									
		return destination;
	}

	public DestinationInfo getDestinationInfo() {
		DestinationInfo info = new DestinationInfo();
		info.setId(getDestinationId());
		info.setName(getName());
		info.setType(isQueue() ? DestinationType.QUEUE : DestinationType.TOPIC);
		info.setVpName(getVPName());
		return info;
	}


	public String getVPName() {
		return vpName;
	}

	public int getDestinationId() {
		return destinationId;
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
		return ((ob instanceof JMSDestination) && ((((JMSDestination) ob).destinationId) == destinationId));
	}

	public int hashCode() {
		return destinationId;
	}

	public String toString() {
        StringBuffer text = new StringBuffer();
        text.append(" [ ");       
        text.append(super.toString());
        text.append(", destinationId = " + getDestinationId());
        text.append(", name = " + getName());
        text.append(", vpName = " + getVPName());        
		if (isTemporary) {
			text.append("Temporary ");
		}
		if (isQueue) {
			text.append("Queue ");
		} else {
			text.append("Topic ");
		}
        text.append(" ] ");       
        return text.toString();
	}
}
