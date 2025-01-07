package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4ExceptionConstants;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.server.P4ServerObjectInfo;
import com.sap.engine.interfaces.cross.StreamHook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Proxy;
import java.rmi.Remote;


/**
 * Stream for marshaled parameters of the requested method
 * in remote call.All functions use ObjectOutputStream
 * serialization methods
 *
 * @author Georgy Stanev
 * @version 7.0
 */
public class MarshalInputStream extends ObjectInputStream implements P4ObjectInput {

  private String connectionType;
  private ClassLoader classLoader;
  private ByteArrayInputStream arrayStream = null;
  private String underlyingProfile;
  private P4ObjectBroker broker = P4ObjectBroker.getBroker();
  private int remoteBrokerId = -1;

  private StreamHook streamHook;

  private boolean icm = false;

  public String getUnderlyingProfile() {
    return underlyingProfile;
  }

  public void setUnderlyingProfile(String underlyingProfile) {
    this.underlyingProfile = underlyingProfile;
  }

  public MarshalInputStream(ByteArrayInputStream _input) throws IOException, StreamCorruptedException {
    this(_input, P4ObjectBroker.DEFAULT_COMMUNICATION_LAYER);
  }

  public MarshalInputStream(ByteArrayInputStream _input, boolean icm) throws IOException, StreamCorruptedException {
    this(_input, null);
    this.icm = icm;
  }
  public MarshalInputStream(ByteArrayInputStream _input, String _type) throws IOException, StreamCorruptedException {
    super(_input);
    enableResolveObject(true);
    this.connectionType = _type;
    this.arrayStream = _input;
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
            P4Logger.getLocation().pathT("MarshalInputStream.resolveClass(ObjectStreamClass)", "ClassNotFound when use streamHook.resolveClass for class: " + osc.getName());
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
          P4Logger.getLocation().pathT("MarshalInputStream.resolveClass(ObjectStreamClass)", "ClassNotFound when use Class.forName: " + osc.getName());
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("MarshalInputStream.resolveClass(ObjectStreamClass)", P4Logger.exceptionTrace(ex));
        }
        try {
          return Class.forName(osc.getName(), true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException cl) {
          if (P4Logger.getLocation().bePath()) {
            P4Logger.getLocation().pathT("MarshalInputStream.resolveClass(ObjectStreamClass)", "ClassNotFound when use Thread.getContextClassLoader: " + osc.getName());
          }
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("MarshalInputStream.resolveClass(ObjectStreamClass)", P4Logger.exceptionTrace(cl));
          }
          return classLoader.loadClass(osc.getName());
        }
      }
    }
  }

  protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    Class[] allClasses = (Class[]) readObject();
    ClassLoader cl = allClasses[0].getClassLoader();
    try {
      return Proxy.getProxyClass(classLoader, allClasses);
    } catch (IllegalArgumentException iaex) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("MarshalInputStream.resolveProxyClass(String[])", "Could not get proxy with the classloader of the steram.");
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("MarshalInputStream.resolveProxyClass(String[])",  P4Logger.exceptionTrace(iaex));
      }
      try {
        return Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), allClasses);
      } catch (IllegalArgumentException iaexx) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("MarshalInputStream.resolveProxyClass(String[])", "Could not get proxy with Thread.getContextClassLoader");
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("MarshalInputStream.resolveProxyClass(String[])",  P4Logger.exceptionTrace(iaexx));
        }
        return Proxy.getProxyClass(cl, allClasses);
      }
    }
  }

  public Object resolveObject(Object _obj) {
    try {
      if (_obj instanceof RemoteObjectInfo) {
        StubBaseInfo stubInfo = (_obj instanceof StubBaseInfo) ? (StubBaseInfo) _obj : StubBaseInfo.makeStubBaseInfo((RemoteObjectInfo) _obj);
        if((_obj instanceof StubBaseInfo) || (_obj instanceof P4ServerObjectInfo)) {
           if (broker.isServerBroker()) {
             if (underlyingProfile != null) {
               for (int i = 0; i < stubInfo.connectionProfiles.length; i++) {
                 if (stubInfo.connectionProfiles[i].toString().equals(underlyingProfile)) {
                  stubInfo.setIncomingProfile(underlyingProfile);
                  break;
                 }
               }
             }
           } else {
             if ((underlyingProfile != null) && (stubInfo.ownerId == Integer.parseInt(underlyingProfile.substring(0, underlyingProfile.indexOf(':'))))) {
               stubInfo.setIncomingProfile(underlyingProfile);
             }
           }
        }
        Class stub = null;
        ClassLoader loader = null;
        for (int i = 0; i < ((RemoteObjectInfo) _obj).stubs.length; i++) {
          try {
            try {
              if (P4ObjectBroker.isEnabledStreamHooks() && (streamHook != null)) {
                try {   //try to load from application cashed classes
                  stub = streamHook.resolveClass(((RemoteObjectInfo) _obj).stubs[i]);
                  loader = stub.getClassLoader();
                } catch (ClassNotFoundException cnfex) {
                  if (P4Logger.getLocation().bePath()) {
                    P4Logger.getLocation().pathT("MarshalInputStream.resolveObject(Object)", "ClassNotFound when use streamHook.resolveClass: " + ((RemoteObjectInfo) _obj).stubs[i]);
                  }
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("MarshalInputStream.resolveObject(Object)", P4Logger.exceptionTrace(cnfex));
                  }
                }
              }
              if (stub == null) {
                stub = classLoader.loadClass(((RemoteObjectInfo) _obj).stubs[i]);
                loader = classLoader;
              }
            } catch (ClassNotFoundException cnf) {
              if (P4Logger.getLocation().bePath()) {
                P4Logger.getLocation().pathT("MarshalInputStream.resolveObject(Object)", P4Logger.exceptionTrace(cnf));
              }
              loader = Thread.currentThread().getContextClassLoader();
              stub = loader.loadClass(((RemoteObjectInfo) _obj).stubs[i]);
            }
            stubInfo.connected = false;
            Object result = broker.narrow(stubInfo, stub, connectionType, loader);
            if(result instanceof StubBaseInfo){
              result = broker.narrow(stubInfo, Remote.class, connectionType, loader);
            }
            return result;
          } catch (ClassNotFoundException cnfe) {
            P4Logger.getLocation().pathT("Resolving object in MarshalInputStream: Class " + stubInfo.stubs[i] + " not found.");
            P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "resolveObject", cnfe);
          }
        }
      } else if (_obj instanceof javax.rmi.CORBA.Stub) {
        try {
          ((javax.rmi.CORBA.Stub) _obj).connect(org.omg.CORBA.ORB.init());
        } catch (Exception e) {
          P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "resolveObject", e);
          ((javax.rmi.CORBA.Stub) _obj).connect(org.omg.CORBA.ORB.init(new String[0], null));
        }
      } else if (_obj instanceof P4ProxyWrapper) {
        ((P4ProxyWrapper) _obj).setRemoteBrokerId(this.getRemoteBrokerId());
        return ((P4ProxyWrapper) _obj).getCarriedClasses();
      } else if (_obj instanceof P4ClassWrapper) {
        ((P4ClassWrapper) _obj).setRemoteBrokerId(this.getRemoteBrokerId());
        return _obj;
      }
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("Resolving object in MarshalInputStream: Can't resolve object:" + _obj);
        P4Logger.getLocation().traceThrowableT(P4ExceptionConstants.SEVERITY_DEBUG, "resolveObject", ex);
      }
    }
    return _obj;
  }

  public Object readRemoteObject() throws IOException, ClassNotFoundException {
    return readObject();
  }

  public void close() throws IOException {
    try {
      super.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("MarshalInputStream.close()", P4Logger.exceptionTrace(ioex));
      }
    }
    try {
      arrayStream.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("MarshalInputStream.close()", P4Logger.exceptionTrace(ioex));
      }
    }
  }

  public void setConnectionType(String _type) {
    connectionType = _type;
  }

  public void setClassLoader(ClassLoader classloader) {
    this.classLoader = classloader;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public void setRemoteBrokerId(int remote_brokerId){
    this.remoteBrokerId = remote_brokerId;
  }

  public int getRemoteBrokerId(){
    return this.remoteBrokerId;
  }

}

