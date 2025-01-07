package com.sap.engine.objectprofiler.view.utils;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

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
 * Time: 17:06:06
 */
public class SortableTableMouseListener implements MouseListener {
  private SortableTable table = null;

  public SortableTableMouseListener(SortableTable table) {
    this.table = table;
  }

  public void mouseClicked(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
    int column = table.convertColumnIndexToModel(viewColumn);
    if (column != -1) {
      //System.out.println("Sorting "+column+" view column = "+viewColumn);
      table.sort(column);
    }
  }

  public void mousePressed(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    int viewColumn = columnModel.getColumnIndexAtX(e.getX());

    table.setPressedColumnIndex(viewColumn);
    table.getTableHeader().repaint();
  }

  public void mouseReleased(MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel();
    int viewColumn = columnModel.getColumnIndexAtX(e.getX());

    table.setPressedColumnIndex(-1);
    table.setLastPressedColumnIndex(viewColumn);

    table.getTableHeader().repaint();
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }
}
