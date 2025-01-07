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

import java.util.Vector;

/**
 * Public class ExceptionHandler is used to reorder the
 * exceptions thrown by a clss' methods
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class ExceptionHandler {

  private Class[] exceps;
  private Vector tree;

  public ExceptionHandler() {
    //     exceps = null;
    tree = new Vector();
  }

  private Vector parents(Class exc) {
    Vector parents = new Vector();
    parents.addElement(new BoolExc(exc, true));

    for (; !(exc.getName().equals("java.lang.Throwable"));) {
      exc = exc.getSuperclass();
      parents.addElement(new BoolExc(exc, false));
    } 

    return parents;
  }

  private void parentTable(Vector parents) {
    for (int i = parents.size() - 1; i >= 0; i--) {
      int j = parents.size() - 1 - i;

      if (tree.size() - 1 < j) {
        tree.addElement(new Vector());
      }

      boolean flag = false;

      for (int k = 0; k < ((Vector) tree.get(j)).size(); k++) {
        if (((BoolExc) ((Vector) tree.get(j)).get(k)).exc.getName().equals(((BoolExc) parents.get(i)).exc.getName()) && ((BoolExc) ((Vector) tree.get(j)).get(k)).child) {
          flag = true;
        }
      } 

      if (!flag) {
        ((Vector) tree.get(j)).addElement(parents.get(i));
      }
    } 
  }

  public Class[] handler(Class[] exceps) {
    tree = new Vector();

    for (int i = 0; i < exceps.length; i++) {
      if (RuntimeException.class.isAssignableFrom(exceps[i])) {
        continue;
      }

      Vector v = parents(exceps[i]);
      parentTable(v);
    } 

    Vector ret = new Vector();
    int k = 0;

    for (int i = tree.size() - 1; i > -1; i--) {
      for (int j = 0; j < ((Vector) tree.get(i)).size(); j++) {
        if (((BoolExc) ((Vector) tree.get(i)).get(j)).child) {
          ret.addElement(((BoolExc) ((Vector) tree.get(i)).get(j)).exc);
        }
      } 
    } 

    Class[] arr = new Class[ret.size()];

    for (int r = 0; r < ret.size(); r++) {
      arr[r] = (Class) ret.get(r);
    } 

    return arr;
  }

  private class BoolExc {

    Class exc;
    boolean child;

    BoolExc(Class ex, boolean ch) {
      exc = ex;
      child = ch;
    }

  }

}

