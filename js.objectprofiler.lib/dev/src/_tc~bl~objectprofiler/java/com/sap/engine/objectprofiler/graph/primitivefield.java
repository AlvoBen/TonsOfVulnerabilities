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

package com.sap.engine.objectprofiler.graph;

import java.io.Serializable;

/**
 * @author Georgi Stanev, Mladen Droshev
 * @version 7.10C
 */
public class PrimitiveField implements Serializable{

  private String name = null;
  private String type = null;
  private String value = null;

  static final long serialVersionUID = 4655231511733348082L;
  
  public PrimitiveField(String name, String type, String value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String getName() {
    return name;
  }


  public String getType() {
    return type;
  }


  public String getValue() {
    return value;
  }

  public String getInfo(){
    if (value != null) {
      return "Field: " + type + " " + name + " = " + value;
    }
    return "Field: " + type + " " + name;
  }


}
