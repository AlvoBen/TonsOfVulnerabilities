package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.lang.Convert;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public final class ProtocolHeader {

  public final static int HEADER_SIZE = 14;
  //OFFSETS
  public final static int VERSION = 0;
  public final static int SIZE = 2;
  public final static int DESTINATION_SERVER_ID = 6;
  public final static int SENDER_SERVER_ID = 10;
  public final static int CALL_ID = 14;
  public static final int THREAD_CONTEXT = 9;  //this is without the 14byte header
  public final static int MESSAGE_TYPE = 22;
  public final static int THREAD_CONTEXT_SIZE = 23;
  public final static int END_PROTOCOL = 27;
  public int size = -1;
  public int sender_server_id = -1; // must specificate Call or Dipatch Message
  public int destination_server_id = -1;

  public static void writeHeader(byte[] message, int offset, int size, int destination_serverId) {
    Convert.writeIntToByteArr(message, offset + SIZE, size - HEADER_SIZE);
    Convert.writeIntToByteArr(message, offset + DESTINATION_SERVER_ID, destination_serverId);
    Convert.writeIntToByteArr(message, offset + SENDER_SERVER_ID, P4ObjectBroker.getBroker().getId());
  }

  public static int getMessageSizeFromArray(byte[] _inArray, int _offset) {
    return Convert.byteArrToInt(_inArray, _offset);
  }

  public byte[] toByteArray() {
    byte[] ret = new byte[HEADER_SIZE];
    Convert.writeIntToByteArr(ret, SIZE, size);
    Convert.writeIntToByteArr(ret, DESTINATION_SERVER_ID, destination_server_id);
    Convert.writeIntToByteArr(ret, SENDER_SERVER_ID, P4ObjectBroker.init().getId());
    return ret;
  }

  public void loadFromByteArray(byte[] array) {
    size = Convert.byteArrToInt(array, SIZE);
    destination_server_id = Convert.byteArrToInt(array, DESTINATION_SERVER_ID);
    sender_server_id = Convert.byteArrToInt(array, SENDER_SERVER_ID);
  }

}

