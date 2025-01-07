package com.sap.engine.lib.xml.signature.elements;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl;
import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.encryption.CipherData;
import com.sap.engine.lib.xml.signature.encryption.CipherReference;
import com.sap.engine.lib.xml.signature.encryption.EncReference;
import com.sap.engine.lib.xml.signature.encryption.EncryptedKey;
import com.sap.engine.lib.xml.signature.encryption.EncryptionMethod;
import com.sap.engine.lib.xml.signature.encryption.EncryptionProperties;
import com.sap.engine.lib.xml.signature.encryption.EncryptionProperty;
import com.sap.engine.lib.xml.signature.encryption.ReferenceList;
import com.sap.engine.lib.xml.util.NS;

public class GenericElement {

  protected Document owner = null;
  protected Element domRepresentation = null;
  protected GenericElement parent = null;
  protected String namespaceUri = null;
  protected String qualifiedName = null;
  protected Hashtable attribs = null;//new Hashtable();
  protected List childElements = null;//new Vector(0);
  protected boolean descendantsInitialized = false;
  public GenericElement(String namespaceUri, String qualifiedName) {
    this.namespaceUri = namespaceUri;
    this.qualifiedName = qualifiedName;
  }

  public GenericElement(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    this(parent.getOwner(), namespaceUri, qualifiedName, parent);
  }

  public GenericElement(Node n, String namespaceUri, String qualifiedName, GenericElement parent, boolean generateDom) throws SignatureException {
    if (generateDom) {
      if (!(n instanceof Document)) {
        throw new SignatureException("The owner is supposed to be a document.", new java.lang.Object[]{n,namespaceUri, qualifiedName,parent});
      }

      init((Document) n, namespaceUri, qualifiedName, parent);
    } else {
      if (!(n instanceof Element)) {
        throw new SignatureException("The owner is supposed to be a document.", new java.lang.Object[]{n,namespaceUri, qualifiedName,parent});
      }

      init((Element) n, parent);
    }
  }

  public GenericElement(Document owner, String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    init(owner, namespaceUri, qualifiedName, parent);
  }

  public GenericElement(Element domRepresentation, GenericElement parent) throws SignatureException {
    //                    LogWriter.getSystemLogWriter().println("GenericElement:
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
    //                    LogWriter.getSystemLogWriter().println(domRepresentation.getParentNode());
    init(domRepresentation, parent);
  }

  private void init(Document $owner, String $namespaceUri, String $qualifiedName, GenericElement $parent) throws SignatureException {
    if ($owner == null && $parent != null) {
      if ($parent instanceof Document) {
        $owner = (Document) $parent;
      } else {
        $owner = $parent.getOwner();
      }
    }

    this.namespaceUri = $namespaceUri;
    this.qualifiedName = $qualifiedName;
    this.owner = $owner;
    //    LogWriter.getSystemLogWriter().println("GenericElement: " + namespaceUri + "; " +
		// qualifiedName);
    this.domRepresentation = $owner.createElementNS($namespaceUri, $qualifiedName);

    if ($parent != null) {
      $parent.appendChild(this);
    }
  }

  static char[] buf = new char[100]; //TODO:sync for multithreading
  static Object monitor = new Object();

  private void init(Element $domRepresentation, GenericElement $parent) throws SignatureException {
    this.owner = $domRepresentation.getOwnerDocument();
    this.parent = $parent;
    this.domRepresentation = $domRepresentation;
    this.namespaceUri = $domRepresentation.getNamespaceURI();

    if ($domRepresentation.getPrefix() == null) {
      this.qualifiedName = $domRepresentation.getLocalName();
    } else {
      String str = $domRepresentation.getPrefix();
      String other = $domRepresentation.getLocalName();
      int count = str.length();
      int otherLen = other.length();
      synchronized (monitor){
        //buf may change - so it should be synchronized by other monitor
        if (buf.length < (count + otherLen + 1)) {
          buf = new char[count + otherLen + 1];
        }
        str.getChars(0, count, buf, 0);
        buf[count] = ':';
        other.getChars(0, otherLen, buf, count + 1);
        this.qualifiedName = new String(buf, 0, count + otherLen + 1);
      }
      //this.qualifiedName = $domRepresentation.getPrefix() + ":" +
			// $domRepresentation.getLocalName();
    }

    NamedNodeMap map = $domRepresentation.getAttributes();
    if ((attribs == null)&&(map.getLength()>0)){
      attribs = new Hashtable();
    }
    for (int i = 0; i < map.getLength(); i++) {
      Attr next = (Attr) map.item(i);
      attribs.put(next.getName(), next.getValue());
    }

    //    if (parent != null) {
    //      this.owner = parent.getOwner();
    //      this.domRepresentation =
		// (Element)this.owner.importNode(this.domRepresentation, true);
    //      //parent.appendChild(this);
    //    }
  }

  public void initDOM(Document $owner) {
    this.owner = $owner;
    this.domRepresentation = $owner.createElementNS(namespaceUri, qualifiedName);
  }

  public void initDOM() throws SignatureException {
    try {
      DocumentBuilderFactory dbf = new DocumentBuilderFactoryImpl();
      DocumentBuilder db = dbf.newDocumentBuilder();
      initDOM(db.newDocument());
    } catch (ParserConfigurationException e) {
      throw new SignatureException("Error while initializing DOM", e);
    }
  }

  public Hashtable getNamespaceMappingsInScope() {
    return DOM.getNamespaceMappingsInScopeSpecial(domRepresentation);
  }

  public String getNodeValue() {
     /*
      * In case the text of the node value is broken by &#xd, domRepresentation.getFirstChild().getNodeValue() only returns the first line
      * The get the complete line, normalize must be called before.
      * Changed by Martijn de Boer
      */
    domRepresentation.normalize();
    return domRepresentation.getFirstChild().getNodeValue();
  }

  public void setDomRepresentation(Element domRepresentation) {
    this.domRepresentation = domRepresentation;
  }

  public Node getDomRepresentation() {
    return domRepresentation;
  }

  public void setOwner(Document owner) {
    this.owner = owner;
  }

  public void setParent(GenericElement parent) {
    this.parent = parent;
  }

  public Document getOwner() {
    return owner;
  }

  public String getNamespaceURI() {
    return namespaceUri == null ? "" : namespaceUri;
  }

  public String getQName() {
    return qualifiedName;
  }

  public String getLocalName() {
    return qualifiedName.substring(qualifiedName.indexOf(":") + 1);
  }

  public String getAttribute(String localName, String prefix, String uri) {
    if (uri != null) {
      return domRepresentation.getAttributeNS(localName, uri);
    } else {
      return domRepresentation.getAttribute(localName);
    }

    //return (String)attribs.get(new QName(prefix, localName, uri));
  }

  public Hashtable getAttributes() {
    return attribs;
  }

  public GenericElement getParent() {
    return parent;
  }

  public void setAttribute(String name, String value) {
    //domRepresentation.setAttribute(name , value);
    //LogWriter.getSystemLogWriter().println("GenericElement: Adding attribute with name: " +
		// name);
    if (name.startsWith("xmlns:") || "xmlns".equals(name)) {
      domRepresentation.setAttributeNS(NS.XMLNS, name, value);
    } else {
      domRepresentation.setAttributeNS(null, name, value);
    }
    if (attribs == null){
      attribs = new Hashtable();
    }
    attribs.put(name, value);
  }

  //  public void setAttribute(String uri, String name, String value) {
  //    //domRepresentation.setAttribute(name , value);
  //    //LogWriter.getSystemLogWriter().println("GenericElement: Adding attribute with name: " +
	// name);
  //    domRepresentation.setAttributeNS(uri, name, value);
  //    attribs.put(name, value);
  //  }

  //  public void setAttibutes(Hashtable attribs) {
  //    this.attribs = attribs;
  //  }
  public void appendTextChild(String text) {
    Text t = owner.createTextNode(text);
    domRepresentation.appendChild(t);
  }

  public void appendBinaryTextChild(byte[] value){
    BinaryTextImpl bin = new BinaryTextImpl(owner);
    bin.setBinaryData(value);
    domRepresentation.appendChild(bin);
  }
  public void insertBefore(GenericElement newChild, GenericElement refChild) throws SignatureException {
    Node refC = refChild.getDomRepresentation();
    Node newC = newChild.getDomRepresentation();
    if (newC.getParentNode() == domRepresentation) {
      domRepresentation.removeChild(newC);
    }

    domRepresentation.insertBefore(newC, refC);
    newChild.setParent(this);
  }

  public void appendChild(GenericElement child) throws SignatureException {
    domRepresentation.appendChild(child.getDomRepresentation());
    child.setParent(this);
  }

  public GenericElement getFirstChild() throws SignatureException {
    if ((childElements != null) && (childElements.size() > 0)) {
      return (GenericElement) childElements.get(0);
    } else {
      return null;
    }
  }

  public Vector getDirectChildren(String uri, String localName) throws SignatureException {
    if (childElements == null) {
      return SignatureContext.EMPTY_VECTOR;
    }
    Vector v = new Vector(10);

    for (int i = 0; i < childElements.size(); i++) {
      GenericElement next = (GenericElement) childElements.get(i);

      if (next.getNamespaceURI().equals(uri) && next.getLocalName().equals(localName)) {
        v.add(next);
      }
    }

    return v;
  }

  public GenericElement getDirectChild(String uri, String localName) throws SignatureException {
    if (childElements == null) {
      return null;
    }
    for (int i = 0; i < this.childElements.size(); i++) {
      GenericElement next = (GenericElement) this.childElements.get(i);

      if (next.getNamespaceURI().equals(uri) && next.getLocalName().equals(localName)) {
        return next;
      }
    }

    return null;
  }

  public GenericElement getDirectChildIgnoreCase(String uri, String localName) throws SignatureException {
    if (childElements == null) {
      return null;
    }
    for (int i = 0; i < this.childElements.size(); i++) {
      GenericElement next = (GenericElement) this.childElements.get(i);

      if (next.getNamespaceURI().equals(uri) && next.getLocalName().equalsIgnoreCase(localName)) {
        return next;
      }
    }

    return null;
  }

  public GenericElement getDescendant(String uri, String localName) throws SignatureException {
    GenericElement directChild = getDirectChild(uri, localName);

    if (directChild != null) {
      return directChild;
    }

    if (childElements == null) {
      return null;
    }

    for (int i = 0; i < childElements.size(); i++) {
      GenericElement grandson = ((GenericElement) childElements.get(i)).getDescendant(uri, localName);

      if (grandson != null) {
        return grandson;
      }
    }

    return null;
  }

  public void removeChild(GenericElement child) {
    domRepresentation.removeChild(child.getDomRepresentation());
    child.setParent(null);
  }

  public void initializeDescendants() throws SignatureException {
    if (descendantsInitialized) {
      return;
    }

    NodeList children = domRepresentation.getChildNodes();

    if (children != null) {
      int length = children.getLength();

      if ((length > 0) && (childElements == null)) {
        childElements = new Vector(length);//Collections.synchronizedList(new Vector(length));
      }

      for (int i = 0; i < length; i++) {
        Node next = children.item(i);

        if (next instanceof Element) {
          GenericElement ge = null;

          String nextUri = next.getNamespaceURI();
//          if (nextUri == null) {
//            nextUri = "";
//          }
          String name = next.getLocalName();
          if (Constants.SIGNATURE_SPEC_NS.equals(nextUri)){
            if ("Reference".equals(name)){
              ge = new Reference((Element) next, this);
            } else if ("KeyInfo".equals(name)){
              ge = new KeyInfo((Element) next, this);
            } else if ("RetrievalMethod".equals(name)){
              ge = new RetrievalMethod((Element) next, this);
            }
            else {
              ge = new GenericElement((Element) next, this);
            }
          } else if(Constants.ENCRYPTION_SPEC_NS.equals(nextUri)){
            if ("EncryptionMethod".equals(name)){
              ge = new EncryptionMethod((Element) next, this);
            } else if ("CipherData".equals(name)){
              ge = new CipherData((Element) next, this);
            } else if ("EncryptedKey".equals(name)) {
              ge = new EncryptedKey((Element) next, this);
            } else if ("CipherReference".equals(name)) {
              ge = new CipherReference((Element) next, this);
            } else if ("DataReference".equals(name)) {
              ge = new EncReference((Element) next, this, true);
            } else if ("KeyReference".equals(name)) {
              ge = new EncReference((Element) next, this, false);
            } else if ("ReferenceList".equals(name)){
              ge = new ReferenceList((Element) next, this);
            } else if ("EncryptionProperty".equals(name)){
              ge = new EncryptionProperty((Element) next, this);
            } else if ("EncryptionProperties".equals(name)){
              ge = new EncryptionProperties((Element) next, this);
            }
            else {
//              LogWriter.getSystemLogWriter().println("<GenericElement>:" +
//  						 ((Element)next).getTagName());
              ge = new GenericElement((Element) next, this);
            }
          } else {
            //String name1 = ((Element) next).getTagName();
//            if(!"SecurityTokenReference".equals(name)&&!"Reference".equals(name)){
//            LogWriter.getSystemLogWriter().println("<GenericElement2>:" + name); 
//            LogWriter.getSystemLogWriter().println("<GenericElement2>:" + ((Element) next).getTagName());
//            }
            ge = new GenericElement((Element) next, this);
          }

          this.childElements.add(ge);
        }
      }
    }
    if (childElements != null) {
      for (int i = 0; i < this.childElements.size(); i++) {
        ((GenericElement) childElements.get(i)).initializeDescendants();
      }
    }

    descendantsInitialized = true;
  }
}
