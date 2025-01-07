package com.sap.jms.client.connection;

import java.io.Serializable;

public class ConnectionData implements Serializable {
	
	private long connectionId;
	private ServerFacade facade = null;
	private int serverVersion = -1;
	
	public ConnectionData(long connectionId, ServerFacade facade, int serverVersion) {
		this.connectionId = connectionId;
		this.facade = facade;
		this.serverVersion = serverVersion;
	}

	public long getConnectionId() {
		return connectionId;
	}	
	
	public ServerFacade getServerFacade() {
		return facade;
	}
	
	public int getServerVersion() {
		return serverVersion;
	}
}
