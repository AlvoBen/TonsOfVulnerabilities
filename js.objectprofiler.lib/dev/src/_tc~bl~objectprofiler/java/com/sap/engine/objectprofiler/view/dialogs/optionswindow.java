package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.view.GraphClient;
import com.sap.engine.objectprofiler.view.GraphVizualizer;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
 * Date: 2005-7-11
 * Time: 11:07:22
 */
public class OptionsWindow extends CommonDialog {
  private JTextField level = null;
  private JTable graphFilters = null;
  private JTable glyphFilters = null;

  private JCheckBox includeTransients = null;
  private JCheckBox onlyNonshareable = null;

  public OptionsWindow(GraphVizualizerPanel vizualizer)  {
    super(vizualizer, "Options", true);
  }

  protected void init() {
    super.init();

    level = new JTextField();
    graphFilters = new JTable();
    glyphFilters = new JTable();

    includeTransients = new JCheckBox("Include Transient Fields");
    onlyNonshareable = new JCheckBox("Build Only Non-shareable Nodes");

    level.setText(""+vizualizer.getConfiguration().getLevel());

    graphFilters.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    glyphFilters.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    setSize(500,500);
  }


  protected void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);
    getRootPane().setDefaultButton(ok);

    JPanel textPanel = getTextPanel();
    JPanel glyphPane = this.getGlyphFiltersPanel();
    JPanel graphPane = this.getGraphFiltersPanel();
    JPanel buttonPanel = getButtonsPanel();

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(textPanel, c);
    pane.add(textPanel);

    c.gridy = 1;
    c.weighty = 0.5;
    gridbag.setConstraints(graphPane, c);
    pane.add(graphPane);

    c.gridy = 2;
    gridbag.setConstraints(glyphPane, c);
    pane.add(glyphPane);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(buttonPanel, c);
    pane.add(buttonPanel);
  }

  protected void ok() {
    int _level = -1;

    try {
      _level = Integer.parseInt(level.getText());
    } catch(Exception e) {
      GraphVizualizer.showMessageBox(e);

      return;
    }

    vizualizer.getConfiguration().setLevel(_level);


    // adjusting graph filter
    ClassesFilter filter = new ClassesFilter();
    int n = graphFilters.getModel().getRowCount();
    for (int i=0;i<n;i++) {
      String className = (String)graphFilters.getModel().getValueAt(i,0);
      if (className != null && !className.trim().equals("")) {
        filter.addFilter(className);
      }
    }
    vizualizer.getConfiguration().setGraphFilters(filter);

    boolean _includeTransients = includeTransients.isSelected();
    vizualizer.getConfiguration().setIncludeTransients(_includeTransients);

    boolean _onlyNonshareable = onlyNonshareable.isSelected();
    vizualizer.getConfiguration().setOnlyNonshareable(_onlyNonshareable);

    // adjusting viz filter
    ClassesFilter glyphFilter = new ClassesFilter();
    n = glyphFilters.getModel().getRowCount();
    for (int i=0;i<n;i++) {
      String className = (String)glyphFilters.getModel().getValueAt(i,0);
      if (className != null && !className.trim().equals("")) {
        glyphFilter.addFilter(className);
      }
    }
    vizualizer.getConfiguration().setGlyphFilters(glyphFilter);

    TreeModel model = vizualizer.buildTreeModelFromGlyphs();
    vizualizer.getGraphTree().setModel(model);
    setVisible(false);
    dispose();
  }

  private JPanel getTextPanel() {
    JPanel panel = new JPanel();

    includeTransients.setSelected(vizualizer.getConfiguration().getIncludeTransients());
    //includeTransients.setHorizontalTextPosition(SwingConstants.LEFT );

    onlyNonshareable.setSelected(vizualizer.getConfiguration().getOnlyNonshareable());
    //onlyNonshareable.setHorizontalTextPosition(SwingConstants.LEFT );

    JLabel levelLabel = new JLabel("Exploring Depth Level: ");

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    panel.setLayout(gridbag);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(levelLabel, c);
    panel.add(levelLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 0;
    gridbag.setConstraints(level, c);
    panel.add(level);

    c.weightx = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    gridbag.setConstraints(includeTransients, c);
    panel.add(includeTransients);

    c.gridy = 2;
    gridbag.setConstraints(onlyNonshareable, c);
    panel.add(onlyNonshareable);

    return panel;
  }

  private JPanel getGraphFiltersPanel() {
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane,BoxLayout.X_AXIS));
    pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Graph Building Filtered Classes"));

    ClassesFilter filter = vizualizer.getConfiguration().getGraphFilters();
    String[] classNames = filter.getFilters();
    Object[] columnNames = new Object[] {"Class Name/Package Pattern"};
    Object[][] data = new Object[classNames.length][1];
    for (int i=0;i<classNames.length;i++) {
      data[i][0] = classNames[i];
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    graphFilters.setModel(model);
    JScrollPane scroller = new JScrollPane(graphFilters);
    scroller.setBorder(BorderFactory.createRaisedBevelBorder());
    pane.add(scroller);


    JPanel buttons = new JPanel();

    Dimension dim = new Dimension(100,30);
    JButton addRow = new JButton("Add");
    addRow.setSize(dim);
    addRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ((DefaultTableModel)graphFilters.getModel()).addRow(new Object[] {new String()});
      }
    });

    JButton removeRow = new JButton("Remove");
    removeRow.setSize(dim);
    removeRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = graphFilters.getSelectedRow();
        if (row < 0) {
          row = graphFilters.getModel().getRowCount()-1;
        }
        if (row > -1) {
          ((DefaultTableModel)graphFilters.getModel()).removeRow(row);
        }
      }
    });


    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    buttons.setLayout(gridbag);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(addRow, c);
    buttons.add(addRow);

    c.gridy = 1;
    gridbag.setConstraints(removeRow, c);
    buttons.add(removeRow);


    JPanel empty = new JPanel();
    c.weighty = 1;
    c.gridy = 2;
    gridbag.setConstraints(empty, c);
    buttons.add(empty);

    pane.add(buttons);

		return pane;
  }

  private JPanel getGlyphFiltersPanel() {
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane,BoxLayout.X_AXIS));
    pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Visualization Filtered Classes"));

    ClassesFilter filter = vizualizer.getConfiguration().getGlyphFilters();
    String[] classNames = filter.getFilters();
    Object[] columnNames = new Object[] {"Class Name/Package Pattern"};
    Object[][] data = new Object[classNames.length][1];
    for (int i=0;i<classNames.length;i++) {
      data[i][0] = classNames[i];
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    glyphFilters.setModel(model);
    JScrollPane scroller = new JScrollPane(glyphFilters);
    scroller.setBorder(BorderFactory.createRaisedBevelBorder());
    pane.add(scroller);


    JPanel buttons = new JPanel();

    Dimension dim = new Dimension(100,30);
    JButton addRow = new JButton("Add");
    addRow.setSize(dim);
    addRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ((DefaultTableModel)glyphFilters.getModel()).addRow(new Object[] {new String()});
      }
    });

    JButton removeRow = new JButton("Remove");
    removeRow.setSize(dim);
    removeRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = glyphFilters.getSelectedRow();
        if (row < 0) {
          row = glyphFilters.getModel().getRowCount()-1;
        }
        if (row > -1) {
          ((DefaultTableModel)glyphFilters.getModel()).removeRow(row);
        }
      }
    });


    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    buttons.setLayout(gridbag);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(addRow, c);
    buttons.add(addRow);

    c.gridy = 1;
    gridbag.setConstraints(removeRow, c);
    buttons.add(removeRow);


    JPanel empty = new JPanel();
    c.weighty = 1;
    c.gridy = 2;
    gridbag.setConstraints(empty, c);
    buttons.add(empty);

    pane.add(buttons);

    return pane;
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

}
