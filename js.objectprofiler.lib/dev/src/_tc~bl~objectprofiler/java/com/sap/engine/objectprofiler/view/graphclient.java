package com.sap.engine.objectprofiler.view;

import com.sap.engine.objectprofiler.graph.*;
import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.view.dialogs.ConnectWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.text.FieldPosition;

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
 * Date: 2005-3-21
 * Time: 15:32:28
 */
public class GraphClient extends JFrame {
  private static final int WW = 800;
  private static final int HH = 600;

  private GraphVizualizer viz;
  private Graph graph = null;

  private JButton exit = null;
  private JButton loadGraph = null;
  private JButton saveGraph = null;
  private JButton saveImage = null;
  //String[] connectionProps = null;

  private JButton switchViews = null;
  private String connectionProperties[] = new String[0];

  public GraphClient() {
    super("Object Profiler - Graph vizualization tool");

    createComponents();
    createMenus();

    setSize(WW, HH);
    setVisible(true);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    showConnectWindow();
  }

  public GraphClient(String[] args){
    super("Object Profiler - Graph vizualization tool");

    //graph = buildGraph3();

    createComponents();
    createMenus();

    setSize(WW, HH);
    setVisible(true);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    if (args == null || args.length != 4) {
      showConnectWindow();
    } else {
      connectionProperties = args;
    }
  }

  public GraphVizualizer getVizualizer() {
    return viz;
  }

  private void createMenus() {
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    GraphMenu graphMenu = new GraphMenu("Graph", this);
    JMenu fileMenu = new JMenu("File");

    JMenuItem connect = new JMenuItem("Connect");
    fileMenu.add(connect);
    connect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (!showConnectWindow()) {
          reconnect();
        }
      }
    });

    fileMenu.add(new JSeparator());

    JMenuItem loadXML = new JMenuItem("Load Graph");
    fileMenu.add(loadXML);
    loadXML.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadFromXML();
      }
    });

    JMenuItem saveXML = new JMenuItem("Save Graph");
    fileMenu.add(saveXML);
    saveXML.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAsXML();
      }
    });

    JMenuItem saveImage = new JMenuItem("Save As Image");
    fileMenu.add(saveImage);
    saveImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAsImage();
      }
    });


    fileMenu.add(new JSeparator());

    JMenuItem loadConf = new JMenuItem("Load Configuration");
    fileMenu.add(loadConf);
    loadConf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadConfiguration();
      }
    });

    JMenuItem saveConf = new JMenuItem("Save Configuration");
    fileMenu.add(saveConf);
    saveConf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveConfiguration();
      }
    });

    fileMenu.add(new JSeparator());

    JMenuItem exitMenu = new JMenuItem("Exit");
    fileMenu.add(exitMenu);
    exitMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exit();
      }
    });

    menuBar.add(fileMenu);
    menuBar.add(graphMenu);

  }

  private void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);

    JPanel vizPanel = getVizPanel(connectionProperties);
    JPanel buttonPanel = getButtonsPanel();

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(vizPanel, c);
    pane.add(vizPanel);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(buttonPanel, c);
    pane.add(buttonPanel);
  }

  public void setConnectionProperties(String[] args) {
    connectionProperties = args;
    reconnect();
  }

  public void reconnect() {
    if (viz != null) {
      viz.reconnect(connectionProperties);
    }
  }

  public Graph buildGraph() {
    HashMap map = new HashMap();

    map.put("key1", new Object[][] { {null, new ArrayList()}, {new String(), new String(), new String()}});
    map.put("key2", new Integer(1));
    map.put("key3", "String Value 2");
    map.put("key4", new Integer(2));
    map.put("key5", map);
    map.put("key6", new Object[] {new String("IhuAhu"), new String("Kolko sam tap!")});
    map.put("key7", new int[][] {{1,2,3,4,5}, {5,6,7,8,9},  {1,2}, {}});
    //map.put("key6", new Drawer());

    HashMap map3 = new HashMap();
    HashMap map2 = new HashMap();
    map2.put("a", "v1");


    map3.put("a", "v1");
    map3.put("b", "v1");
    map3.put("c", "v2");
    map3.put("d", map2);

    map2.put("b", map3);
    //map2.put("visual", this);

    Graph g = null;

    try {
      ClassesFilter filter = new ClassesFilter(new String[] {"java.lang.String"});
      g = Graph.buildGraph(map2, -1, filter);
      //g = Graph.buildGraph(map2, -1);
      //System.out.println(" nodes = " + g.getNodes().length+ " ,refs = " + g.getReferences().length);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return g;
  }

  public Graph buildGraph2() {
    long[][] longArray = new long[][] {{1,2,3,4,5,7,8,9,0}, {5,6,7,8,9,3,45,66},  {1,2,4,5,6,7,8}, {}};

    HashMap map1 = new HashMap();
    HashMap map2 = new HashMap();
    HashMap map3 = new HashMap();

    map1.put("key1", map2);
    map1.put("key2", map3);
    map1.put("key3", new Integer(2));

    map2.put("1000", map2);
    map2.put("bbbb", longArray);

    map3.put("aaaa", new Object[] {new String("G"), new String("P")});
    map3.put("3000", longArray);

    Graph g = null;

    try {
      g = Graph.buildGraph(map1);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return g;
  }

  public Graph buildGraph3() {
    Graph g = null;
    try {
      boolean includeTransients = true;
      HashMap map = new HashMap();
      IhuAhu ihuAhu = new IhuAhu();

      map.put("alibali", ihuAhu);
      ClassesFilter filter = new ClassesFilter(new String[] {"java.lang.Class"});
      java.text.DecimalFormat format = new java.text.DecimalFormat("a##.0b");
      format.format(124.56, new StringBuffer(), new FieldPosition(0));
      format.applyPattern("##.0");
      g = Graph.buildGraph(ihuAhu, -2, filter, includeTransients, false);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return g;
  }

  public GraphReport buildReport() {
    IhuAhu ihuAhu = new IhuAhu();
    GraphReport rep = GraphReport.buildReportForNonshareable(ihuAhu, true);
    //rep.printReport(System.out);

    return rep;
  }


//  public Graph buildGraph4() {
//    Graph g = null;
//    try {
//      Object ihuAhu = MakeP4Graph.makeP4Graph();
//      //ClassesFilter filter = new ClassesFilter(new String[] {"java.lang.Class"});
//      g = Graph.buildGraph(ihuAhu);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    return g;
//  }




  private JPanel getVizPanel(String[] connectionProps) {
    viz = new GraphVizualizer(graph, connectionProps);

    JPanel panel = new JPanel();
    //panel.setBorder(BorderFactory.createLoweredBevelBorder());
    //panel.setBorder(BorderFactory.createEtchedBorder());

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;

    panel.setLayout(gridbag);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(viz, c);
    panel.add(viz);


    return panel;
  }

  private JPanel getButtonsPanel() {
    Dimension bsize = new Dimension(120,28);

    loadGraph = new JButton("Load From File");
    loadGraph.setPreferredSize(bsize);
    loadGraph.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          loadFromXML();
        }
      });

    saveGraph = new JButton("Save As File");
    saveGraph.setPreferredSize(bsize);
    saveGraph.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAsXML();
      }
    });

    saveImage = new JButton("Save As Image");
    saveImage.setPreferredSize(bsize);
    saveImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAsImage();
      }
    });

    exit = new JButton("Exit");
    exit.setPreferredSize(bsize);
    exit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exit();
      }
    });

    switchViews = new JButton("Switch Views");
    switchViews.setPreferredSize(bsize);
    switchViews.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        switchViews();
      }
    });

    JPanel empty = new JPanel();

    JPanel panel = new JPanel();
    //panel.setBorder(BorderFactory.createEtchedBorder());

    Insets insets = new Insets(2,2,2,2);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    panel.setLayout(gridbag);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(empty, c);
    panel.add(empty);

    c.weightx = 0;
    c.gridx = 1;
    gridbag.setConstraints(loadGraph, c);
    panel.add(loadGraph);

    c.weightx = 0;
    c.gridx = 2;
    gridbag.setConstraints(saveGraph, c);
    panel.add(saveGraph);

    c.gridx = 3;
    gridbag.setConstraints(saveImage, c);
    panel.add(saveImage);

    c.gridx = 4;
    gridbag.setConstraints(switchViews, c);
    panel.add(switchViews);

    c.gridx = 5;
    gridbag.setConstraints(exit, c);
    panel.add(exit);

    return panel;
  }



  public void loadConfiguration() {
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("cfg", "Configuration Files");

    fc.setFileFilter(imf);
    int res = fc.showOpenDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      try {
        Configuration conf = Configuration.loadFromFile(file.getAbsolutePath());

        viz.getVizualizerPanel().setConfiguration(conf);


      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }
    }
  }

  public void saveConfiguration() {
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("cfg", "Configuration Files");

    fc.setFileFilter(imf);
    int res = fc.showSaveDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      //graph.writeInXML(file.getAbsolutePath());
      try {
        String fileName = file.getAbsolutePath();
        if (!fileName.toLowerCase().endsWith(".cfg")) {
          fileName = fileName + ".cfg";
        }

        viz.getVizualizerPanel().getConfiguration().saveAsFile(fileName);
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }

    }
  }

  public void saveAsXML() {
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("ser", "Serialized Object Files");

    fc.setFileFilter(imf);
    int res = fc.showSaveDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      //graph.writeInXML(file.getAbsolutePath());
      try {
        String fileName = file.getAbsolutePath();
        if (!fileName.toLowerCase().endsWith(".ser")) {
          fileName = fileName + ".ser";
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(viz.getGraph());
        oos.close();
        fos.close();
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }

    }
  }

  public void loadFromXML() {
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("ser", "Serialized Object Files");

    fc.setFileFilter(imf);
    int res = fc.showOpenDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      //graph = Graph.loadGraphFromXML(file.getAbsolutePath());
      try {
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fis);
        Graph graph = (Graph)ois.readObject();
        ois.close();
        fis.close();
        viz.setGraph(graph);
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }
    }
  }

  public void saveAsImage() {
    viz.getVizualizerPanel().getCanvas().saveAsImage();
  }



  public void exit() {
    setVisible(false);
    dispose();

    System.exit(0);
  }

  public void switchViews() {
    viz.getVizualizerPanel().switchViews();
  }

  public boolean showConnectWindow() {
    ConnectWindow connectWin = new ConnectWindow(viz.getVizualizerPanel());
    connectWin.showDialog();

    return connectWin.canceled;
  }



  public static void main(String args[]) {
    GraphClient client = new GraphClient(args);

//    GraphReport report = client.buildReport();
//    report.setExcludeList(false);
//    ClassesFilter cf = new ClassesFilter();
//    cf.addFilter("java.lang.String");
//    report.setClassesFilter(cf);
//    report.printReport(System.out);
  }
}


class IhuAhu implements Serializable {
  private int[] intArray = new int[10];
  private HashMap map = new HashMap();
  private Hashtable table = new Hashtable();
  private Object notShareable = new Object();
  private transient Object object;
  private transient Object objectIntArray = null;
  private transient Object[] transientArray = new Object[10];
  private transient Object transientObject = new Object();
  private java.text.DecimalFormat decFormat = new java.text.DecimalFormat();
  private String stringatmi = "chaklaka patlaka!";
  private float myFloat = 5.67f;
  private ArrayList list = new ArrayList();
  Vector vector = new Vector();


  public IhuAhu() {
    map.put("key3", new Integer(2));
    map.put("aaaa", new Object[] {new String("G"), new String("P")});
    map.put("bbbb", intArray);

    table.put("meee","beee:(");

    object = new Object();
    objectIntArray = new int[5];
    list.add(new Integer(888));
    list.add(new String("Ihaaaa"));
    list.add(this);
  }
}