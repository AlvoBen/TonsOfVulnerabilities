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

import java.util.List;

import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.ApplicationStatusResolver;
import com.sap.tc.logging.Location;

/**
 * This is a class for representation of calculated time statistic nodes.
 * 
 * @author Todor Stoitsev
 */
public class CalculatedTimeStatisticNode extends TimeStatisticNode {
	
	private static final Location location = 
		Location.getLocation(CalculatedTimeStatisticNode.class);

	public CalculatedTimeStatisticNode(String sNodeName) {
		super(sNodeName);
	}

	/**
	 * Retrieves info. whether the time statistic node is calculated. This node
	 * type is calculated.
	 * 
	 * @return
	 */
	public boolean getIsCalculated() {
		return true;
	}

	/**
	 * Retrieves the duration. It is calculated from the sub operation time
	 * statistic nodes.
	 */
	public long getDuration() {
		long duration = 0;
		List subNodes = getSubOpStat();
		// if no sub operations exists for this
		// calculated node assume its duration is 0 ms
		if (subNodes == null) {
			DSLog
					.traceDebug(
							location, 
							"Time statistic: Calculated node [{0}] does not contain sub operations. Its duration is considered 0 ms.",
							sNodePath);
			return UNKNOWN_TIME;
		}

		for (int i = 0; i < subNodes.size(); i++) {
			TimeStatisticNode oSubNode = (TimeStatisticNode) subNodes.get(i);
			long subDur = oSubNode.getDuration();
			if (subDur >= 0) {
				duration += subDur;
			} else {
				DSLog
						.traceWarning(
								location, 
								"ASJ.dpl_ds.004402",
								"Time statistic: Error - retrieved negative duration [{0}] for [{1}]!",
								subDur, oSubNode.getNodeName());
			}
		}
		return duration;
	}

	/**
	 * Retrieves the cpu duration. It always assumed '-1' which means none as
	 * the cpu duration is calculated for a given process may be even within a
	 * single thread, so it cannot be calculated as the sum of the CPU times of
	 * the nested operations.
	 */
	public float getCpuDuration() {
		return -1;
	}
}