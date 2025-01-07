package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.Glyph;
import com.sap.engine.objectprofiler.view.GraphVizualizerCanvas;

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
 * Date: 2005-3-18
 * Time: 11:00:29
 */
public class Legend {
  public static final Color FRAME_BORDER_COLOR = Color.BLUE;
  public static final Color FRAME_COLOR = new Color(0, 200, 200, 200);
  public static final Color TEXT_COLOR = Color.BLACK;

  public static final int MARGIN = 5;

  private Glyph normalGlyph = null;
  private Glyph compoundGlyph = null;
  private Glyph repeatedGlyph = null;
  private Glyph nonshareableGlyph = null;
  private Glyph semishareableGlyph = null;
  private Glyph arrayGlyph = null;
  private Glyph dummyGlyph = null;

  public Legend() {
    initPrimitives();
  }

  private void initPrimitives() {
    normalGlyph = new Glyph();

    compoundGlyph = new Glyph();
    compoundGlyph.setShape(Glyph.SHAPE_SQUARE);

    repeatedGlyph = new Glyph();
    repeatedGlyph.setGlyphColor(Glyph.HIGHLIGHTED_GLYPH_COLOR);

    semishareableGlyph = new Glyph();
    semishareableGlyph.setContourColor(Glyph.HIGHLIGHTED_CONTOUR_COLOR);
    semishareableGlyph.setShape(Glyph.SHAPE_SQUARE);

    nonshareableGlyph = new Glyph();
    nonshareableGlyph.setContourColor(Glyph.WARNING_CONTOUR_COLOR);
    //nonshareableGlyph.setGlyphColor(Glyph.WARNING_GLYPH_COLOR);


    arrayGlyph = new Glyph();
    arrayGlyph.setCompound(true);

    dummyGlyph = new Glyph();
    dummyGlyph.setGlyphColor(Glyph.DUMMY_GLYPH_COLOR);
  }

  public void draw(int x, int y, Graphics2D g) {
    Font oldFont = g.getFont();
    float size = (float)oldFont.getSize();
    Font newFont = oldFont.deriveFont(Font.BOLD, size);
    g.setFont(newFont);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
    g.setStroke(new BasicStroke(2));

    //drawFrame(x, y, g);
    drawCaption(x, y, g);

    normalGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 20 + MARGIN + Glyph.RADIUS);
    compoundGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 20 + 2 * MARGIN + 3 * Glyph.RADIUS);
    repeatedGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 20 + 3 * MARGIN + 5 * Glyph.RADIUS);
    nonshareableGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 20 + 4 * MARGIN + 7 * Glyph.RADIUS);
    semishareableGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 20 + 5 * MARGIN + 9 * Glyph.RADIUS);
    arrayGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 25 + 6 * MARGIN + 11 * Glyph.RADIUS);
    dummyGlyph.setXY(x + MARGIN + Glyph.RADIUS, y + 25 + 7 * MARGIN + 13 * Glyph.RADIUS);

    normalGlyph.drawGlyph(g);
    normalGlyph.drawLabel(g);

    compoundGlyph.drawGlyph(g);
    compoundGlyph.drawLabel(g);

    repeatedGlyph.drawGlyph(g);
    repeatedGlyph.drawLabel(g);

    nonshareableGlyph.drawGlyph(g);
    nonshareableGlyph.drawLabel(g);

    semishareableGlyph.drawGlyph(g);
    semishareableGlyph.drawLabel(g);

    compoundGlyph.drawGlyph(g);
    compoundGlyph.drawLabel(g);

    arrayGlyph.drawGlyph(g);
    arrayGlyph.drawLabel(g);

    dummyGlyph.setLabel("-1");
    dummyGlyph.drawGlyph(g);
    dummyGlyph.drawLabel(g);

    g.setColor(Glyph.PATH_COLOR);
    g.drawLine(x + MARGIN, y + 20 + 8 * MARGIN + 15 * Glyph.RADIUS, x + MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 15 * Glyph.RADIUS);
    g.setColor(Glyph.WARNING_PATH_COLOR);
    g.drawLine(x + MARGIN, y + 20 + 8 * MARGIN + 17 * Glyph.RADIUS, x + MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 17 * Glyph.RADIUS);
    g.setColor(Glyph.HIGHLIGHTED_PATH_COLOR);
    g.drawLine(x + MARGIN, y + 20 + 8 * MARGIN + 19 * Glyph.RADIUS, x + MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 19 * Glyph.RADIUS);
    g.setColor(Glyph.PATH_COLOR);
    g.setStroke(DrawingArea2D.dashed_stroke);
    g.drawLine(x + MARGIN, y + 20 + 8 * MARGIN + 21 * Glyph.RADIUS, x + MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 21 * Glyph.RADIUS);


    g.setColor(TEXT_COLOR);
    g.drawString("Node without children", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + MARGIN + Glyph.RADIUS + 3);
    g.drawString("Node with children", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + 2 * MARGIN + 3 * Glyph.RADIUS + 3);
    g.drawString("Already referenced node", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 +  3 * MARGIN + 5 * Glyph.RADIUS + 3);
    g.drawString("Non-shareable node", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 +  4 * MARGIN + 7 * Glyph.RADIUS + 3);
    g.drawString("Node has non-shareable kid", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 +  5 * MARGIN + 9 * Glyph.RADIUS + 3);
    g.drawString("Compound node", x + 3 * MARGIN + Glyph.DIAMETER, y + 25 +  6 * MARGIN + 11 * Glyph.RADIUS + 3);
    g.drawString("Dummy node", x + 3 * MARGIN + Glyph.DIAMETER, y + 25 +  7 * MARGIN + 13 * Glyph.RADIUS + 3);

    g.drawString("Reference", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 15 * Glyph.RADIUS + 4);
    g.drawString("Transient Reference", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 17 * Glyph.RADIUS + 4);
    g.drawString("Highlighted path", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 19 * Glyph.RADIUS + 4);
    g.drawString("Unexplored Node Reference", x + 3 * MARGIN + Glyph.DIAMETER, y + 20 + 8 * MARGIN + 21 * Glyph.RADIUS + 4);

    g.setFont(oldFont);
  }

  private void drawFrame(int x, int y, Graphics2D g) {
    g.setColor(FRAME_COLOR);
    g.fillRoundRect(x + 1, y + 1, Glyph.DIAMETER * 4 + 100, 40 + 11 * MARGIN + 3 * Glyph.DIAMETER, 20, 20);

    g.setColor(FRAME_BORDER_COLOR);
    g.drawRoundRect(x + 1, y + 1, Glyph.DIAMETER * 4 + 100, 40 + 11 * MARGIN + 3 * Glyph.DIAMETER, 20, 20);
  }

  private void drawCaption(int x, int y, Graphics2D g) {
    g.setColor(Color.BLACK);
    g.fillRect(x + 2, y + 2, 200, 20);

    g.setColor(Color.YELLOW);
    g.drawString("LEGEND", 6, 17);
  }
}
