package com.sap.engine.lib.xml.dom;

import java.net.URL;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.NestedSAXParseException;
import com.sap.engine.lib.xml.parser.dtd.DTDElement;
import com.sap.engine.lib.xml.parser.dtd.XMLValidator;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NS;
import com.sap.engine.lib.xml.util.SymbolTable;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public final class DOMDocHandler1 implements DocHandler {

  private DocumentImpl documentImpl;
  private Document document;
  private Node root;
  private Node currentNode;
  private ErrorHandler errorHandler;
  private Element element;
  private DocumentType dtd;
  private boolean isInDTD;
  private boolean firstElementReached;
  private boolean isIgnoringComments = false;
  private XMLValidator xmlValidator = null;
  private boolean useCreateMethods = false;
  private boolean optionTrimWhiteSpace;
  private boolean isAttributeSpecified = true;
  private Attr attr;

//  private Hashtable hash = new Hashtable();
//  private int maxsize = 100;
//  private int cursize = 0;
  
  
  private Hashtable chdhash = new Hashtable();
  private Hashtable qnhash = new Hashtable();
  private Hashtable urihash = new Hashtable();

  
  private static final int chdmaxsize = 100, qnmaxsize = 100, urimaxsize = 100;
  private int chdcursize = 0, qncursize = 0, uricursize = 0;
  private Object tmp = null;
  private CharArray tmp2 = null;
  private String tmps = null;



  
  
  public DOMDocHandler1() {
    document = documentImpl = new DocumentImpl().init(null, null);
    useCreateMethods = false;
    root = document;
  }

  public DOMDocHandler1(Document document) {
    if (document instanceof DocumentImpl) {
      this.document = this.documentImpl = (DocumentImpl) document;
      useCreateMethods = (document.getClass() != DocumentImpl.class);
    } else {
      this.document = document;
      useCreateMethods = true;
    }

    this.root = document;
  }

  public DOMDocHandler1(Node node) {
    if (node.getNodeType() == Node.DOCUMENT_NODE) {
      Document document = (Document) node;

      if (document instanceof DocumentImpl) {
        this.document = this.documentImpl = (DocumentImpl) document;
        useCreateMethods = (document.getClass() != DocumentImpl.class);
      } else {
        this.document = document;
        useCreateMethods = true;
      }

      this.root = document;
      return;
    }

    this.document = node.getOwnerDocument();

    if (document == null) {
      document = new DocumentImpl().init(null, null);
      useCreateMethods = false;
    } else {
      useCreateMethods = (document.getClass() != DocumentImpl.class);
//    useCreateMethods = true;
    }

    root = node;
  }

  public void setIgnoringComments(boolean b) {
    isIgnoringComments = b;
  }

  public Document getDocument() {
    return document;
  }

  public void setXMLValidator(XMLValidator inst) {
    xmlValidator = inst;
  }

  public void onXMLDecl(String v, String e, String s) {

  }

  public void startDocument() throws Exception {
    chdhash = new Hashtable();
    qnhash = new Hashtable();
    urihash = new Hashtable();

    currentNode = (root == null) ? document : root;
    isInDTD = false;
    firstElementReached = false;
  }

  
  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    if (uri == null) {
      uri = CharArray.EMPTY;
    }
    String qnstring  = getQn(qname);
    String uristring = getUri(uri);
     
    if (useCreateMethods) {
      element = document.createElementNS(uristring, qnstring);
    } else {
      element = new ElementImpl(document).init(uristring, qnstring, true, null, false);
    }
//    if (useCreateMethods) {
//      element = document.createElementNS(uri.toString(), qname.toString());
//    } else {
//      element = new ElementImpl(document).init(uri.toString(), qname.toString(), true, null, false);
//    }
    


    if (!firstElementReached) {
      currentNode = root;
      firstElementReached = true;
    }
  }
    
//  public String get(CharArray item) {
//    item.bufferHash();
//    tmp = hash.get(item);
//    item.clearHashReady();
//
//    if (tmp == null && cursize < maxsize) {
//      tmp2 = item.copy();
//      tmps = tmp2.getString();
//      tmp2.bufferHash();
//      //      LogWriter.getSystemLogWriter().println("bufferning: " + tmps);
//      hash.put(tmp2, tmps);
//      cursize++;
//      return tmps;
//    } else if (cursize == maxsize) {
//      return item.getString();
//    } else {
//      return (String) tmp;
//    }
//  }

  public String getChd(CharArray item) {
    item.bufferHash();
    tmp = chdhash.get(item);
    item.clearHashReady();

    if (tmp == null && chdcursize < chdmaxsize) {
      tmp2 = item.copy();
      tmps = tmp2.getString();
      tmp2.bufferHash();
      //      LogWriter.getSystemLogWriter().println("bufferning: " + tmps);
      chdhash.put(tmp2, tmps);
      chdcursize++;
      return tmps;
    } else if (chdcursize == chdmaxsize) {
      return item.getString();
    } else {
      return (String) tmp;
    }
  }
   
  private SymbolTable sTable = new SymbolTable(512); //keeps namespaces and localnames in order to save memory.

  public String getQn(CharArray item) {
    item.bufferHash();
    tmp = qnhash.get(item);
    item.clearHashReady();

    if (tmp == null && qncursize < qnmaxsize) {
      tmp2 = item.copy();
      //tmps = tmp2.getString().intern();
      tmps = sTable.addSymbol(tmp2);
      tmp2.bufferHash();
      //      LogWriter.getSystemLogWriter().println("bufferning: " + tmps);
      qnhash.put(tmp2, tmps);
      qncursize++;
      return tmps;
    } else if (qncursize == qnmaxsize) {
      return item.getString();
    } else {
      return (String) tmp;
    }
  }

  public String getUri(CharArray item) {
    item.bufferHash();
    tmp = urihash.get(item);
    item.clearHashReady();

    if (tmp == null && uricursize < urimaxsize) {
      tmp2 = item.copy();
      //tmps = tmp2.getString().intern();
      tmps = sTable.addSymbol(tmp2);
      tmp2.bufferHash();
      //      LogWriter.getSystemLogWriter().println("bufferning: " + tmps);
      urihash.put(tmp2, tmps);
      uricursize++;
      return tmps;
    } else if (uricursize == urimaxsize) {
      return item.getString();
    } else {
      return (String) tmp;
    }
  }



  
  public void addAttribute(CharArray suri, CharArray prefix, CharArray localName, CharArray qnameCA, String type, CharArray valueCA) throws Exception {
//    LogWriter.getSystemLogWriter().println("DOMDocHandler.addAttrubyte:" + suri + ", " + prefix + ", " + localName + ", " + qnameCA + ", spec= " + isAttributeSpecified);

	String qname = getQn(qnameCA);
    String value = getUri(valueCA);

    if (suri == null) {
      suri = CharArray.EMPTY;
    }

    String uri = getUri(suri);
    
    if (!isAttributeSpecified && qname.startsWith("xml:")) {
        isAttributeSpecified = true;
        return;
    }

    if (qname.startsWith("xmlns")) {
      uri = NS.XMLNS;
    }

    if (useCreateMethods) {
      attr = document.createAttributeNS(uri, qname);
      attr.setNodeValue(valueCA.toString());
    } else {
//      String defaultValue = documentImpl.getAttributeDefault(element.getNodeName(), qname);
      attr = new AttrImpl(document).init(uri, qname, true, value, null, false, isAttributeSpecified);
    }

  	try {
      element.setAttributeNodeNS(attr);
    } catch (NoSuchMethodError e) {
      //$JL-EXC$

      element.setAttributeNode(attr);
    }
    isAttributeSpecified = true;
  }

  public AttrImpl getAttr() {
    return((AttrImpl)attr);
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    currentNode = currentNode.appendChild(element);
    attr = null;
  }

  public void endElement(CharArray uri, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    currentNode = currentNode.getParentNode();
  }

  public void endDocument() {
    if (documentImpl != null) {
      documentImpl.updateDTDForAllElements();
    }

    currentNode = null;
  }

  public void charData(CharArray data, boolean bDisableOutputEscaping) {
    if (optionTrimWhiteSpace && (xmlValidator != null) && (xmlValidator.getElementContentSpec(currentNode.getNodeName()) == DTDElement.CHILDREN) && isWhitespace(data)) {
      return;
    }

    //String s = data.getString();
    String s = getChd(data);
    Text text = document.createTextNode(s);
    currentNode.appendChild(text);
  }

  public void onPI(CharArray targetCA, CharArray dataCA) {
    String target = targetCA.getString();
    String data = dataCA.getString();
    ProcessingInstruction pi = document.createProcessingInstruction(target, data);
    currentNode.appendChild(pi);
  }

  public void onComment(CharArray text) {
    if (isIgnoringComments) {
      return;
    }

    if (isInDTD) {
      return;
    }

    Comment comment = document.createComment(text.getString());
    currentNode.appendChild(comment);
  }

  public void onCDSect(CharArray text) {
    CDATASection cd = document.createCDATASection(text.getString());
    currentNode.appendChild(cd);
  }

  public void startDTD(CharArray nameCA, CharArray pubCA, CharArray sysCA) {
    String name = nameCA.getString();
    String publicId = pubCA.getString();
    String systemId = sysCA.getString();
    publicId = ("".equals(publicId)) ? null : publicId;
    systemId = ("".equals(systemId)) ? null : systemId;

    if (documentImpl != null) {
      dtd = new DocumentTypeImpl().init(name, publicId, systemId, documentImpl);
      documentImpl.setDTD(dtd);
    } else {
      dtd = document.getImplementation().createDocumentType(name, publicId, systemId);
      documentImpl.appendChild(dtd);
    }

    isInDTD = true;
  }

  public void onDTDEntity(com.sap.engine.lib.xml.parser.helpers.Entity ent) {
    if (!ent.isPE()) {
      String name = ent.getName().getString();
      String publicId = ent.getPub().getString();
      String systemId = ent.getSys().getString();
      CharArray notationNameCA = ent.getNote();
      String notationName = (notationNameCA == null) ? "" : notationNameCA.getString();
      publicId = (publicId.length() == 0) ? null : publicId;
      systemId = (systemId.length() == 0) ? null : systemId;
      notationName = (notationName.length() == 0) ? null : notationName;
      String value = "";

      if (ent.isInternal()) {
        try {
          value = ent.getValue().toString();
        } catch (Exception e) {
          //$JL-EXC$

			value = "";
        }
      }

      EntityImpl entity = new EntityImpl().init(name, publicId, systemId, value, document);
      entity.setNotationName(notationName);
      dtd.getEntities().setNamedItem(entity);
    }
  }

  public void onDTDNotation(CharArray nameCA, CharArray pubCA, CharArray sysCA) {
    String name = nameCA.getString();
    String publicId = pubCA.getString();
    String systemId = sysCA.getString();
    publicId = (publicId.equals("")) ? null : publicId;
    systemId = (systemId.equals("")) ? null : systemId;
    Notation notation = new NotationImpl().init(name, publicId, systemId
    /*, document*/
    );
    dtd.getNotations().setNamedItem(notation);
  }

  public void onDTDElement(CharArray name, CharArray model) {
    // ignore
  }

  public void onDTDAttListItem(CharArray elementName, CharArray attributeName, String type, String defaultDeclaration, CharArray vAttValue, String note) {
    if (documentImpl != null) {
      if ("ID".equals(type)) {
        documentImpl.addIdAttribute(elementName.getString(), attributeName.getString());
      }

      String attValue = vAttValue.getString();

      if (attValue.length() != 0) {
        documentImpl.addDefaultAttribute(elementName.getString(), attributeName.getString(), vAttValue.getString());
      }
    }
  }

  public void onDTDAttListStart(CharArray name) {
    // ignore
  }

  public void onDTDAttListEnd() {
    // ignore
  }

  public void endDTD() {
    isInDTD = false;
  }

  public void onContentReference(com.sap.engine.lib.xml.parser.helpers.Reference ref) {

  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) {

  }

  public void endPrefixMapping(CharArray prefix) {

  }

  public void setDOMTrimWhiteSpaces(boolean value) {
    optionTrimWhiteSpace = value;
  }

  public void onWarning(String warning) throws Exception {
    if (errorHandler != null) {
      errorHandler.warning(new SAXParseException(warning, null));
    }
  }

  public void setErrorHandler(ErrorHandler e) {
    errorHandler = e;
  }

//  private String trimWhitespace(String s) {
//    int i = 0;
//
//    while (true) {
//      if (i >= s.length()) {
//        return "";
//      }
//
//      char ch = s.charAt(i);
//
//      if ((ch != ' ') && (ch != 0xA) && (ch != 0xD) && (ch != 0x9)) {
//        break;
//      }
//
//      i++;
//    }
//
//    int j = s.length() - 1;
//
//    while (true) {
//      char ch = s.charAt(j);
//
//      if ((ch != ' ') && (ch != 0xA) && (ch != 0xD) && (ch != 0x9)) {
//        break;
//      }
//
//      j--;
//    }
//
//    return s.substring(i, j + 1);
//  }

  public static boolean isWhitespace(CharArray c) {
    for (int i = 0; i < c.length(); i++) {
      if (!Symbols.isWhitespace(c.charAt(i))) {
        return false;
      }
    } 

    return true;
  }

  public void onStartContentEntity(CharArray name, boolean isExpanding) {
    if (!isExpanding) {
      currentNode.appendChild(document.createEntityReference(name.toString()));
    }
  }

  public void onEndContentEntity(CharArray name) {

  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {
    switch (eventId) {
        
      case DocHandler.ATTRIBUTE_IS_NOT_SPECIFIED: {
        isAttributeSpecified = false;
        break;
      }
      case DocHandler.DOCUMENT_URL: {
        if (documentImpl != null) {
          documentImpl.setLocation((URL) obj);
        }

        break;
      }
      case DocHandler.NAMESPACE_AWARENESS: {
        if (documentImpl != null) {
          documentImpl.setNamespaceAwareness(Boolean.TRUE.equals(obj) ? DOM.NSA_TRUE : DOM.NSA_FALSE);
        }

        break;
      }
      case DocHandler.XML_SPEC_ERROR: {
        if (errorHandler != null) {
          errorHandler.error(new NestedSAXParseException((Exception)obj));
        }
      }
    }
  }

  public ElementImpl getElement() {
    return((ElementImpl)element);
  }

  public NodeImpl getCurrentNode() {
    return((NodeImpl)currentNode);
  }

}

