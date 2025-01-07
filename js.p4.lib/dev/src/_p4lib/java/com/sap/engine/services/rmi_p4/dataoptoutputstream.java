package com.sap.engine.services.rmi_p4;

import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * User: I024084
 * Date: 2006-1-25
 * Time: 11:27:39
 */
public class DataOptOutputStream extends DataOutputStream {

  private static Class remoteRefClass = RemoteRef.class;
  private static Class p4RemoteObjectClass = P4RemoteObject.class;
  public P4ObjectBroker broker = P4ObjectBroker.init();
  public ByteArrayOutputStream outStream = null;
  public boolean check = false;

  public DataOptOutputStream(ByteArrayOutputStream bout){
    super(bout);
    this.outStream = bout;
  }

  public void close() throws IOException {
    super.close();
  }

  public byte[] toByteArray() {
    return outStream.toByteArray();
  }

  protected void writeData(byte[] target, int offset) {
    ((ByteArrayOutput) outStream).writeData(target, offset);
  }

  protected int getSize() {
    return ((ByteArrayOutput) outStream).getSize();
  }

  protected byte[] getBuffer() {
    //System.err.println("outStream = " + outStream + "<> " + outStream.getClass());
    return ((ByteArrayOutput) outStream).getBuffer();
  }
}
