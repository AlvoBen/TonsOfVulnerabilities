package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.StreamHook;

import java.io.*;
import java.lang.reflect.Proxy;
import java.rmi.Remote;

/**
 * Stream for marshaled parameters of the requested method
 * in remote call.All functions use ObjectOutputStream
 * serialization methods
 *
 * @author Nickolay Neychev, Mladen Droshev
 * @version 7.0
 */
public class ReplicateInputStream extends ObjectInputStream {

  private ByteArrayInput bai = null;
  private ClassLoader classLoader;
  private String connectionType;
  private P4ObjectBroker broker = P4ObjectBroker.init();

  private StreamHook streamHook;

  ReplicateOutputStream out = null;

  public ReplicateInputStream(ByteArrayInput bai0) throws IOException, StreamCorruptedException {
    super(bai0);
    enableResolveObject(true);
    bai = bai0;
  }

  public ReplicateInputStream(ByteArrayInput bai0, ReplicateOutputStream out) throws IOException, StreamCorruptedException {
    this(bai0);
    this.out = out;
  }

  public void setHook(StreamHook streamHook) {
    this.streamHook = streamHook;
  }

  protected Class resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
    if (osc.getName().equals("int")) {
      return int.class;
    } else if (osc.getName().equals("byte")) {
      return byte.class;
    } else if (osc.getName().equals("boolean")) {
      return boolean.class;
    } else if (osc.getName().equals("char")) {
      return char.class;
    } else if (osc.getName().equals("short")) {
      return short.class;
    } else if (osc.getName().equals("long")) {
      return long.class;
    } else if (osc.getName().equals("double")) {
      return double.class;
    } else if (osc.getName().equals("float")) {
      return float.class;
    } else {
      if (P4ObjectBroker.isEnabledStreamHooks() && (streamHook != null)) {
        try {   //try to load from application class loader
          return streamHook.resolveClass(osc.getName());
        } catch (ClassNotFoundException cnfex) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("MarshalInputStream.resolveClass(ObjectStreamClass)", "ClassNotFound when use streamHook.resolveClass :" + osc.getName());
          }
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("MarshalInputStream.resolveClass(ObjectStreamClass)", P4Logger.exceptionTrace(cnfex));
          }
        }
      }
      try {
        return Class.forName(osc.getName(), true, classLoader);
      } catch (ClassNotFoundException ex) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ReplicateInputStream.resolveClass(ObjectStreamClass)", "Cannot resolve class " + osc.getName() + " with current Class.forName(objectStreamClass.getName(), true, classLoader)");
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ReplicateInputStream.resolveClass(ObjectStreamClass)", P4Logger.exceptionTrace(ex));
        }
        try {
          return Thread.currentThread().getContextClassLoader().loadClass(osc.getName());
        } catch (ClassNotFoundException cl) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("ReplicateInputStream.resolveClass(ObjectStreamClass)", "Cannot resolve class " + osc.getName() + " with context classloader");
          }
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ReplicateInputStream.resolveClass(ObjectStreamClass)", P4Logger.exceptionTrace(cl));
          }
          return classLoader.loadClass(osc.getName());
        }
      }
    }
  }

  protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    Class[] interf = new Class[interfaces.length];
    try {
      for (int i = 0; i < interfaces.length; i++) {
        interf[i] = classLoader.loadClass(interfaces[i]);
      }
      return Proxy.getProxyClass(classLoader, interf);
    } catch (ClassNotFoundException cln) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ReplicateInputStream.resolveProxyClass(String[])", P4Logger.exceptionTrace(cln));
      }
      for (int i = 0; i < interfaces.length; i++) {
        interf[i] = Thread.currentThread().getContextClassLoader().loadClass(interfaces[i]);
      }
      return Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), interf);
    }
  }

  public Object resolveObject(Object _obj) {
    try {
      if (_obj instanceof RemoteObjectInfo) {
        StubBaseInfo stubInfo = (_obj instanceof StubBaseInfo) ? (StubBaseInfo) _obj : StubBaseInfo.makeStubBaseInfo((RemoteObjectInfo) _obj);
        _obj = stubInfo;
        Class stub = null;
        for (int i = 0; i < ((RemoteObjectInfo) _obj).stubs.length; i++) {
          try {
            try {
              if (P4ObjectBroker.isEnabledStreamHooks() && (streamHook != null)) {
                try {   //try to load from application cashed classes
                  stub = streamHook.resolveClass(((RemoteObjectInfo) _obj).stubs[i]);
                } catch (ClassNotFoundException cnfex) {
                  if (P4Logger.getLocation().bePath()) {
                    P4Logger.getLocation().pathT("MarshalInputStream.resolveObject(Object)", "ClassNotFound when use streamHook.resolveClass :" + ((RemoteObjectInfo) _obj).stubs[i]);
                  }
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("MarshalInputStream.resolveObject(Object)", P4Logger.exceptionTrace(cnfex));
                  }
                }
              }
              if (stub == null) {
                stub = classLoader.loadClass(((RemoteObjectInfo) _obj).stubs[i]);
              }
            } catch (ClassNotFoundException cnf) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("ReplicateInputStream.resolveObject(Object)", P4Logger.exceptionTrace(cnf));
              }
              stub = Thread.currentThread().getContextClassLoader().loadClass(((RemoteObjectInfo) _obj).stubs[i]);
            }
            Object result = P4ObjectBroker.init().narrow(stubInfo, stub, connectionType);
            if(result instanceof StubBaseInfo){
              result = P4ObjectBroker.init().narrow(stubInfo, Remote.class, connectionType);
            }
            return result;
          } catch (ClassNotFoundException cnfe) {
            P4Logger.getLocation().debugT("Resolving object in MarshalInputStream: Class " + stubInfo.stubs[i] + " not found.");
            P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "resolveObject", cnfe);
          }
        }
      }
    } catch (Exception ex) {
      P4Logger.getLocation().debugT("Resolving object in MarshalInputStream: Can't resolve object:" + _obj);
      P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "resolveObject", ex);
    }
    Object result = _obj;
    if(result instanceof StubBaseInfo){
      result = P4ObjectBroker.init().narrow(result, Remote.class, connectionType);
    }
    return result;
  }

  public void close() throws IOException {
    super.close();
  }

  public void setConnectionType(String _type) {
    connectionType = _type;
  }

  public void setClassLoader(ClassLoader classloader) {
    this.classLoader = classloader;
  }

  public void setArrayBuffer(byte[] buf) {
    bai.setBuffer(buf);
  }
}

