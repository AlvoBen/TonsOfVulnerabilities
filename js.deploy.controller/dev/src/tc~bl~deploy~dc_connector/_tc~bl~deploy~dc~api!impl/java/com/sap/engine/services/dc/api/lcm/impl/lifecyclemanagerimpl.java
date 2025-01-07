package com.sap.engine.services.dc.api.lcm.impl;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.event.EventMode;
import com.sap.engine.services.dc.api.event.LCEventListener;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.lcm.LCMCompNotFoundException;
import com.sap.engine.services.dc.api.lcm.LCMException;
import com.sap.engine.services.dc.api.lcm.LCMResult;
import com.sap.engine.services.dc.api.lcm.LCMStatus;
import com.sap.engine.services.dc.api.lcm.LifeCycleManager;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.repo.impl.SdaIdImpl;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class LifeCycleManagerImpl implements LifeCycleManager,
		IRemoteReferenceHandler {
	private final Session session;
	private final DALog daLog;
	private RemoteLCEventListener remoteListener;

	private com.sap.engine.services.dc.lcm.RemoteLCM remoteLCM;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();

	LifeCycleManagerImpl(Session session) {
		this.session = session;
		// add the instance as a remote reference handler to the session
		this.session.addRemoteReferenceHandler(this);
		this.daLog = session.getLog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.lcm.LifeCycleManager#start(java.lang.String
	 * , java.lang.String)
	 */
	public LCMResult start(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException {
		final com.sap.engine.services.dc.lcm.LCMResult lcmResult;

		try {
			lcmResult = this.getRemoteLCM().start(componentName,
					componentVendor);
		} catch (com.sap.engine.services.dc.lcm.LCMCompNotFoundException lcmcnfe) {
			throw new LCMCompNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DA_COMP_NOT_EXIST, new String[] {
							componentName, componentVendor,
							lcmcnfe.getMessage() }, lcmcnfe);
		} catch (com.sap.engine.services.dc.lcm.LCMException lcme) {
			throw new LCMException(this.daLog.getLocation(),
					APIExceptionConstants.DA_CANNOT_START_COMP,
					new String[] { componentName, componentVendor,
							lcme.getMessage() }, lcme);
		}

		return LCMMapper.map(lcmResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.lcm.LifeCycleManager#stop(java.lang.String
	 * , java.lang.String)
	 */
	public LCMResult stop(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException {
		final com.sap.engine.services.dc.lcm.LCMResult lcmResult;

		try {
			lcmResult = this.getRemoteLCM()
					.stop(componentName, componentVendor);
		} catch (com.sap.engine.services.dc.lcm.LCMCompNotFoundException lcmcnfe) {
			throw new LCMCompNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DA_COMP_NOT_EXIST, new String[] {
							componentName, componentVendor,
							lcmcnfe.getMessage() }, lcmcnfe);
		} catch (com.sap.engine.services.dc.lcm.LCMException lcme) {
			throw new LCMException(this.daLog.getLocation(),
					APIExceptionConstants.DA_CANNOT_STOP_COMP,
					new String[] { componentName, componentVendor,
							lcme.getMessage() }, lcme);
		}

		return LCMMapper.map(lcmResult);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.lcm.LifeCycleManager#getLCMStatus(java
	 * .lang.String, java.lang.String)
	 */
	public LCMStatus getLCMStatus(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException, ConnectionException,
			APIException {
		final com.sap.engine.services.dc.lcm.LCMStatus lcmStatus;

		try {
			lcmStatus = this.getRemoteLCM().getLCMStatus(componentName,
					componentVendor);
		} catch (com.sap.engine.services.dc.lcm.LCMCompNotFoundException lcmcnfe) {
			throw new LCMCompNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DA_COMP_NOT_EXIST, new String[] {
							componentName, componentVendor,
							lcmcnfe.getMessage() }, lcmcnfe);
		} catch (com.sap.engine.services.dc.lcm.LCMException lcme) {
			throw new LCMException(this.daLog.getLocation(),
					APIExceptionConstants.DA_CANNOT_GET_LCM_STATUS,
					new String[] { componentName, componentVendor,
							lcme.getMessage() }, lcme);
		}

		return LCMMapper.mapLCMStatus(lcmStatus);
	}

	public LCMStatus[] getLCMStatuses(SdaId[] sdaIds)
			throws ConnectionException, APIException {
		if (sdaIds == null || sdaIds.length == 0) {
			return new LCMStatus[0];
		}
		com.sap.engine.services.dc.repo.SdaId[] remoteSdaIds = new com.sap.engine.services.dc.repo.SdaId[sdaIds.length];
		for (int i = 0; i < sdaIds.length; i++) {
			remoteSdaIds[i] = new SdaIdImpl(sdaIds[i].getName(), sdaIds[i]
					.getVendor());
		}
		com.sap.engine.services.dc.lcm.LCMStatus[] remoteStatuses = this
				.getRemoteLCM().getLCMStatuses(remoteSdaIds);
		if (remoteStatuses == null || remoteStatuses.length == 0
				|| sdaIds.length != remoteStatuses.length) {
			throw new LCMException(
					this.daLog.getLocation(),
					APIExceptionConstants.DA_GET_LCM_STATUSES_EXCEPTION,
					new String[] { "Count of the incomint argument does not equals to the count of the returned statuses : "
							+ sdaIds.length + " vs " + remoteStatuses.length });
		}
		LCMStatus[] statuses = new LCMStatus[remoteStatuses.length];
		for (int i = 0; i < statuses.length; i++) {
			statuses[i] = LCMMapper.mapLCMStatus(remoteStatuses[i]);
		}
		return statuses;
	}

	private synchronized com.sap.engine.services.dc.lcm.RemoteLCM getRemoteLCM()
			throws ConnectionException, APIException {
		if (this.remoteLCM == null) {
			try {
				this.remoteLCM = this.session.createCM().getLifeCycleManager();
				// register the reference to the obtained remote object
				registerRemoteReference(remoteLCM);
			} catch (CMException cme) {
				throw new APIException(this.daLog.getLocation(),
						APIExceptionConstants.DA_CANNOT_GET_LCM,
						new String[] { cme.getMessage() }, cme);
			}
		}

		return this.remoteLCM;
	}

	public void addLCEventListener(LCEventListener listener, EventMode eventMode)
			throws ConnectionException, LCMException, APIException {
		if (listener == null) {
			return;
		}

		RemoteLCEventListener remoteListener = null;
		com.sap.engine.services.dc.event.EventMode remoteEventMode = null;
		if (EventMode.SYNCHRONOUS.equals(eventMode)) {
			remoteEventMode = com.sap.engine.services.dc.event.EventMode.SYNCHRONOUS;
		} else if (EventMode.ASYNCHRONOUS.equals(eventMode)) {
			remoteEventMode = com.sap.engine.services.dc.event.EventMode.ASYNCHRONOUS;
		} else {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1103] Unknown Event Mode '"
							+ eventMode + "'.");
		}
		if (this.remoteListener == null) {
			this.remoteListener = new RemoteLCEventListener(daLog);
		}
		remoteListener = this.remoteListener;
		if (remoteListener != null) {
			remoteListener.addLCEventListener(listener, eventMode);
			if (remoteEventMode != null) {
				try {
					getRemoteLCM().addLCEventListener(remoteListener,
							remoteEventMode);
				} catch (com.sap.engine.services.dc.lcm.LCMException e) {
					throw new LCMException(this.daLog.getLocation(),
							APIExceptionConstants.DA_CANNOT_ADD_LCM_LISTENER,
							new String[] { e.getMessage() }, e);
				}
			}
		}

	}

	public void removeLCEventListener(LCEventListener listener)
			throws LCMException {
		removeFromDistinctListener(this.remoteListener, listener);
	}

	private void removeFromDistinctListener(
			RemoteLCEventListener remoteListener, LCEventListener listener)
			throws LCMException {
		if (listener == null || remoteListener == null) {
			return;
		}
		remoteListener.removeLCEventListener(listener);
		if (remoteListener.getListenersCount() == 0) {
			if (this.remoteLCM != null) {
				try {
					this.remoteLCM.removeLCEventListener(remoteListener);
				} catch (com.sap.engine.services.dc.lcm.LCMException e) {
					throw new LCMException(
							this.daLog.getLocation(),
							APIExceptionConstants.DA_CANNOT_REMOVE_LCM_LISTENER,
							new String[] { e.getMessage() }, e);
				}
			}
			remoteListener = null;
		}
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
		if (this.remoteLCM != null) {
			try {
				this.remoteLCM.removeLCEventListener(remoteListener);
			} catch (com.sap.engine.services.dc.lcm.LCMException e) {
				this.daLog
						.logThrowable(
								"ASJ.dpl_api.001096",
								"An exception occured while trying to remove the remote LCM event listener.",
								e);
			}
		}
		// try to release the remote references
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001097",
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
									"ASJ.dpl_api.001098",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}
}
