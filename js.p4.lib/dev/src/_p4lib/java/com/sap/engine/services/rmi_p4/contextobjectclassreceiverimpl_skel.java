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
package com.sap.engine.services.rmi_p4;

import java.rmi.server.Operation;

/**
 *
 * @author RMIC Generator 
 * @version 6.30
 */
public class ContextObjectClassReceiverImpl_Skel implements com.sap.engine.services.rmi_p4.Skeleton {

	public P4ObjectBroker broker = P4ObjectBroker.init();
	private static final Operation[] operations = {
			new Operation("getClassByName(java.lang.String)")};

	public ContextObjectClassReceiverImpl_Skel () {
	}

	public Operation[] getOperations() {
		return operations;
	}

	private static final String[] _implements = {
			"com.sap.engine.services.rmi_p4.ContextObjectClassReceiver"};

	public String[] getImplemntsObjects() {
		return _implements;
	}

	public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws Exception {

		com.sap.engine.services.rmi_p4.ContextObjectClassReceiverImpl impl = (com.sap.engine.services.rmi_p4.ContextObjectClassReceiverImpl) remote;
		P4ObjectInput in = call.getInputStream();

		switch (opnum) {

			case 0 : {  //method public abstract java.lang.Class com.sap.engine.services.rmi_p4.ContextObjectClassReceiver.getClassByName(java.lang.String) throws java.rmi.RemoteException
				try {
					Object obj0;
					java.lang.String param0;
					obj0 = in.readObject();
					try {
						 param0 = (java.lang.String)obj0;
					} catch (ClassCastException ex) {
						 param0 = (java.lang.String) P4ObjectBroker.init().narrow(obj0,java.lang.String.class);
					}
					java.lang.Class _result = impl.getClassByName(param0);
					P4ObjectOutput out = call.getOutputStream();
					if(_result != null){
            if (P4ClassWrapper.TO_USE_CLASS_WRAPPERS) {
              out.writeObject(new com.sap.engine.services.rmi_p4.P4ClassWrapper(_result));
            } else {
              out.writeObject(_result);
            }
          } else {
						out.writeObject(null);
					}
					out.flush();
				} catch (Exception ex) {
					throw ex;
				}
				break;
			}
		}
	}

}
