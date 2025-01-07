package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
import com.sap.engine.lib.xml.*;

/**
 *
 * @author Nick Nickolov
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public final class DOMImplementationImpl implements DOMImplementation {

  /**
   * <pre>
   *    http://www.w3.org/TR/DOM-Level-2-Core/introduction.html#ID-Conformance
   *
   *    Here is the complete list of DOM Level 2.0 modules and the features used by
   *    them. Feature names are case-insensitive.
   *
   *    Core module             defines the feature "Core".
   *    XML module              defines the feature "XML".
   *    HTML module             defines the feature "HTML". (see [DOM Level 2 HTML]).
   *    Views module            defines the feature "Views" in [DOM Level 2 Views].
   *    Style Sheets module     defines the feature "StyleSheets" in [DOM Level 2 Style Sheets].
   *    CSS module              defines the feature "CSS" in [DOM Level 2 CSS].
   *    CSS2 module             defines the feature "CSS2" in [DOM Level 2 CSS].
   *    Events module           defines the feature "Events" in [DOM Level 2 Events].
   *    User interface Events module   defines the feature "UIEvents" in [DOM Level 2 Events].
   *    Mouse Events module     defines the feature "MouseEvents" in [DOM Level 2 Events].
   *    Mutation Events module  defines the feature "MutationEvents" in [DOM Level 2 Events].
   *    HTML Events module      defines the feature "HTMLEvents" in [DOM Level 2 Events].
   *    Range module            defines the feature "Range" in [DOM Level 2 Range].
   *    Traversal module        defines the feature "Traversal" in [DOM Level 2 Traversal].
   */
  public static boolean hasFeatureStatic(String feature, String version) {
    if (feature == null) {
      return false;
    }

    if (version != null && !version.trim().equals("")) {
      if (!version.startsWith("1.") && !version.equals("2.0")) {
        return false;
      }
    }

    return feature.equalsIgnoreCase("core") || feature.equalsIgnoreCase("xml");
  }

  public boolean hasFeature(String feature, String version) {
    return hasFeatureStatic(feature, version);
  }

  public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
    return (new DocumentTypeImpl()).init(qualifiedName, publicId, systemId, null);
  }

  public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) {
    ElementImpl root = null;

    if (qualifiedName != null) {
      if (!Symbols.isName(qualifiedName)) {
        throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Unable to create document, the name of the root element " + "is not a valid xml name, '" + qualifiedName + "'");
      }

      root = (new ElementImpl()).init(namespaceURI, qualifiedName, null);
    }

    if (doctype != null) {
      Document ownerOfTheDoctype = doctype.getOwnerDocument();

      if (ownerOfTheDoctype != null) {
        throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, "DOCTYPE already in use.");
      }
    }

    //Document document = (new DocumentImpl()).init(root, doctype);
    DocumentImpl document = new DocumentImpl();

    if (root != null) {
      root.setOwnerDocument(document);
    }

    document.init(root, doctype);

    if (doctype != null) {
      ((Base) doctype).setOwnerDocument(document);
    }

    return document;
  }

  public static String toXPath(Node node) {
    return DOM.toXPath(node);
  }

  public static String toXPathWithLocation(Node node) {
    return DOM.toXPathWithLocation(node);
  }

  public static StringBuffer toXPathWithLocation(StringBuffer buffer, Node node) {
    return DOM.toXPathWithLocation(buffer, node);
  }

  public static StringBuffer toXPath(StringBuffer buffer, Node node) {
    return DOM.toXPath(buffer, node);
  }

  public static Node toNode(Node scope, String xpath) {
    return DOM.toNode(xpath, scope);
  }

  public static String prefixToURI(String prefix, Node scope) {
    return QNameResolver.prefixToURI(prefix, scope);
  }

  public static String qnameToPrefix(String qname) {
    return QNameResolver.qnameToPrefix(qname);
  }

  public static String qnameToLocalName(String qname) {
    return QNameResolver.qnameToLocalName(qname);
  }

  public static String qnameToURI(String prefix, Node scope) {
    return QNameResolver.qnameToURI(prefix, scope);
  }

  public Object getFeature(String feature, String version){
  	throw new NullPointerException("Not implemented!");
  }
}

