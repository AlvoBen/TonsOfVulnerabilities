package com.sap.engine.objectprofiler.view.utils;

import javax.swing.table.TableColumnModel;
import java.awt.event.MouseListener;
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
 * Date: 2005-6-30
 * Time: 20:04:01
 */
public class TableMouseListener implements MouseListener {
  private SortableTable table = null;

  public TableMouseListener(SortableTable table) {
    this.table = table;
  }

  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
      int row = table.getSelectedRow();
      if (row != -1) {
        String className = (String)table.getModel().getValueAt(row, 0);
        table.pointClass(className);
      }
    }
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }
}
