package com.sap.jms.client.connection;

/**
 * The interface represent an object that can be closed. It is used to avoid the
 * closing and the IO invocations in the finalization method that could potentially
 * block the gc 
 */
public interface Closeable {
            
        public void close() throws javax.jms.JMSException;

}
