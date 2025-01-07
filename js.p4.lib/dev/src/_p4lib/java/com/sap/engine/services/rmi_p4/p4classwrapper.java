package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.client.ClientException;
import com.sap.engine.frame.client.ClientFactory;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.frame.core.thread.Transferable;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.services.rmi_p4.classload.DynamicClassLoader;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.HashMap;

public class P4ClassWrapper implements Serializable { //$JL-SER$ read and write object methods are not neccessary both here

  static final long serialVersionUID = -7663094581799919698L;

  public static final String SECURITY_CO = "security";                                                        // name of security contect object

  private static final HashMap<String, Class> primClasses = new HashMap<String, Class>(8, 1.0F);

  public static final boolean TO_USE_CLASS_WRAPPERS;   //workaround fo improvements in SPECJ   //SKIP_CLASS_WRAPPERS property

  static {
   primClasses.put("boolean", boolean.class);
   primClasses.put("byte", byte.class);
   primClasses.put("char", char.class);
   primClasses.put("short", short.class);
   primClasses.put("int", int.class);
   primClasses.put("long", long.class);
   primClasses.put("float", float.class);
   primClasses.put("double", double.class);
   primClasses.put("void", void.class);

   String sysProp = System.getProperty("SKIP_CLASS_WRAPPERS");
   TO_USE_CLASS_WRAPPERS = ((sysProp == null) || sysProp.equalsIgnoreCase("false"));
 }



  public static Hashtable hastLoader = new Hashtable();
  private String className = null;
  private transient Class cl = null;
  private String classLoaderName = null;
  private String[] urlList = null;
  private String[] hosts = null;
  private transient ClassLoader loader = null;
  private transient URLClassLoader urlLoader = null;
  private boolean isServer = false;
  private boolean isPrepared = false;
  transient P4ObjectBroker broker = null;
  private transient int remote_brokerId = -1;
  private StubImpl currentStub = null;
  private StubBaseInfo info = null;

  public P4ClassWrapper(Class cl) {
    if (cl != null) {
      this.cl = cl;
      this.className = cl.getName();
      try{
        if(P4ObjectBroker.init().getClass().getName().equals(P4ObjectBroker.SERVER_BROKER_CLASS)){
          if (this.cl.getClassLoader() != null) {
            this.classLoaderName = P4ObjectBroker.init().getServiceContext().getCoreContext().getLoadContext().getName(this.cl.getClassLoader());
          }
        }
      } catch (Throwable e) {
        if(P4Logger.getLocation().beDebug()){
          P4Logger.getLocation().debugT("P4ClassWrapper - problem in constructor : " + e.getMessage() + " : " + P4Logger.exceptionTrace(e));
        }
      }
    }

  }

  private boolean isServer() {
    if (P4ObjectBroker.getBroker().isServerBroker()) {
      this.isServer = true;
      this.hosts = P4ObjectBroker.init().getHttp();
    } else {
      this.isServer = false;
    }
    return isServer;
  }

  /**
   * prepare class for serialization
   */
  public void prepare() {
    if(!isPrepared){
      try {
        if (this.className != null && !isPrepared && cl != null) {
          P4ObjectBroker.init();
          this.isServer();
          loader = cl.getClassLoader();
          if (loader == null) {
            return;
          }
          //arrange with current ClassLoader
          if (isServer) {
            LoadContext lc =  P4ObjectBroker.init().getServiceContext().getCoreContext().getLoadContext();
            this.classLoaderName = lc.getName(loader);
          }
        }
      } finally {
        //set - this method was invoked
        isPrepared = true;
        this.cl = null;
      }
    }
  }

  public void setClassLoaderName(String loaderName) {
    this.classLoaderName = loaderName;
  }

  /**
   * deserializing method
   *
   * @param ois input stream for reading and deserializing
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

    ois.defaultReadObject();
    if (ois instanceof MarshalInputStream) {
      this.loader = ((MarshalInputStream) ois).getClassLoader();
    }
    loadClass();
    //this.cl = (Class)ois.readObject();

  }


  private void loadClass() throws ClassNotFoundException {
    this.cl = primClasses.get(this.className);
    if (cl != null) {
        return;
    }
    if (isServer() && (this.classLoaderName != null) && (this.className != null)) {
      try {
        this.cl = Class.forName(this.className, true, P4ObjectBroker.init().getServiceContext().getCoreContext().getLoadContext().getClassLoader(classLoaderName));
        return;
      } catch (Error e) { //$JL-EXC$ here may be ERROR
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " with classloader : " + classLoaderName + " <> " + P4Logger.exceptionTrace(e));
        }
      } catch (Exception e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " with classloader : " + classLoaderName + " <> " + P4Logger.exceptionTrace(e));
        }
      }
    }
    if ((this.loader != null) && this.loader.getClass().getName().equals("com.sap.engine.services.rmi_p4.classload.DynamicClassLoader")) {
      ((DynamicClassLoader)this.loader).setServerLoaderName(this.classLoaderName);
    }
    try {
      if (this.loader == null) {
        this.cl = Class.forName(this.className);
      } else {
        if (this.info != null) {
          this.info.setServer_classLoaderName(this.classLoaderName);
        }
        this.cl = Class.forName(this.className, true, this.loader);
      }
    } catch (ClassNotFoundException ex ) { //$JL-EXC$ here may be ERROR
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
          try {
            this.cl = contextLoader.loadClass(this.className);
          } catch (Error e) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " " + P4Logger.exceptionTrace(e));
            }
          } catch (Exception e) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " " + P4Logger.exceptionTrace(e));
            }
          }
        }
    } catch (Error e) {    //$JL-EXC$ here may be ERROR
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " " + P4Logger.exceptionTrace(e));
      }
    } catch (Exception e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ClassWrapper.loadClass()", "Unsuccessful try to load Class : " + className + " " + P4Logger.exceptionTrace(e));
      }
    }
  }

  public String getTicket() {
    try {
      Object trans = (ClientFactory.getThreadContextFactory().getThreadContext().getContextObject(SECURITY_CO));
      if (trans != null) {
        byte[] result = null;
        try {
          if(trans instanceof TransferableExt){
            result = new byte[((TransferableExt)trans).size(this.remote_brokerId)];//this.cc)];
            ((TransferableExt)trans).store(this.remote_brokerId, result, 0);
          } else {
            result = new byte[((Transferable)trans).size()];//this.cc)];
            ((Transferable)trans).store(result, 0);
          }
        } catch (Exception e) {
          if (P4Logger.getSecLocation().beInfo()) {
            P4Logger.getSecLocation().infoT("P4ClassWrapper.getTicket()", "Can not get security context object: "  + P4Logger.exceptionTrace(e));
          } else {
            if (P4Logger.getLocation().beInfo()) {
              P4Logger.getLocation().infoT("P4ClassWrapper.getTicket()", "Can not get security context object: "  + P4Logger.exceptionTrace(e));
            }
          }
        }
        BigInteger big = new BigInteger(1, result);
        return big.toString(16);
      }
    } catch (ClientException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4ClassWrapper.getTicket()", "Can not get ticket: "  + P4Logger.exceptionTrace(e));
      }
      return null;
    }
    return null;

  }

  public void setClassName(String className) {
    if (this.info != null) {
      this.info.setServer_classLoaderName(this.classLoaderName);
    }
    this.className = className;
  }

  public Class getCarriedClass() throws ClassNotFoundException {
    if (this.className == null) {
      return null;
    }
    if(this.cl != null){
      return this.cl;
    }
    //System.err.println("P4ClassWrapper.getCarriedClass  class name : " + className + "<> loader : " + loader + " <> loader name : " + this.classLoaderName);
    //if (SystemProperties.getProperty(P4_CLASSLOADING, "").equals(P4_CONNECTION) && loader != null && loader instanceof DynamicClassLoader) {
    if(this.loader.getClass().getName().equals("com.sap.engine.services.rmi_p4.classload.DynamicClassLoader")){
      if(this.classLoaderName != null){
        ((DynamicClassLoader)this.loader).setServerLoaderName(this.classLoaderName);
      }
    }

    try {
      return loader.loadClass(this.className);
    } catch (ClassNotFoundException ex ) { //$JL-EXC$ here may be ERROR
      return primClasses.get(this.className);
    }
  }

  public void setURLList(String[] urlList) {
    this.urlList = urlList;
  }

  public ClassLoader getLoader() {
    return urlLoader;
  }

  public String[] getURLList() {
    return this.urlList;
  }

  public String getURL(int index) {
    return this.urlList[index];
  }

  public void setRemoteBrokerId(int remote_brokerId){
    this.remote_brokerId = remote_brokerId;
  }

  public int getRemoteBrokerId(){
    return this.remote_brokerId;
  }

  public void setStub(StubImpl stub){
    this.currentStub = stub;
    if(this.currentStub != null){
      this.info = this.currentStub.p4_getInfo();
    }
  }

  public void setInfo(StubBaseInfo info){
    this.info = info;
  }
}