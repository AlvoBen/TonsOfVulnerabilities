/*
 *  last change 2004-03-05
 */

/*
 * Author d031360
 * Created on 23.09.2003
 *
 */
package com.sap.util.monitor.grmg.tools.xml;

import java.util.*;
import java.lang.Character;
//import javax.xml.parsers.DocumentBuilder; 
//import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.transform.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*; 
import org.w3c.dom.*;
import java.io.*;

public class DocumentNavigator {
	
	private Document m_document;
	
	/**
	<code>DocumentNavigator</code> provides functionality for searching, pasting and cutting
	elements and nodes on the given XML document <code>document</code>.
	*/
	public DocumentNavigator(Document document){
		
		m_document = document;
	}

	public Document getDocument(){
		
	 return m_document;	
	}		

	private static Vector getChildrenAsList(Element base){
 	
	 Vector nodeVec = new Vector();
	 NodeList nodes = base.getChildNodes();
 	
	 for(int j=0; j < nodes.getLength(); j +=1)
		if(nodes.item(j).getNodeType() == Node.ELEMENT_NODE){
		 nodeVec.add(nodes.item(j));
		}
	 return nodeVec; 	
	}
 
	private Vector getChildrenAsList(Vector elementVector){
 	
	 Vector levelNodeVec = new Vector();
	
	 for(int j = 0; j < elementVector.size(); j +=1)
		if(((Element)elementVector.get(j)).getNodeType() == Node.ELEMENT_NODE){
			levelNodeVec.addAll(getChildrenAsList((Element)elementVector.get(j)));
		}
	 return levelNodeVec; 	
	}
 
	private Vector getElementListByName (Vector elementVector, String name)
	 throws DocumentNavigationException{
 	
	 Vector nameVec = new Vector();
 	
	 for(int j = 0; j < elementVector.size(); j +=1)
		try{
		 if(((Element)elementVector.get(j)).getNodeType() == Node.ELEMENT_NODE){
			if(((Element)elementVector.get(j)).getNodeName().trim().equals(name.trim())){
			 nameVec.add(elementVector.get(j));
			}
		 }
		}
		catch(Exception e){
			throw new DocumentNavigationException(e.getMessage() 
								+ " Maybe mismatch of type of object in list.");
		}  	
	 return nameVec;
	}
	
	/**
	Returns the <code>siblingNumber</code>th element with name <code>elementName</code>
	at level <code>level</code> in the document.
	* From a given InputStream a new GrmgRequest will be created.
	* @param elementName name of the element to be searched for
	* @param level tree level where element will be searched - root level is 0
	* @param siblingNumber position of the sibling at the given level with specified name  
	* @return Element which matches search criteria 
	* @throws DocumentNavigationException if illegal coordinates are used, 
	* or <code>siblingNumber</code> number greater than number of matching elements  
	*/
	public Element getElement(String elementName, int level, int siblingNumber)
	 throws DocumentNavigationException {
 	 
	 int column = siblingNumber - 1;
	 Vector tempVec = new Vector();
 	 
	 if(level < 0 || column < 0)
		throw new DocumentNavigationException("Illegal tree coordinates: Level = " + level + ", Column = " + siblingNumber);  
 	 
	 if(level == 0 && m_document.getDocumentElement().getNodeName().trim().equals(elementName.trim()))
		return m_document.getDocumentElement();
 	
	 tempVec.add(m_document.getDocumentElement());
 	 
	 for(int j = 0; j < level; j += 1){ 
		tempVec = getChildrenAsList(tempVec);	
	 }
 	
	 tempVec = getElementListByName(tempVec, elementName);
 	 
	 if(tempVec.size() < column + 1)
		throw new DocumentNavigationException("Number of elements with name \"" + elementName + "\" at level " + level + ": " + tempVec .size() + "; Selected Column number = " + siblingNumber);  
 	 
	 return (Element)tempVec.get(column); 
 }
 
 /**
 Creates an <code>InputStream</code> of the customizing XML document.
 * @return <code>InputStream</code> containing the GRMG customizing XML document.
 */
 public InputStream getDocumentAsStream(){
 	
  ByteArrayOutputStream output = getDocumentAsOutputStream();
  PipedOutputStream	pout = new PipedOutputStream();
     
   try{
		PipedInputStream pin = new PipedInputStream(pout);
		output.writeTo(pout);
	  return pin;
   }
   catch(Exception e){  
   	e.printStackTrace();
	  return null;
   }  
 }

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
		tce.printStackTrace();
	 // println(output,tce.getMessage());
	}
	catch (TransformerException te){
		te.printStackTrace();
	 // println(output,te.getMessage());
	}		   
	return output;
 }
 
 private static String switchWhitespaceToBlank(String text, boolean switchAllWhiteSpaces){
 	
	boolean lastCharIsBlank = false;
	String adjustedText = text;
	adjustedText.trim();
	
	if(! switchAllWhiteSpaces)
	 return adjustedText;
	else{
	 StringBuffer textBuffer = new StringBuffer(text);
 	
		for(int j = 0; j < textBuffer.length(); j += 1){
		 if(Character.isWhitespace(textBuffer.charAt(j))){
			if(lastCharIsBlank){			 
			 textBuffer.deleteCharAt(j);
			 j = j - 1;
			}
			else
			 textBuffer.setCharAt(j, ' ');
			lastCharIsBlank = true;
		 }
		 else
			lastCharIsBlank = false;
		}  
	 return textBuffer.toString().trim();
	}
 }
 
 public static boolean compareNodes(Node n1, Node n2){
 	
	return compareNodes(n1, n2, false, true, true);
 }
 
 public static boolean compareNodes(Node n1, Node n2, boolean ignoreWhitespaceType, boolean ignoreComments, boolean deep){
 	 	
	// 1. equality as objects 	
	if(n1.equals(n2))
	 return true;
 	
	// 2. node type comparison
	if(n1.getNodeType() != n2.getNodeType()){
	 return false;
	}
 	
	// 3. node types which will not be verified at the moment
	if(n1.getNodeType() == Node.ENTITY_REFERENCE_NODE
		 || n1.getNodeType() == Node.ENTITY_NODE
		 || n1.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE
		 || n1.getNodeType() == Node.COMMENT_NODE
		 || n1.getNodeType() == Node.DOCUMENT_NODE
		 || n1.getNodeType() == Node.DOCUMENT_TYPE_NODE
		 || n1.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE
		 || n1.getNodeType() == Node.NOTATION_NODE){
			return true;  
		 }

	// 4. text node
	if(n1.getNodeType() == Node.TEXT_NODE){		
		return switchWhitespaceToBlank(((Text)n1).getData(), ignoreWhitespaceType).equals(switchWhitespaceToBlank(((Text)n2).getData(), ignoreWhitespaceType));
	}

	// 5. CDATA section node
	if(n1.getNodeType() == Node.CDATA_SECTION_NODE){	
	 return  switchWhitespaceToBlank(((CDATASection)n1).getData(), ignoreWhitespaceType).equals(switchWhitespaceToBlank(((CDATASection)n2).getData(), ignoreWhitespaceType));
	} 

	// 6. node name
	if(! n1.getNodeName().trim().equals(n2.getNodeName().trim())){
    return false; 	
	}
	
	// 7. attribute node
	if(n1.getNodeType() == Node.ATTRIBUTE_NODE){
	 return ((Attr)n1).getValue().trim().equals(((Attr)n2).getValue().trim());
	}

	// from here on: only element types are left 
	// 8. node attributes of elements
	if(!compareAttributes(n1,n2)){
	 return false;
	}
 	
	if(!deep)
	 return true;
 	 
	// from here on: compare child nodes iteratively
	// 5. compare all child nodes of elements
	if(!compareNodeLists(n1.getChildNodes(), n2.getChildNodes(), ignoreWhitespaceType, ignoreComments, deep)){
	 return false;
	}

	return true;
 }
 
 private static Vector attributeListFromNodeMap(NamedNodeMap map){
 
	Vector list = new Vector();
 
	 for(int j = 0; j < map.getLength(); j += 1){
		list.add(map.item(j));
	 }   
	return list; 	
 }
 
 private static Vector nodeListFromNodeList(NodeList nodeList, boolean ignoreComments){
 
	Vector list = new Vector();
	Node previousSibling;
	Node nextSibling;
	 
	 for(int j = 0; j < nodeList.getLength(); j += 1){
	 	
	  if(ignoreComments && nodeList.item(j).getNodeType() == Node.COMMENT_NODE){
		 previousSibling = nodeList.item(j).getPreviousSibling();
		 nextSibling = nodeList.item(j).getNextSibling();
		 
		 if(previousSibling.getNodeType() == Node.TEXT_NODE && nextSibling.getNodeType() == Node.TEXT_NODE){
		 	previousSibling.setNodeValue(previousSibling.getNodeValue() + " " + nextSibling.getNodeValue());
		 }
		 j = j + 2;
	  }
 	  list.add(nodeList.item(j));
	 }   
	return list; 	
 }
 
 public static NodeList getNodeListFromTag(Document doc, String tagname){
 	
 	return doc.getElementsByTagName(tagname);
 } 
 
 public static boolean isContainedIn(String containee, String container){
 	
	boolean check = false;
 	
	 for(int j = 0; (j < container.length() - containee.length()) && !check ; j += 1){
		check = container.regionMatches(j,containee,0,containee.length()); 	  
	 }
	return check;
 }
 
 public static boolean compareAttributes(Node n1, Node n2){
 	
	Attr a1;
	Attr a2;
	int marker = -1;
	boolean check = false;
	NamedNodeMap map1 = n1.getAttributes();
	NamedNodeMap map2 = n2.getAttributes();
	Vector attributeList = attributeListFromNodeMap(map2);
 	
	 for(int j = 0; j < map1.getLength(); j += 1){ 	 
		a1 = (Attr)map1.item(j);
		check = false; 	 
		 for(int i = 0; i < attributeList.size(); i += 1){
			a2 = (Attr)attributeList.get(i);
			 if(a1.getName().trim().equals(a2.getName().trim()) && a1.getValue().trim().equals(a2.getValue().trim())){ 	 	 
				check = true;
				marker = i;
				break;
			 }
			 else
				check = check | false; 	 	   
		 }  	   
		 if(!check)
			return check; 	   
		attributeList.removeElementAt(marker);
		marker = -1;
	 } 	 
	if(attributeList.size() == 0)   	 
	 return true;
	else
	 return false;  
 } 	 	 

 public static boolean compareNodeLists(NodeList list1, NodeList list2){
	
	return compareNodeLists(list1, list2, false, true, true);
 }

 public static boolean compareNodeLists(NodeList list1, NodeList list2, boolean ignoreWhitespaceType, boolean ignoreComments, boolean deep){

	Node node1;
	Node node2;
	int marker = -1;
	boolean check = false;
	//boolean textOrCdata = false;
	Vector nodeList1 = nodeListFromNodeList(list1, ignoreComments);
	Vector nodeList2 = nodeListFromNodeList(list2, ignoreComments);
		
	 for(int j = 0; j < nodeList1.size(); j += 1){ 	 
		if(nodeList2.size() == 0)
		 return false;				
		// textOrCdata = false; 
		node1 = (Node)nodeList1.get(j);
		// if(node1.getNodeType() == Node.CDATA_SECTION_NODE || node1.getNodeType() == Node.TEXT_NODE)
		//  textOrCdata = true; 
		check = false; 	 
		 for(int i = 0; i < nodeList2.size(); i += 1){
			node2 = (Node)nodeList2.get(i);
			if(compareNodes(node1, node2, ignoreWhitespaceType, ignoreComments, deep)){	 	 
			/* failed: in order to avoid commutativity of text and cdata Data in a row
			if((!textOrCdata && compareNodes(node1, node2, ignoreWhitespaceType, ignoreComments, deep)) ||
			   (textOrCdata && compareNodes(node1, node2, ignoreWhitespaceType, ignoreComments, deep) && j > 0 && j < nodeList1.size() - 1 &&
    			compareNodes(((Node)nodeList1.get(j-1)), ((Node)nodeList2.get(i-1)), ignoreWhitespaceType, ignoreComments, deep) &&
		    	compareNodes(((Node)nodeList1.get(j+1)), ((Node)nodeList2.get(j+1)), ignoreWhitespaceType, ignoreComments, deep))){
		   */ 	
				check = true;
				marker = i;
				break;
			 }
			 else
				check = check | false; 	 	   
		 }  	   
		 if(!check){
			return check;
		 } 	   	 
		nodeList2.removeElementAt(marker);
		marker = -1;
	 } 	 
	 if(nodeList2.size() == 0){   	 
		return true;
	 }
	 else{
		return false;
	 }  
 }
 
 public static boolean firstIsChildOfSecond(Node child, Node parent){
	return firstIsChildOfSecond(child, parent, false, true);
 }

 public static boolean firstIsChildOfSecond(Node child, Node parent, boolean ignoreComments){
 	
	return firstIsChildOfSecond(child, parent, false, ignoreComments);
 }

 public static boolean firstIsChildOfSecond(Node child, Node parent, boolean ignoreWhitespace, boolean ignoreComments){
 	
	boolean check = false;
	NodeList allChildren = parent.getChildNodes(); 	
 	
	 for(int j = 0; !check && j < allChildren.getLength(); j += 1)
		check = check | DocumentNavigator.compareNodes(child, allChildren.item(j), ignoreWhitespace, ignoreComments, true); 	 	
	
	return check; 	
 } 
}
