 /*
 * @(#)MEJBHome.java 7.0 2004-12-2
 * 
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.mejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.management.j2ee.ManagementHome;

 /**
 * 
 * @version 7.0 - 2004-12-2
 * @author Nikolai Angelov
 */
public interface MEJBHome extends ManagementHome {
//  public Management create() throws CreateException, RemoteException;
  public MEJB createMEJB() throws CreateException, RemoteException;
}
