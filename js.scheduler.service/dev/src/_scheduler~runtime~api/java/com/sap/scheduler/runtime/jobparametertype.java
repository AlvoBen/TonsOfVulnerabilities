/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.scheduler.runtime;

import java.io.Serializable;

/**
 * This class described the supported types for job parameters.
 */
public class JobParameterType implements Serializable
{ 
  static final long serialVersionUID = -5751585231965527481L;

  public static final JobParameterType FLOAT = new JobParameterType('F');
  public static final JobParameterType DOUBLE = new JobParameterType('E');
  public static final JobParameterType LONG = new JobParameterType('L');
  public static final JobParameterType INTEGER = new JobParameterType('I');
  public static final JobParameterType DATE = new JobParameterType('D');
  public static final JobParameterType STRING = new JobParameterType('V');
  public static final JobParameterType BOOLEAN = new JobParameterType('B');
  public static final JobParameterType PROPERTIES = new JobParameterType('P');

  
  public static JobParameterType valueOf(String name) {
    
    if ("string".equalsIgnoreCase(name)) {
        return STRING;
    } else if ("float".equalsIgnoreCase(name)) {
        return FLOAT;
    } else if ("double".equalsIgnoreCase(name)) {
        return DOUBLE;
    } else if ("date".equalsIgnoreCase(name)) {
        return DATE; 
    } else if ("long".equalsIgnoreCase(name)) {
        return LONG;
    } else if ("boolean".equalsIgnoreCase(name)) {
        return BOOLEAN;
    } else if ("integer".equalsIgnoreCase(name)) {
        return INTEGER;
    } else if ("properties".equalsIgnoreCase(name)) {
        return PROPERTIES;
    }
    
    throw new IllegalArgumentException("Parameter type \"" + name + "\" not supported.");
  }
  
  private final char parameterType;

  private JobParameterType(char c)
  {
    parameterType = c;
  }
  
  public String toString()
  {
    switch (parameterType)
    {
      case 'V': return "string";
      case 'F': return "float";
      case 'E': return "double";
      case 'D': return "date";
      case 'L': return "long";
      case 'B': return "boolean";
      case 'I': return "integer";
      case 'P': return "properties";
      default: throw new IllegalArgumentException("Unknown type " + parameterType);
    }
  }
  
  public int hashCode() {
      return new Character(parameterType).hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj==null || !(obj instanceof JobParameterType)) {
      return false;  
    }
    return parameterType == ((JobParameterType)obj).parameterType;
  }
}
