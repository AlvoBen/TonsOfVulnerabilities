﻿// Class generated by SAP Labs Bulgaria Generator
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
package com.sap.engine.services.security.remote.login;



import com.sap.engine.services.rmi_p4.*;


/**
*
* @author  RMIC Generator
* @version 6.30
*/
public class RemoteCallbackHandler_Stub extends com.sap.engine.services.rmi_p4.StubImpl 
		implements com.sap.engine.services.security.remote.login.RemoteCallbackHandler {

	private static final String[] operations = {
			"handle(java.lang.Object)",
			"handle(java.lang.Object[])"};

	public String[] p4_getOperations() {
		return operations;
	}


  public java.lang.Object handle(java.lang.Object _param0) throws java.rmi.RemoteException, java.io.IOException, javax.security.auth.callback.UnsupportedCallbackException {

    if (isLocal) {
      java.rmi.Remote remote;
      if (p4remote == null) {
        throw new java.rmi.NoSuchObjectException("");
      } else {
        remote = p4remote.delegate();
      }
      try {
        com.sap.engine.services.security.remote.login.RemoteCallbackHandler remoteInterface = (com.sap.engine.services.security.remote.login.RemoteCallbackHandler) remote;
        ReplicateOutputStream out1 = p4_getReplicateOutput();
        ReplicateInputStream inn1 = p4_getReplicateInput(out1);
        return (java.lang.Object) broker.narrow(p4_replicate(remoteInterface.handle((java.lang.Object) broker.narrow(p4_replicateWithStreams(inn1,out1,_param0),java.lang.Object.class))), java.lang.Object.class);
      } catch (java.lang.ClassCastException rex) {
        ReplicateOutputStream outt = p4_getReplicateOutput();
        ReplicateInputStream inn = p4_getReplicateInput(outt);
        Object[] params = new Object[]{p4_replicateWithStreams(inn,outt,_param0)};
        Class[] p = new Class[]{(Class) p4_replicateWithStreams(inn,outt,java.lang.Object.class)};
        try { 
          return (java.lang.Object)broker.narrow(p4_replicate(p4_invokeReflect(remote,"handle",params,p)),java.lang.Object.class);
        } catch (NoSuchMethodException nsme) {
          throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
        } catch (IllegalAccessException iae) {
          throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
        } catch (java.lang.reflect.InvocationTargetException ite) {
          Throwable target = (Throwable) p4_replicate(ite.getTargetException());
          if (target instanceof java.rmi.RemoteException) {
           throw (java.rmi.RemoteException)target;
          } else if (target instanceof java.io.IOException) {
           throw (java.io.IOException)target;
          } else if (target instanceof javax.security.auth.callback.UnsupportedCallbackException) {
           throw (javax.security.auth.callback.UnsupportedCallbackException)target;
          } else { 
           throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
          }
        } 
      } catch (RuntimeException rex) {
        broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
        broker.log(rex, broker.debug);
          throw rex;
      } catch (java.rmi.RemoteException ex) {
        broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
        broker.log(ex, broker.debug);
        throw (java.rmi.RemoteException) p4_replicate(ex);
      } catch (java.io.IOException ex) {
        broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
        broker.log(ex, broker.debug);
        throw (java.io.IOException) p4_replicate(ex);
      } catch (javax.security.auth.callback.UnsupportedCallbackException ex) {
        broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
        broker.log(ex, broker.debug);
        throw (javax.security.auth.callback.UnsupportedCallbackException) p4_replicate(ex);
      } 
    }
      com.sap.engine.services.rmi_p4.Call call = null;
      try {
        call = p4_newCall(0);
        P4ObjectOutput out = call.getOutputStream();
        out.writeObject( _param0);
        p4_invoke(call);
        P4ObjectInput in = call.getResultStream();
          Object obj;
          java.lang.Object _result;
          obj = in.readObject();
          try {
             _result = (java.lang.Object)obj;
          } catch (ClassCastException ex) {
             _result = (java.lang.Object) broker.narrow(obj,java.lang.Object.class);
          }
        return _result;
      } catch (java.rmi.RemoteException ex) {
        throw ex;
      } catch (java.io.IOException ex) {
        throw ex;
      } catch (javax.security.auth.callback.UnsupportedCallbackException ex) {
        throw ex;
      } catch (java.lang.Exception tr) {
         // ex.printStackTrace();
        if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
          throw (RuntimeException)tr;
        } else {
          throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
        }
      } finally {
        p4_done(call);
        call.releaseInputStream();
      }
  }

	public java.lang.Object[] handle(java.lang.Object[] _param0) throws java.rmi.RemoteException, java.io.IOException, javax.security.auth.callback.UnsupportedCallbackException {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw new java.rmi.NoSuchObjectException("");
			} else {
				remote = p4remote.delegate();
			}
			try {
				com.sap.engine.services.security.remote.login.RemoteCallbackHandler remoteInterface = (com.sap.engine.services.security.remote.login.RemoteCallbackHandler) remote;
				ReplicateOutputStream out1 = p4_getReplicateOutput();
				ReplicateInputStream inn1 = p4_getReplicateInput(out1);
				return (java.lang.Object[]) broker.narrow(p4_replicate(remoteInterface.handle((java.lang.Object[]) broker.narrow(p4_replicateWithStreams(inn1,out1,_param0),java.lang.Object[].class))), java.lang.Object[].class);
			} catch (java.lang.ClassCastException rex) {
				ReplicateOutputStream outt = p4_getReplicateOutput();
				ReplicateInputStream inn = p4_getReplicateInput(outt);
				Object[] params = new Object[]{p4_replicateWithStreams(inn,outt,_param0)};
				Class[] p = new Class[]{(Class) p4_replicateWithStreams(inn,outt,java.lang.Object[].class)};
				try { 
					return (java.lang.Object[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"handle",params,p)),java.lang.Object[].class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					if (target instanceof java.rmi.RemoteException) {
					 throw (java.rmi.RemoteException)target;
					} else if (target instanceof java.io.IOException) {
					 throw (java.io.IOException)target;
					} else if (target instanceof javax.security.auth.callback.UnsupportedCallbackException) {
					 throw (javax.security.auth.callback.UnsupportedCallbackException)target;
					} else { 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) p4_replicate(ex);
			} catch (java.io.IOException ex) {
				broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.io.IOException) p4_replicate(ex);
			} catch (javax.security.auth.callback.UnsupportedCallbackException ex) {
				broker.log("P4 Call exception: Exception in execute <handle>", broker.debug);
				broker.log(ex, broker.debug);
				throw (javax.security.auth.callback.UnsupportedCallbackException) p4_replicate(ex);
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(1);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				p4_invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					java.lang.Object[] _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.Object[])obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.Object[]) broker.narrow(obj,java.lang.Object[].class);
					}
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw ex;
			} catch (java.io.IOException ex) {
				throw ex;
			} catch (javax.security.auth.callback.UnsupportedCallbackException ex) {
				throw ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
				}
			} finally {
				p4_done(call);
				call.releaseInputStream();
			}
	}


}
