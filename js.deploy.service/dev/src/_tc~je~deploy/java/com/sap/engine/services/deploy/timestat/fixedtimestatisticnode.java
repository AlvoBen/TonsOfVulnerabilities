/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.timestat;

/**
 * This class represents a time statistic node with a fixed start and end time.
 * The concrete implementation is left to the operation classes.
 * 
 * @author Todor Stoitsev
 * 
 * @see OperationTimeStat
 */
public abstract class FixedTimeStatisticNode extends TimeStatisticNode {

	public FixedTimeStatisticNode(String sNodeName, String sAppName) {
		super(sNodeName, sAppName);
	}

	/**
	 * Retrieves info. whether the time statistic node is calculated. This node
	 * is not calculated because start and end time are fixed.
	 * 
	 * @return
	 */
	public boolean getIsCalculated() {
		return false;
	}
}
