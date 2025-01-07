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
package com.sap.engine.services.security.remoteimpl.resource;

import java.rmi.server.Operation;

import com.sap.engine.services.rmi_p4.*;

/**
 *
 * @author Georgy Stanev 
 * @version 4.0
 */
public class RemoteResourceHandleImplp4_Skel extends P4RemoteObject  implements com.sap.engine.services.rmi_p4.Skeleton, java.rmi.Remote {

	public P4ObjectBroker broker = P4ObjectBroker.init();
	private static final Operation[] operations = {
			new Operation("ungroupInstance(java.lang.String)"),
			new Operation("getParent(java.lang.String)"),
			new Operation("createInstance(java.lang.String)"),
			new Operation("removeInstance(java.lang.String)"),
			new Operation("renameResource(java.lang.String)"),
			new Operation("removeAction(java.lang.String)"),
			new Operation("getAlias()"),
			new Operation("groupInstance(java.lang.String,java.lang.String)"),
			new Operation("getActions()"),
			new Operation("createAction(java.lang.String)"),
			new Operation("getChildren(java.lang.String)")};

	public RemoteResourceHandleImplp4_Skel () {
	}

	public Operation[] getOperations() {
		return operations;
	}

	private static final String[] _implements = {
			"com.sap.engine.services.security.remote.resource.RemoteResourceHandle"};

	public String[] getImplemntsObjects() {
		return _implements;
	}

	public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws Exception {

		com.sap.engine.services.security.remoteimpl.resource.RemoteResourceHandleImpl impl = (com.sap.engine.services.security.remoteimpl.resource.RemoteResourceHandleImpl) getDelegate();
		P4ObjectInput in = call.getInputStream();

		switch (opnum) {

			case 0 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.ungroupInstance(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.ungroupInstance(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 1 : {  //method public abstract java.lang.String com.sap.engine.services.security.remote.resource.RemoteResourceHandle.getParent(java.lang.String) throws java.rmi.RemoteException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					java.lang.String _result = impl.getParent(param0);
					P4ObjectOutput out = call.getOutputStream();
					out.writeObject( _result);
					out.flush();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 2 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.createInstance(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.createInstance(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 3 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.removeInstance(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.removeInstance(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 4 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.renameResource(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.renameResource(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 5 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.removeAction(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.removeAction(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 6 : {  //method public abstract java.lang.String com.sap.engine.services.security.remote.resource.RemoteResourceHandle.getAlias() throws java.rmi.RemoteException
				try {
					java.lang.String _result = impl.getAlias();
					P4ObjectOutput out = call.getOutputStream();
					out.writeObject( _result);
					out.flush();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 7 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.groupInstance(java.lang.String,java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					Object obj1;
					java.lang.String param1;
					obj1 = in.readObject();
					try {
						 param1 = (java.lang.String)obj1;
					} catch (ClassCastException ex) {
						 param1 = (java.lang.String) broker.narrow(obj1,java.lang.String.class);
					}
					impl.groupInstance(param0, param1);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 8 : {  //method public abstract java.lang.String[] com.sap.engine.services.security.remote.resource.RemoteResourceHandle.getActions() throws java.rmi.RemoteException
				try {
					java.lang.String[] _result = impl.getActions();
					P4ObjectOutput out = call.getOutputStream();
					out.writeObject( _result);
					out.flush();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 9 : {  //method public abstract void com.sap.engine.services.security.remote.resource.RemoteResourceHandle.createAction(java.lang.String) throws java.rmi.RemoteException,java.lang.SecurityException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					impl.createAction(param0);
					P4ObjectOutput out = call.getOutputStream();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
			case 10 : {  //method public abstract java.lang.String[] com.sap.engine.services.security.remote.resource.RemoteResourceHandle.getChildren(java.lang.String) throws java.rmi.RemoteException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) broker.narrow(obj0,java.lang.String.class);
					}
					java.lang.String[] _result = impl.getChildren(param0);
					P4ObjectOutput out = call.getOutputStream();
					out.writeObject( _result);
					out.flush();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
		}
	}

}
