package com.sap.engine.services.dc.sapcontrol;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
import com.sap.engine.services.dc.cm.CMException;

public class SapControlException extends CMException {

	private static final long serialVersionUID = 6809517318243934537L;

	public SapControlException(String errMessage) {
		super(errMessage);
	}

	public SapControlException(String errMessage, Throwable throwable) {
		super(errMessage, throwable);
	}

}
