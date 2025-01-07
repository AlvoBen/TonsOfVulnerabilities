package com.sap.engine.lib.xml.dom.xpath;

import java.util.*;
import org.w3c.dom.*;

public final class XPathNSResolverImpl { //implements XPathNSResolver {

  private Hashtable namespaceMappings;
  private Node node;

  public XPathNSResolverImpl(Node node) {
    if (node == null) {
      throw new IllegalArgumentException();
    }

    this.namespaceMappings = null;
    this.node = node;
  }

  /**
   * A custom method to serve the needs of XPointer
   */
  protected void addNamespaceMapping(String prefix, String uri) {
    if (prefix == null) {
      throw new IllegalArgumentException();
    }

    if (uri == null) {
      uri = "";
    }

    if (namespaceMappings == null) {
      namespaceMappings = new Hashtable();
    }

    namespaceMappings.put(prefix, uri);
  }

  public String lookupNamespaceURI(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException();
    }
    
    String uri = null;
    if (namespaceMappings != null) {
      uri = (String) namespaceMappings.get(prefix);
    }


    if (uri == null) {
      return com.sap.engine.lib.xml.dom.QNameResolver.prefixToURI(prefix, node);
    } else {
      return uri;
    }
  }

}

