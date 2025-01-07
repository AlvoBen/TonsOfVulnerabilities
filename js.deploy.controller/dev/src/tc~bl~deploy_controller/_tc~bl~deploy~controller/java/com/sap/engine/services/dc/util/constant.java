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
package com.sap.engine.services.dc.util;

import java.io.IOException;
import java.io.Serializable;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class Constant implements Serializable {

	static final long serialVersionUID = -590328664368193037L;

	private final Byte id;
	private String name;

	public Constant(Byte id, String name) {
		this.id = id;
		this.name = StringUtils.intern(name);
	}

	public Byte getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return getName();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		Constant other = (Constant) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.name = StringUtils.intern(this.name);
	}

}
