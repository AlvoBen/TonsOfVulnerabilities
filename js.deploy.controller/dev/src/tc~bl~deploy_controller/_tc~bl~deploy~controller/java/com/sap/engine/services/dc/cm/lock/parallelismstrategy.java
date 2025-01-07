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
package com.sap.engine.services.dc.cm.lock;

import java.io.Serializable;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.20
 */
public abstract class ParallelismStrategy implements Serializable {

	private transient static final long serialVersionUID = -7752898643868128374L;

	protected final Integer id;
	protected final String name;
	protected final String toString;

	protected ParallelismStrategy(Integer id, String name, String type) {
		this.id = id;
		this.name = name;
		this.toString = name + " " + type + " parallelism strategy";
	}

	protected Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ParallelismStrategy)) {
			return false;
		}

		ParallelismStrategy other = (ParallelismStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return id.hashCode();
	}

}
