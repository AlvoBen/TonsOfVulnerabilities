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

import com.sap.engine.lib.refgraph.CyclicReferencesException;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Elena Yaneva
 */
public class ReferenceCycleCheckerHandlerTest extends TestCase {

  ReferenceCycleCheckerHandler<String> handler = new ReferenceCycleCheckerHandler<String>();

  private String[] nodes = new String[] { "node0", "node1", "node2", "node3", "node4", "node5", "node6", "node7",
                                         "node8", "node9", "node10", "node11", "node12", "node13", "node14", "node15",
                                         "node16" };

  /*
   * 0 / \ 4 1 / / \ 5 2 3 / ^ 6 | / | 7---------- / \ 8 -->9
   */

  public void testCycle() {

    try {
      handler.setOnCycleStop(true);
      Assert.assertTrue(handler.isOnCycleStop());
      handler.cycle(nodes[0], null, true);
      Assert
                          .fail("There should be CyclicReferenceException when the cycle method of ReferenceCycleCheckHandler is invoked");
    } catch (CyclicReferencesException e) {
      Assert.assertTrue(e.getMessage().indexOf(
                          "One or more Cyclic reference detected during building application reference graph") == 0);
      System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
    }

    handler.setOnCycleStop(false);
    handler.startNode(nodes[0], null, true);
    handler.startNode(nodes[1], null, true);
    handler.startNode(nodes[3], null, true);
    handler.startNode(nodes[2], null, true);
    handler.startNode(nodes[4], null, true);
    handler.startNode(nodes[5], null, true);
    handler.startNode(nodes[6], null, true);
    handler.startNode(nodes[7], null, true);
    handler.startNode(nodes[8], null, true);
    handler.startNode(nodes[9], null, true);
    handler.startNode(nodes[10], null, true);
    handler.startNode(nodes[11], null, true);
    handler.startNode(nodes[12], null, true);
    handler.startNode(nodes[13], null, true);
    handler.startNode(nodes[14], null, true);
    handler.startNode(nodes[15], null, true);
    handler.startNode(nodes[0], null, true);
    try {
       for (String node : nodes) {
      handler.cycle(node, null, true);
      }
      Assert .fail("There should be CyclicReferenceException \n when the cycle method of ReferenceCycleCheckHandler is invoked \n  with 15 or more cycles and onCycleStop is set to false is invoked");
    } catch (CyclicReferencesException e) {
      Assert.assertTrue(e.getMessage().indexOf(
                          "One or more Cyclic reference detected during building application reference graph") == 0);
      System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$

    }
    handler.selfCycle(nodes[0], null, true);
  }

  public void testStartNode() {
    boolean addedToVisited;

    Assert.assertTrue(handler.startRoot());
    addedToVisited = handler.startNode(nodes[0], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited = handler.startNode(nodes[0], null, true);
    Assert.assertFalse(addedToVisited);
    addedToVisited = handler.startNode(nodes[1], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited = handler.startNode(nodes[2], null, true);
    Assert.assertTrue(addedToVisited);
    addedToVisited = handler.startNode(nodes[0], null, true);
    Assert.assertFalse(addedToVisited);
    handler.endRoot();
  }

}
