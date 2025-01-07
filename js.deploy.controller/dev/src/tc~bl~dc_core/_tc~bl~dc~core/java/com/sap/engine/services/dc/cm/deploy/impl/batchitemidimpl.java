package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;
import com.sap.engine.services.dc.repo.SduId;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class BatchItemIdImpl implements BatchItemId {

	private static final long serialVersionUID = -29401666968308653L;

	private final SduId sduId;
	private final int hashCode;
	private final String toString;

	private int idCount = -1;

	BatchItemIdImpl(SduId sduId) {
		this.sduId = sduId;

		this.hashCode = 17 + this.getSduId().hashCode();

		this.toString = getSduId().toString();
	}

	public SduId getSduId() {
		return this.sduId;
	}

	public int getIdCount() {
		return this.idCount;
	}

	public void setIdCount(int idCount) {
		this.idCount = idCount;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final BatchItemIdImpl otherSduId = (BatchItemIdImpl) obj;

		if (!this.getSduId().equals(otherSduId.getSduId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.toString;
	}

}
