package com.sap.engine.lib.xsl.xpath;

import java.util.Hashtable;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class HashtableAsList extends Hashtable {

  private int n;
  private Object[] keys;
  private Object[] values;
  private int[] hashCodes;

  public HashtableAsList() {
    this(10);
  }

  public HashtableAsList(int l) {
    n = 0;
    keys = new Object[l];
    values = new Object[l];
    hashCodes = new int[l];
  }

  public Object put(Object k, Object v) {
    if (n == keys.length) {
      resize();
    }

    keys[n] = k;
    values[n] = v;
    hashCodes[n] = k.hashCode();
    n++;
    return null;
  }

  public boolean contains(Object o) {
    return containsKey(o);
  }

  public boolean containsKey(Object k) {
    int h = k.hashCode();

    for (int i = 0; i < n; i++) {
      if (hashCodes[i] == h) {
        if (keys[i].equals(k)) {
          return true;
        }
      }
    } 

    return false;
  }

  public boolean containsValue(Object v) {
    for (int i = 0; i < n; i++) {
      if (values[i].equals(v)) {
        return true;
      }
    } 

    return false;
  }

  public Object get(Object k) {
    int h = k.hashCode();

    for (int i = 0; i < n; i++) {
      if (hashCodes[i] == h) {
        if (keys[i].equals(k)) {
          return values[i];
        }
      }
    } 

    return null;
  }

  private void resize() {
    Object[] keysOld = keys;
    Object[] valuesOld = values;
    int[] hashCodesOld = hashCodes;
    int l = keys.length;
    keys = new Object[l * 2];
    values = new Object[l * 2];
    hashCodes = new int[l * 2];
    System.arraycopy(keysOld, 0, keys, 0, l);
    System.arraycopy(valuesOld, 0, values, 0, l);
    System.arraycopy(hashCodesOld, 0, hashCodes, 0, l);
  }

}

