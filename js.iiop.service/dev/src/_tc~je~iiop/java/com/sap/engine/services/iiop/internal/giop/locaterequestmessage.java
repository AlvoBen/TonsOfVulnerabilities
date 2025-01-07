package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.services.iiop.server.IIOPMessageProcessor;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.system.ThreadWrapper;
import com.sap.engine.lib.lang.Convert;

import java.io.IOException;

/**
 * Implementation of LocateRequest GIOP message. Send by a client to find
 * where the searched object is located.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public abstract class LocateRequestMessage extends IncomingMessage {

  //private byte[] object_key = new byte[0];
  protected ClientReply reply = null;
  protected int request_id;

  public LocateRequestMessage(byte[] binaryData) {
    super(binaryData);
  }

  public LocateRequestMessage(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  public int request_id() {
    return request_id;
  }

  public void process_initial() {
    readMessageHeader();
    reply = createServerReply();
    reply.writeMessageHeader();
    reply.flushData();
  }

  public void process() {
    busyThreads++;
    ThreadWrapper.pushSubtask("processing locate request", ThreadWrapper.TS_PROCESSING);
    try {
      ConnectionMetaData metaData = (ConnectionMetaData) connection.getMetaData();
      if (fragmented()) {
        metaData.storeFragment(request_id(), this);
        return;
      }

      process_initial();

      byte[] reqId = new byte[8];
      Convert.writeLongToByteArr(reqId, 0, request_id());

      try {
        connection.sendReply(reply.toByteArray_forSend(), reply.getPos(), reqId);
      } catch (IOException e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("Communication failure : " + LoggerConfigurator.exceptionTrace(e));
        }
      }

      IIOPMessageProcessor.fcaConnector.releaseBuffer(this.getData());
    } finally {
      busyThreads--;
      ThreadWrapper.popSubtask();
    }
  }

  protected abstract ClientReply createServerReply();

  protected abstract void readGIOPHeader();

  protected abstract void readMessageHeader();

  public OutgoingMessage getServerReply() {
    return reply;
  }

}

