package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class ErrorStrategies {

	private static final ErrorStrategy DEFAULT_ERROR_STRATEGY = ErrorStrategy.ON_ERROR_STOP;

	private Map stategiesMap = new HashMap();

	public static ErrorStrategies createInstance() {
		return new ErrorStrategies();
	}

	private ErrorStrategies() {
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.ErrorStrategies#setErrorStrategy(java.lang
	 * .Integer, com.sap.engine.services.dc.cm.ErrorStrategy)
	 */
	void setErrorStrategy(ErrorStrategyAction errorStrategyAction,
			ErrorStrategy strategy) {
		if (errorStrategyAction == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003188 The specified error strategy action is null!");
		}
		if (strategy == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003189 The specified error strategy is null!");
		}

		this.stategiesMap.put(errorStrategyAction, strategy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.ErrorStrategies#getErrorStrategy(java.lang
	 * .Integer)
	 */
	public ErrorStrategy getErrorStrategy(
			ErrorStrategyAction errorStrategyAction) {
		final ErrorStrategy errorStrategy = (ErrorStrategy) this.stategiesMap
				.get(errorStrategyAction);
		if (errorStrategy == null) {
			return DEFAULT_ERROR_STRATEGY;
		}

		return errorStrategy;
	}

	private void init() {
		this.stategiesMap.put(ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
				ErrorStrategy.ON_ERROR_STOP);

		this.stategiesMap.put(ErrorStrategyAction.DEPLOYMENT_ACTION,
				ErrorStrategy.ON_ERROR_STOP);

		this.stategiesMap.put(ErrorStrategyAction.UNDEPLOYMENT_ACTION,
				ErrorStrategy.ON_ERROR_STOP);
	}

	public String toString() {
		final StringBuffer sbToString = new StringBuffer();
		for (Iterator iter = this.stategiesMap.entrySet().iterator(); iter
				.hasNext();) {
			final Map.Entry entry = (Map.Entry) iter.next();
			sbToString.append("Error Handling Action: [")
					.append(entry.getKey()).append(
							"], Error Handling Strategy: [").append(
							entry.getValue()).append("]");
			if (iter.hasNext()) {
				sbToString.append(Constants.EOL);
			}
		}

		return sbToString.toString();
	}

}
