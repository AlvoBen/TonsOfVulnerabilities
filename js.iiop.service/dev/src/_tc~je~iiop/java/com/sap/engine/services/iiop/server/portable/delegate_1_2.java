/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server.portable;

import com.sap.engine.services.iiop.internal.giop.ClientRequest;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.ORB;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.TAG_CODE_SETS;

public class Delegate_1_2 extends Delegate {

  public Delegate_1_2(IOR ior) {
    super(ior);
  }

  public ClientRequest createRequestLocal(String operation, int id, boolean response) {
    Profile codeSetsProfile = ior.getProfile(TAG_CODE_SETS.value);
    CodeSetChooser codeSetChooser = (codeSetsProfile != null) ? codeSetsProfile.getCsChooser() : null;

    return new ClientRequest_1_2(orb, operation, id, response, codeSetChooser);
  }

  /* Iner Class for Dispatch*/
  public class ClientRequest_1_2 extends ClientRequest {

    /*tozi class izrazqwa GIOP MEssage Format-a i e bazov za izprashtashti se messagi*/
    public ClientRequest_1_2(ORB orb, String operation, int id, boolean response, CodeSetChooser codeSetChooser) {
      super(orb, operation, id, response, codeSetChooser);
      versionMajor = 0x01;
      versionMinor = 0x02;
      set_minor_version(2);
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
      data[7] = 0;
      int size = index;
      index = 8;
      write_long(size - 12);
      index = size;
    }

    public void writeMessageHeader() {
      write_long(request_id);

      if (response) {
        write_octet((byte) 0x03);
      } else {
        write_octet((byte) 0x00);
      }

      write_octet((byte) 0); // 3 reserved ocets
      write_octet((byte) 0);
      write_octet((byte) 0);
      // TARGET
      write_long(0); // key ADDR
      write_long(ior.getProfile().getObjectKeyLength());
      write_octet_array(ior.getProfile().getObjectKey_ForSend(), 0, ior.getProfile().getObjectKeyLength());
      write_string(operation);
      writeServiceContexts(requestContexts);
      align(8);
      dataPosition = index;
    }

  }

  public org.omg.CORBA.portable.InputStream forwardRequest(com.sap.engine.services.iiop.internal.ORB orb0, org.omg.CORBA.Object obj, ClientRequest out) throws ApplicationException, RemarshalException{
    IOR newIOR = new IOR(orb, out.reply);
    this.bfwd_ior = ior;
    this.ior = newIOR;
    this.connection = sender.getConnection(ior);
    byte[] wholeMessage = out.toByteArray();
    int dataLength = wholeMessage.length - out.getDataPosition();
    byte[] withoutHeader = new byte[dataLength];
    System.arraycopy(wholeMessage, out.getDataPosition(), withoutHeader, 0, dataLength);
    out.goToMessageHeaderPosition();
    // null all data without GIOP header
    for (int i = 0; i < (wholeMessage.length - 12); i++) {
      out.write_octet((byte) 0);
    }
    out.goToMessageHeaderPosition();

    out.writeMessageHeader();
    out.write_octet_array(withoutHeader, 0 , withoutHeader.length);

    wholeMessage = out.toByteArray();
    int newSize = wholeMessage.length - 12;
    sender.ct.intToArr(newSize, wholeMessage, 8);
    return invoke(obj, out);
  }
}

