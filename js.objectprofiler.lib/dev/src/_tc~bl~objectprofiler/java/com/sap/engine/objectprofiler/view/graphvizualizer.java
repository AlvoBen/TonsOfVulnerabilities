package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.view.utils.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 *
 * User: Pavel Bonev
 * Date: 2005-4-14
 * Time: 16:01:38
 */
public class GraphVizualizer extends JTabbedPane { //implements GlyphActionListener {
  public static String imagesDir = "com/sap/engine/objectprofiler/view/utils/icons/";

  private GraphVizualizerPanel vizPanel = null;
  private JTextArea textArea = null;
  private SortableTable stats = null;
  private SessionsTree sessionsTree = null;
  private CachesTree cachesTree = null;
  private SerializedGraphsBrowser serializedGraphsTree = null;


  public GraphVizualizer(Graph graph) {
    buildComponents(graph);
    addChangeListener(new TabChangedListener());
  }

  public GraphVizualizer(Graph graph, String[] args)  {
    sessionsTree = new SessionsTree(this, args);
    cachesTree = new CachesTree(this, args);
    serializedGraphsTree = new SerializedGraphsBrowser(this);

    buildComponents(graph);
    addChangeListener(new TabChangedListener());
  }

  public GraphVizualizerPanel getVizualizerPanel() {
    return vizPanel;
  }

  public void setGraph(Graph g) {
    vizPanel.setGraph(g);
    showGraphInfo();
  }

  public Graph getGraph() {
    return vizPanel.getGraph();
  }

  public void reconnect(String args[]) {
    if (sessionsTree != null) {
      try {
        sessionsTree.reconnect(args);
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e.getMessage());
      }
      sessionsTree.refreshModel();
    }

    if (cachesTree != null) {
      try {
        cachesTree.reconnect(args);
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e.getMessage());
      }
      cachesTree.refreshModel();
    }
  }

  public static void showMessageBox(String text) {
    JOptionPane.showMessageDialog(null, text, "Warning", JOptionPane.WARNING_MESSAGE);
  }

  public static void showMessageBox(Throwable t) {
    String text = t.getClass().getName()+"\n"+t.getMessage();

    showMessageBox(text);
  }

  private static void expandTree(JTree tree, DefaultMutableTreeNode startNode) {
    Enumeration children = startNode.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)children.nextElement();
      if (!node.isLeaf()) {
        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);

        expandTree(tree, node);
      }
    }
  }


  private void showGraphInfo() {
    Graph graph = vizPanel.getGraph();
    if (graph == null) {
      return;
    }

    textArea.setEditable(true);

    String[] info = graph.getInfo();
    if (info != null) {
      for (int i=0;i<info.length;i++) {
        textArea.append(info[i]+'\n');
      }
    }

    textArea.setEditable(false);

    setStatsTableData();
  }

  private void buildComponents(Graph graph) {
    vizPanel = new GraphVizualizerPanel(graph);

    textArea = new JTextArea();

    stats = new SortableTable(new SortableTableModel(new Object[0][3], new String[] {"Class Name", "Count", "Total Size"}), this);
    stats.getTableHeader().setReorderingAllowed(false);

    setStatsTableData();
    JScrollPane tableScroller = new JScrollPane(stats);

    addTab("Graph Canvas", vizPanel);
    //addTab("Graph Info", scroller);
    addTab("Graph Stats", tableScroller);
    addTab("Browse Sessions", sessionsTree);
    addTab("Browse Caches", cachesTree);
    addTab("Browse Serialized Graphs", serializedGraphsTree);
  }

  private void setStatsTableData() {
    int numRows = 0;
    HashMap statMap = null;

    Graph graph = vizPanel.getGraph();
    if (graph != null) {
      statMap = graph.getClassStatistics();
      numRows = statMap.size();
    }

    String[] columnNames = new String[] {"Class Name", "Count", "Total Size"};
    Object[][] rowInfo = new Object[numRows][3];

    if (statMap != null) {
      int i = 0;

      Iterator iter = statMap.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        Dimension info = (Dimension)entry.getValue();
        rowInfo[i][0] = entry.getKey();
        rowInfo[i][1] = new Integer((int)info.getWidth());
        rowInfo[i][2] = new Integer((int)info.getHeight());

        i++;
      }
    }

    stats.setModel(new SortableTableModel(rowInfo, columnNames));

  }

  

  private class TabChangedListener implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      GraphVizualizer tabPane = (GraphVizualizer)e.getSource();
      int index = tabPane.getSelectedIndex();
      if (index == 0 && vizPanel != null) {
        vizPanel.requestFocus();
      }
    }
  }
}
