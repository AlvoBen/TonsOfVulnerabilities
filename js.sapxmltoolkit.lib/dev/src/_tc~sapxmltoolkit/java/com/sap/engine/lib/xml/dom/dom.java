package com.sap.engine.lib.xml.dom;

import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xml.util.NS;

/**
 * <pre>
 *
 * I recommend using this class.
 *
 * Contains utility methods working on DOM.
 * All of them are implementation-independent! (there are no 'typecasts to impl')
 *
 *
 * 'toXPath' functionality
 *     The toXPath method is capable of producing a 'canonical' XPath expression
 *     out of a DOM Node. If the node (or one of its ancestors) has siblings,
 *     an XPath predicate is appended where necessary (e.g. "/a/b[2]/c[5]/text()[7]")
 *
 *
 * 'toNode' functionality
 *     It's the opposite of the 'toXPath' functionality
 *     The toNode method accepts only a restricted set of XPath expressions
 *     The restricted grammar is:
 *
 *       expr     ::=  step?  ('/' step)*
 *       step     ::=  nodetest ('[' int ']')?
 *       nodetest ::=   customQName  |  ('@' customQName)  |  'text()'  |  'comment()'
 *                     |  ( 'processing-instruction(' quotedLiteral? ')' )
 *                     |  ( 'pi(' quotedLiteral ')' )
 *       quotedLiteral ::=  ('"' string '"') | ("'" string "'")
 *       customQName   ::=  (prefix ':')?  NCName
 *       prefix        ::=  NCName  |  ('{' anyURI '}')
 *
 *      Predicates are applied locally for each step, e.g. if the xml is
 *        <a> <b><c/><c/></b> <b><c/><c/></b> </a>
 *      then
 *        DOM.xpathToNodeList(document, "/a/b/c[1]")
 *      will contain two nodes - those with xpaths /a/b[1]/c[1] and /a/b[2]/c[1]
 *
 * 'ns resolution' functionality
 *      Alleviates resolving a QName's parts:
 *      prefix, local name, and the URI mapped to the prefix.
 *      You have to provide the scope for the resolution, i.e. a Node
 *
 * </pre>
 *
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 19-Mar-02, 13:58:54
 */
public final class DOM {

  private DOM() {

  }

  /*
   'toXPath' methods
   */
  private static final boolean USE_URIS_IN_CURLY_BRACKETS_INSTEAD_OF_PREFIXES = false;

  /**
   * Doesn't return null, and does't throw exceptions, but may return ""
   *
   * @returns the 'canonical XPath' of the node
   */
  public static String toXPath(Node node) {
    return toXPath(new StringBuffer(), node).toString();
  }

  public static String toXPath(NodeList list) {
    return toXPath(new StringBuffer("{ "), list, ", ").append(" }").toString();
  }

  public static String toXPathWithLocation(Node node) {
    return toXPathWithLocation(new StringBuffer(), node).toString();
  }

  public static StringBuffer toXPathWithLocation(StringBuffer buffer, Node node) {
    if (node instanceof Base) {
      DocumentImpl document = (DocumentImpl) node.getOwnerDocument();

      if (document != null) {
        buffer.append("document(\"").append(document.getLocation().toExternalForm()).append("\")");
      }
    }

    toXPath(buffer, node);
    return buffer;
  }

  public static StringBuffer toXPath(StringBuffer buffer, NodeList list, String separator) {
    if (list == null) {
      return buffer;
    }

    if (separator == null) {
      separator = ", ";
    }

    int nList = list.getLength();

    if (nList == 0) {
      return buffer;
    }

    toXPath(buffer, list.item(0));

    for (int i = 1; i < nList; i++) {
      buffer.append(separator);
      toXPath(buffer, list.item(i));
    } 

    return buffer;
  }

  public static StringBuffer toXPath(StringBuffer buffer, Node node) {
    if (node == null) {
      return buffer;
    }

    if (node.getNodeType() == Node.DOCUMENT_NODE) {
      return buffer.append('/');
    } else {
      return toXPath0(buffer, node);
    }
  }

  private static StringBuffer toXPath0(StringBuffer buffer, Node node) {
    if (node == null) {
      return buffer;
    }

    int t = node.getNodeType();
//    String name;

    switch (t) {
      case Node.ELEMENT_NODE: {
        toXPath0(buffer, node.getParentNode());
        buffer.append('/');
        appendQName(buffer, node);
//        name = node.getNodeName();
        break;
      }
      case Node.ATTRIBUTE_NODE: {
        toXPath0(buffer, ((Attr) node).getOwnerElement());
        buffer.append("/@");
        appendQName(buffer, node);
        break;
      }
      case Node.CDATA_SECTION_NODE:
      case Node.ENTITY_REFERENCE_NODE:
      case Node.TEXT_NODE: {
        toXPath0(buffer, node.getParentNode());
        buffer.append("/text()");
        break;
      }
      case Node.COMMENT_NODE: {
        toXPath0(buffer, node.getParentNode());
        buffer.append("/comment()");
//        name = node.getNodeName();
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        toXPath0(buffer, node.getParentNode());
        ProcessingInstruction pi = (ProcessingInstruction) node;
        String target = pi.getTarget();

        if (target != null) {
          buffer.append("/processing-instruction('").append(target).append("')");
        } else {
          buffer.append("/processing-instruction()");
        }

        break;
      }
      default: {
        break;
      }
    }

    int cntLeft = 0;

    for (Node x = node.getPreviousSibling(); x != null; x = x.getPreviousSibling()) {
      if (areEquivalent(node, x)) {
        cntLeft++;
      }
    } 

    int cntRight = 0;

    for (Node x = node.getNextSibling(); x != null; x = x.getNextSibling()) {
      if (areEquivalent(node, x)) {
        cntRight++;
      }
    } 

    int cnt = cntLeft + 1 + cntRight;

    if (cnt > 1) {
      buffer.append('[').append(cntLeft + 1).append(']');
    }

    return buffer;
  }

  // internal for the 'toXPath' methods
  public static boolean areEquivalent(Node a, Node b) {
    int ta = a.getNodeType();

    if (b.getNodeType() != ta) {
      return false;
    }

    switch (ta) {
      case Node.ELEMENT_NODE: {
        return (compare(a, b));
      }
      case Node.ATTRIBUTE_NODE: {
      	if(compare(a, b)) {
      		return(a.getNodeValue().equals(b.getNodeValue()));
      	}
      	return (false);
      }
      case Node.PROCESSING_INSTRUCTION_NODE: {
        String da = ((ProcessingInstruction) a).getData();
        String db = ((ProcessingInstruction) a).getData();
        return da.equals(db);
      }
      case Node.COMMENT_NODE:
      case Node.TEXT_NODE: {
        return true;
      }
      default: {
        return true;
      }
    }
  }
  
  private static boolean compare(Node srcNode, Node dstNode) {
    String aUri = srcNode.getNamespaceURI();
    aUri = (aUri == null) ? "" : aUri;
    String aLocal = srcNode.getLocalName();
    String bUri = dstNode.getNamespaceURI();
    bUri = (bUri == null) ? "" : bUri;
    String bLocal = dstNode.getLocalName();
    return (aUri.equals(bUri) && aLocal.equals(bLocal));
  }

  private static StringBuffer appendQName(StringBuffer buffer, Node node) {
    if (USE_URIS_IN_CURLY_BRACKETS_INSTEAD_OF_PREFIXES) {
      String uri = node.getNamespaceURI();
      String local = node.getLocalName();

      if ((uri == null) || uri.equals("")) {
        return buffer.append(local);
      }

      return buffer.append('{').append(uri).append("}:").append(local);
    } else {
      return buffer.append(node.getNodeName());
    }
  }

  /*
   'toNode' functionality
   */
  /**
   * Computes simple XPath expressions from a given context (see the
   * javadoc for the whole class for details).
   *
   * Doesn't throw exceptions;
   *
   * @returns a Node, or null
   */
  public static Node toNode(String xpath, Node scope) {
    if (xpath == null) {
      return null;
    }

    if (scope == null) {
      return null;
    }

    xpath = xpath.trim();

    if (xpath.equals("/") || xpath.equals("")) {
    	Node r = (scope.getNodeType() == Node.DOCUMENT_NODE) ? scope : scope.getOwnerDocument();
    	return r;
    }

    // First of all, identify the last step
    // Change the base by processing recursively
    // everything to the left of the last step
    int indexOfSlash = xpath.lastIndexOf('/');
    String step;

    if (indexOfSlash == -1) {
      step = xpath;
    } else {
      scope = toNode(xpath.substring(0, indexOfSlash), scope);

      if (scope == null) {
        return null;
      }

      step = xpath.substring(indexOfSlash + 1, xpath.length()).trim();
    }

    // Identify the nodeTest and predicate
    int indexOfBracket = step.lastIndexOf('[');
    int predicate = 1; // the number enclosed in '[' and ']'
    String nodeTest;

    if (indexOfBracket != -1) {
      int indexOfClosingBracket = step.indexOf(']', indexOfBracket + 1);

      if (indexOfClosingBracket != -1) {
        String s = step.substring(indexOfBracket + 1, indexOfClosingBracket).trim();
        try {
          predicate = Integer.parseInt(s);
        } catch (NumberFormatException e) {
          //$JL-EXC$
        }
      }

      nodeTest = step.substring(0, indexOfBracket).trim();
    } else {
      nodeTest = step;
    }

    // Find the respective node and return it
    if (nodeTest.charAt(0) == '@') {
      if (scope.getNodeType() != Node.ELEMENT_NODE) {
        return null;
      }

      nodeTest = nodeTest.substring(1);
      String uri = qnameToURI(nodeTest, scope);
      String local = qnameToLocalName(nodeTest);

      if (uri.equals(prefixToURI("", scope))) {
        uri = null;
      }

      uri = ((uri != null) && (uri.length() == 0)) ? null : uri;
      Attr a = ((Element) scope).getAttributeNodeNS(uri, local);
      return a;
    }

    NodeList children = scope.getChildNodes();

    if (children == null) {
      return null;
    }

    int nChildren = children.getLength();

    if (nChildren == 0) {
      return null;
    }

    if (nodeTest.equals("text()")) {
      int cnt = 0;

      for (int i = 0; i < nChildren; i++) {
        if (children.item(i).getNodeType() == Node.TEXT_NODE) {
          cnt++;

          if (cnt == predicate) {
            return children.item(i);
          }
        }
      } 

      return null;
    } else if (nodeTest.equals("comment()")) {
      int cnt = 0;

      for (int i = 0; i < nChildren; i++) {
        if (children.item(i).getNodeType() == Node.COMMENT_NODE) {
          cnt++;

          if (cnt == predicate) {
            return children.item(i);
          }
        }
      } 

      return null;
    } else if (nodeTest.startsWith("processing-instruction(") || nodeTest.startsWith("pi(")) {
      int indexOfOpeningBracket = nodeTest.indexOf('('); // guaranteed not to be -1
      int indexOfClosingBracket = nodeTest.lastIndexOf(')');

      if (indexOfClosingBracket == -1) {
        indexOfClosingBracket = nodeTest.length();
      }

      String piName = nodeTest.substring(indexOfOpeningBracket + 1, indexOfClosingBracket).trim();

      if (piName.equals("")) {
        piName = null;
      } else {
        char ch = piName.charAt(0);

        if ((ch == '\'') || (ch == '"')) {
          piName = piName.substring(1);
        }

        if (piName.length() > 0) {
          ch = piName.charAt(piName.length() - 1);

          if ((ch == '\'') || (ch == '"')) {
            piName = piName.substring(0, piName.length() - 1);
          }
        }
      }

      int cnt = 0;

      for (int i = 0; i < nChildren; i++) {
        if (children.item(i).getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
          ProcessingInstruction x = (ProcessingInstruction) children.item(i);

          if ((piName == null) || piName.equals(x.getTarget())) {
            cnt++;

            if (cnt == predicate) {
              return x;
            }
          }
        }
      } 

      return null;
    } else {
      String uri = qnameToURI(nodeTest, scope);
      String local = qnameToLocalName(nodeTest);
      uri = (uri == null) ? "" : uri;
      int cnt = 0;

      for (int i = 0; i < nChildren; i++) {
        if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Element x = (Element) children.item(i);
          String xUri = x.getNamespaceURI();
          xUri = (xUri == null) ? "" : xUri;
          String xLocal = x.getLocalName();

          if (uri.equals(xUri) && local.equals(xLocal)) {
            cnt++;

            if (cnt == predicate) {
              return x;
            }
          }
        }
      } 

      return null;
    }
  }

  /*
   namespace resolving functionality
   */
  /**
   * If the prefix is successfully resolved,
   *           returns a URI (probably the empty String, but not null).
   * If the prefix was not successfully resolved returns null.
   *
   * Treats prefix "" and prefix null as equivalent.
   *
   * Supports the default mappings:
   *   "" -> ""
   *   "xml" -> NS.XML
   *   "xmlns" -> NS.XMLNS
   */
  public static String prefixToURI(String prefix, Node scope) {
    if (prefix == null) {
      prefix = "";
    }

    if (prefix.equals("xml")) {
      return NS.XML;
    } else if (prefix.equals("xmlns")) {
      return NS.XMLNS;
    }

    boolean isDefaultNSReached = false;

    if(scope instanceof Document){
    	scope = ((Document)scope).getDocumentElement();
    }

    while (scope instanceof Element) {
      NamedNodeMap attrs = scope.getAttributes();
      int nAttrs = attrs.getLength();

      for (int i = 0; i < nAttrs; i++) {
        Node attr = attrs.item(i);
        String qname = attr.getNodeName();
        String value = attr.getNodeValue();

        if (!isDefaultNSReached && qname.equals("xmlns")) {
          isDefaultNSReached = true;

          if (value.length() != 0 && prefix.equals("")) {
            return value;
          }
        } else if (qname.startsWith("xmlns:")) {
          String tempprefix = qname.substring(6);

          if (prefix.equals(tempprefix)) {
            return value;
          }
        } else if (attr.getNamespaceURI() != null) {
          if (prefix.equals(attr.getPrefix())) {
            return attr.getNamespaceURI();
          }
        }
      } 

      if (scope.getNamespaceURI() != null) {
        if (prefix.equals(scope.getPrefix())) {
          return scope.getNamespaceURI();
        }
      }

      scope = scope.getParentNode();
    }
    if (prefix.length() == 0) {
      return "";
    }    
    return null;
  }

  /**
   * If the prefix is successfully resolved, returns an Attr (whose qname is either "xmlns", or "xmlns:" + prefix).
   * If the prefix was not successfully resolved returns null.
   *
   * Treats prefix "" and prefix null as equivalent.
   */
  public static Attr prefixToAttr(String prefix, Node scope) {
    if ((prefix == null) || (prefix.length() == 0)) {
      while (scope != null) {
        NamedNodeMap map = scope.getAttributes();

        if (map != null) {
          int nMap = map.getLength();

          for (int i = 0; i < nMap; i++) {
            Attr a = (Attr) map.item(i);

            if (a.getNodeName().equals("xmlns")) {
              return a;
            }
          } 
        }

        scope = scope.getParentNode();
      }

      return null;
    } else {
      while (scope != null) {
        NamedNodeMap map = scope.getAttributes();

        if (map != null) {
          int nMap = map.getLength();

          for (int i = 0; i < nMap; i++) {
            Attr a = (Attr) map.item(i);
            String aPrefix = a.getPrefix();

            if ((aPrefix != null) && aPrefix.equals("xmlns") && a.getLocalName().equals(prefix)) {
              return a;
            }
          } 
        }

        scope = scope.getParentNode();
      }

      return null;
    }
  }

  /**
   * Resolves the prefix of the qname and returns the uri as if by a call to
   * prefixToURI(String prefix, Node scope).
   * If scope is null, returns null.
   *
   * Accepts also qnames like this:
   *    {http://www.sap.com/}:abc
   * which is equivalent to
   *    inqmy:abc    with the ns mapping   xmlns:inqmy='http://www.sap.com/'
   *
   * Note: the qname "xmlns" should be mapped to
   * NS.XMLNS in the spirit of the DOM spec.
   * This method does not do that.
   *
   * @throws NullPointerException if qname is null
   */
  public static String qnameToURI(String qname, Node scope) {
    if (qname.charAt(0) == '{') {
      int index = qname.indexOf('}');
      return qname.substring(1, index);
    } else {
      return prefixToURI(qnameToPrefix(qname), scope);
    }
  }

  /**
   * @returns the local name part of the qname
   *
   * @throws NullPointerException if the qname is null
   */
  public static String qnameToLocalName(String qname) {
    int p = qname.lastIndexOf(':'); // I know that  QName ::= NCName ':' NCName,

    // but I allow  QName ::= '{' URI '}:' NCName   here
    // so I use lastIndexOf
    if (p == -1) {
      return qname;
    }

    return qname.substring(p + 1); // this would return "" if the last char of qname is ':'
  }

  /**
   * Note: For qnames matching '{' URI '}:' NCName, returns everything before the last ':' !
   * Be careful not to pass things like "{http://www.sap.com}abc"
   * because in such a case "{http" will be returned
   *
   * @returns the prefix part if present, otherwise null
   * @throws NullPointerException if qname is null
   */
  public static String qnameToPrefix(String qname) {
    int p = qname.lastIndexOf(':');

    if (p == -1) {
      return "";
    }

    return qname.substring(qname.startsWith("@") ? 1 : 0, p);
  }

//  /*
//   Other functionality
//   */
//  /**
//   * This method is implementation-independent too.
//   *
//   * @returns a NodeList containing only the Element children of x;
//   *          if x is null, or has no child elements, returns an empty NodeList
//   */
//  public static ElementOnlyNodeListImpl getChildElements(Node x) {
//    return new ElementOnlyNodeListImpl((x == null) ? null : x.getChildNodes());
//  }

  /**
   * This is more effective than toElementArray(getChildElements(x)),
   * but less effective than iterating over x.getChildNodes() with a node type checking.
   *
   * Doesn't throw exceptions
   *
   * @returns an array, exactly as long as the number of children of x;
   *          returns an empty array if x is null
   */
  public static Element[] getChildElementsAsArray(Node x) {
    if (x == null) {
      return new Element[0];
    }

    NodeList children = x.getChildNodes();

    if (children == null) {
      return new Element[0];
    }

    int nChildren = children.getLength();
    Element[] r0 = new Element[nChildren];
    int nr0 = 0;

    for (int i = 0; i < nChildren; i++) {
      Node y = children.item(i);

      if (y.getNodeType() == Node.ELEMENT_NODE) {
        r0[nr0] = (Element) y;
        nr0++;
      }
    } 

    if (nr0 == nChildren) {
      return r0;
    }

    Element[] r = new Element[nr0];
    System.arraycopy(r0, 0, r, 0, nr0);
    return r;
  }

  /**
   *
   */
  public static Node[] toNodeArray(NodeList list) {
    if (list == null) {
      return new Node[0];
    }

    int nList = list.getLength();
    Node[] r = new Node[nList];

    for (int i = 0; i < nList; i++) {
      r[i] = list.item(i);
    } 

    return r;
  }

  /**
   * @throws ClassCastException if the list doesn't contain only Elements
   */
  public static Element[] toElementArray(NodeList list) {
    if (list == null) {
      return new Element[0];
    }

    int nList = list.getLength();
    Element[] r = new Element[nList];

    for (int i = 0; i < nList; i++) {
      r[i] = (Element) list.item(i);
    } 

    return r;
  }

  public static URL getLocation(Node node) {
    if (node == null) {
      return null;
    }

    Document d = (node.getNodeType() == Node.DOCUMENT_NODE) ? ((Document) node) : node.getOwnerDocument();

    if (!(d instanceof DocumentImpl)) {
      return null;
    }

    return ((DocumentImpl) d).getLocation();
  }

  public static Node parse(String location) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(location);
  }

  public static Node parse(InputStream in) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(in);
  }

  public static Node parseXMLFromAString(String xml) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(new InputSource(new StringReader(xml)));
  }

  public static Hashtable getNamespaceMappingsInScope(Node node) {
    //org.w3c.dom.Node node = this; 
    Hashtable h = new Hashtable();
    boolean isDefaultNSReached = false;

    while (node instanceof Element) {
      NamedNodeMap attrs = node.getAttributes();
      int nAttrs = attrs.getLength();

      for (int i = 0; i < nAttrs; i++) {
        Node attr = attrs.item(i);
        String qname = attr.getNodeName();
        String value = attr.getNodeValue();

        if (!isDefaultNSReached && qname.equals("xmlns")) {
          isDefaultNSReached = true;

          if (value.length() != 0) {
            h.put("", value);
          }
        } else if (qname.startsWith("xmlns:")) {
          String prefix = qname.substring(6);

          if (!h.containsKey(prefix)) {
            h.put(prefix, value);
          }
        } else if (attr.getNamespaceURI() != null) {
          String prefix = attr.getPrefix();

          if (prefix!= null && prefix.length() != 0 && !h.containsKey(prefix)) {
            h.put(prefix, attr.getNamespaceURI());
          }
        }
      } 

      if (node.getNamespaceURI() != null) {
        String prefix = node.getPrefix();

        if (prefix != null && prefix.length() != 0 && !h.containsKey(prefix)) {
          h.put(prefix, node.getNamespaceURI());
        }
      }

      node = node.getParentNode();
    }

    return h;
  }

  /**
   * Useful when namespace declarations have been stripped for some reason
   */
  public static Hashtable getNamespaceMappingsInScopeSpecial(Node node) {
    //    LogWriter.getSystemLogWriter().println("<DOM> " + node);
    Hashtable h = new Hashtable();
    boolean isDefaultNSReached = false;
//    int count = 0;

    while (node instanceof Element) {
      //      LogWriter.getSystemLogWriter().println("<DOM> pass" + count ++ + "... " + h);
      NamedNodeMap attrs = node.getAttributes();
      int nAttrs = attrs.getLength();

      for (int i = 0; i < nAttrs; i++) {
        Node attr = attrs.item(i);
        String qname = attr.getNodeName();
        String value = attr.getNodeValue();

        //        LogWriter.getSystemLogWriter().println("<DOM>" +  qname);    
        if (!isDefaultNSReached && qname.equals("xmlns")) {
          isDefaultNSReached = true;

          if (value.length() != 0) {
            h.put("", value);
          }
        } else if (qname.startsWith("xmlns:")) {
          String prefix = qname.substring(6);

          if (!h.containsKey(prefix)) {
            h.put(prefix, value);
          }
        }
      } 

      String prefix = node.getPrefix();

      if (prefix != null && !h.containsKey(prefix)) {
        h.put(prefix, node.getNamespaceURI());
      }

      node = node.getParentNode();
    }

    //    LogWriter.getSystemLogWriter().println("<DOM> Returning: " + h );
    //    LogWriter.getSystemLogWriter().println("<DOM> Node has become: " + node );
    return h;
  }

  public static Element getElementByAttribute(Node n, String localName, String uri, String value) {
    NodeList nl = n.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      Node temp = nl.item(i);

      if ((temp instanceof Element) && hasAttributeFlat((Element) temp, localName, uri, value)) {
        return (Element) temp;
      } else {
        Element eTemp;

        if ((eTemp = getElementByAttribute(temp, localName, uri, value)) != null) {
          return eTemp;
        } else {
          continue;
        }
      }
    } 

    return null;
  }

  private static boolean hasAttributeFlat(Element el, String localName, String uri, String value) {
    if (el.hasAttributeNS(uri, localName)) {
      String val = el.getAttributeNS(uri, localName);
      return value.equals(val) ? true : false;
    } else {
      return value == null;
    }
  }

  public static final int NSA_UNKNOWN = -1;
  public static final int NSA_FALSE = 0;
  public static final int NSA_TRUE = 1;

  public static int getNamespaceAwareness(Node node) {
    if (node == null) {
      return DOM.NSA_UNKNOWN;
    }

    Document document = (node instanceof Document) ? ((Document) node) : node.getOwnerDocument();
    return (document instanceof DocumentImpl) ? ((DocumentImpl) document).getNamespaceAwareness() : DOM.NSA_UNKNOWN;
  }

  public static void setInQMyJAXP() {
    SystemProperties.setProperty(javax.xml.parsers.DocumentBuilderFactory.class.getName(), com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl.class.getName());
    SystemProperties.setProperty(javax.xml.parsers.SAXParserFactory.class.getName(), com.sap.engine.lib.jaxp.SAXParserFactoryImpl.class.getName());
    SystemProperties.setProperty(javax.xml.transform.TransformerFactory.class.getName(), com.sap.engine.lib.jaxp.TransformerFactoryImpl.class.getName());
  }

  /**
   * Utility method that facilitates serialization of DOM nodes.
   * Caution: NOT RECOMMENDED for purposes other than debugging.
   */
  public static void dumpToStream(Node n, OutputStream out) throws java.io.FileNotFoundException, javax.xml.transform.TransformerConfigurationException, javax.xml.transform.TransformerException {
    Transformer tr = TransformerFactory.newInstance().newTransformer();
    tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    tr.setOutputProperty(OutputKeys.INDENT, "no");
    StreamResult result = new StreamResult(out);
    Source source = new DOMSource(n);
    tr.transform(source, result);
  }

  /**
   * Utility method that facilitates the creation of DOM nodes from a file.
   * Caution: NOT RECOMMENDED for purposes other than debugging.
   */
  public static Node readFileAsDOM(String xmlFile) throws java.io.IOException, java.io.FileNotFoundException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
    FileInputStream in = new FileInputStream(xmlFile);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(in);
    return document;
  }

  /**
   * Utility method that facilitates serialization of DOM nodes.
   * Caution: NOT RECOMMENDED for purposes other than debugging.
   */
  public static void dumpToFile(Node n, String fileName) throws java.io.FileNotFoundException, javax.xml.transform.TransformerConfigurationException, javax.xml.transform.TransformerException {
    FileOutputStream out = new FileOutputStream(fileName);
    Transformer tr = TransformerFactory.newInstance().newTransformer();
    tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    tr.setOutputProperty(OutputKeys.INDENT, "no");
    StreamResult result = new StreamResult(out);
    Source source = new DOMSource(n);
    tr.transform(source, result);
  }

  /**
   * Utility method that facilitates the creation of DOM nodes from a stream.
   * Caution: NOT RECOMMENDED for purposes other than debugging.
   */
  public static Node readStreamAsDOM(InputStream in) throws java.io.IOException, java.io.FileNotFoundException, javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(in);
    return document;
  }

//  public static void main(String[] args) throws Exception {
//    String XML = "<a xmlns:p='ppp' xmlns='eee' xmlns:w='www'><b xmlns='' xmlns:p='ppp1' xmlns:q='qqq'/></a>";
//    Node node = parseXMLFromAString(XML);
//    node = node.getFirstChild().getFirstChild();
//    Hashtable h = getNamespaceMappingsInScope(node);
//    LogWriter.getSystemLogWriter().println(h);
//  }
  
  public static String getNodeValue(Node node) {
  	boolean hasChildren = false;
  	String value = null;
		if(node instanceof Attr) {
			value = node.getNodeValue().trim();
		} else if(node instanceof Element) {
			NodeList childNodes = node.getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node childNode =  childNodes.item(i);
				int nodeType = childNode.getNodeType();
				if(nodeType == Node.TEXT_NODE) {
					value = childNode.getNodeValue().trim();
					if(!value.equals("")) {
						return(value);
					}
				} else if(nodeType == Node.ELEMENT_NODE) {
					hasChildren = true;
				}
			}
		}
		return(hasChildren ? null : value);
	}
	
	public static Node getParent(Node node) {
		return(node instanceof Attr ? ((Attr)node).getOwnerElement() : node.getParentNode());
	}
	
  /**
   * Returns list of Element objects.
   * The list contains child elements with namespace and local name, 
   * according to the given parameters.
   */	
  public static List getChildElementsByTagNameNS(Element root, String ns, String localName) {
    List list = new ArrayList();
    NodeList children = root.getChildNodes();
    Node cur;
    for (int i = 0; i < children.getLength(); i++) {
      cur = children.item(i);
      if (cur.getNodeType() == Node.ELEMENT_NODE) {
        if (cur.getNamespaceURI() == null) {
          if (ns == null) { //if both are null
            if (cur.getLocalName().equals(localName)) {
              list.add(cur);
            }
          }
        } else {
          if (cur.getNamespaceURI().equals(ns) && cur.getLocalName().equals(localName)) {
            list.add(cur);     
          }
        }
      }
    }
    return list;
  }
  
  /**
   * Returns the prefix mapped for certain namespace, starting
   * from the scope element and going up in the tree.
   * If no prefix is found, null is returned.
   */
  public static String getPrefixForNS(Element scope, String ns) {
    NamedNodeMap attrs = scope.getAttributes();
    int nAttrs = attrs.getLength(); 

    for (int i = 0; i < nAttrs; i++) {
      Node attr = attrs.item(i);
      String qname = attr.getNodeName();
      String value = attr.getNodeValue();
      if (value.equals(ns) && NS.XMLNS.equals(attr.getNamespaceURI())) {
        return attr.getLocalName();
      }
    }
    //check for element instance is done because the root element of xml has parent Document entity.
    if (scope.getParentNode() != null && (scope.getParentNode() instanceof Element)) {
      return getPrefixForNS((Element) scope.getParentNode(), ns);
    }
    return null;
  }
  /**
   * Seacheas the element into which <code>pref</code> is defined. The search
   * starts from the <code>scope</code> element and continues upwards.
   * @return Elementn at which the prefix is declared, or null if the prefix is no found to be declared.
   */
  public static Element getPrefixDeclaringElement(Element scope, String pref) {
    NamedNodeMap attrs = scope.getAttributes();
    int nAttrs = attrs.getLength(); 

    for (int i = 0; i < nAttrs; i++) {
      Node attr = attrs.item(i);
      String qname = attr.getNodeName();
      String value = attr.getNodeValue();
      if (NS.XMLNS.equals(attr.getNamespaceURI()) && attr.getLocalName().equals(pref)) {
        return scope;
      }
    }
    //check for element instance is done because the root element of xml has parent Document entity.
    if (scope.getParentNode() != null && (scope.getParentNode() instanceof Element)) {
      return getPrefixDeclaringElement((Element) scope.getParentNode(), pref);
    }
    return null;    
  }
  /**
   * Converts <code>el</code> into string where all prefixes that are currently in use
   * are copied into the root node.
   */
  public static String toSelfDescribingString(Element el) throws Exception {
    Hashtable mappings = getNamespaceMappingsInScope(el);
    
    Element newEl = (Element) el.cloneNode(true);
    Enumeration en = mappings.keys();
    String pref, ns;
    while (en.hasMoreElements()) {
      pref = (String) en.nextElement();
      ns = (String) mappings.get(pref);
      if ("".equals(pref)) {
        newEl.setAttributeNS(NS.XMLNS, "xmlns", ns);
      } else {
        newEl.setAttributeNS(NS.XMLNS, "xmlns:" + pref, ns);
      }
    }
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = tf.newTransformer();
    CharArrayWriter caW = new CharArrayWriter();
    t.transform(new DOMSource(newEl), new StreamResult(caW));
    return caW.toString();
  }
}

