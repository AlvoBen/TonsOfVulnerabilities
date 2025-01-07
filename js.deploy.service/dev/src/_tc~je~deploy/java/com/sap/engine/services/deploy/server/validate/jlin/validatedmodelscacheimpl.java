/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.deploy.container.ValidatedModelsCache;

/**
 *@author Luchesar Cekov
 */
@SuppressWarnings("deprecation")
public class ValidatedModelsCacheImpl implements ValidatedModelsCache {
	private Map<String, Map<String, Map<String, Object>>> containers2files2models = 
		new HashMap<String, Map<String, Map<String, Object>>>();

	/**
	 * Add validated model object map for the given container and file.
	 * @param containerName the name of the related container.
	 * @param absoluteFilePath
	 * @param moMap model object map to be added.
	 */
	public void addValidatedModelObjectMap(final String containerName,
		final String absoluteFilePath, final Map<String, Object> moMap) {
		initMap(containerName).put(absoluteFilePath, moMap);
	}

	private Map<String, Map<String, Object>> initMap(String containerName) {
		Map<String, Map<String, Object>> result = 
			containers2files2models.get(containerName);
		if (result == null) {
			result = new HashMap<String, Map<String, Object>>();
			containers2files2models.put(containerName, result);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.container.ValidatedModelsCache
	 * 		#getModelObjectsMap(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> getModelObjectsMap(
		final String containerName, final String filePath) {
		if (!containers2files2models.containsKey(containerName)) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006112 There is no model object maps for container \""
							+ containerName + "\"");
		}
		return containers2files2models.get(containerName).get(filePath);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.container.ValidatedModelsCache
	 * 		#getFiles2ModelObjectMaps(java.lang.String)
	 */
	public Map<String, Map<String, Object>> getFiles2ModelObjectMaps(
		final String containerName) {
		return containers2files2models.get(containerName);
	}
}
