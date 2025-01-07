package com.sap.engine.objectprofiler.view.utils;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;

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
 * Time: 17:03:42
 */
public class SortableTableModel extends AbstractTableModel {
  public static final int SORT_ASC = 0;
  public static final int SORT_DESC = 1;

  private ArrayList rows = new ArrayList();
  private String[] colNames = null;
  private int sortOrder = SORT_ASC;

  private int selectedColumnIndex = -1;

  public SortableTableModel(Object[][] data, String[] colNames) {
    this.colNames = colNames;

    setData(data);
  }

  private void setData(Object[][] data) {
    for (int i=0;i<data.length;i++) {
      SortableRow row = new SortableRow(data[i]);

      rows.add(row);
    }
  }

  public Class getColumnClass(int index) {
    return ((SortableRow)rows.get(0)).getValue(index).getClass();
  }

  public String getColumnName(int index) {
    return colNames[index];
  }

  public int getColumnCount() {
    return colNames.length;
  }

  public int getRowCount() {
    return rows.size();
  }

  public Object getValueAt(int row, int column) {
    return ((SortableRow)rows.get(row)).getValue(column);
  }

  // specific methods

  public void setSelectedColumnIndex(int index) {
    selectedColumnIndex = index;
  }

  public int getSelectedColumnIndex() {
    return selectedColumnIndex;
  }

  public void setSortOrder(int order) {
    sortOrder = order;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public int alterSortOrder() {
    sortOrder = (sortOrder+1)%2;

    return sortOrder;
  }

  public void sort() {
    Collections.sort(rows);
    if (sortOrder == SORT_DESC) {
      Collections.reverse(rows);
    }

    fireTableDataChanged();
  }

  private class SortableRow implements Comparable {
    private Object[] columns = null;

    public SortableRow(Object[] col) {
      columns = col;
    }

    public int getColumnCount() {
      return columns.length;
    }

    public Object getValue(int index) {
      return columns[index];
    }

    public int compareTo(Object _row) {
      if (_row == null) {
        return 1;
      }

      if (_row == this) {
        return 0;
      }

      SortableRow row = (SortableRow)_row;

      Object o1 = columns[selectedColumnIndex];
      Object o2 = row.getValue(selectedColumnIndex);

      if (o1.getClass().equals(o2.getClass()) &&
          o1 instanceof Comparable) {
        return ((Comparable)o1).compareTo(o2);

      }

      return 0;
    }
  }
}
