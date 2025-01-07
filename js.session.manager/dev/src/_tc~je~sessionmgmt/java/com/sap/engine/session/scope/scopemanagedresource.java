/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.scope;

import java.util.ArrayList;
import java.util.Set;
import java.io.Serializable;

/*
* Author: i024157 /Georgi Stanev/ 
*/
public abstract class ScopeManagedResource implements Serializable {
  /**
   *  Resource notification when the scope is terminated
   * @param scope terminated scope
   */
  public abstract void scopeTerminated(Scope scope);

  /**
   * Search for convinient Resource Types
   * @param instance Scope instance
   * @param resType searched resources
   * @return ScopeManagedResources[] which are class of resType
   */
  public static ScopeManagedResource[] getResources(Scope instance, Class resType){
    if(instance == null || resType == null){
      return null;
    }
    ArrayList resources = new ArrayList();
    Set res = instance.getResources();
    if (res != null && res.size() > 0) {
      for(Object o: res){
        if(resType.isAssignableFrom(o.getClass())){
          resources.add(o);
        }
      }
      if(resources.size() > 0){
        return (ScopeManagedResource[])resources.toArray(new ScopeManagedResource[0]);
      }
    }
    return null;
  }

}
