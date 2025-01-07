/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implserver;

/**
 * Abstract class for ServerContextImpl
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public abstract class AbstractServerContextImpl implements ServerContextInface {

    /**
     * Constructor
     *
     * @param remote Flags if remotely used
     * @throws java.rmi.RemoteException Thrown if error occurs.
     */
    public AbstractServerContextImpl(boolean remote) throws java.rmi.RemoteException {

	}

}
