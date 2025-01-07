package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.view.Glyph;

import java.util.ArrayList;
import java.util.Collections;
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
 * Date: 2005-3-14
 * Time: 16:40:32
 */
public class GlyphRow {
  public static final int MIN_DISTANCE = 10;
  public static final int ROW_HEIGHT = Glyph.DIAMETER * 2;

  private int rowIndex = 0;
  private int numVisibleGlyphs = 0;

  private int h1 = 0;
  private int h2 = 0;

  private ArrayList glyphs = new ArrayList();

  private GraphVizualizerCanvas viz = null;

  public GlyphRow(GraphVizualizerCanvas viz, int rowIndex) {
    this.viz = viz;
    this.rowIndex = rowIndex;

    h1 = rowIndex * ROW_HEIGHT;
    h2 = h1 + ROW_HEIGHT;
  }

  public ArrayList getGlyphs() {
    return glyphs;
  }

  public int getRowIndex() {
    return h1/ROW_HEIGHT;
  }

  public void addGlyph(Glyph glyph) {
    glyphs.add(glyph);
  }

  public void removeGlyph(Glyph glyph) {
    removeGlyph(glyph);
  }

  public int getNumVisibleGlyphs() {
    return numVisibleGlyphs;
  }

  public int decNumVisibleGlyphs() {
    return --numVisibleGlyphs;
  }

  public int incNumVisibleGlyphs() {
    ++numVisibleGlyphs;
    viz.adjustMaxVisibleGlyphsInARow(numVisibleGlyphs);
    //viz.adjustHeight(rowIndex);

    return numVisibleGlyphs;
  }

  public boolean isClicked(int x, int y) {
    boolean res = false;

    if (y >= h1 && y < h2) {
      res = true;
    }

    return res;
  }

  public Glyph locateGlyph(int x, int y) {
    for (int i = 0; i < glyphs.size(); i++) {
      Glyph g = (Glyph) glyphs.get(i);
      if (!g.isDisabled() && !viz.getCanvas().isClipped(g) &&
           g.isVisible() && g.isClicked(x, y)) {
        return g;
      }
    }

    return null;
  }

  public String toString() {
    String str = "ROWNUM=" + this.rowIndex + " -> ";
    for (int i = 0; i < glyphs.size(); i++) {
      Glyph glyph = (Glyph) glyphs.get(i);

      //str += glyph.getDrawingOrder() + " " + glyph.isVisible() + " ";
    }
    return str;
  }
}
