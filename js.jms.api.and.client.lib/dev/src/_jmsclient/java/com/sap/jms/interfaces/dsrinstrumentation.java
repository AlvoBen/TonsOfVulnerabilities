package com.sap.jms.interfaces;

import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.server.ServerComponentAccessor;
import com.sap.jms.util.HexUtils;
import com.sap.jms.util.Logging;
import com.sap.tc.logging.Severity;

/**
 * Interface implemented by the DSR service. The implementation is registered by
 * the DSR service to the JMS after its start via call to method
 * <code>setJMSClientPassportManager()</code>. The methods are called by the
 * {@link DSRInstrumentation} class to mark the start or completion of 
 * send/receive/onMessage JMS operations
 * 
 * Note:
 * There is no method that marks the receipt of a JMS message and there is 
 * no DSR instrumentation of the synchronous receipt of a JMS messages due to following reasons:
 *  - it is possible to call receive several times within the same thread
 *    thus processing several messages with different DSR passports in the same thread. DSR cannot
 *    handle such situation
 *  - the total scenario processing depends on the time when receive() is called, so this is clearly
 *    application dependent and cannot be used for performance measurement
 *  - the receipt of a message can happen long after the sender has exited.
 *    Supporting this would bring huge support effort explaining why the total processing 
 *    of a message took 2 days (only to find eventually the receiver was down in between or did not
 *    call receive for whatever reason).
 * 
 * @see com.sap.jms.server.JMSServerFrame#setJMSClientPassportManager(JMSClientPassportManager)
 */
public class DSRInstrumentation {

    private static String DSR_PASSPORT_PROPERTY = "JMS_SAP_DSRPassport";

    private DSRInstrumentation() {
    	// nothing here
    }
    
    private static JMSClientPassportManager getInstance() {
    	JMSClientPassportManager dsrGateway = ServerComponentAccessor.getServerComponentInterface().getJMSClientPassportManager();
    	return dsrGateway;
    }
    
	public static void beforeOnMessage(JMSMessage message, String vpName) {
		// no DSR instrumentation for standalone clients
		if (ServerComponentAccessor.getServerComponentInterface() == null){
			return;
		}

		if (message == null) {
			return;
		}

		// when dsr tracing is not enabled
		JMSClientPassportManager dsr = getInstance();
		if (dsr == null) {
			return;	
		}

		try {
			int msgLength = message.getMessagePacket().getLength();
			JMSDestination destination = (JMSDestination)message.getJMSDestination();
			String destinationName = destination.getName();
			String dsrProperty = message.getStringProperty(DSR_PASSPORT_PROPERTY);
			// message was not sent with dsr enabled
			if (dsrProperty == null) {
				return;
			}
			
			byte[] dsrId = HexUtils.hexToBytes(dsrProperty);

			if (Logging.isWritable(DSRInstrumentation.class, Severity.DEBUG)) {
				Logging.log(DSRInstrumentation.class, Severity.DEBUG, "DSR instrumentation beforeOnMessage(): dsrProperty=", dsrProperty, ", msgLength=", Integer.valueOf(msgLength), ", destination=", destination);
			}
			
			dsr.beforeOnMessage(dsrId, msgLength, vpName, destinationName);
		  } catch (OutOfMemoryError error) {
    		        if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
    		            Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application OutOfMemoryError happened while invoking beforeOnMessage() method. For more details see the trace file");
    		        }
		    	throw error;
		    } catch (ThreadDeath threadError) {
			if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
    			Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application ThreadDeath happened while invoking beforeOnMessage() method. For more details see the trace file");
			}
		    	throw threadError;
		    } catch (Throwable t) {//$JL-EXC$
		        // catch Throwable - to safeguard against different applications
		        // that throw ClassNotFound or something else
		        Logging.exception(DSRInstrumentation.class, t, "Application exception happened while invoking beforeOnMessage() method. For more details see the trace file");
		    }

	}

	public static void afterOnMessage() {
		// no DSR instrumentation for standalone clients
		if (ServerComponentAccessor.getServerComponentInterface() == null){
			return;
		}

		// when dsr tracing is not enabled
		JMSClientPassportManager dsr = getInstance();
		if (dsr == null) {
			return;	
		}
		
		if (Logging.isWritable(DSRInstrumentation.class, Severity.DEBUG)) {
			Logging.log(DSRInstrumentation.class, Severity.DEBUG, "DSR instrumentation afterOnMessage()");
		}
		
		try {
			dsr.afterOnMessage();
  	    } catch (OutOfMemoryError error) {
  		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
  		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application OutOfMemoryError happened while invoking afterOnMessage() method. For more details see the trace file");
  		}
	     	throw error;
	    } catch (ThreadDeath threadError) {
		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application ThreadDeath happened while invoking afterOnMessage() method. For more details see the trace file");
		}
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        Logging.exception(DSRInstrumentation.class, t, "Application exception happened while invoking afterOnMessage() method. For more details see the trace file");
	    }
	}

	public static void beforeSend(JMSMessage message, String vpName) {
		// no DSR instrumentation for standalone clients
		if (ServerComponentAccessor.getServerComponentInterface() == null){
			return;
		}

		if (message == null) {
			return;
		}

		// when dsr tracing is not enabled
		JMSClientPassportManager dsr = getInstance();
		if (dsr == null) {
			return;	
		}

		try {
			int msgLength = message.getMessagePacket().getLength();
			JMSDestination destination = (JMSDestination)message.getJMSDestination();
			String destinationName = destination.getName();

			byte[] dsrId = dsr.beforeSend(msgLength, vpName, destinationName);

			String hex= HexUtils.bytesToHex(dsrId);

			if (Logging.isWritable(DSRInstrumentation.class, Severity.DEBUG)) {
				Logging.log(DSRInstrumentation.class, Severity.DEBUG, "DSR instrumentation beforeSend(): dsrProperty=", hex, ", msgLength=", Integer.valueOf(msgLength), ", destination=", destination);
			}
			
			message.setStringProperty(DSR_PASSPORT_PROPERTY, hex);
	    } catch (OutOfMemoryError error) {
		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application OutOfMemoryError happened while invoking beforeSend() method. For more details see the trace file");
		}
	    	throw error;
	    } catch (ThreadDeath threadError) {
		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application ThreadDeath happened while invoking beforeSend() method. For more details see the trace file");
		}
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        Logging.exception(DSRInstrumentation.class, t, "Application exception happened while invoking beforeSend() method. For more details see the trace file");
	    }
	}

	public static void afterSend() {
		// no DSR instrumentation for standalone clients
		if (ServerComponentAccessor.getServerComponentInterface() == null){
			return;
		}

		// when dsr tracing is not enabled
		JMSClientPassportManager dsr = getInstance();
		if (dsr == null) {
			return;	
		}

		if (Logging.isWritable(DSRInstrumentation.class, Severity.DEBUG)) {
			Logging.log(DSRInstrumentation.class, Severity.DEBUG, "DSR instrumentation afterSend()");
		}
		
		try {
			dsr.afterSend();
	    } catch (OutOfMemoryError error) {
		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application OutOfMemoryError happened while invoking afterSend() method. For more details see the trace file");
		}
	      	throw error;
	    } catch (ThreadDeath threadError) {
		if (Logging.isWritable(DSRInstrumentation.class, Severity.ERROR)) {
		    Logging.log(DSRInstrumentation.class, Severity.ERROR, "Application ThreadDeath happened while invoking afterSend() method. For more details see the trace file");
		}
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        Logging.exception(DSRInstrumentation.class, t, "Application exception happened while invoking afterSend() method. For more details see the trace file");
	    }
	}
}
