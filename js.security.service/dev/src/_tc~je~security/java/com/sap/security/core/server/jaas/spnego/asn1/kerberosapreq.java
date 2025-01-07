/*
 * Created on 2006-12-1
 */
package com.sap.security.core.server.jaas.spnego.asn1;

/**
 * @author Georgi Dimitrov ( I031654 )
 * 
 */

public class KerberosApReq {

  // AP-REQ ::= [APPLICATION 14] SEQUENCE {
  // pvno[0] INTEGER,
  // msg-type[1] INTEGER,
  // ap-options[2] APOptions,
  // ticket[3] Ticket,
  // authenticator[4] EncryptedData
  // }
  //
  //  
  // Ticket ::= [APPLICATION 1] SEQUENCE {
  // tkt-vno[0] INTEGER,
  // realm[1] Realm,
  // sname[2] PrincipalName,
  // enc-part[3] EncryptedData
  // }
  //
  // PrincipalName ::= SEQUENCE {
  // name-type[0] INTEGER,
  // name-string[1] SEQUENCE OF GeneralString
  // }
  //
  // Realm ::= GeneralString
  //
  //

  // constants for msgType
  public final static int KRB_AS_REQ = 10;
  public final static int KRB_AS_REP = 11;
  public final static int KRB_TGS_REQ = 12;
  public final static int KRB_TGS_REP = 13;
  public final static int KRB_AP_REQ = 14;
  public final static int KRB_AP_REP = 15;
  public final static int KRB_SAFE = 20;
  public final static int KRB_PRIV = 21;
  public final static int KRB_CRED = 22;
  public final static int KRB_ERROR = 30;

  private int pvno;
  private int msgType;
  private byte[] apOptions;
  private KerberosTicket ticket;

  public KerberosApReq(byte[] byteArray) {

    int offset = 0;
    boolean isCurrentTypeCorrect = true;

    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_APPLICATION, true);

    // spnego part
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);// jump to OID
    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);// jump to Boolean
    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);// jump to AP-REQ
    // end of spnego part

    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_APPLICATION, true, 14); // AP-REQ
                                                                                                        // ::=
                                                                                                        // [APPLICATION
                                                                                                        // 14]
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);// get in the
                                                                  // constructed
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_UNIVERSAL, true, TLVParser.TAG_SEQUENCE);
    // ap-req is a sequence
    // 0=pvno
    // 1=msg-type
    // 2=ap-options
    // 3=ticket
    // 4=authenticator
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 0);// (ap-req)SequenceElement[0]
    this.pvno = TLVParser.getSubElementValueToInt(byteArray, offset);

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 1);// (ap-req)SequenceElement[1]
    this.msgType = TLVParser.getSubElementValueToInt(byteArray, offset);

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 2);// (ap-req)SequenceElement[2]
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    this.apOptions = TLVParser.getTLV_ValueBytes(byteArray, offset);

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 3);// (ap-req)SequenceElement[3]
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_APPLICATION, true, 1); // Ticket
                                                                                                        // ::=
                                                                                                        // [APPLICATION
                                                                                                        // 1]
    this.ticket = new KerberosTicket(byteArray, offset);

    // offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    // isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset,
    // TLVParser.CLASS_CONTEXTSPECIFIC, true,4);//(ap-req)SequenceElement[3]
    // do not get authenticator

  }

  public KerberosApReq() {
  }

  public int getPvno() {
    return pvno;
  }

  public int getMsgType() {
    return msgType;
  }

  public byte[] getAPOptions() {
    return apOptions;
  }

  public KerberosTicket getTicket() {
    return ticket;
  }

  public String toString() {

    StringBuffer buffer = new StringBuffer();
    buffer.append("KerberosApReq: [");
    buffer.append("\n  protocol version number: " + pvno);
    buffer.append("\n  protocol message type : " + getMsgTypeString());
    buffer.append("\n  apOptions: " + TLVParser.getStringForBytes(apOptions));
    buffer.append("\n  ticket: " + ticket.toString());
    buffer.append("\n  ]");
    return buffer.toString();

  }

  /**
   * 
   * 
   * Message Types
   * 
   * String value description
   * -----------------------------------------------------------------
   * KRB_AS_REQ 10 Request for initial authentication KRB_AS_REP 11 Response to
   * KRB_AS_REQ request KRB_TGS_REQ 12 Request for authentication based on TGT
   * KRB_TGS_REP 13 Response to KRB_TGS_REQ request KRB_AP_REQ 14 application
   * request to server KRB_AP_REP 15 Response to KRB_AP_REQ_MUTUAL KRB_SAFE 20
   * Safe (checksummed) application message KRB_PRIV 21 Private (encrypted)
   * application message KRB_CRED 22 Private (encrypted) message to forward
   * credentials KRB_ERROR 30 Error response
   * 
   * @return MessageTypeConstant
   */
  public String getMsgTypeString() {

    switch (msgType) {
    case KRB_AS_REQ:
      return "KRB_AS_REQ";
    case KRB_AS_REP:
      return "KRB_AS_REP";
    case KRB_TGS_REQ:
      return "KRB_TGS_REQ";
    case KRB_TGS_REP:
      return "KRB_TGS_REP";
    case KRB_AP_REQ:
      return "KRB_AP_REQ";
    case KRB_AP_REP:
      return "KRB_AP_REP";
    case KRB_SAFE:
      return "KRB_SAFE";
    case KRB_PRIV:
      return "KRB_PRIV";
    case KRB_CRED:
      return "KRB_CRED";
    case KRB_ERROR:
      return "KRB_ERROR";
    default:
      return "KRB_UNKNOWN";
    }

  }

}
