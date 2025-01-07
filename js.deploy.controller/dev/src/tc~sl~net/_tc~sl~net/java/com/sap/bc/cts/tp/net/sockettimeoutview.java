package com.sap.bc.cts.tp.net;

import java.net.Socket;
import java.net.SocketException;

/**
 * Title:        Software Delivery Manager
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Software Logistics - here: D019309
 *
 */
class SocketTimeoutView implements SocketTimeoutViewIF {
  
  Socket socket = null;
  private SocketTimeoutView(Socket socket) {
    this.socket = socket;
  }
  
  static SocketTimeoutView createSocketTimeoutView(Socket socket) {
    return new SocketTimeoutView(socket); 
  }
  public int getSoTimeout() throws SocketTimeoutViewException {
    int timeout = -1;
    try {
      timeout = this.socket.getSoTimeout();
    } catch (SocketException se) {
      throw new SocketTimeoutViewException(se.getMessage()); 
    }
     
    return timeout; 
    
  }
  public void setSoTimeout(int timeout) throws SocketTimeoutViewException {
    try {
      this.socket.setSoTimeout(timeout);
    } catch (SocketException se) {
      throw new SocketTimeoutViewException(se.getMessage()); 
    }
  }

}
