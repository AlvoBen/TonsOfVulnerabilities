package com.sap.engine.services.dc.cm.server.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-5
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class BootstrapMode {

	private static final Map MODES_MAP = new HashMap();

	public static final BootstrapMode SKIP = new BootstrapMode(new Integer(0),
			"SKIP");

	public static final BootstrapMode DETECT = new BootstrapMode(
			new Integer(1), "DETECT");

	public static final BootstrapMode UNKNOWN = new BootstrapMode(
			new Integer(2), "UNKNOWN");

	private final Integer id;
	private final String name;

	private BootstrapMode(Integer id, String name) {
		this.id = id;
		this.name = name;

		MODES_MAP.put(this.name.toUpperCase(), this);
	}

	public static BootstrapMode getBootstrapModeByName(String modeName)
			throws NullPointerException {
		if (modeName == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003164 The argument 'modeName' cannot be null.");
		}

		return (BootstrapMode) MODES_MAP.get(modeName.toUpperCase());
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

		if (!(obj instanceof BootstrapMode)) {
			return false;
		}

		final BootstrapMode other = (BootstrapMode) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
