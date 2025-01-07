package com.sap.engine.services.dc.cm;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Introduce new supported functions in the API.
 * 
 * @version 1.0
 * 
 */
public final class SupportedFunction implements Serializable{
	
	private static final long serialVersionUID = -7546352160091558477L;

	/** Batch validation function */
	public transient static final SupportedFunction	BATCH_VALIDATION 
		= new SupportedFunction(new Integer(0), "BatchValidation");

	/** Undeployment of empty SCA function */
	public transient static final SupportedFunction UNDEPLOYMENT_OF_EMPTY_SCA 
		= new SupportedFunction(new Integer(1), "UndeploymentOfEmptySCA");
	
	/** Measurement */
	public static final SupportedFunction MEASUREMENT 
		= new SupportedFunction(new Integer(2), "Measurement");
	
	/** Undeployment with error */
	public static final SupportedFunction UNDEPLOYMENT_WITH_ERROR 
		= new SupportedFunction(new Integer(3), "UndeploymentWithError");
	
	private final Integer id;
	private final String name;

	private transient static final Set SUPPORTED_FUNCTION_MAP = new HashSet();

	static {
		SUPPORTED_FUNCTION_MAP.add(BATCH_VALIDATION);
		SUPPORTED_FUNCTION_MAP.add(UNDEPLOYMENT_OF_EMPTY_SCA);
		SUPPORTED_FUNCTION_MAP.add(MEASUREMENT);
		SUPPORTED_FUNCTION_MAP.add(UNDEPLOYMENT_WITH_ERROR);
	}

	public static boolean isFunctionSupported(SupportedFunction supportedFunction) {
		return SUPPORTED_FUNCTION_MAP.contains(supportedFunction);
	}
	
	
	private SupportedFunction(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Gets this function's name.
	 * 
	 * @return name of the function
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SupportedFunction)) {
			return false;
		}

		SupportedFunction other = (SupportedFunction) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}
}
