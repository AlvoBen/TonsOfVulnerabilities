/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.lib.xml.parser.tokenizer;

import java.io.IOException;

/**
 * 
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 1.0
 */
public interface AttributeHandler {

  public int handleAttributes(String[] qnames, String[] values, String[] uris, int count, String elementName, String elementUri,XMLTokenWriter writer) throws IOException;
  
}
