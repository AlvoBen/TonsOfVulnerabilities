package com.sap.engine.lib.xml.util;

import com.sap.engine.lib.xml.names.XMLNameMapper;

/**
 * Class representing QName.
 */
public class QName implements java.io.Serializable {

  String prefix;
  String localName;
  String uri;

  public QName(String prefix, String localName, String uri) {
    if (prefix == null) {
      prefix = "";
    }

    this.prefix = prefix;
    this.localName = localName;
    this.uri = uri;
  }

  /**
   * Creates QName
   */
  public QName(String qName, String uri) {
    this.prefix = getPrefix(qName);
    this.localName = getLocalName(qName);
    this.uri = uri;
  }

  public int hashCode() {
    return localName.hashCode() ^ uri.hashCode();
  }

  public boolean equals(Object object) {
    if (!(object instanceof QName)) {
      return super.equals(object);
    }

    QName name = (QName) object;

    if (!prefix.equals(name.prefix)) {
      return false;
    }

    if (!localName.equals(name.localName)) {
      return false;
    }

    if (!uri.equals(name.uri)) {
      return false;
    }

    return true;
  }

  public String getQName() {
    if (prefix.length() == 0) {
      return localName;
    } else {
      return prefix + ":" + localName;
    }
  }

  public String toString() {
    return getQName();
  }

  public String getLocalName() {
    return localName;
  }

  public String getName() {
    return localName;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getURI() {
    return uri;
  }

  public void setURI(String uri) {
    this.uri = uri;
  }

  /* ----------------------------- Static methods --------------------------------*/
  /**
   * Uses com.sap.engine.lib.xml.names.XMLNameMapper to create class name froma local attribute name
   */
  public static String repairClassName(String oldName) {
    return XMLNameMapper.toClassIdentifierStatic(oldName);
  }

  /**
   * Uses com.sap.engine.lib.xml.names.XMLNameMapper to create method name from local attribute name
   */
  public static String repairMethodName(String oldName) {
    return XMLNameMapper.toMethodIdentifierStatic(oldName);
  }

  /**
   * Repairs local name to package name
   */
  public static String repairPackageName(String oldName) {
    StringBuffer result = new StringBuffer();

    for (int i = 0; i < oldName.length(); i++) {
      char c = oldName.charAt(i);

      if (Character.isLetterOrDigit(c)) {
        result.append(Character.toLowerCase(c));
      }
    } 

    return result.toString();
  }

  /**
   * QName creator - creates new QName object
   */
  public static QName qnameCreate(String qName, NamespaceContainer uriContainer) throws Exception {
    String prefix = getPrefix(qName);
    String localName = getLocalName(qName);
    String uri = uriContainer.getPrefixURI(prefix);

    if (uri == null && prefix.length() != 0) {
      throw new Exception(" Trying to ask for QName with undefined namespace prefix: " + qName);
    }

    QName result = new QName(prefix, localName, uri);
    return result;
  }

  /**
   * QName creator with targetNamespace support
   */
  public static QName qnameWSDLCreate(String qName, NamespaceContainer uriContainer, String targetNamespace) throws Exception {
    String prefix = getPrefix(qName);
    String localName = getLocalName(qName);
    String uri = null;

    if (prefix.length() == 0) {
      uri = targetNamespace;
    } else {
      uri = uriContainer.getPrefixURI(prefix);
    }

    if (uri == null && prefix.length() != 0) {
      throw new Exception(" Trying to ask for QName with undefined namespace prefix: " + qName);
    }

    QName result = new QName(prefix, localName, uri);
    return result;
  }

  public static String getPrefix(String qname) {
    int separator = qname.lastIndexOf(':');

    if (separator == -1) {
      return "";
    } else {
      return qname.substring(0, separator);
    }
  }

  public static String getLocalName(String qname) {
    int separator = qname.indexOf(':');

    if (separator == -1) {
      return qname;
    } else {
      return qname.substring(separator + 1);
    }
  }

}

