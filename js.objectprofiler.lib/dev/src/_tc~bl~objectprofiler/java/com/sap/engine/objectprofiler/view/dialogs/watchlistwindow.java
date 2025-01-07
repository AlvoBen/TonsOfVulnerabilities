package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.view.Glyph;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;
import com.sap.engine.objectprofiler.view.utils.WatchListListener;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-19
 * Time: 16:53:50
 * To change this template use File | Settings | File Templates.
 */
public class WatchListWindow extends CommonDialog implements ListSelectionListener, WatchListListener {
  private JList listGlyphs = null;
  private DefaultListModel listModel = null;

  private static WatchListWindow instance = null;

  private WatchListWindow(GraphVizualizerPanel canvas) {
    super(canvas, "Watch List", false);
  }

  public static WatchListWindow getInstance(GraphVizualizerPanel canvas) {
    if (instance == null) {
      instance = new WatchListWindow(canvas);
      instance.makeItVisible();
    }

    return instance;
  }

  private void makeItVisible() {
    super.showDialog();
  }

  public void showDialog() {
  }

  protected void init() {
    super.init();

    ok.setText("Remove");

    vizualizer.getConfiguration().addWatchListListener(this);
    listGlyphs = new JList();
    listGlyphs.addListSelectionListener(this);
    listGlyphs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setListModel();

    setSize(300,400);
  }

  private void setListModel() {
    ArrayList watchList = vizualizer.getConfiguration().getWatchList();
    listModel = new DefaultListModel();

    for (int i=0;i<watchList.size();i++) {
      Glyph g = (Glyph)watchList.get(i);
      if (g.getNode() != null) {
        listModel.addElement(g.getID()+" "+g.getNode().getType());
      } else {
        listModel.addElement(""+g.getID());
      }

    }
    listGlyphs.setModel(listModel);
  }


  protected void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);
    getRootPane().setDefaultButton(close);

    JPanel textPanel = getListPanel();
    JPanel buttonPanel = getButtonsPanel();

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(textPanel, c);
    pane.add(textPanel);


    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(buttonPanel, c);
    pane.add(buttonPanel);
  }

  protected void ok() {
    int index = listGlyphs.getSelectedIndex();
    if (index < 0) {
      return;
    }

    vizualizer.getConfiguration().removeGlyphFromWatchList(index);
  }

  protected void exit() {
    vizualizer.getConfiguration().removeWatchListListener(this);
    super.exit();
    instance = null;
  }


  private JPanel getListPanel() {
    JPanel panel = new JPanel();

    JScrollPane scroller = new JScrollPane(listGlyphs);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    panel.setLayout(gridbag);

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(scroller, c);
    panel.add(scroller);

    return panel;
  }



  private JPanel getButtonsPanel() {
    JPanel empty = new JPanel();
    JPanel panel = new JPanel();

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
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
    gridbag.setConstraints(ok, c);
    panel.add(ok);

    c.weightx = 0;
    c.gridx = 2;
    gridbag.setConstraints(close, c);
    panel.add(close);

    return panel;
  }

  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      int index = listGlyphs.getSelectedIndex();
      if (index < 0) {
        return;
      }
      //System.out.println("index = "+index);
      Glyph glyph = (Glyph)vizualizer.getConfiguration().getWatchList().get(index);
      //System.out.println("glyph = "+glyph.getID());
      vizualizer.getCanvas().pointGlyph(glyph);
      vizualizer.getCanvas().repaintCanvas();
    }
  }

  public void watchListChanged() {
    setListModel();
  }
}
