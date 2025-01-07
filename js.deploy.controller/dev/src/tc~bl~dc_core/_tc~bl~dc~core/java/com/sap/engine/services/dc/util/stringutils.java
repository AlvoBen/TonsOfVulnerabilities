package com.sap.engine.services.dc.util;

public final class StringUtils {

	private StringUtils() {
	}

	public static String intern(String string) {
		if (string == null) {
			return null;
		}
		return string.intern();
	}
	
	public static String getCauseMessage(Throwable th) {
		if (th == null) {
	      return null;
	    } 
	    if (th.getCause() != null) {
	      if (th.getCause().getCause() != null) {
	    	  return getCauseMessage(th.getCause());
	      } else {                
	        if (getMessage(th).indexOf(getMessage(th.getCause())) != -1) {
	          if ((getMessage(th.getCause()).indexOf("Hint") != -1) || 
	              (getMessage(th.getCause()).indexOf("Solution") != -1) ||
	              (getMessage(th.getCause()).indexOf("Reason") != -1)) {
	            return getMessage(th.getCause());
	          } else {
	        	return getMessage(th);
	          }
	        } else {
	        	return getMessage(th) + Constants.EOL_TAB_TAB + " -> " + getMessage(th.getCause());
	        }        
	      } 
	    } else {
	    	return getMessage(th);
	    }
	  }
	  
	  private static String getMessage(Throwable th) {
	    if (th.getLocalizedMessage() != null) {
	      return th.getLocalizedMessage();
	    }
	    if (th.getMessage() != null) {
	      return th.getMessage();
	    }  
	    return th.getClass().toString();  
	  }
}
