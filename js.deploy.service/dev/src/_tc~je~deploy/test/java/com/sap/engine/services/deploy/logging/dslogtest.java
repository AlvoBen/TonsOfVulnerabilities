/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.logging;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.sap.tc.logging.Location;

import junit.framework.TestCase;

/**
 * @author Luchesar Cekov
 */
public class DSLogTest extends TestCase {
	
	private static final Location location = 
		Location.getLocation(DSLogTest.class);

  private static final Object[] arguments = new Object[] {"fileName", "classLoaderName"};
  private static final String messageKey = "deploy_1006";
  private ResourceBundle bundle;
  private static final String TEST = "Test ";
  private static final String LOG = "Log ";
  private static final String TRACE = "Trace ";
  private final static String DEBUG = "Debug ";
  private final static String INFO = "Info ";
  private final static String PATH = "Path ";
  private final static String ERROR = "Error with Wrong Key";
  private final static String WARNING = "Warning with Wrong Key";
  
  public void setUp() throws Exception {
    bundle = ResourceBundle.getBundle("com.sap.engine.services.deploy.DeployResourceBundle", Locale.getDefault());
  }
  /*
   * Class under test for String getLocalizedMessage(String, Object[])
   */
  public void testGetLocalizedMessageStringObjectArray() throws Exception {    
    String message = DSLog.getLocalizedMessage(messageKey, arguments);
    String original = getLocalizedMessage(messageKey, arguments);
    assertEquals(original, message);
  }  
  
  public void testLogTrace(){
	  DSLog.logInfo(location, "","");
	  DSLog.logInfo(location, "", "", TEST, LOG, INFO);
	  
	  DSLog.traceInfo(location, "","");
	  DSLog.traceInfo(location, "","", TEST, TRACE, INFO);
	  
	  DSLog.traceDebug(location, "","");
	  DSLog.traceDebug(location, "","", TEST, TRACE, DEBUG);
	  
	  DSLog.tracePath(location, "","");
	  DSLog.tracePath(location, "","", TEST, TRACE, PATH);
	  }
  
  public void testWarningError(){
	  DSLog.logError(location, "","", ERROR);
	  DSLog.logWarning(location, "","", WARNING);
	  DSLog.traceError(location, "","", ERROR);
	  DSLog.traceWarning(location, "","", WARNING);
	  
	  DSLog.logError(location, "","", ERROR, new Object(){});
	  DSLog.logWarning(location, "","", WARNING, null);
	  DSLog.traceError(location, "","", ERROR, new Object(){});
	  DSLog.traceWarning(location, "","", WARNING, null);
	  
	  DSLog.logWarning(location, "","", messageKey, arguments);
  }
  
  /**
   * @param arguments
   * @param messageKey
   * @return
   */
  private String getLocalizedMessage(String messageKey, final Object[] arguments) {
    String pattern = bundle.getString(messageKey);
    String result = (arguments == null ? pattern : MessageFormat.format(pattern, arguments));
    return result; 
  }
}


