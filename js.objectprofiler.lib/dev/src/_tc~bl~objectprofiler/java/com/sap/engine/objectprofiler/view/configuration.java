package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.utils.WatchListListener;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeMap;
import java.io.*;


/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-14
 * Time: 17:43:28
 * To change this template use File | Settings | File Templates.
 */
public class Configuration implements Serializable {
  public static final int GRAPH_VIEW = 0;
  public static final int TREE_VIEW = 1;

  public static final int LABEL_STYLE_OTHER = -1;
  public static final int LABEL_STYLE_ID = 0;
  public static final int LABEL_STYLE_WEIGHT = 1;
  public static final int LABEL_STYLE_PERCENT = 2;

  private int currentView = GRAPH_VIEW;
  private TreeMap nodeShareabilityProps = new TreeMap();

  private int level = -1;
  private boolean includeTransients = false;
  private boolean onlyNonshareable = false;

  private ClassesFilter reportFilter = new ClassesFilter(new String[] {"java.lang.Class"});
  private ClassesFilter graphFilters = new ClassesFilter(new String[] {"java.lang.Class"});
  private ClassesFilter glyphFilters = new ClassesFilter();

  private boolean excludeMode = true;

  private int labelStyle = LABEL_STYLE_ID;

  private transient ArrayList watchList = new ArrayList();
  private transient GraphVizualizerPanel viz = null;
  private transient HashMap classFieldsFilters = new HashMap();
  private transient ArrayList watchListListeners = new ArrayList();

  public Configuration(GraphVizualizerPanel panel) {
    viz = panel;
    setDefaultNodeShareabilityProps();
  }

  public void saveAsFile(String fileName) throws IOException {
    if (!fileName.toLowerCase().endsWith(".cfg")) {
      fileName = fileName + ".cfg";
    }
    FileOutputStream fos = new FileOutputStream(fileName);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(this);
    oos.close();
    fos.close();
  }

  public static Configuration loadFromFile(String fileName) throws IOException, ClassNotFoundException {
    FileInputStream fis = new FileInputStream(fileName);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Configuration conf = (Configuration)ois.readObject();
    ois.close();
    fis.close();

    return conf;
  }

  public void setVizualizer(GraphVizualizerPanel viz) {
    this.viz = viz;
  }

  public GraphVizualizerPanel getVizualizer() {
    return viz;
  }

  public int getCurrentView() {
    return currentView;
  }

  public void setCurrentView(int curView) {
    this.currentView = curView;

    viz.setCurrentView(currentView);
  }

  public int getLabelStyle() {
    return labelStyle;
  }

  public void setLabelStyle(int style) {
    labelStyle = style;

    ArrayList glyphs = viz.getGlyphs();
    for (int i=0;i<glyphs.size();i++) {
      Glyph g = (Glyph)glyphs.get(i);
      g.applyLabelStyle(style);
    }

    viz.getCanvas().repaintCanvas();
  }

  public void setDefaultNodeShareabilityProps() {
    nodeShareabilityProps.put(Node.NON_SERILIZABLE_BASE_CLASS, new Boolean(true));
    nodeShareabilityProps.put(Node.NON_SHAREABLE_CLASSLOADER, new Boolean(true));
    nodeShareabilityProps.put(Node.NON_TRIVIAL_FINALIZER, new Boolean(true));
    nodeShareabilityProps.put(Node.READ_EXTERNAL, new Boolean(true));
    nodeShareabilityProps.put(Node.READ_OBJECT, new Boolean(true));
    nodeShareabilityProps.put(Node.READ_RESOLVE, new Boolean(true));
    nodeShareabilityProps.put(Node.SERIAL_PERSISTENT_FIELD, new Boolean(true));
    nodeShareabilityProps.put(Node.TRANSIENT_FIELD, new Boolean(true));
    nodeShareabilityProps.put(Node.WRITE_EXTERNAL, new Boolean(true));
    nodeShareabilityProps.put(Node.WRITE_REPLACE, new Boolean(true));
    nodeShareabilityProps.put(Node.WRITE_OBJECT, new Boolean(true));
    nodeShareabilityProps.put(Node.NOT_SERIALIZABLE, new Boolean(true));
  }

  public ArrayList getWatchList() {
    return watchList;
  }

  public void setWatchList(ArrayList list) {
    watchList = list;
    notifyWatchListListeners();
  }

  public void addGlyphToWatchList(Glyph glyph) {
    if (!watchList.contains(glyph)) {
      watchList.add(glyph);
      notifyWatchListListeners();
    }
  }

  public void removeGlyphFromWatchList(int index) {
    watchList.remove(index);
    notifyWatchListListeners();
  }

  public void removeGlyphFromWatchList(Glyph glyph) {
    watchList.remove(glyph);
    notifyWatchListListeners();
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public ClassesFilter getGraphFilters() {
    return graphFilters;
  }

  public void setGraphFilters(ClassesFilter classesFilter) {
    this.graphFilters = classesFilter;
  }

  public ClassesFilter getGlyphFilters() {
    return glyphFilters;
  }

  public void setGlyphFilters(ClassesFilter filter) {
    viz.setCursor(GraphVizualizerPanel.waitCursor);

    glyphFilters = filter;

    viz.filter();

    viz.calcGlyphWeights();

    viz.getCanvas().recalculateGlyphCoordinates();
    viz.getCanvas().repaintCanvas();

    viz.setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public boolean getIncludeTransients() {
    return includeTransients;
  }

  public void setIncludeTransients(boolean includeTransients) {
    this.includeTransients = includeTransients;
  }

  public boolean getOnlyNonshareable() {
    return onlyNonshareable;
  }

  public void setOnlyNonshareable(boolean onlyNonshareable) {
    this.onlyNonshareable = onlyNonshareable;
  }


  public TreeMap getNodeShareabilityProps() {
    return nodeShareabilityProps;
  }

  public void setExcludeMode(boolean flag) {
    excludeMode = flag;
  }

  public boolean getExcludeMode() {
    return excludeMode;
  }

  public void setReportFilter(ClassesFilter filter) {
    reportFilter = filter;
  }

  public ClassesFilter getReportFilter() {
    return reportFilter;
  }

  public HashMap getClassFieldsFilters() {
    return classFieldsFilters;
  }

  public void setClassFieldsFilters(HashMap filters) {
    viz.setCursor(GraphVizualizerPanel.waitCursor);

    classFieldsFilters = filters;

    viz.filterFields();

    viz.calcGlyphWeights();

    viz.getCanvas().recalculateGlyphCoordinates();
    viz.getCanvas().repaintCanvas();

    viz.setCursor(GraphVizualizerPanel.defaultCursor);
  }

   public void setNodeSharebilityProps(TreeMap filters) {
    viz.setCursor(GraphVizualizerPanel.waitCursor);

    nodeShareabilityProps = filters;

    viz.setGlyphDimensions();
    viz.getCanvas().repaintCanvas();
    viz.getGraphTree().repaint();

    viz.setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void update() {
    viz.setCursor(GraphVizualizerPanel.waitCursor);

    viz.filter();
    viz.filterFields();
    viz.setGlyphDimensions();

    viz.calcGlyphWeights();
    viz.getCanvas().recalculateGlyphCoordinates();

    setCurrentView(currentView);
    setLabelStyle(labelStyle);

    viz.getGraphTree().repaint();

    viz.setCursor(GraphVizualizerPanel.defaultCursor);
  }

  public void addWatchListListener(WatchListListener listener) {
    if (!watchListListeners.contains(listener)) {
      watchListListeners.add(listener);
    }
  }

  public void removeWatchListListener(WatchListListener listener) {
    watchListListeners.remove(listener);
  }

  public void removeAllWatchListListeners() {
    watchListListeners.clear();
  }

  public ArrayList getWatchListListeners() {
    return watchListListeners;
  }

  public void setWatchListListeners(ArrayList listeners) {
    watchListListeners = listeners;
  }

  private void notifyWatchListListeners() {
    for (int i=0;i<watchListListeners.size();i++) {
      WatchListListener listener = (WatchListListener)watchListListeners.get(i);
      listener.watchListChanged();
    }
  }
}
