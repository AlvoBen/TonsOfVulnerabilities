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
package com.sap.engine.services.dc.api.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-5
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public final class DeployApiMapper {
	/**
	 * 1800000 ms = 30min
	 */
	public static final long DEFAULT_ENGINE_START_TIMEOUT = 9000000;// 2.5 hours
	public static final long DEFAULT_PING_SLEEP_INTERVAL = 30000; // 30 sec
	public static final long DEFAULT_DCLOCK_EXCEPTION_TIMEOUT = 30000; // 30 sec

	private static final String API_PROPS_LOCATION = "com/sap/engine/services/dc/api/resources/common_api.properties";
	private static Hashtable errorStrategyActions = new Hashtable();
	private static Hashtable errorStrategies = new Hashtable();
	private static long serverTimeoutValue = -1;
	private static long pingTimeoutValue = -1;
	private static long dcLockExceptionTimeout = -1;
	private static boolean shoudLoadPropFile = true;
	private static int asyncNotificationThreads = 5;
	private static long reconnectLoggingInterval = 15000;

	static {
		// init error actions
		errorStrategyActions
				.put(
						com.sap.engine.services.dc.api.ErrorStrategyAction.DEPLOYMENT_ACTION,
						com.sap.engine.services.dc.cm.ErrorStrategyAction.DEPLOYMENT_ACTION);
		errorStrategyActions
				.put(
						com.sap.engine.services.dc.api.ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
						com.sap.engine.services.dc.cm.ErrorStrategyAction.PREREQUISITES_CHECK_ACTION);
		errorStrategyActions
				.put(
						com.sap.engine.services.dc.api.ErrorStrategyAction.UNDEPLOYMENT_ACTION,
						com.sap.engine.services.dc.cm.ErrorStrategyAction.UNDEPLOYMENT_ACTION);
		// init error strategies
		errorStrategies
				.put(
						com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_SKIP_DEPENDING,
						com.sap.engine.services.dc.cm.ErrorStrategy.ON_ERROR_SKIP_DEPENDING);
		errorStrategies.put(
				com.sap.engine.services.dc.api.ErrorStrategy.ON_ERROR_STOP,
				com.sap.engine.services.dc.cm.ErrorStrategy.ON_ERROR_STOP);
	}

	public static boolean isValidErrorStrategyAction(
			com.sap.engine.services.dc.api.ErrorStrategyAction errorStrategyAction) {
		return errorStrategyActions.get(errorStrategyAction) != null;
	}

	public static com.sap.engine.services.dc.cm.ErrorStrategyAction mapErrorAction(
			com.sap.engine.services.dc.api.ErrorStrategyAction errorStrategyAction) {
		com.sap.engine.services.dc.cm.ErrorStrategyAction ret = (com.sap.engine.services.dc.cm.ErrorStrategyAction) errorStrategyActions
				.get(errorStrategyAction);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1164] Unknown error strategy action type "
							+ errorStrategyAction + " detected");
		}
		return ret;
	}

	public static com.sap.engine.services.dc.cm.ErrorStrategy mapErrorStrategy(
			com.sap.engine.services.dc.api.ErrorStrategy errorStrategy) {
		com.sap.engine.services.dc.cm.ErrorStrategy ret = (com.sap.engine.services.dc.cm.ErrorStrategy) errorStrategies
				.get(errorStrategy);
		if (ret == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1165] Unknown error strategy "
							+ errorStrategy + " detected");
		}
		return ret;
	}

	private static void loadValuesFromPropFile() {
		if (shoudLoadPropFile) {
			try {
				Properties props = load(API_PROPS_LOCATION);
				serverTimeoutValue = Long.parseLong(props
						.getProperty("deploy.api.engine.start.timeout.value"));
				pingTimeoutValue = Long.parseLong(props
						.getProperty("deploy.api.engine.ping.timeout.value"));
				dcLockExceptionTimeout = Long.parseLong(props
						.getProperty("dc.lock.exception.timeout"));

				asyncNotificationThreads = Integer.parseInt(props
						.getProperty("async.notification.threads"));

				reconnectLoggingInterval = Long.parseLong(props
						.getProperty("reconnect.logging.interval"));

			} catch (IOException e) {
				e.printStackTrace();
				serverTimeoutValue = DEFAULT_ENGINE_START_TIMEOUT;
				pingTimeoutValue = DEFAULT_PING_SLEEP_INTERVAL;
				dcLockExceptionTimeout = DEFAULT_DCLOCK_EXCEPTION_TIMEOUT;
			}
			shoudLoadPropFile = false;
		}
	}

	public static long getServerTimeout() {
		loadValuesFromPropFile();
		return serverTimeoutValue;
	}

	public static long getReconnectLoggingInterval() {
		loadValuesFromPropFile();
		return reconnectLoggingInterval;
	}

	public static long getPingTimeout() {
		loadValuesFromPropFile();
		return pingTimeoutValue;
	}

	public static long getDcLockExceptionTimeout() {
		loadValuesFromPropFile();
		return dcLockExceptionTimeout;
	}

	public static int getAsyncNotificationThreads() {
		loadValuesFromPropFile();
		return asyncNotificationThreads;
	}

	public String toSTring() {
		return "Deploy Api Mapper";
	}

	public static Properties load(String propsPath) throws IOException {
		final Properties props = new Properties();
		InputStream is = null;
		try {
			final InputStream resInputStream = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(propsPath);
			// DeployApiMapper.class.getClassLoader().getResourceAsStream(
			// propsPath);
			if (resInputStream == null) {
				new IOException(
						"The system could not get input stream from the "
								+ "specified resource '" + propsPath + "'.");
			}

			is = new BufferedInputStream(resInputStream);

			props.load(is);

			return props;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

}
