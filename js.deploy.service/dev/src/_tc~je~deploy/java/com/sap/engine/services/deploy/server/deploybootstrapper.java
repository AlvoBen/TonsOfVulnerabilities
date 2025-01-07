/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.editor.impl.DIBootstrapperThread;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.ServerState;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.LockUtils;
import com.sap.tc.logging.Location;

/**
 * Bootstraps all applications after the current server is ready, but before
 * initial applications start up. This bootstrapping will be invoked once per
 * instance.
 * 
 * @author Anton Georgiev
 * @version 7.00
 */
class DeployBootstrapper {
	private static final Location location = 
		Location.getLocation(DeployBootstrapper.class);
	private final ClusterMonitorHelper cmHelper;
	private final TransactionCommunicator communicator;
	private final String cfgDBootstrap;

	private static final String PREFIX = "DeployBootstrapper: ";
	private static final String CFG_BOOTSTRAPPER = "bootstrapper";

	private static final String PS_WORKER = "worker";
	private static final String PS_E_RESULT = "result";
	private static final String PS_E_CL_ELEM_ID = "clElemID";

	public DeployBootstrapper(final TransactionCommunicator communicator,
	    final ClusterMonitorHelper cmHelper) {
		this.communicator = communicator;
		this.cmHelper = cmHelper;
		cfgDBootstrap = DeployConstants.ROOT_CFG_DEPLOY_SERVICE + "/"
		    + DeployBootstrapper.CFG_BOOTSTRAPPER + "/"
		    + cmHelper.getCurrentInstanceId();
	}

	@SuppressWarnings("boxing")
	public void start() throws DeploymentException {
		final String phase = "start";
		final Set<String> apps4Bootstrap = Applications.getNames();
		if(apps4Bootstrap == null || apps4Bootstrap.isEmpty()) {
			if(location.bePath()) {
				DSLog.tracePath(location, "{0}[{1}] phase is canceled on [{2}], because "
					+ "none application is going to be bootstrapped.", PREFIX,
					phase, cmHelper.getCurrentServerId());
			}
			return;
		}

		final char lockType = LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;
		try {
			LockUtils.lockAndWait(cfgDBootstrap, lockType, 
				PropManager.getInstance().getTimeout4BootstrapLock());
		} catch(LockException le) {
			throwUnexpectedException(cfgDBootstrap + "Already locked: "
			    + cfgDBootstrap + " with lock type " + lockType + ".\nReason: "
			    + le.toString(), le);
		} catch(TechnicalLockException tle) {
			ServerDeploymentException sde = new ServerDeploymentException(
			    ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
			    new String[] {cfgDBootstrap, tle.getMessage()}, tle);
			sde.setMessageID("ASJ.dpl_ds.005071");
			throw sde;
		}
		try {
			final ConfigurationHandler handler = ConfigUtils
			    .getConfigurationHandler(PropManager.getInstance()
			        .getConfigurationHandlerFactory(), PREFIX);
			try {
				if(needBootstrap(handler, phase)) {
					if(location.bePath()) {
						DSLog.tracePath(location, "{0}Start downloading binaries "
						    + "of deployed applications [{1}].", PREFIX,
						    apps4Bootstrap);
					}
					long global = System.currentTimeMillis();
					boolean result = false;

					final Iterator<String> appIter = apps4Bootstrap.iterator();
					final Set<DIBootstrapperThread> callbackMonitor = 
						new HashSet<DIBootstrapperThread>(
							PropManager.getInstance()
					        	.getDeployBootstrapperThreads());
					final Set<String> failed2BootstrapAppNames = 
						new HashSet<String>();

					DIBootstrapperThread dibThread = null;
					synchronized(callbackMonitor) {
						try {
							for(int t = 1; t <= PropManager.getInstance()
							    .getDeployBootstrapperThreads(); t++) {
								dibThread = new DIBootstrapperThread(t,
								    appIter, communicator, handler,
								    callbackMonitor, failed2BootstrapAppNames);
								callbackMonitor.add(dibThread);
								PropManager.getInstance().getThreadSystem()
								    .startThread(dibThread, null,
								        DeployConstants.DEPLOY_APPBIN_THREAD + 
								        " " + t, true, true);
							}
							result = true;
						} finally {
							try {
								long startToWait = System.currentTimeMillis(), 
								waitedTime = -1, 
								timeout = PropManager.getInstance()
								    .getDeployBootstrapperTimeout();
								while(callbackMonitor.size() > 0) {
									if(DSLog.isDebugTraceable()) {
										DSLog.traceDebug(
											location,
											"{0}will wait [{1}] from [{2}] started threads to finish on [{3}] monitor.",
											PREFIX,
											callbackMonitor.size(),
											PropManager.getInstance()
												.getDeployBootstrapperThreads(),
											callbackMonitor);
									}
									callbackMonitor.wait(timeout);
									waitedTime = System.currentTimeMillis()
									    - startToWait;
									if(waitedTime >= timeout) {
										throw new IllegalThreadStateException(
										    "ASJ.dpl_ds.006118 Downloading the application binaries TIMED OUT after ["
											+ waitedTime
											+ "] ms waiting the started threads to finish their work. The issue might be resolved by reconfiguring the ["
											+ PropManager.DEPLOY_BOOTSTRAPPER_THREADS
											+ "] and ["
											+ PropManager.DEPLOY_BOOTSTRAPPER_TIMEOUT
											+ "] properties of deploy service.");
									}
								}
								if(DSLog.isDebugTraceable()) {
									DSLog.traceDebug(
										location,
										"{0}all [{1}] started threads finished.",
										PREFIX, PropManager.getInstance()
											.getDeployBootstrapperThreads());
								}
							} catch(InterruptedException iEx) {
								throwUnexpectedException(
								    "reading application data", iEx);
							} finally {
								try {
									handler.closeAllConfigurations();
									final Configuration cfg = ConfigUtils
										.getSubConfiguration(
											handler,
									        DeployConstants.ROOT_CFG_DEPLOY_SERVICE,
									        DeployBootstrapper.CFG_BOOTSTRAPPER,
									        "" + cmHelper.getCurrentInstanceId());
									try {
										final PropertySheet ps = ConfigUtils
										    .recreateSubConfiguration(
										        cfg,
										        PS_WORKER,
										        Configuration.CONFIG_TYPE_PROPERTYSHEET)
										    .getPropertySheetInterface();
										ps.createPropertyEntry(PS_E_RESULT,
											"" + (failed2BootstrapAppNames.size() == 0 ? 
											true : false), "");
										ps.createPropertyEntry(PS_E_CL_ELEM_ID,
										    "" + cmHelper.getCurrentServerId(),
										    "");
									} finally {
										handler.commit();
									}
								} catch(ConfigurationException ce) {
									throwUnexpectedException(
									    "modifying the property sheet "
									        + PS_WORKER + " from "
									        + cfgDBootstrap, ce);
								} finally {
									if(failed2BootstrapAppNames.size() <= 0
									    && callbackMonitor.size() <= 0
									    && result) {
										if(DSLog.isDebugTraceable()) {
											DSLog.traceDebug(
												location,
												"The deploy service downloaded from DB the binaries of all applications for [{0}] ms.",
												(System.currentTimeMillis() - global));
										}
									} else {
										DSLog.logWarning(location, "ASJ.dpl_ds.003012",
											"The deploy service was NOT able to download from DB the binaries of all applications for [{0}] ms {1}",
											(System.currentTimeMillis() - global),
											(failed2BootstrapAppNames.size() <= 0 ? "." : ", because applications ["
											+ failed2BootstrapAppNames + "] are wrong, ")
											+ (callbackMonitor.size() <= 0 ? "" : "threads ["
											+ callbackMonitor + "] did not finish ")
											+ (result ? "." : " and some threads failed to start."));
									}
								}
							}
						}
					}
				} else {
					if(location.bePath()) {
						DSLog.tracePath(
							location,
							"{0}This server node won't [{1}] to bootstrap the deployed applications, becasue they are bootstraped from other server in the same instance.",
							PREFIX, phase);
					}
				}
			} finally {
				ConfigUtils.closeAllConfigurations(handler, PREFIX);
			}
		} finally {
			try {
				LockUtils.unlock(cfgDBootstrap, lockType);
			} catch(TechnicalLockException tle) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
				    new String[] {cfgDBootstrap, tle.getMessage()}, tle);
				sde.setMessageID("ASJ.dpl_ds.005071");
				throw sde;
			}
		}
	}

	public ConfigurationHandler prepareServer(ConfigurationHandler handler)
	    throws DeploymentException {
		final ServerState sState = PropManager.getInstance().getServerState();
		if(ServerState.SAFE_UPGRADE.equals(sState)) {
			if(DSLog.isDebugTraceable()) {
				DSLog.traceDebug(
					location,
					"Preparation of server for applications bootstrapping is canceled, because the server state is [{0}].",
					sState.getName());
			}
			return handler;
		}

		final String phase = "prepare";
		if(!isPhaseAcceptable(phase + " (without lock)")) {
			return handler;
		}
		try {
			LockUtils.lock(cfgDBootstrap,
			    LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
		} catch(LockException e) {
			if(location.bePath()) {
				DSLog.tracePath(
					location,
					"{0}[{1}] for bootstrapping is canceled, because the [{2}] can not be locked.",
					PREFIX, phase, cfgDBootstrap);
			}
			return handler;
		} catch(TechnicalLockException e) {
			if(location.bePath()) {
				DSLog.tracePath(
					location,
					"{0}[{1}] for bootstrapping is canceled, because the [{2}] can not be locked.",
					PREFIX, phase, cfgDBootstrap);
			}
			return handler;
		}
		try {// finally -> unlock
			if(!isPhaseAcceptable(phase + " (with lock)")) {
				return handler;
			}
			if(!needBootstrap(handler, phase)) {
				try {
					final Configuration cfg = handler.openConfiguration(
					    cfgDBootstrap, ConfigurationHandler.WRITE_ACCESS);
					ConfigUtils.deleteAllSubConfiguration(cfg);
					ConfigUtils.commitHandler(handler,
					    "managing service configs");
				} catch(NameNotFoundException nnfe) {
					// $JL-EXC$ - there is not other way to check does one cfg
					// exist via handler.
				} finally {
					ConfigUtils.closeAllConfigurations(handler, PREFIX);
				}
			} else {
				if(location.bePath()) {
					DSLog.tracePath(
						location,
						"{0}This server node won't [{1}] to bootstrap the deployed applications, because the instance is prepared.",
						PREFIX, phase);
				}
			}
		} catch(ConfigurationException ce) {
			throwUnexpectedException("deleting the property sheet " + PS_WORKER
			    + " from " + cfgDBootstrap, ce);
		} finally {
			try {
				LockUtils.unlock(cfgDBootstrap,
				    LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE);
			} catch(TechnicalLockException tle) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    ExceptionConstants.CANNOT_LOCK_BECAUSE_OF_TECHNICAL_PROBLEMS,
				    new String[] {cfgDBootstrap, tle.getMessage()}, tle);
				sde.setMessageID("ASJ.dpl_ds.005071");
				throw sde;
			}
		}
		return handler;
	}

	@SuppressWarnings("boxing")
	private boolean isPhaseAcceptable(String phase) {
		int clusterId = cmHelper.findAnotherRunningServerInCurrentInstance();
		if(clusterId != -1) {
			if(location.bePath()) {
				DSLog.tracePath(location, "{0}[{1}] phase is canceled on [{2}], "
				    + "because [{3}] is in RUNNING state", PREFIX, phase,
				    cmHelper.getCurrentServerId(), clusterId);
			}
			return false;
		}
		return true;
	}

	private boolean needBootstrap(ConfigurationHandler handler, String phase)
	    throws DeploymentException {
		try {
			final Configuration config;
			try {
				config = handler.openConfiguration(cfgDBootstrap,
				    ConfigurationHandler.READ_ACCESS);
			} catch(NameNotFoundException nnfe) {
				// $JL-EXC$ - there is not other way to check does one cfg exist
				// via handler.
				if(location.beDebug()) {
					DSLog.traceDebug(
						location,
						"{0}need bootstrap in [{1}] phase will return TRUE, because the [{2}] is not found.",
						PREFIX, phase, cfgDBootstrap);
				}
				return true;
			}
			if(config.existsSubConfiguration(PS_WORKER)) {
				if(location.beDebug()) {
					DSLog.traceDebug(
						location,
						"{0}need bootstrap in [{1}] phase will return FALSE, because the property sheet [{2}] exists in [{3}] .",
						PREFIX, phase, PS_WORKER, cfgDBootstrap);
				}
				return false;
			}
			if(location.beDebug()) {
				DSLog.traceDebug(
					location,
					"{0}need bootstrap in [{1}] phase will return TRUE, because the property sheet [{2}] DOESN'T exist in [{3}].",
					PREFIX, phase, PS_WORKER, cfgDBootstrap);
			}
			return true;
		} catch(ConfigurationException ce) {
			throwUnexpectedException("reading the property sheet " + PS_WORKER
			    + " from " + cfgDBootstrap, ce);
		} finally {
			ConfigUtils.closeAllConfigurations(handler, PREFIX);
		}
		return true;
	}

	private void throwUnexpectedException(String message, Exception ex)
	    throws ServerDeploymentException {
		final ServerDeploymentException sde = new ServerDeploymentException(
		    ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
		    new String[] {message}, ex);
		sde.setMessageID("ASJ.dpl_ds.005082");
		throw sde;
	}
}