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

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class DBISplitVisitor implements DeploymentBatchItemVisitor {

	private final Map biIdW_dbItem = new HashMap();

	DBISplitVisitor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem dItem) {
		biIdW_dbItem.put(new BiId(dItem.getBatchItemId()), dItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem cdItem) {
		biIdW_dbItem.put(new BiId(cdItem.getBatchItemId()), cdItem);
		final Collection dItems = cdItem.getDeploymentItems();
		if (dItems == null) {
			return;
		}
		final Iterator dItemIter = dItems.iterator();
		DeploymentItem dItem;
		while (dItemIter.hasNext()) {
			dItem = (DeploymentItem) dItemIter.next();
			dItem.accept(this);
		}
	}

	public Map getBiIdW_dbItem() {
		return biIdW_dbItem;
	}

}
