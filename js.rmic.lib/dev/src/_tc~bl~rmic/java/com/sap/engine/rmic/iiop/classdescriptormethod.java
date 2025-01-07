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
 * Public class ClassDescriptorMethod. The information class representing a Descriptor
 * XML file. A helper class.
 * refer Chapter 10.6 of SAP Java EE Application Server Connector Architecture 1.0
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov, Mladen Droshev
 * @version 4.0
 */
public class ClassDescriptorMethod implements Serializable {

  static final long serialVersionUID = 9154104841830075623L;

  private String name;
  private String idlName;
  private String accessFlag;
  private String type;
  private String[] attribute;
  private ClassDescriptorMethodParameter[] parameter;
  private String isRemoteType;
  private ClassDescriptorMethodException[] exception;
  private ClassDescriptorReturnType returnType;

  /*
   * Name field accessors
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIDLname() {
    return idlName;
  }

  public void setIDLname(String idlName) {
    this.idlName = idlName;
  }

  /*
   * AccessFlag field accessors
   */
  public String getAccessFlag() {
    return accessFlag;
  }

  public void setAccessFlag(String accessFlag) {
    this.accessFlag = accessFlag;
  }

  /*
   * Type field accessors in case method's return type
   * is assignable from java.rmi.Remote
   */
  public String getRemoteType() {
    return isRemoteType;
  }

  //  public void setRemoteType(boolean isRemoteType) {
  //    
  //    this.isRemoteType = isRemoteType;
  //  }  
  public void setRemoteType(String isRemoteType) {
    this.isRemoteType = isRemoteType;
  }

  /*
   * Type field accessors
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /*
   * Exception field accessors
   */
  public ClassDescriptorMethodException[] getException() {
    return exception;
  }

  public void setException(ClassDescriptorMethodException[] exception) {
    this.exception = exception;
  }

  public ClassDescriptorMethodException getException(int i) {
    if (exception != null && exception.length > i && i >= 0) {
      return exception[i];
    } else {
      return null;
    }
  }

  public void setException(int i, ClassDescriptorMethodException entry) {
    if (exception == null || i >= exception.length) {
      ClassDescriptorMethodException[] newObjects = new ClassDescriptorMethodException[i + 1];

      if (exception != null) {
        for (int j = 0; j < exception.length; j++) {
          newObjects[j] = exception[j];
        } 
      }

      exception = newObjects;
    }

    exception[i] = entry;
  }

  public void setException(ClassDescriptorMethodException entry) {
    int length = 0;
    ClassDescriptorMethodException[] newObjects = null;

    if (exception == null) {
      newObjects = new ClassDescriptorMethodException[1];
    } else {
      length = exception.length;
      newObjects = new ClassDescriptorMethodException[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(exception[i])) {
          exception[i] = entry;
          return;
        } else {
          newObjects[i] = exception[i];
        }
      } 
    }

    newObjects[length] = entry;
    exception = newObjects;
  }

  public void removeException(ClassDescriptorMethodException entry) {
    if (entry == null) {
      return;
    }

    if (exception != null && exception.length > 0) {
      ClassDescriptorMethodException[] newObjects = new ClassDescriptorMethodException[exception.length - 1];
      int index = 0;

      for (int j = 0; j < exception.length; j++) {
        if (exception[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = exception[j];
        }
      } 

      exception = newObjects;
    }
  }

  /*
   * Attribute field accessors
   */
  public String[] getAttribute() {
    return attribute;
  }

  public void setAttribute(String[] attribute) {
    this.attribute = attribute;
  }

  public String getAttribute(int i) {
    if (attribute != null && attribute.length > i && i >= 0) {
      return attribute[i];
    } else {
      return null;
    }
  }

  public void setAttribute(int i, String entry) {
    if (attribute == null || i >= attribute.length) {
      String[] newObjects = new String[i + 1];

      if (attribute != null) {
        for (int j = 0; j < attribute.length; j++) {
          newObjects[j] = attribute[j];
        } 
      }

      attribute = newObjects;
    }

    attribute[i] = entry;
  }

  public void setAttribute(String entry) {
    int length = 0;
    String[] newObjects = null;

    if (attribute == null) {
      newObjects = new String[1];
    } else {
      length = attribute.length;
      newObjects = new String[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(attribute[i])) {
          attribute[i] = entry;
          return;
        } else {
          newObjects[i] = attribute[i];
        }
      } 
    }

    newObjects[length] = entry;
    attribute = newObjects;
  }

  public void removeAttribute(String entry) {
    if (entry == null) {
      return;
    }

    if (attribute != null && attribute.length > 0) {
      String[] newObjects = new String[attribute.length - 1];
      int index = 0;

      for (int j = 0; j < attribute.length; j++) {
        if (attribute[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = attribute[j];
        }
      } 

      attribute = newObjects;
    }
  }

  /*
   * Parameter field accessors
   */
  public ClassDescriptorMethodParameter[] getParameter() {
    return parameter;
  }

  public void setParameter(ClassDescriptorMethodParameter[] parameter) {
    this.parameter = parameter;
  }

  public ClassDescriptorMethodParameter getParameter(int i) {
    if (parameter != null && parameter.length > i && i >= 0) {
      return parameter[i];
    } else {
      return null;
    }
  }

  public void setParameter(int i, ClassDescriptorMethodParameter entry) {
    if (parameter == null || i >= parameter.length) {
      ClassDescriptorMethodParameter[] newObjects = new ClassDescriptorMethodParameter[i + 1];

      if (parameter != null) {
        for (int j = 0; j < parameter.length; j++) {
          newObjects[j] = parameter[j];
        } 
      }

      parameter = newObjects;
    }

    parameter[i] = entry;
  }

  public void setParameter(ClassDescriptorMethodParameter entry) {
    int length = 0;
    ClassDescriptorMethodParameter[] newObjects = null;

    if (parameter == null) {
      newObjects = new ClassDescriptorMethodParameter[1];
    } else {
      length = parameter.length;
      newObjects = new ClassDescriptorMethodParameter[length + 1];

      for (int i = 0; i < length; i++) {
        if (entry.equals(parameter[i])) {
          parameter[i] = entry;
          return;
        } else {
          newObjects[i] = parameter[i];
        }
      } 
    }

    newObjects[length] = entry;
    parameter = newObjects;
  }

  public void removeParameter(ClassDescriptorMethodParameter entry) {
    if (entry == null) {
      return;
    }

    if (parameter != null && parameter.length > 0) {
      ClassDescriptorMethodParameter[] newObjects = new ClassDescriptorMethodParameter[parameter.length - 1];
      int index = 0;

      for (int j = 0; j < parameter.length; j++) {
        if (parameter[j].equals(entry)) {
          continue;
        } else {
          newObjects[index++] = parameter[j];
        }
      } 

      parameter = newObjects;
    }
  }

  public ClassDescriptorReturnType getReturnType() {
    return this.returnType;
  }

  public void setReturnType(ClassDescriptorReturnType returnType) {
    this.returnType = returnType;
  }

}

