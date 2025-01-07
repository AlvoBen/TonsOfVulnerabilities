package com.sap.engine.lib.xsl.xslt.output;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import com.sap.engine.lib.xml.parser.helpers.Attribute;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

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
final class AttributeSorterCA implements Comparator {

  private int capacity;
  private Vector attributes = null;
  private Integer[] ordinals;
  private Integer[] permutation;
  private CharArray[] sortKeys = null;

  AttributeSorterCA(int initialCapacity) {
    ordinals = new Integer[initialCapacity];
    permutation = new Integer[initialCapacity];

    for (int i = 0; i < initialCapacity; i++) {
      ordinals[i] = new Integer(i);
    } 

    sortKeys = new CharArray[initialCapacity];
    capacity = initialCapacity;
  }

  private void ensureCapacity(int n) {
    if (n > capacity) {
      Integer[] oldOrdinals = ordinals;
      ordinals = new Integer[n];
      permutation = new Integer[n];
      sortKeys = new CharArray[n];
      System.arraycopy(oldOrdinals, 0, ordinals, 0, capacity);

      for (int i = capacity; i < n; i++) {
        ordinals[i] = new Integer(i);
      } 

      capacity = n;
    }
  }

  void process(Vector attributes, boolean performSort, boolean useLocalNamesAsKeys) {
    this.attributes = attributes;
    int n = attributes.size();
    ensureCapacity(n);
    System.arraycopy(ordinals, 0, permutation, 0, n);

    if (performSort) {
      if (useLocalNamesAsKeys) {
        for (int i = 0; i < n; i++) {
          Attribute attribute = (Attribute) attributes.get(i);
          CharArray k = attribute.crLocalName;
          sortKeys[i] = ((k == null) || k.equals(CharArray.EMPTY)) ? attribute.crRawName : k;
        } 
      } else {
        for (int i = 0; i < n; i++) {
          Attribute attribute = (Attribute) attributes.get(i);
          sortKeys[i] = attribute.crRawName;
        } 
      }

      Arrays.sort(permutation, 0, n, this);
    }
  }

  CharArray getURI(int index) {
    return ((Attribute) attributes.get(index)).crUri;
  }

  CharArray getLocalName(int index) {
    return ((Attribute) attributes.get(index)).crLocalName;
  }

  CharArray getQName(int index) {
    return ((Attribute) attributes.get(index)).crRawName;
  }

  CharArray getValue(int index) {
    return ((Attribute) attributes.get(index)).crValue;
  }

  public int compare(Object a, Object b) {
    int ia = ((Integer) a).intValue();
    int ib = ((Integer) b).intValue();
    CharArray sa = sortKeys[ia];
    CharArray sb = sortKeys[ib];
    return sa.compareTo(sb);
  }

}

