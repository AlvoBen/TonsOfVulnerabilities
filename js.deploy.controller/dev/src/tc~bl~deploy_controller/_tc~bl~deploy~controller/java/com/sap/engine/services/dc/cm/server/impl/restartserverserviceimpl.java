package com.sap.engine.services.dc.cm.server.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService;
import com.sap.engine.services.dc.cm.server.spi.ServerModeService;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.DCState;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
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
final class RestartServerServiceImpl extends AbstractRestartServerService
		implements RestartServerService {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static final String CLUSTER_MANAGER = "ClusterManager";
	private static final String MS_HOST = "ms.host";
	private static final String MS_PORT = "ms.port";

	RestartServerServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.impl.AbstractRestartServerService
	 * #getMSHost()
	 */
	protected String getMSHost() {
		return ServiceConfigurer.getInstance().getApplicationServiceContext()
				.getCoreContext().getCoreMonitor().getManagerProperty(
						CLUSTER_MANAGER, MS_HOST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.impl.AbstractRestartServerService
	 * #getMSPort()
	 */
	protected String getMSPort() {
		return ServiceConfigurer.getInstance().getApplicationServiceContext()
				.getCoreContext().getCoreMonitor().getManagerProperty(
						CLUSTER_MANAGER, MS_PORT);
	}

	protected ServerModeService getServerModeService()
			throws RestartServerServiceException {
		final Server server = ServerFactory.getInstance().createServer();
		final ServerService serverService = server
				.getServerService(ServerFactory.getInstance()
						.createServerModeRequest());

		if (serverService == null
				|| !(serverService instanceof ServerModeService)) {
			final String errMsg = "ASJ.dpl_dc.003159 Received ServerService for get/set the server mode "
					+ "which is not of type ServerModeService.";
			// TODO: log the error: log.fatal(errMsg);
			throw new RestartServerServiceException(errMsg);
		}

		return (ServerModeService) serverService;
	}

	/**
	 * This method should wait until the thread is interrupted or a timeout
	 * occurs. It is assumed that when the thread is interrupted the
	 * Naming(JNDI) is already stopped and DC api will not be able to reconnect
	 * immediately before the server is restarted.
	 * 
	 * @throws RestartServerServiceException
	 *             when a timeout occurs - in this case the DCState should be
	 *             restored to working, so that the upper finally blocks can do
	 *             proper cleanup. If this exception is thrown we should do our
	 *             best to make sure that no deployment will start because this
	 *             will be confusing for the callers.
	 */
	protected void ensureServerDown(boolean restartOnlyTheInstance)
			throws RestartServerServiceException {
		final long waitMillis = 30000;// 30 seconds
		final long startedProcessTimeMillis = System.currentTimeMillis();
		final long timeOutMillis = RestartServerServicePropsLoader
				.getInstance().getRestartTimeout();

		// set the DC state to restarting so that the
		// FS, DB, ENQ lock and the deployment data are not cleared
		// when we throw an exception in order to release the thread
		DCState oldState = DCManager.getInstance().getDCState();
		DCManager.getInstance().setDCState(DCState.RESTARTING);

		// sleep until the thread is interrupted or timeout occurs
		do {
			try {
				if (isDebugLoggable()) {
					if (restartOnlyTheInstance) {
						if (location.beDebug()) {
							traceDebug(location, 
								"After restart instance is still running. Waiting [{0}] milliseconds for the restart process ...",
								new Object[] { String.valueOf(waitMillis) });
						}
					} else {
						if (location.beDebug()) {
							traceDebug(location, 
								"ASJ.dpl_dc.003161 After restart cluster is still running. Waiting [{0}] milliseconds for the restart process ...",
								new Object[] { String.valueOf(waitMillis) });
						}
					}

				}

				Thread.sleep(waitMillis);
			} catch (InterruptedException ie) {

				// at this point the cross service lets us know that it is
				// stopping
				// and we throw a runtime exception so that the thread is
				// released
				// and the cross service could stop cleanly. Currently the cross
				// service closes the client sockets before interrupting the
				// threads
				// which means that the timeout exdeption might not reach the
				// client
				// At this point the naming should already be stopped and DC_API
				// should not
				// be able to reconnect and get a deploy result too early
				throw new RuntimeException(
						"The waiting thread was interrupted as expected."
								+ " This exception does not denote an error. "
								+ "It is thrown for the sole reason to release the thread",
						ie);

			}
		} while (System.currentTimeMillis() - startedProcessTimeMillis < timeOutMillis);

		// restore the old state
		DCManager.getInstance().setDCState(oldState);

		if (restartOnlyTheInstance) {
			DCLog
					.logError(location, 
							"ASJ.dpl_dc.004923",
							"The Deploy Controller was not able to restart the instance for [{0}] milliseconds. The reason is that a timeout occurred while waiting for the restart. Contact the SAP AS Java cluster administrator in order to find out the reason.",
							new Object[] { new Long(timeOutMillis) });

			throw new RestartServerServiceException(
					DCExceptionConstants.SERVER_NOT_ABLE_TO_RESTART_INST,
					new Object[] { new Long(timeOutMillis) });
		} else {
			DCLog
					.logError(location, 
							"ASJ.dpl_dc.004924",
							"Deploy Controller was not able to restart the cluster for [{0}] milliseconds. The reason is that a timeout occurred while waiting for the restart. Contact the AS Java cluster administrator in order to find out the reason.",
							new Object[] { new Long(timeOutMillis) });

			throw new RestartServerServiceException(
					DCExceptionConstants.SERVER_NOT_ABLE_TO_RESTART,
					new Object[] { new Long(timeOutMillis) });
		}
	}

	protected void clear() {

	}

	@Override
	protected String getOsUserName() {
		return ServiceConfigurer.getInstance().getOsUser();
	}

	@Override
	protected String getOsUserPass() {
		return ServiceConfigurer.getInstance().getOsPass();
	}

	@Override
	protected ThreadSystem getThreadSystem() {
		ThreadSystem threadSystem = null;
		ApplicationServiceContext applicationServiceContext = ServiceConfigurer
				.getInstance().getApplicationServiceContext();
		if (applicationServiceContext != null) {
			// the restart is triggerd from the engine
			threadSystem = ServiceConfigurer.getInstance()
					.getApplicationServiceContext().getCoreContext()
					.getThreadSystem();
		}
		return threadSystem;
	}

}
