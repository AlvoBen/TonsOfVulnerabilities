package com.sap.engine.objectprofiler.view;

import java.awt.*;

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
 * Date: 2005-11-30
 * Time: 10:59:37
 */
public class Arc {
  private int x1 = 0;
  private int y1 = 0;

  private int x2 = 0;
  private int y2 = 0;

  private Color color = new Color(0,80,0);
  private Stroke stroke = DrawingArea2D.tick_stroke;

  private boolean isVisible = false;

  public Arc() {
  }

  public void setCoordinates(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean isVisible) {
    this.isVisible = isVisible;
  }

  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
  }

  public void draw(Graphics2D g) {
    Stroke oldStroke = g.getStroke();

    g.setStroke(stroke);
    g.setColor(color);
    g.drawLine(x1,y1,x2,y2);

    g.setStroke(oldStroke);
  }
}
