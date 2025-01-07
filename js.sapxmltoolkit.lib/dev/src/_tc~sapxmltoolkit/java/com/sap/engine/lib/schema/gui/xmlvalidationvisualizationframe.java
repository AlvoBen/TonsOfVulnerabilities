/*
 * Created on 2004-11-29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.sap.engine.lib.schema.components.Schema;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLValidationVisualizationFrame extends JFrame {
	
	private XMLValidationVisualizationPanel xmlValidationVisualPanel;
	private XSDVisualizationPanel xsdVisualPanel;
	
	public XMLValidationVisualizationFrame() {
		super("SAP XML Validation Tool");
		initFrame();
		initView();
	}
	
	private void initView() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		xsdVisualPanel = new XSDVisualizationPanel();
		xmlValidationVisualPanel = new XMLValidationVisualizationPanel(this);
		splitPane.setLeftComponent(xmlValidationVisualPanel);
		splitPane.setRightComponent(xsdVisualPanel);
		splitPane.setOneTouchExpandable(true); 
		splitPane.setResizeWeight(0.5);
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}
	
	private void initFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 1250, 900);
	}
	
	public static void main(String[] args) throws Exception {
		XMLValidationVisualizationFrame frame = new XMLValidationVisualizationFrame();
		frame.setVisible(true);
	}
	
	protected Schema getXMLSchema() {
		return(xsdVisualPanel.getXMLSchema());
	}
}
