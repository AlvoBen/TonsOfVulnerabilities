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
public class Key implements Serializable{
  private int hashID = 0;
  private transient Object obj = null;

  static final long serialVersionUID = -8590963714794827258L;

  public Key(Object obj) {
    this.obj = obj;
    this.hashID = System.identityHashCode(this.obj);

  }

  public boolean equals(Object _obj) {
    if (this.obj == ((Key) _obj).obj) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    return hashID;
  }

  public Object getObj(){
    return this.obj;
  }
}
