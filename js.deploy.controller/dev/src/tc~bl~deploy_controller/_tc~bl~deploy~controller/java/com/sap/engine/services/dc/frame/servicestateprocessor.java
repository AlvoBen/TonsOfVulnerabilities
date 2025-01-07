package com.sap.engine.services.dc.frame;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.monitor.CoreMonitor;
import com.sap.engine.services.dc.frame.impl.NormalServiceStateProcessorImpl;
import com.sap.engine.services.dc.frame.impl.SafeServiceStateProcessorImpl;
import com.sap.engine.services.dc.manage.ServiceConfigurer;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-11
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class ServiceStateProcessor {

	private static ServiceStateProcessor INSTANCE;

	/**
	 * 
	 * The state processor is created once per server node's lifetime and
	 * depends on the current server run mode
	 * 
	 * 
	 */
	public static synchronized ServiceStateProcessor getInstance() {

		if (INSTANCE == null) {
			createInstance();
		}
		return INSTANCE;
	}

	/**
	 * 
	 * If the server is in safe mode this means that there is just a single
	 * server node running in the whole cluster which makes the processing a lot
	 * easier. In this case a simplified state processor could be returned.
	 * 
	 * @param appServiceCtx
	 */
	private static void createInstance() {

		ApplicationServiceContext appServiceCtx = ServiceConfigurer
				.getInstance().getApplicationServiceContext();
		final CoreMonitor cm = appServiceCtx.getCoreContext().getCoreMonitor();

		if (cm.getRuntimeMode() == CoreMonitor.RUNTIME_MODE_SAFE) {

			INSTANCE = new SafeServiceStateProcessorImpl();

		} else {
			INSTANCE = new NormalServiceStateProcessorImpl();
		}

	}

	/**
	 * This method is called when the deploy controller service starts.
	 * 
	 * @throws ServiceStateProcessingException
	 */
	public abstract void start() throws ServiceStateProcessingException;

	public abstract void stop() throws ServiceStateProcessingException;

	public abstract void containerStarted()
			throws ServiceStateProcessingException;

	public abstract void notifyMainRollingNode()
			throws ServiceStateProcessingException;

	/**
	 * This method should be called when a message is received from some other
	 * node in the cluster informing the others that the operation after the
	 * restart is complete.
	 */
	public abstract void handleRegisterDC();

}
