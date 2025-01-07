package com.sap.engine.services.dc.cm.undeploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class UndeploymentException extends CMException {

	private static final long serialVersionUID = 2104398920194920321L;

	private final Collection undeployItems;// $JL-SER$
	private final Collection orderedUndeployItems;// $JL-SER$

	public UndeploymentException(String errMessage) {
		super(errMessage);

		this.undeployItems = new ArrayList();
		this.orderedUndeployItems = new ArrayList();
	}

	public UndeploymentException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);

		this.undeployItems = new HashSet();
		this.orderedUndeployItems = new HashSet();
	}

	public void addUndeployItems(Collection _orderedUndeployItems,
			Collection _undeployItems) {
		if (_undeployItems != null) {
			this.undeployItems.addAll(_undeployItems);
		}
		if (_orderedUndeployItems != null) {
			this.orderedUndeployItems.addAll(_orderedUndeployItems);
		}
	}

	public void removeUndeployItems(Collection _undeployItems) {
		this.undeployItems.removeAll(_undeployItems);
	}

	public Collection getUndeployItems() {
		return this.undeployItems;
	}

	public Collection getOrderedUndeployItems() {
		return this.orderedUndeployItems;
	}
}
