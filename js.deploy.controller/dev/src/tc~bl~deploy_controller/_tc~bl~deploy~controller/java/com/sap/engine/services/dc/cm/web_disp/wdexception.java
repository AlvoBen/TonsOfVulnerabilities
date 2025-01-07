package com.sap.engine.services.dc.cm.web_disp;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class WDException extends CMException {

	private static final long serialVersionUID = 104728015550878605L;

	public WDException(String errMessage) {
		super(errMessage);
	}

	public WDException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
