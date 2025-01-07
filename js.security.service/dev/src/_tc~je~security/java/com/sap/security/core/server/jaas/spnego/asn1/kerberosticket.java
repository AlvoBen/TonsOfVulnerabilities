/*
 * Created on 2006-12-1
 */
package com.sap.security.core.server.jaas.spnego.asn1;

/**
 * @author Georgi Dimitrov ( I031654 )
 * 
 */
public class KerberosTicket {

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

  public final int NT_UNKNOWN = 0;
  public final int NT_PRINCIPAL = 1;
  public final int NT_SRV_INST = 2;
  public final int NT_SRV_HST = 3;
  public final int NT_SRV_XHST = 4;
  public final int NT_UID = 5;

  private int tkt_vno;
  private String realm;
  private int snameType;
  private String sname;

  public KerberosTicket(byte[] byteArray, int startIndex) {

    int offset = startIndex;
    boolean isCurrentTypeCorrect = true;

    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_UNIVERSAL, true, TLVParser.TAG_SEQUENCE);
    // ticket is a sequence
    // 0=tkt-vno
    // 1=realm
    // 2=sname

    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 0);// (ticket)SequenceElement[0]
    this.tkt_vno = TLVParser.getSubElementValueToInt(byteArray, offset);

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 1);// (ticket)SequenceElement[1]
    this.realm = TLVParser.getSubElementValueToString(byteArray, offset);

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 2);// (ticket)SequenceElement[2]

    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_UNIVERSAL, true, TLVParser.TAG_SEQUENCE);
    // sname is a sequence
    // 0 - sname type
    // 1 - sname value

    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 0);// (sname)SequenceElement[0]
    this.snameType = TLVParser.getTLV_ValueBytes(byteArray, offset)[0];
    // String snameTypeToString = getPrincipalNameTypeString();

    offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_CONTEXTSPECIFIC, true, 1);// (sname)SequenceElement[1]
    long lengthOfPrincipalName = TLVParser.getNumberOfValueBytes(byteArray, offset);

    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset, TLVParser.CLASS_UNIVERSAL, true, TLVParser.TAG_SEQUENCE);
    // sname value is a sequence, all elements form the full principal name
    // 0=HTTP
    // 1=myhost.sap.com

    long endOfPrincipalName = offset + lengthOfPrincipalName;
    offset = TLVParser.getOffsetForValueBytes(byteArray, offset);
    StringBuffer principalNameBuffer = new StringBuffer(" ");
    while (offset < endOfPrincipalName) {
      String principalNameElementString = TLVParser.getTLV_Value(byteArray, offset);
      principalNameBuffer.append(principalNameElementString + "/");
      offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    }
    principalNameBuffer.deleteCharAt(principalNameBuffer.length() - 1);
    this.sname = principalNameBuffer.toString();

    // offset = TLVParser.getOffsetForNextTLV(byteArray, offset);
    // isCurrentTypeCorrect = TLVParser.testFor(byteArray, offset,
    // TLVParser.CLASS_CONTEXTSPECIFIC, true,4);//(ticket)SequenceElement[3]
    // do not get enc-part

  }

  /**
   * 
   * name-type value meaning ----------------------------------------------
   * NT-UNKNOWN 0 Name type not known NT-PRINCIPAL 1 Just the name of the
   * principal as in DCE, or for users NT-SRV-INST 2 Service and other unique
   * instance (krbtgt) NT-SRV-HST 3 Service with host name as instance (telnet,
   * rcommands) NT-SRV-XHST 4 Service with host as remaining components NT-UID 5
   * Unique ID
   * 
   * @return PrincipalNameTypeConstant
   */
  public String getPrincipalNameTypeString() {
    switch (snameType) {
    case 0:
      return "NT-UNKNOWN";
    case 1:
      return "NT-PRINCIPAL";
    case 2:
      return "NT-SRV-INST";
    case 3:
      return "NT-SRV-HST";
    case 4:
      return "NT-SRV-XHST";
    case 5:
      return "NT-UID";

    default:
      return "NT-UNKNOWN";
    }

  }

  public int getTkt_vno() {
    return tkt_vno;
  }

  public String getRealm() {
    return realm;
  }

  public int getPrincipalNameType() {
    return snameType;
  }

  public String getPrincipalName() {
    return sname;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("KerberosTicket: [");
    buffer.append("\n  tkt_vno: " + tkt_vno);
    buffer.append("\n  realm: " + realm);
    buffer.append("\n  sname: [" + "type= " + getPrincipalNameTypeString() + ", value=" + sname + "]");
    buffer.append("\n  ]");
    return buffer.toString();
  }

}
