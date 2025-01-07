/*
 *  last change 2004-01-08
 */

package com.sap.util.monitor.grmg.tools.java.client.grmg;

import java.io.*;
import java.util.*;
//import java.lang.Character;
//import javax.xml.parsers.DocumentBuilder; 
//import javax.xml.parsers.DocumentBuilderFactory;  
//import javax.xml.transform.dom.*;
//import javax.xml.transform.*;
//import javax.xml.transform.stream.*; 
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import com.sap.util.monitor.grmg.tools.xml.*;

public class GrmgDocumentAnalyzer {

  static InputStream resourceStream;
  public static Document m_document;
	static HashMap nodesNames;
	static String errorMessage="";
  
  public static void getInputStream(InputStream is){
  	resourceStream = is;  	
  } 

  public static void setDocument(InputStream is){
  
    if(is == null){
     m_document = null;
     return;
    }
    
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);	

		try{
		 DocumentBuilder builder = factory.newDocumentBuilder();
		 m_document = builder.parse(is);
		}
		catch(ParserConfigurationException e){
			errorMessage = e.getMessage();
			//e.printStackTrace();
		}  		
		catch(IOException e){
			errorMessage = e.getMessage();
			//e.printStackTrace();
		}  		
		catch(SAXException e){
			errorMessage = e.getMessage();
			//e.printStackTrace();
		}  		
  }

 public static NodeList getScenarios(){
 
  if(m_document == null){
  	// System.out.println("No document!");
  	return null;
  }
   
  NodeList nlist = DocumentNavigator.getNodeListFromTag(m_document, "scenario");
  return nlist;
 }  
 
 public static String getScenarioUrl(Node scenarioNode){

  String tempName="";
	NodeList templist = scenarioNode.getChildNodes();
	 for(int i = 0; i < templist.getLength(); i += 1)
		if(templist.item(i).getNodeName().equals("scenstarturl")){
		 tempName = templist.item(i).getFirstChild().getNodeValue();
		 return tempName;
		}
	return null;	
 }

 public static String getScenarioUrlFromName(String scenarioName){

  Node scenarioNode = GrmgDocumentAnalyzer.getScenario(scenarioName);
  return GrmgDocumentAnalyzer.getScenarioUrl(scenarioNode);
 }
 
 public static Node getScenario(String scenname){
 	
 	return (Node)getScenarioMap().get(scenname);
 }
 
 public static Document getScenarioAsDocument(String scenname){
 	
	Document scenDoc; 
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setIgnoringElementContentWhitespace(true);	
  
   try{
    DocumentBuilder builder = factory.newDocumentBuilder();
 	  scenDoc = builder.newDocument();
 	
 	   //System.out.println("Node: " + getScenario(scenname)); 
	  //scenDoc.importNode(getScenario(scenname), true);
	  scenDoc.appendChild(scenDoc.importNode(getScenario(scenname), true));
 	  return scenDoc;
   }
   catch(ParserConfigurationException e){
     debug(e.getMessage());
   }
  return null;
 }
 
 public static HashMap getScenarioMap(NodeList scenNodeList){

	String tempName = "";
	HashMap nodeNames = new HashMap();
	NodeList templist;
	
  if(scenNodeList != null){

	 for(int j = 0; j < scenNodeList.getLength(); j += 1){
		templist = scenNodeList.item(j).getChildNodes();
		 for(int i = 0; i < templist.getLength(); i += 1)
			if(templist.item(i).getNodeName().equals("scenname")){
			 tempName = templist.item(i).getFirstChild().getNodeValue();
			 // System.out.println("Name: " + tempName);
			 break;
			}
		nodeNames.put(tempName, scenNodeList.item(j));
   }
  }
  nodesNames = nodeNames;
	return nodeNames;	 
 }
 
 public static HashMap getScenarioMap(){
 	
 	return getScenarioMap(getScenarios());
 }
 
// public Vector getScenarioNames(){
 	
 	
// 	return null;
// }

 public static void main(String[] args){
 
  File file = new File(args[0]);
  try{
  FileInputStream fis = new FileInputStream(file);
	//GrmgDocumentAnalyzer gdoc = new GrmgDocumentAnalyzer();
	GrmgDocumentAnalyzer.setDocument(fis);
	NodeList nlist = GrmgDocumentAnalyzer.getScenarios();
	for(int j = 0; j < nlist.getLength(); j += 1){
		System.out.println(nlist.item(j));
	}
  }
  catch(Exception e){debug(e.getMessage());}   
 }
 
	/**
	 * @return
	 */
	public static String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param string
	 */
	public static void setErrorMessage(String string) {
		errorMessage = string;
	}
  
  private static void debug(String s) {
    //add logging here
    System.out.println(s);
  }

  private static void log(String s) {
    //add logging here
  }

  private static void log(String s, Exception e) {
    //add logging here
  }
}
