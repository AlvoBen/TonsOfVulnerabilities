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
public class RemoteUserStoreFactory_Stub  extends com.sap.engine.services.rmi_p4.StubBase 
		implements com.sap.engine.services.security.remote.RemoteUserStoreFactory {

	private static final Operation[] operations = {
			new Operation("getActiveUserStore()"),
			new Operation("setActiveUserStore(java.lang.String)"),
			new Operation("getUserStore(java.lang.String)"),
			new Operation("listUserStores()"),
			new Operation("registerUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration)"),
			new Operation("updateUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration)"),
			new Operation("unregisterUserStore(java.lang.String)"),
			new Operation("registerListener(com.sap.engine.services.security.remote.UserStoreListenerCallback)"),
			new Operation("unregisterListener(com.sap.engine.services.security.remote.UserStoreListenerCallback)")};

	public Operation[] getOperations() {
		return operations;
	}

	public com.sap.engine.services.security.remote.RemoteUserStore getActiveUserStore() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getActiveUserStore()");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getActiveUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return (com.sap.engine.services.security.remote.RemoteUserStore) broker.narrow(replicate(remoteInterface.getActiveUserStore()), com.sap.engine.services.security.remote.RemoteUserStore.class);
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getActiveUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getActiveUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getActiveUserStore>", broker.debug);
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
					com.sap.engine.services.security.remote.RemoteUserStore _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore)obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteUserStore.class);
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


	public void setActiveUserStore(java.lang.String _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("setActiveUserStore(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <setActiveUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.setActiveUserStore( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <setActiveUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <setActiveUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <setActiveUserStore>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(1);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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


	public com.sap.engine.services.security.remote.RemoteUserStore getUserStore(java.lang.String _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("getUserStore(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <getUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return (com.sap.engine.services.security.remote.RemoteUserStore) broker.narrow(replicate(remoteInterface.getUserStore( _param0)), com.sap.engine.services.security.remote.RemoteUserStore.class);
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <getUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <getUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <getUserStore>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(2);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
					Object obj;
					com.sap.engine.services.security.remote.RemoteUserStore _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore)obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteUserStore.class);
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


	public com.sap.engine.services.security.remote.RemoteUserStore[] listUserStores() throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("listUserStores()");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <listUserStores>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				return (com.sap.engine.services.security.remote.RemoteUserStore[]) broker.narrow(replicate(remoteInterface.listUserStores()), com.sap.engine.services.security.remote.RemoteUserStore[].class);
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <listUserStores>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <listUserStores>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <listUserStores>", broker.debug);
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
					com.sap.engine.services.security.remote.RemoteUserStore[] _result;
					obj = in.readObject();
					try {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore[])obj;
					} catch (ClassCastException ex) {
						 _result = (com.sap.engine.services.security.remote.RemoteUserStore[]) broker.narrow(obj,com.sap.engine.services.security.remote.RemoteUserStore[].class);
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


	public void registerUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("registerUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <registerUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.registerUserStore((com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration) broker.narrow(replicate(_param0),com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration.class));
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <registerUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <registerUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <registerUserStore>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(4);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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


	public void updateUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("updateUserStore(com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <updateUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.updateUserStore((com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration) broker.narrow(replicate(_param0),com.sap.engine.interfaces.security.userstore.config.UserStoreConfiguration.class));
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <updateUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <updateUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <updateUserStore>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(5);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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


	public void unregisterUserStore(java.lang.String _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("unregisterUserStore(java.lang.String)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <unregisterUserStore>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.unregisterUserStore( _param0);
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <unregisterUserStore>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <unregisterUserStore>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <unregisterUserStore>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(6);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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


	public void registerListener(com.sap.engine.services.security.remote.UserStoreListenerCallback _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("registerListener(com.sap.engine.services.security.remote.UserStoreListenerCallback)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <registerListener>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.registerListener((com.sap.engine.services.security.remote.UserStoreListenerCallback) broker.narrow(replicate(_param0),com.sap.engine.services.security.remote.UserStoreListenerCallback.class));
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <registerListener>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <registerListener>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <registerListener>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(7);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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


	public void unregisterListener(com.sap.engine.services.security.remote.UserStoreListenerCallback _param0) throws java.rmi.RemoteException {

		Object state = null;
		if (isLocal) {
			try {
				com.sap.engine.services.security.remote.RemoteUserStoreFactory remoteInterface = (com.sap.engine.services.security.remote.RemoteUserStoreFactory) p4remote.getDelegate();
				try{
					p4remote.checkPermission("unregisterListener(com.sap.engine.services.security.remote.UserStoreListenerCallback)");
				} catch (SecurityException sex) {
					broker.log("P4 Call exception: Exception in execute <unregisterListener>", broker.debug);
					broker.log(sex, broker.debug);
					throw new java.rmi.RemoteException("Security Exception", sex);
				}
				remoteInterface.unregisterListener((com.sap.engine.services.security.remote.UserStoreListenerCallback) broker.narrow(replicate(_param0),com.sap.engine.services.security.remote.UserStoreListenerCallback.class));
				return;
			} catch (java.lang.ClassCastException rex) {
				broker.log("P4 Call exception: Exception in execute <unregisterListener>", broker.debug);
				broker.log(rex, broker.debug);
			} catch (RuntimeException rex) {
				broker.log("P4 Call exception: Exception in execute <unregisterListener>", broker.debug);
				broker.log(rex, broker.debug);
					throw rex;
			} catch (java.rmi.RemoteException ex) {
				broker.log("P4 Call exception: Exception in execute <unregisterListener>", broker.debug);
				broker.log(ex, broker.debug);
				throw (java.rmi.RemoteException) replicate(ex);
			}
		}
			com.sap.engine.services.rmi_p4.Call call = null;
			try {
				call = newCall(8);
				P4ObjectOutput out = call.getOutputStream();
				out.writeObject( _param0);
				invoke(call);
				P4ObjectInput in = call.getResultStream();
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
