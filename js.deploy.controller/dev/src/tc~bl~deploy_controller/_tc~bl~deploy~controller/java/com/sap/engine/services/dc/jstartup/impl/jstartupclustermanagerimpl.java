package com.sap.engine.services.dc.jstartup.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.IOException;

import com.sap.bc.proj.jstartup.api.JStartupClusterControlException;
import com.sap.bc.proj.jstartup.api.JStartupClusterController;
import com.sap.bc.proj.jstartup.api.JStartupClusterControllerFactory;
import com.sap.bc.proj.jstartup.api.JStartupInitialFactory;
import com.sap.bc.proj.jstartup.api.JStartupInstantiationException;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.jstartup.JStartupClusterManager;
import com.sap.engine.services.dc.jstartup.JStartupException;
import com.sap.engine.services.dc.sapcontrol.SapControl;
import com.sap.engine.services.dc.sapcontrol.SapControlException;
import com.sap.engine.services.dc.sapcontrol.SapControlFactory;
import com.sap.engine.services.dc.util.SystemProfileManager;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class JStartupClusterManagerImpl implements JStartupClusterManager {
	
	private Location location = DCLog.getLocation(this.getClass());

	private final String msHost;
	private final int msPort;
	private final String osUserName;
	private final String osUserPass;
	private final ThreadSystem threadSystem;

	JStartupClusterManagerImpl(String msHost, int msPort, String osUserName,
			String osUserPass, ThreadSystem threadSystem) {
		this.msHost = msHost;
		this.msPort = msPort;
		this.osUserName = osUserName;
		this.osUserPass = osUserPass;
		this.threadSystem = threadSystem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.jstartup.JStartupClusterManager#restartCluster
	 * ()
	 */
	public void restartCluster() throws JStartupException {
		JStartupClusterController clusterController = null;
		try {
			if (location.bePath()) {
				tracePath(location, 
						"Getting JStartup Cluster Controller ...");
			}

			clusterController = getJStartupClusterController();

			try {
				if (location.bePath()) {
					tracePath(location, 
							"Invoke instant restart operation of JStartup Cluster Controller ...");
				}
				clusterController.instantRestartCluster();
				if (location.beInfo()) {
					tracePath(location,
						"Instant restart operation has been triggered");
				}
			} catch (JStartupClusterControlException jscce) {
				logErrorThrowable(location, null,
						"An error occurred while performing the JStartup Cluster Controller "
								+ "instant restart operation.", jscce);
				throw new JStartupException(
						DCExceptionConstants.JSTARTUP_RESTART_CLUSTER_ERROR,
						new String[] { this.msHost, this.msPort + "" }, jscce);
			}
		} finally {
			if (clusterController != null) {
				clusterController.exit();
			}
		}
	}

	private JStartupClusterController getJStartupClusterController()
			throws JStartupException {
		final JStartupClusterControllerFactory clusterControllerFactory;
		try {
			if (location.bePath()) {
				tracePath(location, 
						"Invoke the JStartupInitialFactory get initial factory operation ...");
			}
			final JStartupInitialFactory jstartupInitialFactory = JStartupInitialFactory
					.getInitialFactory();
			if (location.bePath()) {
				tracePath(location, 
						"The JStartupInitialFactory is got [{0}]",
						new Object[] { jstartupInitialFactory });
			}

			if (location.bePath()) {
				tracePath(location, 
						"Invoke the JStartupInitialFactory.getClusterControllerFactory ...");
			}
			clusterControllerFactory = jstartupInitialFactory
					.getClusterControllerFactory();
			if (location.bePath()) {
				tracePath(location, 
						"The JStartupClusterControllerFactory is got [{0}]",
						new Object[] { clusterControllerFactory });
			}
		} catch (JStartupInstantiationException jsie) {
			DCLog.logErrorThrowable(location, null,
					"An error occurred while getting JStartupInitialFactory.",
					jsie);
			throw new JStartupException(
					DCExceptionConstants.JSTARTUP_INIT_FACTORY_ERROR, jsie);
		}

		try {
			if (location.bePath()) {
				tracePath(location, 
						"Invoke the JStartupClusterControllerFactory.createClusterController with the specified MS host [{0}] and MS port [{1}]",
						new Object[] { this.msHost, this.msPort });
			}
			final JStartupClusterController jStartupClusterController = clusterControllerFactory
					.createClusterController(this.msHost, this.msPort);
			if (location.bePath()) {
				tracePath(location, 
						"The JStartupClusterController has got [{0}]",
						new Object[] { jStartupClusterController });
			}

			return jStartupClusterController;
		} catch (IOException ioe) {
			DCLog.logErrorThrowable(location, null,
					"An error occurred while creating JStartupClusterController "
							+ "with the specified host '" + this.msHost
							+ "' and port '" + this.msPort + "'.", ioe);
			throw new JStartupException(
					DCExceptionConstants.JSTARTUP_CLUSTER_CTRL_ERROR, ioe);
		}
	}

	public void restartCurrentInstance() throws JStartupException {

		String runDir = SystemProfileManager
				.getSysParamValue(SystemProfileManager.DIR_CT_RUN);
		String host = SystemProfileManager
				.getSysParamValue(SystemProfileManager.SAPLOCALHOST);
		String instNum = SystemProfileManager
				.getSysParamValue(SystemProfileManager.SAPSYSTEM);

		SapControl sapControl;
		try {
			sapControl = SapControlFactory.getInstance()
					.createSapControl(threadSystem, runDir, host, osUserName,
							osUserPass, instNum);
			sapControl.restartInstance();
		} catch (SapControlException e) {
			throw new JStartupException(
					DCExceptionConstants.JSTARTUP_RESTART_INSTANCE_ERROR, e);
		}

	}

}
