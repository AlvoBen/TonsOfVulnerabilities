package com.sap.engine.services.dc.frame;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.thread.execution.Executor;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.util.exception.DCBaseException;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-12-3
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class RepositoryContainerInitializer {
	
	private Location location = DCLog.getLocation(this.getClass());
	private static RepositoryContainerInitializer INSTANCE;

	private RepositoryContainerInitializer() {
	}

	static synchronized RepositoryContainerInitializer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RepositoryContainerInitializer();
		}

		return INSTANCE;
	}

	void init() throws InitializationException {
		synchronized (RepositoryContainer.class) {
			ConfigurationHandler cfgHandler = initCfgHandler();
			try {
				final Repository repo = RepositoryFactory.getInstance()
					.createRepository();
				final Set allSdus = loadInParallelSdus(repo, cfgHandler);
				RepositoryContainer.initDeploymentsContainer(allSdus, repo, cfgHandler);
			} catch (RepositoryException re) {
				throw new InitializationException(
						DCLogConstants.ERROR_LOADING_REPOSITORY_DATA, re);
			} finally {
				try {
					cfgHandler.closeAllConfigurations();
				} catch (ConfigurationException e) {
					// $JL-EXC$
					if (isDebugLoggable()) {
						logDebugThrowable(location, null,
								"RepositoryContainerInitializer#init()", e);
					}
				}
			}
		}
	}

	private ConfigurationHandler initCfgHandler()
			throws InitializationException {
		DCLog.TimeWatcher timeWatcher = DCLog.TimeWatcher.getInstance();
		if (location.beDebug()) {
			traceDebug(
					location,
					"Starting to initialize the configuration listeners ... :: timerId: [{0}]",
					new Object[] { timeWatcher.getId() });
		}
		final ConfigurationHandler cfgHandler;
		try {
			cfgHandler = ServiceConfigurer.getInstance()
					.getConfigurationHandler();

			initCfgListeners(cfgHandler);
		} catch (ConfigurationException ce) {
			throw new InitializationException(
					DCExceptionConstants.ERROR_GETTING,
					new String[] { "ConfigurationHandler" }, ce);
		}

		commit(cfgHandler);
		if (location.beDebug()) {
			traceDebug(
					location,
					"The configuration listeners were initialized. Elapsed: [{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}
		return cfgHandler;
	}

	private void initCfgListeners(ConfigurationHandler cfgHandler) {
		final ConfigurationChangedListener dcCfgListener = RepositoryComponentsFactory
				.getInstance().createRepoDCCfgListener();

		final ConfigurationChangedListener scCfgListener = RepositoryComponentsFactory
				.getInstance().createRepoSCCfgListener();

		cfgHandler.addConfigurationChangedListener(dcCfgListener,
				LocationConstants.ROOT_REPO_DC,
				ConfigurationChangedListener.MODE_SYNCHRONOUS);

		cfgHandler.addConfigurationChangedListener(scCfgListener,
				LocationConstants.ROOT_REPO_SC,
				ConfigurationChangedListener.MODE_SYNCHRONOUS);
	}

	protected void commit(ConfigurationHandler cfgHandler)
			throws InitializationException {
		if (cfgHandler == null) {
			return;
		}
		try {
			cfgHandler.commit();
		} catch (ConfigurationException ce) {
			throw new InitializationException(
					DCExceptionConstants.CANNOT_CFG_HANDLER,
					new String[] { "commit" }, ce);
		}
	}

	static class InitializationException extends DCBaseException {

		private static final long serialVersionUID = 5003566707269292416L;

		public InitializationException(String patternKey) {
			super(patternKey);
		}

		public InitializationException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

		public InitializationException(String patternKey, Object[] parameters) {
			this(patternKey, parameters, null);
		}

		public InitializationException(String patternKey, Object[] parameters,
				Throwable cause) {
			super(patternKey, parameters, cause);
		}

	}
	
	private Set loadInParallelSdus(final Repository repository, final ConfigurationHandler cfgHandler)
	throws RepositoryException {
		DCLog.TimeWatcher timeWatcher = DCLog.TimeWatcher.getInstance();
		if (location.beDebug()) {
			traceDebug(
					location,
					"Going to load all SDUs from the repository. TimeId:[{0}]",
					new Object[] { timeWatcher.getId() });
		}
		
		
		final Set<SduRepoLocation> repoLocations = repository.loadAllSduRepoLocations(cfgHandler);
		final Executor executor = ServiceConfigurer.getInstance().getExecutor();
		final CountDownLatch endWait = new CountDownLatch(repoLocations.size());
		final LoadSduThread[] loadSduRunnable = new LoadSduThread[repoLocations.size()];
		int i = 0;
		final Set<Sdu> result = Collections.synchronizedSet(new HashSet<Sdu>(repoLocations.size()));
		for (SduRepoLocation repoLocation: repoLocations) {
			final String threadName = "LoadRepoLocation[" + repoLocation.getLocation() + "]";
			final String threadTask = "Loading location [" + repoLocation.getLocation() + "]";
			loadSduRunnable[i] = new LoadSduThread(threadName, repository, repoLocation, result, endWait);
			executor.execute(loadSduRunnable[i], threadTask, threadName);
			i++;
		}
		try {
			endWait.await();
		} catch (InterruptedException e) {
			throw handleInteruption(e);
		}
		checkForCorrectLoading(loadSduRunnable);
		if (location.beDebug()) {
			traceDebug(location,
					"SDU information is loaded: [{0}]",
					new Object[] { timeWatcher.getElapsedTimeAsString() });
		}
		return result;
	}
	
	private RepositoryException handleInteruption(InterruptedException ex) {
		return new RepositoryException(
				"Interruption occured while: ",
				null, ex);
		
	}
	
	private void checkForCorrectLoading(LoadSduThread[] loadSduRunnables) 
	throws RepositoryException, NullPointerException {
		if (loadSduRunnables == null) {
			return;
		}
		for (final LoadSduThread loadSduThread: loadSduRunnables) {
			loadSduThread.getException();
		}
	}
	
	private class LoadSduThread extends Thread  {
		
		private final SduRepoLocation sduRepoLocation;		
		private final Repository repository;
		private final CountDownLatch endWait;
		private final Set<Sdu> loadedItems; 
		
		RepositoryException repoException = null;
		NullPointerException npException = null;
		
		LoadSduThread(final String threadName, final Repository repository, final SduRepoLocation sduRepoLocation, 
				final Set<Sdu> loadedItems, final CountDownLatch endWait) {
			super (threadName);
			this.repository = repository;
			this.sduRepoLocation = sduRepoLocation;			
			this.loadedItems = loadedItems;
			this.endWait = endWait;
		}
		
		public void run() {
			try {
				repository.loadSdu(sduRepoLocation);				
				loadedItems.add(sduRepoLocation.getSdu());
			} catch (RepositoryException e) {
				repoException = e;
			} catch (NullPointerException e) {
				npException = e;
			} finally {
				endWait.countDown();
			}						
		}
		
		void getException() throws RepositoryException, NullPointerException {
			if (repoException != null) {
				throw repoException;
			}
			if (npException != null) {
				throw npException;
			}			
		}
	}

}
