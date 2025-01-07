package com.sap.bc.cts.tp.net;

import java.io.*;
import java.net.Socket;

public class Connection
{
  private Socket client = null;
  private Service service = null;
  private NetComm netComm = null;
  private boolean used = false;
  private boolean closed = false;
  private static boolean set = true;
  private static boolean get = false;
  private static boolean noValue = false;

  public Connection (Socket _client, Service _service) {
    this.client = _client;
    this.service = _service;
  }

  public Connection (Socket _client, Service _service, NetComm nc) {
    this.client = _client;
    this.service = _service;
    netComm = nc;
  }

  public synchronized boolean startUsing() {
       return setGetUsed(true);
  }

  public void finishedUsing() {
    // call only after successful startUsing
    setGetUsed(false);
  }

  private synchronized boolean setGetUsed(boolean value) {
    if(true == value && true == used){
      // set used only if it was not set true before
      return false;
    }
    else{
      used = value;
      return used;
    }
  }

  public void setClosing() {
    this.closed = true;
  }

  public boolean isClosed() {
    return this.closed;
  }

  public Socket getClient() {
    return this.client;
  } // getClient

  public Service getService() {
    return this.service;
  } // getService

  public NetComm getNetComm() {
    return this.netComm;
  } // getService

  public String toString() {
    return new String(this.client.toString() + " / " + this.service.toString());
  } //toString
}
