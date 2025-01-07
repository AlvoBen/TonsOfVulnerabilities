package com.sap.engine.lib.xsl.xpath;

import java.util.*;

/**
 * Better than <tt>java.util.HashSet</tt> for small constant sets.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public class HashSetAsList extends HashSet {

  private int n;
  private Object[] a;
  private int[] h;

  public HashSetAsList() {
    this(10);
  }

  public HashSetAsList(int n) {
    this.n = n;
    a = new Object[n];
    h = new int[n];
  }

  public boolean contains(Object o) {
    int ho = o.hashCode();

    for (int i = 0; i < n; i++) {
      if (ho == h[i]) {
        if (a[i].equals(o)) {
          return true;
        }
      }
    } 

    return false;
  }

  public boolean add(Object o) {
    if (n <= a.length) {
      Object[] aOld = a;
      int[] hOld = h;
      a = new Object[2 * n];
      h = new int[2 * n];
      System.arraycopy(aOld, 0, a, 0, n);
      System.arraycopy(hOld, 0, h, 0, n);
    }

    a[n] = o;
    h[n] = o.hashCode();
    n++;
    return false;
  }

}

