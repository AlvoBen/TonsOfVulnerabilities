/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 * 
 */
package com.sap.util.monitor.grmg;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;

public class ScenarioDataLoader {

 //private ScenarioDevice m_device = null;
 ScenarioPanel m_panel = new ScenarioPanel(); 
 Document m_doc = null;  
 private InputStream m_XMLresStream = null;
 private Properties m_XMLProperties = new Properties();
 private Class m_deviceClass = null;
 private ScenarioDataContainer m_scData = new ScenarioDataContainer();
 private ClassLoader m_cLoader;
 private GrmgXMLFile m_GrmgFile = null;
 private String m_hashScenarioName = "";
 private Vector m_allComponentNames = new Vector();

 public ScenarioDataLoader(ClassLoader classLoader){
  if(classLoader == null)
	 ScenarioDataLoader.class.getClassLoader();
	else
   m_cLoader = classLoader;
 }
 
 public ScenarioDataLoader(String deviceName) throws IOException, ClassNotFoundException,
																													ParserConfigurationException, 
																													SAXException,
																													InstantiationException, 
																													IllegalAccessException,
																													ScenarioPanelException{
  this(deviceName, ScenarioDataLoader.class.getClassLoader());
 }

 public ScenarioDataLoader(String deviceName, ClassLoader classLoader) throws IOException, ClassNotFoundException,
                                                          ParserConfigurationException, 
                                                          SAXException,
                                                          InstantiationException, 
                                                          IllegalAccessException,
                                                          ScenarioPanelException{
                                                          	
	if(classLoader == null)
	 ScenarioDataLoader.class.getClassLoader();
  else
	m_cLoader = classLoader;

  load(deviceName);
  /*
 	ScenarioDevice auxDevice;
 	deviceClass = Class.forName(deviceName, true, cLoader);
  m_XMLresStream = deviceClass.getResourceAsStream(deviceName.substring(deviceName.lastIndexOf('.') + 1) + ".xml");
 	
   if(com.sap.util.monitor.grmg.ScenarioDevice.class.isAssignableFrom(deviceClass)){
   	
    setDefaultProperties();
    m_XMLProperties.load(deviceClass.getResourceAsStream(deviceName.substring(deviceName.lastIndexOf('.') + 1) + ".properties"));
    GrmgXMLFile gxml = new GrmgXMLFile(getDefaultCustomizingFile()); 
    gxml.addComponents(m_XMLresStream);
    m_doc = gxml.getDocument();
  	m_device = (com.sap.util.monitor.grmg.ScenarioDevice)deviceClass.newInstance();
  	m_panel.addScenarioDevice(m_device);
   }
       
   if(com.sap.util.monitor.grmg.ScenarioPanel.class.isAssignableFrom(deviceClass)){

    m_XMLProperties.load(deviceClass.getResourceAsStream(deviceName.substring(deviceName.lastIndexOf('.') + 1) + ".properties"));
    GrmgXMLFile gxml = new GrmgXMLFile(m_XMLresStream); 
    m_doc = gxml.getDocument();
   	m_panel = (com.sap.util.monitor.grmg.ScenarioPanel)deviceClass.newInstance();
   	StringTokenizer devices = new StringTokenizer(m_XMLProperties.getProperty("ScenarioDeviceList"), "!!");
   	
   	 while(devices.hasMoreTokens()){   	 	
      auxDevice = (com.sap.util.monitor.grmg.ScenarioDevice)(Class.forName(devices.nextToken()).newInstance());
   	 	m_panel.addScenarioDevice(auxDevice);
   	 }  
   }
   scData.setScenarioPanel(m_panel);
	 scData.setGrmgXmlDocument(m_doc);
   scData.setGrmgXMLFile(new GrmgXMLFile(m_doc));
   scData.setDeviceClass(deviceClass);
   scData.setGrmgProperties(m_XMLProperties);  
  */  
 }

 public final void load(String deviceName) throws IOException, ClassNotFoundException,
                                            ParserConfigurationException, SAXException,
                                            InstantiationException, IllegalAccessException,
                                            ScenarioPanelException{
 	
 	//boolean allowAdd = false;
 	
	m_deviceClass = Class.forName(deviceName, true, m_cLoader);
	m_XMLresStream = m_deviceClass.getResourceAsStream(deviceName.substring(deviceName.lastIndexOf('.') + 1) + ".xml");
	m_XMLProperties.load(m_deviceClass.getResourceAsStream(deviceName.substring(deviceName.lastIndexOf('.') + 1) + ".properties"));
 	
 	GrmgXMLFile tempGrmgFile = new GrmgXMLFile(m_XMLresStream);
 	Document tempDocument = tempGrmgFile.getDocument();
 		 	
	 if(m_GrmgFile == null){
	 	//allowAdd = true;
	  m_GrmgFile = new GrmgXMLFile(tempDocument);	  
	 }
	 else{
	  if(tempGrmgFile != null && disjointLists(m_GrmgFile.getComponentNames(), tempGrmgFile.getComponentNames())){ 
	   //allowAdd = true;
	   m_GrmgFile.addComponents(tempGrmgFile.getDocument());
	  }
	  else
		 throw new ScenarioPanelException("Upload failed due to nonexisting GRMG file or duplicate component names.");	   
	 }

   m_doc = m_GrmgFile.getDocument();
   m_allComponentNames.addAll(m_GrmgFile.getComponentNames());
   
   if(com.sap.util.monitor.grmg.ScenarioDevice.class.isAssignableFrom(m_deviceClass)){
	  m_panel.addScenarioDevice((com.sap.util.monitor.grmg.ScenarioDevice)m_deviceClass.newInstance());   	
   }
       
	 if(com.sap.util.monitor.grmg.ScenarioPanel.class.isAssignableFrom(m_deviceClass)){
    m_panel.addScenarioPanel((com.sap.util.monitor.grmg.ScenarioPanel)m_deviceClass.newInstance());   
	 }
	
  // generate a complete GRMG customizing file containing a single scenario 
  // with various loaded components and hash coded scenario name
	GrmgXMLFile scDataFile = new GrmgXMLFile();
	scDataFile.addComponents(m_doc);
	scDataFile.setScenarioName(generateHashCode(m_allComponentNames));

	m_scData.setScenarioPanel(m_panel);
	m_scData.setGrmgXMLFile(scDataFile);
	//scData.setScenarioName(generateHashCode(allComponentNames));
  // less important:
	m_scData.setGrmgXmlDocument(m_doc);
	m_scData.setGrmgProperties(m_XMLProperties);    	 	
	m_scData.setDeviceClass(m_deviceClass);
 }

 public ScenarioDataContainer getDataContainer(){
 	
 	return m_scData;
 }
 
 // all devices will be put into the private ScenarioPanel
 // important for sending panel data 
 
 public ScenarioPanel getScenarioPanel(){
 	
 	return m_panel;
 }

 // important for sending XML data 
 public Document getGrmgXmlDocument(){
 	
 	return m_doc;
 }

 // important for sending Properties data  
 public Properties getGrmgProperties(){
 	
 	return m_XMLProperties;
 }

 public Class getDeviceClass(){
 	
 	return m_deviceClass;
 }
 	
 // returns 8-character (digit) hashcode of the concatenated String values of the list  
 private String generateHashCode(List items){ 
  
  String jointString = "";
   for(int i = 0; i < items.size(); i += 1){
    jointString += items.get(i).toString(); 	
   }	
	int codeNum  =  jointString.hashCode();
	int codesmall = codeNum % 100000000;	
	return new Integer(codesmall).toString();
 }
 
 private boolean disjointLists(List listone, List listtwo){
  for(int i = 0; i < listone.size(); i += 1){
   if(listtwo.contains(listone.get(i))) 	
 	  return false;
  }
  return true;
 }	
 
 private Document getDefaultCustomizingFile() throws ParserConfigurationException{
 	   
  GrmgCustomizing defaultCust = new GrmgCustomizing();
  GrmgScenario defaultScen = new GrmgScenario();
  GrmgText text = new GrmgText();

  defaultCust.setGrmgRunsFlag(
    m_XMLProperties.getProperty("grmgruns").equalsIgnoreCase(
      GrmgCustomizingXmlGenerator.X_SIGN_STRING
    )
  );
  defaultScen.setName(m_XMLProperties.getProperty("scenname"));
  defaultScen.setVersion(m_XMLProperties.getProperty("scenversion"));
  defaultScen.setInstance(m_XMLProperties.getProperty("sceninst"));
  defaultScen.setStartUrl(m_XMLProperties.getProperty("scenstarturl"));

  text.setLanguage(m_XMLProperties.getProperty("scenlangu"));
  text.setDescription(m_XMLProperties.getProperty("scendesc"));

  defaultScen.setText(text);
  defaultCust.addScenario(defaultScen);

  return GrmgCustomizingXmlGenerator.buildDocument(defaultCust);
 }

 private void setDefaultProperties(){

  m_XMLProperties.setProperty("grmgruns",GrmgCustomizingXmlGenerator.X_SIGN_STRING);
  m_XMLProperties.setProperty("scenname","default");
  m_XMLProperties.setProperty("scenversion","001");
  m_XMLProperties.setProperty("sceninst","100");
  m_XMLProperties.setProperty("scentype","URL");
  m_XMLProperties.setProperty("scenstarturl","http://localhost");
  m_XMLProperties.setProperty("scenstartmod","Unknown");
  m_XMLProperties.setProperty("scenlangu","EN");
  m_XMLProperties.setProperty("scendesc","default GRMG Scenario");
 }  
}
