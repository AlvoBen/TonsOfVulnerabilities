package com.sap.engine.objectprofiler.view.utils;

import com.sap.engine.objectprofiler.view.GraphVizualizer;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;
import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.graph.Key;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-5-4
 * Time: 17:00:20
 */
public class SerializedGraphsBrowser extends JPanel {
  private JTree tree = new JTree();
  private JButton load = new JButton("Load");
  private JButton refresh = new JButton("Refresh");

  private GraphVizualizer vizualizer = null;
  private String rootDir = null;

  private JTextField dir = null;
  private JButton changeDir = new JButton("Change Dir");

  public SerializedGraphsBrowser(GraphVizualizer viz) {
    this.vizualizer = viz;
    this.rootDir = ".";

    dir = new JTextField();
    dir.setText(rootDir);
    dir.setEditable(false);
    dir.setFont(dir.getFont().deriveFont(Font.BOLD));

    refreshModel();

    tree.setEditable(false);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setShowsRootHandles(true);

    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.setCellRenderer(new MyTreeCellRenderer());

    tree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          loadGraph();
        }
      }
    });
    Dimension bsize = new Dimension(120,28);

    load.setPreferredSize(bsize);
    load.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadGraph();
      }
    });

    refresh.setPreferredSize(bsize);
    refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refreshModel();
      }
    });

    changeDir.setPreferredSize(bsize);
    changeDir.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeDir();
      }
    });

    arrangeComponents();
  }

  private void arrangeComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    setLayout(gridbag);

    JScrollPane scroller = new JScrollPane(tree);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.gridheight = 1;
    gridbag.setConstraints(dir, c);
    add(dir);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(changeDir, c);
    add(changeDir);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 3;
    c.gridheight = 1;
    gridbag.setConstraints(scroller, c);
    add(scroller);

    JPanel empty = new JPanel();

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(empty, c);
    add(empty);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 1;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(refresh, c);
    add(refresh);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 2;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(load, c);
    add(load);
  }

  public void refreshModel() {
    setCursor(GraphVizualizerPanel.waitCursor);

    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new GraphTreeElement(rootDir));
    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    buildTreeModel(rootNode);
    tree.setModel(treeModel);

    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void loadGraph() {
    setCursor(GraphVizualizerPanel.waitCursor);
    Graph g = getGraph();
    if (g != null) {
      vizualizer.setGraph(g);
      vizualizer.setSelectedIndex(0);
    }
    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public Graph getGraph() {
    Graph graph = null;

    try {
      graph = getSelectedGraph();
    } catch (Exception e) {
      e.printStackTrace();
      GraphVizualizer.showMessageBox(e);
    }

    return graph;
  }

  public Graph getSelectedGraph() {
    Graph result = null;

    TreePath currentSelection = tree.getSelectionPath();
    if (currentSelection != null) {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
      GraphTreeElement el = (GraphTreeElement)currentNode.getUserObject();
      if (el.getType() == GraphTreeElement.GRAPH) {
        result = el.getGraph();
      }
    }

    return result;
  }

  private void changeDir() {
    JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(new java.io.File("."));
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int res = fc.showOpenDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      String directory = file.getAbsolutePath();
      dir.setText(directory);
      rootDir = directory;
      refreshModel();
    }
  }

  private void buildTreeModel(DefaultMutableTreeNode parentNode) {
    String path = ((GraphTreeElement)(parentNode.getUserObject())).getFileName();

    File file = new File(path);
    File[] files = file.listFiles();
    for (int i=0;i<files.length;i++) {
      if (files[i].isDirectory()) {
        GraphTreeElement el = new GraphTreeElement(files[i].getAbsolutePath());
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(el);
        parentNode.add(node);
        buildTreeModel(node);
      } else if (files[i].getName().toLowerCase().endsWith("ser")) {
        GraphTreeElement el = new GraphTreeElement(files[i].getAbsolutePath(), GraphTreeElement.GRAPH);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(el);
        parentNode.add(node);
      }
    }
  }

  private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon red = null;
    private ImageIcon blue = null;
    private ImageIcon green = null;
    private ImageIcon gray = null;

    public MyTreeCellRenderer() {
      URL url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"redDot.png");
      if (url != null) {
        red = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"blueDot.png");
      if (url != null) {
        blue = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"greenDot.png");
      if (url != null) {
        green = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"grayDot.png");
      if (url != null) {
        gray = new ImageIcon(url);
      }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,  boolean sel,
                                                  boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      Object obj = ((DefaultMutableTreeNode)value).getUserObject();
      if (!(obj instanceof GraphTreeElement)) {
        setIcon(gray);
        return this;
      }

      GraphTreeElement el = (GraphTreeElement)((DefaultMutableTreeNode)value).getUserObject();

      if (el.getType() == GraphTreeElement.DIR) {
        setIcon(blue);
      } else if (el.isShareable()) {
        setIcon(green);
      } else {
        setIcon(red);
      }

      return this;
    }
  }

  private class GraphTreeElement {
    public static final int DIR = 0;
    public static final int GRAPH = 1;

    private String fileName = null;
    private int type = DIR;
    private boolean shareable = false;

    public GraphTreeElement(String fileName) {
      this(fileName, DIR);
    }

    public GraphTreeElement(String fileName, int type) {
      this.fileName = fileName;
      this.type = type;

      if (this.fileName != null && this.fileName.toLowerCase().endsWith("_y.ser")) {
        shareable = true;
      }
    }

    public String getFileName() {
      return fileName;
    }

    public int getType() {
      return type;
    }

    public boolean isShareable() {
      return shareable;
    }

    public Graph getGraph() {
      Graph g = null;

      try {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        g = (Graph)ois.readObject();
        ois.close();
        fis.close();

        //showInfo(g);
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }

      return g;
    }

    public String toString() {
      return fileName;
    }

    public void showInfo(Graph g) {
      int num = g.getNodeCount();
      int k = 0;

      Node root = g.getRoot();
      Stack stack = new Stack();
      ArrayList traversed = new ArrayList();

      stack.push(root);
      traversed.add(root);
      while (!stack.isEmpty()) {
        Node node = (Node)stack.pop();
        System.out.println(" >> NODE ID "+node.getId()+" "+System.identityHashCode(node));
        k++;
        if (!g.getNodes().contains(node)) {
          System.out.println("Node is missing "+node.getId());
        }

        Node[] kids = g.getChildren(node);
        for (int i=0;i<kids.length;i++) {
          if (!traversed.contains(kids[i])) {
            stack.push(kids[i]);
            traversed.add(kids[i]);
          }
        }
      }
      System.out.println(" Node count in graph = "+num+ " node count in kids = "+k);
    }
  }
}
