package com.sap.engine.objectprofiler.view;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.image.VolatileImage;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
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
 * Time: 15:20:45
 */
public abstract class DrawingArea extends JPanel {
  public static final Color BACKGROUND = Color.GRAY;

  protected VolatileImage backBuffer = null;
  protected GraphVizualizerCanvas viz = null;
  protected Rectangle clip = new Rectangle();
  protected Rectangle virtualBounds = new Rectangle();

  public DrawingArea(GraphVizualizerCanvas viz) {
    super();

    this.viz = viz;

    backBuffer = createVolatileImage(GraphVizualizerPanel.INIT_WIDTH, GraphVizualizerPanel.INIT_HEIGHT);//new BufferedImage(INIT_WIDTH, INIT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    setLayout(null);
    clip.setBounds(0,0,this.getWidth(), this.getHeight());
  }

  public void paintComponent(Graphics g) {
    //System.out.println("REPAINT!");
    //super.paintComponent(g);

    if (backBuffer != null) {
      g.drawImage(backBuffer, 0, 0, this);
    }
  }

  public boolean clip(Glyph g) {
    //System.out.println(g.getBoundingRectangle());
    if (clip.intersects(g.getBoundingRectangle())) {
      //System.out.println("g.getVX() = "+g.getVX()+" ID= "+g.getID());
      g.setXY(g.getVX()-(int)clip.getX(), g.getVY()-(int)clip.getY());

      //System.out.println("here");
      return false;
    }

    return true;
  }


  public boolean isClipped(Glyph g) {
    if (clip.intersects(g.getBoundingRectangle())) {
      return false;
    } else {
      return true;
    }
  }

  public void getCandidatesForDrawing(ArrayList glyphs, HashSet arcs) {
    ArrayList list = viz.getParentPanel().getGlyphs();

    if (list == null || list.size() == 0) {
      return;
    }

    int x1 = (int)clip.getX()-Glyph.DIAMETER;
    int x2 = (int)(x1 + clip.getWidth()+Glyph.DIAMETER);

    x1 = binarySearch(list, x1);
    //System.out.println("x1="+x1);
    //x1 = Math.max(0,x1-1);

    x2 = binarySearch(list, x2);
    //System.out.println("x2="+x2);
    //x2 = Math.min(list.size()-1,x2+1);

    for (int i=x1;i<=x2;i++) {
      Glyph g = (Glyph)list.get(i);
      //System.out.println(" getCandidates() ID = "+g.getID()+" VX="+g.getVX());
      if (g.isVisible() && !g.isDisabled() && !clip(g)) {
        glyphs.add(g);

        // add arcs
        Glyph parentGlyph = g.getParent();
        if (parentGlyph != null && parentGlyph.isVisible() && !parentGlyph.isDisabled()) {
          parentGlyph.setXY(parentGlyph.getVX()-(int)clip.getX(), parentGlyph.getVY()-(int)clip.getY());
          Arc parentArc = g.getParentArc();
          parentArc.setCoordinates(parentGlyph.getX(), parentGlyph.getY() + Glyph.RADIUS, g.getX(), g.getY() - Glyph.RADIUS);
          arcs.add(parentArc);
        }

        ArrayList kidGlyphs = g.getChildren();
        for (int j=0;j<kidGlyphs.size();j++) {
          Glyph kidGlyph = (Glyph)kidGlyphs.get(j);
          if (kidGlyph.isVisible() && !kidGlyph.isDisabled()) {
            kidGlyph.setXY(kidGlyph.getVX()-(int)clip.getX(), kidGlyph.getVY()-(int)clip.getY());
            Arc arc = (Arc)kidGlyph.getParentArc();

            arcs.add(arc);
          }
        }
      }
    }
  }

  public int binarySearch(ArrayList list, int x) {
    int a = 0;
    int b = list.size() - 1;
    int currentIndex = 0;
    while (a <= b) {
      currentIndex = (a + b) >> 1;
      Glyph currentGlyph = (Glyph)list.get(currentIndex);
      int currentX = currentGlyph.getVX();

      if (x == currentX) {
        break;
      } else if (x > currentX) {
        a = currentIndex + 1;
      } else {
        b = currentIndex - 1;
      }
    }

    return currentIndex;
  }


  public Rectangle getClip() {
    return clip;
  }

  public void moveClip(int x, int y) {
    clip.setLocation(x,y);
  }

  public Rectangle getVirtualBounds() {
    return virtualBounds;
  }

  public void setVirtualBounds(int x, int y, int w, int h) {
    virtualBounds.setBounds(x, y, w, h);
  }

  public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);

    backBuffer = createVolatileImage(w+1, h+1);
    clip.setBounds((int)clip.getX(), (int)clip.getY(), w, h);
    viz.recalculateGlyphCoordinates();
    redrawImage();
  }

  public void redrawImage() {
    //long milis = System.currentTimeMillis();
    if (backBuffer == null) {
      return;
    }

    //System.out.println("REDRAW IMAGE");
    //System.out.println("W="+backBuffer.getWidth()+" H="+backBuffer.getHeight());
    //System.out.println("CW="+clip.getWidth()+" CH="+clip.getHeight()+" X="+clip.getX()+" Y="+clip.getY());
    //System.out.println("VW="+virtualBounds.getWidth()+" VH="+virtualBounds.getHeight());
    Graphics2D g2 = (Graphics2D)backBuffer.getGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(BACKGROUND);
    g2.fillRect(0,0,getWidth(), getHeight());

    ArrayList glyphs = new ArrayList();
    HashSet arcs = new HashSet();

    getCandidatesForDrawing(glyphs, arcs);
    drawGraph(g2, glyphs, arcs);

    //milis = System.currentTimeMillis() - milis;
    //System.out.println(" DRAWING TIME = "+milis);
  }

  protected abstract void drawGraph(Graphics2D g, ArrayList glyphs, HashSet arcs);

  public void saveAsImage(String fileName) {
    if (!fileName.toLowerCase().endsWith("jpg")) {
      fileName = fileName + ".jpg";
    }

    saveAsImage(new File(fileName));
  }


  public void saveAsImage(File file) {
    try {
      FileOutputStream fos = new FileOutputStream(file);

      Iterator writers = ImageIO.getImageWritersBySuffix("JPG");

      ImageWriter writer = (ImageWriter) writers.next();
      ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
      writer.setOutput(ios);
      ImageWriteParam param = writer.getDefaultWriteParam();

      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(1f);

      writer.write(null, new IIOImage(backBuffer.getSnapshot(), null, null), param);
      //ImageIO.write(backBuffer.getSnapshot(), "PNG", new File(file.getAbsolutePath()+"1.PNG"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void saveAsPNGImage(File file) {
    try {
      ImageIO.write(backBuffer.getSnapshot(), "PNG", file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
