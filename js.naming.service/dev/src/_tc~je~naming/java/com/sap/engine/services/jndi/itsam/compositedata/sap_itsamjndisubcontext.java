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

package com.sap.engine.services.jndi.itsam.compositedata;

/**
 * This class is automatically generated by the CIM Java code generator.<br>
 *
 * @author Dimitar Mihaylov (i031671)
 * @version 7.10
 */
public class SAP_ITSAMJNDISubcontext {
  private String Name = null;
  private String ShortName = null;

  /**
   *
   *
   */
  public SAP_ITSAMJNDISubcontext() {
  }

  /**
   * @param Name
   * @param ShortName
   */
  public SAP_ITSAMJNDISubcontext(String Name, String ShortName) {
    this.Name = Name;
    this.ShortName = ShortName;
  }

  /**
   * @return
   */
  public String getName() {
    return this.Name;
  }

  /**
   * @return
   */
  public String getShortName() {
    return this.ShortName;
	}	
}
