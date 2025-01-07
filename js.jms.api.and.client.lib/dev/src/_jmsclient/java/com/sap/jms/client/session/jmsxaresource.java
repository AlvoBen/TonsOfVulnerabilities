package com.sap.jms.client.session;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.sap.jms.client.connection.ServerFacade.TimeoutData;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

public class JMSXAResource implements javax.transaction.xa.XAResource {

	private JMSXASession session;

	
	// active timeout in miliseconds	
	private long activeTimeout = 0;
	private long heuristicTimeout = 0;
	private AtomicReference<Xid> activeXid;

	public JMSXAResource(JMSXASession session) {
		this.session = session;
		activeXid = new AtomicReference<Xid>();
	}


	public void commit(Xid xid, boolean onePhase) throws XAException {
		xid = new JMSXid(xid);
		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will commit transaction with xid: ", xid, " one phase: ", onePhase, " from session with id: ", ((JMSSession) session.getSession()).getSessionID());
        }
		
		session.xaCommit(xid, onePhase);
	}

	public void end(Xid xid, int flags) throws XAException {
		xid = new JMSXid(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will end transaction with xid: ", xid, " flags: ", flags, " from session with id: ", ((JMSSession) session.getSession()).getSessionID());
        }

		activeXid.set(null);

		session.xaEnd(xid, flags);
	}

	public void forget(Xid xid) throws XAException {
		xid = new JMSXid(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will forget transaction with xid: ", xid, " from session with id: ", ((JMSSession) session.getSession()).getSessionID());
        }

		session.xaForget(xid);
	}

	public int getTransactionTimeout() throws XAException {
		// TODO i ssessionId really needed
		int sessionId = ((JMSSession)session.getSession()).getSessionID();
		if (activeTimeout == 0 || heuristicTimeout == 0) {
			TimeoutData td = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaGetTimeout(sessionId);
			activeTimeout = td.activeTimeout;
			heuristicTimeout = td.heuristicTimeout;
		}

		return (int)(activeTimeout/1000);
	}

	public boolean isSameRM(XAResource xaResource) throws XAException {

		if (!(xaResource instanceof JMSXAResource)) {
			return false;
		}

		return true;
	}

	public int prepare(Xid xid) throws XAException {
		xid = new JMSXid(xid);

		int vote = session.xaPrepare(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Prepare for transaction with xid: ", xid, " resulted in vote: ", vote);
        }
		
		return vote;
	}

	public Xid[] recover(int flags) throws XAException {
		Set<Xid> xids = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaRecover(flags);

		Set<Xid> more_xids = session.xaRecover();
		xids.addAll(more_xids);
		
		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Recover returned transactions with xids: ", xids);
        }

		return xids.toArray(new Xid[xids.size()]);
	}

	public void rollback(Xid xid) throws XAException {
		xid = new JMSXid(xid);

		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will rollback transaction with xid: ", xid, " from session with id: ", ((JMSSession) session.getSession()).getSessionID());
        }

		try {
			session.recover();
		} catch (JMSException e) {
			Logging.exception(this, e);
		}

		session.xaRollback(xid);

	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		int sessionId = ((JMSSession)session.getSession()).getSessionID();
		TimeoutData td = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaGetTimeout(sessionId);
		heuristicTimeout = td.heuristicTimeout;
		if (seconds == 0) {
			activeTimeout = td.activeTimeout;
		} else {
			activeTimeout = seconds * 1000;		
		}

		return true;
	}

	public void start(Xid xid, int flags) throws XAException {

		xid = new JMSXid(xid);
		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will start transaction with xid: ", xid, " flags: ", flags, " from session with id: ", ((JMSSession) session.getSession()).getSessionID());
        }

		if (activeTimeout == 0 || heuristicTimeout == 0) {
			int sessionId = ((JMSSession)session.getSession()).getSessionID();
			TimeoutData td = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaGetTimeout(sessionId);
			activeTimeout = td.activeTimeout;
			heuristicTimeout = td.heuristicTimeout;
		}
		
		session.xaStart(xid, flags, activeTimeout);

		activeXid.set(xid);
	}

	public void onActiveTimeout(Xid xid) {
		if (activeXid.get() != null && activeXid.get().equals(xid)) {
			activeXid.set(null);
		}

		session.onActiveTimeout(xid, heuristicTimeout);
	}

	public boolean isTransactionActive(){
		if (activeXid.get() == null) {
			return false;
		}
		
		return true;
	}
	
	Xid getActiveXid() {
		return activeXid.get();
	}

}
