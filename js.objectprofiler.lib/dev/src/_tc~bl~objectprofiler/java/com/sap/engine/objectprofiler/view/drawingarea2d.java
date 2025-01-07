package com.sap.engine.objectprofiler.view;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.VolatileImage;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

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
 * Date: 2005-4-19
 * Time: 14:37:33
 */
public class DrawingArea2D extends DrawingArea {
  public static final Font font = new Font("Courier", Font.PLAIN,  (2*Glyph.DIAMETER)/5);
  public static final Font small_font = new Font("Courier", Font.PLAIN,  (2*Glyph.DIAMETER)/5);

  public static final Stroke tick_stroke = new BasicStroke(2);
  public static final Stroke thin_stroke = new BasicStroke(1);
  public static final BasicStroke dashed_stroke = new BasicStroke(2,
                                                                  BasicStroke.CAP_BUTT,
                                                                  BasicStroke.JOIN_MITER,
                                                                  10.0f,
                                                                  new float[] {5,3},
                                                                  0);
  public static final Color SELECTION_LINE_COLOR = Color.BLUE;
  public static final Color DELTA_TOOLTIP_BACKGROUND = Color.CYAN;
  public static final Color DELTA_TOOLTIP_FOREGROUND = Color.BLUE;

  private static int xp[] = new int[] {0, Glyph.DIAMETER, Glyph.DIAMETER/2};
  private static int yp[] = new int[] {0, 0, Glyph.DIAMETER};

  public static final Polygon TRIANGLE_SHAPE = new Polygon(xp, yp, 3);

  public DrawingArea2D(GraphVizualizerCanvas viz) {
    super(viz);
  }

  protected void drawGraph(Graphics2D g2, ArrayList glyphs, HashSet arcs) {
    Iterator iterat = arcs.iterator();
    while (iterat.hasNext()) {
      Arc arc = (Arc)iterat.next();
      arc.draw(g2);
    }

    g2.setStroke(tick_stroke);
    g2.setFont(font);


    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);
      glyph.draw(g2);
    }

    g2.setStroke(thin_stroke);
    g2.setFont(small_font);

    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);
      glyph.drawMetaData(g2);
    }

    if (viz.isDeltaVisible()) {
      drawDelta(g2);
    }

    g2.setStroke(thin_stroke);
    g2.setFont(small_font);

    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);
      glyph.drawTooltip(g2);
    }

    g2.setStroke(tick_stroke);
    if (viz.isSelectionPerforming()) {
      drawSelection(g2);
    }
  }

  private void drawSelection(Graphics2D g) {
    g.setColor(SELECTION_LINE_COLOR);

    int selection[][] = viz.getSelectionRectangle();

    g.fillRect(selection[0][0]-3, selection[0][1]-3, 6, 6);
    g.drawLine(selection[0][0], selection[0][1], selection[1][0], selection[1][1]);
    g.fillRect(selection[1][0]-2, selection[1][1]-2, 4, 4);
  }

  private void drawDelta(Graphics2D g) {
    String delta = viz.getDeltaString();
    int coor[] = viz.getDeltaCoordinates();

    Stroke stroke = g.getStroke();
    g.setStroke(thin_stroke);

    FontMetrics metrics = g.getFontMetrics();
    Rectangle2D rect = metrics.getStringBounds(delta,g);
    int w = (int)rect.getWidth();
    int h = (int)rect.getHeight();

    g.setColor(DELTA_TOOLTIP_BACKGROUND);
    g.fillRect(coor[0], coor[1] - h, w+1, h+3);
    g.setColor(DELTA_TOOLTIP_FOREGROUND);
    g.drawRect(coor[0], coor[1] - h, w+1, h+3);

    g.drawString(delta, coor[0], coor[1]);

    g.setStroke(stroke);
  }
}
