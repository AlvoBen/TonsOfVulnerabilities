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

import java.util.List;

import com.sap.engine.lib.refgraph.CyclicReferencesException;


import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Elena Yaneva
 */
public class TopoSortHandlerTest  extends TestCase {

  TopoSortHandler<String> handler = new TopoSortHandler<String>();
  
   
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
  

 
  public void testGetSortedApplicationNames() {
   Assert.assertTrue( handler.startRoot());
    handler.startNode(nodes[0], null, true);
    handler.startNode(nodes[1], null, true);
    handler.startNode(nodes[3], null, true);
    handler.endNode(nodes[3]);
    handler.startNode(nodes[2], null, true);
    handler.endNode(nodes[2]);
    handler.endNode(nodes[1]);
    handler.startNode(nodes[4], null, true);
    handler.startNode(nodes[5], null, true);
    handler.startNode(nodes[6], null, true);
    handler.startNode(nodes[7], null, true);
    handler.startNode(nodes[2], null, true);
    handler.endNode(nodes[2]);
    handler.startNode(nodes[8], null, true);
    handler.startNode(nodes[9], null, true);
    handler.endNode(nodes[9]);
    handler.endNode(nodes[8]);
    handler.endNode(nodes[7]);
    handler.endNode(nodes[6]);
    handler.endNode(nodes[5]);
    handler.endNode(nodes[4]);
    handler.endNode(nodes[0]);
   handler.endRoot();
    
    List <String> sortedNodes=handler.getSortedComponentsNames();
   Assert.assertEquals(nodes[3], sortedNodes.get(0)) ;
   System.out.println(sortedNodes.get(0));//$JL-SYS_OUT_ERR$
   Assert.assertEquals(nodes[2], sortedNodes.get(1)) ;
   System.out.println(sortedNodes.get(0));//$JL-SYS_OUT_ERR$
   Assert.assertEquals(nodes[1], sortedNodes.get(2)) ;
   Assert.assertEquals(nodes[9], sortedNodes.get(3)) ;
   Assert.assertEquals(nodes[8], sortedNodes.get(4)) ;
   Assert.assertEquals(nodes[7], sortedNodes.get(5)) ;
   Assert.assertEquals(nodes[6], sortedNodes.get(6)) ;
   Assert.assertEquals(nodes[5], sortedNodes.get(7)) ;
   Assert.assertEquals(nodes[4], sortedNodes.get(8)) ;
   Assert.assertEquals(nodes[0], sortedNodes.get(9)) ;
  
  
  }
  
  public void testStartNode() {
    boolean addedToVisited;
    
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
  }
  
  public void testEndNode() {
 
    handler.endNode(nodes[0]);
    handler.endNode(nodes[1]);
    handler.endNode(nodes[0]);
    handler.endNode(nodes[1]);
    handler.endNode(nodes[2]);
    handler.endNode(nodes[3]);
    handler.endNode(nodes[4]);
    handler.endNode(nodes[5]);
    handler.endNode(nodes[6]);
    handler.endNode(nodes[5]);
    
    
    List <String> sortedNodes=handler.getSortedComponentsNames();
    Assert.assertEquals(nodes[0], sortedNodes.get(0)) ;
    Assert.assertEquals(nodes[1], sortedNodes.get(1)) ;
    Assert.assertEquals(nodes[2], sortedNodes.get(2)) ;
    Assert.assertEquals(nodes[3], sortedNodes.get(3)) ;
    Assert.assertEquals(nodes[4], sortedNodes.get(4)) ;
    Assert.assertEquals(nodes[5], sortedNodes.get(5)) ;
    Assert.assertEquals(nodes[6], sortedNodes.get(6)) ;

    Assert.assertEquals(7, sortedNodes.size());
    

  }
  
    
  public void testCycle () {
      try {
      handler.cycle(nodes[0], null, true);
      Assert.fail("There should be CyclicReferenceException when the cycle method of TopoSortHandler is invoked");
      } catch (CyclicReferencesException e) {
     Assert.assertTrue(e.getMessage().indexOf("One or more Cyclic reference detected during building application reference graph") ==0);
    System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
    }
  }
}

