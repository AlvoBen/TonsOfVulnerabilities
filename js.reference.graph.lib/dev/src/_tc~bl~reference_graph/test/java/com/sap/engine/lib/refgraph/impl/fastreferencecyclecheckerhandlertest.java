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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.lib.refgraph.CyclicReferencesException;


/**
 *@author Elena Yaneva
 */
public class FastReferenceCycleCheckerHandlerTest extends TestCase  {
  FastReferenceCycleCheckerHandler<String> handler = new FastReferenceCycleCheckerHandler<String>();
  
  
  private String[] nodes = new String[] { "node0", "node1", "node2", "node3", "node4", "node5", "node6", "node7", "node8", "node9" };
 /*
  *                             0
  *                            / \
  *                          4    1
  *                         /     /  \
  *                       5     2    3
  *                     /       ^
  *                   6         |
  *                  /           |
  *                7----------
  *              /  \
  *           8 -->9
  */
  

  public void testCycle () {
    try {
    handler.cycle(nodes[0], null, true);
    Assert.fail("There should be CyclicReferenceException when the cycle method of TopoSortHandler is invoked");
    } catch (CyclicReferencesException e) {
   Assert.assertTrue(e.getMessage().indexOf("One or more Cyclic reference detected during building application reference graph") ==0);
  System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
  }
    handler.selfCycle(nodes[0], null, true);
}
  
  public void testStartNode() {
    boolean addedToVisited;
    
    handler.startRoot();
    addedToVisited=handler.startNode(nodes[0], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited=handler.startNode(nodes[0], null, true);
    Assert.assertFalse(addedToVisited);
    addedToVisited=handler.startNode(nodes[1], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited=handler.startNode(nodes[2], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited=handler.startNode(nodes[0], null, true);
    Assert.assertFalse(addedToVisited);
    handler.endRoot();
  }

}
