package com.sap.jms.interfaces;

import com.sap.jms.client.destination.JMSDestination;
import com.sap.jms.client.message.JMSMessage;
import com.sap.jms.server.ServerComponentAccessor;
import com.sap.jms.util.HexUtils;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

public class DSRInstrumentation {

    private static String DSR_PASSPORT_PROPERTY = "JMS_SAP_DSRPassport";

    private static LogService log = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
    private static String LNAME = "com.sap.jms.interfaces.DSRInstrumentation";
    
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

			dsr.beforeOnMessage(dsrId, msgLength, vpName, destinationName);
		  } catch (OutOfMemoryError error) {
		    	log.errorTrace(LNAME, "Application OutOfMemoryError happened while invoking beforeOnMessage() method. For more details see the trace file");
		    	throw error;
		    } catch (ThreadDeath threadError) {
		    	log.errorTrace(LNAME, "Application ThreadDeath happened while invoking beforeOnMessage() method. For more details see the trace file");
		    	throw threadError;
		    } catch (Throwable t) {//$JL-EXC$
		        // catch Throwable - to safeguard against different applications
		        // that throw ClassNotFound or something else
		        log.errorTrace(LNAME, "Application exception happened while invoking beforeOnMessage() method. For more details see the trace file");
		        log.exception(LogService.ERROR, LNAME, t);
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
		try {
			dsr.afterOnMessage();
  	    } catch (OutOfMemoryError error) {
	     	log.errorTrace(LNAME, "Application OutOfMemoryError happened while invoking afterOnMessage() method. For more details see the trace file");
	     	throw error;
	    } catch (ThreadDeath threadError) {
	    	log.errorTrace(LNAME, "Application ThreadDeath happened while invoking afterOnMessage() method. For more details see the trace file");
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        log.errorTrace(LNAME, "Application exception happened while invoking afterOnMessage() method. For more details see the trace file");
	        log.exception(LogService.ERROR, LNAME, t);
	    }
	}

	public static void onReceive(JMSMessage message, String vpName) {
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

			dsr.onReceive(dsrId, msgLength, vpName, destinationName);
	    } catch (OutOfMemoryError error) {
	    	log.errorTrace(LNAME, "Application OutOfMemoryError happened while invoking onReceive() method. For more details see the trace file");
	    	throw error;
	    } catch (ThreadDeath threadError) {
	    	log.errorTrace(LNAME, "Application ThreadDeath happened while invoking onReceive() method. For more details see the trace file");
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        log.errorTrace(LNAME, "Application exception happened while invoking onReceive() method. For more details see the trace file");
	        log.exception(LogService.ERROR, LNAME, t);
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

			message.setStringProperty(DSR_PASSPORT_PROPERTY, hex);
	    } catch (OutOfMemoryError error) {
	    	log.errorTrace(LNAME, "Application OutOfMemoryError happened while invoking beforeSend() method. For more details see the trace file");
	    	throw error;
	    } catch (ThreadDeath threadError) {
	    	log.errorTrace(LNAME, "Application ThreadDeath happened while invoking beforeSend() method. For more details see the trace file");
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        log.errorTrace(LNAME, "Application exception happened while invoking beforeSend() method. For more details see the trace file");
	        log.exception(LogService.ERROR, LNAME, t);
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
		try {

			dsr.afterSend();
	    } catch (OutOfMemoryError error) {
	      	log.errorTrace(LNAME, "Application OutOfMemoryError happened while invoking afterSend() method. For more details see the trace file");
	      	throw error;
	    } catch (ThreadDeath threadError) {
	     	log.errorTrace(LNAME, "Application ThreadDeath happened while invoking afterSend() method. For more details see the trace file");
	    	throw threadError;
	    } catch (Throwable t) {//$JL-EXC$
	        // catch Throwable - to safeguard against different applications
	        // that throw ClassNotFound or something else
	        log.errorTrace(LNAME, "Application exception happened while invoking afterSend() method. For more details see the trace file");
	        log.exception(LogService.ERROR, LNAME, t);
	    }
	}
}
