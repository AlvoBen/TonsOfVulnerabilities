package com.sap.engine.services.iiop.internal.giop;


public class EmptyProcessMessage extends IncomingMessage {


  public EmptyProcessMessage(byte[] binaryData) {
    super(binaryData);
  }

  public void process() {
  }

  public void process_initial() {
  }

  protected void readGIOPHeader() {
  }

  protected void readMessageHeader() {
  }

  public int request_id() {
    return 0;
  }

  public OutgoingMessage getServerReply() {
    return null;
  }
}
