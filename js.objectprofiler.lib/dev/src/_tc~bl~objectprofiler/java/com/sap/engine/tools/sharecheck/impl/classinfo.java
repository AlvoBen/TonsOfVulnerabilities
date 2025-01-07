package com.sap.engine.tools.sharecheck.impl;

import java.util.*;
import java.util.logging.*;
import java.io.Serializable;

import com.sap.engine.tools.sharecheck.*;

public class ClassInfo {

  static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.ClassInfo");

  static HashMap definedShareableMap = new HashMap(29);

  HashMap classToInfo = new HashMap();


  public ClassInfo() {
  }

  /**
   * Could be null if not cached.
   * @param clazz c
   * @return r
   */
  public Integer findCachedInfo(Class clazz) {
    return (Integer) classToInfo.get(clazz);
  }

  public int analyze(Class clazz) {
    Integer flagI = (Integer) classToInfo.get(clazz);
    int flag = -1;

    // If shareability flag is not found in the cache
    if (flagI == null) {
      try {
        flag = analyze0(clazz);
        flagI = new Integer(flag);
      } catch (RuntimeException x) {
        flag = SessionSerializationReport.NON_SHAREABLE_OTHER;
        classToInfo.put(clazz, flagI);
        throw x;
      }
      classToInfo.put(clazz, flagI);
    } else {
      flag = flagI.intValue();
    }

    return flag;
  }

  private int analyze0(Class clazz) {
    int flag = 0;

    if (definedShareableMap.containsKey(clazz)) {
      return SessionSerializationReport.DEFINED_SHAREABLE;
    }

    if (!Serializable.class.isAssignableFrom(clazz)) {
      flag |= SessionSerializationReport.NON_SERIALIZABLE_CLASS;
    }

    if (clazz.isPrimitive()) {
      return SessionSerializationReport.DEFINED_SHAREABLE;
    }

    return flag;
  }

  public String toString() {
    return "[ClsssInfo] ";
  }


}
