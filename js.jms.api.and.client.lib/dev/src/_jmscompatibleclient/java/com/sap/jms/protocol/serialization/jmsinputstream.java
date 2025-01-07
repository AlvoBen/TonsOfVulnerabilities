/**
 * JMSInputStream.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol.serialization;

import com.sap.jms.JMSConstants;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializing client object message using certain classloader
 *
 * @author  Radoslav Nikolov
 * @version 6.30
 */
public class JMSInputStream extends ObjectInputStream implements JMSConstants {

    private ClassLoader appClassLoader = null;
   
    private final static String LOG_COMPONENT = "serialization.JMSInputStream";
    private static final LogService logService = LogServiceImpl.getLogService(LogServiceImpl.PROTOCOL_LOCATION);
    
    public JMSInputStream(InputStream inStream, ClassLoader classLoader) throws IOException {
        super(inStream);
        appClassLoader = classLoader;
    }
    
    /**
     * resolveClass
     */
    public Class resolveClass(ObjectStreamClass objClass) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(objClass.getName(), false, appClassLoader);
        } catch (ClassNotFoundException ex) {
            logService.errorTrace(LOG_COMPONENT, "Exception was thrown trying to load the class of an ObjectMessage, will use objClass.forClass().");
            logService.exception(LOG_COMPONENT, ex);
            return objClass.forClass();
        }
    }
}
