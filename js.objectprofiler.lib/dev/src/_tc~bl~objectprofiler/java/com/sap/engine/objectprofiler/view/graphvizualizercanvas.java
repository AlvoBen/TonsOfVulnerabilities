package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.dialogs.WatchListWindow;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


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
 * Time: 16:35:09
 */
public class GraphVizualizerCanvas extends JPanel {
  public static final int NODE_ARRANGEMENT_CLASSIC = 0;
  public static final int NODE_ARRANGEMENT_SAVE_SPACE = 1;

  private JScrollBar vscroller = new JScrollBar();
  private JScrollBar hscroller = new JScrollBar();

  private DrawingArea canvas = null;

  private GlyphMenu glyphMenu = null;
  private GraphPopupMenu graphMenu = null;

  private boolean legendIsVisible = false;
  private boolean selectionIsPerforming = false;

  private boolean drawDeltaFlag = false;
  private String deltaString = null;
  private int[] deltaStringCoordinates = new int[2];

  private int selection[][] = new int[2][2];

  private int nodeArrangement = NODE_ARRANGEMENT_CLASSIC;

  private ArrayList rows = new ArrayList();

  private int maxVisibleGlyphsInARow = 0;

  private Glyph lastTooltiped = null;
  private Glyph lastPointed = null;

  private GraphVizualizerPanel parentPanel = null;

  public GraphVizualizerCanvas(GraphVizualizerPanel parentPanel) {
    super();

    this.parentPanel = parentPanel;

    canvas = new DrawingArea2D(this);

    addMouseListener(new GraphVizualizationMouseListener());
    addMouseMotionListener(new GraphVizualizationMotionListener());
    addKeyListener(new GraphVizualizationKeyListener());

    graphMenu = new GraphPopupMenu(parentPanel);

    vscroller.setOrientation(JScrollBar.VERTICAL);
    hscroller.setOrientation(JScrollBar.HORIZONTAL);
    createComponents();

    vscroller.addAdjustmentListener(new MyAdjustmentListener());
    hscroller.addAdjustmentListener(new MyAdjustmentListener());

    adjustScrollers();
  }

  private void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0,0,0,0);

    setLayout(gridbag);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(canvas, c);
    add(canvas);

    c.weightx = 0;
    c.weighty = 1;
    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(vscroller, c);
    add(vscroller);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(hscroller, c);
    add(hscroller);

    JPanel empty = new JPanel();

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(empty, c);
    add(empty);
  }

  public DrawingArea getCanvas() {
   return canvas;
  }

  public GraphVizualizerPanel getParentPanel() {
    return parentPanel;
  }

  public void clearRows() {
    rows.clear();
  }

  public int[][] getSelectionRectangle() {
    return selection;
  }

  public boolean isSelectionPerforming() {
    return selectionIsPerforming;
  }

  public boolean isDeltaVisible() {
    return drawDeltaFlag;
  }

  public String getDeltaString() {
    return deltaString;
  }

  public int[] getDeltaCoordinates() {
    return deltaStringCoordinates;
  }

  public void adjustMaxVisibleGlyphsInARow(int num) {
    if (num > maxVisibleGlyphsInARow) {
      maxVisibleGlyphsInARow = num;
    }
  }

  public GlyphRow getGlyphRow(int index) {
    GlyphRow row = null;

    if (index > rows.size()) {
      System.out.println("A PROBLEM IN THE LOGIC!");
      return null;
    }

    if (index == rows.size()) {
      row = new GlyphRow(this, index);
      rows.add(row);
    } else {
      return (GlyphRow)rows.get(index);
    }

    return row;
  }

  public void recalculateGlyphCoordinates() {
    Glyph root = parentPanel.getRoot();

    if (root == null) {
      return;
    }

    int dd = (GlyphRow.MIN_DISTANCE+Glyph.DIAMETER)/2;
    Dimension dim = null;

    if (this.nodeArrangement == GraphVizualizerCanvas.NODE_ARRANGEMENT_CLASSIC) {
      dim = lineUpGlyphs(dd, root);
    } else {
      int x[] = new int[rows.size()+1];
      for (int i=0;i<x.length;i++) {
        x[i] = dd;
      }
      dim = lineUpGlyphsSaveSpace(0, x, root);
    }

    int rawWidth = (int)dim.getWidth() + dd;
    int rawHeight = (int)dim.getHeight() + GlyphRow.ROW_HEIGHT;
    canvas.setVirtualBounds(0, 0,
            Math.max(rawWidth, (int)canvas.getClip().getWidth()),
            Math.max(rawHeight, (int)canvas.getClip().getHeight()));

    adjustScrollers();
    int tt = (int)(canvas.getVirtualBounds().getWidth() - dim.getWidth() - dd) / 2;
    translateGlyphs(tt);

    Collections.sort(parentPanel.getGlyphs());
  }

  private Dimension lineUpGlyphs(int x, Glyph g) {
    int y = ((g.getLevel() + 1) * GlyphRow.ROW_HEIGHT) - GlyphRow.ROW_HEIGHT / 2;

    g.setVY(y);
    if (g.getNumVisibleChildren() > 0) {
      ArrayList kids = g.getChildren();

      int step = GlyphRow.MIN_DISTANCE+Glyph.DIAMETER;
      for (int i=0;i<kids.size();i++) {
        Glyph kid = (Glyph)kids.get(i);

        if (kid.isVisible() && !kid.isDisabled()) {
          Dimension dim = lineUpGlyphs(x, kid);
          x = (int)dim.getWidth()+step;
          if (y < dim.getHeight()) {
            y = (int)dim.getHeight();
          }
        }
      }

      x -= step;
      adjustOverKids(g);
    } else {
      g.setVX(x);
    }

    return new Dimension(x, y);
  }

  private Dimension lineUpGlyphsSaveSpace(int level, int x[],  Glyph g) {
    int y = ((level + 1) * GlyphRow.ROW_HEIGHT) - GlyphRow.ROW_HEIGHT / 2;
    int step = GlyphRow.MIN_DISTANCE+Glyph.DIAMETER;

    g.setVY(y);
    int num = g.getNumVisibleChildren();
    if (num > 0) {
      int w = (num-1) * step;
      int w2 = w / 2;
      if ((x[level]-w2) > x[level+1]) {
        x[level+1] = x[level]-w2;

        if ((x[level+1] - step) > x[x.length-1]) {
          x[x.length-1] = x[level+1] - step;
        }
      }

      ArrayList kids = g.getChildren();

      for (int i=0;i<kids.size();i++) {
        Glyph kid = (Glyph)kids.get(i);

        if (kid.isVisible() && !kid.isDisabled()) {
          Dimension d = lineUpGlyphsSaveSpace(level+1, x, kid);
          if (d.getHeight() > y) {
            y = (int)d.getHeight();
          }

          x[level+1] += step;

          if ((x[level+1] - step) > x[x.length-1]) {
            x[x.length-1] = x[level+1] - step;
          }
        }
      }
      x[level] = adjustOverKids(g);
      if ((x[level] - step) > x[x.length-1]) {
        x[x.length-1] = x[level] - step;
      }
    } else {
      g.setVX(x[level]);
    }

    return new Dimension(x[x.length-1], y);
  }


  private void translateGlyphs(int t) {
    ArrayList glyphs = parentPanel.getGlyphs();

    for (int i = 0; i < glyphs.size(); i++) {
      Glyph g = (Glyph)glyphs.get(i);

      if (g.isVisible() && !g.isDisabled()) {
        g.setVXVY(g.getVX()+t, g.getVY());
      }
    }
  }

  public int adjustOverKids(Glyph g) {
    ArrayList kids = g.getChildren();
    int w1 = 0;
    int w2 = 0;

    for (int i=0;i<kids.size();i++) {
      Glyph kid = (Glyph)kids.get(i);

      if (kid.isVisible() && !kid.isDisabled()) {
        w2 = kid.getVX();
        if (w1 == 0) {
          w1 = w2;
        }
      }
    }

    int cx = (w1+w2)/2;
    g.setVX(cx);

    return cx;
  }

  public int getCanvasWidth() {
    return canvas.getWidth();
  }

  public int getCanvasHeight() {
    return canvas.getHeight();
  }

  public void repaintCanvas() {
    canvas.redrawImage();
    revalidate();
    repaint();
  }

  public boolean isLegendVisible() {
    return legendIsVisible;
  }

  public void highlightPaths(ArrayList paths) {
    if (paths != null) {
      setCursor(GraphVizualizerPanel.waitCursor);
      for (int i=0;i<paths.size();i++) {
        ArrayList path = (ArrayList)paths.get(i);

        highlightPath(path);
      }
      setCursor(GraphVizualizerPanel.defaultCursor);
    }
  }

  public void saveAsImage() {
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("jpg", "JPG Files");

    fc.setFileFilter(imf);
    int res = fc.showSaveDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      saveCanvasAsImage(file.getAbsolutePath());
    }
  }

  private void highlightPath(ArrayList path) {
    if (path.size() == 0) {
      return;
    }

    for (int i=1;i<path.size();i++) {
      Node kid = (Node)path.get(i);
      Node parent = (Node)path.get(i-1);

      ArrayList list = getAllWithReference(kid, parent);
      for (int j=0;j<list.size();j++) {
        Glyph glyph = (Glyph)list.get(j);
        glyph.setPathColor(Glyph.HIGHLIGHTED_PATH_COLOR);
      }
    }
  }

  private ArrayList getAllWithReference(Node kid, Node parent) {
    ArrayList result = new ArrayList();
    ArrayList glyphs = parentPanel.getGlyphs();

    for (int j=0;j<glyphs.size();j++) {
      Glyph glyph = (Glyph)glyphs.get(j);

      Glyph parentGlyph = glyph.getParent();
      if (parentGlyph == null) {
        continue;
      }

      if (parentGlyph.getNode().equals(parent) &&
          glyph.getNode().equals(kid)) {
        result.add(glyph);
      }
    }

    return result;
  }


  public void expandAll(Glyph g) {
    setCursor(GraphVizualizerPanel.waitCursor);

    expandAllRecursive(g);
    recalculateGlyphCoordinates();
    repaintCanvas();

    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  private void expandAllRecursive(Glyph g) {
    g.expand();
    ArrayList kids = g.getChildren();
    for (int i = 0; i < kids.size(); i++) {
      Glyph g2 = (Glyph)kids.get(i);
      expandAllRecursive(g2);
    }
  }

  public void removeHighlights() {
    setCursor(GraphVizualizerPanel.waitCursor);

    ArrayList glyphs = parentPanel.getGlyphs();
    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);
      glyph.unselect();
      glyph.setPointed(false);
      glyph.setPathColor(glyph.getDefaultPathColor());
    }
    drawDeltaFlag = false;

    repaintCanvas();
    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void alterLegendVisibility() {
    setCursor(GraphVizualizerPanel.waitCursor);
    legendIsVisible = !legendIsVisible;

    repaintCanvas();
    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void hideIt(Glyph g) {
    if (g != parentPanel.getRoot()) {
      setCursor(GraphVizualizerPanel.waitCursor);

      if (g.isVisible()) {
        //System.out.println("hideIt");
        g.hide();

        recalculateGlyphCoordinates();
        repaintCanvas();
      }

      setCursor(GraphVizualizerPanel.defaultCursor);
    }
  }

  public void alterExpansion(Glyph g) {
    setCursor(GraphVizualizerPanel.waitCursor);
    g.alterExpansion();

    if (g.hasChildren()) {
      recalculateGlyphCoordinates();
      repaintCanvas();
    }

    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void alterSelection(Glyph g) {
    setCursor(GraphVizualizerPanel.waitCursor);
    g.alterSelection();

    repaintCanvas();
    setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void saveCanvasAsImage(File f) {
    canvas.saveAsImage(f);
  }

  public void saveCanvasAsImage(String fileName) {
    canvas.saveAsImage(fileName);
  }


  public int getNodeArrangement() {
    return nodeArrangement;
  }

  public void setNodeArrangement(int nodeArrangement) {
    this.nodeArrangement = nodeArrangement;
    recalculateGlyphCoordinates();

    repaintCanvas();
  }

  public GraphPopupMenu getGraphMenu() {
    return graphMenu;
  }

  private Glyph locateGlyph(int x, int y) {
   int vy = y + vscroller.getValue();
   for (int i = 0; i < rows.size(); i++) {
     GlyphRow gr = (GlyphRow) rows.get(i);
     if (gr.isClicked(x, vy)) {
       //System.out.println("row = "+gr.getRowIndex());
       return gr.locateGlyph(x, y);
      }
   }

   return null;
  }

  private void showGlyphMenu(Glyph g, int x, int y) {
    if (glyphMenu == null) {
      glyphMenu = new GlyphMenu(parentPanel);
    }
    glyphMenu.setGlyph(g);
    glyphMenu.show(this, x, y);
  }


  private void showGraphMenu(int x, int y) {
    graphMenu.show(this, x, y);
  }

  // selection actions

  private void selectionStarted(int x, int y) {
    Glyph g = locateGlyph(x,y);
    if (g != null) {
      selection[0][0] = x;
      selection[0][1] = y;

      selectionIsPerforming = true;
    }
  }

  private void selectionEnded(int x, int y) {

    if (selectionIsPerforming) {
      Glyph a = locateGlyph(selection[0][0], selection[0][1]);
      Glyph b = locateGlyph(x,y);
      if (b != null && !a.getNode().equals(b.getNode())) {
//        selectionX2 = x;
//        selectionY2 = y;

        int delta = parentPanel.getGraph().delta(a.getNode(), b.getNode());

        deltaStringCoordinates[0] = a.getX()+Glyph.DIAMETER/2;
        deltaStringCoordinates[1] = a.getY();

        drawDeltaFlag = true;
        deltaString = " delta("+a.getNode().getId()+", "+
                                b.getNode().getId()+") = "+delta;
      }

      selectionIsPerforming = false;
      repaintCanvas();
    }
  }

  public void setVisible(boolean flag) {
    super.setVisible(flag);
  }

  private void mouseMovedAction(int x, int y) {
    Glyph g = locateGlyph(x, y);

    if (lastTooltiped != g) {
      if (lastTooltiped != null) {
        lastTooltiped.setTooltipActive(false);
      }

      if (g != null) {
        g.setTooltipActive(true);
      }

      lastTooltiped = g;
      repaintCanvas();
    }
  }

  private void mouseDraggedAction(int x, int y) {
    if (selectionIsPerforming) {
      selection[1][0] = x;
      selection[1][1] = y;

      repaintCanvas();
    }
  }

  public void pointGlyph(Glyph glyph) {
    if (lastPointed != glyph) {
      if (lastPointed != null) {
        lastPointed.setPointed(false);
      }

      lastPointed = glyph;
    }

    if (glyph != null) {
      glyph.setPointed(true);
      glyph.show();
    }

    Glyph parent = glyph.getParent();
    while (parent != null) {
      parent.show();
      parent = parent.getParent();
    }

    recalculateGlyphCoordinates();
    scrollToGetCentered(glyph);
  }

  private void showWatchListWindow() {
    WatchListWindow wlw = WatchListWindow.getInstance(parentPanel);
    wlw.showDialog();
  }
  // non glyph actions

  private void leftClickedAction(int x, int y) {

  }

  private void shiftLeftClickedAction(int x, int y) {
    //alterLegendVisibility();
  }

  private void altLeftClickedAction(int x, int y) {
    showWatchListWindow();
  }

  private void ctrlLeftClickedAction(int x, int y) {
    removeHighlights();
  }

  private void rightClickedAction(int x, int y) {
    showGraphMenu(x,y);
  }

  private void shiftRightClickedAction(int x, int y) {

  }

  private void altRightClickedAction(int x, int y) {

  }

  private void ctrlRightClickedAction(int x, int y) {
    removeHighlights();
  }

  // glyph actions
  private void glyphLeftClickedAction(Glyph g, int x, int y) {
    alterExpansion(g);
  }

  private void glyphShiftLeftClickedAction(Glyph g, int x, int y) {

  }

  private void glyphCtrlLeftClickedAction(Glyph g, int x, int y) {
    alterSelection(g);
    //showMetaDataScroller(g);
  }

  private void glyphAltLeftClickedAction(Glyph g, int x, int y) {
    hideIt(g);
  }

  private void glyphRightClickedAction(Glyph g, int x, int y) {
    showGlyphMenu(g,x,y);
  }

  private void glyphShiftRightClickedAction(Glyph g, int x, int y) {

  }

  private void glyphCtrlRightClickedAction(Glyph g, int x, int y) {

  }

  private void glyphAltRightClickedAction(Glyph g, int x, int y) {

  }

  private void keyPressedAction(KeyEvent e) {
    if (e.getKeyCode() == 27) {
      removeHighlights();
    }
  }

  public void scrollToGetCentered(Glyph glyph) {
    int x = glyph.getVX();
    int y = glyph.getVY();

    int a = x - (int)canvas.getClip().getWidth()/2;
    int b = y - (int)canvas.getClip().getHeight()/2;
    a = Math.max(0,a);
    b = Math.max(0,b);

    int delta = Math.abs(a - hscroller.getValue()) + Math.abs(b - vscroller.getValue());

    hscroller.setValue(a);
    vscroller.setValue(b);

    if (delta == 0) {
      repaintCanvas();
    }
  }

  public void adjustScrollers() {
    int rw = (int)canvas.getClip().getWidth();
    int rh = (int)canvas.getClip().getHeight();

    int vw = (int)canvas.getVirtualBounds().getWidth();
    int vh = (int)canvas.getVirtualBounds().getHeight();

    float oldHValue = hscroller.getValue();
    if (hscroller.getMaximum()-hscroller.getMinimum() > 0) {
      oldHValue /= (hscroller.getMaximum()-hscroller.getMinimum())*1.0f;
    }

    float oldVValue = vscroller.getValue();
    if (vscroller.getMaximum()-vscroller.getMinimum() > 0) {
      oldVValue /= (vscroller.getMaximum()-vscroller.getMinimum())*1.0f;
    }
    
    hscroller.setMinimum(0);
    hscroller.setMaximum(vw-rw);
    hscroller.setBlockIncrement(Math.max(1,(vw-rw)/100));
    hscroller.setUnitIncrement(1);
    hscroller.setValue((int)(oldHValue*(hscroller.getMaximum()-hscroller.getMinimum())));

    vscroller.setMinimum(0);
    vscroller.setMaximum(vh-rh);
    vscroller.setBlockIncrement(Math.max(1,(vh-rh)/100));
    vscroller.setUnitIncrement(1);
    vscroller.setValue((int)(oldVValue*(vscroller.getMaximum()-vscroller.getMinimum())));
  }

  private class GraphVizualizationKeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      keyPressedAction(e);
    }
  }

  private class GraphVizualizationMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      requestFocus();

      int button = e.getButton();

      int x = e.getX();
      int y = e.getY();

      Glyph g = locateGlyph(x,y);

      if (g != null) {
        if (button == MouseEvent.BUTTON1) {
          if (e.isShiftDown()) {
            glyphShiftLeftClickedAction(g, x, y);
          } else if (e.isAltDown()) {
            glyphAltLeftClickedAction(g, x, y);
          } else if (e.isControlDown()) {
            glyphCtrlLeftClickedAction(g, x, y);
          } else {
            glyphLeftClickedAction(g, x, y);
          }
        } else if (button == MouseEvent.BUTTON3) {
          if (e.isShiftDown()) {
            glyphShiftRightClickedAction(g, x, y);
          } else if (e.isAltDown()) {
            glyphAltRightClickedAction(g, x, y);
          } else if (e.isControlDown()) {
            glyphCtrlRightClickedAction(g, x, y);
          } else {
            glyphRightClickedAction(g, x, y);
          }
        }
      } else {
        if (button == MouseEvent.BUTTON1) {
          if (e.isShiftDown()) {
            shiftLeftClickedAction(x, y);
          } else if (e.isAltDown()) {
            altLeftClickedAction(x, y);
          } else if (e.isControlDown()) {
            ctrlLeftClickedAction(x, y);
          } else {
            leftClickedAction(x, y);
          }
        } else if (button == MouseEvent.BUTTON3) {
          if (e.isShiftDown()) {
            shiftRightClickedAction(x, y);
          } else if (e.isAltDown()) {
            altRightClickedAction(x, y);
          } else if (e.isControlDown()) {
            ctrlRightClickedAction(x, y);
          } else {
            rightClickedAction(x, y);
          }
        }
      }
    }

    public void mousePressed(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();

      selectionStarted(x,y);
    }

    public void mouseReleased(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();

      selectionEnded(x,y);
    }


  }

  private class GraphVizualizationAdjustmentListener implements AdjustmentListener {
    public void adjustmentValueChanged(AdjustmentEvent e){
      if (legendIsVisible) {
        //repaintCanvas();
      }
    }
  }

  private class GraphVizualizationMotionListener implements MouseMotionListener {
    public void mouseMoved(MouseEvent e){
      int x = e.getX();
      int y = e.getY();

      mouseMovedAction(x, y);
    }

    public void mouseDragged(MouseEvent e){
      int x = e.getX();
      int y = e.getY();

      mouseDraggedAction(x, y);
    }
  }

  private class MyAdjustmentListener implements AdjustmentListener {
    public void adjustmentValueChanged(AdjustmentEvent e) {
      canvas.moveClip(hscroller.getValue(), vscroller.getValue());
      repaintCanvas();
    }
  }

  private class ScrollerListener extends MouseAdapter {
    public void mouseClicked(ActionEvent e) {
      //System.out.println(""+e);
      //canvas.moveVisibleRectangle(hscroller.getValue(), vscroller.getValue());
      //repaintCanvas();
    }
  }
}
