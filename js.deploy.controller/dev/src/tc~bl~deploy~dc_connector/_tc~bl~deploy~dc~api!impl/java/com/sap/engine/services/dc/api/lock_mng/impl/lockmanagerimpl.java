package com.sap.engine.services.dc.api.lock_mng.impl;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;
import com.sap.engine.services.dc.api.lock_mng.LockAction;
import com.sap.engine.services.dc.api.lock_mng.LockException;
import com.sap.engine.services.dc.api.lock_mng.LockManager;
import com.sap.engine.services.dc.api.lock_mng.LockNotFoundException;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.lock.DCAlreadyLockedException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockNotFoundException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LockManagerImpl implements LockManager, IRemoteReferenceHandler {

	private final Session session;
	private final DALog daLog;

	private com.sap.engine.services.dc.cm.CM remoteCM;
	private com.sap.engine.services.dc.cm.lock.RemoteLockManager remoteLockManager;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();

	private static final Map lockActionsMap = new HashMap();

	static {

		lockActionsMap.put(
				com.sap.engine.services.dc.api.lock_mng.LockAction.DEPLOY,
				com.sap.engine.services.dc.cm.lock.LockAction.DEPLOY);

	}

	LockManagerImpl(Session session) {
		this.session = session;
		// add the instance as a remote reference handler to the session
		this.session.addRemoteReferenceHandler(this);
		this.daLog = session.getLog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.lock_mng.LockManager#lock(com.sap.engine
	 * .services.dc.api.lock_mng.LockAction)
	 */
	public void lock(LockAction lockAction) throws ConnectionException,
			APIException {
		final com.sap.engine.services.dc.cm.lock.RemoteLockManager remoteLockMng = getRemoteLockManager();

		final com.sap.engine.services.dc.cm.lock.LockAction remoteLockAction = map(lockAction);

		try {
			remoteLockMng.lock(remoteLockAction);
		} catch (DCAlreadyLockedException dcale) {
			throw new AlreadyLockedException(this.daLog.getLocation(),
					APIExceptionConstants.DA_ALREADY_LOCKED, new String[] {
							lockAction.getName(), dcale.getMessage() }, dcale);
		} catch (DCLockException dcle) {
			throw new LockException(this.daLog.getLocation(),
					APIExceptionConstants.DA_CANNOT_LOCK, new String[] {
							lockAction.getName(), dcle.getMessage() }, dcle);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.lock_mng.LockManager#unlock(com.sap.engine
	 * .services.dc.api.lock_mng.LockAction)
	 */
	public void unlock(LockAction lockAction) throws ConnectionException,
			APIException {
		final com.sap.engine.services.dc.cm.lock.RemoteLockManager remoteLockMng = getRemoteLockManager();

		final com.sap.engine.services.dc.cm.lock.LockAction remoteLockAction = map(lockAction);

		try {
			remoteLockMng.unlock(remoteLockAction);
		} catch (DCLockNotFoundException dclnfe) {
			throw new LockNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DA_NO_SUCH_LOCK, new String[] {
							lockAction.getName(), dclnfe.getMessage() }, dclnfe);
		} catch (DCLockException dcle) {
			throw new LockException(this.daLog.getLocation(),
					APIExceptionConstants.DA_CANNOT_UNLOCK, new String[] {
							lockAction.getName(), dcle.getMessage() }, dcle);
		}
	}

	private synchronized com.sap.engine.services.dc.cm.lock.RemoteLockManager getRemoteLockManager()
			throws ConnectionException, APIException {
		if (this.remoteLockManager == null) {
			try {
				this.remoteLockManager = this.getRemoteCM().getLockManager();
				// register the reference to the obtained remote object
				registerRemoteReference(remoteLockManager);
			} catch (CMException cme) {
				throw new APIException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_GET_LOCK_MNG,
						new String[] { cme.getMessage() }, cme);
			}
		}

		return this.remoteLockManager;
	}

	private synchronized com.sap.engine.services.dc.cm.CM getRemoteCM()
			throws ConnectionException {
		if (this.remoteCM == null) {
			this.remoteCM = this.session.createCM();
		}

		return this.remoteCM;
	}

	private com.sap.engine.services.dc.cm.lock.LockAction map(
			com.sap.engine.services.dc.api.lock_mng.LockAction lockAction) {
		return (com.sap.engine.services.dc.cm.lock.LockAction) lockActionsMap
				.get(lockAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * registerRemoteReference(Remote)
	 */
	public void registerRemoteReference(Remote remote) {
		remoteRefs.add(remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * releaseRemoteReferences()
	 */
	public void releaseRemoteReferences() {
		// try to release the remote references
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001101",
							"The P4ObjectBroker is null while trying to release remote references. The release operation is aborted!");
		} else {
			Iterator iter = this.remoteRefs.iterator();
			while (iter.hasNext()) {
				Remote remoteRef = (Remote) iter.next();
				// separate error handling for each resource
				// to release as much remote refs. as possible
				try {
					broker.release(remoteRef);
				} catch (Exception e) {
					this.daLog
							.logThrowable(
									"ASJ.dpl_api.001102",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}
}
