/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.dpl_info.module;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.deploy.container.util.Constant;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class Version extends Constant {

	static final long serialVersionUID = -4129465575566680129L;

	private static byte count = 0;

	// This version is the default one, if there is no other in DB.
	// It means that the DeploymentInfo data is mixed, not separated. The key is
	// container name.
	public static final Version FIRST = new Version(new Byte((byte) ++count),
			"FIRST");

	// The DeploymentInfo is separated. The key is container name.
	public static final Version SECOND = new Version(new Byte((byte) ++count),
			"SECOND");

	private static final Map NAME_VERSION_MAP = new HashMap();
	static {
		NAME_VERSION_MAP.put(FIRST.getName(), FIRST);
		NAME_VERSION_MAP.put(SECOND.getName(), SECOND);
	}

	private static final Map ID_VERSION_MAP = new HashMap();
	static {
		ID_VERSION_MAP.put(FIRST.getId(), FIRST);
		ID_VERSION_MAP.put(SECOND.getId(), SECOND);
	}

	public static Map getNameAndVersion() {
		return NAME_VERSION_MAP;
	}

	public static Version getVersionByName(String name) {
		return (Version) NAME_VERSION_MAP.get(name);
	}

	public static Version getNewestVersion() {
		return (Version) ID_VERSION_MAP.get(new Byte(count));
	}

	public Version(Byte id, String name) {
		super(id, name);
	}

}
