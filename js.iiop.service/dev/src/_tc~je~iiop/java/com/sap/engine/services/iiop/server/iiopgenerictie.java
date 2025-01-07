/*
 * Created on 2004-11-10
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.iiop.server;

import java.io.Serializable;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.ObjectImpl;
import java.lang.reflect.Method;
import javax.rmi.PortableRemoteObject;
import javax.rmi.CORBA.ValueHandler;
import com.sap.engine.services.iiop.internal.util.RepositoryID;
import com.sap.engine.services.iiop.server.generator.DescriptorWriter;

import java.util.*;

/**
 * @author Pavel-B
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IIOPGenericTie extends ObjectImpl implements Tie  {
  private Remote target = null;
  private Class[] remoteInterfaces = {};
  private Hashtable remoteMethods = new Hashtable();
  private ArrayList abstractMethods = new ArrayList();
    
  private String[] _type_ids = null;
    
  public void setTarget(Remote target) {
    this.target = target;
    // if null is passed (in case of releasing the tie)
    if (target != null) {
      init(target.getClass());
    }
  }
    
  public Remote getTarget() {
    return target;
  }
    
  public org.omg.CORBA.Object thisObject() {
    return this;
  }
    
  public void deactivate() {
    _orb().disconnect(this);
    _set_delegate(null);
    target = null;
  }
    
  public ORB orb() {
    return _orb();
  }
    
  public void orb(ORB orb) {
    orb.connect(this);
  }
    
  public String[] _ids() { 
    return _type_ids;
  }  
  
  public OutputStream  _invoke(String methodName, InputStream _in, ResponseHandler reply) throws SystemException {
    ClassLoader tcLoader = Thread.currentThread().getContextClassLoader();  
    try {  
        Thread.currentThread().setContextClassLoader(target.getClass().getClassLoader());
        try {
          org.omg.CORBA_2_3.portable.InputStream in = (org.omg.CORBA_2_3.portable.InputStream) _in;
          Method method = (Method) remoteMethods.get(methodName);
          if (method == null) {
            throw new BAD_OPERATION();
          }
          Class[] mArgs = method.getParameterTypes();
          Object[] params = null;
    
          if (mArgs != null && mArgs.length > 0) {
            params = new Object[mArgs.length];
            for (int i = 0; i < mArgs.length; i++) {
              if (mArgs[i].equals(boolean.class)) {
                params[i] = (new Boolean(in.read_boolean()));
              } else if (mArgs[i].equals(byte.class)) {
                params[i] = (new Byte(in.read_octet()));
              } else if (mArgs[i].equals(short.class)) {
                params[i] = (new Short(in.read_short()));
              } else if (mArgs[i].equals(char.class)) {
                char ch = in.read_wchar();
                params[i] = (new Character(ch));
              } else if (mArgs[i].equals(float.class)) {
                params[i] = (new Float(in.read_float()));
              } else if (mArgs[i].equals(int.class)) {
                params[i] = (new Integer(in.read_long()));
              } else if (mArgs[i].equals(double.class)) {
                params[i] = (new Double(in.read_double()));
              } else if (mArgs[i].equals(long.class)) {
                params[i] = (new Long(in.read_longlong()));
              } else if (java.rmi.Remote.class.isAssignableFrom(mArgs[i])) {              
                params[i] = (Remote) PortableRemoteObject.narrow(in.read_Object(), mArgs[i]);
              } else if (mArgs[i].equals(java.io.Serializable.class)) {
                params[i] = (Serializable)Util.readAny(in);
              } else if (mArgs[i].equals(java.io.Externalizable.class)) {
                params[i] = (java.io.Externalizable)Util.readAny(in);
              } else if (mArgs[i].equals(java.lang.Object.class)) {
                params[i] = (java.lang.Object)Util.readAny(in);
              } else if (org.omg.CORBA.Object.class.isAssignableFrom(mArgs[i])) {
                params[i] = (org.omg.CORBA.Object) PortableRemoteObject.narrow(in.read_Object(), mArgs[i]);
              } else if (mArgs[i].isInterface() && areAllMethodsThrowRemoteEx(mArgs[i])) {
                params[i] = PortableRemoteObject.narrow(in.read_abstract_interface(), mArgs[i]);
              } else {
                params[i] = in.read_value();
              }
            }
          }
          
          Class returnType = method.getReturnType();
          Object result;
          try {
            result = method.invoke(target, params);
          } catch (Throwable t) {
            Throwable originalEx = t.getCause();
            if (originalEx != null) {
              if (java.rmi.RemoteException.class.isAssignableFrom(originalEx.getClass())) {
                throw (java.rmi.RemoteException)originalEx;
              } else if (RuntimeException.class.isAssignableFrom(originalEx.getClass())) {
                throw (RuntimeException)originalEx;
              } else {
                Class _class = getExceptionType(method.getExceptionTypes(), originalEx);
                if (_class != null) {  
                  String id = RepositoryID.getIDLRepositoryID(_class);
                  org.omg.CORBA_2_3.portable.OutputStream out = 
                      (org.omg.CORBA_2_3.portable.OutputStream) reply.createExceptionReply();
                  out.write_string(id);
                  out.write_value(originalEx,_class);
                  return out;
                } 
              } 
            } 
            throw t;
          }
          OutputStream out = reply.createReply();
          if (returnType.getName().equals("void")) {
            return out;
          } else {
            if (returnType.isPrimitive()) {
              if (returnType.equals(boolean.class)) {
                out.write_boolean(((Boolean)result).booleanValue());
              } else if (returnType.equals(byte.class)) {
                out.write_octet(((Byte)result).byteValue());
              } else if (returnType.equals(short.class)) {
                out.write_short(((Short)result).shortValue());
              } else if (returnType.equals(char.class)) {
                out.write_wchar(((Character)result).charValue());
              } else if (returnType.equals(float.class)) {
                out.write_float(((Float)result).floatValue());
              } else if (returnType.equals(int.class)) {
                out.write_long(((Integer)result).intValue());
              } else if (returnType.equals(double.class)) {
                out.write_double(((Double)result).doubleValue());
              } else if (returnType.equals(long.class)) {
                out.write_longlong(((Long)result).longValue());
              }
            } else if (java.rmi.Remote.class.isAssignableFrom(returnType)) {
              Util.writeRemoteObject(out,(Remote)result);
            } else if (returnType.equals(java.io.Serializable.class) || 
                       returnType.equals(java.io.Externalizable.class) ||
                       returnType.equals(java.lang.Object.class)) {
              Util.writeAny(out, result);
            } else if (org.omg.CORBA.Object.class.isAssignableFrom(returnType)) {
              out.write_Object((org.omg.CORBA.Object) result); 
            } else if (abstractMethods.contains(method)) {//(returnType.isInterface() && areAllMethodsThrowRemoteEx(returnType)) { 
              Util.writeAbstractObject((org.omg.CORBA_2_3.portable.OutputStream)out,result);            
            } else {
              ((org.omg.CORBA_2_3.portable.OutputStream) out).write_value((Serializable) result, returnType);
            }
            return out;
          }
        } catch (SystemException ex) {
          throw ex;
        } catch (Throwable ex) {
          throw new UnknownException(ex);
        }
    } finally {
      Thread.currentThread().setContextClassLoader(tcLoader);
    }
  }
  
  
  private boolean areAllMethodsThrowRemoteEx(Class _class) {
    boolean result = true;
    
    Method[] m = _class.getMethods();
    
    int num = 0;
    for (int i = 0; i < m.length; i++) {
      Class[] exc = m[i].getExceptionTypes();
      for (int j = 0; j < exc.length; j++) {
        if (exc[j].equals(java.rmi.RemoteException.class)) {
          num++;
          break;
        }
      }
    }
    
    if (num != m.length) {
      result = false; 
    }    
    
    
    return result;
  }
  
  private boolean isAbstractInterface(Class _class) {
    boolean result = true;
    
    if (!_class.isInterface() ||
        java.rmi.Remote.class.isAssignableFrom(_class)) {
      return false;
    } 
        
    Method[] m = _class.getMethods();
    
    int num = 0;
    for (int i = 0; i < m.length; i++) {
      Class[] exc = m[i].getExceptionTypes();
      for (int j = 0; j < exc.length; j++) {
        if (exc[j].equals(java.rmi.RemoteException.class)) {
          num++;
          break;
        }
      }
    }
    
    if (num != m.length) {
      result = false; 
    }    
    
    
    return result;
  }
  
  
  private Class getExceptionType(Class[] exceptionClasses, Throwable exception) {
    Class _class = exception.getClass();
    
    for (int i = 0; i < exceptionClasses.length; i++) {
      if (exceptionClasses[i].isAssignableFrom(_class)) {
        return exceptionClasses[i];
      }
    }
    
    return null;
  }
  
  private void init(Class _class) {
    Vector temp_interfaces = new Vector();
    
    remoteMethods.clear();
    abstractMethods.clear();
    
    DescriptorWriter.getRemoteInterfaces(_class, temp_interfaces);
    DescriptorWriter.getIDLRemoteMethods(_class, temp_interfaces, remoteMethods);
    
    setAbstractMethods(remoteMethods.values(), abstractMethods);

    if (temp_interfaces.size() > 0) {
      remoteInterfaces = new Class[temp_interfaces.size()];
      temp_interfaces.copyInto(remoteInterfaces);
    } else {
      remoteInterfaces = new Class[0];
    }
    
    setIDs();
  }
  
  private void setAbstractMethods(Collection methods, ArrayList aMethods) {
    Iterator iter = methods.iterator();
    
    while (iter.hasNext()) {
      Method method = (Method)iter.next();
      Class retType = method.getReturnType();
      
      if (isAbstractInterface(retType)) {
        aMethods.add(method);
      }
    }
  }
  
  
  private void setIDs() {
    _type_ids = new String[remoteInterfaces.length];
    
    ValueHandler vh = Util.createValueHandler();
    for (int i = 0 ; i < remoteInterfaces.length; i++) {
      _type_ids[i] = vh.getRMIRepositoryID(remoteInterfaces[i]);
    }
  }

  public Class[] getRemoteInterfaces() {
    return remoteInterfaces;
  }
}
