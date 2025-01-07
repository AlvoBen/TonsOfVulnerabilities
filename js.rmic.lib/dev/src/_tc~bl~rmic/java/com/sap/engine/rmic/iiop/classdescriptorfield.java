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
 * Public class ClassDescriptorField. Iinformation class representing a Descriptor
 * XML file.  A helper class.
 * refer Chapter 10.6 of SAP Java EE Application Server Connector Architecture 1.0
 *
 * @author Ralitsa Bozhkova, Svetoslav Nedkov
 * @version 4.0
 */
public class ClassDescriptorField implements Serializable {

  static final long serialVersionUID = 1266794413684953762L;

  private String name = null;
  private String accessFlag = null;
  private String type = null;
  private String[] attribute = null;

  /*
   * Name field accessors
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
   * Type field accessors
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

}

