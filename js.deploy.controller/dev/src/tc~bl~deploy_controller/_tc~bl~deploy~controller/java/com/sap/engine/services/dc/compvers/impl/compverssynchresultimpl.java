package com.sap.engine.services.dc.compvers.impl;

import com.sap.engine.services.dc.compvers.CompVersSynchResult;
import com.sap.engine.services.dc.compvers.CompVersSynchStatus;

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
final class CompVersSynchResultImpl implements CompVersSynchResult {

	private int failed;
	private int successes;
	private CompVersSynchStatus compVersSynchStatus;
	private String resultText;

	CompVersSynchResultImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.compvers.CompVersSynchResult#getTotal()
	 */
	public int getTotal() {
		return this.getFailed() + this.getSuccesses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.compvers.CompVersSynchResult#getFailed()
	 */
	public int getFailed() {
		return this.failed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersSynchResult#getSuccesses()
	 */
	public int getSuccesses() {
		return this.successes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.compvers.CompVersSynchResult#getResultText()
	 */
	public String getResultText() {
		return this.resultText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.compvers.CompVersSynchResult#
	 * getCompVersSynchStatus()
	 */
	public CompVersSynchStatus getCompVersSynchStatus() {
		if (this.compVersSynchStatus == null) {
			if (this.failed > 0) {
				if (this.successes > 0) {
					return CompVersSynchStatus.COMPVERS_SYNC_PARTIALLY_FAILED;
				} else {
					return CompVersSynchStatus.COMPVERS_SYNC_FAILED;
				}
			} else {
				return CompVersSynchStatus.COMPVERS_SYNC_OK;
			}
		} else {
			return this.compVersSynchStatus;
		}
	}

	void incrementFailed() {
		this.failed++;
	}

	void incrementSuccesses() {
		this.successes++;
	}

	void setResultText(String text) {
		this.resultText = text;
	}

	void setCompVersSynchStatus(CompVersSynchStatus status) {
		this.compVersSynchStatus = status;
	}

}
