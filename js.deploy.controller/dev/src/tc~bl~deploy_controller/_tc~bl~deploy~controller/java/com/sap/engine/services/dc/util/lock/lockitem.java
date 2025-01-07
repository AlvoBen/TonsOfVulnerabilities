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
package com.sap.engine.services.dc.util.lock;

import java.io.Serializable;

import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.ValidatorUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class LockItem implements Serializable {

	private static final long serialVersionUID = -213374204371919947L;
	final SduId sduId;
	final LockType lockType;

	public LockItem(SduId sduId, LockType lockType) {
		ValidatorUtils.validateNull(sduId, "SduId");
		this.sduId = sduId;
		ValidatorUtils.validateNull(lockType, "LockType");
		this.lockType = lockType;
	}

	/**
	 * 
	 * NOTE: Will create SdaID
	 * 
	 * @param name
	 * @param vendor
	 * @param lockType
	 */
	public LockItem(String name, String vendor, LockType lockType) {
		this(RepositoryComponentsFactory.getInstance()
				.createSdaId(name, vendor), lockType);
	}

	/**
	 * @return Returns the lockType.
	 */
	public LockType getLockType() {
		return lockType;
	}

	/**
	 * @return Returns the sduId.
	 */
	public SduId getSduId() {
		return sduId;
	}

	public String toString() {
		return getSduId().toString() + " " + getLockType().getName();
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

		LockItem other = (LockItem) obj;

		if (!this.getSduId().equals(other.getSduId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return getSduId().hashCode();
	}
}
