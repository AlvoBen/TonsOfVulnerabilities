package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.graph.Node;
import com.sap.engine.objectprofiler.view.utils.FieldProps;
import com.sap.engine.objectprofiler.view.utils.AutoCompletion;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;



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
public class ExcludeFieldsWindow extends CommonDialog {
  private JComboBox classNames = null;
  private JTable fields = null;

  private HashMap filter = null;
  private Node node = null;

  public ExcludeFieldsWindow(GraphVizualizerPanel canvas) {
    this(canvas, null);
  }

  public ExcludeFieldsWindow(GraphVizualizerPanel canvas, Node node) {
    super(canvas, "Exclude Fields", true);

    this.node = node;
  }


  protected void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);
    getRootPane().setDefaultButton(ok);

    JPanel graphPane = this.getGraphFiltersPanel();
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
    vizualizer.getConfiguration().setClassFieldsFilters(filter);

    TreeModel model = vizualizer.buildTreeModelFromGlyphs();
    vizualizer.getGraphTree().setModel(model);

    setVisible(false);
    dispose();
  }


  protected void init() {
    super.init();

    classNames = new JComboBox();
    fields = new JTable();

    fields.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    fields.setRowSelectionAllowed(false);
    fields.getTableHeader().setReorderingAllowed(false);

    classNames.setEditable(true);
    classNames.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String className = (String)cb.getSelectedItem();
        changeModel(className);
      }
    });
    AutoCompletion.enable(classNames);

    filter = makeACopy();
    ArrayList classes = new ArrayList(filter.keySet());
    Collections.sort(classes);

    for (int i=0;i<classes.size();i++) {
      classNames.addItem(classes.get(i));
    }

    if (node != null) {
      String className = node.getGenericType();
      classNames.setSelectedItem(className);
    }

    setSize(500,500);
  }

  private HashMap makeACopy() {
    HashMap newFieldsFilter = new HashMap();

    HashMap oldFieldsFilter = vizualizer.getConfiguration().getClassFieldsFilters();
    Iterator iter = oldFieldsFilter.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry classEntry = (Map.Entry)iter.next();

      String className = (String)classEntry.getKey();
      HashMap fields = (HashMap)classEntry.getValue();
      HashMap newFields = new HashMap();
      newFieldsFilter.put(className, newFields);

      Iterator iter2 = fields.entrySet().iterator();
      while (iter2.hasNext()) {
        Map.Entry fieldEntry = (Map.Entry)iter2.next();

        String fieldName = (String)fieldEntry.getKey();
        FieldProps props = (FieldProps)fieldEntry.getValue();

        newFields.put(fieldName, props.clone());
      }
    }

    return newFieldsFilter;
  }

  private JPanel getGraphFiltersPanel() {
    JPanel pane = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    Insets insets = new Insets(2,2,2,2);
    c.fill = GridBagConstraints.BOTH;
    c.insets = insets;

    pane.setLayout(gridbag);

    JScrollPane scroller = new JScrollPane(fields);
    scroller.setBorder(BorderFactory.createRaisedBevelBorder());

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(classNames, c);
    pane.add(classNames);

    c.weighty = 1;
    c.gridy = 1;
    gridbag.setConstraints(scroller, c);
    pane.add(scroller);

    return pane;
  }

  private void changeModel(String className) {
    HashMap map = (HashMap)filter.get(className);
    MyTableModel model = new MyTableModel( map);

    fields.setModel(model);
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

  public class MyTableModel extends DefaultTableModel {
    private final Object[] columnNames = new Object[] {"Field Name", "Type", "Excluded"};
    private HashMap map = null;

    public MyTableModel(HashMap map) {
      super();

      this.map = map;
      initModel();
    }

    private void initModel() {
      if (map == null) {
        return;
      }
      
      Set entrySet = map.entrySet();
      int n = entrySet.size();
      Object[][] data = new Object[n][3];
      Iterator iter = entrySet.iterator();
      int i = 0;
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        FieldProps props = (FieldProps)entry.getValue();

        data[i][0] = props.getFieldName();
        data[i][1] = props.getFieldType();
        data[i][2] = new Boolean(props.isExcluded());

        i++;
      }

      setDataVector(data, columnNames);
    }

    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
      if (col < 2) {
        return false;
      } else {
        return true;
      }
    }

    public void setValueAt(Object value, int row, int col) {
      String fieldName = (String)getValueAt(row, 0);
      FieldProps props = (FieldProps)map.get(fieldName);
      props.setExcluded(((Boolean)(value)).booleanValue());

      super.setValueAt(value, row, col);
    }

  }

}

