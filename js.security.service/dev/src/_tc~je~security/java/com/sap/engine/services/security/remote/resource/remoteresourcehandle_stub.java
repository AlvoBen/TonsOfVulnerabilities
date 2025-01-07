﻿// Class generated by InQMy Generator
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
package com.sap.engine.services.security.remote.resource;

import java.rmi.server.Operation;


import com.sap.engine.services.rmi_p4.*;


/**
*
* @author  Nickolay Neychev, Georgy Stanev
* @version 4.0
*/
public class RemoteResourceHandle_Stub  extends com.sap.engine.services.rmi_p4.StubBase 
		implements com.sap.engine.services.security.remote.resource.RemoteResourceHandle {

	private static final Operation[] operations = {
			new Operation("getActions()"),
			new Operation("getChildren(java.lang.String)"),
			new Operation("getParent(java.lang.String)"),
			new Operation("ungroupInstance(java.lang.String)"),
			new Operation("groupInstance(java.lang.String,java.lang.String)"),
			new Operation("renameResource(java.lang.String)"),
			new Operation("removeAction(java.lang.String)"),
			new Operation("removeInstance(java.lang.String)"),
			new Operation("getAlias()"),
			new Operation("createAction(java.lang.String)"),
			new Operation("createInstance(java.lang.String)")};

	public Operation[] getOperations() {
		return operations;
	}

	public java.lang.String[] getActions() throws java.rmi.RemoteException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getActions()");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getActions>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return remoteInterface.getActions();
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getActions>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getActions>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getActions>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(0);
				P4ObjectOutput out = call.getOutputStream();
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					java.lang.String[] _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.String[])obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
					}
				done(call);
				call.releaseInputStream();
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public java.lang.String[] getChildren(java.lang.String _param0) throws java.rmi.RemoteException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getChildren(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getChildren>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return remoteInterface.getChildren( _param0);
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getChildren>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getChildren>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getChildren>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(1);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					java.lang.String[] _result;
					obj = in.readObject();
					try {
						 _result = (java.lang.String[])obj;
					} catch (ClassCastException ex) {
						 _result = (java.lang.String[]) broker.narrow(obj,java.lang.String[].class);
					}
				done(call);
				call.releaseInputStream();
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public java.lang.String getParent(java.lang.String _param0) throws java.rmi.RemoteException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getParent(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getParent>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return remoteInterface.getParent( _param0);
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getParent>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getParent>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getParent>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(2);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
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
				done(call);
				call.releaseInputStream();
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void ungroupInstance(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("ungroupInstance(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <ungroupInstance>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.ungroupInstance( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <ungroupInstance>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <ungroupInstance>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <ungroupInstance>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(3);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void groupInstance(java.lang.String _param0, java.lang.String _param1) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("groupInstance(java.lang.String,java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <groupInstance>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.groupInstance( _param0,  _param1);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <groupInstance>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <groupInstance>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <groupInstance>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(4);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				out.writeObject( _param1);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void renameResource(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("renameResource(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <renameResource>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.renameResource( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <renameResource>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <renameResource>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <renameResource>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(5);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void removeAction(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("removeAction(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <removeAction>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.removeAction( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <removeAction>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <removeAction>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <removeAction>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(6);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void removeInstance(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("removeInstance(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <removeInstance>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.removeInstance( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <removeInstance>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <removeInstance>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <removeInstance>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(7);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public java.lang.String getAlias() throws java.rmi.RemoteException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getAlias()");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getAlias>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return remoteInterface.getAlias();
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getAlias>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getAlias>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getAlias>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(8);
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
				done(call);
				call.releaseInputStream();
				return _result;
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void createAction(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("createAction(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <createAction>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.createAction( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <createAction>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <createAction>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <createAction>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(9);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


	public void createInstance(java.lang.String _param0) throws java.rmi.RemoteException, java.lang.SecurityException {
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.resource.RemoteResourceHandle remoteInterface = (com.sap.engine.services.security.remote.resource.RemoteResourceHandle) p4remote.getDelegate();
				try{
					p4remote.checkPermission("createInstance(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <createInstance>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.createInstance( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <createInstance>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <createInstance>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <createInstance>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			try {
				com.sap.engine.services.rmi_p4.Call call = newCall(10);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
				done(call);
				call.releaseInputStream();
			} catch (java.rmi.RemoteException ex) {
				throw (java.rmi.RemoteException) ex;
			} catch (java.lang.Exception tr) {
				 // ex.printStackTrace();
				if ( java.lang.RuntimeException.class.isAssignableFrom(tr.getClass()) ) {
					throw (RuntimeException)tr;
				} else {
					throw new P4RuntimeException(tr.toString(), tr);
				}
			}
	}


}
