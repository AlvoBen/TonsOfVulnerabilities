package com.sap.engine.services.dc.cm.offline_phase;

import java.util.Properties;

import com.sap.bc.proj.jstartup.JStartupFramework;
import com.sap.bc.proj.jstartup.NodeState;
import com.sap.engine.core.configuration.bootstrap.ConfigurationManagerBootstrapImpl;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.dc.util.ClusterUtils;
import com.sap.engine.services.dc.util.logging.DCLogConstants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class Runner {

	private Runner() {
	}

	public static void run() throws OfflinePhaseProcessException {
		JStartupFramework.setState(NodeState.STARTING);
		if (!isApplicableInstance()) {
			JStartupFramework.setState(NodeState.STOPPING);
			System.exit(0);
		}

		// makes the deploy offline node green in Sap MC
		JStartupFramework.setState(NodeState.RUNNING);

		final ConfigurationManagerBootstrapImpl cfgFactory;
		try {
			cfgFactory = new ConfigurationManagerBootstrapImpl(
					getConfigurationManagerBootstrapProperties());
		} catch (ConfigurationException ce) {
			throw new OfflinePhaseProcessException(
					DCLogConstants.TELNET_ONE,
					new Object[] { "ASJ.dpl_dc.003119 An error occurred while getting ConfigurationHandlerFactory "
							+ "from the Deploy Controller Offline Processor." },
					ce);
		}

		try {
			final OfflinePhaseProcessor offlinePhaseProcessor = OfflinePhaseFactory
					.getInstance().createOfflinePhaseProcessor(cfgFactory,
							false);

			offlinePhaseProcessor.process();
		} finally {
			JStartupFramework.setState(NodeState.STOPPING);
			cfgFactory.shutdown();
		}
	}

	private static boolean isApplicableInstance() {
		String operation = ClusterUtils.getLockOperationFromFS();
		return operation != null;
	}

	private static Properties getConfigurationManagerBootstrapProperties() {
		Properties cfgManagerBootstrpProperties = new Properties();
		// to prevent OOM as kernel team requested
		cfgManagerBootstrpProperties.setProperty(
				"confighandler.disable.readcache", "true");

		return cfgManagerBootstrpProperties;
	}

	public static void main(String[] args) {
		try {
			run();

			// if there is a problem during the offline operation the
			// FS lock should not be cleared so that the offline operation can
			// be resumed when
			// the problem is solved. For example if there are DB or
			// Configuration failures
			// and these are resolved the offline deploy operation will be
			// resumed after a
			// restart

		} catch (OfflinePhaseProcessException oppe) {
			oppe.printStackTrace();
			System.exit(66);
		} catch (Throwable t) {
			System.err.println("Unexpected throwable");
			t.printStackTrace();
			System.exit(67);

		}

		// always clean up the FS lock when we exit under normal circumstances

		ClusterUtils.cleanLockOnFS();

	}
}
