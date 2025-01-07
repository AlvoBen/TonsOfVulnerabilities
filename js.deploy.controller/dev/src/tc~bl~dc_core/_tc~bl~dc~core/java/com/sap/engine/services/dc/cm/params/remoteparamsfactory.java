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
package com.sap.engine.services.dc.cm.params;

import java.rmi.Remote;
import java.util.Map;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public interface RemoteParamsFactory extends Remote {

	public Param createParam(String name, String value)
			throws RemoteParamsFactoryException;

	/**
	 * Returns created <code>Param</code>s
	 * 
	 * @param nameValulePairs
	 *            <code>Map</code>, where the key is param name as
	 *            <code>String</code> and value is param value as
	 *            <code>String</code>.
	 * @return <code>Param</code>[]
	 * @throws RemoteParamsFactoryException
	 *             in case the <code>Param</code>s cannot be created.
	 */
	public Param[] createParams(Map nameValulePairs)
			throws RemoteParamsFactoryException;

	public ParamManager createParamManager()
			throws RemoteParamsFactoryException;

}
