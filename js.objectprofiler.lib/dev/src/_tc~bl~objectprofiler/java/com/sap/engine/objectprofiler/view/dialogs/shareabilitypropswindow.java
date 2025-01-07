package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.utils.FieldProps;
import com.sap.engine.objectprofiler.view.utils.AutoCompletion;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;
import com.sap.engine.objectprofiler.view.GraphClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-12
 * Time: 14:33:18
 * To change this template use File | Settings | File Templates.
 */


public class ShareabilityPropsWindow extends CommonDialog {
  private JCheckBox[] checkBoxes = null;

  public ShareabilityPropsWindow(GraphClient client) {
    this(client.getVizualizer().getVizualizerPanel());
  }

  public ShareabilityPropsWindow(GraphVizualizerPanel canvas) {
    super(canvas, "Shareability Preconditions", true);
  }

  protected void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);
    getRootPane().setDefaultButton(ok);

    JPanel graphPane = getPropsPanel();
    JPanel buttonPanel = getButtonsPanel();

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(graphPane, c);
    pane.add(graphPane);

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
    TreeMap map = new TreeMap();
    for (int i=0;i<checkBoxes.length;i++) {
      map.put(checkBoxes[i].getText(), new Boolean(checkBoxes[i].isSelected()));
    }
    vizualizer.getConfiguration().setNodeSharebilityProps(map);

    setVisible(false);
    dispose();
  }

  protected void init() {
    super.init();

    TreeMap map = vizualizer.getConfiguration().getNodeShareabilityProps();

    checkBoxes = new JCheckBox[map.size()];
    Iterator iterat = map.entrySet().iterator();

    int i = 0;
    while (iterat.hasNext()) {
      Map.Entry entry = (Map.Entry)iterat.next();
      String text = (String)entry.getKey();
      Boolean bool = (Boolean)entry.getValue();

      //labels[i].setText(text);
      checkBoxes[i] = new JCheckBox(text, bool.booleanValue());
      i++;
    }

    setSize(300,400);
  }


  private JPanel getPropsPanel() {
    JPanel pane = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    pane.setLayout(gridbag);
    pane.setBorder(BorderFactory.createEtchedBorder());

    for (int i=0;i<checkBoxes.length;i++) {
      c.weightx = 1;
      c.weighty = 0;
      c.gridx = 0;
      c.gridy = i;
      c.gridwidth = 1;
      c.gridheight = 1;
      gridbag.setConstraints(checkBoxes[i], c);
      pane.add(checkBoxes[i]);
    }

    return pane;
  }


  private JPanel getButtonsPanel() {
    Dimension bsize = new Dimension(90,25);

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
}


