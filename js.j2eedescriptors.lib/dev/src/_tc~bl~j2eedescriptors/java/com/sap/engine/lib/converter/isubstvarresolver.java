/*
 * Copyright (c) 2005 by SAP AG, Walldorf. http://www.sap.com All rights
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

import java.io.InputStream;

/**
 * @author denitsa-e
 */
public interface ISubstVarResolver {
  /**
   * Resolves all occurences of substitution variables ( ${VAR_NAME} ) with
   * their values.
   * 
   * @param paramString
   *          raw String with variables to be replaced.
   * @return a new String with all variables replaced with their values.
   * @throws DescriptorParseException
   *           if any variable value can't be found or an IOException occurs.
   */
  public String substituteParamString(String paramString)
      throws DescriptorParseException;

  /**
   * Resolves all occurences of substitution variables ( ${VAR_NAME} ) with
   * their values.
   * 
   * @param paramStream
   *          raw InputStream with variables to be replaced. Will be closed on
   *          return.
   * @return a new InputStream with all variables replaced with their values.
   * @throws DescriptorParseException
   *           if any variable value can't be found or an IOException occurs.
   */
  public InputStream substituteParamStream(InputStream paramStream)
      throws DescriptorParseException;
}