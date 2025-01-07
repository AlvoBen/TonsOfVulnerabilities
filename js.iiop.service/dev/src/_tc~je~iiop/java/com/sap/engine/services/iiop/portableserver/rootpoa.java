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
 
 
package com.sap.engine.services.iiop.PortableServer;

import org.omg.CORBA.ORB;

public class RootPOA extends POAImpl {

  public static final String ROOT_POA_MANAGER_NAME = "RootPOAManager";
  //reference 11-6;
  
  public RootPOA(ORB orb) {
    super(orb);
    
  }

}
