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
 
 
package com.sap.engine.services.rmi_p4.classload;

import com.sap.engine.services.rmi_p4.exception.P4Logger;


public class ClassLoaderKey {

  private ClassLoader parent;
  private int brokerId;

  public ClassLoaderKey(ClassLoader parent, int brokerId) {
    this.parent = parent;
    this.brokerId = brokerId;
    if(P4Logger.getLocation().beDebug()){
      P4Logger.getLocation().debugT("ClassLoaderKey: parent loader: " + parent + " : brokerId: " + brokerId);
    }
  }

  public int hashCode() {
    return brokerId;
  }

  public ClassLoader getParent() {
    return parent;
  }

  public int getBrokerId() {
    return brokerId;
  }

  /**
   * return true if this object is instance of ClassLoaderKey
   * and these objects have the same brokerId and parent loaders
   * are the same
   * @param obj
   * @return
   */
  public boolean equals(Object obj) {
    if (obj instanceof ClassLoaderKey && this.brokerId == ((ClassLoaderKey)obj).getBrokerId()) {
      if(this.parent != null && ((ClassLoaderKey)obj).getParent() != null){
        return this.parent.equals(((ClassLoaderKey)obj).getParent());
      } else if(this.parent == null && ((ClassLoaderKey)obj).getParent() == null){
        return true;
      }
    }
    return false;
  }

  public String toString(){

    if(brokerId > 0){
      return "BrokerID: " + brokerId + " --> parent: " + parent;
    } else {
      return super.toString();
    }
  }

}
