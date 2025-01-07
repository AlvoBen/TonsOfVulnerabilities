package com.sap.jms.client.session;
import java.util.TimerTask;

import javax.transaction.xa.Xid;


class XAInMemoryAbandonementTimeoutTask  extends TimerTask {

	private Xid xid;
	private JMSXASession session;

	public XAInMemoryAbandonementTimeoutTask(Xid xid, JMSXASession session) {
		this.xid = xid;
		this.session = session;
	}

	public void run() {
		session.onInMemoryAbandonmentTimeout(xid);
	}
}
