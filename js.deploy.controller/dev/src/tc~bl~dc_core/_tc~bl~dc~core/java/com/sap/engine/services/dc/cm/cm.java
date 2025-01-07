package com.sap.engine.services.dc.cm;

import java.rmi.Remote;

import com.sap.engine.services.dc.cm.SupportedFunction;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.lock.RemoteLockManager;
import com.sap.engine.services.dc.cm.params.RemoteParamsFactory;
import com.sap.engine.services.dc.cm.security.authorize.AuthorizationException;
import com.sap.engine.services.dc.cm.undeploy.UndeployFactory;
import com.sap.engine.services.dc.cm.utils.filters.RemoteBatchFilterFactory;
import com.sap.engine.services.dc.cm.validate.Validator;
import com.sap.engine.services.dc.lcm.RemoteLCM;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.repo.explorer.RemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.selfcheck.RemoteSelfChecker;
import com.sap.engine.services.dc.selfcheck.SelfCheckerException;

/**
 * 
 * Title: J2EE Deployment Team Description: The interface acts as an entry point
 * for the Deploy Controller.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-10
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface CM extends Remote {

	/**
	 * The constant defines the name with which the Deploy Controller is
	 * registered in the Engine service framework.
	 */
	public static final String SERVICE_NAME = "tc~bl~deploy_controller";

	/**
	 * It is good practice immediate after retrieving Component Manager to check
	 * the state of the service and procede according to the returned value
	 * 
	 * @return current <code>DCState</code>.
	 * @throws CMException
	 */
	public DCState getState() throws CMException;

	/**
	 * 
	 * @return <code>Deployer</code>.
	 * @throws CMException
	 */
	public Deployer getDeployer() throws CMException;

	/**
	 * @return <code>UndeployFactory</code>.
	 * @throws CMException
	 */
	public UndeployFactory getUndeployFactory() throws CMException;

	/**
	 * @return <code>RemoteBatchFilterFactory</code>.
	 * @throws CMException
	 */
	public RemoteBatchFilterFactory getRemoteBatchFilterFactory()
			throws CMException;

	/**
	 * @return <code>RemoteParamsFactory</code>.
	 * @throws CMException
	 */
	public RemoteParamsFactory getRemoteParamsFactory() throws CMException;

	/**
	 * @return <code>RemoteRepositoryExplorerFactory</code>.
	 * @throws CMException
	 */
	public RemoteRepositoryExplorerFactory getRemoteRepositoryExplorerFactory()
			throws CMException;

	/**
	 * Generates unique id for the current session. The id is unique for the
	 * whole Engine cluster and and does not depend on the Engine lifecycle. Tis
	 * means in case the Engine is restarted, the next time the operation is
	 * invoked, it will continue to generate unique ids which do not overlap
	 * with the previously generated ones.
	 * 
	 * @return <code>String</code> the generated session id.
	 * @throws CMException
	 */
	public String generateSessionId() throws CMException;

	/**
	 * @param sessionId
	 *            <code>String</code> the id which was generated for the
	 *            concrete session by using the opeartion
	 *            <code>generateSessionId()</code>.
	 * @return <code>String</code> which represents a path to the SAP J2EE
	 *         Engine directory where the archives haev to be uploaded. Later
	 *         on, in case of deployment the files will be get from the
	 *         directory in order to be deployed.
	 * @throws CMException
	 */
	public String getUploadDirName(String sessionId) throws CMException;

	/**
	 * SelfChecker is responsible for performing some checks on the server
	 * relevant to deploy controller logic (e.g Check for repository
	 * consistency).
	 * 
	 * @return <code>CheckFactory</code>
	 * @throws AuthorizationException
	 * @throws SelfCheckerException
	 */
	public RemoteSelfChecker getSelfChecker() throws AuthorizationException,
			SelfCheckerException;

	/**
	 * The <code>ArchiveManager</code> is responsible for delivering the actual
	 * deployed archives to the clients. Please do bear in mind that the
	 * archives are the ones which have been originally deployed.
	 * 
	 * @return <code>ArchiveManager</code>
	 * @throws CMException
	 */
	public ArchiveManager getArchiveManager() throws CMException;

	public RemoteLCM getLifeCycleManager() throws CMException;

	public RemoteLockManager getLockManager() throws CMException;

	public Validator getValidator() throws CMException;

	public void checkForBatchValidation();
	
	public boolean isFunctionSupported(SupportedFunction supportedFunction);

}
