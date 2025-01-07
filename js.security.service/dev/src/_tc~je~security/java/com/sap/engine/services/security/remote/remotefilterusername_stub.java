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



import com.sap.engine.services.rmi_p4.*;


/**
*
* @author  RMIC Generator
* @version 6.30
*/
public class RemoteFilterUsername_Stub extends com.sap.engine.services.rmi_p4.StubImpl 
		implements com.sap.engine.services.security.remote.RemoteFilterUsername {

	private static final String[] operations = {
			"filterUsername(java.lang.String)",
			"generateUsername()",
			"getRestriction(int)",
			"getRestrictionsInfo()",
			"getUsageInfo()",
			"setRestriction(int,int)"};

	public String[] p4_getOperations() {
		return operations;
	}

	public boolean filterUsername(java.lang.String _param0)  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("filterUsername(java.lang.String)");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				return remoteInterface.filterUsername( _param0);
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{_param0};
				Class[] p = new Class[]{java.lang.String.class};
				try { 
					return ((Boolean) p4_invokeReflect(remote,"filterUsername",params,p)).booleanValue();
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <filterUsername>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(0);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				p4_invoke(call);
				P4ObjectInput in = call.getResultStream();
				boolean _result = in.readBoolean();
				return _result;
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


	public java.lang.String generateUsername()  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("generateUsername()");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				return remoteInterface.generateUsername();
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return ((String) p4_invokeReflect(remote,"generateUsername",params,p));
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <generateUsername>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(1);
				P4ObjectOutput out = call.getOutputStream();
				p4_invoke(call);
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


	public int getRestriction(int _param0)  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("getRestriction(int)");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				return remoteInterface.getRestriction( _param0);
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{new Integer(_param0)};
				Class[] p = new Class[]{int.class};
				try { 
					return ((Integer) p4_invokeReflect(remote,"getRestriction",params,p)).intValue();
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getRestriction>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(2);
				P4ObjectOutput out = call.getOutputStream();
				out.writeInt( _param0);
				p4_invoke(call);
				P4ObjectInput in = call.getResultStream();
				int _result = in.readInt();
				return _result;
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


	public java.lang.String[] getRestrictionsInfo()  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("getRestrictionsInfo()");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				return remoteInterface.getRestrictionsInfo();
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return (java.lang.String[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"getRestrictionsInfo",params,p)),java.lang.String[].class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getRestrictionsInfo>", broker.debug);
				broker.log(rex, broker.debug);
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
					java.lang.String[] _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.String[])obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
					}
				return _result;
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


	public java.lang.String[] getUsageInfo()  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("getUsageInfo()");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				return remoteInterface.getUsageInfo();
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{};
				Class[] p = new Class[]{};
				try { 
					return (java.lang.String[])broker.narrow(p4_replicate(p4_invokeReflect(remote,"getUsageInfo",params,p)),java.lang.String[].class);
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getUsageInfo>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(4);
				P4ObjectOutput out = call.getOutputStream();
				p4_invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					java.lang.String[] _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.String[])obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
					}
				return _result;
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


	public void setRestriction(int _param0, int _param1)  {

		if (isLocal) {
			java.rmi.Remote remote;
			if (p4remote == null) {
				throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, null);
			} else {
				try {
					remote = p4remote.delegate();
					p4remote.checkPermission("setRestriction(int,int)");
				} catch (java.rmi.NoSuchObjectException nso) {
					throw (P4RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Object_is_Disconnected, nso);
				}
			}
			try {
				com.sap.engine.services.security.remote.RemoteFilterUsername remoteInterface = (com.sap.engine.services.security.remote.RemoteFilterUsername) remote;
				remoteInterface.setRestriction( _param0,  _param1);
				return;
			} catch (java.lang.ClassCastException rex) {
				Object[] params = new Object[]{new Integer(_param0),new Integer(_param1)};
				Class[] p = new Class[]{int.class,int.class};
				try { 
					p4_invokeReflect(remote,"setRestriction",params,p);
					return ;
				} catch (NoSuchMethodException nsme) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.No_Such_Method,nsme);
				} catch (IllegalAccessException iae) {
					throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.Illegal_Access,iae);
				} catch (java.lang.reflect.InvocationTargetException ite) {
					Throwable target = (Throwable) p4_replicate(ite.getTargetException());
					{ 
					 throw (P4RuntimeException)P4ObjectBroker.init().getException(P4ObjectBroker.P4_RuntimeException,com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException.InvocationTarget, target);
					}
				} 
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <setRestriction>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} 
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = p4_newCall(5);
				P4ObjectOutput out = call.getOutputStream();
				out.writeInt( _param0);
				out.writeInt( _param1);
				p4_invoke(call);
				P4ObjectInput in = call.getResultStream();
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
