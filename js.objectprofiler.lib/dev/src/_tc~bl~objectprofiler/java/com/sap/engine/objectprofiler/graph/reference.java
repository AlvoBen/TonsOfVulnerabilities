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
public class Reference implements Serializable {
  private String name = null;
  private Node parent = null;
  private Node child = null;
  private boolean isTransient = false;

  static final long serialVersionUID = -4636741075902695786L;

  public Reference(String name, Node parent, Node child, boolean isTransient) {
    this.name = name;
    this.parent = parent;
    this.child = child;
    this.isTransient = isTransient;
  }

  public String getName() {
    return name;
  }

  public Node getParent() {
    return parent;
  }

  public Node getChild() {
    return child;
  }

  public boolean isTransient() {
    return isTransient;
  }

  public String toString() {
    return name + " : " +  parent.getCurrentClassData().getClassName() +
           " -> " +child.getCurrentClassData().getClassName();
  }
}
