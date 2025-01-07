/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import com.sap.mw.jco.IRepository;

/**
 * @author Silvia Petrova
 *
 *
 */
public interface RFCRepositoryInterface {
	
	/**
	 * Register a repository
	 *
	 * @param  name  Program Id of the bundle
	 * @param  repository Repository to be used for this bundle
	 */
	public void registerRepository(String name, IRepository repository);
	
	/**
	 * Unregister a repository
	 *
	 * @param  name  Program Id of the bundle
	 *
	 */
	public void unregisterRepository(String name);
	
	/**
	 * Get the repository
	 *
	 * @param  name  Program Id of the bundle
	 * @return IRepository the repository to be used. 
	 */
	public IRepository getRepository(String name);

}
