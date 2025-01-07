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
package com.sap.engine.services.dc.cm.undeploy.storage.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class UIIdCounter {

	private static UIIdCounter INSTANCE;

	private UIIdCounter() {
	}

	public static synchronized UIIdCounter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UIIdCounter();
		}
		return INSTANCE;
	}

	public void initIdCount(Set<GenericUndeployItem> undeplItems) {
		final Map<UndeployItemId, Integer> uiId_CountInteger = new HashMap<UndeployItemId, Integer>();
		Iterator<GenericUndeployItem> uiIter = undeplItems.iterator();
		GenericUndeployItem uItem;
		while (uiIter.hasNext()) {
			uItem = uiIter.next();
			// current UI, which is SDA
			initUiIdCount(uItem.getId(), uiId_CountInteger);
			
		}
	}
	
	private void initUiIdCount(UndeployItemId uiId, Map<UndeployItemId, Integer> uiId_CountInteger) {
		if (uiId.getIdCount() >= 0) {
			return;
		}
		final Integer count = (Integer) uiId_CountInteger.get(uiId);
		int currCoutValue = 0;
		if (count == null) {
			currCoutValue = 0;
		} else {
			currCoutValue = count.intValue();
			currCoutValue++;
		}
		uiId.setIdCount(currCoutValue);
		uiId_CountInteger.put(uiId, new Integer(currCoutValue));
	}

}
