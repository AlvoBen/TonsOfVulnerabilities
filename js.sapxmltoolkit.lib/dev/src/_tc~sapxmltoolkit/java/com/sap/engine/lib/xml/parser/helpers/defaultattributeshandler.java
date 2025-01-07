package com.sap.engine.lib.xml.parser.helpers;

import java.util.*;

public class DefaultAttributesHandler {

  private Hashtable attributesHash = null;
  private Hashtable attributesVect = null;

  public DefaultAttributesHandler() {
    attributesHash = new Hashtable();
    attributesVect = new Hashtable();
  }

  public void reuse() {
    attributesHash.clear();
    attributesVect.clear();
  }

  public void add(CharArray elementName, CharArray atQName, CharArray atPrefix, CharArray atLocalName, CharArray value) {
    if (attributesVect.containsKey(elementName) == false) {
      attributesVect.put(elementName.copy(), new Vector());
      attributesHash.put(elementName.copy(), new Hashtable());
    }

    Attribute a = new Attribute().reuse(atQName, atPrefix, atLocalName, null, value, false);
    Vector v = (Vector) attributesVect.get(elementName);
    Hashtable h = (Hashtable) attributesHash.get(elementName);
    v.add(a);
    h.put(a.crRawName, a);
  }

  public int length(CharArray elementName) {
    if (attributesVect.containsKey(elementName) == false) {
      return 0;
    } else {
      return ((Vector) attributesVect.get(elementName)).size();
    }
  }

  public Vector getVect(CharArray elementName) {
    return (Vector) attributesVect.get(elementName);
  }

  public Vector getHash(CharArray elementName) {
    return (Vector) attributesVect.get(elementName);
  }

  public void clearDefCheck(CharArray elementName) {
    Vector v = (Vector) attributesVect.get(elementName);

    if (v != null) {
      for (int i = 0; i < v.size(); i++) {
        ((Attribute) v.get(i)).setDefCheck(false);
      } 
    }
  }

  public void setDefCheck(CharArray elementName, CharArray attributeName) {
    Hashtable hash = (Hashtable) attributesHash.get(elementName);

    if (hash != null) {
      Attribute attr = (Attribute) hash.get(attributeName);

      if (attr != null) {
        attr.setDefCheck(true);
      }
    }
  }

  public void clear() {
    attributesVect.clear();
    attributesHash.clear();
  }

}

