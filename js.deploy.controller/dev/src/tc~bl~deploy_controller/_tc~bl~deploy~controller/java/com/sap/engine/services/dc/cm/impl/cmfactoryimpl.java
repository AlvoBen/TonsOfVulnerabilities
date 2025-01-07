package com.sap.engine.services.dc.cm.impl;

import java.rmi.RemoteException;

import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMFactory;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-1
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class CMFactoryImpl extends CMFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.CMFactory#createComponentManager()
	 */
	public CM createComponentManager() throws RemoteException {
		return new CMImpl();
	}

}
