/*
 *  last change 2004-03-19
 */
 
/*
 * Author d031360
 * Created on 13.10.2003
 *
 */
package com.sap.util.monitor.grmg;

import org.w3c.dom.*;
import java.util.*;
import java.io.*;

public class ScenarioDataContainer {
	
  private ScenarioPanel m_panel;
  private GrmgXMLFile m_grmgfile;
  
	private ScenarioDevice m_device = null;
	private Document m_doc = null;
	private InputStream m_XMLresStream = null;
	private Properties m_XMLProperties = new Properties();
	private Class deviceClass = null;
  
	public void addDataContainer(ScenarioDataContainer scDeat){
	}
	
	public ScenarioDataContainer(){
	}
	
	public void setScenarioPanel(ScenarioPanel scpan){
	
	 m_panel = scpan;	
	}

	public void setGrmgXMLFile(GrmgXMLFile xmlFile){
	
	 m_grmgfile = xmlFile;	
	}
 
	public ScenarioPanel getScenarioPanel(){
	
	 return m_panel;	
	}

	public GrmgXMLFile getGrmgXMLFile(){
	
	 return m_grmgfile;	
	}
	
 public String getScenarioName(){
				
	if(m_grmgfile.getScenarioNames().length > 0)
	 return m_grmgfile.getScenarioNames()[0];
	else 
	 return ""; 	 	 		
 }
	
	public Document getGrmgXmlDocument(){

	 return m_doc;
	}

	public void setGrmgXmlDocument(Document grmgDoc){

	 m_doc = grmgDoc;
	}

	public Properties getGrmgProperties(){

	 return m_XMLProperties;
	}

	public void setGrmgProperties(Properties grmgProps){

	 m_XMLProperties = grmgProps;
	}

	public Class getDeviceClass(){

	 return deviceClass;
	}

	public void setDeviceClass(Class devClass){

	 deviceClass = devClass;
	}
}
