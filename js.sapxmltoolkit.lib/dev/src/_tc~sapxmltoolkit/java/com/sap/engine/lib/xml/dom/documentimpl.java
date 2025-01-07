package com.sap.engine.lib.xml.dom;

import java.net.URL;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
@Deprecated
public class DocumentImpl extends NodeImpl implements Document {

  private DOMImplementationImpl implementation; //$JL-SER$
  private DocumentTypeImpl dtd; //$JL-SER$
  private int modificationCounter = 0;
  private URL location = null;
  private int namespaceAwareness = DOM.NSA_UNKNOWN;
  private Hashtable hIdToElement = new Hashtable();
  private boolean needsTraversing = true;
  
  public CharArray normBuffer = new CharArray(1000);

  public DocumentImpl() {
    setOwnerDocument(null);
  }

  protected DocumentImpl init(Element root, DocumentType dtd) {
    super.init();

    if (root != null) {
      appendChild(root);
    }

    location = null;
    modificationCounter = 0;
    setDTD(dtd);
    return this;
  }

  public int getNamespaceAwareness() {
    return namespaceAwareness;
  }

  public void setNamespaceAwareness(int namespaceAwareness) {
    switch (namespaceAwareness) {
      case DOM.NSA_FALSE:
      case DOM.NSA_TRUE:
      case DOM.NSA_UNKNOWN: {
        break;
      }
      default: {
        throw new IllegalArgumentException();
      }
    }

    this.namespaceAwareness = namespaceAwareness;
  }

  public Attr createAttribute(String name) {
    if (name == null) {
      throw new IllegalArgumentException();
    }

    if (!Symbols.isName(name)) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "'" + name + "' is not a valid name.");
    }

    return (new AttrImpl(this)).init(name, null);
  }

  public Attr createAttributeNS(String namespaceURI, String qualifiedName) {
    // correctness checking is in the init method
    return (new AttrImpl(this)).init(namespaceURI, qualifiedName, null);
  }

  public CDATASection createCDATASection(String data) {
    if (data == null) {
      throw new IllegalArgumentException();
    }

    if (isHTML()) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "An HTML document cannot create CDATA sections.");
    }

    return (CDATASection) (new CDATASectionImpl(this)).init(data, null);
  }

  public Text createTextNode(String data) {
    if (data == null) {
      data = "";
    }

    return (Text) (new TextImpl(this)).init(data, null);
  }

  public Comment createComment(String data) {
    if (data == null) {
      throw new IllegalArgumentException();
    }

    return (Comment) (new CommentImpl(this)).init(data, null);
  }

  public DocumentFragment createDocumentFragment() {
    return (new DocumentFragmentImpl(this));
  }

  public Element createElement(String tagName) {
    // correctness checking is in the init method
    return (new ElementImpl(this)).init(null, tagName, false, null, true);
  }

  public Element createElementNS(String namespaceURI, String qualifiedName) {
    // correctness checking is in the init method
    return (new ElementImpl(this)).init(namespaceURI, qualifiedName, true, null, true);
  }

  public EntityReference createEntityReference(String name) {
    if (name == null) {
      throw new IllegalArgumentException();
    }

    if (isHTML()) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "An HTML document cannot create entity reference nodes.");
    }

    if (!Symbols.isName(name)) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "'" + name + "' is not a valid name.");
    }

    return (new EntityReferenceImpl(this)).init(name, this);
  }

  public ProcessingInstruction createProcessingInstruction(String target, String data) {
    if (!Symbols.isName(target)
    /*|| target.equalsIgnoreCase("xml")*/
    ) {
      throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Processing instruction is not ok.");
    }

    if (isHTML()) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "An HTML document cannot create entity processing instruction nodes.");
    }

    return (new ProcessingInstructionImpl(this)).init(target, data, null);
  }

  public DocumentType getDoctype() {
    return dtd;
  }

  public Element getDocumentElement() {
    for (int i = 0; i < getLength(); i++) {
      if (item(i).getNodeType() == ELEMENT_NODE) {
        return (Element) item(i);
      }
    }

    return null;
  }

  /**
   * When this method is first invoked, the whole DOM tree
   * is traversed and an ID-to-Element cache is built.
   * On following invokations, only the cache is used,
   * without looking at the tree.
   */
  public Element getElementById(String s) {
    if (needsTraversing) {
      if (dtd != null) {
        Element root = getDocumentElement();

        if (root != null) {
          traverse(root);
        }
      }

      needsTraversing = false;
    }

    return (Element) hIdToElement.get(s);
  }

  private void traverse(Element e) {
    NamedNodeMap map = e.getAttributes();
    String elementName = e.getNodeName();

    if (map != null) {
      int nMap = map.getLength();

      for (int i = 0; i < nMap; i++) {
        Node attr = map.item(i);

        if (isIdAttribute(elementName, attr.getNodeName())) {
          hIdToElement.put(attr.getNodeValue(), e);
        }
      }
    }

    NodeList children = e.getChildNodes();
    int nChildren = children.getLength();

    for (int i = 0; i < nChildren; i++) {
      Node child = children.item(i);

      if (child.getNodeType() == Node.ELEMENT_NODE) {
        traverse((Element) child);
      }
    }
  }

  /*
   public Element getElementById(String elementId) {
   Element root = getDocumentElement();
   if (root == null) {
   return null;
   }
   return getElementById(root, elementId);
   }
   private Element getElementById(Element base, String elementId) {
   NamedNodeMap map = base.getAttributes();
   String elementName = base.getNodeName();
   if (map != null) {
   int nMap = map.getLength();
   for (int i = 0; i < nMap; i++) {
   Node attr = map.item(i);
   if (isIdAttribute(elementName, attr.getNodeName())) {
   if (elementId.equals(attr.getNodeValue())) {
   return base;
   }
   }
   }
   }
   NodeList children = base.getChildNodes();
   int nChildren = children.getLength();
   for (int i = 0; i < nChildren; i++) {
   Node child = children.item(i);
   if (child.getNodeType() == Node.ELEMENT_NODE) {
   Element r = getElementById((Element) child, elementId);
   if (r != null) {
   return r;
   }
   }
   }
   return null;
   }
   */
  public DOMImplementation getImplementation() {
    if (implementation == null) {
      implementation = new DOMImplementationImpl();
    }

    return implementation;
  }

  public Node importNode(Node importedNode, boolean deep) {
    int t = importedNode.getNodeType();

    if ((t == DOCUMENT_NODE) || (t == DOCUMENT_TYPE_NODE)) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Importing document nodes or document type nodes is not allowed");
    }

    Base newNode;
    try {
      newNode = (Base) importedNode.cloneNode(deep);
    } catch (ClassCastException cce) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Importing nodes from a different implementation is not allowed");
    }
    newNode.setOwner(this, deep);
    newNode.setParent(null);

    if (t == Node.ELEMENT_NODE) {
      NamedNodeMap n = ((ElementImpl)importedNode).getAttributes();
      for (int i = 0; i < n.getLength(); i++) {
        AttrImpl a = (AttrImpl)n.item(i);
        if (a.isDefaultAttribute()) {
          ((ElementImpl)newNode).removeAttributeNode(a);
        }
      }
      updateDTDForAllElements0((ElementImpl) newNode);
    }
    if (t == Node.ATTRIBUTE_NODE) {
      ((AttrImpl)newNode).specified = true;
      ((AttrImpl)newNode).overridden = (AttrImpl)newNode;
    }

    return newNode;
  }

  public short getNodeType() {
    return DOCUMENT_NODE;
  }

  public String getNodeName() {
    return "#document";
  }

  public Node cloneNode(boolean deep) {
    DocumentImpl r = new DocumentImpl();
    super.transferData(this, r, deep);

	NodeList nl = r.getChildNodes();
	for (int i=0; i<nl.getLength(); i++) {
	  ((Base)nl.item(i)).setOwner(r, true);
	}
	if (r.getDocumentElement() != null) {
      ((ElementImpl) r.getDocumentElement()).setOwner(r, true);
    }
    return r;
  }

  protected void modify() {
    modificationCounter++;
  }

  protected int getModificationCounter() {
    return modificationCounter;
  }

  protected void setDTD(DocumentType dtd) {
    this.dtd = (DocumentTypeImpl) dtd;

    if (dtd != null) {
      this.dtd.setParent(this);
      forcedAppendChild(dtd);
    }
  }

  private boolean isHTML() {
    /*
     Element e = getDocumentElement();
     if (e == null) {
     return false;
     }
     if ("html".equalsIgnoreCase(e.getLocalName()) ||
     "html".equalsIgnoreCase(e.getTagName())) {
     return true;
     }
     */
    return false; //(location != null) && (location.toExternalForm().endsWith(".html"));
  }

  public String toString() {
    return serializeNode(this);
    //return "#document";
  }

  public DocumentImpl setLocation(URL url) {
    location = url;
    return this;
  }

  public URL getLocation() {
    return location;
  }

  public Node appendChild(Node newChild) {
    int t = newChild.getNodeType();

    switch (t) {
      case Node.TEXT_NODE: {
        if (Symbols.isWhitespace(((Text) newChild).getData())) {
          return null;
        }
      }
      case Node.ATTRIBUTE_NODE:
      case Node.CDATA_SECTION_NODE:
      case Node.DOCUMENT_NODE:
      case Node.ENTITY_NODE:
      case Node.ENTITY_REFERENCE_NODE:
      case Node.NOTATION_NODE: {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Cannot append " + Base.nameOfNodeWithArticle(newChild) + " to a document node.");
      }
      case Node.DOCUMENT_TYPE_NODE: {
        if (getDoctype() != null) {
          throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "DocumentType is already present, cannot be appended as a child.");
        }

        this.setDTD((DocumentType) newChild);
        break;
      }
      case Node.ELEMENT_NODE: {
        if (getDocumentElement() != null) {
          throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Root Element is already present, cannot be appended as a child.");
        }

        if (((Element) newChild).getOwnerDocument() != this) {
          throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "Attempt to append a child which has been created by another Document");
        }

        super.appendChild(newChild);
        break;
      }
      case Node.COMMENT_NODE:
      case Node.PROCESSING_INSTRUCTION_NODE: {
        super.appendChild(newChild);
        break;
      }
      case Node.DOCUMENT_FRAGMENT_NODE: { //$JL-SWITCH$
        DocumentFragmentImpl df = (DocumentFragmentImpl) newChild;
        NodeList list = df.getChildNodes();
        int nList = list.getLength();

        for (int i = 0; i < nList; i++) {
          this.appendChild(list.item(i));
        }
      }
      default: {
        break;
      }
    }

    return newChild;
  }

  // Overriden by DocumentImpl
  public Node insertBefore(Node newChild, Node refChild) {
    if ((newChild instanceof Element) && (refChild != null)) {
      throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Attempt to insert " + NodeImpl.nameOfNodeWithArticle(newChild) + " before " + NodeImpl.nameOfNodeWithArticle(refChild) + " in a Document");
    } else {
      return super.insertBefore(newChild, refChild);
    }
  }

  protected void addIdAttribute(String elementName, String attributeName) {
    if (dtd != null) {
      dtd.addIdAttribute(elementName, attributeName);
    }
  }

  protected boolean isIdAttribute(String elementName, String attributeName) {
    return (dtd == null) ? false : dtd.isIdAttribute(elementName, attributeName);
  }

  protected void addDefaultAttribute(String elementName, String attributeName, String defaultValue) {
    if (dtd != null) {
      dtd.addDefaultAttribute(elementName, attributeName, defaultValue);
    }
  }

  protected String getAttributeDefault(String elementName, String attributeName) {
    return (dtd == null) ? null : dtd.getAttributeDefault(elementName, attributeName);
  }

  protected String[][] getNamesOfDefaultAttributes(String elementName) {
    return (dtd == null) ? null : dtd.getDefaultAttributes(elementName);
  }

  protected void updateDTDForAllElements() {
    ElementImpl e = (ElementImpl) getDocumentElement();

    if (e != null) {
      updateDTDForAllElements0(e);
    }
  }

  private void updateDTDForAllElements0(ElementImpl e) {
    ((ElementImpl) e).updateDefaultAttributesFromDTD(dtd);
    NodeList children = e.getChildNodes();

    if (children != null) {
      int nChildren = children.getLength();

      for (int i = 0; i < nChildren; i++) {
        Node child = children.item(i);

        if (child instanceof ElementImpl) {
          updateDTDForAllElements0((ElementImpl) child);
        }
      }
    }
  }
  
  public Node adoptNode(Node n) {
    return null;
  }
  
  public String getEncoding() {
    return "";
  }
  
  public boolean getStandalone() {
    return true;
  }
  
  public boolean getStrictErrorChecking() {
    return false;
  }
  
  public String getVersion() {
    return "";
  }
  public void setVersion(String s){
  }
  
  public void setEncoding(String enc) {
  }
  
  public void setStandalone(boolean b) {
  }
  
  public void setStrictErrorChecking(boolean b) {
  	
  }
  
  public String getInputEncoding(){
  	return null;
  }

  
  public String getXmlEncoding(){
  	return null;
  }


  public boolean getXmlStandalone(){
  	return false;
  }
  
  public void setXmlStandalone(boolean xmlStandalone) throws DOMException{
  	
  }

  public String getXmlVersion(){
  	return null;
  }
 
  public void setXmlVersion(String xmlVersion) throws DOMException{
  	  	
  }
 
 
  public String getDocumentURI(){
  	return null;
  }
  
  public void setDocumentURI(String documentURI){
  	
  }
 
  
  public DOMConfiguration getDomConfig(){
  	return null;
  }


  public void normalizeDocument(){
  	
  }

  
  public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException{
  	return null;
  }
  
  
  
  
  
}

