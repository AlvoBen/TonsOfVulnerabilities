package com.sap.engine.lib.xsl.xslt.output;

import java.util.Arrays;
import java.util.Comparator;

import org.xml.sax.Attributes;

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
final class AttributeSorter implements Comparator {

  private int capacity;
  private Attributes attributes = null;
  private Integer[] ordinals;
  private Integer[] permutation;
  private String[] sortKeys = null;

  AttributeSorter(int initialCapacity) {
    ordinals = new Integer[initialCapacity];
    permutation = new Integer[initialCapacity];

    for (int i = 0; i < initialCapacity; i++) {
      ordinals[i] = new Integer(i);
    } 

    sortKeys = new String[initialCapacity];
    capacity = initialCapacity;
  }

  private void ensureCapacity(int n) {
    if (n > capacity) {
      Integer[] oldOrdinals = ordinals;
      ordinals = new Integer[n];
      permutation = new Integer[n];
      sortKeys = new String[n];
      System.arraycopy(oldOrdinals, 0, ordinals, 0, capacity);

      for (int i = capacity; i < n; i++) {
        ordinals[i] = new Integer(i);
      } 

      capacity = n;
    }
  }

  void process(Attributes attributes, boolean performSort, boolean useLocalNamesAsKeys) {
    this.attributes = attributes;
    int n = attributes.getLength();
    ensureCapacity(n);
    System.arraycopy(ordinals, 0, permutation, 0, n);

    if (performSort) {
      if (useLocalNamesAsKeys) {
        for (int i = 0; i < n; i++) {
          String k = attributes.getLocalName(i);
          sortKeys[i] = ((k == null) || k.equals("")) ? attributes.getQName(i) : k;
        } 
      } else {
        for (int i = 0; i < n; i++) {
          sortKeys[i] = attributes.getQName(i);
        } 
      }

      Arrays.sort(permutation, 0, n, this);
    }
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
    String sa = sortKeys[ia];
    String sb = sortKeys[ib];
    return sa.compareTo(sb);
  }

//  public boolean equals(Object object) {
//    return (object == this); //The same is executed in java.lang.Object, otherwise we have to override the hashcode method
//  }

}

