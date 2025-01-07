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
package com.sap.engine.mejb.notification;

import java.rmi.server.Operation;

import com.sap.engine.services.rmi_p4.*;

/**
 *
 * @author RMIC Generator 
 * @version 6.30
 */
public class NotificationListenerRemoteImplp4_Skel extends P4RemoteObject  implements com.sap.engine.services.rmi_p4.Skeleton, java.rmi.Remote {

	public P4ObjectBroker broker = P4ObjectBroker.init();
	private static final Operation[] operations = {
			new Operation("handleNotification(javax.management.Notification,java.lang.Object)")};

	public NotificationListenerRemoteImplp4_Skel () {
	}

	public Operation[] getOperations() {
		return operations;
	}

	private static final String[] _implements = {
			"com.sap.engine.mejb.notification.NotificationListenerRemote"};

	public String[] getImplemntsObjects() {
		return _implements;
	}

	public void dispatch(java.rmi.Remote remote, Dispatch call, int opnum) throws java.lang.Exception {

		com.sap.engine.mejb.notification.NotificationListenerRemoteImpl impl = (com.sap.engine.mejb.notification.NotificationListenerRemoteImpl) delegate();
		P4ObjectInput in = call.getInputStream();

		switch (opnum) {

			case 0 : {  //method public abstract void com.sap.engine.mejb.notification.NotificationListenerRemote.handleNotification(javax.management.Notification,java.lang.Object)
				try {
					Object obj0;
					javax.management.Notification param0;
					obj0 = in.readObject();
					try {
						 param0 = (javax.management.Notification)obj0;
					} catch (java.lang.ClassCastException ex) {
					// $JL-EXC$
						 param0 = (javax.management.Notification) broker.narrow(obj0,javax.management.Notification.class);
					}
					Object obj1;
					java.lang.Object param1;
					obj1 = in.readObject();
					try {
						 param1 = (java.lang.Object)obj1;
					} catch (java.lang.ClassCastException ex) {
					// $JL-EXC$
						 param1 = (java.lang.Object) broker.narrow(obj1,java.lang.Object.class);
					}
					impl.handleNotification(param0, param1);
					P4ObjectOutput out = call.getOutputStream();
				} catch (java.lang.Exception ex) {
					// $JL-EXC$
					throw ex;
				}
				break;
			}
		}
	}

}
