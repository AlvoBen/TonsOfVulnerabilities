package com.sap.engine.objectprofiler.view.dialogs;

import com.sap.engine.objectprofiler.view.GraphClient;
import com.sap.engine.objectprofiler.view.GraphVizualizerPanel;
import com.sap.engine.objectprofiler.view.GraphVizualizer;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.FileInputStream;

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
 * Date: 2005-6-28
 * Time: 10:57:41
 */
public class ConnectWindow extends CommonDialog {
  public static final String PROPS_FILE_NAME = "connection.properties";

  JTextField host = null;
  JTextField port = null;
  JTextField serverID = null;
  JTextField user = null;
  JPasswordField password = null;

  public ConnectWindow(GraphVizualizerPanel viz) {
    super(viz, "Connect", true);
  }

  protected void init() {
    super.init();

    host = new JTextField();
    port = new JTextField();
    serverID = new JTextField();
    user = new JTextField();
    password = new JPasswordField();

    ok.setText("Connect");
    loadProps();

    setSize(250,200);
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
    String[] args = new String[] {host.getText(), port.getText(), serverID.getText(), user.getText(), new String(password.getPassword())};
    saveProps();

    Frame frame = (Frame)vizualizer.getTopLevelAncestor();
    if (frame instanceof GraphClient) {
      ((GraphClient)frame).setConnectionProperties(args);
    }

    setVisible(false);
    dispose();
  }

  private void loadProps() {
    Properties props = new Properties();

    try {
      props.load(new FileInputStream(PROPS_FILE_NAME));
      host.setText((String)props.get("host"));
      port.setText((String)props.get("port"));
      serverID.setText((String)props.get("serverID"));
      user.setText((String)props.get("uid"));
    } catch (Exception e) {
      GraphVizualizer.showMessageBox(e.getMessage());
    }
  }

  private void saveProps() {
    Properties props = new Properties();
    props.put("host", host.getText());
    props.put("port", port.getText());
    props.put("serverID", serverID.getText());
    props.put("uid", user.getText());

    try {
      props.store(new FileOutputStream(PROPS_FILE_NAME), "");
    } catch (Exception e) {
      GraphVizualizer.showMessageBox(e.getMessage());
    }
  }

  private JPanel getTextPanel() {
    JPanel panel = new JPanel();

    JLabel hostLabel = new JLabel("Host: ");
    JLabel portLabel = new JLabel("Port: ");
    JLabel serverIDLabel = new JLabel("Server ID: ");
    JLabel userLabel = new JLabel("User: ");
    JLabel passwordLabel = new JLabel("Password: ");

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
    gridbag.setConstraints(hostLabel, c);
    panel.add(hostLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 0;
    gridbag.setConstraints(host, c);
    panel.add(host);

    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    gridbag.setConstraints(portLabel, c);
    panel.add(portLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 1;
    gridbag.setConstraints(port, c);
    panel.add(port);

    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 2;
    gridbag.setConstraints(serverIDLabel, c);
    panel.add(serverIDLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 2;
    gridbag.setConstraints(serverID, c);
    panel.add(serverID);

    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 3;
    gridbag.setConstraints(userLabel, c);
    panel.add(userLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 3;
    gridbag.setConstraints(user, c);
    panel.add(user);

    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 4;
    gridbag.setConstraints(passwordLabel, c);
    panel.add(passwordLabel);

    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 4;
    gridbag.setConstraints(password, c);
    panel.add(password);

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

}
