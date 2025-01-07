﻿// Class generated by SAP Labs Bulgaria's RMIC Generator
// Don't change it !!


/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.remote;



import com.sap.engine.services.rmi_p4.*;


/**
*
* @author  SAP's RMIC Generator
* @version J2EE Engine 7.0
*/
public class RemoteFilterPassword_Stub extends com.sap.engine.services.rmi_p4.StubImpl 
    implements com.sap.engine.services.security.remote.RemoteFilterPassword {

  private static final String[] operations = {
      "filterPassword(char[],java.lang.String)",
      "filterPassword(char[])",
      "generatePassword(java.lang.String)",
      "generatePassword()",
      "getRestriction(int)",
      "getRestrictionsInfo()",
      "getUsageInfo()",
      "setRestriction(int,int)"};

  public String[] p4_getOperations() {
    return operations;
  }

  public boolean filterPassword(char[] _param0, java.lang.String _param1)  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.filterPassword( _param0,  _param1);
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{_param0,_param1};
        Class[] p = new Class[]{char[].class,java.lang.String.class};
        try { 
          return ((Boolean) p4_invokeReflect(remote,"filterPassword",params,p)).booleanValue();
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <filterPassword>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "filterPassword", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(0);
        P4ObjectOutput out = call.getOutputStream();
        out.writeObject( _param0);
        out.writeObject( _param1);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
        boolean _result = in.readBoolean();
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "filterPassword", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public boolean filterPassword(char[] _param0)  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.filterPassword( _param0);
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{_param0};
        Class[] p = new Class[]{char[].class};
        try { 
          return ((Boolean) p4_invokeReflect(remote,"filterPassword",params,p)).booleanValue();
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <filterPassword>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "filterPassword", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(1);
        P4ObjectOutput out = call.getOutputStream();
        out.writeObject( _param0);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
        boolean _result = in.readBoolean();
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "filterPassword", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public char[] generatePassword(java.lang.String _param0)  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.generatePassword( _param0);
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{_param0};
        Class[] p = new Class[]{java.lang.String.class};
        try { 
          return (char[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"generatePassword",params,p)),char[].class);
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <generatePassword>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "generatePassword", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(2);
        P4ObjectOutput out = call.getOutputStream();
        out.writeObject( _param0);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
          Object obj;
          char[] _result;
          obj = in.readObject();
          try {
             _result = (char[])obj;
          } catch (java.lang.ClassCastException ex) {
             _result = (char[]) broker.narrow(obj,char[].class);
          }
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "generatePassword", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public char[] generatePassword()  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.generatePassword();
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{};
        Class[] p = new Class[]{};
        try { 
          return (char[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"generatePassword",params,p)),char[].class);
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <generatePassword>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "generatePassword", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(3);
        P4ObjectOutput out = call.getOutputStream();
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
          Object obj;
          char[] _result;
          obj = in.readObject();
          try {
             _result = (char[])obj;
          } catch (java.lang.ClassCastException ex) {
             _result = (char[]) broker.narrow(obj,char[].class);
          }
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "generatePassword", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public int getRestriction(int _param0)  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.getRestriction( _param0);
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{new Integer(_param0)};
        Class[] p = new Class[]{int.class};
        try { 
          return ((Integer) p4_invokeReflect(remote,"getRestriction",params,p)).intValue();
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <getRestriction>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getRestriction", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(4);
        P4ObjectOutput out = call.getOutputStream();
        out.writeInt( _param0);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
        int _result = in.readInt();
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getRestriction", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public java.lang.String[] getRestrictionsInfo()  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.getRestrictionsInfo();
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{};
        Class[] p = new Class[]{};
        try { 
          return (java.lang.String[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"getRestrictionsInfo",params,p)),java.lang.String[].class);
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <getRestrictionsInfo>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getRestrictionsInfo", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(5);
        P4ObjectOutput out = call.getOutputStream();
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
          Object obj;
          java.lang.String[] _result;
          obj = in.readObject();
          try {
             _result = (java.lang.String[])obj;
          } catch (java.lang.ClassCastException ex) {
             _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
          }
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getRestrictionsInfo", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public java.lang.String[] getUsageInfo()  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        return remoteInterface.getUsageInfo();
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{};
        Class[] p = new Class[]{};
        try { 
          return (java.lang.String[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"getUsageInfo",params,p)),java.lang.String[].class);
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <getUsageInfo>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getUsageInfo", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(6);
        P4ObjectOutput out = call.getOutputStream();
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
          Object obj;
          java.lang.String[] _result;
          obj = in.readObject();
          try {
             _result = (java.lang.String[])obj;
          } catch (java.lang.ClassCastException ex) {
             _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
          }
        return _result;
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "getUsageInfo", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public void setRestriction(int _param0, int _param1)  {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
      } else {
        try {
          remote = p4remote.delegate();
        } catch (java.rmi.NoSuchObjectException nso) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
        }
      }
      try {
        com.sap.engine.services.security.remote.RemoteFilterPassword remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterPassword) remote;
        remoteInterface.setRestriction( _param0,  _param1);
        return;
      } catch (java.lang.ClassCastException rex) {
        Object[] params = new Object[]{new Integer(_param0),new Integer(_param1)};
        Class[] p = new Class[]{int.class,int.class};
        try { 
          p4_invokeReflect(remote,"setRestriction",params,p);
          return ;
        } catch (java.lang.NoSuchMethodException nsme) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (java.lang.IllegalAccessException iae) {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          { 
           throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (java.lang.RuntimeException rex) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.logDebug("P4 Call exception: Exception in execute <setRestriction>");
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "setRestriction", rex);
        throw rex;
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(7);
        P4ObjectOutput out = call.getOutputStream();
        out.writeInt( _param0);
        out.writeInt( _param1);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
      } catch (java.lang.Exception tr) {
        com.sap.engine.services.rmi_p4.exception.P4Logger.traceDebug(this.getClass(), "setRestriction", tr);
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (com.sap.engine.services.rmi_p4.P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }



  public byte[] readByteArray(com.sap.engine.services.rmi_p4.P4ObjectInput in, int length){
    byte[] buffer = new byte[length];
    int readed = 0;
    int offset = 0;
    try {
      while (readed != -1 && offset < length) {
        readed = in.read(buffer, offset, length - offset);
        offset += readed;
      }
    } catch (java.io.IOException ioe) {
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().debugT(this + ". Problem while read byte[] from the stream : " + ioe.getMessage());
      com.sap.engine.services.rmi_p4.exception.P4Logger.getLocation().throwing(ioe);
    }
    return buffer;
  }


}
