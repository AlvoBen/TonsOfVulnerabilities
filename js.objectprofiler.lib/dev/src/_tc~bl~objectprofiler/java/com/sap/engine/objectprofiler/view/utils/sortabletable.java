package com.sap.engine.objectprofiler.view.utils;

import com.sap.engine.objectprofiler.view.GraphVizualizer;
import com.sap.engine.objectprofiler.view.Glyph;
import com.sap.engine.objectprofiler.view.GraphClient;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

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
 * Date: 2005-4-21
 * Time: 17:02:56
 */
public class SortableTable extends JTable {
  private SortableTableHeaderCellRenderer renderer = new SortableTableHeaderCellRenderer();
  private GraphVizualizer vizualizer = null;

  public SortableTable(SortableTableModel model, GraphVizualizer vizualizer) {
    super(model);

    this.vizualizer = vizualizer;

    SortableTableMouseListener listener = new SortableTableMouseListener(this);
    TableMouseListener listener2 = new TableMouseListener(this);

    getTableHeader().addMouseListener(listener);
    addMouseListener(listener2);

    TableColumnModel colModel = getColumnModel();
    for (int i=0;i<colModel.getColumnCount();i++) {
      getColumnModel().getColumn(i).setHeaderRenderer(renderer);
    }

  }

  public void setModel(TableModel model) {
    if (model instanceof SortableTableModel) {
      super.setModel(model);

      TableColumnModel colModel = getColumnModel();
      for (int i=0;i<colModel.getColumnCount();i++) {
        getColumnModel().getColumn(i).setHeaderRenderer(renderer);
      }
    }
  }

  public void pointClass(String className) {
     Glyph g = vizualizer.getVizualizerPanel().getGlyphWithType(className);
    if (g != null) {
      vizualizer.getVizualizerPanel().getCanvas().pointGlyph(g);
      vizualizer.setSelectedIndex(0);
      vizualizer.getVizualizerPanel().getCanvas().repaintCanvas();
    }
  }

  public void sort(int columnIndex) {
    SortableTableModel model = (SortableTableModel)getModel();

    int oldIndex = model.getSelectedColumnIndex();

    if (oldIndex == columnIndex) {
      model.alterSortOrder();
    } else {
      model.setSortOrder(0);
    }

    model.setSelectedColumnIndex(columnIndex);
    model.sort();
  }

  public void setPressedColumnIndex(int columnIndex) {
    renderer.setPressedColumnIndex(columnIndex);
  }

  public void setLastPressedColumnIndex(int columnIndex) {
    renderer.setLastSortedColumnIndex(columnIndex);
  }
}
