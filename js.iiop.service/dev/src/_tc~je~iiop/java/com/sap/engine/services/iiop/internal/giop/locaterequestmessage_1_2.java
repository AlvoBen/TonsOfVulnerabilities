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

import com.sap.engine.services.iiop.system.CommunicationLayer;
import org.omg.CORBA.ORB;

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class LocateRequestMessage_1_2 extends LocateRequestMessage {

  /* Request msg - GIOP 1_0*/
  protected byte[] object_key;
  protected byte[] principal;

  public LocateRequestMessage_1_2(byte[] binaryData) {
    super(binaryData);
  }

  public LocateRequestMessage_1_2(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  public byte[] object_id() {
    return object_key;
  }

  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMinor = data[5];

    littleEndian = ((data[6] & 0x01) != 0);

    isFragmented = ((data[6] & 0x02) != 0);

    reset(12);
  }

  protected void readMessageHeader() {
    request_id = read_long();
    int addressinDisposition = read_short();

    switch (addressinDisposition) {
      case 0: {
        int s = read_long();
        object_key = new byte[s];
        read_octet_array(object_key, 0, object_key.length);
        // SENT KEY
        break;
      }
      case 1: {
        // SENT Profile
        break;
      }
      case 2: {
        // SENT Reference
        break;
      }
      default: {
        int s = read_long();
        object_key = new byte[s];
        read_octet_array(object_key, 0, object_key.length);
      }
    }
  }

  protected ClientReply createServerReply() {
    return new ClientLocateReply_1_2(orb);
  }

  /* Reply is inerclass of Incoming Request*/
  public class ClientLocateReply_1_2 extends ClientReply {

    /*tozi class izrazqwa GIOP MEssage Format-1_0*/
    protected ClientLocateReply_1_2(ORB orb) {
      super(orb, icsChooser);
      versionMajor = 0x01;
      versionMinor = 0x02;
      set_minor_version(2);
    }

    protected void writeGIOPHeader() {
      data[0] = 0x47;
      data[1] = 0x49;
      data[2] = 0x4f;
      data[3] = 0x50;
      data[4] = 0x01;
      data[5] = 0x02;
      data[6] = (byte) (littleEndian ? 0x01 : 0x00);
      //reply type
      data[7] = 4;
      int size = index;
      index = 8;
      write_long(size - 12);
      index = size;
    }

    protected void writeExceptionMessageHeader(byte type) {
        write_long(request_id);
        // locateStatusType
        org.omg.CORBA.portable.ObjectImpl target = (org.omg.CORBA.portable.ObjectImpl) CommunicationLayer.getORB().getObject(object_key);
        if (target == null) {
          write_long(0); // unknown_object
        } else {
          write_long(1); // object_here
        }
        // TODO
        // object_forward = 2
        // object_forward_perm = 3
        // loc_system_exception = 4
        // loc_needs_addressing_mode = 5
    }

    protected void writeMessageHeader() {
      goToMessageHeaderPosition();
      writeExceptionMessageHeader((byte) 0);
    }

  }

}

