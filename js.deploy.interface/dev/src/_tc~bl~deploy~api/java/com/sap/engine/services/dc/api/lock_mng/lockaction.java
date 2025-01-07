package com.sap.engine.services.dc.api.lock_mng;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Specifies for which action the lock should be performed.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-26</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class LockAction {
	/**
	 * Lock the server for &quot;Deploy&quot; operation.
	 */
	public static final LockAction DEPLOY = new LockAction(new Integer(0),
			"Deploy");

	private static final Map LOCKS_MAP = new HashMap();

	private final Integer id;
	private final String name;

	static {
		LOCKS_MAP.put(DEPLOY.getName(), DEPLOY);
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
		return this.name;
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
		return this.id.hashCode();
	}

}