﻿/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.deploy;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.dc.cm.lock.ParallelismStrategy;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.20
 */
public class DeployParallelismStrategy extends ParallelismStrategy {

	private transient static final long serialVersionUID = -7752898345868128472L;

	protected transient static final Map STRATEGY_MAP = new HashMap();

	public transient static final DeployParallelismStrategy NORMAL = new DeployParallelismStrategy(
			new Integer(0), "normal");

	public transient static final DeployParallelismStrategy SAFETY = new DeployParallelismStrategy(
			new Integer(1), "safety");

	static {
		STRATEGY_MAP.put(NORMAL.getName(), NORMAL);
		STRATEGY_MAP.put(SAFETY.getName(), SAFETY);
	}

	public static Map getNameAndParallelismStrategy() {
		return STRATEGY_MAP;
	}

	public static DeployParallelismStrategy getDeployParallelismStrategy(
			String name) {
		return (DeployParallelismStrategy) STRATEGY_MAP.get(name);
	}

	private DeployParallelismStrategy(Integer id, String name) {
		super(id, name, "deploy");
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeployParallelismStrategy)) {
			return false;
		}

		if (!super.equals(obj)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return super.hashCode();
	}

}
