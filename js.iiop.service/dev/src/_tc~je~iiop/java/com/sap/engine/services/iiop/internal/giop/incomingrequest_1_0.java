/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.system.CommunicationLayer;
import org.omg.CORBA.ORB;


/**
 * This class implements GIOP 1.0 Message Request.
 *
 * @author Georgy Stanev
 * @version 6.30
 */
public class IncomingRequest_1_0 extends IncomingRequest { //$JL-SER$

  /* Request msg - GIOP 1_0*/
  protected byte[] principal = null;

  public IncomingRequest_1_0(byte[] binaryData) {
    super(binaryData);
  }

  public IncomingRequest_1_0(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMinor = data[5];
    int order = (data[6] & 0x01);

    littleEndian = (order != 0);

    reset(12);
  }

  protected void readMessageHeader() {
    requestContexts = readServiceContexts();
    request_id = read_long();
    response = read_boolean();
    int s = read_long();
    object_key = new byte[s];
    read_octet_array(object_key, 0, object_key.length);
    operation = read_string();
    try {
      if (java.util.Arrays.equals(object_key, GIOPMessageConstants.INIT_REQUEST_OPERATION) && operation.equals("get")) {
        targetHolder = CommunicationLayer.getORB().getNamingServant();
      } else if (java.util.Arrays.equals(object_key, GIOPMessageConstants.NAME_SERVICE_BYTES)) {
        targetHolder = CommunicationLayer.getORB().getNamingServant();
      } else {
        targetHolder = CommunicationLayer.getORB().getServant(object_key);
      }
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingRequest.readMessageHeader()", "Initializing target holder failed " +LoggerConfigurator.exceptionTrace(e));
      }
    }


    principal = new byte[read_long()];
    read_octet_array(principal, 0, principal.length);
  }


  protected ClientReply createLocalServerReply() {
    ClientReply_1_0 rep = new ClientReply_1_0(orb);
    rep.set_minor_version(minorVersion);
    return rep;
  }

  /* Reply is inerclass of Incoming Request*/
  public class ClientReply_1_0 extends ClientReply {

    /*tozi class izrazqwa GIOP MEssage Format-1_0*/
    protected ClientReply_1_0(ORB orb) {
      super(orb, icsChooser);
      versionMajor = 0x01;
      versionMinor = 0x00;
      set_minor_version(0);
      this.target = targetHolder == null ? null : (org.omg.CORBA.portable.ObjectImpl) targetHolder.getObject();
//      ocsChooser = icsChooser;
    }

    protected void writeGIOPHeader() {
      data[0] = 0x47;
      data[1] = 0x49;
      data[2] = 0x4f;
      data[3] = 0x50;
      data[4] = versionMajor;
      data[5] = versionMinor;
      data[6] = (byte) (littleEndian ? 0x01 : 0x00);
      //reply type
      data[7] = 1;
      int size = index;
      index = 8;
      write_long(size - 12);
      index = size;
    }

    protected void writeExceptionMessageHeader(byte type) {
      writeServiceContexts(replyContexts);
      write_long(request_id);
      write_long(type);
    }

    protected void writeMessageHeader() {
      writeExceptionMessageHeader((byte) 0); // 0 - NO_EXCEPTION
    }

  }

}

