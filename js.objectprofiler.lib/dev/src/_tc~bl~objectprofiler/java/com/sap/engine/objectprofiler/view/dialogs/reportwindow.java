package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.graph.GraphReport;
import com.sap.engine.objectprofiler.view.GraphVizualizer;
import com.sap.engine.objectprofiler.view.CustomFileFilter;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

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
 * Date: 2005-12-6
 * Time: 14:44:01
 */
public class ReportWindow extends CommonDialog {
  private JButton filters = null;
  private JTextArea text = null;

  private GraphReport report = null;

  public ReportWindow(GraphVizualizerPanel vizualizer, GraphReport report) {
    super(vizualizer, "Report", true);

    this.report = report;
    this.vizualizer = vizualizer;
  }



  private void showReport() {
    text.setEditable(true);

    report.setExcludeList(vizualizer.getConfiguration().getExcludeMode());
    report.setClassesFilter(vizualizer.getConfiguration().getReportFilter());

    StringWriter sw = new StringWriter();
    PrintWriter writer = new PrintWriter(sw);
    report.printReport(writer);
    writer.flush();

    text.setText(sw.toString());
    text.setEditable(false);
  }


  protected void init() {
    super.init();

    filters = new JButton("Filters...");
    text = new JTextArea();

    ok.setText("Save");

    setSize(500,500);

    showReport();
  }

  protected void createComponents() {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2,2,2,2);

    Container pane = getContentPane();
    pane.setLayout(gridbag);

    JPanel textPanel = getTextPanel();
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
    JFileChooser fc = new JFileChooser();
    CustomFileFilter imf = new CustomFileFilter("txt", "Text Files");

    fc.setFileFilter(imf);
    int res = fc.showSaveDialog(this);

    if (res == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      //graph.writeInXML(file.getAbsolutePath());
      try {
        String fileName = file.getAbsolutePath();
        if (!fileName.toLowerCase().endsWith(".txt")) {
          fileName = fileName + ".txt";
        }
        FileWriter fw = new FileWriter(fileName);
        fw.write(text.getText());
        fw.close();
      } catch (Exception e) {
        GraphVizualizer.showMessageBox(e);
      }

    }
  }



  private JPanel getTextPanel() {
    JPanel panel = new JPanel();
    JScrollPane scroller = new JScrollPane(text);

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

  public void filters() {
    ReportPropsWindow window = new ReportPropsWindow(vizualizer);
    window.showDialog();

    showReport();
  }

  private JPanel getButtonsPanel() {
    filters.setPreferredSize(bsize);
    filters.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        filters();
      }
    });


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
    gridbag.setConstraints(filters, c);
    panel.add(filters);

    c.weightx = 0;
    c.gridx = 2;
    gridbag.setConstraints(ok, c);
    panel.add(ok);

    c.weightx = 0;
    c.gridx = 3;
    gridbag.setConstraints(close, c);
    panel.add(close);

    return panel;
  }
}
