/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BenchResult {
  
  private Hashtable resultMap;
  
  public BenchResult() {
    resultMap = new Hashtable();
  }
  
  public synchronized String[] getKeys() {
    String[] keys = new String[resultMap.size()];
    TreeSet sorted = new TreeSet();
    sorted.addAll(resultMap.keySet());
    Iterator keysIterator = sorted.iterator();
    int counter = 0;
    while (keysIterator.hasNext()) {
      String current = (String) keysIterator.next();
      keys[counter] = current;
      counter++;
    }
    return keys;
  }
  
  public synchronized int getValue(String key) {
    return ((Integer)resultMap.get(key)).intValue();
  }
  
  public synchronized int putValue(String key, int value) {
    Integer temp = (Integer)resultMap.put(key, new Integer(value));
    return temp == null? -1 : temp.intValue();
  }
  
  public synchronized int removeValue(String key) {
    Integer temp = (Integer)resultMap.remove(key);
    return temp == null? -1 : temp.intValue();
  }

  public void addAll(BenchResult result) {
    String[] keys = result.getKeys();
    for (int i = 0; i < keys.length; i++) {
      putValue(keys[i], result.getValue(keys[i]));
    }
  }
  
  public int getSingleValue() {
    Iterator iterator = resultMap.values().iterator();
    return (iterator.hasNext()) ? ((Integer)iterator.next()).intValue() : -1;
  }
  
  static final String SPC = "                                                                              ";

  public void printResults() {
    System.out.println(toString());
  }
  
  public String toString() {
    String result = null;
    StringBuffer sb = new StringBuffer();
    String[] keys = getKeys();
    int totalSpc = -1;
    for (int i = 0; i < keys.length; i++) {
      if (totalSpc < keys[i].length()) {
        totalSpc = keys[i].length();
      }
    }
    sb.append("==================================================================");
    for (int i = 0; i < keys.length; i++) {
      sb.append("\n" + keys[i] + " : " + SPC.substring(0, totalSpc - keys[i].length()) + getValue(keys[i]));
    }
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;
  }

}
