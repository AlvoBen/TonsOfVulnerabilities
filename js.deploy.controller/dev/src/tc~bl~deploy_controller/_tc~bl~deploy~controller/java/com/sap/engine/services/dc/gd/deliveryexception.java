/*
 * Created on 2004-4-20
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.dc.gd;

import com.sap.engine.services.dc.util.exception.DCBaseException;

/**
 * @author georgi-d
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeliveryException extends DCBaseException {

	private static final long serialVersionUID = 5235558580153319518L;

	public DeliveryException(String patternKey) {
		super(patternKey);
	}

	public DeliveryException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public DeliveryException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public DeliveryException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}

}
