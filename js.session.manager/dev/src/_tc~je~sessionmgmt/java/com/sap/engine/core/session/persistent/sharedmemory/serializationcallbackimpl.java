package com.sap.engine.core.session.persistent.sharedmemory;

import com.sap.jvm.session.SerializationCallback;
import com.sap.jvm.session.SharedSessionChunk;
import com.sap.engine.core.session.failover.DefFailoverConfiguration;

public class SerializationCallbackImpl extends SerializationCallback {

  protected SerializationCallbackImpl(){
    super();
  }

  public Object annotateClass(SharedSessionChunk chunk, java.lang.Class cl) {
    ClassLoader loader = cl.getClassLoader();
    String loaderName = null;
    if (loader != null) {
      loaderName = DefFailoverConfiguration.loadContext.getName(loader);
    }
    if (loaderName != null) {
      return loaderName;
    } else {
      return "NoName";
    }
  }

  public Object annotateProxyClass(SharedSessionChunk chunk, java.lang.Class cl) {

    ClassLoader loader = cl.getClassLoader();
    String loaderName = null;

    if (loader != null) {
      loaderName = DefFailoverConfiguration.loadContext.getName(loader);
    }
    if (loaderName != null) {
      return loaderName;
    } else {
      return "NoName";
    }
  }

  public Object replaceObject(SharedSessionChunk chunk, java.lang.Object obj) {
    return obj;
  }
}
