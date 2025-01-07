package com.sap.engine.lib.xsl.xslt;

import java.util.Vector;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class InternalAttributeList {

  protected Vector items = new Vector();
  int allsize = 0;
  int cursize = 0;

  private boolean compareCA(CharArray ca1, CharArray ca2) {
    if (ca1 == null && ca2 == null) {
      return true;
    } else if ((ca1 == null && ca2.length() == 0) || (ca2 == null && ca1.length() == 0)) {
      return true;
    } else if (ca1 == null || ca2 == null) {
      return false;
    } else if (ca1.equals(ca2)) {
      return true;
    } else {
      return false;
    }
  }

  public int find(CharArray uri, CharArray prefix, CharArray qname, CharArray localname) {
    for (int i = 0; i < cursize; i++) {
      InternalAttribute ia = get(i);

      if (compareCA(ia.uri, uri) && compareCA(ia.prefix, prefix) && compareCA(ia.qname, qname) && compareCA(ia.localName, localname)) {
        return i;
      }
    } 

    return -1;
  }

  public void addAttribute(CharArray uri, CharArray prefix, CharArray qname, CharArray localname, CharArray value) {
    int idx = find(uri, prefix, qname, localname);

    //    LogWriter.getSystemLogWriter().println("IAL: idx= " + idx);
    if (cursize == allsize && idx == -1) {
      items.add(new InternalAttribute().init(uri, prefix, qname, localname, value));
      allsize++;
      cursize++;
    } else if (idx > -1) {
      ((InternalAttribute) items.get(idx)).init(uri, prefix, qname, localname, value);
    } else {
      ((InternalAttribute) items.get(cursize)).init(uri, prefix, qname, localname, value);
      cursize++;
    }
  }

  public void clear() {
    cursize = 0;
  }

  public int getLength() {
    return cursize;
  }

  public InternalAttribute getAttr(int i) {
    return (InternalAttribute) items.get(i);
  }

  public InternalAttribute get(int i) {
    return (InternalAttribute) items.get(i);
  }

  public int compare(int a, int b) {
    return getAttr(a).localName.compareTo(getAttr(b).localName);
  }

  public void swap(int a, int b) {
    InternalAttribute ia = getAttr(a);
    InternalAttribute ib = getAttr(b);
    items.setElementAt(ia, b);
    items.setElementAt(ib, a);
  }

  public int size() {
    return cursize;
  }

  public void addAttribute(InternalAttribute at) {
    addAttribute(at.uri, at.prefix, at.qname, at.localName, at.value);
  }

  public void copy(InternalAttributeList ia) {
    clear();

    for (int i = 0; i < ia.size(); i++) {
      addAttribute(ia.get(i));
    } 
  }
  
  public String getValue(CharArray uri, CharArray localName) {
    for (int i = 0; i < cursize; i++) {
      InternalAttribute ia = get(i);

      if (compareCA(ia.uri, uri) && compareCA(ia.localName, localName)) {
        return ia.value.toString();
      }
    } 
    return null;
  }

  public String getValue(CharArray localName) {
    return getValue(CharArray.EMPTY, localName);
  }

  public String getValue(String localName) {
    return getValue("", localName);
  }

  public String getValue(String uri, String localName) {
    for (int i = 0; i < cursize; i++) {
      InternalAttribute ia = get(i);

      if (ia.uri.equals(uri) && ia.localName.equals(localName)) {
        return ia.value.toString();
      }
    } 
    return null;
  }

}

