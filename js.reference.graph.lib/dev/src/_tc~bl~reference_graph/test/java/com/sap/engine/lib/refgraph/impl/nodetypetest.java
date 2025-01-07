/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.lib.refgraph.impl.ComponentNodeHandler;
import com.sap.engine.lib.refgraph.impl.ReferencePrinterHandler;


/**
 *@author Elena Yaneva
 */
public class NodeTypeTest  extends TestCase{
  
  ComponentNodeHandler.NodeType nodeType = ComponentNodeHandler.NodeType.APPLICATION;
  
  public void testToString() {
    
    Assert.assertEquals("application",nodeType.toString());
    nodeType = ComponentNodeHandler.NodeType.INTERFACE;
    Assert.assertEquals("interface",nodeType.toString());
    nodeType = ComponentNodeHandler.NodeType.LIBRARY;
    Assert.assertEquals("library",nodeType.toString());
    nodeType = ComponentNodeHandler.NodeType.SERVICE;
    Assert.assertEquals("service",nodeType.toString());
  }
}
