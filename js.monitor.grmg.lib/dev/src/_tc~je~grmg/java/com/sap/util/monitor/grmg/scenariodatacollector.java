/*
 *  last change 2004-03-19
 */


/*
 * Author d031360
 * Created on 13.10.2003
 *
 */
package com.sap.util.monitor.grmg;

import java.io.*;
import java.util.*;

public class ScenarioDataCollector {

	ScenarioDataContainer m_dataContainer = null;
	Vector m_allDataContainers = new Vector();
	GrmgXMLFile m_grmgfile;
 
	public ScenarioDataCollector(){	
	}
 
	public void addDataContainer(ScenarioDataContainer scDataCont){
	 m_allDataContainers.add(scDataCont); 
	}
 
  public List getAllDataContainers(){
  	
  	return m_allDataContainers;
  }
  
  public ScenarioPanel getScenarioPanelForScenario(String scenname){
  	
  	for(int i = 0; i < m_allDataContainers.size(); i += 1){
  		if(((ScenarioDataContainer)m_allDataContainers.get(i)).getScenarioName().equals(scenname))
  		 return ((ScenarioDataContainer)m_allDataContainers.get(i)).getScenarioPanel();  		  		
  	}  	
   return null;  	
  }
   
 public GrmgXMLFile getGrmgFile() throws javax.xml.parsers.ParserConfigurationException,
                                         org.xml.sax.SAXException, IOException {
  
  m_grmgfile = new GrmgXMLFile(); 	 
 	 for(int i = 0; i < m_allDataContainers.size(); i += 1){
    m_grmgfile.addScenarios(((ScenarioDataContainer)m_allDataContainers.get(i)).getGrmgXmlDocument());       
 	 }
  return m_grmgfile;	
 }
     
	public ScenarioDataContainer getJointDataContainer(){
	 try{
		return setDataOfJointContainer();
	 }
	 catch(org.xml.sax.SAXException e){return null;} 
	 catch(javax.xml.parsers.ParserConfigurationException e){return null;} 
	}
 
	// this part is specific for the implementation at hand
	//
	private ScenarioDataContainer setDataOfJointContainer() 
				throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException{
 
	 ScenarioPanel jointScpan = new ScenarioPanel();
	 // TODO
	 //ScenarioDataContainer jointScDataCont = new ScenarioDataContainer();
	 // up to now only default grmg xml file
	 // TODO  
	 //GrmgXMLFile jointGrmgFile = new GrmgXMLFile();
 
		for(int j = 0; j < m_allDataContainers.size(); j += 1){
		 try{
			jointScpan.addScenarioPanel(((ScenarioDataLoader)m_allDataContainers.get(j)).getScenarioPanel());	
		 }
		 catch(ScenarioPanelException e){
			System.out.println(e.getMessage());
		 }
		}

	 return null;
	 // noch zu tun: add documents to a joint document !!!!!
	 //Document jointDoc;
	 //((ScenarioDataLoader)allDataContainer.get(j)).getGrmgXmlDocument(); 	
	}
}
