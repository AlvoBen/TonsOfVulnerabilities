/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Dec 16, 2005
 */
package com.sap.engine.services.dc.event;

import com.sap.engine.services.dc.repo.Sdu;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Dec 16, 2005</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class LCEvent extends DCEvent {
	private static final long serialVersionUID = 2966848993220371633L;
	private final Sdu sdu;
	private final LCEventAction action;
	private final String toString;
	private final String[] errors;
	private final String[] warnings;

	public LCEvent(Sdu sdu, LCEventAction action, String[] errors,
			String[] warnings) {
		super(sdu, action);
		this.sdu = sdu;
		this.action = action;
		this.errors = errors;
		this.warnings = warnings;
		this.toString = genToString();
	}

	public Sdu getSdu() {
		return this.sdu;
	}

	public LCEventAction getLCEventAction() {
		return this.action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.event.DCEvent#accept(com.sap.engine.services
	 * .dc.event.DCEventVisitor)
	 */
	public void accept(DCEventVisitor visitor) {
		visitor.visit(this);
	}

	public String[] getErrors() {
		return this.errors;
	}

	public String[] getWarnings() {
		return this.warnings;
	}

	public String toString() {
		return this.toString;
	}

	private String genToString() {
		return "LC Event, action: " + this.action + ", sdu: " + this.sdu;
	}

}
