package com.sap.engine.lib.xml.dom;

import java.util.*;
import org.w3c.dom.*;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.util.DOMSerializer;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;

/**
 * This class implements common functionality for the implementations.
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public class Base implements Node, java.io.Serializable {

  protected Document ownerDocument = null; //$JL-SER$
  private Hashtable augmentationMappings;
  private Node nextSibling; //$JL-SER$
  private Node previousSibling; //$JL-SER$
  
  private static DOMSerializer serializer = null;


  protected Base() {
		//augmentationMappings = new Hashtable();
  }

  public String toXPath() {
    return DOMImplementationImpl.toXPath(this);
  }

  public String toXPathWithLocation() {
    return DOMImplementationImpl.toXPathWithLocation(this);
  }

  public NamedNodeMap getAttributes() {
    return null;
  }

  public Node appendChild(Node newChild) {
    throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to append a child to " + nameOfNodeWithArticle(this) + " node.");
  }

  public NodeList getChildNodes() {
    return NodeListImplEmpty.getInstance();
  }

  public Node getFirstChild() {
    return null;
  }

  public Node getLastChild() {
    return null;
  }

  public String getLocalName() {
    return null;
  }

  public String getNamespaceURI() {
    return null;
  }

  public Node getNextSibling() {
    return nextSibling;
  }
  
  public void setNextSibling(Node nextSibling) {
    this.nextSibling = nextSibling;
  }

  public String getNodeValue() {
    return null;
  }

  public Node getParentNode() {
    return null;
  }

  public String getPrefix() {
    return null;
  }

  public Node getPreviousSibling() {
    return previousSibling;
  }

  public void setPreviousSibling(Node previousSibling) {
    this.previousSibling = previousSibling;
  }

  public boolean hasAttributes() {
    return false;
  }

  public boolean hasChildNodes() {
    return false;
  }

  public Node insertBefore(Node newChild, Node refChild) {
    throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to insert a child to " + nameOfNodeWithArticle(this) + " node.");
  }

  public final boolean isSupported(String feature, String version) {
    return DOMImplementationImpl.hasFeatureStatic(feature, version);
  }

  public final boolean supports(String feature, String version) {
    return isSupported(feature, version);
  }

  public Node removeChild(Node oldChild) {
    throw new DOMException(DOMException.NOT_FOUND_ERR, "Attempt to remove a child from " + nameOfNodeWithArticle(this) + " node.");
  }

  public Node replaceChild(Node newChild, Node oldChild) {
    throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to replace a child from " + nameOfNodeWithArticle(this) + " node.");
  }

  public void setPrefix(String prefix) {
    throw new DOMException(DOMException.NAMESPACE_ERR, "Prefix cannot be set to " + nameOfNodeWithArticle(this) + " node.");
  }

  public void setNodeValue(String value) {
    // Should have no effect
  }

  protected final Document getOwnerDocument_internal() {
    return null;
  }

  protected void setParent(NodeImpl parent) {
    // those who have a parent override it
  }

  public final Document getOwnerDocument() {
    if (getNodeType() == Node.DOCUMENT_NODE) {
      //return (Document) this;
      return null;
    } else {
      return ownerDocument;
    }
  }

  protected final void setOwnerDocument(Document doc) {
    ownerDocument = doc;
  }

  public void normalize() {
    //the default normalize. NodeImpl imlpements it and does what is needed
  }
  
//  public void normalize(StringBuffer buffer){
//    // the default normalize!!! if normalize is called on 
//    // the parent node no additional stringbuffers are allocated. 
//  }

  public static synchronized String serializeNode(Node n) {
    try {
      if (serializer == null) {
        serializer = new DOMSerializer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      }
      
      //Remove the logging to the default error stream.
      /*LogWriter.getSystemLogWriter().println(">>>>>>>>>>>>>>>>>>> DO NOT USE Document.toString()");
      Thread.dumpStack();
      LogWriter.getSystemLogWriter().println("<<<<<<<<<<<<<<<");*/
      if ((n.getNodeType() == Node.DOCUMENT_NODE) || (n.getNodeType() == Node.ELEMENT_NODE)) {
        StringWriter wr = new StringWriter();
        serializer.write(n, wr);
        wr.close();
        return wr.toString();
      } else {
        return "--Only Element and Document Nodes can be serilized in serializeNode--";
      }
    } catch (Exception e) {
      //$JL-EXC$

      return "--Exception during serialization: " + e.toString();
    }
  }

  public void setOwner(Document doc, boolean deep) {
    setOwnerDocument(doc);

    if (deep == true) {
      NodeList nl = getChildNodes();

      for (int i = 0; i < nl.getLength(); i++) {
        ((Base) nl.item(i)).setOwner(doc, deep);
      } 

      if (this.getNodeType() == Node.ELEMENT_NODE) {
        NamedNodeMap map = ((Element) this).getAttributes();

        for (int i = 0; i < map.getLength(); i++) {
          ((AttrImpl) map.item(i)).setOwnerDocument(doc);
        } 
      }
    }
  }
  
  public void setAugmentation(String augmentationProperty, Object augmentationObj) {
    if (augmentationMappings == null){
      augmentationMappings = new Hashtable();
    }
    if(augmentationObj != null) {
  	  augmentationMappings.put(augmentationProperty, augmentationObj);
    }
  }
  
  public Object getAugmentation(String augmentationProperty) {
  	return(augmentationMappings==null?null:augmentationMappings.get(augmentationProperty));
  }
  
  public String getAugmentationPropsAsString() {
  	return(augmentationMappings==null?"":augmentationMappings.toString());
  }
  
  public boolean hasAugmentation(String augmentationProperty) {
  	return(augmentationMappings==null?false:augmentationMappings.containsKey(augmentationProperty));
  }

  public static boolean areNamespaceURIEqual(String n1, String n2) {
    if (n1 == null && n2 == null) {
      return true;
    } else if (n1 == null && n2 != null) {
      return false;
    } else if (n1 != null && n2 == null) {
      return false;
    } else {
      return n1.equals(n2);
    }
  }

  
//  public String getNodeName(){
//  	throw new NullPointerException("Not implemented!");
//  }
//  
//  public Node cloneNode(boolean arg){
//  	throw new NullPointerException("Not implemented!");
//  }
  
//  public String getBaseURI(){
//  	throw new NullPointerException("Not implemented!");
//  }
//  
//  
//  
//  
//  public short compareDocumentPosition(Node other) throws DOMException{
//  	throw new NullPointerException("Not implemented!");
//  }
//
//
//  public String getTextContent() throws DOMException{
//  	throw new NullPointerException("Not implemented!");
//  }
//  
//  
//  public void setTextContent(String textContent) throws DOMException{
//  	throw new NullPointerException("Not implemented!");
//  }
//  
//  public boolean isSameNode(Node other){
//  	throw new NullPointerException("Not implemented!");
//  }
//  
//  
//  public String lookupPrefix(String namespaceURI){
//  	throw new NullPointerException("Not implemented!");
//  }
//
//
//public boolean isDefaultNamespace(String namespaceURI){
//	throw new NullPointerException("Not implemented!");
//}
//
//
//public String lookupNamespaceURI(String prefix){
//	throw new NullPointerException("Not implemented!");
//}
//
//
//public boolean isEqualNode(Node arg){
//	throw new NullPointerException("Not implemented!");
//}
//
//
//public Object getFeature(String feature, String version){
//	throw new NullPointerException("Not implemented!");
//}


//public Object setUserData(String key, Object data, UserDataHandler handler){
//	throw new NullPointerException("Not implemented!");
//}


//public Object getUserData(String key){
//	throw new NullPointerException("Not implemented!");
//}
//  
//  
//public short getNodeType(){
//	throw new NullPointerException("Not implemented!");
//}
  
  
  protected static final String nameOfNode(Node node) {
    if (node == null) {
      return "null";
    }

    short t = node.getNodeType();

    switch (t) {
      case Node.ELEMENT_NODE: {
        return "element";
      }
      case Node.ATTRIBUTE_NODE: {
        return "attribute";
      }
      case Node.TEXT_NODE: {
        return "text";
      }
      case Node.CDATA_SECTION_NODE: {
        return "CDATA section";
      }
      case Node.ENTITY_REFERENCE_NODE: {
        return "entity reference";
      }
      case Node.ENTITY_NODE: {
        return "entity";
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        return "processing instruction";
      }
      case Node.COMMENT_NODE: {
        return "comment";
      }
      case Node.DOCUMENT_NODE: {
        return "document";
      }
      case Node.DOCUMENT_TYPE_NODE: {
        return "document type";
      }
      case Node.DOCUMENT_FRAGMENT_NODE: {
        return "document fragment";
      }
      case Node.NOTATION_NODE: {
        return "notation";
      }
      default: {
        return "???";
      }
    }
  }

  protected static final String nameOfNodeWithArticle(Node node) {
    if (node == null) {
      return "null";
    }

    String s = nameOfNode(node);

    if ("aoeiu".indexOf(s.charAt(0)) != -1) {
      return "an " + s;
    } else {
      return "a " + s;
    }
  }

  protected static boolean isWhiteSpaceChar(char ch) {
    return ((ch == 0x20) || (ch == 0x9) || (ch == 0xA) || (ch == 0xD));
  }

  protected static void checkCompatibleOwnerDocuments(Node a, Node b) {
    Document da = a.getOwnerDocument();
    Document db = b.getOwnerDocument();

    if ((da != null) && (db != null) && (da != db)) {
      throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Attempt to use a node in another document without importing it first");
    }
  }
  
  //java 5.0 org.w3c.dom.Node methods - not implemented yet - this is so JLin doesn't throw exceptions
  public Node cloneNode(boolean deep){
    return null;
  }

  public short compareDocumentPosition(Node other) throws DOMException{
    return -1;
  }
  
  public String getBaseURI(){
    return null;
  }
  
  public Object getFeature(String feature, String version){
    return null;
  }
  
  public String getNodeName(){
    return null;
  }
  
  public short getNodeType(){
    return -1;
  }
  public String getTextContent()throws DOMException { 
    return null;
  }
  
  
  public Object getUserData(String key){
    return null;
  }
  
  public boolean isDefaultNamespace(String namespaceURI){
    return false;
  }
  
  public boolean isEqualNode(Node arg){
    return false;
  }
  
  public boolean isSameNode(Node other){
    return false;
  }
  
  public String lookupNamespaceURI(String prefix){
    return null;
  }
  
  public String lookupPrefix(String namespaceURI){
    return null;
  }
  
  public void setTextContent(String textContent)throws DOMException{
    
  }
  
  public Object setUserData(String key, Object data, UserDataHandler handler){
    return null;
  }
}

