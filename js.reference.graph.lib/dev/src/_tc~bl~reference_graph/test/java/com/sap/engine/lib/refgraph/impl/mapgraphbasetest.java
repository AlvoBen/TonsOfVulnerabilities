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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeRemoveException;
/**
 * @author Luchesar Cekov
 */
public class MapGraphBaseTest {
  private Edge[] edges;
  private Object[] nodes;

  MapGraphBase<Object> graph = new MapGraphBase<Object>();

  @Before public void setUp() {
    graph.clear();

    nodes = new Object[] { "node0", "node1", "node2", "node3", "node4", "node5", "node6", "node7", "node8", "node9" };

    for (int i = 0; i < nodes.length; i++) {
      graph.add(nodes[i]);
    }
    /*
     * +Application References
    |
    +->+application:node0
    |  |
    |  +->+hard to application:node2
    |  |  |
    |  |  +->+weak to application:node6
    |  |  |
    |  |  +->+hard to application:node5
    |  |
    |  +->+hard to application:node1
    |     |
    |     +->+weak to application:node4
    |     |
    |     +->+hard to application:node3
    |
    |
    +->+application:node7
       |
       +->+weak to application:node9
       |
       +->+hard to application:node8
             |
             +->+hard to application:node2
    
     */
    edges = new Edge[] { new Edge<Object>(nodes[0], nodes[1], Edge.Type.HARD, null),
                        new Edge<Object>(nodes[0], nodes[2], Edge.Type.HARD, null),
                        new Edge<Object>(nodes[1], nodes[3], Edge.Type.HARD, null),
                        new Edge<Object>(nodes[1], nodes[4], Edge.Type.WEAK, null),
                        new Edge<Object>(nodes[2], nodes[5], Edge.Type.HARD, null),
                        new Edge<Object>(nodes[2], nodes[6], Edge.Type.WEAK, null),
                        new Edge<Object>(nodes[7], nodes[8], Edge.Type.HARD, null),
                        new Edge<Object>(nodes[7], nodes[9], Edge.Type.WEAK, null),
                        new Edge<Object>(nodes[8], nodes[2], Edge.Type.WEAK, null), };

    for (int i = 0; i < edges.length; i++) {
      graph.add(edges[i]);
    }
  }

  @Test public void testContainsNode() throws Exception {
    List<Object> sorted =  new Graph<Object>( graph ).sortBackward();
    for (Object node : sorted) {
      assertTrue(graph.containsNode(node));
      graph.remove(node);
    }

    for (int i = 0; i < nodes.length; i++) {
      assertFalse(graph.containsNode(nodes[i]));
    }
  }

  @Test public void testClear() throws Exception {
    graph.clear();
    assertEquals(0, graph.referencesTo.size());
    assertEquals(0, graph.referencesFrom.size());
  }

  @Test public void testAddRemoveNode() throws Exception {
    graph.clear();

    nodes = new Object[] { "node1", "node1.1", "node1.2", "node1.1.1", "node1.1.2", "node1.2.1", "node1.2.2", "node2",
                          "node2.1", "node2.2" };

    for (int i = 0; i < nodes.length; i++) {
      graph.add(nodes[i]);
    }

    assertEquals(nodes.length, graph.referencesTo.size());
    assertEquals(nodes.length, graph.referencesFrom.size());

    for (int i = 0; i < nodes.length; i++) {
      assertTrue(graph.referencesTo.containsKey(nodes[i]));
      assertTrue(graph.referencesFrom.containsKey(nodes[i]));
    }

    for (int i = 0; i < nodes.length; i++) {
      graph.remove(nodes[i]);
    }

    assertEquals(0, graph.referencesTo.size());
    assertEquals(0, graph.referencesFrom.size());

    for (int i = 0; i < nodes.length; i++) {
      assertFalse(graph.referencesTo.containsKey(nodes[i]));
      assertFalse(graph.referencesFrom.containsKey(nodes[i]));
    }

  }

  @Test public void testAddRemoveEdge() throws Exception {
    List list = sort(graph, nodes[0]);
    assertTrue(list.contains(nodes[0]));
    assertTrue(list.contains(nodes[1]));
    assertTrue(list.contains(nodes[2]));
    assertTrue(list.contains(nodes[3]));
    assertTrue(list.contains(nodes[4]));
    assertTrue(list.contains(nodes[5]));
    assertTrue(list.contains(nodes[6]));
    assertFalse(list.contains(nodes[7]));
    assertFalse(list.contains(nodes[8]));
    assertFalse(list.contains(nodes[9]));

    
    graph.remove(edges[1]);
    assertTrue(graph.referencesTo.containsKey(nodes[2]));
    assertTrue(graph.referencesFrom.containsKey(nodes[2]));
    assertTrue(graph.referencesTo.containsKey(nodes[6]));
    assertTrue(graph.referencesFrom.containsKey(nodes[6]));
    assertTrue(graph.referencesTo.containsKey(nodes[7]));
    assertTrue(graph.referencesFrom.containsKey(nodes[7]));
    list = sort(graph, nodes[0]);
    assertTrue(list.contains(nodes[0]));
    assertTrue(list.contains(nodes[1]));
    assertTrue(list.contains(nodes[3]));
    assertTrue(list.contains(nodes[4]));

    assertFalse(list.contains(nodes[2]));
    assertFalse(list.contains(nodes[5]));
    assertFalse(list.contains(nodes[6]));
    assertFalse(list.contains(nodes[7]));
    assertFalse(list.contains(nodes[8]));
    assertFalse(list.contains(nodes[9]));

    graph.remove(edges[8]);
    graph.remove(nodes[2]);
    assertFalse(graph.referencesTo.containsKey(nodes[2]));
    assertFalse(graph.referencesFrom.containsKey(nodes[2]));
    assertTrue(graph.referencesTo.containsKey(nodes[6]));
    assertTrue(graph.referencesFrom.containsKey(nodes[6]));
    assertTrue(graph.referencesTo.containsKey(nodes[7]));
    assertTrue(graph.referencesFrom.containsKey(nodes[7]));
    list = sort(graph, nodes[0]);
    assertTrue(list.contains(nodes[0]));
    assertTrue(list.contains(nodes[1]));
    assertTrue(list.contains(nodes[3]));
    assertTrue(list.contains(nodes[4]));

    assertFalse(list.contains(nodes[2]));
    assertFalse(list.contains(nodes[5]));
    assertFalse(list.contains(nodes[6]));
    assertFalse(list.contains(nodes[7]));
    assertFalse(list.contains(nodes[8]));
    assertFalse(list.contains(nodes[9]));
    /*
     * Check whether Runtime exception is thrown, when one tries to remove a node that is not present in the graph
     */
    try {
      graph.remove("not present");
      Assert.fail("There should be RuntimeException when the checkPresent method of MapGraphBase is invoked with arg. node, that is not present in the graph");
      } catch (RuntimeException  e) {
     Assert.assertTrue(e.getMessage().indexOf("not present in the graph")>0);
  }
  }

  @Test public void testRemove() throws Exception {
    try {
      graph.remove(nodes[2]);
      assertFalse("NodeRemoveException expected. Trying to remove node that still have references from (the node is refered from other nodes).", true);
    } catch (NodeRemoveException ne) {
      Set<Edge<Object>> refsTo = graph.getReferencesToOthersFrom(nodes[2]);      
      assertEquals(2, refsTo.size());
      assertTrue(refsTo.contains(edges[4]));
      assertTrue(refsTo.contains(edges[5]));
      
      Set<Edge<Object>> refsFrom = graph.getReferencesFromOthersTo(nodes[2]);
      assertEquals(2, refsFrom.size());
      assertTrue(refsFrom.contains(edges[1]));
      assertTrue(refsFrom.contains(edges[8]));
    }

  }

  @Test public void testRemoveAddEdge() throws CyclicReferencesException {
    List list;
    graph.remove(edges[1]);

    list = sort(graph, nodes[0]);
    assertTrue(list.contains(nodes[0]));
    assertTrue(list.contains(nodes[1]));
    assertTrue(list.contains(nodes[3]));
    assertTrue(list.contains(nodes[4]));

    assertFalse(list.contains(nodes[2]));
    assertFalse(list.contains(nodes[5]));
    assertFalse(list.contains(nodes[6]));
    assertFalse(list.contains(nodes[7]));
    assertFalse(list.contains(nodes[8]));
    assertFalse(list.contains(nodes[9]));

    graph.add(edges[1]);
    list = sort(graph, nodes[0]);
    assertTrue(list.contains(nodes[0]));
    assertTrue(list.contains(nodes[1]));
    assertTrue(list.contains(nodes[2]));
    assertTrue(list.contains(nodes[3]));
    assertTrue(list.contains(nodes[4]));
    assertTrue(list.contains(nodes[5]));
    assertTrue(list.contains(nodes[6]));
    assertFalse(list.contains(nodes[7]));
    assertFalse(list.contains(nodes[8]));
    assertFalse(list.contains(nodes[9]));
  }
  
  @ Test public void testGetReferences() {
    graph.clear();
    
    String node = "No references";
    graph.add(node);
    Set<Edge<Object>> referencesFrom=graph.getReferencesFromOthersTo(node);
    Assert.assertEquals(0,referencesFrom.size());
    Set<Edge<Object>> referencesTo=graph.getReferencesToOthersFrom(node);
    Assert.assertEquals(0,referencesFrom.size());
        
  }
  
  
  private List sort(MapGraphBase<Object> graphBase, Object node) throws CyclicReferencesException {
    return new Graph<Object>( graphBase ).sort(node);
  }
}
