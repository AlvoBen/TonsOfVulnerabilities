package com.sap.engine.objectprofiler.view.utils;

import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.GraphReport;
import com.sap.engine.objectprofiler.view.GraphVizualizer;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;
import com.sap.engine.objectprofiler.view.Configuration;
import com.sap.engine.objectprofiler.view.dialogs.ReportWindow;
import com.sap.engine.objectprofiler.interfaces.SessionProperties;

import javax.swing.*;
import javax.swing.tree.*;
import javax.naming.NamingException;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
public class SessionsTree extends JPanel {
  private SessionBrowser sessionBrowser = null;

  private JTree tree = new JTree();
  private JButton getReport = new JButton("Get Report");
  private JButton load = new JButton("Load");
  private JButton refresh = new JButton("Refresh");

  private GraphVizualizer vizualizer = null;

  public SessionsTree(GraphVizualizer viz, String[] connectionProps) {
    vizualizer = viz;

    if (connectionProps != null && connectionProps.length > 0) {
      try {
        sessionBrowser = new SessionBrowser(connectionProps);
      } catch (Exception e) {
        e.printStackTrace();
        GraphVizualizer.showMessageBox(e.getMessage());
      }
    }

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

    getReport.setPreferredSize(bsize);
    getReport.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getReport();
      }
    });

    refresh.setPreferredSize(bsize);
    refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refreshModel();
      }
    });

    arrangeComponents();
  }

  public void reconnect(String args[]) throws NamingException {
    sessionBrowser = null;
    sessionBrowser = new SessionBrowser(args);
  }

  private void arrangeComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    setLayout(gridbag);

    JScrollPane scroller = new JScrollPane(tree);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 4;
    c.gridheight = 1;
    gridbag.setConstraints(scroller, c);
    add(scroller);

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
    gridbag.setConstraints(refresh, c);
    add(refresh);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 2;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(load, c);
    add(load);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 3;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(getReport, c);
    add(getReport);
  }

  public void refreshModel() {
    setCursor(GraphVizualizerPanel.waitCursor);

    DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(SessionProperties.EMPTY_STRING));

    if (sessionBrowser != null) {
      try {
        treeModel = sessionBrowser.buildTreeModel();
      } catch (Exception e) {
        e.printStackTrace();
        GraphVizualizer.showMessageBox(e);
      }
    }

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

  public void getReport() {
    setCursor(GraphVizualizerPanel.waitCursor);
    SessionProperties info = getSelectedSession();
    GraphReport report = null;
    if (info != null) {
      try {
        report = sessionBrowser.getReport(info.getPath());
      } catch (Exception e) {
        e.printStackTrace();
        GraphVizualizer.showMessageBox(e);
      }
      setCursor(GraphVizualizerPanel.defaultCursor);
      if (report != null) {
        showReport(report);
      }
    }
  }

  public void showReport(GraphReport report) {
    ReportWindow rw = new ReportWindow(vizualizer.getVizualizerPanel(), report);
    rw.showDialog();
  }

  public Graph getGraph() {
    Graph graph = null;
    SessionProperties info = getSelectedSession();

    if (info != null) {
      try {
        Configuration conf = vizualizer.getVizualizerPanel().getConfiguration();
        graph = sessionBrowser.getSessionGraph(info.getPath(), conf.getLevel(),
                                               conf.getGraphFilters(),
                                               conf.getIncludeTransients(),
                                               conf.getOnlyNonshareable());
      } catch (Exception e) {
        e.printStackTrace();
        GraphVizualizer.showMessageBox(e);
      }
    }

    return graph;
  }

  public SessionProperties getSelectedSession() {
    SessionProperties result = null;

    TreePath currentSelection = tree.getSelectionPath();
    if (currentSelection != null) {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
      SessionProperties info = (SessionProperties)currentNode.getUserObject();
      if (info.getType() == SessionProperties.TYPE_SESSION) {
         result = info;
      }
    }

    return result;
  }

  private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon red = null;
    private ImageIcon blue = null;
    private ImageIcon green = null;
    private ImageIcon yellow = null;
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

      url = ClassLoader.getSystemResource(GraphVizualizer.imagesDir+"yellowDot.png");
      if (url != null) {
        yellow = new ImageIcon(url);
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
      if (!(obj instanceof SessionProperties)) {
        setIcon(gray);
        return this;
      }

      SessionProperties info = (SessionProperties)((DefaultMutableTreeNode)value).getUserObject();

      if (info.getType() == SessionProperties.TYPE_SESSION_CONTEXT) {
        setToolTipText(null);
        setIcon(red);
      } else if (info.getType() == SessionProperties.TYPE_DOMAIN) {
        setToolTipText(null);
        setIcon(blue);
      } else if (info.getType() == SessionProperties.TYPE_SESSION){
        setToolTipText(info.getDateTime());
        setIcon(green);
      } else {
        setToolTipText(null);
        setIcon(yellow);
      }

      return this;
    }

  }
}
