/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic.iiop;

import java.io.Serializable;

/*
 * Public class ClassDescriptorMethodException. The information class representing a Descriptor
 * XML file.  A helper class.
 * refer Chapter 10.6 of SAP Java EE Application Server Connector Architecture 1.0
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov
 * @version 4.0
 */
public class ClassDescriptorMethodException implements Serializable {

  static final long serialVersionUID = 6260808190246777740L;

  private String name;
  private String IDLRepID;
  private String isRemoteException = null;

  /*
   * Name field accessor
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * IDLRepositoryId
   */
  public String getIDLRepID() {
    return IDLRepID;
  }

  public void setIDLRepID(String IDLRepID) {
    this.IDLRepID = IDLRepID;
  }

  public String isRemoteException() {
    return this.isRemoteException;
  }

  public void setIsRemoteException(String isRemoteException) {
    this.isRemoteException = isRemoteException;
  }

}

