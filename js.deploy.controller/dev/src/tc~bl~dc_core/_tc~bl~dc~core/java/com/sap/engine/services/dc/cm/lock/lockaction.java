package com.sap.engine.services.dc.cm.lock;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-11
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class LockAction implements Serializable {

	private static final long serialVersionUID = -2314737203760923277L;

	public transient static final LockAction DEPLOY = new LockAction(
			new Integer(0), "Deploy");

	public transient static final LockAction UNDEPLOY = new LockAction(
			new Integer(1), "Undeploy");

	public transient static final LockAction SESSION_ID = new LockAction(
			new Integer(2), "Session ID");

	public transient static final LockAction POST_PROCESS = new LockAction(
			new Integer(3), "Post Process");

	public transient static final LockAction SYNC_PROCESS = new LockAction(
			new Integer(4), "Sync Process");

	private transient static final Map LOCKS_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		LOCKS_MAP.put(DEPLOY.getName(), DEPLOY);
		LOCKS_MAP.put(UNDEPLOY.getName(), UNDEPLOY);
		LOCKS_MAP.put(SESSION_ID.getName(), SESSION_ID);
		LOCKS_MAP.put(POST_PROCESS.getName(), POST_PROCESS);
		LOCKS_MAP.put(SYNC_PROCESS.getName(), SYNC_PROCESS);
	}

	public static LockAction getLockActionByName(String name) {
		return (LockAction) LOCKS_MAP.get(name);
	}

	public static Collection getLockActions() {
		return LOCKS_MAP.values();
	}

	private LockAction(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof LockAction)) {
			return false;
		}

		LockAction other = (LockAction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
