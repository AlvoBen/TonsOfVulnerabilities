/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.util;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Comparator;

/**
 *
 * @author  Ralitsa Bozhkova
 * @version  4.0
 */
public class RepositoryIDCacheTable implements Serializable {

  static final long serialVersionUID = -7680494716778716331L;
  public CompareByName comparator = new CompareByName(); //$JL-SER$
  private transient TableEntry table[];
  private transient int count;
//  private int threshold;
//  private float loadFactor;

  public RepositoryIDCacheTable() {
    this(7, 0.75F);
  }

  public RepositoryIDCacheTable(int i, float f) {
    if (i <= 0 || (double) f <= 0.0D) {
      throw new IllegalArgumentException("ID010020 Negative or zero parameter received");
    } else {
//      loadFactor = f;
      table = new TableEntry[i];
//      threshold = (int) ((float) i * f);
    }
  }

  public class TableEntry {

    int hash;
    Object key;
    Object value;
    TableEntry next;
    TableEntry previous;
    int size;

    //    protected Object clone() {
    //      TableEntry entry = new TableEntry();
    //      entry.hash = hash;
    //      entry.key = key;
    //      entry.value = value;
    //      entry.next = (next == null ? null : (TableEntry)next.clone());
    //      return entry;
    //    }

  }

  class CompareByName implements Comparator {

    public int compare(Object obj, Object obj1) {
      String s = ((Member) obj).getName();
      String s1 = ((Member) obj1).getName();
      return s.compareTo(s1);
    }

    public boolean equals(Object obj) {
      return (obj instanceof CompareByName);
    }

    public int hashCode() {
      return super.hashCode();
    }
  }

  private synchronized boolean equal(Object obj, Object obj1) {
    return obj.equals(obj1);
  }

  public synchronized Object get(Object _key) {
    int i = _key.hashCode();
    int j = (i & 0x7fffffff) % table.length;

    //    TableEntry previous = null;
    for (TableEntry tableEntry = table[j]; tableEntry != null; tableEntry = tableEntry.next) {
      if (tableEntry.hash == i && equal(tableEntry.key, _key)) {
        if (tableEntry.previous != null) {
          tableEntry.previous.next = tableEntry.next;
        } else {
          table[j] = tableEntry;
        }

        return tableEntry.value;
      }

      tableEntry.previous = tableEntry;
    }

    return null;
  }

  public synchronized Object put(Object _key, Object _value) {
    if (_value == null) {
      throw new NullPointerException("Null entry is not allowed in the table");
    }

    int i = _key.hashCode();
    int j = (i & 0x7fffffff) % table.length;

    for (TableEntry tableEntry = table[j]; tableEntry != null; tableEntry = tableEntry.next) {
      if (tableEntry.hash == i && equal(tableEntry.key, _key)) {
        Object obj = tableEntry.value;
        tableEntry.value = _value;
        return obj;
      }
    }

    //    if(count >= threshold) {
    //      rehash();
    //      return put(_key, _value);
    //    } else {
    TableEntry tentry = new TableEntry();
    tentry.hash = i;
    tentry.key = _key;
    tentry.value = _value;
    tentry.next = table[j];
    table[j] = tentry;
    count++;
    table[j].size++;
    return null;
    //    }
  }

}

