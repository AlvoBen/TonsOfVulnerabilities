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
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.*;

import java.util.Vector;

/**
 * @author Georgy Stanev
 * @version 4.0
 */
public class ContextImpl extends Context {

  private String name = "";
  private Context parentContext = null;
  private Vector data = new Vector();
  private org.omg.CORBA.ORB orb;
  private String star = "*";

  public ContextImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
  }

  public ContextImpl(org.omg.CORBA.ORB orb0, Context theParent, String theName) {
    this(orb0);
    parentContext = theParent;
    name = theName;
  }

  public String context_name() {
    return name;
  }

  public Context parent() {
    return parentContext;
  }

  public Context create_child(String child_ctx_name) {
    return new ContextImpl(orb, this, child_ctx_name);
  }

  public void set_one_value(String propname, Any propvalue) {
    data.add(new NamedValueImpl(orb, propname, propvalue, 0));
  }

  public void set_values(NVList values) {
    try {
      for (int i = 0; i < values.count(); i++) {
        data.add(values.item(i));
      }
    } catch (Bounds b) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextImpl.set_values(NVList)", LoggerConfigurator.exceptionTrace(b));
      }
    }
  }

  public void delete_values(String propname) {
    boolean found = false;

    synchronized (data) {
      for (int i = 0; i < data.size(); i++) {
        if (match(((NamedValue) data.elementAt(i)).name(), propname)) {
          found = true;
          data.removeElementAt(i--);
        }
      }
    }

    if (!found) {
      String messageWithId = "ID019026: Context.delete_values(): name not found";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextImpl.delete_values(String)", messageWithId);
      }
      throw new RuntimeException(messageWithId);
    }
  }

  public NVList get_values(String start_scope, int op_flags, String pattern) {
    NVList result = new NVListImpl(orb);
    boolean found = false;

    if ((start_scope != null) && (!start_scope.equals(name)) && (parentContext == null)) {
      String messageWithId = "ID019027: Invalid scope name";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextImpl.get_values(String, int, String)", messageWithId);
      }
      throw new Error(messageWithId);
    }

    if ((start_scope != null) && (!start_scope.equals(name)) && (parentContext != null)) {
      return parentContext.get_values(start_scope, op_flags, pattern);
    }

    if ((start_scope == null) || (start_scope.equals(name))) {
      for (int i = 0; i < data.size(); i++) {
        if (match(((NamedValue) data.elementAt(i)).name(), pattern)) {
          found = true;
          NamedValue nv = (NamedValue) data.elementAt(i);
          result.add_value(nv.name(), nv.value(), 0);
        }
      }
    }

    if (found) {
      return result;
    }

    if ((parentContext != null) && (op_flags != CTX_RESTRICT_SCOPE.value)) {
      return parentContext.get_values(start_scope, op_flags, pattern);
    }
    String messageWithId = "ID019028: Property not found";
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ContextImpl.get_values(String, int, String)", messageWithId);
    }
    throw new Error(messageWithId);
  }

  /////////////////////////////////// match() method ////////////////////
  String cutWildcards(String in) {
    String s = in;

    while (s.indexOf("**") != -1) {
      int i = s.indexOf("**");
      String s1 = s.substring(0, i + 1);
      String s2 = s.substring(i + 2);
      s = s1 + s2;
    }

    return s;
  }

  String[] parsePattern(String thePattern) {
    String pattern = thePattern;
    String[] arr = new String[10];
    int i = 0;

    while (pattern.indexOf(star) != -1) { // "*"
      arr[i] = pattern.substring(0, pattern.indexOf(star)); // "*"
      pattern = pattern.substring(pattern.indexOf(star) + 1);
      i++;

      if (i == arr.length) {
        String[] temp = new String[arr.length + 5];
        System.arraycopy(arr, 0, temp, 0, arr.length);
        arr = temp;
      }
    }

    arr[i] = pattern;
    String[] temp = new String[i + 1];
    System.arraycopy(arr, 0, temp, 0, i + 1);
    return temp;
  }

  boolean match(String theName, String thePattern) {
    boolean randomStart = false;
    boolean randomEnd = false;
    String name = theName;
    String pattern = thePattern;

    if (pattern.indexOf(star) == -1) { // "*"
      return (name.equals(pattern));
    }

    pattern = cutWildcards(pattern);

    if (pattern.startsWith(star)) {
      randomStart = true;
      pattern = pattern.substring(1);
    }

    if (pattern.endsWith(star)) { // "*"
      randomEnd = true;
      pattern = pattern.substring(0, pattern.length() - 1);
    }

    String[] arr = parsePattern(pattern);

    if ((!randomStart) && (!name.startsWith(arr[0]))) {
      return false;
    }

    if ((!randomEnd) && (!name.endsWith(arr[arr.length - 1]))) {
      return false;
    }

    for (int i = 0; i < arr.length; i++) {
      if (name.indexOf(arr[i]) == -1) {
        return false;
      }

      name = name.substring(name.indexOf(arr[i]) + arr[i].length());
    }

    return true;
  }

}

