/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.timestat;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for a time statistic node representation.
 * 
 * @author Todor Stoitsev
 * 
 * @see CalculatedTimeStatisticNode
 * @see FixedTimeStatisticNode
 */
public abstract class TimeStatisticNode {

	/**
	 * Tree path separator for the time statistic nodes.
	 */
	public static final String TREE_PATH_SEP = "/";

	/**
	 * A constant for undefined time.
	 */
	public static final long UNKNOWN_TIME = 0;

	protected String sNodeName;

	protected String sAppName;

	protected String sNodePath;

	protected List subOpStat;

	/**
	 * Constructor - used for convenience for nodes, that do not have an
	 * application name.
	 * 
	 * @param sNodeName
	 *            - name of the node.
	 */
	public TimeStatisticNode(String sNodeName) {
		this(sNodeName, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param sNodeName
	 *            - name of the node
	 */
	public TimeStatisticNode(String sNodeName, String sAppName) {
		this.sNodeName = sNodeName;
		// initially set path equal to name -
		// especially important for root nodes
		this.sNodePath = this.sNodeName;
		// set application name
		this.sAppName = sAppName != null ? sAppName : "";
	}

	/**
	 * Retrieves the name of the time statistic node.
	 * 
	 * @return
	 */
	public String getNodeName() {
		return sNodeName;
	}

	/**
	 * Retrieves the name of the application for which this statistic is
	 * triggered.
	 * 
	 * @return
	 */
	public String getAppName() {
		return sAppName;
	}

	/**
	 * Adds a sub operation statistic. Note that the path is modified in this
	 * method so that the added sub node inherits the parent's path. Adding a
	 * single operation node to more than one parent is invalid.
	 * 
	 * @param stat
	 */
	public void addSubOpStat(TimeStatisticNode stat) {
		if (stat == null)
			return;
		// initialize once only if required
		if (subOpStat == null)
			subOpStat = new ArrayList();
		// add sub node
		subOpStat.add(stat);
		// set the path of the node
		stat.setPath(this.getPath() + TREE_PATH_SEP + stat.getNodeName());
	}

	/**
	 * Retyrieves the sub operaton statistics list.
	 */
	public List getSubOpStat() {
		return subOpStat;
	}

	/**
	 * Mutator for the node path.
	 * 
	 * @return
	 */
	private void setPath(String sPath) {
		this.sNodePath = sPath;
	}

	/**
	 * Accessor for the node path.
	 * 
	 * @return
	 */
	public String getPath() {
		return this.sNodePath;
	}

	/**
	 * Overrides the method to give adequate node info.
	 */
	public String toString() {
		String sSep = "|";
		return "<NAME=" + sNodeName + sSep + "CALCULATED=" + getIsCalculated()
				+ sSep + "DURATION=" + getDuration() + sSep + "CHILDERN="
				+ subOpStat + ">";
	}

	/**
	 * Retrieves info. whether the time statistic node is calculated
	 * 
	 * @return
	 */
	public abstract boolean getIsCalculated();

	/**
	 * Retrieves the duration.
	 * 
	 * @return
	 */
	public abstract long getDuration();

	/**
	 * Retrieves the cpu duration.
	 * 
	 * @return
	 */
	public abstract float getCpuDuration();
}