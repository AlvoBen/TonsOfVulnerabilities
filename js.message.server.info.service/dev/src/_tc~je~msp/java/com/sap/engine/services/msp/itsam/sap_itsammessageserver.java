﻿/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.msp.itsam;

import com.sap.engine.services.msp.itsam.compositedata.SAP_ITSAMMessageServerListener;
import com.sap.engine.services.msp.itsam.compositedata.SAP_ITSAMMessageServerParameter;
import com.sap.engine.services.msp.itsam.compositedata.SAP_ITSAMMessageServerSystemInfo;

/**
 * This interface is automatically generated by the CIM Java generator.<br>
 * It represents a message server and provides various information about it.
 * 
 * @author Dimitar Mihaylov (i031671)
 * @version 7.10
 */
public interface SAP_ITSAMMessageServer {
	/**
	 * The MBean name under which a wrapper instance will be registered in the MBean server. 
	 */
	public static final String MBEAN_NAME = SAP_ITSAMMessageServer.class.getName();
	/**
	 * The J2EE type under which a wrapper instance will be registered in the MBean server.
	 */
	public static final String MBEAN_J2EE_TYPE = "ITSAM";
	/**
	 * Returns the hardware id 
	 * @return the hardware id
	 */
	public String getHardwareId();
	/**
	 * Returns the system id
	 * @return the system id
	 */
	public String getSystemId();
	/**
	 * Returns the parameters of the message server
	 * @return the parameters
	 * @see SAP_ITSAMMessageServerParameter
	 */
	public SAP_ITSAMMessageServerParameter[] getParameters();	
	/**
	 * Returns the system info key/value pairs of the message server 
	 * @return an array of system info key/value pairs
	 * @see SAP_ITSAMMessageServerSystemInfo
	 */			 
	public SAP_ITSAMMessageServerSystemInfo[] getSystemInfos();	
	/**
	 * Returns information about the registers listeners in the
	 * message server
	 * @return an array with listeners
	 * @see SAP_ITSAMMessageServerListener
	 */			 
	public SAP_ITSAMMessageServerListener[] getListeners();	
				 	
}