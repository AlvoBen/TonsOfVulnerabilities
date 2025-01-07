/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jan 4, 2006
 */
package com.sap.engine.services.dc.api.event;

import java.util.Arrays;

import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.util.DAConstants;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 4, 2006</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public class LCEvent extends DAEvent {
	private final String[] errors;
	private final String[] warnings;
	private String toString;

	public LCEvent(Sdu sdu, LCEventAction lcEventAction, String[] errors,
			String[] warnings) {
		super(sdu, lcEventAction);
		this.errors = errors;
		this.warnings = warnings;
	}

	public Sdu getSdu() {
		return (Sdu) super.getUserObject();
	}

	public LCEventAction getLCEventAction() {
		return (LCEventAction) super.getAction();
	}

	public String[] getErrors() {
		return this.errors;
	}

	public String[] getWarnings() {
		return this.warnings;
	}

	public boolean hasErrors() {
		return this.errors != null && this.errors.length > 0;
	}

	public boolean hasWarnings() {
		return this.warnings != null && this.warnings.length > 0;
	}

	public String toString() {
		if (this.toString == null) {
			this.toString = "LC Event, action: " + super.getAction()
					+ ", sdu: " + super.getUserObject();
			if (hasErrors()) {
				this.toString += DAConstants.EOL_INDENT + "Errors:"
						+ Arrays.asList(this.errors);
			}
			if (hasWarnings()) {
				this.toString += DAConstants.EOL_INDENT + "Warnings:"
						+ Arrays.asList(this.warnings);
			}
		}
		return this.toString;
	}

}
