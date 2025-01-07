package com.sap.jms.client.xa;

import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.sap.jms.util.compat.concurrent.atomic.AtomicBoolean;
import com.sap.jms.util.compat.concurrent.atomic.AtomicReference;
import com.sap.jms.client.session.JMSSession;
import com.sap.jms.util.logging.LogServiceImpl;
import com.sap.jms.util.logging.LogService;


public class JMSXAResource implements javax.transaction.xa.XAResource {

	private JMSXASession session;

	private LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private String LNAME;
	// active timeout in miliseconds	
	private long activeTimeout = 0;
	private AtomicReference/*<Xid>*/ activeXid;

	public JMSXAResource(JMSXASession session) {
		this.session = session;
		activeXid = new AtomicReference/*<Xid>*/();
		LNAME = getClass().getName();
	}


	public void commit(Xid xid, boolean onePhase) throws XAException {
		log.debug(LNAME, "Will commit transaction with xid: " + new JMSXid(xid) + " one phase: " + onePhase + " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );
		((JMSSession)session.getSession()).getConnection().getServerFacade().xaCommit(xid, onePhase);
	}

	public void end(Xid xid, int flags) throws XAException {

		log.debug(LNAME, "Will end transaction with xid: " + new JMSXid(xid) + " flags: " + flags+ " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );

		activeXid.set(null);

		Map/*<Long, Set<Long>>*/ dlvrdMsgsPerConsumer = (Map)session.getDlvrdMsgsPerConsumer();

		((JMSSession)session.getSession()).getConnection().getServerFacade().xaEnd(xid, flags, dlvrdMsgsPerConsumer);
	}

	public void forget(Xid xid) throws XAException {
		log.debug(LNAME, "Will forget transaction with xid: " + new JMSXid(xid)+ " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );

		((JMSSession)session.getSession()).getConnection().getServerFacade().xaForget(xid);
	}

	public int getTransactionTimeout() throws XAException {
		// TODO i ssessionId really needed
		int sessionId = ((JMSSession)session.getSession()).getSessionID();
		if (activeTimeout == 0) {
			activeTimeout = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaGetTimeout(sessionId);
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
		log.debug(LNAME, "Will prepare transaction with xid: " + new JMSXid(xid)+ " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );

		int vote = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaPrepare(xid);

		log.debug(LNAME, "Prepare for transaction with xid: " + new JMSXid(xid)+ " resulted in vote: " + vote);
		
		return vote;
	}

	public Xid[] recover(int flags) throws XAException {
		Set/*<Xid>*/ xids = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaRecover(flags);

		log.debug(LNAME, "Recover returned transactions with xids: " + xids);

		return (Xid[])xids.toArray(new Xid[xids.size()]);
	}

	public void rollback(Xid xid) throws XAException {
		log.debug(LNAME, "Will rollback transaction with xid: " + new JMSXid(xid)+ " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );

		try {
			session.recover();
		} catch (JMSException e) {
			log.exception(LNAME, e);
		}

		((JMSSession)session.getSession()).getConnection().getServerFacade().xaRollback(xid);

	}

	public boolean setTransactionTimeout(int seconds) throws XAException {
		if (seconds == 0) {
			int sessionId = ((JMSSession)session.getSession()).getSessionID();
			activeTimeout = ((JMSSession)session.getSession()).getConnection().getServerFacade().xaGetTimeout(sessionId);
		} else {
			activeTimeout = seconds * 1000;		
		}

		return true;
	}

	public void start(Xid xid, int flags) throws XAException {

		log.debug(LNAME, "Will start transaction with xid: " + new JMSXid(xid) + " flags: " + flags+ " from session with id: " + ((JMSSession)session.getSession()).getSessionID() );

		int sessionId = ((JMSSession)session.getSession()).getSessionID();
		((JMSSession)session.getSession()).getConnection().getServerFacade().xaStart(sessionId, xid, flags, activeTimeout);

		activeXid.set(xid);
	}

	public boolean isTransactionActive(){
		if (activeXid.get() == null) {
			return false;
		}
		
		return true;
	}
	
	Xid getActiveXid() {
		return (Xid)activeXid.get();
	}

}
