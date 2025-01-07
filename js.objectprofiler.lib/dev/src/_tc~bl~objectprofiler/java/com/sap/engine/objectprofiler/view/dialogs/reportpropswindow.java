package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: pavel-b
 * Date: 2005-12-21
 * Time: 9:55:58
 * To change this template use File | Settings | File Templates.
 */
public class ReportPropsWindow extends CommonDialog {
  private JTable reportFilters = null;

  private JRadioButton include = null;
  private JRadioButton exclude = null;

  public ReportPropsWindow(GraphVizualizerPanel vizualizer) {
    super(vizualizer, "Report Options", true);
  }

  protected void init() {
    super.init();

    reportFilters = new JTable();

    include = new JRadioButton("Include Classes");
    exclude = new JRadioButton("Exclude Classes");

    reportFilters.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    setSize(500,400);
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
    JPanel reportFiltersPane = this.getReportFiltersPanel();
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
    c.weighty = 1;
    gridbag.setConstraints(reportFiltersPane, c);
    pane.add(reportFiltersPane);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.gridheight = 1;
    gridbag.setConstraints(buttonPanel, c);
    pane.add(buttonPanel);
  }

  protected void ok() {
    ClassesFilter filter = new ClassesFilter();
    int n = reportFilters.getModel().getRowCount();
    for (int i=0;i<n;i++) {
      String className = (String)reportFilters.getModel().getValueAt(i,0);
      if (className != null && !className.trim().equals("")) {
        filter.addFilter(className);
      }
    }
    vizualizer.getConfiguration().setReportFilter(filter);

    boolean excludeFlag = exclude.isSelected();
    vizualizer.getConfiguration().setExcludeMode(excludeFlag);

    setVisible(false);
    dispose();
  }

  private JPanel getTextPanel() {
    JPanel panel = new JPanel();
    ButtonGroup group = new ButtonGroup();
    group.add(exclude);
    group.add(include);

    boolean flag = vizualizer.getConfiguration().getExcludeMode();
    if (flag) {
      exclude.setSelected(true);
    } else {
      include.setSelected(true);
    }

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
    gridbag.setConstraints(exclude, c);
    panel.add(exclude);

    c.gridy = 1;
    gridbag.setConstraints(include, c);
    panel.add(include);

    return panel;
  }

  private JPanel getReportFiltersPanel() {
    JPanel pane = new JPanel();
    pane.setLayout(new BoxLayout(pane,BoxLayout.X_AXIS));
    pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Graph Building Filtered Classes"));

    ClassesFilter filter = vizualizer.getConfiguration().getReportFilter();
    String[] classNames = filter.getFilters();
    Object[] columnNames = new Object[] {"Class Name/Package Pattern"};
    Object[][] data = new Object[classNames.length][1];
    for (int i=0;i<classNames.length;i++) {
      data[i][0] = classNames[i];
    }

    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    reportFilters.setModel(model);
    JScrollPane scroller = new JScrollPane(reportFilters);
    scroller.setBorder(BorderFactory.createRaisedBevelBorder());
    pane.add(scroller);


    JPanel buttons = new JPanel();

    Dimension dim = new Dimension(100,30);
    JButton addRow = new JButton("Add");
    addRow.setSize(dim);
    addRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ((DefaultTableModel)reportFilters.getModel()).addRow(new Object[] {new String()});
      }
    });

    JButton removeRow = new JButton("Remove");
    removeRow.setSize(dim);
    removeRow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int row = reportFilters.getSelectedRow();
        if (row < 0) {
          row = reportFilters.getModel().getRowCount()-1;
        }
        if (row > -1) {
          ((DefaultTableModel)reportFilters.getModel()).removeRow(row);
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
