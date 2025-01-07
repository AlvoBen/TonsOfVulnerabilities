package com.sap.engine.services.rmi_p4;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.server.Operation;
import java.util.*;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

public class P4DynamicSkeleton extends P4RemoteObject implements com.sap.engine.services.rmi_p4.SkeletonOpt, java.rmi.Remote {

  private Operation[] operations = null;
  private String[] _implements = null;
  private Class[] remoteInterfaces = {};
  private Hashtable remoteMethods = new Hashtable();
  private Class remote_class = null;
  private Object impl;
  public static String TRACE_MSG = "P4 Service : P4DynamicSkeleton remote implementation :-> ";

  public P4ObjectBroker broker = P4ObjectBroker.init();

  public P4DynamicSkeleton() {
  }

  public P4DynamicSkeleton(Object impl) {
    this.impl = impl;
    remote_class = impl.getClass();
    Vector temp_interfaces = new Vector();

    /* get remote Interfaces and methods */
    getRemoteInterfaces(remote_class, temp_interfaces);
    getRemoteMethods(temp_interfaces, remoteMethods);

    /* copy buffer into corresponding array */
    if (temp_interfaces.size() > 0) {
      remoteInterfaces = new Class[temp_interfaces.size()];
      temp_interfaces.copyInto(remoteInterfaces);
    }

    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT(TRACE_MSG + impl.getClass() + " : remote interfaces : " + temp_interfaces);
    }

    this._implements = new String[remoteInterfaces.length];
    for (int i = 0; i < remoteInterfaces.length; i++) {
      this._implements[i] = remoteInterfaces[i].getName();
    }
    this.getObjectInfo().stubs = this._implements;

    if (P4Logger.getLocation().beDebug()) {
      if (this.getObjectInfo().stubs != null) {
        for (int i = 0; i < this.getObjectInfo().stubs.length; i++) {
          P4Logger.getLocation().debugT(TRACE_MSG + "stub[" + i + "] = " + this.getObjectInfo().stubs[i]);
        }
      }
    }

    /* create Operation[] instance for all methods */
    generateOperations();

  }

  private void generateOperations() {
    Enumeration enumeration = remoteMethods.keys();

    ArrayList _ops = new ArrayList();
    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      _ops.add(new Operation(key));
      Method remote_method = (Method) remoteMethods.get(key);

      boolean isParamOpt = isOptimizableParams(remote_method);
      boolean isResultOpt = isOptimizableReturnType(remote_method);
      if (isParamOpt || isResultOpt) {
        if (isParamOpt && isResultOpt) {
          _ops.add(new Operation(P4ObjectBroker.BOTH_OPTIMIZED + key));
        } else if (isParamOpt) {
          _ops.add(new Operation(P4ObjectBroker.PARAM_OPTIMIZED + key));
        } else {
          _ops.add(new Operation(P4ObjectBroker.RESULT_OPTIMIZED + key));
        }
      }
    }
    this.operations = (Operation[]) _ops.toArray(new Operation[0]);
  }

  public Operation[] getOperations() {
    return operations;
  }

  public String[] getImplemntsObjects() {
    if (_implements == null) {
      return new String[0];
    }
    return _implements;
  }

  private void getRemoteInterfaces(Class _target, Vector interfaceBuffer) {
    Class[] temp_interfaces = null;

    /* _target is null when previous call _target is java.lang.Object or interface */
    if (_target != null) {

      /* get remote interfaces from supperclass */
      getRemoteInterfaces(_target.getSuperclass(), interfaceBuffer);
      temp_interfaces = _target.getInterfaces();

      for (Class temp_interface : temp_interfaces) {
        /* get remote inheriting interfaces from consecutive interface */
        if (update(temp_interface, interfaceBuffer)) {
          getAdditionalInterfaces(temp_interface, interfaceBuffer);
        }
      }
    }
  }

  private void getAdditionalInterfaces(Class _target, Vector interfaceBuffer) {

    /* _target is null when previous call _target is java.lang.Object or interface */
    if (_target != null) {
      Class[] temp_interfaces = _target.getInterfaces();
      boolean implementsRemoteExplicitly = false;
      Vector notRemoteInterfaces = new Vector();
      for (int k = 0; k < temp_interfaces.length; k++) {

        /* get remote inheriting interfaces from consecutive interface */
        if (temp_interfaces[k].equals(java.rmi.Remote.class)) {
          implementsRemoteExplicitly = true;
        } else if (!java.rmi.Remote.class.isAssignableFrom(temp_interfaces[k])) {
          notRemoteInterfaces.add(temp_interfaces[k]);
        }
      }

      if (implementsRemoteExplicitly) {
        for (int i = 0; i < notRemoteInterfaces.size(); i++) {
          if (!interfaceBuffer.contains(notRemoteInterfaces.elementAt(i))) {
            interfaceBuffer.add(notRemoteInterfaces.elementAt(i));
          }
        }
      }
    }

  }


  public boolean update(Class _interface, Vector buffer) {
    if (!java.rmi.Remote.class.isAssignableFrom(_interface)) {
      return false;
    }

    for (int i = 0; i < buffer.size(); i++) {
      if (_interface.isAssignableFrom((Class) buffer.elementAt(i))) {
        return false;
      }

      if (((Class) buffer.elementAt(i)).isAssignableFrom(_interface)) {
        buffer.removeElementAt(i--);
      }
    }
    buffer.addElement(_interface);
    return true;
  }

  /**
   * Gets methods from all interfaces in the Vector _target and puts them in the second parameter Hashtable methodBuffer.
   * the key of the Hashtable is string of method name and its parameters. Value is the Method object itself. 
   * @param _target
   * @param methodBuffer
   */
  public void getRemoteMethods(Vector _target, Hashtable methodBuffer) {
    for (int k = 0; k < _target.size(); k++) {
      Method[] methods = ((Class) _target.elementAt(k)).getMethods();
      String methodName = "";

      /* if methods is not in methodsBuffer - add */
      for (Method method : methods) {
        if ((method.getName()).startsWith("<clinit>")) {
          continue;
        }
        // remove package and class information from method.toString() ... so at the end there will be just the method name and its parameters.
        methodName = method.toString();
        methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
        methodName = method.getName() + methodName;

        if (!methodBuffer.contains(methodName)) {
          methodBuffer.put(methodName, method);
        }
      }
    }
  }

  public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws Exception {
    String methodName = operations[opnum].getOperation();
    P4ObjectInput in = null;
    DataOptInputStream din = null;

    /* check if for optimization - if the operation name is set as optmizable */
    boolean isParamsOptimized = false;
    boolean isResultOptmized = false;
    if (methodName.startsWith(P4ObjectBroker.BOTH_OPTIMIZED) || methodName.startsWith(P4ObjectBroker.PARAM_OPTIMIZED)) {
      isParamsOptimized = true;
    }
    if (methodName.startsWith(P4ObjectBroker.BOTH_OPTIMIZED) || methodName.startsWith(P4ObjectBroker.RESULT_OPTIMIZED)) {
      isResultOptmized = true;
    }

    if (isParamsOptimized || isResultOptmized) {
      methodName = methodName.substring(P4ObjectBroker.BOTH_OPTIMIZED.length());
    }

    if (isParamsOptimized) {
      din = call.getDataInputStream();
      din.setClassLoader(impl.getClass().getClassLoader());
    } else {
      in = call.getInputStream();
      ((MarshalInputStream) in).setClassLoader(impl.getClass().getClassLoader());
    }

    Method method = (Method) remoteMethods.get(methodName);
    Class[] mArgs = method.getParameterTypes();
    Object[] params = null;

    /* read arguments */
    if (mArgs != null && mArgs.length > 0) {
      if (isParamsOptimized) {

        /* reads the args from the DataOptInputStream  - Optimized*/
        params = readOptArgs(din, mArgs, methodName);
      } else {

        /* reads the args from the MarshalInputStream - Old version - not Optimized */
        params = readArgs(in, mArgs);
      }
    }

    /* remote method execution */
    Class returnType = method.getReturnType();
    Object result;
    try {
      result = method.invoke(this.impl, params);
    } catch (InvocationTargetException invEx) {
      if (invEx.getTargetException() instanceof Exception) {
        throw (Exception) invEx.getTargetException();
      } else {
        throw invEx;
      }
    }


    P4ObjectOutput out = null;
    DataOptOutputStream dout = null;
    if (isResultOptmized) {
      dout = call.getDataOutputStream();
    } else {
      out = call.getOutputStream();
    }

    /* write the result from the remote method invocation */
    if (returnType.getName().equals("void")) {
      return;
    } else {
      if (isResultOptmized) {
        writeOptResult(dout, returnType, result, methodName);
      } else {
        writeResult(out, returnType, result);
      }
    }

    if (isResultOptmized) {
      dout.flush();
    } else {
      out.flush();
    }
  }

  private boolean isOptimizableMethod(Method m) {
    if (m != null) {
      boolean opt = isOptimizableParams(m);
      opt &= isOptimizableReturnType(m);
      return opt;
    }
    return false;
  }

  private boolean isOptimizableParams(Method m) {
    if (m != null) {
      boolean opt = true;
      Class[] params = m.getParameterTypes();
      for (Class param : params) {
        opt &= isOptimizable(param);
      }
      return opt;
    }
    return false;
  }

  private boolean isOptimizableReturnType(Method m) {
    if (m != null) {
      Class retType = m.getReturnType();
      return isOptimizable(retType);
    }
    return false;
  }

  private boolean isOptimizable(Class _class) {
    String type = _class.getName().replace('$', '.');
    if (type.equals("boolean")) {
      return true;
    } else if (type.equals("byte")) {
      return true;
    } else if (type.equals("short")) {
      return true;
    } else if (type.equals("char")) {
      return true;
    } else if (type.equals("int")) {
      return true;
    } else if (type.equals("long")) {
      return true;
    } else if (type.equals("float")) {
      return true;
    } else if (type.equals("double")) {
      return true;
    } else if (type.equals("void")) {
      return true;
    } else if (type.equals("java.lang.String")) {
      return true;
    } else if (type.equals("[B")) { // stands for byte[] (but not for byte[][][])
      return true;
    } 
    return false;
  }

  private Object[] readOptArgs(DataOptInputStream din, Class[] mArgs, String opName) throws Exception {
    Object[] params = new Object[mArgs.length];
    for (int i = 0; i < mArgs.length; i++) {
      if (mArgs[i].equals(boolean.class)) {
        params[i] = (din.readBoolean());
      } else if (mArgs[i].equals(byte.class)) {
        params[i] = (din.readByte());
      } else if (mArgs[i].equals(short.class)) {
        params[i] = (din.readShort());
      } else if (mArgs[i].equals(char.class)) {
        params[i] = (din.readChar());
      } else if (mArgs[i].equals(float.class)) {
        params[i] = (din.readFloat());
      } else if (mArgs[i].equals(int.class)) {
        params[i] = (din.readInt());
      } else if (mArgs[i].equals(double.class)) {
        params[i] = (din.readDouble());
      } else if (mArgs[i].equals(long.class)) {
        params[i] = (din.readLong());
      } else if (mArgs[i].equals(byte[].class)) {
        params[i] = readByteArray(din);
      } else if (mArgs[i].equals(String.class)) {
        byte[] __obj = readByteArray(din);
        if (__obj == null) {
          params[i] = null;
        } else {
          params[i] = new String(__obj);
        }
      } else {
        throw new RuntimeException("Wrong called operation<" + opName + ">. Excepcted param[" + i + "]=" + mArgs[i] + " which cannot be read from DataStream! Check your P4Stub.");
      }
    }

    return params;
  }

  private void writeOptResult(DataOptOutputStream dout, Class returnType, Object result, String opName) throws Exception {
    if (returnType.isPrimitive()) {
      if (returnType.equals(boolean.class)) {
        dout.writeBoolean((Boolean) result);
      } else if (returnType.equals(byte.class)) {
        dout.writeByte((Byte) result);
      } else if (returnType.equals(short.class)) {
        dout.writeShort((Short) result);
      } else if (returnType.equals(char.class)) {
        dout.writeChar((Character) result);
      } else if (returnType.equals(float.class)) {
        dout.writeFloat((Float) result);
      } else if (returnType.equals(int.class)) {
        dout.writeInt((Integer) result);
      } else if (returnType.equals(double.class)) {
        dout.writeDouble((Double) result);
      } else if (returnType.equals(long.class)) {
        dout.writeLong((Long) result);
      } else {
        throw new RuntimeException("Wrong called operation <" + opName + ">. result type=" + returnType + " which cannot be written in DataStream!");
      }

    } else if (returnType.equals(byte[].class)) {
      writeByteArray(dout, (byte[]) result);
    } else if (returnType.equals(String.class)) {
      if(result == null){
        writeByteArray(dout, null);
      } else {
        writeByteArray(dout, ((String) result).getBytes());
      }
    } else {
      throw new RuntimeException("Wrong called operation <" + opName + ">. result type=" + returnType + " which cannot be written in DataStream!");
    }
  }

  private void writeResult(P4ObjectOutput out, Class returnType, Object result) throws Exception {
    if (returnType.isPrimitive()) {
      if (returnType.equals(boolean.class)) {
        out.writeBoolean((Boolean) result);
      } else if (returnType.equals(byte.class)) {
        out.writeByte((Byte) result);
      } else if (returnType.equals(short.class)) {
        out.writeShort((Short) result);
      } else if (returnType.equals(char.class)) {
        out.writeChar((Character) result);
      } else if (returnType.equals(float.class)) {
        out.writeFloat((Float) result);
      } else if (returnType.equals(int.class)) {
        out.writeInt((Integer) result);
      } else if (returnType.equals(double.class)) {
        out.writeDouble((Double) result);
      } else if (returnType.equals(long.class)) {
        out.writeLong((Long) result);
      }
    } else if (returnType.getName().equals("java.lang.Class")) {
      if (result != null) {
        if (P4ClassWrapper.TO_USE_CLASS_WRAPPERS) {
          out.writeObject(new com.sap.engine.services.rmi_p4.P4ClassWrapper((java.lang.Class) result));
        } else {
          out.writeObject(result);
        }
      } else {
        out.writeObject(null);
      }
    } else {
      out.writeObject(result);
    }
  }

  private Object[] readArgs(P4ObjectInput in, Class[] mArgs) throws Exception {
    Object[] params = new Object[mArgs.length];

    for (int i = 0; i < mArgs.length; i++) {
      if (mArgs[i].equals(boolean.class)) {
        params[i] = (in.readBoolean());
      } else if (mArgs[i].equals(byte.class)) {
        params[i] = (in.readByte());
      } else if (mArgs[i].equals(short.class)) {
        params[i] = (in.readShort());
      } else if (mArgs[i].equals(char.class)) {
        params[i] = (in.readChar());
      } else if (mArgs[i].equals(float.class)) {
        params[i] = (in.readFloat());
      } else if (mArgs[i].equals(int.class)) {
        params[i] = (in.readInt());
      } else if (mArgs[i].equals(double.class)) {
        params[i] = (in.readDouble());
      } else if (mArgs[i].equals(long.class)) {
        params[i] = (in.readLong());
      } else {
        params[i] = in.readObject();
        if ((params[i] != null) && (java.rmi.Remote.class.isAssignableFrom(params[i].getClass()))) {
          params[i] = P4ObjectBroker.init().narrow(params[i], mArgs[i]);
        }
      }
    }
    return params;
  }

  private void writeByteArray(com.sap.engine.services.rmi_p4.DataOptOutputStream out, byte[] data) throws java.io.IOException {
    try {
      if (data == null) {
        out.writeInt(-1);
      } else if (data != null && data.length == 0) {
        out.writeInt(0);
      } else {
        out.writeInt(data.length);
        out.write(data, 0, data.length);
      }
    } catch (java.io.IOException ioe) {
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + ". Problem while write byte[] in the stream : " + ioe.getMessage());
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);
      throw ioe;
    }
  }

  private byte[] readByteArray(com.sap.engine.services.rmi_p4.DataOptInputStream in) throws java.io.IOException {
    /*  read  param  as byte[] */
    byte[] buffer = null;
    try {
      int byteArrayLength = in.readInt();
      if (byteArrayLength == -1) {
        return null;
      } else if (byteArrayLength == 0) {
        return new byte[0];
      }
      if (byteArrayLength > 0) {
        buffer = new byte[byteArrayLength];
        int readed = 0;
        int offset = 0;
        while (readed != -1 && offset < byteArrayLength) {
          readed = in.read(buffer, offset, byteArrayLength - offset);
          offset += readed;
        }
      }
    } catch (java.io.IOException ioe) {
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + ". Problem while read byte[] from the stream : " + ioe.getMessage());
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);
      throw ioe;
    }
    return buffer;
  }

  public String toString() {
    String result = "P4DynamicSkeleton@" + Integer.toHexString(this.hashCode()) + " for implementaion: " + remote_class;
    if (remoteInterfaces != null && remoteInterfaces.length > 0) {
      result += "\r\n implements interfaces:";
      for (Class remoteInterface : remoteInterfaces) {
        result += "\r\n    -" + remoteInterface.getName();
      }
      result += "\r\n--------------------------------------";
    }
    if (operations != null && operations.length > 0) {
      result += "\r\n remote methods:";
      for (Operation operation : operations) {
        result += "\r\n    " + operation;
      }
      result += "\r\n--------------------------------------";
    }

    return result;
  }

}