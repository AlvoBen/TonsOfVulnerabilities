package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.graph.Graph;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
 * Time: 16:40:43
 */
public class Glyph implements Comparable {
  // private static int INDEX = 0;

  public static final int DIAMETER = 26;
  public static final int RADIUS = DIAMETER/2;

  public static final int SHAPE_CIRCLE = 0;
  public static final int SHAPE_SQUARE = 1;
  public static final int SHAPE_TRIANGLE = 2;

  public static final Color GLYPH_COLOR = Color.YELLOW;
  public static final Color HIGHLIGHTED_GLYPH_COLOR = new Color(255,140,0);
  public static final Color DUMMY_GLYPH_COLOR = new Color(0,140,0);
  //public static final Color WARNING_GLYPH_COLOR = new Color(200,0,0);

  public static final Color PATH_COLOR = new Color(0,80,0);
  public static final Color WARNING_PATH_COLOR = new Color(100,0,0);
  public static final Color HIGHLIGHTED_PATH_COLOR = Color.RED;

  public static final Color CONTOUR_COLOR = Color.BLACK;
  public static final Color HIGHLIGHTED_CONTOUR_COLOR = new Color(155,0,0);
  public static final Color WARNING_CONTOUR_COLOR = new Color(255,0,0);
  public static final Color DUMMY_CONTOUR_COLOR = new Color(0,255,0);

  public static final Color LABEL_COLOR = Color.BLACK;
  public static final Color TOOLTIP_TRANSPARENT_COLOR = new Color(Color.yellow.getRed(), Color.yellow.getGreen(), Color.yellow.getBlue(), 200);
  public static final Color TOOLTIP_COLOR = Color.YELLOW;

  private Node node = null;

  private Glyph parent = null;
  private ArrayList children = new ArrayList();
  private int numVisibleChildren = 0;

  private Arc parentArc = null;

  private Color defaultPathColor = PATH_COLOR;
  private Color glyphColor = GLYPH_COLOR;
  private Color pathColor = PATH_COLOR;
  private Color contourColor = CONTOUR_COLOR;
  private Color labelColor = LABEL_COLOR;

  // if it is expanded or not
  private boolean expanded = false;

  // if it is visible or not; it can be expanded and not visible!!!
  private boolean visible = false;

  // if it is selected - the node props tooltip is shown
  private boolean selected = false;

  // if the glyph is marked with right and left arrows
  private boolean pointed = false;

  // if the glyph tooltip is shown
  private boolean tooltipActive = false;

  // if the tooltip is compound - like glyohs representing array nodes
  private boolean compound = false;

  // if the tooltip is disabled it can't be visible and also it doesn't counts at all
  private boolean disabled = false;

  // if one or more kids of this glyph are more "special" (like some of the kid's node represent non-shareable node)
  private boolean hasMarkedChild = false;

  //private int drawingOrder = 0;
  private int level = 0;

  private Rectangle boundingRectangle = new Rectangle(Glyph.DIAMETER, Glyph.DIAMETER);

  // drawing coordinates
  private int x = 0;
  private int y = 0;

  // virtual coordiantes
  private int vx = 0;
  private int vy = 0;

  public int a = 0;
  public int b = 0;

  private String linkName = "<root>";
  private String label = null;
  private String tooltipString = null;

  private int percent = 0;
  private float weight = 0;
  private int id = 0;

  private GlyphRow row = null;

  private GraphVizualizerPanel viz = null;

  private int shape = SHAPE_CIRCLE;

  public Glyph() {
    label = ""+id;
  }

  public Glyph(GraphVizualizerPanel viz) {
    this.viz = viz;
    label = ""+id;
  }

  public Glyph(GraphVizualizerPanel viz, Node node) {
    this(viz, node, null);
  }

  public Glyph(GraphVizualizerPanel viz, Node node, Glyph parent) {
    this.viz = viz;
    this.node = node;
    this.parent = parent;

    if (parent != null) {
      level = parent.getLevel()+1;
      parentArc = new Arc();
      if (node.isDummy()) {
        parentArc.setStroke(DrawingArea2D.dashed_stroke);
      }
    }

    if (node != null) {
      id = node.getId();

      if (node.isCompound()) {
        setCompound(true);
      }
    }

    row = viz.getCanvas().getGlyphRow(level);
    row.addGlyph(this);

    applyLabelStyle(viz.getConfiguration().getLabelStyle());
  }

  public Arc getParentArc() {
    return parentArc;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }

  public void setCompound(boolean compound) {
    this.compound = compound;
  }

  public boolean getCompound() {
    return compound;
  }

  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }

  public String getLinkName() {
    return linkName;
  }

  public void hasMarkedChild(boolean flag) {
    hasMarkedChild = true;
  }

  public boolean hasMarkedChild() {
    return hasMarkedChild;
  }

  public void setShape(int shape) {
    this.shape = shape;
  }

  public int getShape() {
    return shape;
  }

  public void applyLabelStyle(int labelStyle) {
    if (labelStyle == Configuration.LABEL_STYLE_ID) {
      label = ""+id;
    } else if (labelStyle == Configuration.LABEL_STYLE_WEIGHT) {
      label = ""+Graph.convertToShortSize(weight);
    } else if (labelStyle == Configuration.LABEL_STYLE_PERCENT) {
      label = ""+percent+"%";
    }
  }

  public Glyph getParent() {
    return parent;
  }

  public void setWeight(float w) {
    this.weight = w;
  }

  public float getWeight() {
    return weight;
  }

  public void setPointed(boolean pointed) {
    this.pointed = pointed;
  }

  public boolean getPointed() {
    return pointed;
  }


  public boolean isDisabled() {
    return disabled;
  }

  public int getID() {
    return id;
  }

  public Color getCountourColor() {
    return contourColor;
  }

  public void calculatePercent() {
    if (parent == null) {
      return;
    }

    ArrayList allKids = parent.getChildren();
    int num = allKids.size();

    float totalWeight = 0;
    for (int i=0;i<num;i++) {
      Glyph kid = (Glyph)allKids.get(i);

      totalWeight += kid.getWeight();
    }
    if (totalWeight != 0) {
      percent = Math.round((weight*100)/totalWeight);
    }
  }

  public void setVXVY(int rx, int ry) {
    this.vx = rx;
    this.vy = ry;

    boundingRectangle.setLocation(rx-RADIUS, ry-RADIUS);
  }

  public void setVX(int vx) {
    this.vx = vx;

    boundingRectangle.setLocation(vx-RADIUS, vy-RADIUS);
  }

  public int getVX() {
    return vx;
  }

  public void setVY(int vy) {
    this.vy = vy;

    boundingRectangle.setLocation(vx-RADIUS, vy-RADIUS);
  }

  public int getVY() {
    return vy;
  }

  public Rectangle getBoundingRectangle() {
    return boundingRectangle;
  }

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;

    this.a = x - RADIUS;
    this.b = y - RADIUS;

    if (parentArc != null) {
      parentArc.setCoordinates(parent.getX(), parent.getY() + Glyph.RADIUS, x, y - Glyph.RADIUS);
    }
  }

  public void setAB(int a, int b) {
    this.a = a;
    this.b = b;

    this.x = a + RADIUS;
    this.y = b + RADIUS;
  }


  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setX(int x) {
    this.x = x;

    this.a = x-RADIUS;
    if (parentArc != null) {
      parentArc.setCoordinates(parent.getX(), parent.getY() + Glyph.RADIUS, x, y - Glyph.RADIUS);
    }
  }

  public void setY(int y) {
    this.y = y;

    this.b = y-RADIUS;
    if (parentArc != null) {
      parentArc.setCoordinates(parent.getX(), parent.getY() + Glyph.RADIUS, x, y - Glyph.RADIUS);
    }
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isSelected() {
    return selected;
  }

  public void select() {
    selected = true;
  }

  public void unselect() {
    selected = false;
  }

  public void alterSelection() {
    selected = !selected;
  }

  public Node getNode() {
    return node;
  }

  public void setGlyphColor(Color color) {
    this.glyphColor = color;
  }

  public void setDefaultPathColor(Color color) {
    this.defaultPathColor = color;
  }

  public Color getDefaultPathColor() {
    return this.defaultPathColor;
  }

  public void setPathColor(Color color) {
    this.pathColor = color;
    if (parentArc != null) {
      parentArc.setColor(pathColor);
    }
  }

  public void setContourColor(Color color) {
    this.contourColor = color;
  }

  public void addChild(Glyph child) {
    children.add(child);
    Collections.sort(children, new VizGlyphsComparator());
 }

  public void setChildren(ArrayList kids) {
    children = kids;
    Collections.sort(children, new VizGlyphsComparator());
  }

  public void setParent(Glyph parent) {
    parent = null;
  }

  public void draw(Graphics2D g) {
    if (isVisible() && !isDisabled()) {
      drawGlyph(g);

      if (label != null) {
        drawLabel(g);
      }
    }
  }

  public void drawSubtree(Graphics2D g) {
    draw(g);

    for (int i=0;i<children.size();i++) {
      Glyph kid = (Glyph)children.get(i);
      if (kid.isVisible() && !kid.isDisabled()) {

        kid.drawSubtree(g);
      }
    }
  }

  public void drawSubtreeTooltips(Graphics2D g) {
    if (tooltipActive) {
      drawTooltip(g);
    }

    for (int i=0;i<children.size();i++) {
      Glyph kid = (Glyph)children.get(i);
      if (kid.isVisible() && !kid.isDisabled()) {
        kid.drawSubtreeTooltips(g);
      }
    }
  }

  public void drawSubtreeMetaData(Graphics2D g) {
    if (selected) {
      drawMetaData(g);
    }

    for (int i=0;i<children.size();i++) {
      Glyph kid = (Glyph)children.get(i);
      if (kid.isVisible() && !kid.isDisabled()) {
        kid.drawSubtreeMetaData(g);
      }
    }
  }

  protected void drawLinkToParent(Graphics2D g) {
    if (parent != null) {
      Rectangle clip = viz.getCanvas().getCanvas().getClip();
      parent.setXY(parent.getVX()-(int)clip.getX(), parent.getVY()-(int)clip.getY());
      g.setColor(pathColor);
      if (node.isDummy()) {
        Stroke stroke = g.getStroke();
        g.setStroke(DrawingArea2D.dashed_stroke);
        g.drawLine(a + RADIUS, b, parent.a + RADIUS, parent.b + DIAMETER);
        g.setStroke(stroke);
      } else {
        g.drawLine(a + RADIUS, b, parent.a + RADIUS, parent.b + DIAMETER);
      }
    }
  }

  protected void drawGlyph(Graphics2D g) {
    if (shape == SHAPE_TRIANGLE) {

      DrawingArea2D.TRIANGLE_SHAPE.translate(a,b);
      g.setColor(glyphColor);
      g.fillPolygon(DrawingArea2D.TRIANGLE_SHAPE);

      g.setColor(contourColor);
      g.drawPolygon(DrawingArea2D.TRIANGLE_SHAPE);

      DrawingArea2D.TRIANGLE_SHAPE.translate(-a,-b);
    } else if (shape == SHAPE_CIRCLE) {
      if (compound) {
        g.setColor(glyphColor);
        g.fillOval( a+2, b-2, DIAMETER, DIAMETER);
        g.fillOval( a+4, b-4, DIAMETER, DIAMETER);

        g.setColor(contourColor);
        g.drawOval( a+2, b-2, DIAMETER-1, DIAMETER-1);
        g.drawOval( a+4, b-4, DIAMETER-1, DIAMETER-1);
      }

      g.setColor(glyphColor);
      g.fillOval( a, b, DIAMETER, DIAMETER);

      g.setColor(contourColor);
      g.drawOval( a, b, DIAMETER-1, DIAMETER-1);
    } else if (shape == SHAPE_SQUARE) {
      if (compound) {
        g.setColor(glyphColor);
        g.fillRoundRect( a+2, b-2, DIAMETER, DIAMETER, 16 , 16);
        g.fillRoundRect( a+4, b-4, DIAMETER, DIAMETER, 16 , 16);

        g.setColor(contourColor);
        g.drawRoundRect( a+2, b-2, DIAMETER-1, DIAMETER-1, 16, 16);
        g.drawRoundRect( a+4, b-4, DIAMETER-1, DIAMETER-1, 16, 16);
      }

      g.setColor(glyphColor);
      g.fillRoundRect( a, b, DIAMETER, DIAMETER, 16 , 16);

      g.setColor(contourColor);
      g.drawRoundRect( a, b, DIAMETER-1, DIAMETER-1, 16 , 16);
    }

    if (pointed) {
      Stroke stroke = g.getStroke();
      g.setStroke(DrawingArea2D.thin_stroke);

      g.setColor(Color.RED);

      Polygon p = new Polygon();
      p.addPoint(x + RADIUS + 2, y);
      p.addPoint(x + RADIUS + 6, y - 4);
      p.addPoint(x + RADIUS + 6, y + 4);

      g.drawPolygon(p);
      g.fillPolygon(p);

      p.reset();
      p.addPoint(x - RADIUS - 3, y);
      p.addPoint(x - RADIUS - 7, y - 4);
      p.addPoint(x - RADIUS - 7, y + 4);

      g.drawPolygon(p);
      g.fillPolygon(p);

      g.setStroke(stroke);
    }

  }

  public void drawTooltip(Graphics2D g) {
    if (!isVisible() || isDisabled() || !tooltipActive) {
      return;
    }

    if (tooltipString == null) {
      tooltipString = toString();
    }

    g.setColor(TOOLTIP_COLOR);

    FontMetrics fm = g.getFontMetrics();
    Rectangle2D rec = fm.getStringBounds(tooltipString, g);
    rec.getWidth();

    g.fillRect(a + Glyph.DIAMETER-3, b - fm.getDescent()-3, (int)rec.getWidth()+3, (int)rec.getHeight()+2);
    g.setColor(LABEL_COLOR);
    g.drawRect(a + Glyph.DIAMETER-3, b - fm.getDescent()-3, (int)rec.getWidth()+3, (int)rec.getHeight()+2);
    g.drawString(tooltipString, a + Glyph.DIAMETER, b + fm.getHeight()/2 - fm.getDescent());
  }

  protected void drawLabel(Graphics2D g) {
    g.setColor(labelColor);

    FontMetrics fm = g.getFontMetrics();
    Rectangle2D rec = fm.getStringBounds(label, g);
    rec.getWidth();
    g.drawString(label, x - (int)(rec.getWidth()/2), y + fm.getHeight()/2 - fm.getDescent());
  }

  public void drawMetaData(Graphics2D g) {
    if (!isVisible() || isDisabled() || !selected) {
      return;
    }

    if (node.isDummy()) {
      return;
    }

    String[] metaData = node.getInfo();

    int w = 0;
    int h = 0;
    int n = metaData.length;

    int cx = x;
    int cy = y;

    FontMetrics fm = g.getFontMetrics();

    for (int i=0;i<n;i++) {
      int nw = fm.stringWidth(metaData[i]);
      if (nw > w)
        w = nw;
    }

    h = n * fm.getHeight() + 10;
    w += 10;

    if (viz != null) {
      if ((cx+w) > viz.getCanvas().getCanvasWidth()) {
        cx -= w;
      }

      if ((cy+h) > viz.getCanvas().getCanvasHeight()) {
        cy -= h;
      }
    }

    g.setColor(TOOLTIP_TRANSPARENT_COLOR);
    g.fillRect(cx,cy,w,h);
    g.setColor(LABEL_COLOR);
    g.drawRect(cx,cy,w-1,h-1);

    int step = h / n;

    for (int i=0;i<n;i++)
    {
      g.drawString(metaData[i],cx+3,cy+fm.getAscent()+i*step+1);
    }
    //g.drawString("Disabled="+isDisabled()+" Visible="+isVisible(),cx+3,cy+fm.getAscent()+n*step+1);
  }

  public boolean isClicked(int x, int y) {
    if ((x >= a) && (x <= (a+DIAMETER)) &&
        (y >= b) && (y <= (b+DIAMETER))) {
      //System.out.println(" ID = "+id + " a = "+a+ " b = "+b);
      return true;
    } else {
      return false;
    }
  }

  public boolean isTooltipActive() {
    return tooltipActive;
  }

  public void setTooltipActive(boolean flag) {
    this.tooltipActive = flag;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public boolean isVisible() {
    return visible;
  }

  public void alterExpansion() {
    if (expanded) {
      collapse();
    } else {
      expand();
    }
  }

  public void expand() {
    int num = children.size();

    if (num > 0) {
      expanded = true;
      for (int i=0;i<num;i++) {
        Glyph glyph = (Glyph)children.get(i);

        glyph.show();
      }
    }
  }

  public void collapse() {
    expanded = false;

    for (int i=0;i<children.size();i++) {
      Glyph glyph = (Glyph)children.get(i);

      glyph.hide();
    }
  }

  public void show() {
    if (visible) {
      return;
    }

    visible = true;

    if (!disabled) {
      if (row != null) {
        row.incNumVisibleGlyphs();
      }

      if (parent != null) {
        parent.incNumVisibleChildren();
      }
    }

    if (expanded) {
      expand();
    }
  }

  public void hide() {
    if (!visible) {
      return;
    }

    visible = false;

    if (!disabled) {
      if (row != null) {
        row.decNumVisibleGlyphs();
      }

      if (parent != null) {
        parent.decNumVisibleChildren();
      }
    }

    for (int i=0;i<children.size();i++) {
      Glyph glyph = (Glyph)children.get(i);

      glyph.hide();
    }
  }

  public void disable() {
    if (disabled) {
       return;
    }

    disabled = true;

    if (visible) {
      if (row != null) {
        row.decNumVisibleGlyphs();
      }

      if (parent != null) {
        parent.decNumVisibleChildren();
      }
    }

    for (int i=0;i<children.size();i++) {
      Glyph glyph = (Glyph)children.get(i);

      glyph.disable();
    }
   }

   public void enable() {
     if (!disabled) {
       return;
     }

     disabled = false;

     if (visible) {
       if (row != null) {
         row.incNumVisibleGlyphs();
       }

       if (parent != null) {
         parent.incNumVisibleChildren();
       }
     }
     for (int i=0;i<children.size();i++) {
      Glyph glyph = (Glyph)children.get(i);

      glyph.enable();
     }

     if (expanded) {
      expand();
     }
   }


  public int compareTo(Object o) {
    Glyph g = (Glyph)o;

    int res = this.vx - g.getVX();

    return res;
  }

  public ArrayList getChildren() {
    return children;
  }

  public boolean hasChildren() {
    if (children.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  public int countChildren() {
    return children.size();
  }

  public int getNumVisibleChildren() {
    return numVisibleChildren;
  }

  public int incNumVisibleChildren() {
    return ++numVisibleChildren;
  }

  public int decNumVisibleChildren() {
    return --numVisibleChildren;
  }

  public String toString() {
    return linkName +  " " +
           node.getType() + " " +
           Graph.convertToShortSize(weight);
  }

  public String toHTMLString() {
    return  "<html><b>" + linkName +  "</b> <font color='0000ee'>" +
            node.getType() + "</font>" +
            "<font color='ff0000'> " + Graph.convertToShortSize(weight) +
            "</font></html>";
  }

  private class VizGlyphsComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      int res = 0;
      Glyph g1 = (Glyph)o1;
      Glyph g2 = (Glyph)o2;

      res = g1.getID() - g2.getID();

      return res;
    }
  }
}
