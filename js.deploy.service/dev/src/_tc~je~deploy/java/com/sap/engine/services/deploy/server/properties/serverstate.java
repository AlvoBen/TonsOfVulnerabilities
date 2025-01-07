/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.properties;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.deploy.container.util.Constant;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ServerState extends Constant {

	static final long serialVersionUID = -1940483872324642955L;

	private static byte count = -1;

	public static final ServerState SAFE_DEPLOY = new ServerState(new Byte(
			(byte) ++count), "SAFE_DEPLOY");

	public static final ServerState SAFE_UPGRADE = new ServerState(new Byte(
			(byte) ++count), "SAFE_UPGRADE");

	public static final ServerState SAFE_MIGRATE = new ServerState(new Byte(
			(byte) ++count), "SAFE_MIGRATE");

	public static final ServerState SAFE_APP_MIGRATE = new ServerState(
			new Byte((byte) ++count), "SAFE_APP_MIGRATE");

	public static final ServerState SAFE_SWITCH = new ServerState(new Byte(
			(byte) ++count), "SAFE_SWITCH");

	public static final ServerState NORMAL_NONE = new ServerState(new Byte(
			(byte) ++count), "NORMAL_NONE");

	private static final Map SERVER_STATE_MAP = new HashMap();

	static {
		SERVER_STATE_MAP.put(SAFE_DEPLOY.getName(), SAFE_DEPLOY);
		SERVER_STATE_MAP.put(SAFE_UPGRADE.getName(), SAFE_UPGRADE);
		SERVER_STATE_MAP.put(SAFE_MIGRATE.getName(), SAFE_MIGRATE);
		SERVER_STATE_MAP.put(SAFE_APP_MIGRATE.getName(), SAFE_APP_MIGRATE);
		SERVER_STATE_MAP.put(SAFE_SWITCH.getName(), SAFE_SWITCH);
		SERVER_STATE_MAP.put(NORMAL_NONE.getName(), NORMAL_NONE);
	}

	public static Map getNameAndAccessModifier() {
		return SERVER_STATE_MAP;
	}

	public ServerState(Byte id, String name) {
		super(id, name);
	}

	public boolean isValid4ContainerMigration() {
		return ServerState.SAFE_MIGRATE.equals(this) ||
			ServerState.SAFE_APP_MIGRATE.equals(this) || 
			ServerState.SAFE_SWITCH.equals(this);
	}

	public boolean isAppStartNOTAcceptable() {
		return ServerState.SAFE_UPGRADE.equals(this) ||
			isValid4ContainerMigration();
	}
}