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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.InvalidValueException;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.NoWriteAccessException;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.cm.deploy.DeployFactory;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SduId;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class DeployStorageUtil {

	/**
	 * Returns <code>Map</code>, where the key is
	 * <code>BatchItemIdWrapper</code> and value is <code>Integer</code>.
	 * 
	 * @param sortedDBItems
	 *            <code>DeploymentBatchItem</code>s
	 * @return <code>Map</code>
	 */
	static Map getOrder(Collection sortedDBItems) {
		final Map res = new HashMap();
		final Iterator iter = sortedDBItems.iterator();
		DeploymentBatchItem dbItem;
		int number = 0;
		while (iter.hasNext()) {
			dbItem = (DeploymentBatchItem) iter.next();
			res.put(new BiId(dbItem.getBatchItemId()), new Integer(number));
			number++;
		}
		return res;
	}

	// /////////////////////
	// SET //
	// /////////////////////
	static void setBatchItemId(PropertySheet ps, BatchItemId batchItemId)
			throws NameAlreadyExistsException, NoWriteAccessException,
			ConfigurationException {
		ps.createPropertyEntry(DeployConstants.DBI_NAME, batchItemId.getSduId()
				.getName(), "");
		ps.createPropertyEntry(DeployConstants.DBI_VENDOR, batchItemId
				.getSduId().getVendor(), "");
		ps.createPropertyEntry(DeployConstants.DBI_BATCHITEMID_COUNT,
				new Integer(batchItemId.getIdCount()), "");
	}

	static void setBatchItemId(Configuration cfg, BatchItemId batchItemId)
			throws NameNotFoundException, NoWriteAccessException,
			InvalidValueException, ConfigurationException {
		cfg.addConfigEntry(DeployConstants.DBI_NAME, batchItemId.getSduId()
				.getName());
		cfg.addConfigEntry(DeployConstants.DBI_VENDOR, batchItemId.getSduId()
				.getVendor());
		// do not store the batchItemId.getIdCount() here
	}

	// /////////////////////
	// SET //
	// /////////////////////

	// /////////////////////
	// GET //
	// /////////////////////
	static BatchItemId getBatchItemId(PropertySheet ps, boolean isSda)
			throws NameNotFoundException, ConfigurationException {
		final String name = (String) getPropertyEntry(ps,
				DeployConstants.DBI_NAME);
		final String vendor = (String) getPropertyEntry(ps,
				DeployConstants.DBI_VENDOR);
		final int idCount = ((Integer) getPropertyEntry(ps,
				DeployConstants.DBI_BATCHITEMID_COUNT)).intValue();

		return createBatchItemId(isSda, name, vendor, idCount);
	}

	static BatchItemId getBatchItemId(Configuration cfg, boolean isSda,
			int idCount) throws ConfigurationException {
		final String name = getConfigEntry(cfg, DeployConstants.DBI_NAME);
		final String vendor = getConfigEntry(cfg, DeployConstants.DBI_VENDOR);
		final BatchItemId biId = createBatchItemId(isSda, name, vendor);
		biId.setIdCount(idCount);

		return biId;
	}

	// /////////////////////
	// GET //
	// /////////////////////

	static BatchItemId createBatchItemId(SduId sduId) {
		return DeployFactory.getInstance().createBatchItemId(sduId);
	}

	// PRIVATE METHODS
	private static String getConfigEntry(Configuration cfg, String configName)
			throws NameNotFoundException, InconsistentReadException,
			ConfigurationException {
		return (String) cfg.getConfigEntry(configName);
	}

	private static Object getPropertyEntry(PropertySheet ps, String propName)
			throws NameNotFoundException, ConfigurationException {
		return ps.getPropertyEntry(propName).getDefault();
	}

	private static BatchItemId createBatchItemId(boolean isSda, String name,
			String vendor) {
		return createBatchItemId(isSda, name, vendor, -1);
	}

	private static BatchItemId createBatchItemId(boolean isSda, String name,
			String vendor, int idCount) {
		final SduId sduId = createSduId(isSda, name, vendor);
		final BatchItemId biId = createBatchItemId(sduId);
		biId.setIdCount(idCount);
		return biId;
	}

	private static SduId createSduId(boolean isSda, String name, String vendor) {
		if (isSda) {
			return createSdaId(name, vendor);
		} else {
			return createScaId(name, vendor);
		}
	}

	private static SdaId createSdaId(String name, String vendor) {
		return RepositoryComponentsFactory.getInstance().createSdaId(name,
				vendor);
	}

	private static ScaId createScaId(String name, String vendor) {
		return RepositoryComponentsFactory.getInstance().createScaId(name,
				vendor);
	}
}
