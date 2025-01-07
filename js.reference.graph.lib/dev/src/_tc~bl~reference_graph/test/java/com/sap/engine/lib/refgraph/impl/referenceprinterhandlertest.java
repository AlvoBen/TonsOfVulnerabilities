package com.sap.engine.lib.refgraph.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.sap.engine.lib.refgraph.Edge;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ReferencePrinterHandlerTest extends TestCase {

  private static final String TEST_REF_APPLICATION_NAME_PREFIX = "test.ref/applicationName_";
  private static final String TEST_REF_INTERFACE_NAME_PREFIX = "interface:/test.ref/interfaceName_";
  private static final String TEST_REF_SERVICE_NAME_PREFIX = "service:/serviceName_";
  private static final String TEST_REF_LIBRARY_NAME_PREFIX = "library:/libraryName_";
  private static final int NODES_COUNT = 5;
  private StringWriter strr = new StringWriter();
  final PrintWriter out = new PrintWriter(strr);
  private final Graph<Object> graph = new Graph<Object>();
  private ReferencePrinterHandler<String> handler = new ReferencePrinterHandler<String>(out);

  public void testPrintGraphAllComponentsTypes() throws Exception {
    graph.clear();
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(TEST_REF_APPLICATION_NAME_PREFIX + i);
    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(TEST_REF_INTERFACE_NAME_PREFIX + i);
    }

    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(TEST_REF_SERVICE_NAME_PREFIX + i);
    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(TEST_REF_LIBRARY_NAME_PREFIX + i);
    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + i, TEST_REF_SERVICE_NAME_PREFIX + i,
                          Edge.Type.HARD, null));

    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(new Edge<Object>(TEST_REF_SERVICE_NAME_PREFIX + i, TEST_REF_LIBRARY_NAME_PREFIX + i, Edge.Type.HARD,
                          null));
    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(new Edge<Object>(TEST_REF_LIBRARY_NAME_PREFIX + i, TEST_REF_INTERFACE_NAME_PREFIX + i, Edge.Type.HARD,
                          null));
    }
    graph.print(out);
    System.out.println("----------------------testPrintGraphAllComponentsTypes---------------------------");//$JL-SYS_OUT_ERR$
    System.out.println(strr.toString());//$JL-SYS_OUT_ERR$

    for (int i = 0; i <= NODES_COUNT; i++) {
      Assert.assertTrue(strr.toString().contains("+-->+application:test.ref/applicationName_" + i));
      Assert.assertTrue(strr.toString().contains("   +-->+HARD to service:/serviceName_" + i));
      Assert.assertTrue(strr.toString().contains("      +-->+HARD to library:/libraryName_" + i));
      Assert.assertTrue(strr.toString().contains("        +-->+HARD to interface:/test.ref/interfaceName_" + i));

    }
    out.flush();
    strr.flush();
  }

  public void testSelfCycle() throws Exception {
    graph.clear();

    graph.add(TEST_REF_APPLICATION_NAME_PREFIX + 0);

    graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + "0", TEST_REF_APPLICATION_NAME_PREFIX + "0",
                        Edge.Type.HARD, null));
    String[] nodes = { TEST_REF_APPLICATION_NAME_PREFIX + "0" };
    graph.print(nodes, out);
    System.out.println("----------------------testSelfCycle---------------------------");//$JL-SYS_OUT_ERR$
    System.out.println(strr.toString());//$JL-SYS_OUT_ERR$

    Assert.assertTrue(strr.toString().contains("+-->+application:test.ref/applicationName_0"));
    Assert.assertTrue(strr.toString().contains(" +-->+Self Cycle+"));
    Assert.assertEquals(0, handler.getLevel());
    out.flush();
    strr.flush();
  }

  public void testSelfCycleThroughResource() throws Exception {
    graph.clear();

    graph.add(TEST_REF_APPLICATION_NAME_PREFIX + 0);
    graph.add(TEST_REF_INTERFACE_NAME_PREFIX + 0);
    graph.add(TEST_REF_SERVICE_NAME_PREFIX + 0);
    graph.add(TEST_REF_LIBRARY_NAME_PREFIX + 0);
    graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + "0", TEST_REF_SERVICE_NAME_PREFIX + "0",
                        Edge.Type.HARD, null));

    graph.add(new Edge<Object>(TEST_REF_SERVICE_NAME_PREFIX + "0", TEST_REF_LIBRARY_NAME_PREFIX + "0", Edge.Type.HARD,
                        null));

    graph.add(new Edge<Object>(TEST_REF_LIBRARY_NAME_PREFIX + "0", TEST_REF_INTERFACE_NAME_PREFIX + "0",
                        Edge.Type.HARD, null));

    graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + "0", TEST_REF_APPLICATION_NAME_PREFIX + "0",
                        Edge.Type.HARD, "test/resource (public)"));
    String[] nodes = { TEST_REF_APPLICATION_NAME_PREFIX + "0" };
    graph.print(nodes, out);
    System.out.println("----------------------testSelfCycleThroughResource---------------------------");//$JL-SYS_OUT_ERR$
    System.out.println(strr.toString());//$JL-SYS_OUT_ERR$

    Assert.assertTrue(strr.toString().contains("+-->+HARD to resource:test/resource (public)"));
    Assert.assertTrue(strr.toString().contains(" +-->+Resource provided by test.ref/applicationName_0"));

    out.flush();
    strr.flush();
  }

  public void testCycle() throws Exception {
    graph.clear();
    for (int i = 0; i <= NODES_COUNT; i++) {
      graph.add(TEST_REF_APPLICATION_NAME_PREFIX + i);
    }
    for (int i = 0; i <= NODES_COUNT; i++) {
      int j = i + 1;
      if (i != NODES_COUNT)
        graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + i, TEST_REF_APPLICATION_NAME_PREFIX + j,
                            Edge.Type.HARD, null));
      else
        graph.add(new Edge<Object>(TEST_REF_APPLICATION_NAME_PREFIX + i, TEST_REF_APPLICATION_NAME_PREFIX + "0",
                            Edge.Type.HARD, null));
    }

    String[] nodes = new String[1];
    nodes[0] = "test.ref/applicationName_0";
    graph.print(nodes, out);
    System.out.println("----------------------testCycle---------------------------");//$JL-SYS_OUT_ERR$
    System.out.println(strr.toString());//$JL-SYS_OUT_ERR$
    Assert.assertTrue(strr.toString().contains("+-->+ERROR--HARD to application:test.ref/applicationName_" + 0));
  }

}
