﻿/*
 *  last change 2004-03-19
 */

/*
 * Author d031360
 * Created on 13.10.2003
 *
 */
package com.sap.util.monitor.grmg;

import java.util.*;

public final class StandardScenarioDataCollector extends ScenarioDataCollector{

 ScenarioDataContainer dataContainer = null;
 ArrayList allDataContainer = new ArrayList();
 
 // non-public constructor
 StandardScenarioDataCollector(){	
 }
 
 public void addDataContainer(ScenarioDataContainer scDataCont){
  allDataContainer.add(scDataCont); 
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
 
   for(int j = 0; j < allDataContainer.size(); j += 1){
    try{
 	   jointScpan.addScenarioPanel(((ScenarioDataLoader)allDataContainer.get(j)).getScenarioPanel());	
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
