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
package com.sap.engine.services.security.remote;

import java.rmi.server.Operation;


import com.sap.engine.services.rmi_p4.*;


/**
*
* @author  RMIC Generator
* @version 6.30
*/
public class RemoteGroupInfo_Stub  extends com.sap.engine.services.rmi_p4.StubBase 
		implements com.sap.engine.services.security.remote.RemoteGroupInfo {

	private static final Operation[] operations = {
			new Operation("getName()"),
			new Operation("getParentGroups()"),
			new Operation("getChildGroups()"),
			new Operation("getUsersInGroup()")};

	public Operation[] getOperations() {
		return operations;
	}

	public java.lang.String getName() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw new java.rmi.NoSuchObjectException("");
			} else {
				remote = p4remote.delegate();
				p4remote.checkPermission("getName()");
			}
			try {
				com.sap.engine.services.security.remote.RemoteGroupInfo remoteInterface = (com.sap.engine.services.security.remote.RemoteGroupInfo) remote;
				return remoteInterface.getName();
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return ((String) invokeReflect(remote,"getName",params,p));
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable)replicate(ite.getTargetException());
					if (target instanceof java.rmi.RemoteException) {
					 throw (java.rmi.RemoteException)target;
					} else { 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getName>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getName>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(0);
				P4ObjectOutput out = call.getOutputStream();
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					java.lang.String _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.String)obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.String) broker.narrow(obj,java.lang.String.class);
					}
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
				}
			} finally {
				done(call);
				call.releaseInputStream();
			}
	}


	public com.sap.engine.services.security.remote.RemoteIterator getParentGroups() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw new java.rmi.NoSuchObjectException("");
			} else {
				remote = p4remote.delegate();
				p4remote.checkPermission("getParentGroups()");
			}
			try {
				com.sap.engine.services.security.remote.RemoteGroupInfo remoteInterface = (com.sap.engine.services.security.remote.RemoteGroupInfo) remote;
				return (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(replicate(remoteInterface.getParentGroups()), com.sap.engine.services.security.remote.RemoteIterator.class);
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return (com.sap.engine.services.security.remote.RemoteIterator)broker.narrow(replicate(invokeReflect(remote,"getParentGroups",params,p)),com.sap.engine.services.security.remote.RemoteIterator.class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable)replicate(ite.getTargetException());
					if (target instanceof java.rmi.RemoteException) {
					 throw (java.rmi.RemoteException)target;
					} else { 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getParentGroups>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getParentGroups>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(1);
				P4ObjectOutput out = call.getOutputStream();
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					com.sap.engine.services.security.remote.RemoteIterator _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator)obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteIterator.class);
					}
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
				}
			} finally {
				done(call);
				call.releaseInputStream();
			}
	}


	public com.sap.engine.services.security.remote.RemoteIterator getChildGroups() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw new java.rmi.NoSuchObjectException("");
			} else {
				remote = p4remote.delegate();
				p4remote.checkPermission("getChildGroups()");
			}
			try {
				com.sap.engine.services.security.remote.RemoteGroupInfo remoteInterface = (com.sap.engine.services.security.remote.RemoteGroupInfo) remote;
				return (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(replicate(remoteInterface.getChildGroups()), com.sap.engine.services.security.remote.RemoteIterator.class);
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return (com.sap.engine.services.security.remote.RemoteIterator)broker.narrow(replicate(invokeReflect(remote,"getChildGroups",params,p)),com.sap.engine.services.security.remote.RemoteIterator.class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable)replicate(ite.getTargetException());
					if (target instanceof java.rmi.RemoteException) {
					 throw (java.rmi.RemoteException)target;
					} else { 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getChildGroups>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getChildGroups>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(2);
				P4ObjectOutput out = call.getOutputStream();
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					com.sap.engine.services.security.remote.RemoteIterator _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator)obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteIterator.class);
					}
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
				}
			} finally {
				done(call);
				call.releaseInputStream();
			}
	}


	public com.sap.engine.services.security.remote.RemoteIterator getUsersInGroup() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw new java.rmi.NoSuchObjectException("");
			} else {
				remote = p4remote.delegate();
				p4remote.checkPermission("getUsersInGroup()");
			}
			try {
				com.sap.engine.services.security.remote.RemoteGroupInfo remoteInterface = (com.sap.engine.services.security.remote.RemoteGroupInfo) remote;
				return (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(replicate(remoteInterface.getUsersInGroup()), com.sap.engine.services.security.remote.RemoteIterator.class);
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return (com.sap.engine.services.security.remote.RemoteIterator)broker.narrow(replicate(invokeReflect(remote,"getUsersInGroup",params,p)),com.sap.engine.services.security.remote.RemoteIterator.class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable)replicate(ite.getTargetException());
					if (target instanceof java.rmi.RemoteException) {
					 throw (java.rmi.RemoteException)target;
					} else { 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getUsersInGroup>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getUsersInGroup>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(3);
				P4ObjectOutput out = call.getOutputStream();
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					com.sap.engine.services.security.remote.RemoteIterator _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator)obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteIterator) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteIterator.class);
					}
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Unexpexted_exception, tr);
				}
			} finally {
				done(call);
				call.releaseInputStream();
			}
	}


}
