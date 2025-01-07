package com.sap.persistence.monitors.sql.connection;

public class ConnectionManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ConnectionManagerException() {
	    super();
	}

	public ConnectionManagerException(String message) {
	    super(message);
	}

	public ConnectionManagerException(Throwable cause) {
	    super(cause);
	}

	public ConnectionManagerException(String message, Throwable cause) {
	    super(message, cause);
	}
	
	
	 public static final String EXTERNAL_ERROR    = "0000";
	    public static final String FILE_NOT_FOUND    = "0010";
	    public static final String NOTHING_TO_DO     = "0020";
	    public static final String PARAMETER_MISSING = "0030";
	    public static final String PARAMETER_WRONG   = "0040";
	    public static final String RECORD_NOT_FOUND  = "0050";
	    public static final String NULL_NOT_ALLOWED   = "0060";
	    public static final String ASSERTION_VIOLATED = "0070"; 
	    
}
