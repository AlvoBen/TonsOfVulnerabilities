package com.sap.engine.objectprofiler.view.utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
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
 * Date: 2005-4-22
 * Time: 10:23:53
 */
public class Arrows {
  public static int UP = 0;
  public static int DOWN = 1;

  private BufferedImage upArrow = null;
  private BufferedImage downArrow = null;

  private ImageIcon upArrowIcon = null;
  private ImageIcon downArrowIcon = null;

  private static int SIZE = 10;

  private static Color baseColor = UIManager.getColor("control");
  private static Color shadowColor = UIManager.getColor("controlShadow");
  private static Color highlightColor = UIManager.getColor("controlHighlight");

  public Arrows() {
    initImages();
    drawArrows();

    initIcons();
  }

  private void initImages() {
    upArrow = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
    downArrow = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
  }

  private void initIcons() {
    upArrowIcon = new ImageIcon(upArrow);
    downArrowIcon = new ImageIcon(downArrow);
  }

  private void drawArrows() {
    drawUpArrow();
    drawDownArrow();
  }

  private void drawUpArrow() {
    Graphics2D g = (Graphics2D)upArrow.getGraphics();

    g.setColor(baseColor);
    g.fillRect(0, 0, SIZE, SIZE);

    g.setColor(highlightColor);
    g.drawLine(0, SIZE-1, SIZE-1, SIZE-1);
    g.drawLine(SIZE-1, SIZE-1, SIZE/2, 0);

    g.setColor(shadowColor);
    g.drawLine(0, SIZE-1, SIZE/2, 0);
  }

  private void drawDownArrow() {
    Graphics2D g = (Graphics2D)downArrow.getGraphics();

    g.setColor(baseColor);
    g.fillRect(0, 0, SIZE, SIZE);

    g.setColor(shadowColor);
    g.drawLine(0, 0, SIZE-1, 0);
    g.drawLine(0, 0, SIZE/2, SIZE-1);

    g.setColor(highlightColor);
    g.drawLine(SIZE/2, SIZE-1, SIZE-1, 0);
    g.setColor(baseColor);
  }

  public BufferedImage getArrow(int style) {
    if (style == UP) {
      return upArrow;
    } else {
      return downArrow;
    }
  }

  public ImageIcon getArrowIcon(int style) {
    if (style == UP) {
      return upArrowIcon;
    } else {
      return downArrowIcon;
    }
  }
}
