package com.sap.jms.client.rmi;

import java.io.Serializable;

import com.sap.engine.interfaces.cross.ObjectIdentifier;

public class ConnectionFactoryObjectIdentifier implements Serializable, ObjectIdentifier {
	
	private String redirectableKey = null;
	
	public ConnectionFactoryObjectIdentifier(String redirectableKey) {
		this.redirectableKey = redirectableKey;
	}
	
	public String _getFactoryName() {
		return RMIConnectionFactoryInterface.CrossObjectFactoryName;
	}
	public Serializable _getObjectId() {
		return redirectableKey;
	}	
	
	public String toString() {
		return "RedirectableKey: " + redirectableKey + "; CrossObjectFactoryName: " + RMIConnectionFactoryInterface.CrossObjectFactoryName;
	}

}
