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
package com.sap.engine.services.deploy.server.dpl_info.module;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.deploy.container.util.Constant;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class InitiallyStarted extends Constant {

	static final long serialVersionUID = 325380306489298821L;

	private static byte count = -1;

	public static final InitiallyStarted NO = new InitiallyStarted(new Byte(
			(byte) ++count), "NO");

	public static final InitiallyStarted YES = new InitiallyStarted(new Byte(
			(byte) ++count), "YES");

	private static final Map NAME_START_INITIALLY_MAP = new HashMap();
	static {
		NAME_START_INITIALLY_MAP.put(NO.getName(), NO);
		NAME_START_INITIALLY_MAP.put(YES.getName(), YES);
	}

	private static final Map ID_START_INITIALLY_MAP = new HashMap();
	static {
		ID_START_INITIALLY_MAP.put(NO.getId(), NO);
		ID_START_INITIALLY_MAP.put(YES.getId(), YES);
	}

	public static Map getNameAndStartInitially() {
		return NAME_START_INITIALLY_MAP;
	}

	public static InitiallyStarted getStartInitiallyByName(String name) {
		return (InitiallyStarted) NAME_START_INITIALLY_MAP.get(name);
	}

	public static InitiallyStarted getDefaultInitiallyStarted() {
		return InitiallyStarted.YES;
	}

	public InitiallyStarted(Byte id, String name) {
		super(id, name);
	}

}
