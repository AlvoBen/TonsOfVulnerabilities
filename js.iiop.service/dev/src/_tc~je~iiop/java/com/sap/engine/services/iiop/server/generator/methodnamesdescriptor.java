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
package com.sap.engine.services.iiop.server.generator;

import java.lang.reflect.Method;

/*
 * Public class DescriptorWriter is used for passing
 * the class accessors to ClassDescriptor
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class MethodNamesDescriptor {

  private Method method;
  private String idlName;
  private String attributeName;
  private int attributeKind;
  private int attributePairIndex;

  public MethodNamesDescriptor(Method meth) {
    method = meth;
  }

  public boolean isAttribute() {
    return attributeKind != 0;
  }

  public void setAttributeKind(int start) {
    attributeKind = start;
  }

  public int getAttributeKind() {
    return attributeKind;
  }

  public void setAttributeName(String s) {
    attributeName = s;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public String getIDLname() {
    return idlName;
  }

  public void setIDLname(String iName) {
    idlName = iName;
  }

  public Method getMethod() {
    return method;
  }

  public String getName() {
    return method.getName();
  }

  public Class[] parameters() {
    return method.getParameterTypes();
  }

  public Class returnType() {
    return method.getReturnType();
  }

  public Class[] exceptions() {
    return method.getExceptionTypes();
  }

  public void setAttributePairIndex(int i) {
    attributePairIndex = i;
  }

  public int getAttributePairIndex() {
    return attributePairIndex;
  }

}

