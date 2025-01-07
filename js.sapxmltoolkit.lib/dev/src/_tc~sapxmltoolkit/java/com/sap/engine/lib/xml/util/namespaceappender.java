package com.sap.engine.lib.xml.util;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.DocHandler;
import com.sap.engine.lib.xml.parser.handlers.EmptyDocHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.parser.helpers.Entity;
import com.sap.engine.lib.xml.parser.helpers.Reference;
import com.sap.engine.lib.xsl.xpath.DTMFactory;

/**
 * Implements the namespace fixup routines defined in XSLT.
 *
 * An instance of this class should be 'plugged' in a chain of DocHandlers
 * in order to ensure that the resulting (serialized) document will be
 * correct according to the the namespaces syntax (i.e. every element and
 * attribute will be in the namespace it is requested to be and there will be
 * no undeclared namespace prefixes).
 *
 * <pre>
 * The algorithm is the following:
 * If the current event is:
 *   1. ELEMENT with a prefix and its namespace uri: map (or remap) if necessary
 *           the prefix to the uri
 *   2. ELEMENT with no prefix but with a uri: set (or reset) if necessary
 *           the default xmlns prefix to that uri
 *   3. ATTRIBUTE with a prefix and its namespace uri: do similarly to case 1,
 *           but also check if the same prefix has already been used in the same
 *           markup with a different uri.
 *   4. ATTRIBUTE with no prefix, but with a uri: throw an exception
 *   5. ATTRIBUTE whose name is xmlns or matches xmlns:* :
 *           check if its presence would cause any contradiction with appended
 *           namespace declarations.
 * </pre>
 *
 * <p>
 * This classification is due to Vladimir Savchenko.
 * </p>
 * <p>
 * My implementation does not strictly follow the above.
 * Also, it does not produce xmlns declarations, unless they are
 * needed. For example if prefix 'p' has to be mapped
 * to uri 'u' and there is such a mapping in scope of the current element,
 * then a new mapping is not declared.
 * If there is an xmlns or xmlns:* attribute reported to this class, and the
 * corresponding ns mapping is already in scope, the attribute is hushed down.
 * </p>
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      August 2001
 */
public final class NamespaceAppender extends EmptyDocHandler {

  // Constants
  private static final String MARK = "#MARK"; // ns context separator
  private static final String DNS = "#DNS"; // denotes the default ns
  private static final String NONE = "#NONE"; // used for undeclaring the default ns
  private static final CharArray CA_EMPTY = CharArray.EMPTY;
  private static final CharArray CA_XMLNS = new CharArray("xmlns");
  private static final CharArray CA_XMLNS_COLON = new CharArray("xmlns:");
  private static final CharArray CA_XML = new CharArray("xml");
  private static final CharArray CA_XMLNS_NS = new CharArray(NS.XMLNS);
  // Fields
  private DocHandler h = null;
  private String[] stack = new String[100];
  private int nStack;
  private boolean isMappedInLastContext = false; // modified by the findMapping method
  private CharArray ca0 = new CharArray(); // a temporary variable
  private CharArray ca1 = new CharArray(); // a temporary variable
  private CharArray ca2 = new CharArray(); // a temporary variable

  public NamespaceAppender() {

  }

  public NamespaceAppender(DocHandler h) {
    init(h);
  }

  public NamespaceAppender init(DocHandler h) {
    this.h = h;
    return this;
  }

  public void startDocument() throws Exception {
    nStack = 0;
    h.startDocument();
  }

  public void startElementStart(CharArray uri, CharArray localName, CharArray qname) throws Exception {
    qname.parseNS(ca0, ca1);
//    String prefix = ca0.getString();
    String uri1 = findMapping(ca0);//prefix

    //if an element with a prefix in a previously declared namespace is used, then do not throw error
    if (empty(uri) && !empty(uri1)) {
      uri = new CharArray(uri1);
    }

    h.startElementStart(uri, localName, qname);
    pushContext();

    if (empty(uri)) {
//      if (!empty(prefix)) {
//        throw new TransformerException("Namespace fixup failed. An element with a prefix, but with no uri.");
//      }

      undeclareDNS();
    } else if (empty(ca0)) { // prefix
      declareDNS(uri.toString());
    } else {
      String prefix = ca0.getString();
      declare(prefix, uri.toString());
    }
  }

  public void addAttribute(CharArray curi, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) throws Exception {
   // String uri = curi.toString();

    if (empty(prefix) && localName.equals("xmlns")) {
      if (empty(value)) {
        undeclareDNS();
      } else {
        declareDNS(value.getString());
      }

      return;
    } else if (prefix.equals("xmlns")) {
      declare(localName.getString(), value.getString());
      return;
    } else if (localName.startsWith(CA_XMLNS_COLON)) {
      // this case is used when a xmlns:xxxx attribute is passed but in non namespace aware mode - that is
      // the prefix is empty and the localname is xmlns:xxxx - this is used in SAP's applications
      // by Rolland Preusmann - credentials
      declare(localName.getString().substring(6), value.getString());
      return;
    } else if (curi.length()==0) {
      if (!empty(prefix) && !prefix.startsWith(CA_XML)) {
        throw new TransformerException("Namespace fixup failed. Prefix \'" + prefix + "\' used in attribute \'" + qname + "\' is not declared anywhere");
      }
    } else if (empty(prefix)) {
      LogWriter.getSystemLogWriter().println("Warning: XMLParser/NamepspaceAppender: Adding an attribute with name:\"" + qname + "\" and namespace:\"" + curi + "\". This is not allowed. The attribute name must have a prefix. Ignoring namespace declaration." );
      //throw new TransformerException("Namespace fixup failed. NamespaceAppender - prefix is empty!");
    } else if (!prefix.equals(CA_XML)) {
      String uri = curi.toString();
      declare(prefix.getString(), uri);
    }

    h.addAttribute(curi, prefix, localName, qname, type, value);
  }

  public void startElementEnd(boolean isEmpty) throws Exception {
    h.startElementEnd(isEmpty);
  }

  public void endElement(CharArray curi, CharArray localName, CharArray qname, boolean isEmpty) throws Exception {
    h.endElement(curi, localName, qname, isEmpty);
    popContext();
  }

  public void onXMLDecl(String version, String encoding, String ssdecl) throws Exception {
    h.onXMLDecl(version, encoding, ssdecl);
  }

  public void startPrefixMapping(CharArray prefix, CharArray uri) throws Exception {

  }

  public void endPrefixMapping(CharArray prefix) throws Exception {

  }

  // The following methods are simply passed on to the DocHandler wrapped here
  public void endDocument() throws Exception {
    h.endDocument();
  }

  public void charData(CharArray carr, boolean bDisableOutputEscaping) throws Exception {
    h.charData(carr, bDisableOutputEscaping);
  }

  public void onPI(CharArray target, CharArray data) throws Exception {
    h.onPI(target, data);
  }

  public void onComment(CharArray text) throws Exception {
    h.onComment(text);
  }

  public void onCDSect(CharArray text) throws Exception {
    h.onCDSect(text);
  }

  public void onDTDElement(CharArray name, CharArray model) throws Exception {
    h.onDTDElement(name, model);
  }

  public void onDTDAttListStart(CharArray name) throws Exception {
    h.onDTDAttListStart(name);
  }

  public void onDTDAttListItem(CharArray name, CharArray attname, String type, String defDecl, CharArray vAttValue, String note) throws Exception {
    h.onDTDAttListItem(name, attname, type, defDecl, vAttValue, note);
  }

  public void onDTDAttListEnd() throws Exception {
    h.onDTDAttListEnd();
  }

  public void onDTDEntity(Entity entity) throws Exception {
    h.onDTDEntity(entity);
  }

  public void onDTDNotation(CharArray name, CharArray pub, CharArray sys) throws Exception {
    h.onDTDNotation(name, pub, sys);
  }

  public void startDTD(CharArray name, CharArray pub, CharArray sys) throws Exception {
    h.startDTD(name, pub, sys);
  }

  public void endDTD() throws Exception {
    h.endDTD();
  }

  public void onContentReference(Reference ref) throws Exception {
    h.onContentReference(ref);
  }

  public void onWarning(String s) throws Exception {
    h.onWarning(s);
  }

  // Helper methods
  private void push(String prefix, String uri) {
    if (nStack + 2 >= stack.length) {
      // resize
      String[] old = stack;
      stack = new String[nStack * 2];
      System.arraycopy(old, 0, stack, 0, old.length);
    }

    stack[nStack++] = prefix;
    stack[nStack++] = uri;
  }

  private void pushContext() {
    push(MARK, MARK);
  }

  // p and u must be non-null and non-empty Strings
  private void declare(String p, String u) throws Exception {
    String m = findMapping(p);

    if (isMappedInLastContext) {
      if (!m.equals(u)) {
        throw new TransformerException("Namespace fixup failed. Attempt to map prefix '" + p + "' to both '" + m + "' and '" + u + "'");
      }
    } else {
      if ((m == null) || !m.equals(u)) {
        push(p, u);
        ca1.set(CA_XMLNS);
        ca1.append(':');
        ca1.append(p);
        h.addAttribute(null, CA_XMLNS, ca0.set(p), ca1, null, ca2.set(u));
      }
    }
  }

  // u must be a non-null and non-empty String
  private void declareDNS(String u) throws Exception {
    String m = findMapping(DNS);

    if (isMappedInLastContext) {
      if (!m.equals(u)) {
        throw new TransformerException("Namespace fixup failed.");
      }
    } else {
      if ((m == null) || (m.equals(NONE)) || !m.equals(u)) {
        push(DNS, u);
        h.addAttribute(CA_XMLNS_NS, CA_EMPTY, CA_XMLNS, CA_XMLNS, null, ca0.set(u));
      }
    }
  }

  private void undeclareDNS() throws Exception {
    String m = findMapping(DNS);

    if (isMappedInLastContext) {
      if (!m.equals(NONE)) {
        throw new TransformerException("Namespace fixup failed");
      }
    } else {
      if ((m != null) && (!m.equals(NONE))) {
        push(DNS, NONE);
        h.addAttribute(CA_XMLNS_NS, CA_EMPTY, CA_XMLNS, CA_XMLNS, null, CA_EMPTY);
      }
    }
  }

  private void popContext() {
    while (!stack[--nStack].equals(MARK)) {

    }

    nStack--;
  }

  private String findMapping(String prefix) {
    int i = nStack - 2;
    isMappedInLastContext = true;

    while (i >= 0) {
      if (stack[i].equals(MARK)) {
        i -= 2;
        isMappedInLastContext = false;
        continue;
      }

      if (prefix.equals(stack[i])) {
        return stack[i + 1];
      }

      i -= 2;
    }

    return null; // not found
  }

  private String findMapping(CharArray prefix) {
    int i = nStack - 2;
    isMappedInLastContext = true;

    while (i >= 0) {
      if (stack[i].equals(MARK)) {
        i -= 2;
        isMappedInLastContext = false;
        continue;
      }

      if (prefix.equals(stack[i])) {
        return stack[i + 1];
      }

      i -= 2;
    }

    return null; // not found
  }  
  
  private boolean empty(String s) {
    return ((s == null) || (s.length() == 0));
  }

  private boolean empty(CharArray s) {
    return ((s == null) || (s.length() == 0));
  }

  public void onStartContentEntity(CharArray name, boolean isExpanding) throws Exception {
    h.onStartContentEntity(name, isExpanding);
  }

  public void onEndContentEntity(CharArray name) throws Exception {
    h.onEndContentEntity(name);
  }

  public void onCustomEvent(int eventId, Object obj) throws Exception {
    h.onCustomEvent(eventId, obj);
  }

  void setNodeToHandler(Node node) {
    if (h instanceof DTMFactory) {
      ((DTMFactory) h).setNodeToHandler(node);
    }
  }
}

