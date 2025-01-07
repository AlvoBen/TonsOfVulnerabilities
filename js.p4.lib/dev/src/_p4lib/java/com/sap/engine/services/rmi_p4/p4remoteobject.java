package com.sap.engine.services.rmi_p4;


import java.rmi.*;
import java.rmi.NoSuchObjectException;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.Map;
import java.lang.ref.WeakReference;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class P4RemoteObject implements RemoteRef {

  public P4RemoteObjectInfo info;
  public Remote delegate;
  P4ObjectBroker broker = P4ObjectBroker.init();
  private boolean disconnected;

  private Map exportedIiopStubs = Collections.synchronizedMap(new WeakHashMap(2));

  public P4RemoteObject() {
    disconnected = false;
    P4ObjectBroker.init().connect(this);

    if (Remote.class.isAssignableFrom(getClass())) {
      this.delegate = (Remote) this;
    }
  }

  public P4RemoteObjectInfo getInfo() {
    return this.info;
  }

  public RemoteObjectInfo getObjectInfo() {
    return this.info;
  }

  public void setInfo(P4RemoteObjectInfo info) {
    this.info = info;
  }

  public void setDelegate(java.rmi.Remote rem) {
    delegate = rem;
  }

  public Remote getDelegate() {
    if (!disconnected) {
      return delegate;
    } else {
      throw new P4RuntimeException("Object is disconnected");// broker.getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Object_is_Disconnected, null);
    }
  }

  public Remote delegate() throws java.rmi.NoSuchObjectException {
    if (!disconnected) {
      return delegate;
    } else {
      throw new java.rmi.NoSuchObjectException("Object is disconnected");
    }
  }


  protected void setDisconnected() {
    disconnected = true;
  }

  protected void setConnected() {
    disconnected = false;
  }

  public void checkPermission(String operationName) throws SecurityException {
  }

  public void newIIOPExportedStub(Object stub) {
    exportedIiopStubs.put(stub, null);
  }

  public void clearIIOPExportedStubs() {
   Set keySet = exportedIiopStubs.keySet();
    synchronized(exportedIiopStubs) {
      Iterator iterator = keySet.iterator();
      while (iterator.hasNext()) {
        Remote stub = (Remote) iterator.next();
        if (stub != null) {
          try {
            javax.rmi.CORBA.Util.unexportObject(stub);
          } catch (NoSuchObjectException e) {
            //$JL-EXC$ the object is not exported.
          }
        }
      }
      exportedIiopStubs.clear();
    }
  }

  protected void finalize() throws Throwable { //$JL-FINALIZE$
    super.finalize();
    if (!P4ObjectBroker.getBroker().useReiterationOfGC) {
      broker.disconnect(this);
    }
  }

}

