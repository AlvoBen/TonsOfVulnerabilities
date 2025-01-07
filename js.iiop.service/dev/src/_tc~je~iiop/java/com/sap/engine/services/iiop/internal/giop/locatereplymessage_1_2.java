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

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class LocateReplyMessage_1_2 extends LocateReplyMessage {

  /* Request msg - GIOP 1_0*/
  protected byte[] object_key = null;
  protected byte[] principal = null;

  public LocateReplyMessage_1_2(byte[] binaryData) {
    super(binaryData);
  }

  public LocateReplyMessage_1_2(byte[] binaryData, int size) {
    super(binaryData, size);
  }

  public byte[] object_id() {
    return object_key;
  }

  protected void readGIOPHeader() {
    versionMajor = data[4];
    versionMinor = data[5];
    int order = (data[6] & 0x01);

    if (order == 0) {
      littleEndian = false;
    } else {
      littleEndian = true;
    }

    int fragment = (data[6] & 0x02);

    if (fragment == 0) {
      isFragmented = false;
    } else {
      isFragmented = true;
    }

    reset(12);
  }

  protected void readMessageHeader() {
    request_id = read_long();
    int stType = unaligned_read_long(); // statustype

    switch (stType) {
      case 0: {
        // unknown_object
        break;
      }
      case 1: {
        // object_here
        break;
      }
      case 2: {
        // object_forward
        break;
      }
      case 3: {
        // object_forward_perm
        break;
      }
      case 4: {
        // loc_system_exception
        break;
      }
      case 5: {
        // loc_needs_addressing_mode
        break;
      }
    }

    align(8);
  }

}

