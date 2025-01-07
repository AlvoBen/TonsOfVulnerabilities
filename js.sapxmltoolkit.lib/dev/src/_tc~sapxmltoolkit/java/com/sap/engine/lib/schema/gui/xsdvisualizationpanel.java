/*
 * Created on 2004-11-29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.sap.engine.lib.schema.components.Schema;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XSDVisualizationPanel extends JPanel {
	
	protected XSDVisualizationPanel() {
		initView();
	}
	
	private void initView() {
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.insets = new Insets(1, 1, 1, 1);
		add(createVerticalSplitPane(createVerticalSplitPane(createPanel_SelectedXsdsPanel(), createPanel_SchemaStructurePanel(), 0.3), createPanel_SchemaStructureErrorsPanel(), 0.7), gridBagConstr);
		
		gridBagConstr.insets = new Insets(5, 5, 5, 5);
		gridBagConstr.gridy = 1;
		gridBagConstr.weightx = 0;
		gridBagConstr.weighty = 0;
		gridBagConstr.anchor = GridBagConstraints.WEST;
		JLabel xsdValidityIdentificationLabel = new JLabel("Correct");
		add(xsdValidityIdentificationLabel, gridBagConstr);
	}
	
	private JSplitPane createVerticalSplitPane(Component topComponent, Component bottomComponent, double resizeWeight) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(topComponent);
		splitPane.setBottomComponent(bottomComponent);
		splitPane.setResizeWeight(resizeWeight);
		return(splitPane);
	}
	
	private JPanel createPanel_SelectedXsdsPanel() {
		JPanel selectedXsdsPanel = new JPanel();
		selectedXsdsPanel.setLayout(new GridBagLayout());
		selectedXsdsPanel.setBorder(BorderFactory.createEtchedBorder());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		selectedXsdsPanel.add(createPanel_SelectedXsdsViewerPanel(), gridBagConstr);
		
		gridBagConstr.gridx = 1;
		gridBagConstr.weightx = 0;
		gridBagConstr.weighty = 0;
		selectedXsdsPanel.add(createPanel_SelectedXsdsActionPanel(), gridBagConstr);
		
		return(selectedXsdsPanel);
	}
	
	private JPanel createPanel_SelectedXsdsViewerPanel() {
		JPanel selectedXsdsViewerPanel = new JPanel();
		selectedXsdsViewerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.insets = new Insets(1, 1, 1, 1);
		gridBagConstr.anchor = GridBagConstraints.WEST;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		JLabel xsdsLabel = new JLabel("Xsds:");
		selectedXsdsViewerPanel.add(xsdsLabel, gridBagConstr);
		
		gridBagConstr.gridy = 1;
		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.anchor = GridBagConstraints.CENTER;
		selectedXsdsViewerPanel.add(createScrollPane_SelectedXsdsTextAreaScrollPane(), gridBagConstr);
		
		return(selectedXsdsViewerPanel);
	}
	
	private JScrollPane createScrollPane_SelectedXsdsTextAreaScrollPane() {
		JTextArea selectedXsdsTextArea = new JTextArea();
		selectedXsdsTextArea.enable(false);
		return(new JScrollPane(selectedXsdsTextArea));
	}
	
	private JPanel createPanel_SelectedXsdsActionPanel() {
		JPanel selectedXsdsActionPanel = new JPanel();
		selectedXsdsActionPanel.setLayout(new GridBagLayout());
		selectedXsdsActionPanel.setBorder(BorderFactory.createEtchedBorder());
		GridBagConstraints gridBagCostr = new GridBagConstraints(); 
		
		gridBagCostr.gridx = 0;
		gridBagCostr.gridy = 0;
		gridBagCostr.fill = GridBagConstraints.HORIZONTAL;
		JButton addButton = new JButton("Add");
		selectedXsdsActionPanel.add(addButton, gridBagCostr);

		gridBagCostr.gridy = 1;
		JButton removeButton = new JButton("Remove");
		selectedXsdsActionPanel.add(removeButton, gridBagCostr);
		
		gridBagCostr.gridy = 2;
		gridBagCostr.weighty = 0.1;
		gridBagCostr.anchor = GridBagConstraints.NORTH;
		JButton loadButton = new JButton("Load");
		selectedXsdsActionPanel.add(loadButton, gridBagCostr);
		
		return(selectedXsdsActionPanel);
	}
	
	private JPanel createPanel_SchemaStructurePanel() {
		JPanel xsdStructureVisualPanel = new JPanel();
		xsdStructureVisualPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.insets = new Insets(1, 1, 1, 1);
		gridBagConstr.anchor = GridBagConstraints.WEST;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		JLabel schemaStructureLabel = new JLabel("Schema structure :");
		xsdStructureVisualPanel.add(schemaStructureLabel, gridBagConstr);

		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.gridy = 1;
		xsdStructureVisualPanel.add(createScrollPane_SchemaStructureScrollPane(), gridBagConstr);
		
		return(xsdStructureVisualPanel);
	} 
	
	private JScrollPane createScrollPane_SchemaStructureScrollPane() {
		JTextArea schemaStructureTextArea = new JTextArea();
		schemaStructureTextArea.enable(false);
		return(new JScrollPane(schemaStructureTextArea));
	}
	
	private JPanel createPanel_SchemaStructureErrorsPanel() {
		JPanel xsdStructureErrorsVisualPanel = new JPanel();
		xsdStructureErrorsVisualPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.insets = new Insets(1, 1, 1, 1);
		gridBagConstr.anchor = GridBagConstraints.WEST;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		JLabel schemaStructureErrorsLabel = new JLabel("Errors :");
		xsdStructureErrorsVisualPanel.add(schemaStructureErrorsLabel, gridBagConstr);

		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.gridy = 1;
		xsdStructureErrorsVisualPanel.add(createScrollPane_SchemaStructureErrorsTextAreaScrollPane(), gridBagConstr);
		
		return(xsdStructureErrorsVisualPanel);
	} 
	
	private JScrollPane createScrollPane_SchemaStructureErrorsTextAreaScrollPane() {
		JTextArea schemaStructureErrorsTextArea = new JTextArea();
		schemaStructureErrorsTextArea.enable(false);
		return(new JScrollPane(schemaStructureErrorsTextArea));
	}
	
	protected Schema getXMLSchema() {
		return(null);
	}
}

