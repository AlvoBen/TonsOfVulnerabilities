package com.sap.engine.lib.xml.util;

import java.util.*;
import org.xml.sax.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 *
 * @deprecated
 */
class AttributeSorter implements Comparator {

  private Attributes attributes = null;
  private Integer[] ordinals;
  private Integer[] permutation;
  private boolean useNamespaces;

  AttributeSorter(int n) {
    ordinals = new Integer[n];
    permutation = new Integer[n];

    for (int i = 0; i < n; i++) {
      ordinals[i] = new Integer(i);
    } 
  }

  void process(Attributes attributes, boolean useNamespaces) {
    this.attributes = attributes;
    this.useNamespaces = useNamespaces;
    int n = attributes.getLength();

    if (n > ordinals.length) {
      int oldCapacity = ordinals.length;
      Integer[] ordinalsOld = ordinals;
      ordinals = new Integer[n];
      permutation = new Integer[n];
      System.arraycopy(ordinalsOld, 0, ordinals, 0, oldCapacity);

      for (int i = oldCapacity; i < n; i++) {
        ordinals[i] = new Integer(i);
      } 
    }

    System.arraycopy(ordinals, 0, permutation, 0, n);
    Arrays.sort(permutation, 0, n, this);
  }

  String getURI(int index) {
    return attributes.getURI(permutation[index].intValue());
  }

  String getLocalName(int index) {
    return attributes.getLocalName(permutation[index].intValue());
  }

  String getQName(int index) {
    return attributes.getQName(permutation[index].intValue());
  }

  String getValue(int index) {
    return attributes.getValue(permutation[index].intValue());
  }

  public int compare(Object a, Object b) {
    int ia = ((Integer) a).intValue();
    int ib = ((Integer) b).intValue();
    String sa;
    String sb;

    if (useNamespaces) {
      sa = attributes.getLocalName(ia);
      sb = attributes.getLocalName(ib);
    } else {
      sa = attributes.getQName(ia);
      sb = attributes.getQName(ib);
    }

    return sa.compareTo(sb);
  }

//  public boolean equals(Object object) {
//    return (object == this); //The same is executed in java.lang.Object, otherwise we have to override the hashcode method 
//  }
}

