package com.sap.engine.services.dc.manage.messaging;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class MessageConstants {

	public static final int MSG_TYPE_REGISTER_DC = 0;

	public static final int MSG_TYPE_GLOBAL_EVENT_MASK = 1 << 2;// 100

	public static final int MSG_TYPE_GLOBAL_EVENT_CLUSTER = MSG_TYPE_GLOBAL_EVENT_MASK | 1; // 101

	public static final int MSG_TYPE_GLOBAL_EVENT_DEPLOY = MSG_TYPE_GLOBAL_EVENT_MASK | 2; // 110

	public static final int MSG_TYPE_GLOBAL_EVENT_UNDEPLOY = MSG_TYPE_GLOBAL_EVENT_MASK | 3; // 111

	public static final int MSG_TYPE_ROLLING_EVENT_SYNC = 10;

	public static final int MSG_TYPE_ROLLING_EVENT_SYNCED = 11;

	private MessageConstants() {
	}

}
