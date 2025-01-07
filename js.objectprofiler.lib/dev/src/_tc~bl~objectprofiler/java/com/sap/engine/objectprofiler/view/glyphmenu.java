package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.controller.PathFinder;
import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.dialogs.ExcludeFieldsWindow;
import com.sap.engine.objectprofiler.view.dialogs.WatchListWindow;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
 * Date: 2005-3-17
 * Time: 11:40:11
 */
public class GlyphMenu extends JPopupMenu {
  private GraphVizualizerPanel viz = null;
  private HashMap menuItems = new HashMap();
  private Glyph glyph = null;

  private JMenuItem addToWatchList = null;
  private JMenuItem menuItemHide = null;
  private JMenuItem menuItemAlterSelection = null;
  private JMenuItem menuItemAlterExpansion = null;
  private JMenuItem menuExpandAll = null;
  private JMenuItem switchAndSelect = null;
  private JMenuItem fieldsFilter = null;
  private JMenuItem menuWatchList = null;

  public GlyphMenu(GraphVizualizerPanel viz) {
    super();

    this.viz = viz;
    buildMenu();
  }

  public void setGlyph(Glyph g) {
    glyph = g;

    Node node = g.getNode();
    if (node != null) {
      fieldsFilter.setEnabled(!node.isDummy());
      menuItemAlterSelection.setEnabled(!node.isDummy());
      Iterator iter = menuItems.keySet().iterator();
      while (iter.hasNext()) {
        JMenuItem item = (JMenuItem)iter.next();
        item.setEnabled(!node.isDummy());
      }
    }

    if (g == viz.getRoot()) {
      menuItemHide.setEnabled(false);
    } else {
      menuItemHide.setEnabled(true);
    }

    if (g.isSelected()) {
      menuItemAlterSelection.setText("Hide Details");
    } else {
      menuItemAlterSelection.setText("Show Details");
    }

    if (g.hasChildren()) {
      menuItemAlterExpansion.setEnabled(true);
      menuExpandAll.setEnabled(true);
      if (g.isExpanded()) {
        menuItemAlterExpansion.setText("Collapse");
      } else {
        menuItemAlterExpansion.setText("Expand");
      }
    } else {
      menuItemAlterExpansion.setText("Expand/Collapse");
      menuItemAlterExpansion.setEnabled(false);
      menuExpandAll.setEnabled(false);
    }
  }

  private void buildMenu() {
    ArrayList pathFinders = viz.getPathFinders();
    PathFinderActionListener listener = new PathFinderActionListener();

    for (int i = 0; i < pathFinders.size(); i++) {
      PathFinder finder = (PathFinder) pathFinders.get(i);
      String menuItemName = finder.toString();

      JMenuItem menuItem = new JMenuItem(menuItemName);
      add(menuItem);
      menuItems.put(menuItem, finder);
      menuItem.addActionListener(listener);
    }

    if (pathFinders.size() > 0) {
      add(new JSeparator());
    }

    menuItemAlterSelection = new JMenuItem("Show details");
    add(menuItemAlterSelection);
    menuItemAlterSelection.addActionListener(listener);

    menuItemAlterExpansion = new JMenuItem("Expand");
    add(menuItemAlterExpansion);
    menuItemAlterExpansion.addActionListener(listener);

    menuExpandAll = new JMenuItem("Expand All");
    add(menuExpandAll);
    menuExpandAll.addActionListener(listener);

    menuItemHide = new JMenuItem("Hide");
    add(menuItemHide);
    menuItemHide.addActionListener(listener);

    switchAndSelect = new JMenuItem("Switch & Select");
    add(switchAndSelect);
    switchAndSelect.addActionListener(listener);

    addToWatchList = new JMenuItem("Add To Watch List");
    add(addToWatchList);
    addToWatchList.addActionListener(listener);

    addSeparator();

    menuWatchList = new JMenuItem("Show Watch List");
    add(menuWatchList);
    menuWatchList.addActionListener(listener);

    fieldsFilter = new JMenuItem("Fields Filtering...");
    add(fieldsFilter);
    fieldsFilter.addActionListener(listener);
  }


  private void doFinder(JMenuItem item) {
    PathFinder finder = (PathFinder) menuItems.get(item);
    viz.setCursor(GraphVizualizerPanel.waitCursor);

    ArrayList paths = finder.findPath(viz.getRoot().getNode(), glyph.getNode());
    viz.getCanvas().highlightPaths(paths);

    viz.setCursor(GraphVizualizerPanel.defaultCursor);

    viz.getCanvas().repaintCanvas();
  }

  private void showFieldsWindow(Node node) {
    if (!node.isDummy()) {
      ExcludeFieldsWindow window = new ExcludeFieldsWindow(viz, node);
      window.showDialog();
    }
  }

  private void showWatchListWindow() {
    WatchListWindow window = WatchListWindow.getInstance(viz);
    window.showDialog();
  }

  private void addToWatchList(Glyph glyph) {
    viz.getConfiguration().addGlyphToWatchList(glyph);
  }

  private class PathFinderActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JMenuItem item = (JMenuItem) e.getSource();
      if (menuItems.keySet().contains(item)) {
        doFinder((JMenuItem) e.getSource());
      } else {
        if (item == menuItemHide) {
          viz.getCanvas().hideIt(glyph);
        } else if (item == menuItemAlterExpansion) {
          viz.getCanvas().alterExpansion(glyph);
        } else if (item == menuItemAlterSelection) {
          viz.getCanvas().alterSelection(glyph);
        } else if (item == menuExpandAll) {
          viz.getCanvas().expandAll(glyph);
        } else if (item == switchAndSelect) {
          viz.switchViews(glyph);
        } else if (item == fieldsFilter) {
          showFieldsWindow(glyph.getNode());
        } else if (item == addToWatchList) {
          addToWatchList(glyph);
        } else if (item == menuWatchList) {
          showWatchListWindow();
        }
      }
    }
  }
}
