package com.sap.engine.services.rmi_p4.reflect;

import com.sap.engine.services.rmi_p4.Call;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.RemoteRef;
import com.sap.engine.services.rmi_p4.StubImpl;
import com.sap.engine.services.rmi_p4.RemoteObjectInfo;
import com.sap.engine.services.rmi_p4.P4ClassWrapper;
import com.sap.engine.services.rmi_p4.P4RuntimeException;
import com.sap.engine.services.rmi_p4.MarshalInputStream;
import com.sap.engine.services.rmi_p4.P4ObjectOutput;
import com.sap.engine.services.rmi_p4.P4ObjectInput;
import com.sap.engine.services.rmi_p4.DataOptOutputStream;
import com.sap.engine.services.rmi_p4.DataOptInputStream;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.Connection;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Mladen Droshev, Georgi Stanev
 * @version 7.10
 */
public class P4InvocationHandler extends AbstractInvocationHandler implements Serializable {

  static final long serialVersionUID = -4995299584378296149L;

  private StubImpl info = null;

  private transient Connection repl = null;

  /**
   * Constructor for P4InvocationHandler.
   */
  public P4InvocationHandler() {
    super();
  }

  public Connection getRepl() {
    return repl;
  }

  public void setRepl(Connection repl) {
    this.repl = repl;
  }

  /**
   * methods for in/out of stub base
   */

  public StubImpl getInfo() {
    return info;
  }

  public void setInfo(StubImpl info) {
    this.info = info;
  }

  public String getMethodName(Method method) {
    String methodName = method.toString();
    methodName = methodName.substring(methodName.indexOf('('), methodName.lastIndexOf(')') + 1);
    methodName = method.getName() + methodName;
    return methodName;
  }

  protected Object invokeInternal(Object proxyObject, Method method, Object[] args) throws Throwable {
    if (P4Logger.getLocation().bePath()) {
      P4Logger.getLocation().pathT("P4InvocationHandler.invokeInternal(Object, Method, Object[])", "Called method: " + method.getName());
      if ((args != null) && (args.length > 0)) {
        for (int i = 0; i < args.length; i++) {
          P4Logger.getLocation().pathT("P4InvocationHandler.invokeInternal(Object, Method, Object[])", "Called method: " + method.getName() + " PARAM[" + i + "] = " + args[i]);
        }
      }
    }

    com.sap.engine.services.rmi_p4.Call call = null;

    if (method.equals(RemoteRef.class.getMethod("getObjectInfo", null))) {
      return info.p4_getInfo();
    } 
      
      try {

        boolean isParamsOptmizable = false;
        boolean isResultOptimizable = false;
        if(info != null && info.p4_getInfo() != null && info.p4_getInfo().supportOptimization()){
          isParamsOptmizable = isOptimizableParams(method);
          isResultOptimizable =  isOptimizableReturnType(method);
        }


        Class[] mArgs = method.getParameterTypes();
        if(isParamsOptmizable && isResultOptimizable){
          call = info.p4_newCall(P4ObjectBroker.BOTH_OPTIMIZED + getMethodName(method), true);
        } else if(isResultOptimizable){
          call = info.p4_newCall(P4ObjectBroker.RESULT_OPTIMIZED + getMethodName(method), false);
        } else if(isParamsOptmizable){
          call = info.p4_newCall(P4ObjectBroker.PARAM_OPTIMIZED + getMethodName(method), true);
        } else {
          call = info.p4_newCall(getMethodName(method));
        }

        P4ObjectOutput out = null;
        DataOptOutputStream dout = null;
        if(isParamsOptmizable){
          dout = call.getDataOutputStream();
        } else {
          out = call.getOutputStream();
        }

        String methodName = getMethodName(method);
        if (args != null && args.length > 0 && mArgs != null && mArgs.length > 0) {
          if(isParamsOptmizable && dout != null){
            writeOptArgs(dout, args, mArgs);
          } else {
            writeArgs(out, args, mArgs);
          }
        }

         if(isParamsOptmizable){
            call.sendSimpleRequest();
         } else {
            call.sendRequest();
         }

        Object result = null;
        if(isResultOptimizable){
          DataOptInputStream din = call.getDataResultStream();
          din.setClassLoader(proxyObject.getClass().getClassLoader());
          result = readOptResult(din, method);
        } else {
          P4ObjectInput in = call.getResultStream();
          ((MarshalInputStream)in).setClassLoader(proxyObject.getClass().getClassLoader()); // sets classloader of stub as classloader of return value
          result = readResult(in, method);
        }

        return result;
      } catch (RuntimeException rex) {
        throw rex;
      } catch (Exception ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("P4InvocationHandler.invokeInternal()", "Exception in calling method: " + method.getName());
          P4Logger.getLocation().throwing(ex);
          if (info != null && info.p4_getInfo() != null) {
            P4Logger.getLocation().debugT("P4InvocationHandler : info : \r\n" + info.p4_getInfo());
          }
        }
        Class[] exceptions = method.getExceptionTypes();
        if (exceptions != null) {
          for (Class exception : exceptions) {
            if (exception.isAssignableFrom(ex.getClass())) {
              throw ex;
            }
          }
        }
        throw (P4RuntimeException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, ex);
      } finally {
        if (call != null) {
          Call.removeCall(call.getCall_id());
        }
      }
  }

  private void writeArgs(P4ObjectOutput out, Object[] args, Class[] mArgs) throws Exception {
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null) {
        out.writeObject(null);
      } else {
        if (mArgs[i].isPrimitive()) {
          if (mArgs[i].equals(boolean.class)) {
            out.writeBoolean((Boolean) args[i]);
          } else if (mArgs[i].equals(byte.class)) {
            out.writeByte((Byte) args[i]);
          } else if (mArgs[i].equals(short.class)) {
            out.writeShort((Short) args[i]);
          } else if (mArgs[i].equals(char.class)) {
            out.writeChar((Character) args[i]);
          } else if (mArgs[i].equals(float.class)) {
            out.writeFloat((Float) args[i]);
          } else if (mArgs[i].equals(int.class)) {
            out.writeInt((Integer) args[i]);
          } else if (mArgs[i].equals(double.class)) {
            out.writeDouble((Double) args[i]);
          } else if (mArgs[i].equals(long.class)) {
            out.writeLong((Long) args[i]);
          }
        } else {
          out.writeObject(args[i]);
        }
      }
    }
  }

  private void writeOptArgs(DataOptOutputStream dout, Object[] args, Class[] mArgs) throws Exception {
    for (int i = 0; i < args.length; i++) {
      if (mArgs[i].isPrimitive()) {
        if (mArgs[i].equals(boolean.class)) {
          dout.writeBoolean((Boolean) args[i]);
        } else if (mArgs[i].equals(byte.class)) {
          dout.writeByte((Byte) args[i]);
        } else if (mArgs[i].equals(short.class)) {
          dout.writeShort((Short) args[i]);
        } else if (mArgs[i].equals(char.class)) {
          dout.writeChar((Character) args[i]);
        } else if (mArgs[i].equals(float.class)) {
          dout.writeFloat((Float) args[i]);
        } else if (mArgs[i].equals(int.class)) {
          dout.writeInt((Integer) args[i]);
        } else if (mArgs[i].equals(double.class)) {
          dout.writeDouble((Double) args[i]);
        } else if (mArgs[i].equals(long.class)) {
          dout.writeLong((Long) args[i]);
        }
      } else if (mArgs[i].getName().equals("[B")) {
        writeByteArray(dout, (byte[]) args[i]);
      } else if (mArgs[i].getName().equals("java.lang.String")) {
        if(args[i] == null){
          writeByteArray(dout, null);
        } else {
          writeByteArray(dout, ((String) args[i]).getBytes());
        }
      } else {
        throw new P4RuntimeException("Not Supported parameter type : " + args[i].getClass());
      }
    }

  }

  private Object readOptResult(DataOptInputStream din, Method method) throws Exception {
    Object result = null;

    Class ret = method.getReturnType();

    if (ret.equals(void.class)) {
      result = null;
    } else if (ret.equals(boolean.class)) {
      result = (din.readBoolean());
    } else if (ret.equals(byte.class)) {
      result = (din.readByte());
    } else if (ret.equals(short.class)) {
      result = (din.readShort());
    } else if (ret.equals(char.class)) {
      result = (din.readChar());
    } else if (ret.equals(float.class)) {
      result = (din.readFloat());
    } else if (ret.equals(int.class)) {
      result = (din.readInt());
    } else if (ret.equals(double.class)) {
      result = (din.readDouble());
    } else if (ret.equals(long.class)) {
      result = (din.readLong());
    } else if (ret.equals(byte[].class)) {
      result = readByteArray(din);
    } else if (ret.equals(String.class)) {
      Object _res = readByteArray(din);
      if (_res != null) {
        result = new String((byte[]) _res);
      }
    } else {
      throw new P4RuntimeException("Not Supported result type : " + ret);
    }

    return result;
  }

  private Object readResult(P4ObjectInput in, Method method) throws Exception {
    Object result = null;
    Class ret = method.getReturnType();
    if (ret.equals(void.class)) {
      result = null;
    } else if (ret.equals(boolean.class)) {
      result = (in.readBoolean());
    } else if (ret.equals(byte.class)) {
      result = (in.readByte());
    } else if (ret.equals(short.class)) {
      result = (in.readShort());
    } else if (ret.equals(char.class)) {
      result = (in.readChar());
    } else if (ret.equals(float.class)) {
      result = (in.readFloat());
    } else if (ret.equals(int.class)) {
      result = (in.readInt());
    } else if (ret.equals(double.class)) {
      result = (in.readDouble());
    } else if (ret.equals(long.class)) {
      result = (in.readLong());
    } else if (ret.getName().equals("java.lang.Class")) {
      Object obj = in.readObject();
      if (obj == null) {
        result = null;
      } else if (obj instanceof P4ClassWrapper) {
        ((P4ClassWrapper) obj).setStub(info); // TODO  Vancho da se napravi s opcia a moze i taka da si ostane
        result = ((P4ClassWrapper) obj).getCarriedClass();
      } else {
        result = obj;
      }
    } else {
      result = P4ObjectBroker.init().narrow(in.readObject(), ret);
    }
    return result;
  }


  /**
   * method's goal is to compare the remote objects of 2 proxy instances. If these remote Objects are equals then return true.
   *
   * @param obj
   * @return true if their remote objcts are equals
   */
  public boolean equals_internal(Object baseObj, Object obj) {
    if (obj instanceof Proxy) {
      if (this.repl == null && this.info != null) {
        this.repl = this.info.repliable;
      }
      try {
        P4InvocationHandler askHandler = (P4InvocationHandler) Proxy.getInvocationHandler(obj);
        Connection askRep = askHandler.getRepl();
        if (askRep == null && askHandler.getInfo() != null) {
          askRep = askHandler.getInfo().repliable;     //try to get repl from the StubImpl
        }
        if (this.repl != null && askRep != null) {
          if (!(this.repl.equals(askRep))) {
            return false;
          } else if (this.info != null && askHandler.info != null && java.util.Arrays.equals(this.info.info.connectionProfiles, askHandler.info.info.connectionProfiles)) {
            return java.util.Arrays.equals(this.info.info.key, askHandler.info.info.key);
          }
        }
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4InvocationHandler.equals_internal(Object, Object)", "This equals method from the P4InvocationHandler return false because repl and info are null!");
        }
      } catch (ClassCastException e) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("P4InvocationHandler.equals_internal(Object, Object)", P4Logger.exceptionTrace(e));
        }
      }

      return false;
    } else {
      if (obj instanceof RemoteObjectInfo) {
        RemoteObjectInfo remoteInfo = (RemoteObjectInfo) obj;
        return (this.info != null && obj != null && java.util.Arrays.equals(this.info.info.key, remoteInfo.key) && java.util.Arrays.equals(this.info.info.connectionProfiles, remoteInfo.connectionProfiles));
      }
    }
    return false;
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

  private Object writeReplace() throws ObjectStreamException {
    InvocationHandlerProxy ihProxy = new InvocationHandlerProxy();
    ihProxy.setRemoteInfo(info.p4_getInfo());
    return ihProxy;
  }

  public String invokeToString(Object proxy) {
    String result = "P4: Remote Dynamic Stub<" + System.identityHashCode(proxy) + ">:\r\n============================================================\r\n";
    if (info != null) {
      result += " info:" + info.p4_getInfo();
    }
    return result;
  }


}
