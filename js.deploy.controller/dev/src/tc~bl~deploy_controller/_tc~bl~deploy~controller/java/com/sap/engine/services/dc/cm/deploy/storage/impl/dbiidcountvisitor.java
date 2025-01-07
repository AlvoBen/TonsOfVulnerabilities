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
package com.sap.engine.services.dc.cm.deploy.storage.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class DbiIdCountVisitor implements DeploymentBatchItemVisitor {

	private final Map biId_CountInteger;

	DbiIdCountVisitor() {
		biId_CountInteger = new HashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem dItem) {
		initBIIdCount(dItem.getBatchItemId());

		final Set depending = dItem.getDepending();

		initBIIdCount(depending.iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem cdItem) {
		initBIIdCount(cdItem.getBatchItemId());

		final Collection dItems = cdItem.getDeploymentItems();

		initBIIdCount(dItems.iterator());
	}

	private void initBIIdCount(Iterator dItemsIter) {
		DeploymentItem depDeplItem;
		while (dItemsIter.hasNext()) {
			depDeplItem = (DeploymentItem) dItemsIter.next();
			initBIIdCount(depDeplItem.getBatchItemId());
		}
	}

	private void initBIIdCount(BatchItemId biId) {
		if (biId.getIdCount() >= 0) {
			return;
		}
		final Integer count = (Integer) biId_CountInteger.get(biId);
		int currCoutValue = 0;
		if (count == null) {
			currCoutValue = 0;
		} else {
			currCoutValue = count.intValue();
			currCoutValue++;
		}
		biId.setIdCount(currCoutValue);
		biId_CountInteger.put(biId, new Integer(currCoutValue));
	}

}
