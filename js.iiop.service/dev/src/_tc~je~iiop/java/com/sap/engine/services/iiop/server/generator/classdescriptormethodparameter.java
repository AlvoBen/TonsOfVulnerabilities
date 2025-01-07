/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server.generator;

import java.io.Serializable;

/*
 * Public class ClassDescriptorMethodParameter. The information class representing a Descriptor
 * XML file.  A helper class.
 * refer Chapter 10.6 of J2EE Connector Architecture 1.0
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov, Mladen Droshev
 * @version 4.0
 */
public class ClassDescriptorMethodParameter implements Serializable {

  static final long serialVersionUID = 2152631690099870610L;

  //  private boolean isRemote;
  private String type;
  private String isRemote;
  private String isInterface;
  private ClassDescriptorMethod method[];
  private String toWriteAsObject = null;
  private String forStubName = null;

  /*
   * Parameter type field accessors
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getToWriteAsObject(){
    return this.toWriteAsObject;
  }

  public void setToWriteAsObject(String toWriteAsObject){
    this.toWriteAsObject = toWriteAsObject;
  }

  /*
   * A flag if the parameter's class is assignable
   * from java.rmi.Remote
   */
  public String isRemote() {
    return isRemote;
  }

  //  public void setRemote(boolean isRemote) {
  //    
  //    this.isRemote = isRemote;
  //  }  
  public void setRemote(String isRemote) {
    this.isRemote = isRemote;
  }

  public String getForStubName(){
    return this.forStubName;
  }

  public void setForStubName(String forStubName){
    this.forStubName = forStubName;
  }

  /*
   * A flag if the method is abstract
   */
  public String isInterface() {
    return isInterface;
  }

  /*
   *
   */
  public void setInterface(String isInterface) {
    this.isInterface = isInterface;
  }

  /*
   * Method field accessors
   */
  public ClassDescriptorMethod[] getMethod() {
    return method;
  }

  public void setMethod(ClassDescriptorMethod[] method) {
    this.method = method;
  }

  public ClassDescriptorMethod getMethod(int i) {
    if (method != null && method.length > i && i >= 0) {
      return method[i];
    } else {
      return null;
    }
  }

  public void setMethod(int i, ClassDescriptorMethod entry) {
    if (method == null || i >= method.length) {
      ClassDescriptorMethod[] newObjects = new ClassDescriptorMethod[i + 1];

      if (method != null) {
        for (int j = 0; j < method.length; j++) {
          newObjects[j] = method[j];
        } 
      }

      method = newObjects;
    }

    method[i] = entry;
  }

  public void setMethod(ClassDescriptorMethod entry) {
    int length = 0;
    ClassDescriptorMethod[] newObjects = null;

    if (method == null) {
      newObjects = new ClassDescriptorMethod[1];
    } else {
      length = method.length;
      newObjects = new ClassDescriptorMethod[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(method[i])) {
          method[i] = entry;
          return;
        } else {
          newObjects[i] = method[i];
        }
      } 
    }

    newObjects[length] = entry;
    method = newObjects;
  }

  public void removeMethod(ClassDescriptorMethod entry) {
    if (entry == null) {
      return;
    }

    if (method != null && method.length > 0) {
      ClassDescriptorMethod[] newObjects = new ClassDescriptorMethod[method.length - 1];
      int index = 0;

      for (int j = 0; j < method.length; j++) {
        if (method[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = method[j];
        }
      } 

      method = newObjects;
    }
  }

}

