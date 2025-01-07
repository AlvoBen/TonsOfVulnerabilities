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
import java.io.Writer;
import java.util.List;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeHandler;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Elena Yaneva
 */
public class DFSTraverserTest extends TestCase {
  DFSTraverser<String> DFStraverser = new DFSTraverser<String>();
  TestHandler<String> tesHandler = new TestHandler<String>();
  private final Graph graph = new Graph<String>();
  private TopoSortHandler<String> handler = new TopoSortHandler<String>();
  private TopoSortHandler<String> handlerCycle = new TopoSortHandler<String>();
  private String[] nodes = new String[] { "node0", "node1", "node2", "node3", "node4", "node5", "node6", "node7",
                                         "node8", "node9" };
  private StringWriter strr = new StringWriter();
  final PrintWriter out = new PrintWriter(strr);

  public void testTraverseForward() throws Exception {
    graph.add(nodes[0]);

    handler.startNode(nodes[0], null, true);

    DFStraverser.traverseForward(graph, nodes[0], handler, true);
    DFStraverser.traverseForward(graph, handler);

  }

  public void testTraverseBackward() throws Exception {
    graph.clear();
    graph.add(nodes[0]);
    graph.add(nodes[1]);
    graph.add(nodes[2]);
    graph.add(new Edge<String>(nodes[0], nodes[1], Edge.Type.HARD, null));
    graph.add(new Edge<String>(nodes[1], nodes[2], Edge.Type.HARD, null));

    System.out.println("Sorted");//$JL-SYS_OUT_ERR$
    DFStraverser.traverseBackward(graph, handler);
    List<String> sorted = handler.getSortedComponentsNames();
    Assert.assertEquals(nodes[0], sorted.get(0));
    Assert.assertEquals(nodes[1], sorted.get(1));
    Assert.assertEquals(nodes[2], sorted.get(2));

    for (String node : sorted) {
      System.out.println(node);//$JL-SYS_OUT_ERR$
    }
    DFStraverser.traverseBackward(graph, nodes[0], handler);
    DFStraverser.traverseBackward(graph, nodes[0], handler);

  }

  public void testtraverseBackwardSelfCycle() throws Exception {
    graph.clear();
    graph.add(nodes[0]);
    graph.add(nodes[1]);
    graph.add(nodes[2]);
    graph.add(nodes[3]);
    graph.add(new Edge<String>(nodes[0], nodes[0], Edge.Type.HARD, null));
    graph.add(new Edge<String>(nodes[1], nodes[2], Edge.Type.HARD, null));
    graph.add(new Edge<String>(nodes[2], nodes[3], Edge.Type.HARD, null));
    System.out.println("SortedWithSelfCycle");//$JL-SYS_OUT_ERR$

    DFStraverser.traverseBackward(graph, nodes[0], handlerCycle);
    List<String> sorted = handlerCycle.getSortedComponentsNames();
    for (String node : sorted) {
      System.out.println(node);//$JL-SYS_OUT_ERR$
    }

  }

  public void testtraverseBackwardCycle() {
    graph.clear();
    graph.add(nodes[0]);
    graph.add(nodes[1]);
    graph.add(nodes[2]);
    graph.add(new Edge<String>(nodes[0], nodes[1], Edge.Type.HARD, null));
    graph.add(new Edge<String>(nodes[1], nodes[2], Edge.Type.HARD, null));
    graph.add(new Edge<String>(nodes[2], nodes[0], Edge.Type.HARD, null));
    System.out.println("SortedWithCycle");//$JL-SYS_OUT_ERR$
    try {
      DFStraverser.traverseBackward(graph, nodes[0], handlerCycle);
      Assert.fail("There should be CyclicReferenceException when the cycle method of TopoSortHandler is invoked");

    } catch (CyclicReferencesException e) {
      Assert.assertTrue(e.getMessage().indexOf(
                          "One or more Cyclic reference detected during building application reference graph") == 0);
      System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$

    }
  }
  public void testStartRoot() throws Exception {
    DFStraverser.traverseForwardRootNode(graph, nodes, tesHandler);
    DFStraverser.traverseBackwardRootNode(graph, nodes, tesHandler);
    
  }
  public void testTraverseBackwardHard() throws Exception {
	  graph.clear();
	  for (int i=0; i<=6; i++){
		  graph.add(nodes[i]);
	  }
	  Edge[] edge = new Edge[]{
				new Edge<String>(nodes[0], nodes[1], Edge.Type.WEAK, null),
				new Edge<String>(nodes[0], nodes[2], Edge.Type.HARD, null),
				new Edge<String>(nodes[1], nodes[3], Edge.Type.HARD, null),
				new Edge<String>(nodes[2], nodes[3], Edge.Type.WEAK, null),
				new Edge<String>(nodes[4], nodes[1], Edge.Type.HARD, null),
				new Edge<String>(nodes[0], nodes[6], Edge.Type.WEAK, null),
				new Edge<String>(nodes[4], nodes[5], Edge.Type.WEAK, null)
	  };
	  
	  for (int i=0; i<edge.length; i++){
		  graph.add(edge[i]);
	  }
	  DFStraverser.traverseBackwardHard(graph, nodes[3], handler);
	  
	  List<String> sorted = handler.getSortedComponentsNames();
	  Assert.assertEquals(nodes[4], sorted.get(0));
	  Assert.assertEquals(nodes[1], sorted.get(1));
	  Assert.assertEquals(nodes[3], sorted.get(2));

	  
	  System.out.println("Sorted");//$JL-SYS_OUT_ERR$
	  for (String node : sorted) {
	      System.out.println(node);//$JL-SYS_OUT_ERR$
	    }
  }
  
  public void testTraverseBackwardHardCycle(){
	  graph.clear();
	  for (int i=0; i<=6; i++){
		  graph.add(nodes[i]);
	  }
	  Edge[] edge = new Edge[]{
				new Edge<String>(nodes[0], nodes[1], Edge.Type.WEAK, null),
				new Edge<String>(nodes[1], nodes[3], Edge.Type.HARD, null),
				new Edge<String>(nodes[2], nodes[3], Edge.Type.WEAK, null),
				new Edge<String>(nodes[4], nodes[1], Edge.Type.HARD, null),
				new Edge<String>(nodes[0], nodes[4], Edge.Type.HARD, null),
				new Edge<String>(nodes[0], nodes[6], Edge.Type.WEAK, null),
				new Edge<String>(nodes[4], nodes[5], Edge.Type.WEAK, null),
				new Edge<String>(nodes[5], nodes[0], Edge.Type.WEAK, null)
	  };
	  
	  for (int i=0; i<edge.length; i++){
		  graph.add(edge[i]);
	  }
	  try {
		  graph.traverseBackwardHard(nodes[3], handler);
	  } catch (CyclicReferencesException cre) {
	      Assert.assertTrue(cre.getMessage().indexOf(
          "One or more Cyclic reference detected during building application reference graph") == 0);
	      System.out.println(cre.getMessage());//$JL-SYS_OUT_ERR$
	}  
  }

 class TestHandler<N>  implements NodeHandler<N>{
  public boolean startRoot() {
    
    return false;
  }
  
  public void endRoot() {//
  }

  public boolean startNode(N node, Edge<N> formEdge, boolean hasNextCybling) {
       return true;
    }
    
  
  public void endNode(N aNode) {
  }

  public void cycle(N node, Edge<N> formEdge,boolean isLastCybling) throws CyclicReferencesException {
    throw new CyclicReferencesException(new String[] {});
  }

  public void selfCycle(N node, Edge<N> formEdge,boolean isLastCybling) {
  }
 }
}


