/*
 * Created on 2005.5.13
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
public class CommonKeyTransformer {
  
  private ObjectKeyTransformer transformer;

  protected String transformObject(Object key) {
    if (transformer == null) {
      return key.toString();
    } else {
      return transformer.transform(key);
    }
  }
  
  protected Map transformMap(Map keyToValue) {
    HashMap map = new HashMap();
    Iterator iter = keyToValue.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String stringKey = transformObject(entry.getKey());
      map.put(stringKey, entry.getValue());
    }
    return map;
  }

  protected Set transformSet(Set keySet) {
    HashSet set = new HashSet();
    Iterator iter = keySet.iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      String stringKey = transformObject(key);
      set.add(stringKey);
    }
    return set;
  }

  public void hookTransformer(ObjectKeyTransformer transformer) {
    this.transformer = transformer;
  }
  
  public Map reverseMap(Map original) {
    if (transformer == null) {
      return original;
    } else {
      if (transformer.reversible()) {
        HashMap map = new HashMap();
        Iterator iter = original.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry) iter.next();
          Object objectKey = transformer.reverse((String)entry.getKey());
          map.put(objectKey, entry.getValue());
        }
        return map;
      } else {
        return original;
      }
    }
  }

  public Set reverseSet(Set original) {
    if (transformer == null) {
      return original;
    } else {
      if (transformer.reversible()) {
        HashSet set = new HashSet();
        Iterator iter = original.iterator();
        while (iter.hasNext()) {
          Object key = iter.next();
          Object objectKey = transformer.reverse((String)key);
          set.add(objectKey);
        }
        return set;
      } else {
        return original;
      }
    }
  }


}
