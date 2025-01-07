/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.injection;

/**
 * This interface serves as an object factory according to the abstract
 * factory design pattern.
 * 
 * Each injection will use a proper implementation of it in order to obtain
 * the proper value which should be injected.
 *
 * @author  Vesselin Mitrov, vesselin.mitrov@sap.com
 * @version 7.10
 */
public interface ObjectFactory {

	/**
	 * Returns an object according to the specific type of this factory
	 * 
	 * @return either a newly created or cached object
	 */
	public Object getObject() throws Exception;
}
