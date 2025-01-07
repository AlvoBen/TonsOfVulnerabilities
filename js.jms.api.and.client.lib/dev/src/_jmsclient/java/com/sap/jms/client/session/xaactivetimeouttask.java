package com.sap.jms.client.session;

import java.util.TimerTask;

import javax.transaction.xa.Xid;

class XAActiveTimeoutTask extends TimerTask {

	private Xid xid;
	private JMSXAResource resource;

	public XAActiveTimeoutTask(Xid xid, JMSXAResource resource) {
		this.xid = xid;
		this.resource = resource;
	}

	public void run() {
		resource.onActiveTimeout(xid);
	}
}
