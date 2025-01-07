package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.graph.Reference;

import com.sap.engine.objectprofiler.view.utils.FieldProps;
import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.controller.impl.ShortestPathFinder;
import com.sap.engine.objectprofiler.controller.impl.AllPathsFinder;
import com.sap.engine.objectprofiler.controller.PathFinder;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
 * Date: 2005-7-28
 * Time: 16:03:19
 */
public class GraphVizualizerPanel extends JPanel {
  public static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  public static final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

  public static final int INIT_WIDTH = 200;
  public static final int INIT_HEIGHT = 200;

  private Graph graph = null;
  private ArrayList glyphs = new ArrayList();

  private Glyph root = null;

  private LegendPane legend = null;
  private GraphVizualizerCanvas canvas = null;
  private JSplitPane splitter = null;
  private GraphTree graphTree = null;

  private Configuration configuration = new Configuration(this);

  private ArrayList pathFinders = new ArrayList();


  public GraphVizualizerPanel(Graph graph) {
    super();

    createComponents();

    pathFinders.add(new ShortestPathFinder());
    pathFinders.add(new AllPathsFinder());


    setGraph(graph);
  }


  public void setConfiguration(Configuration conf) {
    conf.setVizualizer(this);
    conf.setWatchList(configuration.getWatchList());
    conf.setClassFieldsFilters(configuration.getClassFieldsFilters());

    configuration = conf;
    configuration.update();
  }

  public Configuration getConfiguration() {
    return configuration;
  }


  private void createComponents() {
    canvas = new GraphVizualizerCanvas(this);
    canvas.setMinimumSize(new Dimension(INIT_WIDTH/2, INIT_HEIGHT/2));

    graphTree = new GraphTree(this);

    legend = new LegendPane();

    splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            canvas, legend);
    splitter.setOneTouchExpandable(true);
    splitter.addComponentListener(new ResizedListener());

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    setLayout(gridbag);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(splitter, c);
    gridbag.setConstraints(graphTree, c);
    add(splitter);
  }

  public void setRoot(Node root) {
    graph.setRoot(root);

    setGraph(graph);
  }

  public void switchViews() {
    //GridBagLayout gridbag = (GridBagLayout)getLayout();

    Component current = this.getComponent(0);
    if (current == splitter) {
      configuration.setCurrentView(Configuration.TREE_VIEW);
    } else {
      configuration.setCurrentView(Configuration.GRAPH_VIEW);
    }
  }

  public void setCurrentView(int desiredView) {
    Component current = this.getComponent(0);

    if (desiredView == Configuration.TREE_VIEW && current == splitter) {
      remove(splitter);

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(2,2,2,2);

      setLayout(gridbag);

      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 1;
      gridbag.setConstraints(graphTree, c);
      add(graphTree);
    } else if (desiredView == Configuration.GRAPH_VIEW && current == graphTree) {
      remove(graphTree);

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(2,2,2,2);

      setLayout(gridbag);

      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = 1;
      gridbag.setConstraints(splitter, c);
      add(splitter);
    }

    repaint();
    revalidate();
  }

  public void switchViews(Glyph g) {
    switchViews();

    Component current = this.getComponent(0);
    if (current == splitter) {
      canvas.pointGlyph(g);
    } else {
      graphTree.pointGlyph(g);
    }
  }


  public GraphVizualizerCanvas getCanvas() {
    return canvas;
  }

  public GraphTree getGraphTree() {
    return graphTree;
  }

  public DefaultTreeModel buildTreeModelFromGlyphs() {
    DefaultMutableTreeNode node = null;
    if (root != null && !root.isDisabled()) {
      node = buildTreeModelNode(root);
    }

    DefaultTreeModel model = new DefaultTreeModel(node);

    return model;
  }

  public DefaultMutableTreeNode buildTreeModelNode(Glyph glyph) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(glyph);

    if (glyph != null) {
      ArrayList kids = glyph.getChildren();
      for (int i=0;i<kids.size();i++) {
        Glyph kid = (Glyph)kids.get(i);
        if (!kid.isDisabled()) {
          DefaultMutableTreeNode kidNode = buildTreeModelNode(kid);
          node.add(kidNode);
        }
      }
    }

    return node;
  }

  public Glyph getGlyphWithType(String type) {
    Stack stack = new Stack();
    stack.push(root);

    while (!stack.isEmpty()) {
      Glyph g = (Glyph)stack.pop();

      Node node = g.getNode();
      if (node == null) {
        continue;
      }

      if (node.getCurrentClassData() == null) {
        continue;
      }

      String nodeClassName = node.getCurrentClassData().getClassName();
      if (nodeClassName.equals(type)) {
        return g;
      }

      ArrayList kids = g.getChildren();
      stack.addAll(kids);
    }

    return null;
  }

  public void setGraph(Graph graph) {
    if ( graph != null) {
      setCursor(waitCursor);

      this.graph = graph;

      //System.out.println("Graph has "+graph.getNodes().length+ " nodes");
      //System.out.println("Graph has "+graph.getReferences().length+ " refs");
      //showClassStats(graph.getNodes());

      //System.out.println("PATH FINDERS...");
      long time = System.currentTimeMillis();
      applyPathFinders(graph);
      float dtime = (System.currentTimeMillis()-time)/1000.0f;
      //System.out.println("PATH FINDERS OK for "+dtime+" sec(s)");

      //System.out.println("MEM CALCS ...");
//      time = System.currentTimeMillis();
//      applyMemoryCalculators(graph);
//      dtime = (System.currentTimeMillis()-time)/1000.0f;
      //System.out.println("MEM CALCS OK for "+dtime+" sec(s)");

      rebuildGlyphs();

      DefaultTreeModel model = buildTreeModelFromGlyphs();
      graphTree.setModel(model);

      //System.out.println("REPAINT ...");
      canvas.repaintCanvas();
      setCursor(defaultCursor);
      //System.out.println("REPAINT OK");
    }
  }

  public void rebuildGlyphs() {
    glyphs.clear();
    canvas.clearRows();
    configuration.getClassFieldsFilters().clear();

    //System.out.println("Building tree...");
    long time = System.currentTimeMillis();
    buildGlyphs();
    setGlyphDimensions();
    float dtime = (System.currentTimeMillis()-time)/1000.0f;
    //System.out.println("Tree has "+glyphs.size() + " glyphs and was built for "+dtime+" sec(s)");

    filter();
    filterFields();

    calcGlyphWeights();

    //System.out.println("GLYPH COORDINATES CALCS ...");
    time = System.currentTimeMillis();
    canvas.recalculateGlyphCoordinates();
    dtime = (System.currentTimeMillis()-time)/1000.0f;
    //System.out.println("GLYPH COORDINATES CALCS OK for "+dtime+" sec(s)");
 }

 public void setGlyphDimensions() {
   if (root == null) {
     return;
   }

   Stack stack = new Stack();
   stack.push(root);

   while (!stack.isEmpty()) {
     Glyph glyph = (Glyph)stack.pop();

     boolean bad = calculateIfBad(glyph);
     //System.out.println(" ID = "+glyph.getID()+ ":"+glyph.getNode().getCurrentClassData().getClassName()+":"+bad);
     if (bad) {
       glyph.setContourColor(Glyph.WARNING_CONTOUR_COLOR);
       setParentsAsWarning(glyph);
     } else {
       glyph.setContourColor(Glyph.CONTOUR_COLOR);
     }

     ArrayList kids = glyph.getChildren();
     stack.addAll(kids);
   }
 }

  private boolean calculateIfBad(Glyph glyph) {
    boolean res = false;

    Node node = glyph.getNode();
    if (node == null || node.isDummy()) {
      return res;
    }

    HashMap nodeProps = node.getProps();
    Boolean definedShareable = (Boolean)nodeProps.get(Node.DEFINED_SHAREABLE);

    if (definedShareable == null) {
      return true;
    }

    if (definedShareable.booleanValue()) {
      return res;
    }

    Iterator iterat = configuration.getNodeShareabilityProps().entrySet().iterator();
    while (iterat.hasNext()) {
      Map.Entry entry = (Map.Entry)iterat.next();
      String propName = (String)entry.getKey();
      Boolean propValue = (Boolean)entry.getValue();

      Boolean nodePropValue = (Boolean)nodeProps.get(propName);
      boolean flag = propValue.booleanValue() && nodePropValue.booleanValue();
      res = res || flag;
      if (res) {
        return res;
      }
    }

    return res;
  }

  private void setParentsAsWarning(Glyph glyph) {
    Glyph parent = glyph.getParent();
    while (parent != null) {
      if (parent.getCountourColor().equals(Glyph.WARNING_CONTOUR_COLOR) ||
          parent.getCountourColor().equals(Glyph.HIGHLIGHTED_CONTOUR_COLOR)) {
        return;
      }
      parent.setContourColor(Glyph.HIGHLIGHTED_CONTOUR_COLOR);
      parent = parent.getParent();
    }
  }

  public void calcGlyphWeights() {
    HashMap map = new HashMap();

    getAllChildrenWithoutRepetition(root, map);
    Iterator iter = map.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      Glyph glyph = (Glyph)entry.getKey();
      HashSet set = (HashSet)entry.getValue();

      //System.out.print(" Set for ["+glyph.getNode().getId()+"] is (");
      int weight = 0;
      Iterator iterNodes = set.iterator();
      while (iterNodes.hasNext()) {
        Node node = (Node)iterNodes.next();
        //System.out.print(node.getId()+ " ");

        weight += node.getWeight();
      }

      //System.out.println(")");
      glyph.setWeight(weight);
    }

    for (int i=0;i<glyphs.size();i++) {
      Glyph g = (Glyph)glyphs.get(i);
      g.calculatePercent();
      g.applyLabelStyle(configuration.getLabelStyle());
    }
  }

  public HashSet getAllChildrenWithoutRepetition(Glyph glyph, HashMap map) {
    HashSet set = new HashSet();

    if (glyph != null && !glyph.isDisabled()) {
      ArrayList kids = glyph.getChildren();

      set.add(glyph.getNode());
      map.put(glyph, set);

      for (int i=0;i<kids.size();i++) {
        Glyph kid = (Glyph)kids.get(i);
        set.addAll(getAllChildrenWithoutRepetition(kid, map));
      }
    }

    return set;
  }

//  private static final String[] letters = new String[] {"", "K", "M", "G", "T"};
//  public static String convertToShortSize(float size) {
//    float threshold = 1024f;
//    String res = null;
//
//    int counter = 0;
//
//    while (counter < letters.length-1 && size > threshold) {
//      counter++;
//      size /= threshold;
//    }
//
//    DecimalFormat formater = new DecimalFormat("0.#"+letters[counter]);
//    res = formater.format(size);
//
//    return res;
//
//
//  }

  public void removePathFinder(PathFinder finder) {
    pathFinders.remove(finder);
  }

  public ArrayList getPathFinders() {
    return pathFinders;
  }

  private Glyph contained(Node node) {
    Glyph contained = null;

    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);
      if (node == glyph.getNode()) {
        contained = glyph;
        break;
      }
    }

    return contained;
  }



  public void addPathFinder(PathFinder finder) {
    if (!pathFinders.contains(finder)) {
      pathFinders.add(finder);
    }
  }

  public Glyph getRoot() {
    return root;
  }

  public void filter() {
    //System.out.println("FILTER!");
    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);

      glyph.enable();
    }

    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);

      Node node = glyph.getNode();
      //System.out.println(" CHECKING GLYPH WITH ID= "+node.getId());
      if (configuration.getGlyphFilters().filterByDescription(node.getGenericType())) {
        //System.out.println(" DISABLED GLYPH WITH ID= "+node.getId());
        glyph.disable();
      }
    }
  }

  public void filterFields() {
    if (root != null) {
      filterFields(root);
    }
  }

  public void filterFields(Glyph g) {
    for (int i=0;i<glyphs.size();i++) {
      Glyph glyph = (Glyph)glyphs.get(i);

      glyph.enable();
    }

    filterFieldsRec(g);
  }

  private void filterFieldsRec(Glyph g) {
    Node node = g.getNode();
    String className = node.getType();

    HashMap fields = (HashMap)configuration.getClassFieldsFilters().get(className);

    ArrayList kids = g.getChildren();
    for (int i=0;i<kids.size();i++) {
      Glyph kid = (Glyph)kids.get(i);

      if (fields != null) {
        String linkName = kid.getLinkName();

        FieldProps props = (FieldProps)fields.get(linkName);
        if (props != null && props.isExcluded()) {
          kid.disable();
        }
      }
      filterFieldsRec(kid);
    }
  }

  public ArrayList getGlyphs() {
    return glyphs;
  }

  public Graph getGraph() {
    return graph;
  }

  private void fillFieldInfo(Node node) {
    if (!node.isCompound()) {
      String className = node.getType();
      HashMap fields = (HashMap)configuration.getClassFieldsFilters().get(className);

      if (fields == null) {
        Node[] kids = graph.getChildren(node);
        if (kids != null) {
          fields = new HashMap();
          configuration.getClassFieldsFilters().put(className, fields);
          for (int i=0;i<kids.length;i++) {
            Reference ref = graph.getReference(node, kids[i]);
            String linkName = ref.getName();
            String type = kids[i].getType();

            FieldProps props = new FieldProps(linkName, type);
            fields.put(linkName, props);
          }
        }
      }
    }
  }

  public void buildGlyphs() {
    Node rootNode = graph.getRoot();

    if (rootNode != null) {
      Glyph g = new Glyph(this, rootNode);
      root = g;

      ArrayList list = new ArrayList();
      list.add(g);
      buildChildrenGlyphs(list, 0);
      root.show();
    } else {
      root = null;
    }
  }

  private void buildChildrenGlyphs(ArrayList parentGlyphs, int rowNum) {
    ArrayList kidsGlyphs = new ArrayList();

    for (int i=0;i<parentGlyphs.size();i++) {
      Glyph parent = (Glyph)parentGlyphs.get(i);
      Node parentNode = parent.getNode();
      if (parentNode.isDummy()) {
        //parent.setShape(Glyph.SHAPE_TRIANGLE);
        parent.setContourColor(Glyph.CONTOUR_COLOR);
        parent.setGlyphColor(Glyph.DUMMY_GLYPH_COLOR);

        glyphs.add(parent);
        continue;
      }

      fillFieldInfo(parentNode);

      Glyph contained = contained(parentNode);
      glyphs.add(parent);

      if (contained == null) {
        //Node[] kidsNodes = graph.getChildren(parent.getNode());
        Node[] kidsNodes = graph.getChildren(parentNode);
        if (kidsNodes != null && kidsNodes.length > 0) {
          int n = kidsNodes.length;
          parent.setShape(Glyph.SHAPE_SQUARE);

          for (int j=0;j<n;j++) {
            Glyph g = new Glyph(this, kidsNodes[j], parent);
            Reference reference = graph.getReference(parentNode, kidsNodes[j]);
            String linkName = reference.getName();
            g.setLinkName(linkName);
            parent.addChild(g);

            kidsGlyphs.add(g);

            if (reference.isTransient()) {
              g.setPathColor(Glyph.WARNING_PATH_COLOR);
              g.setDefaultPathColor(Glyph.WARNING_PATH_COLOR);
            }
          }
        }
//        if (!parent.getNode().isShareable()) {
//          parent.setContourColor(Glyph.WARNING_CONTOUR_COLOR);
//        } else if (parent.getNode().hasNonShareableKids()) {
//          parent.setContourColor(Glyph.HIGHLIGHTED_CONTOUR_COLOR);
//        }
      } else {
        parent.setGlyphColor(Glyph.HIGHLIGHTED_GLYPH_COLOR);
        contained.setGlyphColor(Glyph.HIGHLIGHTED_GLYPH_COLOR);

//        if (!parent.getNode().isShareable()) {
//          parent.setContourColor(Glyph.WARNING_CONTOUR_COLOR);
//          contained.setContourColor(Glyph.WARNING_CONTOUR_COLOR);
//        } else if (parent.getNode().hasNonShareableKids()) {
//          parent.setContourColor(Glyph.HIGHLIGHTED_CONTOUR_COLOR);
//          contained.setContourColor(Glyph.HIGHLIGHTED_CONTOUR_COLOR);
//        }
      }
    }

    if (kidsGlyphs.size() > 0) {
      buildChildrenGlyphs(kidsGlyphs, ++rowNum);
    }
  }

  public void applyPathFinders(Graph graph) {
    for (int i=0;i<pathFinders.size();i++) {
      PathFinder finder = (PathFinder)pathFinders.get(i);

      finder.setGraph(graph);
    }
  }

//  public void applyMemoryCalculators(Graph graph) {
//    for (int i=0;i<memoryCalculators.size();i++) {
//      MemoryCalculator calc = (MemoryCalculator)memoryCalculators.get(i);
//
//      calc.setGraph(graph);
//      calc.calculateWeight();
//
//      if (i == 0) {
//        applyWeight(calc.getWeight());
//      }
//    }
//  }
  private class ResizedListener extends ComponentAdapter {
    public void componentResized(ComponentEvent e) {
      JSplitPane splitPane = (JSplitPane)e.getComponent();

      int pos = splitPane.getWidth()-216;
      if (splitPane.getDividerLocation() < pos) {
        splitPane.setDividerLocation(pos);
      }
    }
  }
}
