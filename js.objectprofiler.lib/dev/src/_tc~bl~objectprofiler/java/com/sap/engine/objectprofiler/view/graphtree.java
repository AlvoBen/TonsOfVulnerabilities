package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Node;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;

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
 * Date: 2005-6-2
 * Time: 14:48:38
 */
public class GraphTree extends JPanel {

  private JTree tree = new JTree();
  private JTextArea area = new JTextArea();
  private JButton showAll = new JButton("Mark References");
  private JButton switchAndPoint = new JButton("Switch & Select");

  private GraphVizualizerPanel parentPanel = null;

  public GraphTree(GraphVizualizerPanel _parentPanel) {
    super();

    this.parentPanel = _parentPanel;

    tree.putClientProperty("JTree.lineStyle", "Angled");

    tree.setModel(null);
    tree.setEditable(false);
    tree.setShowsRootHandles(true);

    tree.setCellRenderer(new MyTreeCellRenderer());

    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        Glyph g = getSelectedGlyph();
        if (g != null) {
          Node node = g.getNode();
          showNodeInfo(node);
        }
      }
    });

    tree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
          int selRow = tree.getRowForLocation(e.getX(), e.getY());
          if(selRow != -1) {
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            if(selPath != null) {
              tree.setSelectionPath(selPath);
            }
          }

          selectAllGlyphsForNode();
        } else if (e.getClickCount() == 2) {
          Glyph glyph = getSelectedGlyph();
          if (glyph != null) {
            parentPanel.switchViews(glyph);
          }
        }
      }
    });


    showAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectAllGlyphsForNode();
      }
    });

    switchAndPoint.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Glyph glyph = getSelectedGlyph();
        if (glyph != null) {
          parentPanel.switchViews(glyph);
        }
      }
    });


    arrangeComponents();
  }

  public void pointGlyph(Glyph glyph) {
    ArrayList parents = new ArrayList();
    ArrayList pathList = new ArrayList();
    parents.add(tree.getModel().getRoot());
    //TreePath path = getPathFromRoot(parents);
    getPathFromRoot(glyph, parents, pathList);
    if (pathList.size() > 0) {
      TreePath path = new TreePath(pathList.toArray());
      tree.setSelectionPath(path);
    }
  }

  private boolean getPathFromRoot(Glyph goal, ArrayList parents, ArrayList path) {
    if (parents.size() == 0) {
      return false;
    }

    for (int i=0;i<parents.size();i++) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)parents.get(i);
      if (node.getUserObject() == goal) {
        constructPath(node, path);
        return true;
      } else {
        Enumeration enumer = node.children();
        while (enumer.hasMoreElements()) {
          parents.add(enumer.nextElement());
        }
      }
    }

    return getPathFromRoot(goal, parents, path);
  }

  private void constructPath(TreeNode node, ArrayList path) {
    path.add(0,node);
    node = node.getParent();
    while (node != null) {
      path.add(0,node);
      node = node.getParent();
    }
  }

  public void selectAllGlyphsForNode() {
    setCursor(GraphVizualizerPanel.waitCursor);

    Glyph parentGlyph = getSelectedGlyph();
    if (parentGlyph != null) {
      Node node = parentGlyph.getNode();

      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
      Enumeration enumer = root.breadthFirstEnumeration();
      while (enumer.hasMoreElements()) {
        DefaultMutableTreeNode kid = (DefaultMutableTreeNode)enumer.nextElement();
        Glyph kidGlyph = (Glyph)kid.getUserObject();
        Node kidNode = kidGlyph.getNode();

        if (kidNode == node) {
          kidGlyph.select();
        } else {
          kidGlyph.unselect();
        }
      }
      tree.repaint();
    }

    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public Glyph getSelectedGlyph() {
    Glyph glyph = null;

    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
    if (treeNode != null) {
      Object obj = treeNode.getUserObject();
      if (obj instanceof Glyph) {
        glyph = (Glyph)obj;
      }
    }

    return glyph;
  }

  private void arrangeComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    setLayout(gridbag);

    JScrollPane scroller = new JScrollPane(tree);
    JScrollPane scroller2 = new JScrollPane(area);

    JSplitPane spliter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            scroller, scroller2);
    spliter.setOneTouchExpandable(true);
    spliter.addComponentListener(new ResizedListener());

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 3;
    c.gridheight = 1;
    gridbag.setConstraints(spliter, c);
    add(spliter);

    JPanel empty = new JPanel();

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(empty, c);
    add(empty);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(switchAndPoint, c);
    add(switchAndPoint);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 2;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(showAll, c);
    add(showAll);
  }

  public void setModel(TreeModel model) {
    tree.setModel(model);
  }

  public void showNodeInfo(Node node) {
    area.setEditable(true);

    area.setText("");
    String[] info = node.getInfo();
    if (!node.isDummy() && info != null) {
      for (int i=0;i<info.length;i++) {
        area.append(info[i]+'\n');
      }
    }

    area.setEditable(false);
  }

  private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon selectedNode = null;
    private ImageIcon sherableNode = null;
    private ImageIcon nonsherableNode = null;
    private ImageIcon semiSherableNode = null;
    private ImageIcon nullNode = null;

    public MyTreeCellRenderer() {
      URL url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"blueDot.png");
      if (url != null) {
        selectedNode = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"greenDot.png");
      if (url != null) {
        sherableNode = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"redDot.png");
      if (url != null) {
        nonsherableNode = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"redRing.png");
      if (url != null) {
        semiSherableNode = new ImageIcon(url);
      }

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"grayDot.png");
      if (url != null) {
        nullNode = new ImageIcon(url);
      }
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,  boolean sel,
                                                  boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      Object obj = ((DefaultMutableTreeNode)value).getUserObject();
      if (!(obj instanceof Glyph)) {
        setIcon(nullNode);
        return this;
      }

      
      Glyph g = (Glyph)obj;
      this.setText(g.toHTMLString());
      if (g.isSelected()) {
        setIcon(selectedNode);
      } else if (g.getNode().isDummy()) {
        setIcon(sherableNode);
      } else if (g.getCountourColor().equals(Glyph.WARNING_CONTOUR_COLOR)) {
        setIcon(nonsherableNode);
      } else if (g.getCountourColor().equals(Glyph.HIGHLIGHTED_CONTOUR_COLOR)) {
        setIcon(semiSherableNode);
      } else {
        setIcon(sherableNode);
      }

      return this;
    }

  }

  private class ResizedListener extends ComponentAdapter {
    public void componentResized(ComponentEvent e) {
      JSplitPane splitPane = (JSplitPane)e.getComponent();

      int pos = splitPane.getWidth()-216;
      if (splitPane.getDividerLocation() < pos) {
        splitPane.setDividerLocation(pos);
      }
    }
  }
}
