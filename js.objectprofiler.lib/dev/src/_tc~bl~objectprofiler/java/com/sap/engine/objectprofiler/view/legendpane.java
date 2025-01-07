package com.sap.engine.objectprofiler.view;

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
 * Date: 2005-4-15
 * Time: 10:52:45
 */
public class LegendPane extends JPanel {
  private Legend legend = null;

  public LegendPane() {
    legend = new Legend();

    //setPreferredSize(new Dimension(202, 800));
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    legend.draw(0,0,(Graphics2D)g);
  }
}
