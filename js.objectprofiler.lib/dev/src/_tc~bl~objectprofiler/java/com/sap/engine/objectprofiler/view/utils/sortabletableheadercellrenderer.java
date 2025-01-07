package com.sap.engine.objectprofiler.view.utils;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;

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
 * Time: 17:05:52
 */
public class SortableTableHeaderCellRenderer extends DefaultTableCellRenderer {
  private int pressedColumnIndex = -1;
  private int lastSortedColumnIndex = -1;

  private static Arrows arrows = new Arrows();

  private static int GAP = 8;

  public void setPressedColumnIndex(int index) {
    pressedColumnIndex = index;
  }

  public void setLastSortedColumnIndex(int index) {
    lastSortedColumnIndex = index;
  }

  public Component getTableCellRendererComponent(JTable table,
                                                 Object value, boolean isSelected, boolean hasFocus, int row,
                                                 int col) {

    setHorizontalAlignment(CENTER);

    setHorizontalTextPosition(LEADING);
    setIconTextGap(GAP);
    setFont(getFont().deriveFont(Font.BOLD));
    setText(value.toString());
    setIcon(null);

    if (pressedColumnIndex == col) {
      setBorder(BorderFactory.createLoweredBevelBorder());
    } else {
      setBorder(BorderFactory.createRaisedBevelBorder());
    }

    if (lastSortedColumnIndex == col) {
      SortableTable stable = (SortableTable)table;
      SortableTableModel model = (SortableTableModel)stable.getModel();
      int sortOrder = model.getSortOrder();

      if (sortOrder == SortableTableModel.SORT_ASC) {
        setText(value.toString());
        setIcon(arrows.getArrowIcon(Arrows.UP));
      } else {
        setText(value.toString());
        setIcon(arrows.getArrowIcon(Arrows.DOWN));
      }
    }

    return this;
  }
}
