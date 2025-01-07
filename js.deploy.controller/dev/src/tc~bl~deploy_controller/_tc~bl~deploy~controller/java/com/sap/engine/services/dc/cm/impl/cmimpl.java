package com.sap.engine.services.dc.cm.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.rmi.PortableRemoteObject;

import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.DCNotAvailableException;
import com.sap.engine.services.dc.cm.SupportedFunction;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManagerFactory;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.lock.DCLockManagerFactory;
import com.sap.engine.services.dc.cm.lock.RemoteLockManager;
import com.sap.engine.services.dc.cm.params.AbstractRemoteParamsFactory;
import com.sap.engine.services.dc.cm.params.RemoteParamsFactory;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizationException;
import com.sap.engine.services.dc.cm.security.authorize.Authorizer;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizerFactory;
import com.sap.engine.services.dc.cm.session_id.SessionIDException;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.undeploy.AbstractUndeployFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeployFactory;
import com.sap.engine.services.dc.cm.utils.filters.AbstractRemoteBatchFilterFactory;
import com.sap.engine.services.dc.cm.utils.filters.RemoteBatchFilterFactory;
import com.sap.engine.services.dc.cm.validate.ValidateFactory;
import com.sap.engine.services.dc.cm.validate.Validator;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.lcm.RemoteLCM;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.repo.explorer.AbstractRemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.repo.explorer.RemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.repo.explorer.RepositoryExploringException;
import com.sap.engine.services.dc.selfcheck.RemoteSelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.selfcheck.SelfCheckerFactory;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.exception.DCResourceAccessor;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-1
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class CMImpl extends PortableRemoteObject implements CM {
	
	private final Location location = DCLog.getLocation(this.getClass());

	CMImpl() throws RemoteException {
		super();
	}

	public DCState getState() throws CMException {
		doAuthorize();

		return DCManager.getInstance().getDCState();
	}

	/*
	 * (non-Javadoc) The only one method which is necessare to check if the DC
	 * is already available on invoking deploy or validate command.
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getDeployer()
	 */
	public Deployer getDeployer() throws CMException {
		final String performerUserUniqueId = doAuthorize();

		try {
			return DeployFactory.getInstance().createDeployer(
					performerUserUniqueId);
		} catch (RemoteException re) {
			CMException cme = new CMException(
					"An error occurred while getting deployer.",
					re);
			cme.setMessageID("ASJ.dpl_dc.003102");
			throw cme;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getUploadDirName()
	 */
	public String getUploadDirName(String sessionId) throws CMException {
		doAuthorize();

		checkForAvailablility();

		String uploadDirName = PathsConfigurer.getInstance().getUploadDirName(
				sessionId);
		try {
			uploadDirName = new File(uploadDirName).getCanonicalPath();
		} catch (IOException e) {
			if (location.beDebug()) {
				traceDebug(location, 
						"Cannot get canonical path for the upload dir name [{0}]{1}{2}",
						new Object[] { uploadDirName, Constants.EOL,
								e.getMessage() });
			}
		}

		return uploadDirName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getUndeployFactory()
	 */
	public UndeployFactory getUndeployFactory() throws CMException {
		doAuthorize();

		return AbstractUndeployFactory.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getRemoteBatchFilterFactory()
	 */
	public RemoteBatchFilterFactory getRemoteBatchFilterFactory()
			throws CMException {
		doAuthorize();

		checkForAvailablility();

		return AbstractRemoteBatchFilterFactory.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getRemoteParamsFactory()
	 */
	public RemoteParamsFactory getRemoteParamsFactory() throws CMException {
		doAuthorize();

		checkForAvailablility();

		return AbstractRemoteParamsFactory.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.CM#getRemoteRepositoryExplorerFactory()
	 */
	public RemoteRepositoryExplorerFactory getRemoteRepositoryExplorerFactory()
			throws CMException {
		doAuthorize();

		checkForAvailablility();

		try {
			return AbstractRemoteRepositoryExplorerFactory.getInstance();
		} catch (RepositoryExploringException ree) {
			throw new CMException(
					"An error occurred while creating Repository Explorer Factory",
					ree);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#generateSessionId()
	 */
	public String generateSessionId() throws CMException {
		doAuthorize();

		checkForAvailablility();

		try {
			return SessionIDFactory.getInstance().generateSessionID().getID();
		} catch (SessionIDException side) {
			throw new CMException("Cannot generate new session id.", side);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getCheckFactory()
	 */
	public RemoteSelfChecker getSelfChecker() throws AuthorizationException,
			SelfCheckerException {
		doAuthorize();

		try {
			checkForAvailablility();
		} catch (DCNotAvailableException e) {
			throw new SelfCheckerException(e.getLocalizedMessage(), e);
		}

		return SelfCheckerFactory.getInstance().getSelfChecker();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getArchiveManager()
	 */
	public ArchiveManager getArchiveManager() throws CMException {
		doAuthorize();

		checkForAvailablility();

		try {
			return ArchiveManagerFactory.getInstance().createArchiveManager();
		} catch (RemoteException re) {
			throw new CMException(
					"An error occurred while getting Archive Manager.", re);
		}
	}

	private String doAuthorize() throws AuthorizationException {
		final Authorizer authorizer = AuthorizerFactory.getInstance()
				.createAuthorizer();

		authorizer.doAuthorize();

		return authorizer.getUserUniqueId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getLifeCycleManager()
	 */
	public RemoteLCM getLifeCycleManager() throws CMException {
		doAuthorize();

		checkForAvailablility();

		try {
			return LifeCycleManagerFactory.getInstance()
					.createRemoteLifeCycleManager();
		} catch (RemoteException re) {
			throw new CMException(
					"An error occurred while getting the remote life cycle manager.",
					re);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CM#getLockManager()
	 */
	public RemoteLockManager getLockManager() throws CMException {
		doAuthorize();

		checkForAvailablility();

		try {
			return DCLockManagerFactory.getInstance().createRemoteLockManager();
		} catch (RemoteException re) {
			throw new CMException(
					"An error occurred while getting the remote lock manager.",
					re);
		}
	}

	/**
	 * Checks if the DC is ready to server
	 * 
	 * @throws DCNotAvailableException
	 *             if DC performs some operation after the offline phase or the
	 *             repo is still initializing
	 */
	private static void checkForAvailablility() throws DCNotAvailableException {

		if (!DCManager.getInstance().isInWorkingMode()) {

			DCState state = DCManager.getInstance().getDCState();
			throw new DCNotAvailableException(DCResourceAccessor.getInstance()
					.getMessageText(DCExceptionConstants.DC_NOT_AVAILABLE_YET)
					+ state);
		}
	}

	public Validator getValidator() throws CMException {
		final String performerUserUniqueId = doAuthorize();

		try {
			return ValidateFactory.getInstance().createValidator(
					performerUserUniqueId);
		} catch (RemoteException re) {
			throw new CMException("An error occurred while getting validator.",
					re);
		}
	}

	public void checkForBatchValidation() {
		// this method does nothing. Used to check if the functionality is
		// available on server/engine
	}

	public boolean isFunctionSupported(SupportedFunction supportedFunction) {
		return SupportedFunction.isFunctionSupported(supportedFunction);
	}
	
	
	
}
