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

package com.sap.engine.lib.refgraph;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.sap.engine.lib.refgraph.impl.FastReferenceCycleCheckerHandler;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.lib.refgraph.impl.ReferenceCycleCheckerHandler;
import com.sap.engine.lib.refgraph.impl.ReferencePrinterHandler;

/**
 * @author Luchesar Cekov
 */

public class GraphTest {
  private static final String TEST_REF_APPLICATION_NAME_PREFIX = "test.ref/applicationName_";
  private static final int REFERENCES_COUNT = 50;
  private static final int APPLICATIONS_COUNT = 2000;
  private final Graph<Object> graph = new Graph<Object>();

  private void initGraph(final int referenceCount, final int applicationCount) {

    graph.clear();

    final Object[] previousRefs = new Object[referenceCount];
    for (int i = 0; i < applicationCount; i++) {
      final String name = TEST_REF_APPLICATION_NAME_PREFIX + i;
      final Object current = name;
      graph.add(current);

      for (int j = 0; j < previousRefs.length; j++) {
        if (previousRefs[j] != null) {
          if (j % 2 == 0) {
            graph.add(new Edge<Object>(current, previousRefs[j], Edge.Type.HARD, null));
          } else {
            graph.add(new Edge<Object>(current, previousRefs[j], Edge.Type.HARD, new Object()));
          }
        }
      }
      previousRefs[i % previousRefs.length] = current;
    }
  }

  @Test
  public void topoSort() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);

    final String lastAppName = TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1);

    final long start = System.currentTimeMillis();
    List<Object> result = graph.sort();
    final long end = System.currentTimeMillis();
    System.out.println("TopSort took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$

    int preveous = -1;
    int current = 0;
    for (final Iterator<Object> iter = result.iterator(); iter.hasNext();) {
      current = getApplicationIndex((String) iter.next());
      Assert.assertTrue(current == preveous + 1);
      preveous = current;
    }

    result = graph.sort(lastAppName);
    preveous = -1;
    current = 0;
    for (final Iterator<Object> iter = result.iterator(); iter.hasNext();) {
      current = getApplicationIndex((String) iter.next());
      Assert.assertTrue(current == preveous + 1);
      preveous = current;
    }

  }

  @Test
  public void topoSortBackward() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);
    final String lastAppName = TEST_REF_APPLICATION_NAME_PREFIX + (0);

    long start = System.currentTimeMillis();
    List<Object> result = graph.sortBackward();
    long end = System.currentTimeMillis();
    System.out.println("testTopoSortBackward took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$

    int preveous = result.size();
    int current = 0;
    for (final Iterator<Object> iter = result.iterator(); iter.hasNext();) {
      current = getApplicationIndex((String) iter.next());
      Assert.assertTrue(current == preveous - 1);
      preveous = current;
    }

    start = System.currentTimeMillis();
    result = graph.sortBackward(lastAppName);
    end = System.currentTimeMillis();
    System.out.println("testTopoSortBackward1 took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
    preveous = result.size();
    current = 0;
    for (final Iterator<Object> iter = result.iterator(); iter.hasNext();) {
      current = getApplicationIndex((String) iter.next());
      Assert.assertTrue(current == preveous - 1);
      preveous = current;
    }

  }

  


  @Test public void addRemove() throws Exception {    
    graph.clear();

    final Object node1 = "node1";
    final Object node11 = "node1.1";
    final Object node12 = "node1.2";
    final Object node111 = "node1.1.1";
    final Object node112 = "node1.1.2";
    final Object node121 = "node1.2.1";
    final Object node122 = "node1.2.2";
    final Object node2 = "node2";
    final Object node21 = "node2.1";
    final Object node22 = "node2.2";

    graph.add(node1);
    graph.add(node11);
    graph.add(node111);
    graph.add(node112);
    graph.add(node121);
    graph.add(node122);
    graph.add(node12);
    graph.add(node2);
    graph.add(node21);
    graph.add(node22);

    graph.add(new Edge<Object>(node1, node11, Edge.Type.HARD, null));
    graph.add(new Edge<Object>(node1, node12, Edge.Type.HARD, "Resource:temp (public)"));
    graph.add(new Edge<Object>(node11, node111, Edge.Type.HARD, null));
    graph.add(new Edge<Object>(node11, node112, Edge.Type.WEAK, "Resource:temp (public)"));
    graph.add(new Edge<Object>(node12, node121, Edge.Type.HARD, null));
    graph.add(new Edge<Object>(node12, node122, Edge.Type.WEAK, "Resource:temp (public)"));
    graph.add(new Edge<Object>(node2, node21, Edge.Type.HARD, "Resource:temp (public)"));
    graph.add(new Edge<Object>(node2, node22, Edge.Type.WEAK, null));

    List<Object> sorted = graph.sort(node1);

    Assert.assertTrue(sorted.contains(node122));
    Assert.assertTrue(sorted.contains(node11));
    Assert.assertTrue(sorted.contains(node12));
    Assert.assertTrue(sorted.contains(node111));
    Assert.assertTrue(sorted.contains(node112));
    Assert.assertTrue(sorted.contains(node121));
    Assert.assertTrue(sorted.contains(node122));


    sorted = graph.sort();
    Assert.assertTrue(sorted.contains(node122));
    Assert.assertTrue(sorted.contains(node11));
    Assert.assertTrue(sorted.contains(node12));
    Assert.assertTrue(sorted.contains(node111));
    Assert.assertTrue(sorted.contains(node112));
    Assert.assertTrue(sorted.contains(node121));
    Assert.assertTrue(sorted.contains(node122));
    Assert.assertTrue(sorted.contains(node2));
    Assert.assertTrue(sorted.contains(node21));
    Assert.assertTrue(sorted.contains(node22));

    graph.traverseForward(node1, new ReferencePrinterHandler(new PrintWriter(System.out)));//$JL-SYS_OUT_ERR$
    graph.traverseForward(node2, new ReferencePrinterHandler(new PrintWriter(System.out)));//$JL-SYS_OUT_ERR$

    graph.traverseForward(new ReferencePrinterHandler(new PrintWriter(System.out)));//$JL-SYS_OUT_ERR$

    graph.traverseBackward(new ReferencePrinterHandler(new PrintWriter(System.out)));//$JL-SYS_OUT_ERR$

    graph.remove(new Edge<Object>(node1, node12, Edge.Type.HARD, "Resource:temp (public)"));
    final List<Object> sorted1 = graph.sort(node1);
    Assert.assertFalse(sorted1.contains(node122));

  }

  @Test
  public void testRemove() throws NodeRemoveException {
    graph.clear();
    final Object node1 = "node1";
    final Object node11 = "node1.1";
    final Object node12 = "node1.2";
    final Object node111 = "node1.1.1";
    final Object node112 = "node1.1.2";

    graph.add(node1);
    graph.add(node11);
    graph.add(node111);
    graph.add(node112);
    graph.add(node12);
    Assert.assertTrue(graph.containsNode(node1));
    graph.remove(node1);
    Assert.assertFalse(graph.containsNode(node1));
    graph.add(new Edge<Object>(node11, node111, Edge.Type.HARD, null));

    try {
      graph.remove(node111);
      Assert.fail("There should be NodeRemoveException when the remove method  is invoked for node, that is not in the graph");
    } catch (NodeRemoveException nre) {
      Assert.assertTrue(nre.getMessage().indexOf("can not be removed. Nodes") > 0);
      System.out.println(nre.getMessage());//$JL-SYS_OUT_ERR$

    }

  }

  @Test public void testPrint() throws Exception {
    initGraph(2, 5);
    // graph.add("new Node");
   String [] nodes = {TEST_REF_APPLICATION_NAME_PREFIX+1,TEST_REF_APPLICATION_NAME_PREFIX+2};
    final PrintWriter out = new PrintWriter(System.out);//$JL-SYS_OUT_ERR$
    out.println("----------------------Print----------------------");
    // graph.print(out);
    graph.print(new String[] { TEST_REF_APPLICATION_NAME_PREFIX + 4, TEST_REF_APPLICATION_NAME_PREFIX + 3,
                              TEST_REF_APPLICATION_NAME_PREFIX + 2 }, out);
    out.println("------------------ Print END -------------------");
    // graph.traverseForwardRootNode(new String[]
    // {TEST_REF_APPLICATION_NAME_PREFIX + 4},
    // new ReferencePrinterHandler(out));
    //
    // graph.traverseBackwardRootNode(new String[]
    // {TEST_REF_APPLICATION_NAME_PREFIX + (0)},
    // new ReferencePrinterHandler(out));
    out.println("----------------------PrintBackward----------------------");
    graph.printBackward(out);
    out.println("------------------ PrintBackward END -------------------");
    out.println("----------------------PrintBackward with nodes----------------------");
    graph.printBackward(nodes, out);
    out.println("------------------ PrintBackward END with nodes -------------------");
    
    
  }

  private void initGraphForPrint(final int referenceCount, final int applicationCount) {
    graph.clear();

    final Object[] previousRefs = new Object[referenceCount];
    for (int i = 0; i < applicationCount; i++) {
      final String name = getRandomComponentName(i);
      final Object current = name;
      graph.add(current);

      for (int j = 0; j < previousRefs.length; j++) {
        if (previousRefs[j] != null) {
          if (j % 2 == 0) {
            graph.add(new Edge<Object>(current, previousRefs[j], Edge.Type.HARD, null));
          } else {
            // graph.add(new Edge<Object>(current, previousRefs[j],
            // Edge.Type.HARD, new Resource("Resource", "temp",
            // AccessType.PUBLIC)));
            // new ComponentResource(
            graph.add(new Edge<Object>(current, previousRefs[j], Edge.Type.HARD, null));

            // (String) previousRefs[j], "APPLICATION", Resource.HARD)));
          }
        }
      }
      previousRefs[i % previousRefs.length] = current;
    }
  }

  private String getRandomComponentName(final int i) {
    String prefix = "";
    final double random = Math.random();
    if (random > 0 && random < 0.25) {
      prefix = "library:";
    } else if (random > 0.25 && random < 0.5) {
      prefix = "interface:";
    } else if (random > 0.5 && random < 0.75) {
      prefix = "service:";
    }
    return prefix + TEST_REF_APPLICATION_NAME_PREFIX + i;
  }

  @Test
  public void fastCycleCheck() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);

    // let's give chance GIT to work in order to detect real cycleCheck time
    graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));
    graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));
    graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));
    graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));

    final long start = System.currentTimeMillis();
    graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));
    final long end = System.currentTimeMillis();
    System.out.println("Fast CycleCheck no cycles took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$

  }

  @Test
  public void cycleCheck() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);

    final long start = System.currentTimeMillis();
    graph.traverseForward(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1),
                        new ReferenceCycleCheckerHandler());
    final long end = System.currentTimeMillis();
    graph.cycleCheck();
    System.out.println("CycleCheck no cycles  took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
  }

  @Test
  public void cycleCheckWithCycles() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);
    final Edge edge = new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + 0, TEST_REF_APPLICATION_NAME_PREFIX
                                                                             + (APPLICATIONS_COUNT - 1),
                        Edge.Type.HARD, null);
    graph.add(edge);

    final long start = System.currentTimeMillis();
   try {
      graph.cycleCheck(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1));
      Assert.assertTrue("CyclicReferenceException expected", false);
      
    } catch (final CyclicReferencesException e) {
      // $JL-EXC$ works fine
    } finally {
      final long end = System.currentTimeMillis();
      System.out.println("CycleCheck With Cycles took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
    }
  }
    @Test 
   public void cycleChechOnlyCycles() {
   
	   graph.clear();
	   final Object node1 = "node1";
	   final Object node11 = "node1.1";
	   final Object node12 = "node1.2";
	
	   graph.add(node1);
	   graph.add(node11);
	   graph.add(node12);
	
	   graph.add(new Edge<Object>(node1, node11, Edge.Type.HARD, null));
	   graph.add(new Edge<Object>(node11, node12, Edge.Type.HARD, null));
	   graph.add(new Edge<Object>(node12, node1, Edge.Type.HARD, null));
	
	  
	    try {
	    	graph.cycleCheck();
	    	Assert.fail();
	    } catch (final CyclicReferencesException e) {
	      System.out.println("Cycle has been found. CyclicReferenceException is thrown ");
	      Assert.assertTrue(e.getMessage().contains("One or more Cyclic reference detected during building application reference graph"));//$JL-SYS_OUT_ERR$
	      System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
	    }
    }
    @ Test
    public void testSortNoIndependantNodes() {
      
      graph.clear();
	   final Object node1 = "node1";
	   final Object node11 = "node1.1";
	   final Object node12 = "node1.2";
	
	   graph.add(node1);
	   graph.add(node11);
	   graph.add(node12);
	
	   graph.add(new Edge<Object>(node1, node11, Edge.Type.HARD, null));
	   graph.add(new Edge<Object>(node11, node12, Edge.Type.HARD, null));
	   graph.add(new Edge<Object>(node12, node1, Edge.Type.HARD, null));
	
	  
	    try {
	    	graph.sort();
	    	Assert.fail();
	    } catch (final CyclicReferencesException e) {
	      System.out.println("Cycle has been found. CyclicReferenceException is thrown ");//$JL-SYS_OUT_ERR$
	      Assert.assertTrue(e.getMessage().contains("One or more Cyclic reference detected during building application reference graph"));
	      System.out.println(e.getMessage());//$JL-SYS_OUT_ERR$
	    }
    }


  @Test
  public void fastCycleCheckWithCycles() throws Exception {
    initGraph(REFERENCES_COUNT, APPLICATIONS_COUNT);
    final Edge edge = new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + 0, TEST_REF_APPLICATION_NAME_PREFIX
                                                                             + (APPLICATIONS_COUNT - 1),
                        Edge.Type.HARD, null);
    graph.add(edge);

    final long start = System.currentTimeMillis();
    try {
      graph.traverseForward(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1),
                          new FastReferenceCycleCheckerHandler());
      Assert.assertTrue("CyclicReferenceException expected", false);
    } catch (final CyclicReferencesException e) {
      // $JL-EXC$ works fine
    } finally {
      final long end = System.currentTimeMillis();
      System.out.println("Fast CycleCheck With Cycles took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
    }
  }

  @Test
  public void sizeTest() {
    final Object node1 = "node1";
    final Object node11 = "node1.1";
    final Object node12 = "node1.2";
    final Object node111 = "node1.1.1";
    final Object node112 = "node1.1.2";
    final Object node121 = "node1.2.1";
    final Object node122 = "node1.2.2";
    final Object node2 = "node2";
    final Object node21 = "node2.1";
    final Object node22 = "node2.2";

    graph.add(node1);
    graph.add(node11);
    graph.add(node111);
    graph.add(node112);
    graph.add(node121);
    graph.add(node122);
    graph.add(node12);
    graph.add(node2);
    graph.add(node21);
    graph.add(node22);
    int size = graph.size();
    System.out.println("Size of the graph is " + size);//$JL-SYS_OUT_ERR$
    Assert.assertEquals(10, size);
  }

  @Test
  public void testGetReferencesFrom() {

    graph.clear();

    final Object node1 = "node1";
    final Object node11 = "node1.1";
    final Object node12 = "node1.2";

    graph.add(node1);
    graph.add(node11);
    graph.add(node12);

    graph.add(new Edge<Object>(node1, node11, Edge.Type.HARD, null));
    graph.add(new Edge<Object>(node12, node11, Edge.Type.HARD, null));

    Set<Edge<Object>> references = graph.getReferencesFromOthersTo(node11);

    for (Edge<Object> edges : references) {
      Assert.assertTrue(edges.getFirst().equals(node1) || edges.getFirst().equals(node12));
    }

  }

  @Test
  public void testGetReferencesTo() {

    graph.clear();

    final Object node1 = "node1";
    final Object node11 = "node1.1";
    final Object node12 = "node1.2";

    graph.add(node1);
    graph.add(node11);
    graph.add(node12);

    graph.add(new Edge<Object>(node1, node11, Edge.Type.HARD, null));
    graph.add(new Edge<Object>(node1, node12, Edge.Type.HARD, null));

    Set<Edge<Object>> references = graph.getReferencesToOthersFrom(node1);
    int i = 0;
    for (Edge<Object> edges : references) {
      Assert.assertTrue(edges.getSecond().equals(node11) || edges.getSecond().equals(node12));
    }
  }

 
  private int getApplicationIndex(final String applicationName) {
    return Integer.parseInt(applicationName.substring(TEST_REF_APPLICATION_NAME_PREFIX.length()));
  }
}
