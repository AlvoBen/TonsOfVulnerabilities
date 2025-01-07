package com.sap.jms.client.connection;

import javax.jms.JMSException;
import com.sap.jms.util.Logging;
import com.sap.jms.util.Task;
import com.sap.tc.logging.Severity;

public class ExceptionListenerCaller implements Task {
    
    private Connection openConnection;
    private JMSException jmsException;
    private ClassLoader classLoader;

    ExceptionListenerCaller(Connection openConnection, JMSException jmsException, ClassLoader classLoaderToUse) {
    	this.openConnection = openConnection;
        this.jmsException = jmsException;
        this.classLoader = classLoaderToUse;
    }

    public void execute() {
        ClassLoader oldClassLoader = null;
        try {
            if (classLoader != null) {
              oldClassLoader = Thread.currentThread().getContextClassLoader();
              Thread.currentThread().setContextClassLoader(classLoader);
            }                 
            if (Logging.isWritable(this, Severity.INFO)) {
                Logging.log(this, Severity.INFO, "Exception Listener from a new application thread will be called for JMS connection ", openConnection, ", classloader will be set to ", classLoader);
            }  
            openConnection.onException(jmsException);
        } finally {
            if (oldClassLoader != null) {
              Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }           	
    }
    
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
