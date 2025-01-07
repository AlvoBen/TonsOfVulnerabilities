package com.sap.engine.lib.xsl.xpath;

import java.io.*;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.xml.parser.URLLoader;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.signature.transform.algorithms.ByteArrayOutputStreamPool;
import com.sap.engine.lib.xml.signature.transform.algorithms.PooledByteArrayOutputStream;
import com.sap.engine.lib.xml.util.SAXToDocHandler;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;


/**
 * Used to initialize or re-initialize the <tt>DTM</tt> table.
 *
 * @see DTM
 * @see DTMManager
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class DTMFactory extends EmptyDocHandler {

  /*
   * <p>
   * Most of the methods are public because <tt>DTMFactory</tt> implements
   * <tt><i>DocHandler</i></tt>. Here are the methods that actually form the
   * interface of this class:
   * </p>
   * <ul>
   *   <li> void initialize(DTM, NamespaceManager); </li>
   *   <li> void approximateAndInitialize(DTM, NamespaceManager, String filename); </li>
   *   <li> void initialize(DTM, NamespaceManager, String filename); </li>
   *   <li> int appendDocument(DTM, InputStream); </li>
   *   <li> int initializeAndAppend(DTM, NamespaceManager, String filename); </li>
   * </ul>
   */
  private XMLParser xmlParser;
  private DTM dtm;
  private IntStack stack = new IntStack(100);
  private NamespaceManager namespaceManager = null;
  private int startingIndex;
  private int lastElementProcessed;
  private DTMApproximator approximator = null;
  private URLLoader urlLoader = new URLLoader();
  private int APPROXIMATE_OVERLOAD = 500;
  private static final CharArray DISABLE_OUTPUT_ESCAPING_PI = new CharArray("javax.xml.transform.disable-output-escaping");
  private static final CharArray ENABLE_OUTPUT_ESCAPING_PI = new CharArray("javax.xml.transform.enable-output-escaping");
  private boolean inText = false;

  public DTMFactory() {
    try {
      approximator = new DTMApproximator();
    } catch (Exception e) {
      //$JL-EXC$
      //when using approximator is checked whether it is != null, and is used only then
    }
  }

  public void initialize(DTM dtm0, NamespaceManager namespaceManager) throws XPathException {
    dtm = dtm0;
    ensureCapacity(dtm, 1000);
    this.namespaceManager = namespaceManager;

    if (dtm.analyzerResult != null) {
      dtm.buffer.assertDataLen(dtm.analyzerResult.docdata + 100);
    }

    stack.clear();
    dtm.clear();
    startingIndex = 0;
  }

  public void approximateAndInitialize(DTM dtm0, NamespaceManager namespaceManager, String filename) throws XPathException {
    dtm = dtm0;
    dtm.buffer.clear();
    stack.clear();
    dtm.clear();
    startingIndex = 0;
    this.namespaceManager = namespaceManager;

    if (approximator != null) {
      int a;
      try {
        //a = approximator.approximate(new FileInputStream(filename));
        a = approximator.approximate(urlLoader.load(filename).openStream(), filename) + APPROXIMATE_OVERLOAD;
      } catch (IOException e) {
        throw new XPathException(e.toString());
      }
      ensureCapacity(dtm, a);
    }
  }

  public void initialize(DTM dtm0, NamespaceManager namespaceManager, InputStream in) throws XPathException {
    dtm = dtm0;
    dtm.clear();
    startingIndex = 0;
    this.namespaceManager = namespaceManager;
    try {
      //int a = approximator.aproximate(new FileInputStream(filename));
      //ensureCapacity(dtm0, a);
      ensureParser();
      xmlParser.parse(in, this);
    } catch (Exception e) {
      throw new XPathException("Unable to create DTM", e);
    }
  }

  public void initializeDirty(DTM dtm0, NamespaceManager namespaceManager, InputStream in) throws XPathException {
    dtm = dtm0;
    startingIndex = 0;
    this.namespaceManager = namespaceManager;
    try {
      //int a = approximator.aproximate(new FileInputStream(filename));
      //ensureCapacity(dtm0, a);
      ensureParser();
      xmlParser.parse(in, this);
    } catch (Exception e) {
      throw new XPathException("Unable to create DTM", e);
    }
  }
  
  public void initialize(DTM dtm0, NamespaceManager namespaceManager, String filename) throws XPathException {
    dtm = dtm0;
    dtm.clear();
    startingIndex = 0;
    this.namespaceManager = namespaceManager;
    try {
      int a = 100;
      if (approximator == null) {       
        a = approximator.approximate(urlLoader.load(filename).openStream(), filename);
      }
      ensureCapacity(dtm0, a);
      ensureParser();
      xmlParser.parse(filename, this);
    } catch (Exception e) {
      throw new XPathException("Unable to create DTM", e);
    }
  }

  public int appendDocument(DTM dtm0, Source source) throws XPathException {
    return appendDocument(dtm0, source, false);
  }

  public int appendDocument(DTM dtm0, Source source, boolean contentOnly) throws XPathException {
    dtm = dtm0;
    startingIndex = dtm.size;
    this.namespaceManager = new NamespaceManager();
    try {
      InputSource inputSource = new InputSource();
      inputSource.setSystemId(source.getSystemId());

      if (source instanceof StreamSource) {
        inputSource.setCharacterStream(((StreamSource) source).getReader());
        inputSource.setByteStream(((StreamSource) source).getInputStream());
        inputSource.setSystemId(source.getSystemId());
      } else if (source instanceof DOMSource) {
         TransformerFactory factory = new TransformerFactoryImpl();//TransformerFactory.newInstance();
         Transformer tr = factory.newTransformer();
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         tr.transform(source, new StreamResult(out));
         ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
         inputSource.setByteStream(in);
 //        String data = ((DOMSource)source).getNode().toString();
 //        inputSource.setCharacterStream(new StringReader(data));
      } else if (source instanceof SAXSource) {
        SAXSource ss = (SAXSource)source;
        ss.getXMLReader().setContentHandler(new SAXToDocHandler(this));
        ss.getXMLReader().parse(ss.getInputSource());
        return startingIndex;
      }

//      int a = 1000; //approximator.approximate(inputStream);
//      ensureCapacity(dtm0, dtm0.getCapacity() + a);
      ensureParser();
      xmlParser.setScanContentOnly(contentOnly);

      if (contentOnly) {
        this.startDocument();
      }

      xmlParser.parse(inputSource, this);

      if (contentOnly) {
        this.endDocument();
      }
    } catch (Exception e) {
      throw new XPathException("XPath : unable to create DTM", e);
    }
    return startingIndex;
  }

//  public int appendDocument(DTM dtm0, String url) throws XPathException {
//    dtm = dtm0;
//    startingIndex = dtm.size;
//    this.namespaceManager = new NamespaceManager();
//    InputStream inputStream;
//    try {
//      inputStream = urlLoader.load(url).openStream();
//      int a = 100;
//      if (approximator == null) {       
//        a = approximator.approximate(inputStream) + APPROXIMATE_OVERLOAD;
//      }
//      ensureCapacity(dtm0, dtm0.getCapacity() + a);
//      ensureParser();
//      xmlParser.parse(inputStream, this);
//    } catch (Exception e) {
//      throw new XPathException("XPath : unable to create DTM", e);
//    }
//    return startingIndex;
//  }

//  public int initializeAndAppend(DTM dtm0, NamespaceManager namespaceManager, String filename) throws XPathException {
//    dtm = dtm0;
//    startingIndex = dtm.size;
//    this.namespaceManager = namespaceManager;
//    try {
//      int a = 100;
//      if (approximator == null) {       
//        a = approximator.approximate(new FileInputStream(filename));
//      }
//      ensureCapacity(dtm0, dtm0.getCapacity() + a);
//      ensureParser();
//      xmlParser.parse(filename, this);
//    } catch (Exception e) {
//      throw new XPathException("XPath : unable to create DTM");
//    }
//    return startingIndex;
//  }

  // Methods from interface DocHandler
  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    //int uriid = namespaceManager.put(uri);
    dtm.appendNode(qname, namespaceManager.get(uri), -1, DTM.ELEMENT_NODE, null, -1, -1);
    dtm.indexStart[dtm.size - 1] = dtm.buffer.length();
    //indexStart.add(dtm.buffer.length());
    //indexEnd.add(-1); // this will be known later, when void endElement is called.
    int x = dtm.size - 1;
    int b = stack.pop();
    int p = stack.top();
//    System.out.println("DTMFactory: startElementStart: x = " + x + ", b = " + b + ", p = " + p + ", localName = " + localName + ", parent = " + dtm.name[p] + ", parentType = " + dtm.nodeType[p]);

    if ((b == DTM.NONE) && (p != DTM.NONE)) {
      dtm.firstChild[p] = x;
    }

    if (p != DTM.NONE) {
      dtm.parent[x] = p;
    }

    if (b != DTM.NONE) {
      dtm.nextSibling[b] = x;
      dtm.previousSibling[x] = b;
    }

    stack.push(x);
    stack.push(DTM.NONE);
    lastElementProcessed = x;
    inText = false;
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray localName, CharArray qName, String type, CharArray value) throws Exception {
    //System.out.println("DTMFactory:addAttribute: name = " + localName + ", qname=" + qName + ", node=" + dtm.size);
    if (prefix.equals(XMLParser.caXMLNS)) {
      dtm.appendNode(localName, CharArray.EMPTY, -1, DTM.NAMESPACE_NODE, value, -1, -1);
    } else if (qName.equals(XMLParser.caXMLNS)) {
      dtm.appendNode(CharArray.EMPTY, CharArray.EMPTY, -1, DTM.NAMESPACE_NODE, value, -1, -1);
    } else {
      dtm.appendNode(qName, namespaceManager.get(uri), -1, DTM.ATTRIBUTE_NODE, value, -1, -1);
    }

    dtm.parent[dtm.size - 1] = lastElementProcessed;
    
    if (dtm.lastAttribute != -1) {
      dtm.nextSibling[dtm.lastAttribute] = dtm.size -1;
    } else {
      dtm.firstAttr[lastElementProcessed] = dtm.size -1;
    }
    
    dtm.lastAttribute = dtm.size-1;
                          

    if ((type != null) && type.equals("ID")) {
      dtm.addIdAttribute(dtm.size - 1);
    }
    inText = false;

    //indexStart.add(-1);
    //indexEnd.add(-1);
  }

  public void startElementEnd(boolean b) {
    inText = false;
    dtm.lastAttribute = -1;
//    if (dtm.previousSibling[lastElementProcessed] == -1) {
//      // the element is the first child
//      dtm.previousSibling[lastElementProcessed] = dtm.lastAttribute;
//    }

  }

  public void endElement(CharArray uri, CharArray localName, CharArray QName, boolean b) throws Exception {
    stack.pop();
    int x = stack.top();
    dtm.indexEnd[x] = dtm.buffer.length();
    inText = false;
  }

  public void startDocument() throws Exception {
    // Must first add the root, '/'
    dtm.appendNode(CharArray.EMPTY, null, -1, DTM.DOCUMENT_NODE, null, -1, -1);
    stack.push(dtm.size - 1);
    stack.push(DTM.NONE);
    inText = false;
    //dtm.indexStart[dtm.size - 1] = dtm.buffer.length();
    //indexStart.clear();
    //indexEnd.clear();
    //indexStart.add(-1);
    //indexEnd.add(-1);
  }

  public void endDocument() throws Exception {
    
    //TODO - this seems wrong !!!!!!!, if we are in processing of a variable, we should not bother the dtm root eleemnt
    dtm.indexStart[0] = 0;
    dtm.indexEnd[0] = dtm.buffer.length();
    
    
    dtm.bEndDoc = true;
    inText = false;
    int x;
    do {
      x = stack.pop();
    } while (x == DTM.NONE || dtm.nodeType[x] != DTM.DOCUMENT_NODE);
  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {
    //    System.out.println("DTMFactory.charData: carr=" + carr + ", dtm.size=" + dtm.size );
    int l0 = dtm.buffer.length();
    dtm.buffer.append(carr);
    int l1 = dtm.buffer.length();
    if (inText) {
      dtm.indexEnd[dtm.size - 1] = l1;
      return;
    }
    dtm.appendNode(CharArray.EMPTY, null, -1, DTM.TEXT_NODE, null, l0, l1);
    //indexStart.add(dtm.buffer.length());
    //dtm.buffer.append(carr);
    //indexEnd.add(dtm.buffer.length());
    int x = dtm.size - 1;
    int b = stack.pop();
    int p = stack.top();

    if ((b == DTM.NONE) && (p != DTM.NONE)) {
      dtm.firstChild[p] = x;
    }

    if (p != DTM.NONE) {
      dtm.parent[x] = p;
    }

    if (b != DTM.NONE) {
      dtm.nextSibling[b] = x;
      dtm.previousSibling[x] = b;
    }

    stack.push(x);
    inText = true;
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    //    System.out.println("DTMFactory.onPI: " + target + ", data=" + data);
    //dont create processing instructions for internal pis
    if (target.equals(DISABLE_OUTPUT_ESCAPING_PI)) {
      return;
    }
    if (target.equals(ENABLE_OUTPUT_ESCAPING_PI)) {
      return;
    }
    int l0 = dtm.buffer.length();
    dtm.buffer.append(data);
    int l1 = dtm.buffer.length();
    dtm.appendNode(target, null, -1, DTM.PROCESSING_INSTRUCTION_NODE, null, l0, l1);
    //indexStart.add(dtm.buffer.length());
    //dtm.buffer.append(data);
    //indexEnd.add(dtm.buffer.length());
    int x = dtm.size - 1;
    int b = stack.pop();
    int p = stack.top();

    if ((b == DTM.NONE) && (p != DTM.NONE)) {
      dtm.firstChild[p] = x;
    }

    if (p != DTM.NONE) {
      dtm.parent[x] = p;
    }

    if (b != DTM.NONE) {
      dtm.nextSibling[b] = x;
      dtm.previousSibling[x] = b;
    }

    stack.push(x);
    inText = false;
  }

  public void onComment(CharArray text) throws Exception {
    dtm.appendNode(CharArray.EMPTY, null, -1, DTM.COMMENT_NODE, text, -1, -1);
    //indexStart.add(-1);
    //indexEnd.add(-1);
    int x = dtm.size - 1;
    int b = stack.pop();
    int p = stack.top();

    if ((b == DTM.NONE) && (p != DTM.NONE)) {
      dtm.firstChild[p] = x;
    }

    if (p != DTM.NONE) {
      dtm.parent[x] = p;
    }

    if (b != DTM.NONE) {
      dtm.nextSibling[b] = x;
      dtm.previousSibling[x] = b;
    }

    stack.push(x);
    inText = false;
  }

  public void onCDSect(CharArray text) throws Exception {
    charData(text, true);
  }

  private void ensureCapacity(DTM dtm, int a) {
    if (dtm.getCapacity() < a) {
      dtm.resize(a);
    }

    /*
     if (indexStart == null) {
     indexStart = new IntVector(100000, 1000);
     indexEnd = new IntVector(100000, 1000);
     } else {
     if (indexStart.getCapacity() < a) {
     indexStart.resize(a);
     indexEnd.resize(a);
     }
     }
     */
  }

  private void ensureParser() throws Exception {
    if (xmlParser == null) {
      xmlParser = new XMLParser();
      xmlParser.setValidation(false);
      xmlParser.setNamespaces(true);
      xmlParser.setNamespacePrefixes(true);
    }
  }

  public void onWarning(String warning) throws Exception {

  }

  public void setDTM(DTM adtm) {
    dtm = adtm;
  }
  
  public DTM getDTM() {
    return dtm;
  }

  public void setNodeToHandler(Node node) {
    if (node != null) {
      if (dtm.domtree == null) {
        dtm.setDOMInitialized(true);
        dtm.domtree = new Node[dtm.name.length];
      } else if (dtm.domtree.length < dtm.name.length) {
        Node[] newDomTree = new Node[dtm.name.length];
        System.arraycopy(dtm.domtree, 0, newDomTree, 0, dtm.domtree.length);
        dtm.domtree = newDomTree;
      }
      dtm.domtree[dtm.size - 1] = node;
    }
  }
}

