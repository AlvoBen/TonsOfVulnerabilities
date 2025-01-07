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
package com.sap.engine.services.iiop.client.portable;

import com.sap.engine.services.iiop.internal.giop.ClientRequest;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.ORB;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.TAG_CODE_SETS;

/**
 *  @author Nikolai Neichev
 *  @version 4.0
 */
public class Delegate_1_1 extends Delegate {

  public Delegate_1_1(IOR ior) {
    super(ior);
  }

  public ClientRequest createRequestLocal(String operation, int id, boolean response) {
    Profile codeSetsProfile = ior.getProfile(TAG_CODE_SETS.value);
    CodeSetChooser codeSetChooser = (codeSetsProfile != null) ? codeSetsProfile.getCsChooser() : null;

    return new ClientRequest_1_1(orb, operation, id, response, codeSetChooser);
  }

  /* Iner Class for Dispatch*/
  public class ClientRequest_1_1 extends ClientRequest {

    /*tozi class izrazqwa GIOP MEssage Format-a i e bazov za izprashtashti se messagi*/
    public ClientRequest_1_1(ORB orb, String operation, int id, boolean response, CodeSetChooser codeSetChooser) {
      super(orb, operation, id, response, codeSetChooser);
      versionMajor = 0x01;
      versionMinor = 0x01;
      set_minor_version(1);
      //      this.target = target;
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
      writeServiceContexts(requestContexts);
      write_long(request_id);
      write_boolean(response);
      write_octet((byte) 0); // 3 reserved ocets
      write_octet((byte) 0);
      write_octet((byte) 0);
      write_long(ior.getProfile().getObjectKeyLength());
      write_octet_array(ior.getProfile().getObjectKey_ForSend(), 0, ior.getProfile().getObjectKeyLength());
      write_string(operation);
      write_long(0); //principal
    }
  }

  public org.omg.CORBA.portable.InputStream forwardRequest(com.sap.engine.services.iiop.internal.ClientORB orb0, org.omg.CORBA.Object obj, ClientRequest out) throws ApplicationException, RemarshalException{
    byte[] old_obj_key = ior.getProfile().getObjectKey();
    IOR new_ior = new IOR(orb, out.reply); //read from input stream
    this.ior = new_ior;
    String host = new_ior.getProfile().getHost();
    int port = new_ior.getProfile().getPort();
//    boolean useSSL = (new_ior.getProfile().getPort() == 0);
//    int[] d_c_IDs = sender.retrieveConnAndDispID(host, port, useSSL);
//    setDispIDAndConnID(d_c_IDs[0], d_c_IDs[1]);|
    setHostPort(host, port);
    byte[] new_obj_key = new_ior.getProfile().getObjectKey();
    int end_context_index = out.getEnd_contexts_index();

    if (new_obj_key.length == old_obj_key.length) {
      int last_index = out.byteArray_forSend_length();
      int reqID = com.sap.engine.services.iiop.internal.ClientORB.getIDFactory().requestID(); //request ID???
      out.setPos(end_context_index);
      out.write_long(reqID);
      out.setRequestId(reqID);

      out.setPos(end_context_index + 12); //to ObjectKey position +size
      out.write_octet_array(new_obj_key, 0, new_obj_key.length);
      out.setPos(last_index);
    } else {

      boolean toAlign8 = ((0 < new_obj_key.length%8) && (new_obj_key.length%8 <= 4) && ((old_obj_key.length%8 == 0) || (4 < old_obj_key.length%8)))
                       ||((0 < old_obj_key.length%8) && (old_obj_key.length%8 <= 4) && ((new_obj_key.length%8 == 0) || (4 < new_obj_key.length%8)));


      byte[] wholeMessage = out.toByteArray();

      out.goToMessageHeaderPosition();
      if (toAlign8) {
        out.write_long(2);
      } else {
        out.write_long(1);
      }


      out.write_octet_array(wholeMessage, 16, end_context_index - 16);
      //align to 8
      if (toAlign8) {
        out.write_long(ALIGN_CONTEXT);
        out.write_long(4);
        out.write_long(0);
      }
      out.setEnd_contexts_index(out.getPos());

      int reqID = com.sap.engine.services.iiop.internal.ClientORB.getIDFactory().requestID();
      out.setRequestId(reqID);
      out.write_long(reqID);
      //pass request ID to invoke(), the ID will be placed in GIOP header          TODO!!!!!

      out.write_octet_array(wholeMessage, end_context_index + 4, 4);  //flag
      out.write_long(new_obj_key.length);
      out.write_octet_array(new_obj_key, 0, new_obj_key.length);

      int possitionIndex = end_context_index + 12 + old_obj_key.length;
      int alignPossitions = (new_obj_key.length%4 == 0) ?  (0) : ((new_obj_key.length/4 + 1)*4 - new_obj_key.length);
      int alignIndex = (possitionIndex%4 == 0) ?  (possitionIndex) : ((possitionIndex/4 + 1)*4);
      out.write_octet_array(new byte[4], 0, alignPossitions);
      out.write_octet_array(wholeMessage, alignIndex, wholeMessage.length - alignIndex);
      int newSize = out.getPos() - 12;
      out.setByte(8, (byte) (newSize));
      out.setByte(9, (byte) ((newSize) >> 8));
      out.setByte(10, (byte) ((newSize) >> 16));
      out.setByte(11, (byte) ((newSize) >> 24));
    }
    return invoke(obj, out);
  }


}

