/*
 * Created on 2005.8.8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.core.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.util.cache.ObjectKeyTransformer;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrimitiveKeyTransformer implements ObjectKeyTransformer {
  
  public String transform(Object key) {
    return transformLong(((Long)key).longValue());
  }

  public String transformObject(Object key) {
    return transformLong(((Long)key).longValue());
  }

  public boolean reversible() {
    return true;
  }

  public Object reverse(String key) {
    return new Long(reverseLong(key));
  }

  private final static char[] digits = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

  public String transformLong(long i) {
    char[] buf = new char[16];
    buf[15] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 15, 1);
    buf[14] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 14, 2);
    buf[13] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 13, 3);
    buf[12] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 12, 4);
    buf[11] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 11, 5);
    buf[10] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 10, 6);
    buf[9] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 9, 7);
    buf[8] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 8, 8);
    buf[7] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 7, 9);
    buf[6] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 6, 10);
    buf[5] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 5, 11);
    buf[4] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 4, 12);
    buf[3] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 3, 13);
    buf[2] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 2, 14);
    buf[1] = digits[(int)(i & 0xf)]; i >>>=4; if (i == 0) return new String(buf, 1, 15);
    buf[0] = digits[(int)(i & 0xf)]; return new String(buf, 0, 16);
  }
  
  public long reverseLong(String key) {
    long result = 0;
    int i = 0;
    int max = key.length();
    int digit;
    while (i < max) {
      digit = key.charAt(i++);
      digit = digit > 57 ? digit - 87 : digit - 48;
      result <<= 4;
      result -= digit;
    }
    return -result;
  }
  
  /////////////////////////////////////
  
  protected Map transformMap(Map keyToValue) {
    HashMap map = new HashMap();
    Iterator iter = keyToValue.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String stringKey = transform(entry.getKey());
      map.put(stringKey, entry.getValue());
    }
    return map;
  }

  protected Set transformSet(Set keySet) {
    HashSet set = new HashSet();
    Iterator iter = keySet.iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      String stringKey = transform(key);
      set.add(stringKey);
    }
    return set;
  }

  public Map reverseMap(Map original) {
    HashMap map = new HashMap();
    Iterator iter = original.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object objectKey = reverse((String)entry.getKey());
      map.put(objectKey, entry.getValue());
    }
    return map;
  }

  public Set reverseSet(Set original) {
    HashSet set = new HashSet();
    Iterator iter = original.iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      Object objectKey = reverse((String)key);
      set.add(objectKey);
    }
    return set;
  }
  

}
