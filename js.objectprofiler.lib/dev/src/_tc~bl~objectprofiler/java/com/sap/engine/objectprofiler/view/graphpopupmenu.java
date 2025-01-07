package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.controller.MemoryCalculator;
import com.sap.engine.objectprofiler.controller.WeightInfo;
import com.sap.engine.objectprofiler.view.dialogs.ExcludeFieldsWindow;
import com.sap.engine.objectprofiler.view.dialogs.WatchListWindow;

import javax.swing.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
 * Date: 2005-3-21
 * Time: 10:58:32
 */
public class GraphPopupMenu extends JPopupMenu {
  private GraphVizualizerPanel viz = null;
  private HashMap menuItems = new HashMap();

  private JMenuItem menuAlterLegend = null;
  private JMenuItem menuRemoveSelections = null;
  private JMenuItem menuRefresh = null;
  private JMenuItem menuCalcWeights = null;
  private JMenuItem menuWatchList = null;

  private JMenu menuLabelStyle = null;
  private JRadioButtonMenuItem menuLabelID = null;
  private JRadioButtonMenuItem menuLabelSize = null;
  private JRadioButtonMenuItem menuLabelPercent = null;

  private JMenu menuNodeArrangement = null;
  private JRadioButtonMenuItem menuNodeArrangementClassic = null;
  private JRadioButtonMenuItem menuNodeArrangementSave = null;

  private JMenuItem menuSaveAsImage = null;

  private ButtonGroup groupMemCalc = null;
  private ButtonGroup groupLabelStyle = null;
  private ButtonGroup groupNodeArrangement = null;

  private JMenuItem fieldsFilter = null;

  public GraphPopupMenu(GraphVizualizerPanel viz)  {
    super();

    this.viz = viz;
    buildMenuItems();
  }

  public void setVisible(boolean b) {
    if (b == true) {
//      if (viz.isLegendVisible()) {
//        menuAlterLegend.setText("Hide Legend");
//      } else {
//        menuAlterLegend.setText("Show Legend");
//      }


      if (viz.getCanvas().getNodeArrangement() == GraphVizualizerCanvas.NODE_ARRANGEMENT_CLASSIC) {
        menuNodeArrangementClassic.setSelected(true);
      } else {
        menuNodeArrangementSave.setSelected(true);
      }

      if (viz.getConfiguration().getLabelStyle() == Configuration.LABEL_STYLE_ID) {
        menuLabelID.setSelected(true);
      } else if (viz.getConfiguration().getLabelStyle() == Configuration.LABEL_STYLE_WEIGHT) {
        menuLabelSize.setSelected(true);
      } else if (viz.getConfiguration().getLabelStyle() == Configuration.LABEL_STYLE_PERCENT) {
        menuLabelPercent.setSelected(true);
      }
    }

    super.setVisible(b);
  }

  private void buildMenuItems() {
    MemoryCalculatorActionListener listener = new MemoryCalculatorActionListener();

    ArrayList memoryCalculators = new ArrayList();//viz.getMemoryCalculators();
    groupMemCalc = new ButtonGroup();

    for (int i = 0; i < memoryCalculators.size(); i++) {
      MemoryCalculator calculator = (MemoryCalculator) memoryCalculators.get(i);
      String menuItemName = calculator.toString();

      JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(menuItemName);
      add(menuItem);
      if (i == 0) {
        menuItem.setSelected(true);
      }
      menuItem.addActionListener(listener);
      groupMemCalc.add(menuItem);

      menuItems.put(menuItem, calculator);
    }

    if (memoryCalculators.size() > 0) {
      add(new JSeparator());
    }

    menuCalcWeights = new JMenuItem("Calculate Weights");
    //add(menuCalcWeights);
    menuCalcWeights.addActionListener(listener);

    groupLabelStyle = new ButtonGroup();

    menuLabelStyle = new JMenu("Label Style");
    add(menuLabelStyle);

    menuLabelID = new JRadioButtonMenuItem("Node ID");
    menuLabelStyle.add(menuLabelID);
    menuLabelID.addActionListener(listener);
    menuLabelID.setSelected(true);
    groupLabelStyle.add(menuLabelID);

    menuLabelSize = new JRadioButtonMenuItem("Weight");
    menuLabelStyle.add(menuLabelSize);
    menuLabelSize.addActionListener(listener);
    menuLabelSize.setSelected(false);
    groupLabelStyle.add(menuLabelSize);

    menuLabelPercent = new JRadioButtonMenuItem("Percent");
    menuLabelStyle.add(menuLabelPercent);
    menuLabelPercent.addActionListener(listener);
    menuLabelPercent.setSelected(false);
    groupLabelStyle.add(menuLabelPercent);

    groupNodeArrangement = new ButtonGroup();

    menuNodeArrangement = new JMenu("Node Arrangement Method");
    add(menuNodeArrangement);
    //menuNodeArrangement.setEnabled(false);

    menuNodeArrangementClassic = new JRadioButtonMenuItem("Classic");
    menuNodeArrangement.add(menuNodeArrangementClassic);
    menuNodeArrangementClassic.addActionListener(listener);
    menuNodeArrangementClassic.setSelected(true);
    groupNodeArrangement.add(menuNodeArrangementClassic);

    menuNodeArrangementSave = new JRadioButtonMenuItem("Space Saving");
    menuNodeArrangement.add(menuNodeArrangementSave);
    menuNodeArrangementSave.addActionListener(listener);
    menuNodeArrangementSave.setSelected(false);
    groupNodeArrangement.add(menuNodeArrangementSave);


    menuAlterLegend = new JMenuItem("Show/Hide Legend");
    //add(menuAlterLegend);
    //menuAlterLegend.addActionListener(listener);

    menuRemoveSelections = new JMenuItem("Remove Selections");
    add(menuRemoveSelections);
    menuRemoveSelections.addActionListener(listener);

    menuSaveAsImage = new JMenuItem("Save As Image");
    add(menuSaveAsImage);
    menuSaveAsImage.addActionListener(listener);

    menuRefresh = new JMenuItem("Refresh");
    add(menuRefresh);
    menuRefresh.addActionListener(listener);

    addSeparator();

    menuWatchList = new JMenuItem("Show Watch List");
    add(menuWatchList);
    menuWatchList.addActionListener(listener);

    fieldsFilter = new JMenuItem("Fields Filtering...");
    add(fieldsFilter);
    fieldsFilter.addActionListener(listener);
  }

  private void doCalculator(JMenuItem item) {
    MemoryCalculator calculator = (MemoryCalculator) menuItems.get(item);

    WeightInfo info = calculator.getWeight();

    //viz.applyWeight(info);
  }

  private void showFieldsWindow() {
    ExcludeFieldsWindow window = new ExcludeFieldsWindow(viz, null);
    window.showDialog();
  }

  private void showWatchListWindow() {
    WatchListWindow window = WatchListWindow.getInstance(viz);
    window.showDialog();
  }


  private class MemoryCalculatorActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JMenuItem item = (JMenuItem) e.getSource();
      if (menuItems.keySet().contains(item)) {
        doCalculator((JMenuItem) e.getSource());
      } else {
        if (item == menuAlterLegend) {
          viz.getCanvas().alterLegendVisibility();
        } else if (item == menuRemoveSelections) {
          viz.getCanvas().removeHighlights();
        } else if (item == menuRefresh) {
          viz.getCanvas().repaintCanvas();
        } else if (item == menuLabelID) {
          viz.getConfiguration().setLabelStyle(Configuration.LABEL_STYLE_ID);
        } else if (item == menuLabelSize) {
          viz.getConfiguration().setLabelStyle(Configuration.LABEL_STYLE_WEIGHT);
        } else if (item == menuLabelPercent) {
          viz.getConfiguration().setLabelStyle(Configuration.LABEL_STYLE_PERCENT);
        } else if (item == menuCalcWeights) {
          //viz.getCanvas().calculateWeights();
        } else if (item == menuNodeArrangementClassic) {
          viz.getCanvas().setNodeArrangement(GraphVizualizerCanvas.NODE_ARRANGEMENT_CLASSIC);
        } else if (item == menuNodeArrangementSave) {
          viz.getCanvas().setNodeArrangement(GraphVizualizerCanvas.NODE_ARRANGEMENT_SAVE_SPACE);
        } else if (item == menuSaveAsImage) {
          viz.getCanvas().saveAsImage();
        } else if (item == fieldsFilter) {
          showFieldsWindow();
        } else if (item == menuWatchList) {
          showWatchListWindow();
        }
      }
    }
  }
}
