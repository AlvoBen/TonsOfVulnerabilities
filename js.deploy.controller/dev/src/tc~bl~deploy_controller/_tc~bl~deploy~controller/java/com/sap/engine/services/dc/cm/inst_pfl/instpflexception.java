package com.sap.engine.services.dc.cm.inst_pfl;

import com.sap.engine.services.dc.cm.CMException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class InstPflException extends CMException {

	private static final long serialVersionUID = -2287548620345567691L;

	public InstPflException(String errMessage) {
		super(errMessage);
	}

	public InstPflException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
