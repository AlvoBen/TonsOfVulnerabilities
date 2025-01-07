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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;
import com.sap.engine.services.dc.repo.LocationConstants;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class UIPersistor implements UndeployItemVisitor{

	private Configuration dcCfg;
	private Configuration scCfg;
	private Map<UiId, Integer> uiIDW_Integer;
	private ConfigurationException exception; 

	private static UIPersistor INSTANCE;

	private UIPersistor() {
	}

	public static synchronized UIPersistor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UIPersistor();
		}
		return INSTANCE;
	}

	public void persistUI(Set<GenericUndeployItem> undeplItems, Configuration parentCfg,
			Map<UiId, Integer> uiIDW_Integer) throws ConfigurationException {

		exception = null;
		dcCfg = parentCfg.createSubConfiguration(LocationConstants.DC);
		scCfg = parentCfg.createSubConfiguration(LocationConstants.SC);
		this.uiIDW_Integer = uiIDW_Integer;
		
 		Iterator<GenericUndeployItem> uiIter = undeplItems.iterator();
		while (uiIter.hasNext()) {
			uiIter.next().accept(this);
			if (exception != null) throw exception;
		}
		
	}

	private Configuration getUiCfg(UndeployItemId uiId,
			Configuration dbiParentCfg) throws ConfigurationException {
		Configuration dbiCfg = null;
		try {
			dbiCfg = dbiParentCfg.createSubConfiguration(uiId.toString());
		} catch (NameAlreadyExistsException naee) {
			dbiCfg = dbiParentCfg.getSubConfiguration(uiId.toString());
		}
		return dbiCfg.createSubConfiguration(uiId.getIdCount() + "");
	}

	private void visit(Configuration cfg, GenericUndeployItem uItem, Map<UiId, Integer> uiIDW_Integer)
			throws ConfigurationException {
		UndeployStorageUtil.setUndeployItemId(uItem.getId(), cfg);
		cfg.addConfigEntry(UndeployConstants.UI_UNDEPLOY_ITEM_STATUS, uItem
				.getUndeployItemStatus().getName());
		cfg.addConfigEntry(UndeployConstants.UI_DESCRIPTION, uItem
				.getDescription());
		// Persists the number of this UndeployItem in the sorted ones,
		// if it is there.
		persistNumer(uItem.getId(), cfg, uiIDW_Integer);

	}

	private void persistNumer(UndeployItemId uiId, Configuration uiCfg,
			Map<UiId, Integer> uiIDW_Integer) throws ConfigurationException {
		final Integer number = (Integer) uiIDW_Integer.get(new UiId(uiId));
		if (number != null) {
			uiCfg.addConfigEntry(UndeployConstants.UI_SORTED_NUMBER, number);
		}
	}

	private void persistSetWithUI(Set<UndeployItem> uiSet, Configuration parentCfg,
			String childCfgName) throws ConfigurationException {
		if (uiSet == null || uiSet.size() == 0) {
			return;
		}
		final Configuration childCfg = parentCfg
				.createSubConfiguration(childCfgName);
		final Iterator<UndeployItem> uiIter = uiSet.iterator();
		GenericUndeployItem uItem;
		Configuration currUICfg;
		int count = 0;
		while (uiIter.hasNext()) {
			count++;
			uItem = uiIter.next();
			currUICfg = childCfg.createSubConfiguration((new Integer(count))
					.toString(), Configuration.CONFIG_TYPE_PROPERTYSHEET);
			UndeployStorageUtil.setUndeployItemId(uItem.getId(), currUICfg
					.getPropertySheetInterface());
		}
	}

	public void visit(UndeployItem undeployItem) {
		Configuration idCountUiCfg;
		try {
			idCountUiCfg = getUiCfg(undeployItem.getId(), dcCfg);
			visit(idCountUiCfg, undeployItem, uiIDW_Integer);
			// Persists depending on this
			persistSetWithUI(undeployItem.getDependingOnThis(), idCountUiCfg,
					UndeployConstants.UI_DEP_ON_THIS);
		} catch (ConfigurationException e) {
			exception = e;
		}
	}

	public void visit(ScaUndeployItem undeployItem) {
		Configuration idCountUiCfg;
		try {
			idCountUiCfg = getUiCfg(undeployItem.getId(), scCfg);
			visit(idCountUiCfg, undeployItem, uiIDW_Integer);
		} catch (ConfigurationException e) {
			exception = e;
		}
	}

}
