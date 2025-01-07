/*
 * Created on Oct 17, 2004
 *
 */
package com.sap.engine.services.dc.api.impl;

import com.sap.engine.services.dc.api.AuthenticationException;
import com.sap.engine.services.dc.api.BatchFactory;
import com.sap.engine.services.dc.api.ComponentManager;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.SettingsFactory;
import com.sap.engine.services.dc.api.SupportedFunction;
import com.sap.engine.services.dc.api.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.api.archive_mng.ArchiveManagerFactory;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployProcessorFactory;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerFactory;
import com.sap.engine.services.dc.api.explorer.impl.RepositoryExplorerFactoryImpl;
import com.sap.engine.services.dc.api.filters.BatchFilterFactory;
import com.sap.engine.services.dc.api.filters.impl.BatchFilterFactoryImpl;
import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.impl.ModelFactoryImpl;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;
import com.sap.engine.services.dc.api.params.ParamsProcessorFactory;
import com.sap.engine.services.dc.api.selfcheck.SelfChecker;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerException;
import com.sap.engine.services.dc.api.selfcheck.SelfCheckerFactory;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessorFactory;
import com.sap.engine.services.dc.api.validate.ValidateException;
import com.sap.engine.services.dc.api.validate.ValidateProcessor;
import com.sap.engine.services.dc.api.validate.ValidateProcessorFactory;
import com.sap.engine.services.rmi_p4.exception.NoSuchOperationException;

/**
 * @author Georgi Danov
 * @author Boris Savov
 */
final class ComponentManagerImpl implements ComponentManager {
	private final Session session;

	ComponentManagerImpl(Session session) {
		this.session = session;
	}

	public DeployProcessor getDeployProcessor() throws ConnectionException,
			DeployException {
		return DeployProcessorFactory.getInstance().createDeployProcessor(
				this.session);
	}

	public UndeployProcessor getUndeployProcessor() throws ConnectionException,
			UndeployException {
		return UndeployProcessorFactory.getInstance().createUndeployProcessor(
				this.session);
	}

	public ParamsProcessor getParamsProcessor() throws ConnectionException,
			ParamsException {
		return ParamsProcessorFactory.getInstance().createParamsProcessor(
				this.session);
	}

	public BatchFilterFactory getBatchFilterFactory() {
		return BatchFilterFactoryImpl.getInstance();
	}

	public RepositoryExplorerFactory getRepositoryExplorerFactory()
			throws ConnectionException, RepositoryExplorerException {
		return RepositoryExplorerFactoryImpl.getInstance(this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.ComponentManager#getSelfChecker()
	 */
	public SelfChecker getSelfChecker() throws SelfCheckerException,
			ConnectionException, AuthenticationException {
		return SelfCheckerFactory.getInstance().createSelfChecker(this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.ComponentManager#getArchiveManager()
	 */
	public ArchiveManager getArchiveManager() {
		return ArchiveManagerFactory.getInstance().createArchiveManager(
				this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.ComponentManager#getModelFactory()
	 */
	public ModelFactory getModelFactory() {
		return ModelFactoryImpl.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.ComponentManager#getValidateProcessor()
	 */
	public ValidateProcessor getValidateProcessor() throws ConnectionException,
			ValidateException {
		return ValidateProcessorFactory.getInstance().createValidateProcessor(
				this.session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.ComponentManager#isFunctionSupported()
	 */
	public boolean isFunctionSupported(SupportedFunction supportedFunction)
			throws ConnectionException {
		if(supportedFunction == null){
			throw new IllegalArgumentException(
				"Supported Function should not be null.");			
		}
		com.sap.engine.services.dc.cm.CM cm = this.session.createCM();
		try {
			return cm.isFunctionSupported(
					SupportedFunctionMapper.mapSupportedFunction(supportedFunction));			
		} catch (NoSuchOperationException e) {
			if (supportedFunction.equals(SupportedFunction.BATCH_VALIDATION)) {
				try {
					cm.checkForBatchValidation();
					return true;
				} catch (NoSuchOperationException ex) {
					return false;
				}
			}else{
				return false;
			}
		}
	}

	public BatchFactory getBatchFactory() {
		return BatchFactoryImpl.getInstance();
	}

	public SettingsFactory getSettingsFactory() {
		return SettingsFactoryImpl.getInstance();
	}
}
