package com.sap.engine.objectprofiler.controller.impl;

import com.sap.engine.objectprofiler.controller.GraphFilter;
import com.sap.engine.objectprofiler.graph.Node;

import java.util.ArrayList;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-7-7
 * Time: 16:55:36
 */
public class ClassesFilter implements GraphFilter {
  private ArrayList classNames = new ArrayList();

  public ClassesFilter() {
  }

  public ClassesFilter(String className) {
    addFilter(className);
  }

  public ClassesFilter(String[] classNames) {
    addFilters(classNames);
  }

  public void addFilter(String className) {
    if (!classNames.contains(className)) {
      classNames.add(className);
    }
  }

  public void addFilters(String[] className) {
    for (int i=0;i<className.length;i++) {
      addFilter(className[i]);
    }
  }

  public void removeFilter(String className) {
    classNames.remove(className);
  }

  public void removeFilters(String[] className) {
    for (int i=0;i<className.length;i++) {
      removeFilter(className[i]);
    }
  }

  public void removeAllFilters() {
    classNames.clear();
  }

  public String[] getFilters() {
    return (String[])classNames.toArray(new String[classNames.size()]);
  }

  public boolean filter(Object obj) {
    if (obj == null) {
      return false;
    }


    String className = Node.formGenericType(obj.getClass());

    return filterByDescription(className);
  }

  public boolean filterByDescription(String desc) {
    boolean res = false;

    if (desc == null) {
      return false;
    }
    //System.out.println(" DESC="+desc);
    for (int i=0;i<classNames.size();i++) {
     String classNamePattern = (String)classNames.get(i);
     //System.out.println("  PATTERN = "+classNamePattern);
      if (desc.equals(classNamePattern) ||
          (classNamePattern.endsWith("*")) &&
           desc.startsWith(classNamePattern.substring(0,classNamePattern.length()-1))) {
        return true;
      }
    }

    return res;
  }

}
