package com.sap.engine.services.dc.frame;

import static com.sap.engine.services.dc.util.PerformanceUtil.isBoostPerformanceDisabled;
import static com.sap.engine.services.dc.util.ThreadUtil.popTask;
import static com.sap.engine.services.dc.util.ThreadUtil.pushTask;

import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryContainer.CsnContainer;
import com.sap.engine.services.dc.util.exception.DCBaseException;
import com.sap.tc.logging.LoggingManager;
import com.sap.tc.logging.interfaces.IDeployRuntimeInfoProvider;

class HookInitializer {

	private static HookInitializer INSTANCE;

	private HookInitializer() {
	}

	static synchronized HookInitializer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HookInitializer();
		}

		return INSTANCE;
	}

	void init() throws HookInitializationException {
		synchronized (HookInitializer.class) {
			final IDeployRuntimeInfoProvider deployRuntimeInfoProvider = new DeployControllerRuntimeInfoProvider();

			initLoggingHook(deployRuntimeInfoProvider);
		}
	}

	private void initLoggingHook(
			final IDeployRuntimeInfoProvider deployRuntimeInfoProvider)
			throws HookInitializationException {
		try {
			if (isBoostPerformanceDisabled()) {
				pushTask("[Deploy Controller] � registering csn hook in logging manager...");
			}
			LoggingManager
					.registerDeployRuntimeInfoProvider(deployRuntimeInfoProvider);
		} catch (final Exception exc) {
			throw new HookInitializationException(
					"Error occurred while restistering deploy runtime "
							+ "info provider in logging manager: ", exc);
		} finally {
			if (isBoostPerformanceDisabled()) {
				popTask();
			}
		}
	}

	static class HookInitializationException extends DCBaseException {

		private static final long serialVersionUID = 1020667187369572316L;

		public HookInitializationException(String patternKey) {
			super(patternKey);
		}

		public HookInitializationException(String patternKey, Throwable cause) {
			super(patternKey, cause);
		}

		public HookInitializationException(String patternKey,
				Object[] parameters) {
			this(patternKey, parameters, null);
		}

		public HookInitializationException(String patternKey,
				Object[] parameters, Throwable cause) {
			super(patternKey, parameters, cause);
		}

	}

	public static class DeployControllerRuntimeInfoProvider implements
			IDeployRuntimeInfoProvider {

		private static final CsnContainer csnContainer = RepositoryContainer
				.getCsnContainer();

		DeployControllerRuntimeInfoProvider() {
		}

		public String getCsnComponentByDcName(final String compName) {
			return csnContainer.getCsnByComponentName(compName);
		}
	}

}
