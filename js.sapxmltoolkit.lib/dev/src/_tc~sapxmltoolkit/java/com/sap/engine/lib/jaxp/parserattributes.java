/*
 * Created on 2005-2-11
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.jaxp;

import java.util.Vector;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.Features;
import com.sap.engine.lib.xml.parser.JAXPProperties;

/**
 * @author Ivan-M
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ParserAttributes {
	
	private Vector attributeNames;
	private Vector attributeValues;
	
	protected ParserAttributes() {
		attributeNames = new Vector();
		attributeValues = new Vector();
	}
	
	public void set(String name, Object value) {
		expectAttributeName(name);
		if(attributeNames.size() == 0) {
			attributeNames.add(name);
			attributeValues.add(value);
		}
		int index = 0;
		while(!attributeNames.get(index).equals(name) && ++index < attributeNames.size());
		if(index >= attributeNames.size()) {
			attributeNames.add(name);
			attributeValues.add(value);
		} else {
			attributeValues.set(index, value);
		}
	}
	
	private void expectAttributeName(String name) {
		if(name == null) {
			throw new IllegalArgumentException("Name is null.");
		}
		if (!Features.SUPPORTED.contains(name) && !JAXPProperties.SUPPORTED.contains(name)) {
			throw new IllegalArgumentException("Attribute '" + name + "' is not supported");
		}
	}

	public Object get(String name) {
		expectAttributeName(name);
		if(attributeNames.size() == 0) {
			return(null);
		}
		int index = 0;
		while(!attributeNames.get(index).equals(name) && ++index < attributeNames.size());
		if(index >= attributeNames.size()) {
			return(null);
		}
		return(attributeValues.get(index));
	}
	
	public Object get(int index) {
		return(attributeValues.get(index));
	}
	
	public String getName(int index) {
		return((String)(attributeNames.get(index)));
	}
	
	public int size() {
		return(attributeNames.size());
	}
	
	public static void main(String[] args) throws Exception {
		ParserAttributes attributes = new ParserAttributes();
		
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name") + " null expected");
		attributes.set("name1", "value1");
		attributes.set("name2", "value2");
		attributes.set("name3", "value3");
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name1") + " value1 expected");
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name2") + " value2 expected");
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name3") + " value3 expected");
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name4") + " null expected");
		attributes.set("name2", "value2.1");
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name2") + " value2.1 expected");
		attributes.set("name2", null);
		LogWriter.getSystemLogWriter().println("value : " + attributes.get("name2") + " null expected");
	}
	
}

