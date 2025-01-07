package com.sap.engine.lib.xsl.xslt.output;

import java.util.Comparator;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class AttributeComparator implements Comparator {

  private static final CharArray xmlns = new CharArray("xmlns");
//  private static final CharArray empty = new CharArray("");

  public int compare(Object obj1, Object obj2) {
    return compare((AttributeRepr) obj1, (AttributeRepr) obj2);
  }

  /**
   * xmlns="value" first
   * xmlns:prefix="URI" second
   * localName = "value" third
   * prefix:localName = "value" fourth
   *
   */
  public int compare(AttributeRepr attr1, AttributeRepr attr2) {
    //LogWriter.getSystemLogWriter().println("About to compare " + attr1.localName + " with " + attr2.localName);
    if (xmlns.equals(attr1.localName) && (attr1.prefix == null)) {
      return -1;
    }

    if (xmlns.equals(attr2.localName) && (attr2.prefix == null)) {
      return 1;
    }

    if (xmlns.equals(attr1.prefix)) {
      if (!xmlns.equals(attr2.prefix)) {
        return -1;
      }

      return attr1.localName.compareTo(attr2.localName);
    }

    if (attr2.prefix != null && attr2.prefix.equals(xmlns)) {
      return 1;
    }

    if (attr1.prefix == null) {
      if (attr2.prefix != null) {
        return -1;
      }

      int result = attr1.localName.compareTo(attr2.localName);
      //LogWriter.getSystemLogWriter().println("Returning " + result);
      return result;
    }

    if (attr2.prefix == null) {
      return 1;
    }

    int result = attr1.uri.compareTo(attr2.uri);
    if (result==0){
      return attr1.localName.compareTo(attr2.localName);
    }
    return result;
  }

}

