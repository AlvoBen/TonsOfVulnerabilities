package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;

/**
 * Has static method that alleviate resolving a QName's components:
 * prefix, local name, and the URI mapped to the prefix.
 *
 * QNameResolver would accept nodes from any DOM implementation. (There are no casts to impl)
 *
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      November 2001
 *
 *
 * @deprecated I moved the implementation of the methods to com.sap.engine.lib.xml.dom.DOM
 */
@Deprecated
public final class QNameResolver {

  private QNameResolver() {

  }

  public static String prefixToURI(String prefix, Node scope) {
    return DOM.prefixToURI(prefix, scope);
  }

  public static String qnameToURI(String qname, Node scope) {
    return DOM.qnameToURI(qname, scope);
  }

  public static String qnameToLocalName(String qname) {
    return DOM.qnameToLocalName(qname);
  }

  public static String qnameToPrefix(String qname) {
    return DOM.qnameToPrefix(qname);
  }

}

