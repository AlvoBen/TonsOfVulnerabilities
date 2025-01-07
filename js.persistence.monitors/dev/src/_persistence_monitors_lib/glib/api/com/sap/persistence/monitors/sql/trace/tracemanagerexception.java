package com.sap.persistence.monitors.sql.trace;



/**
 * Class for all SQLTrace exceptions thrown by the persistence monitors API. 
 * Copyright (c) 2008, SAP-AG
 * 
 * @author Enno Folkerts
 */
public class TraceManagerException extends Exception  {

    




/**
	 * 
	 */
	private static final long serialVersionUID = 493512147122420622L;
public TraceManagerException() {
    super();
}

public TraceManagerException(String message) {
    super(message);
}

public TraceManagerException(Throwable cause) {
    super(cause);
}

public TraceManagerException(String message, Throwable cause) {
    super(message, cause);
}



    //-----------
    // Constants ---------------------------------------------------------------
    //-----------

   
    public static final String EXTERNAL_ERROR    = "0000";
    
    
   /* public static final String FILE_NOT_FOUND    = "0010";
    public static final String NOTHING_TO_DO     = "0020";
    public static final String PARAMETER_MISSING = "0030";
    public static final String PARAMETER_WRONG   = "0040";
    public static final String RECORD_NOT_FOUND  = "0050";
    public static final String NULL_NOT_ALLOWED   = "0060";
    public static final String ASSERTION_VIOLATED = "0070"; */
    
      
   

   
}
