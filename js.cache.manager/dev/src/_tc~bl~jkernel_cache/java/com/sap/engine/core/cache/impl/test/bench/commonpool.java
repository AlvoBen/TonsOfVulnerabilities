/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.core.cache.impl.test.bench;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonPool {
  
  private static int DIM = 1000;

  public static Object cachedObject = null;
  public static Object[] cachedObjectArray = null;
  
  public static String[] objectTypes = new String[] {
    "byte[0]",
    "java.lang.String  (the length is 128)",
    "byte[1024]",
    "java.util.HashSet (a hash set with 8 hash sets inside with 8 almost empty hash sets inside each)"
  };
  
  private static Object[] singleObject = new Object[4];
  private static Object[][] multiObjects = new Object[4][];

  public synchronized static void setObjectType(int index) {
    index--;
    if (singleObject[index] == null) { // not still initialized
      Object single = null;
      Object[] multi = new Object[DIM];
      switch (index) {
        case 0: // byte[0]
          single = new byte[0];
          for (int i = 0; i < DIM; i++) {
            multi[i] = new byte[0];
          }
          break;
        case 1: // String
          StringBuffer sb = new StringBuffer();
          sb.append(new char[128]);
          for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < 128; j++) sb.setCharAt(j, (char)(Math.random() * 26 + 65));
            multi[i] = sb.toString();
          }
          single = sb.toString();
          break;
        case 2: // byte[1024]
          single = new byte[1024];
          for (int i = 0; i < DIM; i++) {
            multi[i] = new byte[1024];
          }
          break;
        case 3: // Hashtable
          HashSet hash = null;
          for (int i = 0; i < DIM; i++) {
            hash = new HashSet();
            for (int j = 0; j < 8; j++) {
              HashSet tempHash = new HashSet();
              for (int k = 0; k < 8; k++) {
                HashSet innerHash = new HashSet();
                innerHash.add(new InternalObject());
                tempHash.add(innerHash);
              }
              hash.add(tempHash);
            }
            multi[i] = hash;
          }
          single = hash;
          break;
        default:
      }
      singleObject[index] = single;
      multiObjects[index] = multi;
    }
    cachedObject = singleObject[index];
    cachedObjectArray = multiObjects[index];
  }
  
  public static String toStringStatic() {
    String result = null;
    StringBuffer sb = new StringBuffer();
    sb.append("==================================================================");
    for (int i = 1; i <= objectTypes.length; i++) {
      sb.append("\n " + i + ". ");
      sb.append(objectTypes[i - 1]);
    }
    sb.append("\n==================================================================");
    result = sb.toString();
    return result;
  }
  
}

class InternalObject implements Serializable {
  
  static final long serialVersionUID = -246691821123751035L;
  
  public String toString() {
    return "";
  }
  
}
