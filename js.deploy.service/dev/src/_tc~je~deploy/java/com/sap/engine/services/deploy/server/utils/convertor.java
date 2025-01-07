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
package com.sap.engine.services.deploy.server.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.lib.io.hash.Entry;

import com.sap.engine.services.deploy.container.util.CAConvertor;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class Convertor extends CAConvertor {

	public static Set<String> cObject2String(Entry[] source) {
		if (source == null) {
			return null;
		}
		final Set<String> result = new LinkedHashSet<String>();
		for (int i = 0; i < source.length; i++) {
			result.add(source[i].getName());
		}
		return result;
	}

	public static Set<String> cObject2String(Object[] source) {
		if (source == null) {
			return null;
		}
		final Set<String> result = new LinkedHashSet<String>();
		for (int i = 0; i < source.length; i++) {
			result.add(source[i].toString());
		}
		return result;
	}

}
