/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.client;

import com.sap.engine.services.iiop.internal.giop.OutgoingMessage;
import com.sap.engine.services.iiop.internal.util.IDFactoryItem;
import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.client.pool.ThreadPool;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class Connection implements Runnable, GIOPMessageConstants {
  Socket socket;
  InputStream in;
  OutputStream out;
  ConnectionParser parser;
  byte[] header = new byte[GIOP_HEADER_LENGTH];
  ThreadPool threadPool = null;
  IDFactoryItem item = null;
  CommunicationLayerImpl communicationLayer = null;
  long connectionId = 0;
  boolean isClientOpenedConnection = false;

  /* Construcor for ServerSocket
  */
  public Connection(Socket socket) throws IOException {
    this.isClientOpenedConnection = false;
    this.connectionId = 0;
    this.socket = socket;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
    this.parser = new ConnectionParser();
    this.communicationLayer = null;
    threadPool = new ThreadPool(5);
  }

  /* Construcor for opened out sockets
  */
  public Connection(long connectionId, Socket socket, CommunicationLayerImpl commLayer) throws IOException {
    this.isClientOpenedConnection = true;
    this.connectionId = connectionId;
    this.socket = socket;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
    this.parser = new ConnectionParser();
    this.communicationLayer = commLayer;
    threadPool = new ThreadPool(5);
//    this.threadPool = null; //todo Vancho experiment da se vidi zashto trjabva poll
  }

  public void send(IDFactoryItem item, byte[] data, int pos, int len) throws IOException {
    this.item = item;
    out.write(data, pos, len);
    out.flush();
  }

  public void run() {
    try {
      while (true) {
        //read header
        int readed = 0;
        while(((readed += in.read(header, readed, header.length - readed)) != -1) && (readed < header.length));
        if (readed == -1) {
          return; //stream closed
        }
        byte minorVersion = header[5];
        byte endian = header[6]; // GIOP 1.0
        if (minorVersion != 0) {
          endian &= 0x01; //GIOP 1.1 or 1.2
        }

        int msgSize = 0;
        if (endian == 0) { // MSB
          msgSize = (header[8] << 24) & 0xff000000;
          msgSize |= (header[9] << 16) & 0x00ff0000;
          msgSize |= (header[10] << 8) & 0x0000ff00;
          msgSize |= header[11] & 0x000000ff;
        } else { // LSB
          msgSize = header[8] & 0x000000ff;
          msgSize |= (header[9] << 8) & 0x0000ff00;
          msgSize |= (header[10] << 16) & 0x00ff0000;
          msgSize |= (header[11] << 24) & 0xff000000;
        }
        byte[] request = new byte[msgSize + GIOP_HEADER_LENGTH];
        System.arraycopy(header, 0, request, 0, GIOP_HEADER_LENGTH);
        while(((readed += in.read(request, readed, request.length - readed)) != -1) && (readed < request.length));
        if (readed == -1) {
          return; // stream closed
        }
        // message type
        if ( ( (request[7] == REQUEST) && ((request[6] & 0x02) == 0) ) ||
             (request[7] == LOCATE_REQUEST) ||
             ( (request[7] == FRAGMENT) && ((request[6] & 0x02) == 0) )
            ) { // invoke is expected !!!
          threadPool.startWork(new RequestProcessor(out, request), true);
        } else {
          OutgoingMessage outMessage = parser.readMessage(request);
          if (outMessage != null && outMessage.forSend()) {
            synchronized (out) {
              try {
                byte[] toSend = outMessage.toByteArray();
                out.write(toSend);
                out.flush();
              } catch (Exception e) {
                if (LoggerConfigurator.getLocation().beError()) {
                  LoggerConfigurator.getLocation().errorT("Connection.run()", "Connection to " + socket.getRemoteSocketAddress() + " lost....\n");
                }
              }
            }
          } else {
            item = null;
          }
        }
      }
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation().beDebug()) {
        LoggerConfigurator.getLocation().errorT("Connection.run()", "Connection to " + socket.getRemoteSocketAddress() + " lost....\n");
      }
    } finally {
      if (item != null) {
        synchronized (item) {
          item.notify();
        }
      }

      if (isClientOpenedConnection) { //not server socket
        communicationLayer.connectionFailed(connectionId);
      }
//      } else { //todo Vancho experiment da se vidi zashto trjabva poll  v isClientOpenedConnection
        threadPool.finish();
//      }
    }
  }

  private class RequestProcessor implements Runnable {

    private OutputStream out;
    private byte[] request;

    public RequestProcessor(OutputStream out, byte[] request) {
      this.out = out;
      this.request = request;
    }

    public void run() {
      OutgoingMessage outMessage = parser.readMessage(request);
      if (outMessage != null && outMessage.forSend()) {
        synchronized (out) {
          try {
            byte[] toSend = outMessage.toByteArray();
            out.write(toSend, 0, toSend.length);
            out.flush();
          } catch (Exception e) {
            if (LoggerConfigurator.getLocation().beError()) {
              LoggerConfigurator.getLocation().errorT("Connection$RequestProcessor.run()", "Connection to " + socket.getRemoteSocketAddress() + " lost....\n" + LoggerConfigurator.exceptionTrace(e));
            }
          }
        }
      } else {
        item = null;
      }
    }
  }

}
