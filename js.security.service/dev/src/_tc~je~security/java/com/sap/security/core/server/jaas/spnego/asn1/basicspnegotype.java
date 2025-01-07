package com.sap.security.core.server.jaas.spnego.asn1;

import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.util.Utils;

public class BasicSpNegoType {
  private byte[] mechListMIC;

  /**
   * @param byteArray
   */
  public void setMechListMIC(byte[] byteArray) {
    mechListMIC = byteArray;
  }

  /**
   * @return
   */
  public byte[] getMechListMIC() {
    return mechListMIC;
  }

  /////////////////////////////////////////////////////////    
  //
  //      H E L P E R  M E T H O D S
  //    
  /////////////////////////////////////////////////////////    
  protected void appendOctetStringToSB(StringBuffer buffer, byte[] bytes) {
    for (int i = 0; i < IConstants.OCTETSTRING_DUMP_LIMIT; i++) {
      buffer.append(Integer.toHexString(Utils.getRawIntFromByte(bytes[i])));
    }
  }
}
