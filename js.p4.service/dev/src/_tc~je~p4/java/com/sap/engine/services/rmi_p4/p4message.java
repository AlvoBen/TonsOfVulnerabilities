package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.server.*;
import com.sap.engine.interfaces.cross.CrossMessage;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.frame.core.thread.exception.RejectedExecutionException;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Author: Asen Petrov
 * Date: 2005-3-11
 * Time: 16:51:18
 */
public class P4Message extends Message implements CrossMessage, Runnable {

  private Connection connection;
  private P4MessageProcessor msgProcessor;
  private ServerDispatchImpl dispatch;
  private boolean execute = false;
  public P4SessionProcessor process = null;
  private P4ObjectBroker broker; 
 

  public P4Message(P4MessageProcessor msgProcessor, P4SessionProcessor processor) {
    this.msgProcessor = msgProcessor;
    process = processor;
    call_id = new byte[8];
    broker = P4ObjectBroker.getBroker();
  }

  public void execute() {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("P4Message.execute()","Executing queued message");
    }
    //Needed for application and temporary threads.
    ThreadWrapper.pushTask("Processing a p4 message", ThreadWrapper.TS_PROCESSING);
    try{
      process();
      release();
    }finally{
      ThreadWrapper.popTask();  
    }
  }

  public void process() {
    try {
      synchronized(msgProcessor) {
       msgProcessor.busyThreads++;
      }
      if (type == Message.CALL_REQUEST) {
          if (!execute) {
            execute = true;
             try {
               msgProcessor.getConnector().executeRequest(this);
             } catch (RejectedExecutionException ree) {
               SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.rt0007", "Processing RMI-P4 request failed. There are no resources to handle the request. This may happen when the load is too big. Increase the P4 request queue size or number of concurrent threads");
               dispatch = new ServerDispatchImpl(this, P4ObjectBroker.init(), process, connection);
               dispatch.throwException(new P4RuntimeException("Processing RMI-P4 request failed. Server is overloaded, there are no resources to handle the request. Increase the p4 request queue size or number of concurrent threads"));
             }
             return;
          }
      }
      if (broker != null) {
        broker.beginMeasure("P4/Process_P4_Message", P4Message.class);
      }
      dispatch = new ServerDispatchImpl(this, P4ObjectBroker.init(), process, connection);
      dispatch.run();
      if (broker != null) {
        broker.endMeasure("P4/Process_P4_Message");
      }
    } catch (Exception e) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "P4Message.process()", "Processing RMI-P4 request failed. Exception: {0}", "ASJ.rmip4.rt1001", new Object[] {P4Logger.exceptionTrace(e)});
      }
      if (broker != null) {
        broker.endMeasure("P4/Process_P4_Message");
      }
    } finally {
      synchronized(msgProcessor) {
        msgProcessor.busyThreads--;
      }
    }
  }


  public Connection getConnection() {
    return connection;
  }

  public void setData(byte[] data, int length) {
    this.offBody = ProtocolHeader.HEADER_SIZE;
    this.size = length;
    this.own_id = Convert.byteArrToInt(data, 6);
    this.request = data;
    parsed = false;
    System.arraycopy(request, offBody, call_id, 0, 8); // protocol call id
    type = request[offBody + 8];
    this.clusterEl_id = this.sender_id = Convert.byteArrToInt(data, 10);
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
    //this.clusterEl_id = connection.getPeerId(); //This leed to problem with initial context between clusters. Also seems not to be needed.
  }

  public void release() {
    if (inCall) {
      /* do not release if in call */
      inCall = false;
      return;
    }
    if ((!execute)) {
      msgProcessor.releaseMessage(this);
    }
  }

}
