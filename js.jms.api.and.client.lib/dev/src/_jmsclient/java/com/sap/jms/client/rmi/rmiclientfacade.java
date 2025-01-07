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

package com.sap.jms.client.rmi;

import java.rmi.Remote;

import javax.jms.JMSException;
import com.sap.jms.client.connection.ClientFacade;
import com.sap.engine.services.rmi_p4.interfaces.P4Notification;

/**
 * @author Desislav Bantchovski
 * @version 7.30 
 */

public interface RMIClientFacade extends ClientFacade, Remote , P4Notification { 
}
