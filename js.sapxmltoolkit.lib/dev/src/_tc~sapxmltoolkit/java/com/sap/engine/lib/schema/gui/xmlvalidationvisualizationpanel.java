/*
 * Created on 2004-11-29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.components.Schema;
import com.sap.engine.lib.xml.parser.JAXPProperties;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLValidationVisualizationPanel extends JPanel implements ActionListener {
	
	private JTextArea textArea_XmlVisual;
	private JTextArea textArea_ErrorsVisual;
	private JLabel errorsIdentificationLabel;
	private JLabel xmlLocationIdentificationLabel;
	private XMLValidationVisualizationFrame frame;
	private SAXParser parser;
	private String xmlLocation;
	private SAXValidationHandler handler;
	
	private static final String LOAD_XML_FILE_ACTION_COMMAND = "load.xml.file";
	private static final String VALIDATE_XML_FILE_ACTION_COMMAND = "validate.xml.file";
	private static final String RELOAD_XML_FILE_ACTION_COMMAND = "reload.xml.file";
	
	protected XMLValidationVisualizationPanel(XMLValidationVisualizationFrame frame) {
		this.frame = frame;
		initView();
		initParser();
		handler = new SAXValidationHandler(this);
	}
	
	private void initParser() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			parser = factory.newSAXParser();
			parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_LANGUAGE, Constants.SCHEMA_LANGUAGE);
		} catch(Exception exc) {
			//$JL-EXC$
			exc.printStackTrace();
		}
	}
	
	private void initView() {
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		add(createPanel_Action(), gridBagConstraints);

		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;  
		add(createXmlVisualErrorsVisualSplitPane(), gridBagConstraints);
		
		gridBagConstraints.weighty = 0;
		errorsIdentificationLabel = new JLabel();
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		add(errorsIdentificationLabel, gridBagConstraints);
	}
	
	private JPanel createPanel_Action() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		controlPanel.setBorder(BorderFactory.createEtchedBorder());
		
		JButton xmlChooserButton = new JButton("choose xml");
		xmlChooserButton.setActionCommand(LOAD_XML_FILE_ACTION_COMMAND);
		xmlChooserButton.addActionListener(this);
		controlPanel.add(xmlChooserButton);
		
		JButton xmlValidateButton = new JButton("validate xml");
		xmlValidateButton.setActionCommand(VALIDATE_XML_FILE_ACTION_COMMAND);
		xmlValidateButton.addActionListener(this);
		controlPanel.add(xmlValidateButton);
		
		JButton xmlReloadButton = new JButton("reload xml");
		xmlReloadButton.setActionCommand(RELOAD_XML_FILE_ACTION_COMMAND);
		xmlReloadButton.addActionListener(this);
		controlPanel.add(xmlReloadButton);
		
		return(controlPanel);
	}
	
	private JSplitPane createXmlVisualErrorsVisualSplitPane() {
		JSplitPane xmlVizualFromErrorsVizualSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		xmlVizualFromErrorsVizualSplitPane.setResizeWeight(0.7);
		xmlVizualFromErrorsVizualSplitPane.setOneTouchExpandable(true);
		xmlVizualFromErrorsVizualSplitPane.setTopComponent(createPanel_XMLVisual());
		xmlVizualFromErrorsVizualSplitPane.setBottomComponent(createPanel_ErrorsVisual());
		return(xmlVizualFromErrorsVizualSplitPane);
	}
	
	private JPanel createPanel_XMLVisual() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		 
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.anchor = GridBagConstraints.WEST;
		gridBagConstr.insets = new Insets(3, 3, 3, 3);
		JLabel identificationLabel = new JLabel("XML:");
		panel.add(identificationLabel, gridBagConstr);
		
		gridBagConstr.gridx = 1;
		xmlLocationIdentificationLabel = new JLabel();
		panel.add(xmlLocationIdentificationLabel, gridBagConstr);
	
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 1;
		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.gridwidth = 2;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		panel.add(createScrollPane_XmlVisual(), gridBagConstr);
		
		return(panel);
	}
	
	private JPanel createPanel_ErrorsVisual() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstr = new GridBagConstraints();
		 
		gridBagConstr.gridx = 0;
		gridBagConstr.gridy = 0;
		gridBagConstr.anchor = GridBagConstraints.WEST;
		gridBagConstr.insets = new Insets(3, 3, 3, 3);
		JLabel identificationLabel = new JLabel("Errors:");
		panel.add(identificationLabel, gridBagConstr);

		gridBagConstr.gridy = 1;
		gridBagConstr.weightx = 1;
		gridBagConstr.weighty = 1;
		gridBagConstr.fill = GridBagConstraints.BOTH;
		panel.add(createScrollPane_ErrorsVisual(), gridBagConstr);
		
		return(panel);
	}
	
	private JScrollPane createScrollPane_XmlVisual() {
		textArea_XmlVisual = new JTextArea();
		textArea_XmlVisual.setEditable(false);
		return(new JScrollPane(textArea_XmlVisual));
	}
	
	private JScrollPane createScrollPane_ErrorsVisual() {
		textArea_ErrorsVisual = new JTextArea();
		textArea_ErrorsVisual.setEditable(false);
		textArea_ErrorsVisual.setForeground(Color.RED);
		return(new JScrollPane(textArea_ErrorsVisual));
	} 
	
	public void actionPerformed(ActionEvent actionEvent) {
		String actionCommand = actionEvent.getActionCommand();
		if(actionCommand.equals(LOAD_XML_FILE_ACTION_COMMAND)) {
			showXml_UseFileChooser();
		} else if(actionCommand.equals(VALIDATE_XML_FILE_ACTION_COMMAND)) {
			validateXml();
		} else if(actionCommand.equals(RELOAD_XML_FILE_ACTION_COMMAND)) {
			showXml();
		} 
	}
	
	private void saveXml(String xmlLocation) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(xmlLocation);
			byte[] textArea_XmlVisualTextBytes = textArea_XmlVisual.getText().getBytes(); //$JL-I18N$
			fileOutputStream.write(textArea_XmlVisualTextBytes);
			fileOutputStream.flush();
		} catch(Exception exc) {
			displayError(exc.getMessage());
		} finally {
			if(fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch(IOException ioExc) {
					//$JL-EXC$			
				}
			}
		}
	}
	
	private void showXml_UseFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(frame);
		if(option == JFileChooser.APPROVE_OPTION) {
			try {
				File selectedFile = fileChooser.getSelectedFile();
				xmlLocation = selectedFile.getCanonicalPath();
				xmlLocationIdentificationLabel.setText(xmlLocation);
				showXml();
			} catch(Exception exc) {
				displayError(exc.getMessage());
			}
		}
	}
	
	private void showXml() {
		FileInputStream fileInputStream = null;
		try {
			reset();
			fileInputStream = new FileInputStream(xmlLocation);
			textArea_XmlVisual.setText("");  
			byte[] buffer = new byte[1024];
			int readedBytesCount = -1;
			while((readedBytesCount = fileInputStream.read(buffer)) > 0) {
				textArea_XmlVisual.append(new String(buffer, 0, readedBytesCount)); //$JL-I18N$
			}
		} catch(Exception exc) {
			displayError(exc.getMessage());
		} finally {
			if(fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch(IOException ioExc) {
					//$JL-EXC$				
				}
			}
		}
	}
	
	private void validateXml() {
		try {
			reset();
			Schema schema = frame.getXMLSchema();
			parser.setProperty(JAXPProperties.PROPERTY_SCHEMA_OBJECT, schema);
			parser.parse(xmlLocation, handler);
		} catch(Exception exc) {
			displayError(exc instanceof NestedSAXParseException ? ((NestedSAXParseException)exc).getCause().getMessage() : exc.getMessage());
		} finally {
			if(textArea_ErrorsVisual.getText() != null) {
				errorsIdentificationLabel.setForeground(Color.RED);
				errorsIdentificationLabel.setText("Errors");		
			} else {
				errorsIdentificationLabel.setForeground(Color.GREEN);
				errorsIdentificationLabel.setText("No errors");
			}
		}
	}
	
	private void reset() {
		textArea_ErrorsVisual.setText("");
		errorsIdentificationLabel.setText("");
	}
	
	protected void displayError(String errorMessage) {
		textArea_ErrorsVisual.append(errorMessage);
	}
}
