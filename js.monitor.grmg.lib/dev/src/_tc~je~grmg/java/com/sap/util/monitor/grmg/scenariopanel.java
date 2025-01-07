/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 * 
 */
package com.sap.util.monitor.grmg;

import java.net.*;
import java.io.*;
import java.util.*;

 /**
 <code>ScenarioPanel</code> is a container for <code>ScenarioDevice</code> objects.
 <code>ScenarioDevice</code>s can be added to and retrieved from the 
 <code>ScenarioPanel</code>. Dynamic loading of new <code>ScenarioDevice</code>s 
 is possible. <code>GRMFactory</code> manufactures a GRMG Scenario test from a 
 <code>ScenarioPanel</code>.
 */

public class ScenarioPanel {

 private ArrayList scDevList = new ArrayList();

	/**
	Adds a <code>ScenarioDevice</code> to the <code>ScenarioPanel</code>.	
	* @param  scdev			the <code>ScenarioDevice</code> to be added 
	**/
 public void addScenarioDevice(ScenarioDevice scdev){
 	
 	addScenarioDevice(new ScenarioDevice[] {scdev});
 }

 /**
 Adds an array of <code>ScenarioDevice</code>s to the <code>ScenarioPanel</code>.	
 * @param  scdevarr			the array of <code>ScenarioDevice</code>s to be added 
 **/
 public void addScenarioDevice(ScenarioDevice[] scdevarr){
 	
 	if(scdevarr.length > 0){
 	 for(int j = 0; j < scdevarr.length; j += 1)
    scDevList.add(scdevarr[j]);
  }
 }

 /**
 Adds a <code>ScenarioDevice</code> with name <code>classname</code>
 to the <code>ScenarioPanel</code>.	
 * @param  classname			the name (String) of the <code>ScenarioDevice</code> to be added 
 **/
 public void addScenarioDevice(String classname) throws ScenarioPanelException{
 	
  try {
   Class cls = Class.forName(classname);
    if(ScenarioDevice.class.isAssignableFrom(cls))
     scDevList.add(cls.newInstance());
  }
  catch(ClassNotFoundException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
  catch(IllegalAccessException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
  catch(InstantiationException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
 }

 /**
 Adds a <code>ScenarioDevice</code> with name <code>classname</code>
 to the <code>ScenarioPanel</code>.	The class will be loaded from the given jar file
 * @param  jarfile			the name (String) of the jar file from which the 
 * <code>ScenarioDevice</code> will be loaded 
 * @param  classname			the name (String) of the <code>ScenarioDevice</code> to be added 
 **/
 public void addScenarioDevice(String jarfile, String classname) throws ScenarioPanelException{
 
  File jardir = new File(jarfile);
  addScenarioDevice(jardir, classname);
 }

 /**
 Adds a <code>ScenarioDevice</code> with name <code>classname</code>
 to the <code>ScenarioPanel</code>.	The class will be loaded from the given jar file
 * @param  jardir			the <code>File</code> representing the jar file from which the 
 * <code>ScenarioDevice</code> will be loaded 
 * @param  classname			the name (String) of the <code>ScenarioDevice</code> to be added 
 **/
 public void addScenarioDevice(File jardir, String classname) throws ScenarioPanelException{
                                                               	
  try{                                                             	
   URL url = jardir.toURL();
   addScenarioDevice(url, classname);  
  }
  catch(MalformedURLException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
 }

 /**
 Adds a <code>ScenarioDevice</code> with name <code>classname</code>
 to the <code>ScenarioPanel</code>.	The class will be loaded from the given URL
 * @param  url			the <code>URL</code> from which the 
 * <code>ScenarioDevice</code> will be loaded 
 * @param  classname			the name (String) of the <code>ScenarioDevice</code> to be added 
 **/
 public void addScenarioDevice(URL url, String classname) throws ScenarioPanelException{
 	
  try{ 
   URLClassLoader urlload = new URLClassLoader(new URL[] {url}, ScenarioPanel.class.getClassLoader());
   Class cls = urlload.loadClass(classname);
    if(ScenarioDevice.class.isAssignableFrom(cls)){
     scDevList.add(cls.newInstance());
    } 
  }
  catch(ClassNotFoundException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
  catch(IllegalAccessException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }
  catch(InstantiationException e){
  	throw new ScenarioPanelException(e.getMessage()); 
  }    
 }

 public void addScenarioPanel(ScenarioPanel scpan) throws ScenarioPanelException{
 	
 	if(!scpan.getScenarioDeviceCollection().equals(scDevList))
 	 scDevList.addAll(scpan.getScenarioDeviceCollection());
 	else
 	 throw new ScenarioPanelException("Panel may not be added to itself.");  	
 }
 	  
  /**
  Returns an ArrayList of the <code>ScenarioDevice</code>s contained in the
  <code>ScenarioPanel</code>.
  * @return ArrayList of the <code>ScenarioDevice</code>s contained in the
  <code>ScenarioPanel</code>
  **/
 public ArrayList getScenarioDeviceCollection(){

	return scDevList;
 }

  /**
	Returns an array of the <code>ScenarioDevice</code>s contained in the
	<code>ScenarioPanel</code>.
	* @return array of the <code>ScenarioDevice</code>s contained in the
	<code>ScenarioPanel</code>
	**/
 public ScenarioDevice[] getScenarioDeviceList(){
 	
 	int length = scDevList.size();

 	if(length > 0){
   ScenarioDevice[] scDevArray = new ScenarioDevice[length];
   
   for(int j=0; j < length; j += 1){
 	  scDevArray[j] = (ScenarioDevice)(scDevList.get(j));
 	 }
 	  
 	 return scDevArray; 
 	}
 	else 
	 return null;
 }
 
 /**
 Returns the number of <code>ScenarioDevice</code>s contained in the
 <code>ScenarioPanel</code>.
 * @return number of <code>ScenarioDevice</code>s contained in the
 <code>ScenarioPanel</code>
 **/
 public int getSize(){
 	
 	return scDevList.size();
 }	
}
