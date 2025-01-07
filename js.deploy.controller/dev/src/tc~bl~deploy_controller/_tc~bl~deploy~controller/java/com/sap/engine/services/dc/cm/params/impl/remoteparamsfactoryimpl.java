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
package com.sap.engine.services.dc.cm.params.impl;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import com.sap.engine.services.dc.cm.params.AbstractRemoteParamsFactory;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamManager;
import com.sap.engine.services.dc.cm.params.ParamsFactory;
import com.sap.engine.services.dc.cm.params.RemoteParamsFactoryException;

/**
 * Implements <code>RemoteParamsFactory</code> interface.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class RemoteParamsFactoryImpl extends AbstractRemoteParamsFactory {

	public RemoteParamsFactoryImpl() throws RemoteException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.RemoteParamsFactory#createParam(
	 * java.lang.String, java.lang.String)
	 */
	public Param createParam(String name, String value)
			throws RemoteParamsFactoryException {
		return ParamsFactory.getInstance().createParam(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.RemoteParamsFactory#createParams
	 * (java.util.Map)
	 */
	public Param[] createParams(Map nameValulePairs)
			throws RemoteParamsFactoryException {
		return ParamsFactory.getInstance().createParams(nameValulePairs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.RemoteParamsFactory#createParamManager
	 * ()
	 */
	public ParamManager createParamManager()
			throws RemoteParamsFactoryException {
		try {
			return new RemoteParamManagerImpl();
		} catch (RemoteException re) {
			RemoteParamsFactoryException rpfe = new RemoteParamsFactoryException(
					re.getMessage(), re);
			rpfe.setMessageID("ASJ.dpl_dc.003148");
			throw rpfe;
		}
	}

}
