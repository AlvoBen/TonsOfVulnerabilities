/*
 *  last change 2004-03-19
 */

/**
 * @author Bernhard Drabant
 * 
 */
package com.sap.util.monitor.grmg;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.transform.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*; 
import org.w3c.dom.*;
import java.io.*;
import java.util.*;

 /**
 <code>GrmgXMLFile</code>  represents a customizing GRMG XML file (or chunk of it). It allows 
 dedicated manipulations on the document content.
 */
public class GrmgXMLFile{

 private Document m_document;
 private String lineEnd=System.getProperty("line.separator");

 /**
 Creates a default <code>GrmgXMLFile</code> with empty component list.
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws java.io.IOException
 * @throws org.xml.sax.SAXException
 */
 public GrmgXMLFile() throws javax.xml.parsers.ParserConfigurationException,
																						org.xml.sax.SAXException{

  GrmgCustomizing cust = new GrmgCustomizing();
  cust.setGrmgRunsFlag(true);

  GrmgScenario scen = new GrmgScenario();
  scen.setName("Default");
  scen.setVersion("");
  scen.setInstance("001");
  scen.setStartUrl("http://localhost");
  GrmgText text = new GrmgText();
  text.setLanguage("E");
  text.setDescription("default/loaded scenario");
  scen.setText(text);
  cust.addScenario(scen);

  m_document = GrmgCustomizingXmlGenerator.buildDocument(cust);
 }


	/**
	Creates a <code>GrmgXMLFile</code> from a base customizing XML file - possibly
	with empty component list.
	* @throws javax.xml.parsers.ParserConfigurationException
	* @throws java.io.IOException
	* @throws org.xml.sax.SAXException
	*/
 public GrmgXMLFile(File baseFile) throws javax.xml.parsers.ParserConfigurationException,
                                            java.io.IOException, org.xml.sax.SAXException{

  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setIgnoringElementContentWhitespace(true);	
  DocumentBuilder builder = factory.newDocumentBuilder();
  m_document = builder.parse(baseFile);  	
 }
 
	/**
	Creates a <code>GrmgXMLFile</code> from a base customizing XML document - possibly
	with empty component list.
	*/
 public GrmgXMLFile(Document document){

  this.m_document = document;  	
 }
 
	/**
	Creates a <code>GrmgXMLFile</code> from a base customizing XML input stream - possibly
	with empty component list.
	*/
 public GrmgXMLFile(InputStream instream) throws javax.xml.parsers.ParserConfigurationException,
                                            java.io.IOException, org.xml.sax.SAXException{

  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  factory.setIgnoringElementContentWhitespace(true);	
  DocumentBuilder builder = factory.newDocumentBuilder();
  m_document = builder.parse(instream);  	
 }
 
 /**
 Adds scenario components to the <code>GrmgXMLFile</code>.
 * From the given document the components will be extracted and built into
 * this XML document of <code>GrmgXMLFile</code>.
 * The components will be added to the scenario which has a tag <code>scenname</code>
 * whose text element has value <code>scenName</code>.
 * <p>
 * The XML document in the input stream needs to obey the following DTD:
 * <p>
 * <code>
 * &lt;!ELEMENT components (component*)&gt;<br>
 * &lt;!ELEMENT component (compname,compversion,comptype,comptexts,properties)&gt;<br>
 * &lt;!ELEMENT compname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compversion (#PCDATA)&gt;<br> 
 * &lt;!ELEMENT comptype (#PCDATA)&gt;<br>
 * &lt;!ELEMENT comptexts (comptext*)&gt;<br> 
 * &lt;!ELEMENT comptext (complangu, compdesc)&gt;<br>
 * &lt;!ELEMENT complangu (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compdesc (#PCDATA)&gt;<br>
 * &lt;!ELEMENT properties (property*)&gt;<br>
 * &lt;!ELEMENT property (propname, propvalue)&gt;<br>
 * &lt;!ELEMENT propname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT propvalue (#PCDATA)&gt;
 * </code>
 * <p>
 * @param  extdocument <code>Document</code>
 * @param scenName the value of the text element of the tag <code>scenname</code>
 * in the document of the GrmgXMLFile
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws java.io.IOException
 * @throws org.xml.sax.SAXException
 */
 public void addScenarios(Document extdocument) throws javax.xml.parsers.ParserConfigurationException,
																											 java.io.IOException, org.xml.sax.SAXException{
 	
 Element childElementExt;   
 Element childImpDoc;
 Element baseElement = null;   
 NodeList allScens = m_document.getElementsByTagName("scenario");
 baseElement = (Element) m_document.getElementsByTagName("scenarios").item(0);

 if(baseElement != null){
  for(int j = 0; j < allScens.getLength(); j += 1){   
	 childElementExt = (Element) allScens.item(j);   
	 childImpDoc = (Element) m_document.importNode(childElementExt, true);
	 baseElement.appendChild(childImpDoc);
	}
 }
}

	/**
	Adds scenario components to the <code>GrmgXMLFile</code>.
	* From the XML input stream the components will be extracted and built into
	* the XML document of <code>GrmgXMLFile</code>.
	* The components will be added to the first scenario encountered in the preorder traversal
	* of the XML document of the <code>GrmgXMLFile</code>. 
	* The method is equivalent to <code>addComponent(InputStream instream, null)</code>.
	* @param  instream	<code>InputStream</code> for which a <code>GrmgRequest</code> 
	* will be created.
	* @throws javax.xml.parsers.ParserConfigurationException
	* @throws java.io.IOException
	* @throws org.xml.sax.SAXException
  */
 public void addComponents(InputStream instream) throws javax.xml.parsers.ParserConfigurationException,
                                                         java.io.IOException, org.xml.sax.SAXException{ 	
  addComponents(instream, null);
  
 }

 /**
 Like addComponents(Document extdocument, String scenName) but with no specification of 
 the scenario name.
 * From the given document the components will be extracted and built into
 * this XML document of <code>GrmgXMLFile</code>.
 * The components will be added to the scenario which has a tag <code>scenname</code>
 * whose text element has value <code>scenName</code>.
 * <p>
 * The XML document in the input stream needs to obey the following DTD:
 * <p>
 * <code>
 * &lt;!ELEMENT components (component*)&gt;<br>
 * &lt;!ELEMENT component (compname,compversion,comptype,comptexts,properties)&gt;<br>
 * &lt;!ELEMENT compname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compversion (#PCDATA)&gt;<br> 
 * &lt;!ELEMENT comptype (#PCDATA)&gt;<br>
 * &lt;!ELEMENT comptexts (comptext*)&gt;<br> 
 * &lt;!ELEMENT comptext (complangu, compdesc)&gt;<br>
 * &lt;!ELEMENT complangu (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compdesc (#PCDATA)&gt;<br>
 * &lt;!ELEMENT properties (property*)&gt;<br>
 * &lt;!ELEMENT property (propname, propvalue)&gt;<br>
 * &lt;!ELEMENT propname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT propvalue (#PCDATA)&gt;
 * </code>
 * <p>
 * @param  extdocument <code>Document</code>
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws java.io.IOException
 * @throws org.xml.sax.SAXException
 */
 public void addComponents(Document extdocument) throws javax.xml.parsers.ParserConfigurationException,
																												java.io.IOException, org.xml.sax.SAXException{
  addComponents(extdocument, null);																													
 }
 
 /**
 Adds scenario components to the <code>GrmgXMLFile</code>.
 * From the given document the components will be extracted and built into
 * this XML document of <code>GrmgXMLFile</code>.
 * The components will be added to the scenario which has a tag <code>scenname</code>
 * whose text element has value <code>scenName</code>.
 * <p>
 * The XML document in the input stream needs to obey the following DTD:
 * <p>
 * <code>
 * &lt;!ELEMENT components (component*)&gt;<br>
 * &lt;!ELEMENT component (compname,compversion,comptype,comptexts,properties)&gt;<br>
 * &lt;!ELEMENT compname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compversion (#PCDATA)&gt;<br> 
 * &lt;!ELEMENT comptype (#PCDATA)&gt;<br>
 * &lt;!ELEMENT comptexts (comptext*)&gt;<br> 
 * &lt;!ELEMENT comptext (complangu, compdesc)&gt;<br>
 * &lt;!ELEMENT complangu (#PCDATA)&gt;<br>
 * &lt;!ELEMENT compdesc (#PCDATA)&gt;<br>
 * &lt;!ELEMENT properties (property*)&gt;<br>
 * &lt;!ELEMENT property (propname, propvalue)&gt;<br>
 * &lt;!ELEMENT propname (#PCDATA)&gt;<br>
 * &lt;!ELEMENT propvalue (#PCDATA)&gt;
 * </code>
 * <p>
 * @param  extdocument <code>Document</code>
 * @param scenName the value of the text element of the tag <code>scenname</code>
 * in the document of the GrmgXMLFile
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws java.io.IOException
 * @throws org.xml.sax.SAXException
 */
 public void addComponents(Document extdocument, String scenName) throws javax.xml.parsers.ParserConfigurationException,
																												java.io.IOException, org.xml.sax.SAXException{
 	
 Element childElementExt;   
 Element childImpDoc;
 Element scenNode = null;
 Element baseElementDoc = null;
   
 if(scenName != null){
	NodeList scenList = m_document.getElementsByTagName("scenario");  
	 for(int k = 0; k < scenList.getLength(); k +=1){
		if(scenList.item(k).getChildNodes().item(1).getFirstChild().getNodeValue().equals(scenName)){
		 scenNode =  (Element) scenList.item(k);
		 break;
		}
	 }
	 if(scenNode != null)
		baseElementDoc = (Element)scenNode.getElementsByTagName("components").item(0);  
 }
 else
	baseElementDoc = (Element) m_document.getElementsByTagName("components").item(0);

 NodeList nextdoclst = extdocument.getElementsByTagName("component");

 for(int j = 0; j < nextdoclst.getLength(); j += 1){   
	if(baseElementDoc != null){
	 childElementExt = (Element) nextdoclst.item(j);   
	 childImpDoc = (Element) m_document.importNode(childElementExt, true);
	 baseElementDoc.appendChild(childImpDoc);
	}
 }
}

	/**
	Adds scenario components to the <code>GrmgXMLFile</code>.
	* From the XML input stream the components will be extracted and built into
	* the XML document of <code>GrmgXMLFile</code>.
	* The components will be added to the scenario which has a tag <code>scenname</code>
	* whose text element has value <code>scenName</code>.
	* <p>
	* The XML document in the input stream needs to obey the following DTD:
	* <p>
	* <code>
	* &lt;!ELEMENT scenariodevice (component*)&gt;<br>
  * &lt;!ELEMENT component (compname,compversion,comptype,comptexts,properties)&gt;<br>
  * &lt;!ELEMENT compname (#PCDATA)&gt;<br>
  * &lt;!ELEMENT compversion (#PCDATA)&gt;<br> 
  * &lt;!ELEMENT comptype (#PCDATA)&gt;<br>
  * &lt;!ELEMENT comptexts (comptext*)&gt;<br> 
  * &lt;!ELEMENT comptext (complangu, compdesc)&gt;<br>
  * &lt;!ELEMENT complangu (#PCDATA)&gt;<br>
  * &lt;!ELEMENT compdesc (#PCDATA)&gt;<br>
  * &lt;!ELEMENT properties (property*)&gt;<br>
  * &lt;!ELEMENT property (propname, propvalue)&gt;<br>
  * &lt;!ELEMENT propname (#PCDATA)&gt;<br>
  * &lt;!ELEMENT propvalue (#PCDATA)&gt;
  * </code>
  * <p>
	* @param  instream	<code>InputStream</code>
	* @param scenName the value of the text element of the element <code>scenname</code>
	* @throws javax.xml.parsers.ParserConfigurationException
	* @throws java.io.IOException
	* @throws org.xml.sax.SAXException
  */
 public void addComponents(InputStream instream, String scenName) throws javax.xml.parsers.ParserConfigurationException,
                                                         java.io.IOException, org.xml.sax.SAXException{
 	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setIgnoringElementContentWhitespace(true);
	DocumentBuilder builder = factory.newDocumentBuilder();
	Document extdocument = builder.parse(instream);
	
	addComponents(extdocument, scenName); 	
 }

 /**
 Adds a component to the document underlying the GrmgXMLFile.
 * @param <code>Element</code> component to be added.
 * @param String scenName name of Scenario to which the component shall be added
 */
 public void addComponent(Element component, String scenName) throws javax.xml.parsers.ParserConfigurationException,
																												 java.io.IOException, org.xml.sax.SAXException{
	
	Element scenNode = null;
	Element baseElement = null;	
		
	if(scenName != null){
   NodeList scenList = m_document.getElementsByTagName("scenario");
  
		for(int k = 0; k < scenList.getLength(); k +=1){
	   if(scenList.item(k).getChildNodes().item(1).getFirstChild().getNodeValue().equals(scenName)){
	 	  scenNode =  (Element) scenList.item(k);
		  break;
		 }
		}
		if(scenNode != null)
		  baseElement = (Element)scenNode.getElementsByTagName("components").item(0);  
		}
		else
		 baseElement = (Element) m_document.getElementsByTagName("components").item(0);
												
   if(component.getNodeName().equalsIgnoreCase("component") && baseElement != null){
    Element childImpDoc = (Element) m_document.importNode(component, true);
    baseElement.appendChild(childImpDoc);
   }  																											
 }
  
 /**
 Like 2-parameter method addComponent, but adds component to first scenarion tag
 encountered in the preorder traversal of the XML document of the <code>GrmgXMLFile</code>.
 * @param <code>Element</code> component to be added.
 */
 public void addComponent(Element component) throws javax.xml.parsers.ParserConfigurationException,
																												 java.io.IOException, org.xml.sax.SAXException{
  addComponent(component, null);
 }

 /**
 Adds a unique component to the document underlying the GrmgXMLFile. 
 * The component will be added only if the scenario of the GrmgXMLFile does not
 * contain a component with the same <code>compname</code> already.  
 * @param <code>Element</code> component to be added.
 * @param scenName name of scenario to which the component shall be added
 */
 // semantically erroneous - to be corrected!
 public void addUniqueComponent(Element component, String scenName) throws javax.xml.parsers.ParserConfigurationException,
																												 java.io.IOException, org.xml.sax.SAXException{
	
	NodeList allBaseComponents = getComponentNodes(scenName);
	NodeList compNameNodes = component.getElementsByTagName("compname");

 	 if(compNameNodes.getLength() != 1)
	 return;
 	
 	String nameOfComponent = compNameNodes.item(0).getFirstChild().getNodeValue(); 
	
	 if(allBaseComponents.getLength() == 0){
		addComponent(component, scenName);  		
		return;					
	 }	 
	
	 for(int j = 0; j < allBaseComponents.getLength(); j += 1){		
		NodeList allCompNameNodes = ((Element)allBaseComponents.item(j)).getElementsByTagName("compname");

		 if((allCompNameNodes.getLength() != 1) ||
		    (allCompNameNodes.item(0).getFirstChild().getNodeValue().equals(nameOfComponent)))
		  return;
						
	  addComponent(component, scenName);  		
	  return;			
	 } 	
 }

 /**
 Like 2-parameter method addUniqueComponent, but adds component to first scenario tag
 encountered in the preorder traversal of the XML document of the <code>GrmgXMLFile</code>.
 * @param <code>Element</code> component to be added.
 */
 public void addUniqueComponent(Element component) throws javax.xml.parsers.ParserConfigurationException,
																												 java.io.IOException, org.xml.sax.SAXException{
  addUniqueComponent(component, null);
 }

 public String[] getScenarioNames(){
 	
	NodeList allScens = m_document.getElementsByTagName("scenname");
	String[] allScenNames = new String[allScens.getLength()];	
 	
	for(int i = 0; i < allScens.getLength(); i += 1){
	 allScenNames[i] = ((Element)allScens.item(i)).getChildNodes().item(0).getNodeValue();
	} 		
	return allScenNames;
 }

 /**
 Returns the component nodes of the first scenario tag encountered in the preorder 
 traversal of the XML document underlying the GrmgXMLFile.
 * @return <code>NodeList</code> of all component nodes of the GrmgXMLFile.
 */
 public NodeList getComponentNodes(){
 	
 	return getComponentNodes(null);
 }

	/**
	Returns the component nodes of the given scenario underlying the GrmgXMLFile.
	* @return <code>NodeList</code> of all component nodes of the GrmgXMLFile in the 
	* given scenario
	*/
 public NodeList getComponentNodes(String scenName){

  Element scenNode = null;
  
	if(scenName != null){
	 NodeList scenList = m_document.getElementsByTagName("scenario");
  
		for(int k = 0; k < scenList.getLength(); k +=1){
		 if(scenList.item(k).getChildNodes().item(1).getFirstChild().getNodeValue().equals(scenName)){
			scenNode =  (Element) scenList.item(k);
			break;
		 }
		}
		if(scenNode != null)
			return scenNode.getElementsByTagName("component");
		else return null;	  
	}
	else
	 return m_document.getElementsByTagName("component");
 }
 
 public List getComponentNames(String scenName){
 	
 	NodeList allComponents = getComponentNodes(scenName);
 	Vector allCompNames = new Vector();
 	
 	for(int i = 0; i < allComponents.getLength(); i += 1){
 	 allCompNames.add(((Element)allComponents.item(i)).getElementsByTagName("compname").item(0).getNodeValue());
 	} 		
 	return allCompNames;
 }
 
 public List getComponentNames(){
  return getComponentNames(null); 	
 }

	/**
	Returns the document underlying the GrmgXMLFile.
	* @return <code>Document</code> underlying the GrmgXMLFile object.
  */
	public Document getDocument(){
		
		return m_document;
	}
	
	/**
	Creates an <code>InputStream</code> of the customizing XML document.
	* @return <code>InputStream</code> containing the GRMG customizing XML document.
  */
	// yet erroneous - due to pipe connect problems in case of threadding
 public InputStream getDocumentAsStream(){
 	
  ByteArrayOutputStream output = getDocumentAsOutputStream();
  PipedOutputStream	pout = new PipedOutputStream();
     
  try{
   output.writeTo(pout);
	 PipedInputStream pin = new PipedInputStream(pout);
   return pin;
  }
  catch(Exception e){  
   e.printStackTrace();
   return null;
  }  
 }

	/**
	Creates a <code>ByteArrayOutputStream</code> of the customizing XML document.
	* @return <code>ByteArrayOutputStream</code> containing the GRMG customizing XML document.
  */
 public ByteArrayOutputStream getDocumentAsOutputStream(){
  
  ByteArrayOutputStream output = new ByteArrayOutputStream();      
 		 
  try{
 	 TransformerFactory tFactory = TransformerFactory.newInstance();
	 Transformer transformer = tFactory.newTransformer();
	 DOMSource source = new DOMSource(m_document);
	 StreamResult result = new StreamResult(output);
	 transformer.transform(source, result);	    	
  }
	catch (TransformerConfigurationException tce){
	 println(output,tce.getMessage());
	}
	catch (TransformerException te){
	 println(output,te.getMessage());
	}		   
  return output;
 }

	/**
	Creates a <code>File</code> of the GRMG customizing XML document.
	* @param  custFile	the <code>File</code> to which the content of the XML document will be written to.
  */
 public void createCustomizingFile(File custFile){
 	
  ByteArrayOutputStream output = getDocumentAsOutputStream();
  
	try{
	 output.writeTo(new FileOutputStream(custFile));
 	 output.close();			
	}
	catch(Exception e){debug("createCustomizingFile : " + e.getMessage());}	
 }

	/**
	Writes a <code>String</code> to a <code>ByteArrayOutputStream</code> and adds
	a new line.
	* @param  output	the <code>ByteArrayOutputStream.
	* @param  line		the <code>String</code> line to be written to the output stream.
  */
 private void println(ByteArrayOutputStream output, String line){

  output.write(line.getBytes(),0,line.length());
 	output.write(lineEnd.getBytes(),0,lineEnd.length());  
 }
 
 public void setScenarioName(String scenOldName, String scenNewName){
 	
	boolean test = false;
	NodeList allScens = m_document.getElementsByTagName("scenname");
 	
	for(int j = 0; j < allScens.getLength(); j += 1){

	 if(scenOldName == null)
		test = true;
	 else 
		test = allScens.item(j).getFirstChild().getNodeValue().equals(scenOldName);    
    
	 if(test){
		allScens.item(j).getFirstChild().setNodeValue(scenNewName);
		m_document.importNode(allScens.item(j), true);
	 }
	} 	
 }

 public void setScenarioName(String scenName){ 	
	setScenarioName(null,scenName);
 }

 // master method to set a text value for a given tag in a given scenario (name) - in document 
 private void setScenarioTagValue(String scenName, String tagname, String tagvalue){
 	 	
	boolean test = false;
	NodeList allScenTagNameNodes = m_document.getElementsByTagName(tagname);
 	
	for(int j = 0; j < allScenTagNameNodes.getLength(); j += 1){

	 if(scenName == null)
		test = true;
	 else 
		test = getNodeByName(allScenTagNameNodes.item(j).getParentNode().getChildNodes(),"scenname").getFirstChild().getNodeValue().equals(scenName);    
    
	 if(test){
		allScenTagNameNodes.item(j).getFirstChild().setNodeValue(tagvalue);
		 m_document.importNode(allScenTagNameNodes.item(j), true);		 
		 //baseElementDoc.appendChild(childImpDoc);
		}
	} 	 	
 }
 
 public void setScenarioVersion(String scenName, String scenversion){
  setScenarioTagValue(scenName, "scenversion", scenversion);
  
  /*
  boolean test = false;
 	NodeList allScenVersions = m_document.getElementsByTagName("scenversion");
 	
 	for(int j = 0; j < allScenVersions.getLength(); j += 1){

   if(scenName == null)
    test = true;
   else 
    test = getNodeByName(allScenVersions.item(j).getParentNode().getChildNodes(),"scenname").getFirstChild().getNodeValue().equals(scenName);    
    
 	 if(test){
  	 allScenVersions.item(j).getFirstChild().setNodeValue(scenversion);
 		 m_document.importNode(allScenVersions.item(j), true);
 		}
 	} 
  */		 	
 }

 public void setScenarioVersion(String scenversion){ 	
 	setScenarioVersion(null, scenversion);
 }

 public void setScenarioInstance(String scenName, String sceninst){
	setScenarioTagValue(scenName, "sceninst", sceninst);

  /*
  boolean test = false; 	
 	NodeList allScenInsts = m_document.getElementsByTagName("sceninst");
 	
 	for(int j = 0; j < allScenInsts.getLength(); j += 1){

   if(scenName == null)
    test = true;
   else 
    test = getNodeByName(allScenInsts.item(j).getParentNode().getChildNodes(),"scenname").getFirstChild().getNodeValue().equals(scenName);    
    
 	 if(test){
 		 allScenInsts.item(j).getFirstChild().setNodeValue(sceninst);
 		 m_document.importNode(allScenInsts.item(j), true);
 		}
 	} 
 	*/	 	 	
 }

 public void setScenarioInstance(String sceninst){
 	setScenarioInstance(null, sceninst);
 }

 public void setScenarioStartUrl(String scenName, String scenstarturl){ 	
	setScenarioTagValue(scenName, "scenstarturl", scenstarturl);

  /*
  boolean test = false;
 	NodeList allScenUrls = m_document.getElementsByTagName("scenstarturl");
 	
 	for(int j = 0; j < allScenUrls.getLength(); j += 1){
    
   if(scenName == null)
    test = true;
   else 
    test = getNodeByName(allScenUrls.item(j).getParentNode().getChildNodes(),"scenname").getFirstChild().getNodeValue().equals(scenName);    
    
 	 if(test){
 		 allScenUrls.item(j).getFirstChild().setNodeValue(scenstarturl);
 		 m_document.importNode(allScenUrls.item(j), true);
 		}
 	} 
  */	 	 	 	
 }

 public void setScenarioStartUrl(String scenstarturl){ 	
 	setScenarioStartUrl(null, scenstarturl);
 }

 public void setScenarioStartMode(String scenName, String scenstartmod){ 	
	setScenarioTagValue(scenName, "scenstartmod", scenstartmod);
  
  /*
  boolean test = false;
 	NodeList allScenMods = m_document.getElementsByTagName("scenstartmod");
 	
 	for(int j = 0; j < allScenMods.getLength(); j += 1){

   if(scenName == null)
    test = true;
   else 
    test = getNodeByName(allScenMods.item(j).getParentNode().getChildNodes(),"scenname").getFirstChild().getNodeValue().equals(scenName);    
    
 	 if(test){
 		 allScenMods.item(j).getFirstChild().setNodeValue(scenstartmod);
 		 m_document.importNode(allScenMods.item(j), true);
 		}
 	} 
 	*/	 	 	 	
 }
 
 public void setScenarioStartMode(String scenstartmod){ 	
 	setScenarioStartMode(null, scenstartmod);
 }
 
 private Node getNodeByName(NodeList nlist, String nodename){
 	
 	for(int j = 0; j < nlist.getLength(); j += 1)
 	 if(nlist.item(j).getNodeName().equals(nodename))
 	  return nlist.item(j);
 	
 	return null;  	
 }
 
 private void debug(String s) {
   //add logging here
   System.out.println(s);
 }

 private void log(String s) {
   //add logging here
 }

 private void log(String s, Exception e) {
   //add logging here
 }
}
