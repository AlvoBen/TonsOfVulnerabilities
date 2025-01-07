package com.sap.engine.lib.xsl.xpath;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.dom.AttrImpl;
import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.pool.CharArrayPool;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.util.analyze.XMLAnalyzerResult;
import com.sap.engine.lib.xsl.xpath.xobjects.XObjectFactory;
import com.sap.engine.lib.xsl.xslt.QName;
import com.sap.engine.lib.xsl.xslt.VariableContext;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;

/**
 * <p>
 * Represents the XML document as arrays of integers.
 * </p>
 * <p>
 * This is an alternative to the DOM API, which usually requires a lot of
 * space and involves object creation. DTM, or the Document Table Model,
 * has been developed to meet the needs of the XSLT engine, the XPath
 * in particular.
 * </p>
 * <p>
 * The idea of such a 'table' model and the name 'DTM' is due to Apache.
 * This implementation, however, has nothing to do with their source code.
 * </p>
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version July 2001
 */
public final class DTM {

  public static final int DEFAULT_CAPACITY = 10; //100000;
  public int RESIZE_STEP = DEFAULT_CAPACITY;
//  private XPathContext initialContext = null;
  
  public static int MAX_SIZE = 1000;
  
  public int size = 0;
  /**
   * Length of the arrays.
   */
  private int capacity;
  /**
   * Arrays, containing information about the nodes.
   */
  public QName[] name;
  public byte[] nodeType;
  public int[] parent;
  public int[] firstChild;
  public int[] firstAttr;
  public int[] previousSibling;
  public int[] nextSibling;
  public int[] documentNodes;
  public int documentNodesCount = 0;
  private boolean[] initialized = null;
  private StringBuffer leadingText = new StringBuffer(1024);
  private static final String[] empty = new String[] {};// an empty String array
  // when part of the DOM tree is initialized there could be some nodes that have all thier ancestors
  // excluded form the tree - these are detected, marked in this array and later appended to the DocumentFragment node
  private boolean[] dangling = null;
  private boolean domInitialized = false;
  private DocumentImpl doc = null;
  private Node frag = null;
  protected CharArray[] stringValue;
  protected CharArray buffer = new CharArray(100000, 100000);
  protected int[] indexStart;
  protected int[] indexEnd;
  public XMLAnalyzerResult analyzerResult = null;
//  protected IntStack pendingSibling = new IntStack();
  boolean bEndDoc = false;
  private int[] idAttributes = new int[100];
  private int nIdAttributes = 0;
  
  private int danglingLength = 0;
  public int lastAttribute = -1;
  
  public Node[] domtree = null;  
  /**
   * Same as the constants from org.w3c.dom.Node
   */
  public static final byte ELEMENT_NODE = 1;
  public static final byte ATTRIBUTE_NODE = 2;
  public static final byte TEXT_NODE = 3;
  public static final byte CDATA_SECTION_NODE = 4;
  public static final byte ENTITY_REFERENCE_NODE = 5;
  public static final byte ENTITY_NODE = 6;
  public static final byte PROCESSING_INSTRUCTION_NODE = 7;
  public static final byte COMMENT_NODE = 8;
  public static final byte DOCUMENT_NODE = 9;
  public static final byte DOCUMENT_TYPE_NODE = 10;
  public static final byte DOCUMENT_FRAGMENT_NODE = 11;
  public static final byte NOTATION_NODE = 12;
  public static final byte NAMESPACE_NODE = 13;
  public static final byte WHATEVER_NODE = 100; // used in XNodeSet
  public static final int NONE = -1;
  public CharArrayPool carrPool = new CharArrayPool(10000, 10000);
//  public ObjectPool qnamePool = new ObjectPool(QName.class, 10000, 10000);
  public Hashtable qnamesTable = new Hashtable(10000);
  public XObjectFactory xfactProcess = null;
  public XObjectFactory xfactMatch = null;
  public Hashtable xfDocumentCache = new Hashtable();

  public DTM() {
    this(DEFAULT_CAPACITY);
  }

  public DTM(int capacity) {
    this.capacity = capacity;
    name = new QName[capacity];
    nodeType = new byte[capacity];
    parent = new int[capacity];
    firstChild = new int[capacity];
    firstAttr = new int[capacity];
    previousSibling = new int[capacity];
    nextSibling = new int[capacity];
    stringValue = new CharArray[capacity];
    indexStart = new int[capacity];
    indexEnd = new int[capacity];
    documentNodes = new int[DEFAULT_CAPACITY];
  }

  public void setFactories(XObjectFactory xp, XObjectFactory xm) {
    xfactProcess = xp;
    xfactMatch = xm;
  }

  public XPathContext getInitialContext() {
    return getInitialContext(null);
  }

  public XPathContext getInitialContext(XSLStylesheet owner) {
    if (xfactProcess == null) {
      xfactProcess = new XObjectFactory(100);
      xfactMatch = xfactProcess;
    }
    return new XPathContext(this, 0, 1, 1, new VariableContext(), new LibraryManager(), new Hashtable(), 0, xfactProcess, xfactMatch, owner, null);
  }

  public void clear() {
    size = 0;
    analyzerResult = null;
    buffer.clear();
//    initialContext = null;
    xfactProcess = null;

    xfactMatch = null;
    xfDocumentCache.clear();
    nIdAttributes = 0;
    documentNodesCount = 0;
    //leadingText.
    qnamesTable.clear();
    domInitialized = false;
  }
  
  
  public void clearDirty() {
    size = 0;
    analyzerResult = null;
    buffer.clear();
//    initialContext = null;
    if (xfactProcess != null){
      xfactProcess.releaseAllPools();
    }
    if (xfactMatch!=null){
      xfactMatch.releaseAllPools();
    }
    xfDocumentCache.clear();
    nIdAttributes = 0;
    documentNodesCount = 0;
    leadingText.setLength(0);
    if (capacity>MAX_SIZE){
      trim();
      xfactMatch = null;
      xfactProcess = null;
    }
    //leadingText.
    qnamesTable.clear();
  }
  

  public void addIdAttribute(int x) {
    if (idAttributes.length == nIdAttributes) {
      int[] idAttributesOld = idAttributes;
      idAttributes = new int[nIdAttributes * 2];
      System.arraycopy(idAttributesOld, 0, idAttributes, 0, nIdAttributes);
    }

    idAttributes[nIdAttributes] = x;
    nIdAttributes++;
  }

  public int getElementById(String id) {
    for (int i = 0; i < nIdAttributes; i++) {
      if (getStringValue(idAttributes[i]).equals(id)) {
        return parent[idAttributes[i]];
      }
    } 

    return -1;
  }

  protected void appendNode(CharArray name0, CharArray uri, int uriId, byte nodeType0, CharArray stringValue0, int indexStart0, int indexEnd0) {
    if (size >= capacity - 1) {
      resize();
    }

    if (nodeType0 == DOCUMENT_NODE) {

      bEndDoc = false;
      documentNodes[documentNodesCount++] = size;
      if (documentNodesCount == documentNodes.length) {
        int[] temp = new int[documentNodesCount + RESIZE_STEP];
        System.arraycopy(documentNodes, 0, temp, 0, documentNodesCount);
        documentNodes = temp;
      }
    } else {
      bEndDoc = false;
    }

    if (uri == null) {
      uri = CharArray.EMPTY;
    }

//    if ((name[size] == null) && (nodeType0 != TEXT_NODE)) {
//      name[size] = (QName) qnamePool.getObject();
//    }
//
//    if (name[size] != null) {
//      name[size].reuse(name0, uri, uriId);
//    }

    if (nodeType0 != TEXT_NODE) {
      String key = "{" + uri + '}' + name0;
      QName qname = (QName) qnamesTable.get(key);
      if (qname == null) {
        qname = new QName().reuse(name0, uri, uriId);
        qnamesTable.put(key, qname);
      }
      name[size] = qname;
    } else {
      name[size] = null;
    }

    nodeType[size] = nodeType0;
    parent[size] = firstChild[size] = previousSibling[size] = nextSibling[size] = NONE;

    if (stringValue0 != null) {
      if (stringValue[size] != null) {
        stringValue[size].clear();
      }

      if (stringValue[size] == null) {
        stringValue[size] = carrPool.getObject().reuse();
      }

      stringValue[size].set(stringValue0);
    }

    indexStart[size] = indexStart0;
    indexEnd[size] = indexEnd0;
    size++;
  }

  /**
   * Used in the toDetailedString() method.
   */
  private void appendDetailedItem(StringBuffer sb, int index) {
    sb.append("    [").append(index).append("] ").append(nodeTypeToString(nodeType[index])).append(" name='").append(name[index]).append("', parent=").append(parent[index]).append(", previousSibling=").append(previousSibling[index]).append(", nextSibling=").append(nextSibling[index]).append(", firstChild=").append(firstChild[index]).append(", stringValue='");

    if (stringValue[index].length() < 30) {
      sb.append(stringValue[index].replace('\n', '#'));
    } else {
      sb.append(stringValue[index].replace('\n', '#'));
      sb.append("...");
    }

    sb.append("']\n");
  }

  /**
   * Returns a String, which is a detailed description of the DTM.
   */
  public String toDetailedString() {
    if (size == 0) {
      return "(EMPTY DTM)";
    }

    StringBuffer r = new StringBuffer(100000);
    r.append("\nDocument Table Model, detailed description: \n");
    appendDetailedItem(r, 0);

    for (int i = 1; i < size; i++) {
      appendDetailedItem(r, i);
    } 

    r.append("\n");
    return r.toString();
  }

  /**
   * Appends the children of a node into an int[] starting from a
   * specified position. Resizes, copies, and returns the int[] if necessary.
   * If not resized, the parameter int[] itself is returned.
   * A denotes the end of the children list.
   */
  public int[] getChildNodes(int node, int[] nodes, int start) {
    int i = firstChild[node];
    int p = start;
    int nodesLength = nodes.length;

    while (true) {
      if (p >= nodesLength) {
        nodes = resizeIntArray(nodes, p);
        nodesLength = nodes.length;
      }

      nodes[p] = i;

      if (i == NONE) {
        break;
      }

      i = nextSibling[i];
      p++;
    }

    return nodes;
  }

  // to be optimized
  public int[] getChildNodes(int node) {
    int i = firstChild[node];
    int nodeCount = 0;

    while (i != DTM.NONE) {
      nodeCount++;
      i = nextSibling[i];
    }

    int[] nodes = new int[nodeCount];
    i = firstChild[node];
    nodeCount = 0;

    while (i != DTM.NONE) {
      nodes[nodeCount++] = i;
      i = nextSibling[i];
    }

    return nodes;
  }

  // to be optimized
  public int[] getAttributeNodes(int node, int[] nodes, int start) {
    int x = node + 1;
    int p = start;
    int nodesLength = nodes.length;

    while (true) {
      if (p >= nodesLength) {
        nodes = resizeIntArray(nodes, p);
        nodesLength = nodes.length;
      }

      int t = nodeType[x];

      if ((t != ATTRIBUTE_NODE) && (t != NAMESPACE_NODE)) {
        break;
      }

      if (t == ATTRIBUTE_NODE) {
        nodes[p] = x;
        p++;
      }

      x++;
    }

    nodes[p] = NONE;
    return nodes;
  }
  
    
  public int[] getAttributeAndNSNodes(int node, int[] nodes, int start) {
    int x = firstAttr[node];
    int p = start;
    while (x != -1) {
      nodes[p++] = x;
      if (p >= nodes.length) {
        nodes = resizeIntArray(nodes, p);
      }
      x = nextSibling[x];
      
    }
//    int x = node + 1;
//    int p = start;
//    int nodesLength = nodes.length;
//    
//
//    while (x < size) {
//      if (p >= nodesLength) {
//        nodes = resizeIntArray(nodes, p);
//        nodesLength = nodes.length;
//      }
//
//      int t = nodeType[x];
//
//      if ((t != ATTRIBUTE_NODE) && (t != NAMESPACE_NODE)) {
//        break;
//      }
//      nodes[p] = x;
//      p++;
//
//      x++;
//    }

    nodes[p] = NONE;
    return nodes;
  }


  public int getAttributesStartIndex(int node) {
    return (firstAttr[node] != -1)? firstAttr[node]: node + 1;
  }

  public int getAttributesEndIndex(int node) {
    
    //must be alligned with concepts for XSLT 1.1 vars
    if (firstAttr[node] != -1) {
      int x = firstAttr[node];
      while (nextSibling[x] != -1) {
        x = nextSibling[x];
      }
      return x;
    } else {
      int x = node + 1;
  
      while ((x < size) && (nodeType[x] == ATTRIBUTE_NODE)) {
        x++;
      }
  
      return (x - 1);
    }
  }
  
  public int getAttributesAndNSEndIndex(int node) {
    
    //must be alligned with concepts for XSLT 1.1 vars
    if (firstAttr[node] != -1) {
      int x = firstAttr[node];
      while (nextSibling[x] != -1) {
        x = nextSibling[x];
      }
      return x;
    } else {
      int x = node + 1;
  
      while ((x < size) && (nodeType[x] == ATTRIBUTE_NODE || nodeType[x] == NAMESPACE_NODE)) {
        x++;
      }
  
      return (x - 1);
    }
  }

  void resize() {
    resize(capacity + RESIZE_STEP);
  }

  public void trim(){
    name = new QName[DEFAULT_CAPACITY];
    nodeType = new byte[DEFAULT_CAPACITY];
    parent = new int[DEFAULT_CAPACITY];
    firstChild = new int[DEFAULT_CAPACITY];
    previousSibling = new int[DEFAULT_CAPACITY];
    nextSibling = new int[DEFAULT_CAPACITY];
    stringValue = new CharArray[DEFAULT_CAPACITY];
    indexStart = new int[DEFAULT_CAPACITY];
    indexEnd = new int[DEFAULT_CAPACITY];
    documentNodes = new int[DEFAULT_CAPACITY];
    buffer = new CharArray(100000, 100000);
    firstAttr  = new int[DEFAULT_CAPACITY];
    //
    capacity = DEFAULT_CAPACITY;
    RESIZE_STEP = capacity;
  }
      

  
  void resize(int capacity1) {
    QName[] name1 = new QName[capacity1];
    System.arraycopy(name, 0, name1, 0, capacity);
    name = name1;
    //
    byte[] nodeType1 = new byte[capacity1];
    System.arraycopy(nodeType, 0, nodeType1, 0, capacity);
    nodeType = nodeType1;
    //
    int[] parent1 = new int[capacity1];
    System.arraycopy(parent, 0, parent1, 0, capacity);
    parent = parent1;
    //
    int[] firstChild1 = new int[capacity1];
    System.arraycopy(firstChild, 0, firstChild1, 0, capacity);
    firstChild = firstChild1;
    //
    int[] firstAttr1 = new int[capacity1];
    System.arraycopy(firstAttr, 0, firstAttr1, 0, capacity);
    firstAttr = firstAttr1;
    //
    int[] previousSibling1 = new int[capacity1];
    System.arraycopy(previousSibling, 0, previousSibling1, 0, capacity);
    previousSibling = previousSibling1;
    //
    int[] nextSibling1 = new int[capacity1];
    System.arraycopy(nextSibling, 0, nextSibling1, 0, capacity);
    nextSibling = nextSibling1;
    //
    CharArray[] stringValue1 = new CharArray[capacity1];
    System.arraycopy(stringValue, 0, stringValue1, 0, capacity);
    stringValue = stringValue1;
    //
    int[] indexStart1 = new int[capacity1];
    System.arraycopy(indexStart, 0, indexStart1, 0, capacity);
    indexStart = indexStart1;
    //
    int[] indexEnd1 = new int[capacity1];
    System.arraycopy(indexEnd, 0, indexEnd1, 0, capacity);
    indexEnd = indexEnd1;
    //
    capacity = capacity1;
    RESIZE_STEP = capacity / 2;
  }

  public String nodeTypeToString(int nt) {
    String[] s = {"", "ELEM", "ATTR", "TEXT", "CDTA", "EREF", "ENTY", "PROC", "COMM", "DOCU", "DCTP", "DCFR", "NOTN", };
    return s[nt];
  }

  public int getCapacity() {
    return capacity;
  }

  private int[] resizeIntArray(int[] a, int l) {
    int[] b = new int[l + a.length];
    System.arraycopy(a, 0, b, 0, a.length);
    return b;
  }

  public void getStringValue(int index, CharArray ca) {
    int b = indexStart[index];

    if (b != -1) {
      int e = indexEnd[index];
      ca.substring(buffer, b, e);
    } else {
      ca.set(stringValue[index]);
    }
  }

  public void appendStringValue(int index, CharArray ca) {
    int b = indexStart[index];

    if (b != -1) {
      int e = indexEnd[index];
      ca.append(buffer.getData(), b, e - b);
    } else {
      ca.append(stringValue[index]);
    }
  }

  public CharArray getStringValue(int index) {
    int b = indexStart[index];

    if (b != -1) {
      int e = indexEnd[index];

      if (stringValue[index] == null) {
        stringValue[index] = carrPool.getObject().reuse();
      }

      stringValue[index].substring(buffer, b, e);
      indexStart[index] = -1;
      indexEnd[index] = -1;
      return stringValue[index];
    } else {
      return stringValue[index];
    }
  }
/*
  void print() {
    System.err.println("DUMP DTM BEGIN");
    System.err.println("#x %nodeType (0)indexStart (1)indexEnd $stringValue");

    for (int i = 0; i < Math.min(size, 100); i++) {
      System.err.println(" #" + i + ": %" + nodeTypeToString(nodeType[i]) + " (0)" + indexStart[i] + " (1)" + indexEnd[i] + " $\"" + stringValue[i] + "\"");
    } 

    System.err.println("DUMP DTM END");
    System.err.println("DUMP BUFFER BEGIN");
    int w = 50;
    int e = (buffer.length() + w - 1) % w;

    for (int i = 0; i < e - 1; i++) {
      System.err.print(new String(buffer.getData(), i * w, i * w + w));
    } 

    System.err.println(new String(buffer.getData(), (e - 1) * w, buffer.length()));
    System.err.println("DUMP BUFFER END");
  }
*/
  public Document getDocument() {
    initializeDOM();
    return doc;
  }
  public Node getFragment() {
    return frag;
  }
  
  public StringBuffer getLeadingText() {
    return leadingText;
  }
  
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void reinitializeDOM(int[] $excluding) {
    reinitializeDOM($excluding, false, empty, false);
  }
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void reinitializeDOM(int[] $excluding, String[] specialNamespaces) {
    reinitializeDOM($excluding, false, specialNamespaces, false);
  }
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces) {
    reinitializeDOM($excluding, retainParentNamespaces, null, false);
  }
  
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces, String[] specialNamespaces) {
    reinitializeDOM($excluding, retainParentNamespaces, specialNamespaces, false);
  }

  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void reinitializeDOM(int[] $excluding, boolean retainParentNamespaces, String[] specialNamespaces, boolean $visibleOnly) {
    retain = retainParentNamespaces;
    doc = new DocumentImpl();
    domtree = new Node[size];
    dangling = new boolean[size];
    initialized = new boolean[size];
    excluding = $excluding;
    inclusive = !$visibleOnly; 
    specialPrefixes = specialNamespaces;
    int tempIndex = (excluding.length==0)||(excluding[0]>0)?0:1;
    danglingLength = 0;
    for (int i = 1; i < domtree.length; i++) {
//      domtree[i] = subtree(i, null/*retainParentNamespaces, specialNamespaces, visibleOnly*/,null);
      if ((tempIndex>=excluding.length)||(i<excluding[tempIndex])){
// node must be build - it is not in the excluding set
        domtree[i] = subtree(i, null, null, null);
//      node must be added to document fragment        
        dangling[i] = isDangling(i, excluding);
      }
      else {
// node may not be build - it is in the excluding set, only namespace must be dragged if inclusive        
        subtreeEx(i, null, null, null, null);
        tempIndex++;
      }
    } 

    int i = 1;
    tempIndex = (excluding.length==0)||(excluding[0]>0)?0:1;
// flag indicating if i is in the excluding list    
    boolean found = false;
    for (i = 1; i < size; i++) {
      if ((tempIndex>=excluding.length)||(i<excluding[tempIndex])){
        found = false;
      }  else {
        found = true; // i increases every step by 1 - so it will eventually reach excluding[tempIndex]
        tempIndex++;
      }      
      if ((!found)&&(domtree[i]!=null)&&(domtree[i].getNodeType() == Node.TEXT_NODE)) {
// this text node is to be added - it was not found in the excluding list        
        leadingText.append(stringValue[i].getData(),stringValue[i].getOffset(),stringValue[i].getSize());
        dangling[i]=false;
        danglingLength--;
      }

      if (found){
// index found in the excluding list - try next        
        continue;
      }

      break;
    } 
// no root element found! - only leading text
    if (i >= domtree.length) {
      return;
    }

//    doc.appendChild(domtree[i]);
    
    frag = doc.createDocumentFragment();
//    frag.appendChild(do)
    if (danglingLength >= 1) {
      for (int j = i; j < size; j++) {
        if (dangling[j]){
          frag.appendChild(domtree[j]);
        }
      } 
    } else {
      frag = doc;
    }

    domtree[0] = doc;
    domInitialized = true;
  }
  
  public void setDOMInitialized(boolean domInitialized) {
    this.domInitialized = domInitialized;
  }
  
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void initializeDOM() {
    //    System.out.println("DTM.initializeDOM: "+ domInitialized + ", size = " + size);
    if (!domInitialized) {
      doc = new DocumentImpl();
      domtree = new Node[size];
      initialized = new boolean[size];
      excluding = new int[] {};
      specialPrefixes = empty;
      inclusive = true;
      for (int i = 1; i < domtree.length; i++) {
        domtree[i] = subtree(i, null/*false, empty, false*/, null, null);
      } 

      int i = 1;
      for (; i < domtree.length && domtree[i].getNodeType() == Node.TEXT_NODE; i++) {
        ; 
      }
      doc.appendChild(domtree[i]);
      domtree[0] = doc;
      domInitialized = true;
    }
  }
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  private boolean isDangling(int node, int[] excludeList) {
    int father = parent[node];

    if (nodeType[father] != Node.DOCUMENT_NODE) {
      if (Arrays.binarySearch(excludeList, father) >= 0) {
        return isDangling(father, excludeList);
      } else {
        return false;
      }
    } else {
      danglingLength++;
      return true;
    }
  }
  /* removed from parameters - better*/
  private int[] excluding = null;
  private String[] specialPrefixes = null; // better chararray - but not comparable
  private boolean inclusive = true;
  private Hashtable hiddenXML = new Hashtable();
  private Hashtable ownXMLAttribs = new Hashtable();
  
  boolean retain = true;
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */  
  private Node subtree(int index, Hashtable namespaces, Hashtable usedNamespaces, Hashtable xmlAttributes) {
    if (initialized[index]) {
      return domtree[index];
    }

    Node n;
    switch (nodeType[index]) {
      case Node.TEXT_NODE: {
        n = doc.createTextNode(getStringValue(index).toString());
        break;
      }
      case Node.ATTRIBUTE_NODE: {
        n = doc.createAttributeNS(name[index].uri.toString(), name[index].rawname.toString());
        ((AttrImpl) n).setValue(getStringValue(index).toString());
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        n = doc.createProcessingInstruction(name[index].rawname.toString(), getStringValue(index).toString());
        break;  
      }
      case NAMESPACE_NODE: {
        n = null;
        break;
      }
      case Node.COMMENT_NODE: {
        n = doc.createComment(getStringValue(index).toString());
        break;
      }
      case Node.CDATA_SECTION_NODE: {
        n = doc.createCDATASection(getStringValue(index).toString());
        break;
      }
      case Node.DOCUMENT_NODE: {
        DocumentImpl newDoc = new DocumentImpl();
        n = newDoc;
        DocumentImpl oldDoc = this.doc;
        this.doc = newDoc;
        try {
          int child = firstChild[index];
          while (child != DTM.NONE) {
            Node subtree = subtree(child, namespaces, null, xmlAttributes); 
            //n.appendChild(subtree); //Do not append elements due to the variable may contain several child nodes without a root element
            child = nextSibling[child];
          }         
        } finally {
          this.doc = oldDoc;
        }
        break;
      }  
      case Node.ELEMENT_NODE: {
        boolean notCloned1 = true;
        hiddenXML.clear();
        ownXMLAttribs.clear();
// namespaces which are used in this node
// only namespaces which are used in this node and not defined in the output ancesstors or are overriden are
// set in the namespace axis
        usedNamespaces = usedNamespaces==null?new Hashtable():usedNamespaces;
        n = doc.createElementNS(name[index].uri.toString(), name[index].rawname.toString());
        CharArray prefix = name[index].prefix;
        usedNamespaces.put(((prefix==null)||(prefix.length()==0))?SignatureContext.DEFAULT:prefix, name[index].uri==null?CharArray.EMPTY:name[index].uri);
        int x = index + 1;
        int i2 = Arrays.binarySearch(excluding, x); 
        if (i2<0){i2=~i2;}
// i2 - first not excluded node index in the excluding list
        while ((x < size)) {
        
          if ((i2<excluding.length)&&(excluding[i2]<x)){
            i2++;
          } else if ((i2<excluding.length)&&(excluding[i2]==x)){
            // do not build excluded elements 
            //TODO:fix for xml namespace elements
            QName q1 = name[x];
            if ((nodeType[x] == Node.ATTRIBUTE_NODE)&&SignatureContext.XML.equals(q1.prefix)&&inclusive) {
              Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
              ai.setValue(getStringValue(x).toString());
              if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                if (notCloned1){
                  if (xmlAttributes==null){
                    xmlAttributes = new Hashtable();
                  } else {
                    xmlAttributes = (Hashtable) xmlAttributes.clone();
                  }
                  notCloned1 = false;
                }
                xmlAttributes.put(q1.localname, ai);
              }     
              hiddenXML.put(q1.localname, ai);
            }
            domtree[x]=null;
            initialized[x]=true;
            i2++;
            x++;
          } else {
              if (nodeType[x] != Node.ATTRIBUTE_NODE) {
                if (nodeType[x] == NAMESPACE_NODE) {
// if this is inclusive canonicalization or this namespace is in inclusive namespaces
                  CharArray tempPrefix = name[x].localname;
                  if ((tempPrefix==null)||(tempPrefix.length()==0)){
                    tempPrefix = SignatureContext.DEFAULT;
                  }
                  if (retain||inclusive|| ((specialPrefixes!=null)&& (Arrays.binarySearch(specialPrefixes, tempPrefix.toString()) >= 0))) {                  
                    usedNamespaces.put(tempPrefix, stringValue[x]);
                  }
                } else if (nodeType[x]== Node.COMMENT_NODE){
// creates comments in the element, such as <foo <!-- comment --> />                  
                  Comment comment = doc.createComment(getStringValue(index).toString());
                  ((Element) n).appendChild(comment);
                  domtree[x] = comment;
                } else {
// only attribute nodes belonging to this parent are searched              
                  break;
                }
            } else {
              QName q1 = name[x];
//              if (q1.localname.equals("xmlns")){
////TODO: Never used
//// if this is inclusive canonicalization or this namespace is in inclusive namespaces                                  
//                if (inclusive|| ((specialPrefixes!=null)&& (Arrays.binarySearch(specialPrefixes, "#default") >= 0))) {                  
//                  usedNamespaces.put(DEFAULT, stringValue[x]);
//                }                
//              } else
              if (q1.uri != null) {

                Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
                ai.setValue(getStringValue(x).toString());
//TODO: uri comparison!                
                if (SignatureContext.XML.equals(q1.prefix)&&inclusive){
                  if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                    if (notCloned1){
                      if (xmlAttributes==null){
                        xmlAttributes = new Hashtable();
                      } else {
                        xmlAttributes = (Hashtable) xmlAttributes.clone();
                      }
                      notCloned1 = false;
                    }
                    xmlAttributes.put(q1.localname, ai);
                    ownXMLAttribs.put(q1.localname, ai);
                  }               
                } else {
                  ((Element) n).setAttributeNodeNS(ai);
                }
                domtree[x]=ai;
                if (((q1.prefix!=null) && (q1.prefix.length()!=0))){
                  usedNamespaces.put(q1.prefix, q1.uri);
                }
              } else {                
                Attr ai = doc.createAttribute(q1.rawname.toString());
                ai.setValue(getStringValue(x).toString());
                ((Element) n).setAttributeNode(ai);
                domtree[x] = ai;
//                usedNamespaces.put(DEFAULT, CharArray.EMPTY);
              }
            }
                  initialized[x] = true;
                  x++;              
          }
        }
        
        Enumeration newNamespaces = usedNamespaces.keys();
//namespace addition
        while (newNamespaces.hasMoreElements()) {
          CharArray nextPrefix = (CharArray) newNamespaces.nextElement();
          CharArray nextValue = namespaces==null?null:(CharArray) namespaces.get(nextPrefix);
          CharArray currentValue = (CharArray) usedNamespaces.get(nextPrefix);
          if(!currentValue.equals(nextValue)){
            Attr at = SignatureContext.DEFAULT.equals(nextPrefix)?doc.createAttribute("xmlns"):doc.createAttribute("xmlns:".concat(nextPrefix.toString()));
            at.setValue(currentValue.toString());
            ((Element) n).setAttributeNode(at);
          }
        }

//      adding attributes from xml namespace        
        if (xmlAttributes!=null){
            if (domtree[parent[index]]==null){ // check excluded list
              Enumeration enum1 = xmlAttributes.keys();
              while(enum1.hasMoreElements()){
                CharArray key = (CharArray) enum1.nextElement();
                if (!hiddenXML.containsKey(key)){
                    Attr atr = (Attr) ((Attr) xmlAttributes.get(key)).cloneNode(true);
                    ((Element) n).setAttributeNodeNS(atr);
                }
              }
            } else {
              Enumeration enum1 = ownXMLAttribs.keys();
              while(enum1.hasMoreElements()){
                CharArray key = (CharArray) enum1.nextElement();
                Attr atr = (Attr) ((Attr) ownXMLAttribs.get(key)).cloneNode(true);
                ((Element) n).setAttributeNodeNS(atr);
               }
            }
        }
        
// overriding namespaces declaration for children - may optimize if they are the same!        
        if (namespaces != null){
          Enumeration enum1 = namespaces.keys();
          while (enum1.hasMoreElements()){
            Object key = enum1.nextElement();
            if (usedNamespaces.get(key)==null){
              usedNamespaces.put(key, namespaces.get(key));
            }
          }
        }
        
        namespaces = usedNamespaces;
        int i1 = firstChild[index];
        domtree[index] = n;
        while (i1 != DTM.NONE) {
          if (Arrays.binarySearch(excluding, i1) < 0) {
            n.appendChild(subtree(i1, namespaces, null, xmlAttributes));
          } else {
            subtreeEx(i1, namespaces, n,null, xmlAttributes);
          }
          i1 = nextSibling[i1];
        }         
        break;
      }
        
      default: {
// unknown node type          
        return (Node) (doc.createTextNode(""));
        
      }
    }
    
    domtree[index] = n;
    initialized[index] = true;
    return n;
  }
  
  /**
   * @deprecated use {@link DTMDOMBuilder}
   */
  public void subtreeEx(int index, Hashtable namespaces, Node outputAncesstor, Hashtable usedNamespaces, Hashtable xmlAttributes){
    if (nodeType[index] == Node.ELEMENT_NODE){
// drag parent namespaces in inclusive canonicalization      
      if (inclusive) {
        boolean notCloned = true;
        boolean notCloned1 = true;
        int x = index + 1;
        while ((x < size)) {
          
            if (nodeType[x] == NAMESPACE_NODE) {
              CharArray tempPrefix = name[x].localname;
              if ((tempPrefix==null)||(tempPrefix.length()==0)){
                tempPrefix = SignatureContext.DEFAULT;
              }
// this namespace declaration is not in output ancestors' namespace axis              
              if (((namespaces==null)||(!stringValue[x].equals(namespaces.get(tempPrefix))))&&
                  ((usedNamespaces==null)||(!stringValue[x].equals(usedNamespaces.get(tempPrefix))))){
                if (notCloned){
                  if (usedNamespaces==null){
                    usedNamespaces = new Hashtable();
                  } else {
                    usedNamespaces = (Hashtable) usedNamespaces.clone();
                  }
                  notCloned = false;
                }
                usedNamespaces.put(tempPrefix, stringValue[x]);  
              }
              domtree[x] = null;
            } else if (nodeType[x] == Node.ATTRIBUTE_NODE){
            QName q1 = name[x];
            if (q1.localname.equals("xmlns")) {
// TODO: never used!              
              if (((namespaces==null)||(!stringValue[x].equals(namespaces.get(SignatureContext.DEFAULT))))&&
                  ((usedNamespaces==null)||(!stringValue[x].equals(usedNamespaces.get(SignatureContext.DEFAULT))))){
                if (notCloned){
                  if (usedNamespaces==null){
                    usedNamespaces = new Hashtable();
                  } else {
                    usedNamespaces = (Hashtable) usedNamespaces.clone();
                  }
                  notCloned = false;
                }
                usedNamespaces.put(SignatureContext.DEFAULT, stringValue[x]);  
              }
              domtree[x] = null;
            }
            
            if (SignatureContext.XML.equals(q1.prefix)){
              if ((xmlAttributes==null)||(xmlAttributes.get(q1.localname)==null)||(!stringValue[x].equals(((Attr) xmlAttributes.get(q1.localname)).getNodeValue()))){
                if (notCloned1){
                  if (xmlAttributes==null){
                    xmlAttributes = new Hashtable();
                  } else {
                    xmlAttributes = (Hashtable) xmlAttributes.clone();
                  }
                  notCloned1 = false;
                }
                Attr ai = doc.createAttributeNS(q1.uri.toString(), q1.rawname.toString());
                ai.setValue(getStringValue(x).toString());                
                xmlAttributes.put(q1.localname, ai);
                domtree[x] = ai;
              }               
            }
//          TODO: add attributes from xml namespace            
          } else if (nodeType[x] != Node.COMMENT_NODE){
            break;  
          }
          
          initialized[x++]=true;
        }
      } else if (retain) {
        boolean notCloned = true;
        int x = index + 1;
        while ((x < size)) {
          
            if (nodeType[x] == NAMESPACE_NODE) {
              CharArray tempPrefix = name[x].localname;
              if ((tempPrefix==null)||(tempPrefix.length()==0)){
                tempPrefix = SignatureContext.DEFAULT;
              }
// this namespace declaration is not in output ancestors' namespace axis              
              if (((namespaces==null)||(!stringValue[x].equals(namespaces.get(tempPrefix))))&&
                  ((usedNamespaces==null)||(!stringValue[x].equals(usedNamespaces.get(tempPrefix))))){
                if (notCloned){
                  if (usedNamespaces==null){
                    usedNamespaces = new Hashtable();
                  } else {
                    usedNamespaces = (Hashtable) usedNamespaces.clone();
                  }
                  notCloned = false;
                }
                usedNamespaces.put(tempPrefix, stringValue[x]);  
              }
              domtree[x] = null;
            } else if (nodeType[x] == Node.ATTRIBUTE_NODE){
            QName q1 = name[x];
            if (q1.localname.equals("xmlns")) {
// TODO: never used!              
              if (((namespaces==null)||(!stringValue[x].equals(namespaces.get(SignatureContext.DEFAULT))))&&
                  ((usedNamespaces==null)||(!stringValue[x].equals(usedNamespaces.get(SignatureContext.DEFAULT))))){
                if (notCloned){
                  if (usedNamespaces==null){
                    usedNamespaces = new Hashtable();
                  } else {
                    usedNamespaces = (Hashtable) usedNamespaces.clone();
                  }
                  notCloned = false;
                }
                usedNamespaces.put(SignatureContext.DEFAULT, stringValue[x]);  
              }
              domtree[x] = null;
            }
          } else if (nodeType[x] != Node.COMMENT_NODE){
            break;  
          }
          
          initialized[x++]=true;
        }
      }
      
      
      int i1 = firstChild[index];
  //TODO: see fix!
  // xml: attribs handling      
      while (i1 != DTM.NONE) {
        if (Arrays.binarySearch(excluding, i1) < 0) {
          if (outputAncesstor!=null){
            outputAncesstor.appendChild(subtree(i1, namespaces, usedNamespaces, xmlAttributes));
          } else {
            subtree(i1, namespaces, usedNamespaces, xmlAttributes);
          }
        } else {
          subtreeEx(i1, namespaces, outputAncesstor, usedNamespaces, xmlAttributes);
        }
        i1 = nextSibling[i1];
      }         
    }
    domtree[index] = null;
    initialized[index] = true;
  }

  public int getDocumentElement(int node) {    
    for (int i = documentNodesCount - 1; i >= 0; i--) {
      int docNode = documentNodes[i];
      if (docNode <= node) {
        return docNode;
      }
    }
    return 0;    
  }
  
  
  
}

