/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.ear.common.CloneUtils;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DSCloneUtils extends CloneUtils {

	private DSCloneUtils() {
	}

	public static ReferenceObject[] clone(ReferenceObject[] source)
			throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		final ReferenceObject[] result = new ReferenceObject[source.length];
		for (int i = 0; i < source.length; i++) {
			result[i] = (ReferenceObject) source[i].clone();
		}
		return result;
	}

	/**
	 * 
	 * @param source
	 *            <code>Hashtable</code> the key is String, the value is
	 *            ContainerData
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public static Hashtable<String, ContainerData> clone(
			Hashtable<String, ContainerData> source)
			throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		final Hashtable<String, ContainerData> result = new Hashtable<String, ContainerData>();

		final Enumeration cNames = source.keys();
		String cName = null;
		ContainerData cData = null;
		while (cNames.hasMoreElements()) {
			cName = (String) cNames.nextElement();
			cData = source.get(cName);
			result.put(clone(cName), (ContainerData) cData.clone());
		}

		return result;
	}

	public static Set cloneObject(Collection source)
			throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		final Set<String> result;
		if (source instanceof LinkedHashSet) {
			result = new LinkedHashSet();
		} else {
			result = new HashSet();
		}

		cloneObject(source, result);
		return result;
	}

	public static Set cloneObject(Set source) throws CloneNotSupportedException {
		if (source == null) {
			return null;
		}
		final Set<String> result;
		if (source instanceof LinkedHashSet) {
			result = new LinkedHashSet();
		} else {
			result = new HashSet();
		}

		cloneObject(source, result);
		return result;
	}

	// Supported types are <code>String</code>, <code>DeployedComponent</code>
	// and <code>ResourceReference</code>.
	private static void cloneObject(Collection source, Set result)
			throws CloneNotSupportedException {
		final Iterator iter = source.iterator();
		Object tmp = null;
		while (iter.hasNext()) {
			tmp = iter.next();
			if (tmp instanceof String) {
				tmp = CloneUtils.clone((String) tmp);
			} else if (tmp instanceof Resource) {
				Resource res = (Resource) tmp;
				tmp = new Resource(res.getName(), res.getType(), res
						.getAccessType());
			} else if (tmp instanceof ResourceReference) {
				tmp = ((ResourceReference) tmp).clone();
			} else {
				throw new CloneNotSupportedException(
						"ASJ.dpl_ds.006097 This program point must never be reached.");
			}
			result.add(tmp);
		}
	}

}
