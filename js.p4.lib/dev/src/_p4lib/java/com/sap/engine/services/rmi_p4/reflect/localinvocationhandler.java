package com.sap.engine.services.rmi_p4.reflect;

import com.sap.engine.services.rmi_p4.ByteArrayInput;
import com.sap.engine.services.rmi_p4.ByteArrayOutput;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.engine.services.rmi_p4.RemoteObjectInfo;
import com.sap.engine.services.rmi_p4.RemoteRef;
import com.sap.engine.services.rmi_p4.StubBaseInfo;
import com.sap.engine.services.rmi_p4.ReplicateInputStream;
import com.sap.engine.services.rmi_p4.ReplicateOutputStream;
import com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.interfaces.LocalRemoteByRef;
import com.sap.engine.interfaces.cross.StreamHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.NoSuchObjectException;
import java.io.Serializable;
import java.io.ObjectStreamException;

public class LocalInvocationHandler extends AbstractInvocationHandler implements Serializable {

  static final long serialVersionUID = -2661297024497740256L;

  private static final Class REMOTE_REF_CLASS = RemoteRef.class;
  private static Method getObjectInfoMethod = null;

  static {
    try {
      getObjectInfoMethod = REMOTE_REF_CLASS.getMethod("getObjectInfo", null);
    } catch (NoSuchMethodException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("LocalInvocationHandler.invokeInternal(Method, Object[])", P4Logger.exceptionTrace(e));
      }
    } catch (SecurityException e) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("LocalInvocationHandler.invokeInternal(Method, Object[])", P4Logger.exceptionTrace(e));
      }
    }
  }

  private P4RemoteObject delegate = null; //$JL-SER$ class uses writeReplace method
  public StubBaseInfo info = null;
  private String delegateString = null;

  public LocalInvocationHandler(StubBaseInfo info) {
    this.info = info;
    this.delegate = P4ObjectBroker.init().getObject(info.key);
    if (delegate != null) {
      try {
        delegateString = delegate.delegate().toString();
      } catch (NoSuchObjectException nso) {
        delegate = null;
      }
    }
  }

  protected Object invokeInternal(Object proxyObject, Method method, Object[] args) throws Throwable {
    if (delegate == null) {
      throw new NoSuchObjectException("Object is disconnected");
    }
    if (REMOTE_REF_CLASS == method.getDeclaringClass() && method.equals(getObjectInfoMethod)) {
      return delegate.getObjectInfo();
    }
    Remote implementation = delegate.delegate();
    String methodName = method.getName();
    //ClassLoader interfaceClassLoader = method.getDeclaringClass().getClassLoader();
    ClassLoader interfaceClassLoader = proxyObject.getClass().getClassLoader();
    ClassLoader implClassLoader = implementation.getClass().getClassLoader();
    //delegate.checkPermission(methodName);
    Method current = null;
    try {
      current = implementation.getClass().getMethod(methodName, method.getParameterTypes()); // method loaded from the loader of impl
    } catch (Exception e) { // will try to replicate parameters with the implementation's classloader
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("LocalInvocationHandler.invokeInternal(Method, Object[])", P4Logger.exceptionTrace(e));
      }
      current = implementation.getClass().getMethod(methodName, (Class[]) replicateParameters(method.getParameterTypes(), implClassLoader)); // method loaded from the loader of impl
    }
    Object result = null;

    try {
      //result = method.invoke(implementation, (Object[])replicateParameters(args));

      /* check if the parameters are primitive */
// TODO - changed by i024079 
      if(!(implementation instanceof LocalRemoteByRef && P4ObjectBroker.isLocalCallOptimization()) && !arePrimitive(method.getParameterTypes())){
        args = (Object[]) replicateParameters(args, implClassLoader);
      }

      ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
      try {
        Thread.currentThread().setContextClassLoader(implementation.getClass().getClassLoader());
        result = current.invoke(implementation, args);
      } finally {
        Thread.currentThread().setContextClassLoader(currentLoader);
      }
    } catch (InvocationTargetException ite) {
      //$JL-EXC$
      Throwable th = null;	
      if (implementation instanceof LocalRemoteByRef && P4ObjectBroker.isLocalCallOptimization()) {
          th = ite.getTargetException();
      } else {
    	  th = (Throwable) replicateReturnValue(ite.getTargetException(), interfaceClassLoader); 
      }
      
      Class[] exceptions = method.getExceptionTypes();
      if (th instanceof RuntimeException) {
          throw th;
      }
      if (exceptions != null) {
        for (Class exception : exceptions) {
          if (exception.isAssignableFrom(th.getClass())) {
            throw th;
          }
        }
      }
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, th);
      //throw (Throwable) replicateReturnValue(ite.getTargetException(), interfaceClassLoader);
    }
    
 // TODO - changed by i024079    
    if(!(implementation instanceof LocalRemoteByRef && P4ObjectBroker.isLocalCallOptimization()) && !isPrimitive(method.getReturnType())) {
      result = replicateReturnValue(result, interfaceClassLoader);
      result = P4ObjectBroker.init().narrow(result, method.getReturnType());
    }
    return result;
  }

  protected Object replicateParameters(Object obj, ClassLoader implementationCL) {
    try {
      Remote impl = delegate.delegate();
      StreamHook stramHook =  (impl instanceof StreamHook) ? (StreamHook) impl : null;

      ByteArrayOutput bao = new ByteArrayOutput(0);
      ReplicateOutputStream out = new ReplicateOutputStream(bao);
      out.writeObject(obj);
      ReplicateInputStream in = new ReplicateInputStream(new ByteArrayInput(bao.getBuffer()), out);
      in.setHook(stramHook);
      in.setClassLoader(implementationCL);
      return in.readObject();
    } catch (Exception e) {
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, e, new Object[]{e.toString()});
    }
  }

  protected Object replicateReturnValue(Object obj, ClassLoader interfaceCL) {
    try {
      ByteArrayOutput bao = new ByteArrayOutput(0);
      ReplicateOutputStream out = new ReplicateOutputStream(bao);
      out.writeObject(obj);
      ReplicateInputStream in = new ReplicateInputStream(new ByteArrayInput(bao.getBuffer()), out);
      in.setClassLoader(interfaceCL);
      return in.readObject();
    } catch (Exception e) {
      throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Couldnt_initialize_stream, e, new Object[]{e.toString()});
    }
  }

  protected boolean equals_internal(Object base, Object asked) throws Throwable {
    if (delegate == null) {
      throw new NoSuchObjectException("Object is disconnected");
    }
    if ((asked instanceof Proxy)) {
      LocalInvocationHandler askHandler = (LocalInvocationHandler) Proxy.getInvocationHandler(asked);
      if (askHandler.delegate == null) {
        return false;
      }
      return delegate.delegate().equals(askHandler.delegate.delegate());
    } else {
      if (asked instanceof RemoteObjectInfo) {
        RemoteObjectInfo remoteInfo = (RemoteObjectInfo) asked;
        return (this.delegate.info != null && asked != null && java.util.Arrays.equals(this.delegate.info.key, remoteInfo.key) && java.util.Arrays.equals(this.delegate.info.connectionProfiles, remoteInfo.connectionProfiles));
      }
    }
    return false;
  }

  public String invokeToString(Object proxy) {
    String result = "RMI_P4: Local Dynamic Stub for impl -> " + delegateString;
    if (delegate != null && delegate.info != null) {
      result += "\r\n" + delegate.info;
    }
    return result;
  }

  private Object writeReplace() throws ObjectStreamException {
    InvocationHandlerProxy ihProxy = new InvocationHandlerProxy();
    if (delegate != null) {
      ihProxy.setLocalInfo(delegate.getObjectInfo());
    } else {
      ihProxy.setLocalInfo(info);
    }
    return ihProxy;
  }

  private boolean arePrimitive(Class[] args) {
    if (args == null) {
      return true;
    }
    boolean arePrimitive = true;
    for (Class arg : args) {
      arePrimitive = arePrimitive && isPrimitive(arg);
    }
    return arePrimitive;
  }

  private boolean isPrimitive(Class arg) {
    if(arg == null){
      return true;
    }
    if(arg.isPrimitive()){
      return true;
    } else if(arg.equals(String.class)){
      return true;
    } else if(arg.isArray()){
      return isPrimitive(arg.getComponentType());
    }
    return false;
  }

}