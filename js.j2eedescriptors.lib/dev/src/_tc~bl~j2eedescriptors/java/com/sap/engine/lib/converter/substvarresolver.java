/*
 * Copyright (c) 2004 by SAP AG, Walldorf. http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 * 
 * $Id$
 */
package com.sap.engine.lib.converter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Utitility class for resolving substitution variables of the form ${VAR_NAME}
 * to their values in a raw InputStream before XML-parsing it.
 * 
 * @author d037913
 */
public class SubstVarResolver implements ISubstVarResolver {

  private static final String ENCODING = "UTF-8";

  private static final String BEGIN_VAR = "${";

  private static final char END_VAR = '}';

  private Properties substVars;

  /**
   * Constructs a Substitution variable resolver.
   * 
   * @param substitutionVariables
   *          keys and values of all known substitution variables. Keys must be
   *          specified without the begin/end variable tokens "${" and "}".
   */
  public SubstVarResolver(Properties substitutionVariables) {
    if (substitutionVariables == null) {
      throw new IllegalArgumentException("substVars must not be null");
    }
    this.substVars = substitutionVariables;
  }

  /**
   * Resolves all occurences of substitution variables ( ${VAR_NAME} ) with
   * their values found in the Properties used in the constructor.
   * 
   * @param rawStreamWithVariables
   *          raw InputStream with variables to be replaced. Will be closed on
   *          return.
   * @return a new InputStream with all variables replaced with their values.
   * @throws DescriptorParseException
   *           if any variable value can't be found or an IOException occurs.
   */
  public InputStream substituteParamStream(InputStream rawStreamWithVariables)
      throws DescriptorParseException {
    Set unresolvedVars = new HashSet(2);
    byte[] result;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          rawStreamWithVariables, ENCODING));
      String line = null;
      StringBuffer buffer = new StringBuffer();
      while ((line = reader.readLine()) != null) {
        buffer.append(substituteParams(line, unresolvedVars));
        buffer.append('\n');
      }
      checkIfUnresolvedVars(unresolvedVars);
      result = buffer.toString().getBytes(ENCODING);
    } catch (IOException e) {
      throw new DescriptorParseException(e);
    } finally {
      try {
        rawStreamWithVariables.close();
      } catch (IOException e1) {
        throw new DescriptorParseException(e1);
      }
    }
    return new ByteArrayInputStream(result);
  }

  private void checkIfUnresolvedVars(Set unresolvedVars)
      throws DescriptorParseException {
    if (unresolvedVars.size() == 0) {
      return;
    }
    StringBuffer msgBuf = new StringBuffer(
        "The following substitution variable(s) could not be resolved: ");
    int i = 0;
    for (Iterator iter = unresolvedVars.iterator(); iter.hasNext(); i++) {
      String varName = (String) iter.next();
      if (i > 0) {
        msgBuf.append(", ");
      }
      msgBuf.append(BEGIN_VAR);
      msgBuf.append(varName);
      msgBuf.append(END_VAR);
    }
    throw new DescriptorParseException(msgBuf.toString());
  }

  private String substituteParams(String paramValue, Set unresolvedVars) {
    StringBuffer str = new StringBuffer();
    int idx = 0;
    while (idx != -1) {
      idx = paramValue.indexOf(BEGIN_VAR);
      if (idx != -1) {
        str.append(paramValue.substring(0, idx));
        paramValue = paramValue.substring(idx);
        idx = paramValue.indexOf(END_VAR);
        if (idx != -1) {
          String param = paramValue.substring(2, idx);
          str.append(getSubstitutionValue(param, unresolvedVars));
          paramValue = paramValue.substring(idx + 1);
        }
      }
    }
    str.append(paramValue);
    return str.toString();
  }

  private String getSubstitutionValue(String varName, Set unresolvedVars) {
    String value = substVars.getProperty(varName);
    if (value != null) {
      return value;
    }
    unresolvedVars.add(varName);
    return BEGIN_VAR + varName + END_VAR;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.engine.lib.converter.ISubstVarResolver#substituteParamString(java.lang.String)
   */
  public String substituteParamString(String paramString)
      throws DescriptorParseException {
    Set unresolvedVars = new HashSet(2);
    String substitutedString = substituteParams(paramString, unresolvedVars); 
    checkIfUnresolvedVars(unresolvedVars);
    return substitutedString;
  }
}