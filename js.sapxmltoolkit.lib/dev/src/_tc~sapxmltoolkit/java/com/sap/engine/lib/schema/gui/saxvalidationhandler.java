/*
 * Created on 2004-12-10
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.gui;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SAXValidationHandler extends DefaultHandler {
	
	private XMLValidationVisualizationPanel xmlValidationVisualPanel;
	
	protected SAXValidationHandler(XMLValidationVisualizationPanel xmlValidationVisualPanel) {
		this.xmlValidationVisualPanel = xmlValidationVisualPanel;
	}
	
	public void error(SAXParseException exc) throws SAXException {
		xmlValidationVisualPanel.displayError(exc.getMessage());
	}
	
	public void fatalError(SAXParseException exc) throws SAXException {
		xmlValidationVisualPanel.displayError(exc.getMessage());
	}
}
