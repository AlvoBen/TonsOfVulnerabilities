/*
 * Created on 2004.11.30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.test;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.sap.engine.cache.util.Serializator;
import com.sap.engine.cache.util.dump.DumpWriter;
import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestSerializator {

  public static void main(String[] args) {
    ClassLoader a = null;
    ClassLoader b = null;
    Object aO = null;
    Object bO = null;

    try {
      a = new URLClassLoader(new URL[] {new URL("file:///K:/NWDI/workspace.jdi/2/DCs/sap.com/tc/bl/iqlib/_comp/gen/default/public/impl/lib/java/sap.com~tc~bl~iqlib~impl.jar")}, null);
//      Class aC = a.loadClass("com.sap.engine.lib.util.HashMapObjectObject"); This line might cause problems although not exactly in this case, see http://bugs.sun.com/view_bug.do?bug_id=6500212
      Class aC = Class.forName("com.sap.engine.lib.util.HashMapObjectObject", false, a);
      aO = aC.newInstance();
      Method put = aC.getMethod("put", new Class[] {Object.class, Object.class});
      put.invoke(aO, new Object[] {"example_map_key", "value"});
    } catch (MalformedURLException e) {
      LogUtil.logT(e);
    } catch (InstantiationException e) {
      LogUtil.logT(e);
    } catch (IllegalAccessException e) {
      LogUtil.logT(e);
    } catch (ClassNotFoundException e) {
      LogUtil.logT(e);
    } catch (SecurityException e) {
      LogUtil.logT(e);
    } catch (NoSuchMethodException e) {
      LogUtil.logT(e);
    } catch (IllegalArgumentException e) {
      LogUtil.logT(e);
    } catch (InvocationTargetException e) {
      LogUtil.logT(e);
    }
    
    try {
      b = new URLClassLoader(new URL[] {new URL("file:///K:/NWDI/workspace.jdi/2/DCs/sap.com/tc/bl/iqlib/_comp/gen/default/public/impl/lib/java/sap.com~tc~bl~iqlib~impl.jar")}, null);
    } catch (MalformedURLException e) {
      LogUtil.logT(e);
    }

    byte[] ba = null;
    try {
      ba = Serializator.toByteArray(aO);
    } catch (IOException e1) {
      LogUtil.logT(e1);
    }
    
    ClassLoader old = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(b);
    try {
      bO = Serializator.toObject(ba);
    } catch (StreamCorruptedException e2) {
      LogUtil.logT(e2);
    } catch (IOException e2) {
      LogUtil.logT(e2);
    } catch (ClassNotFoundException e2) {
      LogUtil.logT(e2);
    }
    Thread.currentThread().setContextClassLoader(old);
    
    DumpWriter.dump("ORIGINAL: " + aO);
    DumpWriter.dump("RESULT:   " + bO);
    DumpWriter.dump("-----------------------------------------");
    DumpWriter.dump("ORIGINAL loader: " + aO.getClass().getClassLoader());
    DumpWriter.dump("RESULT loader:   " + bO.getClass().getClassLoader());
    
  }
 
}

class MySerializable implements Serializable {

  static final long serialVersionUID = 5234788911238774901L;
  
  int a = 50;
  
}
