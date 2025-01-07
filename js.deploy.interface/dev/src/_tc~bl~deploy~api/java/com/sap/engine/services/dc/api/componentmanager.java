package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerFactory;
import com.sap.engine.services.dc.api.filters.BatchFilterFactory;
import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;
import com.sap.engine.services.dc.api.selfcheck.SelfChecker;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.validate.ValidateException;
import com.sap.engine.services.dc.api.validate.ValidateProcessor;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * 
 * <DT><B>Description: </B></DT>
 * <DD>An Entry point to the almost all of the components related operations.</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface ComponentManager {

	/**
	 * Creates new <code>DeployProcessor</code>. Creating the
	 * <code>DeployProcessor</code> does not creates anything on the server.
	 * 
	 * @return new created deploy processor
	 * @throws ConnectionException
	 * @throws DeployException
	 */
	public DeployProcessor getDeployProcessor() throws ConnectionException,
			DeployException;

	/**
	 * Creates new <code>UndeployProcessor</code>. Creating the
	 * <code>UndeployProcessor</code> does not creates anything on the server.
	 * 
	 * @return new created undeploy processor
	 * @throws ConnectionException
	 * @throws UndeployException
	 */
	public UndeployProcessor getUndeployProcessor() throws ConnectionException,
			UndeployException;

	/**
	 * Creates new <code>ParamsProcessor</code>.<code>ParamsProcessor</code>
	 * acts like a proxy to the remote one.Creating <code>ParamsProcessor</code>
	 * creates remote ParameterProcessor object.
	 * 
	 * @return new created params processor
	 * @throws ConnectionException
	 * @throws ParamsException
	 */
	public ParamsProcessor getParamsProcessor() throws ConnectionException,
			ParamsException;

	/**
	 * Creates batch filter factory. Use created <code>BatchFilterFactory</code>
	 * for creating specific <code>BatchFilter</code> s on demand.
	 * 
	 * @return new created batch filter processor
	 */
	public BatchFilterFactory getBatchFilterFactory();

	/**
	 * Creates new <code>RepositoryExplorerFactory</code>.
	 * <code>RepositoryExplorerFactory</code> acts like a proxy to the remote
	 * one. Creating <code>RepositoryExplorerFactory</code> creates remote
	 * RepositoryExplorerFactory object.
	 * 
	 * @return new created repository explorer factory
	 * @throws RepositoryExplorerException
	 * @throws ConnectionException
	 */
	public RepositoryExplorerFactory getRepositoryExplorerFactory()
			throws RepositoryExplorerException, ConnectionException;

	/**
	 * Create new <code>SelfChecker</code> instance.
	 * 
	 * @return new <code>SelfChecker</code> instance.
	 * @throws SelfCheckerException
	 * @throws ConnectionException
	 * @throws AuthenticationException
	 */
	public SelfChecker getSelfChecker() throws SelfCheckerException,
			ConnectionException, AuthenticationException;

	/**
	 * Archive manager serves to download archives of already deployed
	 * componetns.
	 * 
	 * @return new <code>ArchiveManager</code> instance.
	 */
	public ArchiveManager getArchiveManager();

	/**
	 * Serves to create component related stuff.
	 * 
	 * @return shared <code>ModelFactory</code> instance.
	 */
	public ModelFactory getModelFactory();

	/**
	 * Creates new <code>ValidateProcessor</code>.
	 * 
	 * @return new created validate processor
	 * @throws ConnectionException
	 * @throws ValidateException
	 */
	public ValidateProcessor getValidateProcessor() throws ConnectionException,
			ValidateException;

	/**
	 * Check if a particular functionality is supported on server.
	 * 
	 * @param supportedFunction
	 *            is the function that have to be checked.
	 * @return True if the function is supported.
	 * @throws ConnectionException
	 */
	public boolean isFunctionSupported(SupportedFunction supportedFunction)
			throws ConnectionException;

	/**
	 * Creates new <code>SettingsFactory</code>.
	 * 
	 * @return new created SettingsFactory
	 */
	public SettingsFactory getSettingsFactory();

	/**
	 * Creates new <code>BatchFactory</code>.
	 * 
	 * @return new created BatchFactory
	 */
	public BatchFactory getBatchFactory();
}