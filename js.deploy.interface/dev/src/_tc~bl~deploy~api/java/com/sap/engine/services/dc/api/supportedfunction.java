package com.sap.engine.services.dc.api;

/**
 * Introduce new supported functions in the API.
 * 
 * @author Todor Atanasov
 * @version 1.0
 * 
 */
public final class SupportedFunction {
	
	/** Batch validation function */
	public static final SupportedFunction BATCH_VALIDATION 
		= new SupportedFunction(new Integer(0), "BatchValidation");
	
	/** Undeployment of empty SCA function */
	public static final SupportedFunction UNDEPLOYMENT_OF_EMPTY_SCA 
		= new SupportedFunction(new Integer(1), "UndeploymentOfEmptySCA");
	
	/** Measurement */
	public static final SupportedFunction MEASUREMENT 
		= new SupportedFunction(new Integer(2), "Measurement");
	
	/** Undeployment with error */
	public static final SupportedFunction UNDEPLOYMENT_WITH_ERROR 
		= new SupportedFunction(new Integer(3), "UndeploymentWithError");


	private final Integer id;
	private final String name;

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
