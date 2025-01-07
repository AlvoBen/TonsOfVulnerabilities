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
package com.sap.engine.rmic.iiop.util;


import com.sap.engine.rmic.log.RMICLogger;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 *
 * @author  Ralitsa Bozhkova
 * @version  4.0
 */
public class RepositoryIDCacheTable implements Serializable {

  static final long serialVersionUID = -7680494716778716331L;

  public CompareByName comparator = new CompareByName();//$JL-SER$
  private transient TableEntry table[];
  private transient int count;
//  private int threshold;
//  private float loadFactor;
  //  private static final long serialVersionUID = -1940337748967564339;
//  private static final short ENTRY = 0;
  private static final short KEY = 1;
//  private static final short VALUE = 2;
  private static final int PERCENT_FOR_MEMORY_LEVEL = 20;
  private int memLevel;

  public RepositoryIDCacheTable() {
    this(7, 0.75F);
  }

  public RepositoryIDCacheTable(int i) {
    this(i, 0.75F);
  }

  public RepositoryIDCacheTable(int i, float f) {
    if (i <= 0 || (double) f <= 0.0D) {
      throw new IllegalArgumentException("ID010020");
    } else {
//      loadFactor = f;
      table = new TableEntry[i];
//      threshold = (int) ((float) i * f);
      return;
    }
  }

  public class TableEntry {

    int hash;
    Object key;
    Object value;
    TableEntry next;
    TableEntry previous;
    int size;

  }

  class CompareByName implements Comparator {

    public int compare(Object obj, Object obj1) {
      String s = ((Member) obj).getName();
      String s1 = ((Member) obj1).getName();
      return s.compareTo(s1);
    }

    public int hashCode(){
      return super.hashCode();
    }

    public boolean equals(Object obj) {
      return obj.equals(this);
    }

  }

  class TableEnumerator implements Enumeration {

    short mode;
    int index;
    TableEntry[] table;
    TableEntry entry;

    TableEnumerator(TableEntry[] tentry, short modes) {
      table = tentry;
      mode = modes;
      index = tentry.length;
    }

    public boolean hasMoreElements() {
      if (entry != null) {
        return true;
      }

      while ((index--) > 0) {
        if ((entry = table[index]) != null) {
          return true;
        }
      }

      return false;
    }

    public Object nextElement() {
      if (entry == null) {
        while ((index--) > 0 && (entry = table[index]) == null) {
          ;
        }
      }

      if (entry != null) {
        TableEntry tentry = entry;
        entry = tentry.next;

        switch (mode) {
          case 0: // '\0'
          {
            return tentry;
          }
          case 1: // '\001'
          {
            return tentry.key;
          }
          case 2: // '\002'
          {
            return tentry.value;
          }
        }
      }

      throw new NoSuchElementException("ID010021: TableEnumerator");
    }

  }

  private synchronized boolean equal(Object obj, Object obj1) {
    return obj.equals(obj1);
  }

  public synchronized Enumeration keys() {
    return new TableEnumerator(table, KEY);
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
      throw new NullPointerException();
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

  public int size() {
    return count;
  }

  public void free(int level, boolean fromMemory) {
    try {
      if (!fromMemory || level < 2 || level < memLevel) {
        return;
      }

      int oldCount = count;
      count = oldCount * (5 - level) * PERCENT_FOR_MEMORY_LEVEL / 100;
      synchronized (this) {
        for (int i = 0; i < table.length; i++) {
          if (table[i] == null) {
            continue;
          }

          int oldSize = table[i].size;
          int newSize = oldSize * (5 - level) * PERCENT_FOR_MEMORY_LEVEL / 100;
          TableEntry tableEntry = table[i];
          tableEntry.size = newSize;

          for (int j = 0; j < newSize; j++) {
            tableEntry = tableEntry.next;
          }

          tableEntry.next = null; // The GarbageCollector will do the rest
          table[i] = tableEntry;
        }
      }
      memLevel = level;
    } catch (Exception ex) {  //$JL-EXC$
      RMICLogger.throwing(ex);
    }
  }

}

