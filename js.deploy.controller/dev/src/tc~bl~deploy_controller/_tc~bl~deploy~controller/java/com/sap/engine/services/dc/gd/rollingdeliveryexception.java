package com.sap.engine.services.dc.gd;

/**
 * 
 * @author I031421
 * @deprecated The exception will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */

public class RollingDeliveryException extends DeliveryException {

	private static final long serialVersionUID = 4131933165302026505L;

	public RollingDeliveryException(String patternKey) {
		super(patternKey);
	}

	public RollingDeliveryException(String patternKey, Object[] parameters) {
		super(patternKey, parameters);
	}

	public RollingDeliveryException(String patternKey, Throwable cause) {
		super(patternKey, cause);
	}

	public RollingDeliveryException(String patternKey, Object[] parameters,
			Throwable cause) {
		super(patternKey, parameters, cause);
	}
}
