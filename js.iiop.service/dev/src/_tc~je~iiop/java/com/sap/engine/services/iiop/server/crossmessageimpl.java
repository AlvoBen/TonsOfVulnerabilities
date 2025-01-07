package com.sap.engine.services.iiop.server;

import com.sap.engine.interfaces.cross.*;
import com.sap.engine.frame.core.thread.exception.RejectedExecutionException;
import com.sap.engine.services.iiop.internal.giop.IncomingMessage;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Author: Asen Petrov
 * Date: 2006-6-9
 * Time: 11:13:42
 */
public class CrossMessageImpl extends AbstractCrossMessage {

  private IncomingMessage message;
  private FCAConnector connector;

  protected CrossMessageImpl(IncomingMessage message, FCAConnector connector) {
    this.message = message;
    this.connector = connector;
  }

  public void execute() {
    ThreadWrapper.pushTask("Processing a iiop message", ThreadWrapper.TS_PROCESSING);
    try {
      message.process();
    } finally {
      ThreadWrapper.popTask(); 
    }
  }

  public int getProtocol() {
    return MessageProcessor.IIOP_PROCESSOR;
  }

  public void process() {
    try {
      connector.executeRequest(this);
    } catch (RejectedExecutionException ree) {
      SimpleLogger.log(Severity.WARNING, Category.SYS_SERVER, LoggerConfigurator.getLocation(), "ASJ.iiop.rt0001", "No resources to handle the request. Increase the IIOP request queue size or number of concurrent threads for parallel requests");
      message.generateNoResoucesErrorReply();
      message.process();
    }
  }

  public int getLength() {
    return message.getLength();
  }

  public byte[] getData() {
    return message.getData();
  }

  public Connection getConnection() {
    return message.getConnection();
  }

  public void setData(byte[] data, int length) {
    message.setData(data, length);
  }

  public void setConnection(Connection connection) {
    message.setConnection(connection);
  }

  public void release() {
    message.release();
  }
}
