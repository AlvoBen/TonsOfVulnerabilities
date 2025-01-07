/**
 * JMSInputStream.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2003.
 * All rights reserved.
 */
package com.sap.jms.protocol;

import com.sap.jms.JMSConstants;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

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
        	if (Logging.isWritable(this, Severity.ERROR)) {
        		Logging.log(this, Severity.ERROR, "Exception was thrown trying to load the class of an ObjectMessage, will use objClass.forClass().");
        	}
            Logging.exception(this, ex);
            return objClass.forClass();
        }
    }
}
