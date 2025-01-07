package com.sap.engine.services.iiop.csiv2.GSSUP;

import java.io.IOException;
import iaik.asn1.DerCoder;
import iaik.asn1.ObjectID;
import iaik.asn1.CodingException;
import com.sap.engine.services.iiop.csiv2.CSI.GSS_NT_Export_Name_OID;
import com.sap.engine.services.iiop.csiv2.CSI.GSS_NT_Scoped_Username_OID;

public class GSSUtils {

  public static String GSSUP_MECH_OID = null;
  public static String GSS_NT_EXPORT_NAME_OID = null;
  /* GSS_NT_SCOPED_USERNAME_OID is currently not used by this class. It is
   * defined here for the sake of completeness.
   */
  public static String GSS_NT_SCOPED_USERNAME_OID = null;

  static {
    int i; // index
    /* Construct an ObjectIdentifer by extracting each OID */
    try {
      i = GSSUPMechOID.value.indexOf(':');
      GSSUP_MECH_OID = GSSUPMechOID.value.substring(i + 1);
      i = GSS_NT_Export_Name_OID.value.indexOf(':');
      GSS_NT_EXPORT_NAME_OID = GSS_NT_Export_Name_OID.value.substring(i + 1);
      i = GSS_NT_Scoped_Username_OID.value.indexOf(':');
      GSS_NT_SCOPED_USERNAME_OID = GSS_NT_Scoped_Username_OID.value.substring(i + 1);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static String dumpHex(byte[] octets) {
    StringBuffer result = new StringBuffer("");

    for (int i = 0; i < octets.length; i++) {
      if ((i != 0) && ((i % 16) == 0)) {
        result.append("\n    ");
      }
      int b = octets[i];
      if (b < 0) {
        b = 256 + b;
      }

      String hex = Integer.toHexString(b);
      if (hex.length() == 1) {
        result.append("0");
        result.append(hex);
      } else {
        result.append(hex);
      }

      result.append(" ");
    } 

    return result.toString();
  }

  public static byte[] importName(String oid, byte[] externalName) throws IOException {
    IOException e = new IOException("Invalid Name");

    if (externalName[0] != 0x04) {
      throw e;
    }

    if (externalName[1] != 0x01) {
      throw e;
    }

    int mechoidlen = (((int) externalName[2]) << 8) + externalName[3];

    if (externalName.length < (4 + mechoidlen + 4)) {
      throw e;
    }

    byte[] deroid = new byte[mechoidlen];
    System.arraycopy(externalName, 4, deroid, 0, mechoidlen);
    String oid1 = getOID(deroid);

    if (!oid1.equals(oid)) {
      throw e;
    }

    int pos = 4 + mechoidlen;
    int namelen = (((int) externalName[pos]) << 24) + (((int) externalName[pos + 1]) << 16) + (((int) externalName[pos + 2]) << 8) + (((int) externalName[pos + 3]));
    pos += 4; // start of the mechanism specific exported name

    if (externalName.length != (4 + mechoidlen + 4 + namelen)) {
      throw e;
    }

    byte[] name = new byte[externalName.length - pos];
    System.arraycopy(externalName, pos, name, 0, externalName.length - pos);
    return name;
  }

  public static boolean verifyMechOID(String oid, byte[] externalName) throws IOException {
    IOException e = new IOException("Invalid Name");

    if (externalName[0] != 0x04) {
      throw e;
    }

    if (externalName[1] != 0x01) {
      throw e;
    }

    int mechoidlen = (((int) externalName[2]) << 8) + externalName[3];

    if (externalName.length < (4 + mechoidlen + 4)) {
      throw e;
    }

    byte[] deroid = new byte[mechoidlen];
    System.arraycopy(externalName, 4, deroid, 0, mechoidlen);
    String oid1 = getOID(deroid);

    return oid1.equals(oid);
  }

  /*
   *  Generate an exported name as specified in [RFC 2743]
   *  section 3.2, "Mechanism-Independent Exported Name Object Format".
   *  For convenience, the format of the exported name is reproduced here
   *  from [RFC2743] :
   *
   *  Format:
   *  Bytes
   *  2          0x04 0x01
   *  2          mech OID length (len)
   *  len        mech OID's DER value
   *  4          exported name len
   *  name len   exported name
   *
   */
  public static byte[] createExportedName(String oid, byte[] extName) throws IOException {
    byte[] oidDER = getDER(oid);
    int tokensize = 2 + 2 + oidDER.length + 4 + extName.length;
    byte[] token = new byte[tokensize];
    int pos = 0;
    token[0] = 0x04;
    token[1] = 0x01;
    token[2] = (byte) (oidDER.length & 0xFF00);
    token[3] = (byte) (oidDER.length & 0x00FF);
    pos = 4;
    System.arraycopy(oidDER, 0, token, pos, oidDER.length);
    pos += oidDER.length;
    int namelen = extName.length;
    token[pos++] = (byte) (namelen & 0xFF000000);
    token[pos++] = (byte) (namelen & 0x00FF0000);
    token[pos++] = (byte) (namelen & 0x0000FF00);
    token[pos++] = (byte) (namelen & 0x000000FF);
    System.arraycopy(extName, 0, token, pos, namelen);
    return token;
  }

  /*
   * Return the DER representation of an ObjectIdentifier.
   * The DER representation is as follows:
   *
   *    0x06            --  Tag for OBJECT IDENTIFIER
   *    derOID.length   --  length in octets of OID
   *    DER value of OID -- written as specified byte the DER representation
   *                        for an ObjectIdentifier.
   */
  public static byte[] getDER(String id) throws IOException {
      ObjectID oid = new ObjectID(id);
      return DerCoder.encode(oid); 
  }

  /*
   * Return the OID corresponding to an OID represented in DER format
   * as follows:
   *
   *    0x06            --  Tag for OBJECT IDENTIFIER
   *    derOID.length   --  length in octets of OID
   *    DER value of OID -- written as specified byte the DER representation
   *                        for an ObjectIdentifier.
   */
  public static String getOID(byte[] derOID) throws IOException {
    try {
      ObjectID oid = (ObjectID)DerCoder.decode(derOID); 	
      return oid.getID();
    } catch (CodingException ce) {
      throw new IOException(ce.getMessage());	
    }     
  }

  /*
   * Construct a mechanism level independent token as specified in section
   * 3.1, [RFC 2743]. This consists of a token tag followed byte a mechanism
   * specific token. The format - here for convenience - is as follows:
   *
   *  Token Tag                      Description
   *
   *     0x60                       | Tag for [APPLICATION 0] SEQUENCE
   *     <token-length-octets>      |
   *     0x06                       | Along with the next two entries
   *     <object-identifier-length> | is a DER encoding of an object
   *     <object-identifier-octets> | identifier
   *
   *  Mechanism specific token      | format defined by the mechanism itself
   *                                  outside of RFC 2743.
   */
  public static byte[] createMechIndToken(String mechoid, byte mechtok[]) throws IOException {
    byte[] deroid = getDER(mechoid);
    byte[] token = new byte[1 + getDERLengthSize(deroid.length + mechtok.length) + deroid.length + mechtok.length];
    int index = 0;
    token[index++] = 0x60;
    index = writeDERLength(token, index, deroid.length + mechtok.length);
    System.arraycopy(deroid, 0, token, index, deroid.length);
    index += deroid.length;
    System.arraycopy(mechtok, 0, token, index, mechtok.length);
    return token;
  }

  /*
   * Retrieve a mechanism specific token from a mechanism independent token.
   * The format of a mechanism independent token is specified in section
   * 3.1, [RFC 2743].
   */
  public static byte[] getMechToken(String oid, byte[] token) {
    byte[] mechtoken = null;
    try {
      int index = verifyTokenHeader(oid, token);
      int mechtoklen = token.length - index;
      mechtoken = new byte[mechtoklen];
      System.arraycopy(token, index, mechtoken, 0, mechtoklen);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mechtoken;
  }

  /* Verfies the header of a mechanism independent token. The header must
   * be as specified in RFC 2743, section 3.1. The header must contain
   * an object identifier specified by the first parameter.
   *
   * If the header is well formed, then the starting position of the
   * mechanism specific token within the token is returned.
   *
   * If the header is mal formed, then an exception is thrown.
   */
  private static int verifyTokenHeader(String oid, byte[] token) throws IOException {
    int index = 0;

    // verify header
    if (token[index++] != 0x60) {
      throw new IOException("Defective Token");
    }

    int toklen = readDERLength(token, index); // derOID length + token length
    index += getDERLengthSize(toklen);

    if (token[index] != 0x06) {
      throw new IOException("Defective Token");
    }

    byte[] buf = new byte[token.length - index];
    System.arraycopy(token, index, buf, 0, token.length - index);
    String mechoid = getOID(buf);

    if (!mechoid.equals(oid)) {
      throw new IOException("Defective Token");
    }

    int mechoidlen = getDER(oid).length;
    return (index + mechoidlen); // starting position of mech specific token
  }

  static int getDERLengthSize(int length) {
    if (length < (1 << 7)) {
      return (1);
    } else if (length < (1 << 8)) {
      return (2);
    } else if (length < (1 << 16)) {
      return (3);
    } else if (length < (1 << 24)) {
      return (4);
    } else {
      return (5);
    }
  }

  static int writeDERLength(byte[] token, int index, int length) {
    if (length < (1 << 7)) {
      token[index++] = (byte) length;
    } else {
      token[index++] = (byte) (getDERLengthSize(length) + 127);
      if (length >= (1 << 24)) {
        token[index++] = (byte) (length >> 24);
      }
      if (length >= (1 << 16)) {
        token[index++] = (byte) ((length >> 16) & 0xff);
      }
      if (length >= (1 << 8)) {
        token[index++] = (byte) ((length >> 8) & 0xff);
      }
      token[index++] = (byte) (length & 0xff);
    }

    return (index);
  }

  static int readDERLength(byte[] token, int index) {
    byte sf;
    int ret = 0;
    int nooctets;
    sf = token[index++];

    if ((sf & 0x80) == 0x80) { // value > 128
      // bit 8 is 1 ; bits 0-7 of first bye is the number of octets
      nooctets = (sf & 0x7f); // remove the 8th bit

      for (; nooctets != 0; nooctets--) {
        ret = (ret << 8) + (token[index++] & 0x00FF);
      } 
    } else {
      ret = sf;
    }

    return (ret);
  }

//  public static void main(String[] args) {
//    try {
//      //        byte[] len = new byte[3];
//      //        len[0] = (byte) 0x82;
//      //        len[1] = (byte) 0x01;
//      //        len[2] = (byte) 0xd3;
//      //	      String name = "default";
//      //	      byte[] externalName = createExportedName(GSSUtils.GSSUP_MECH_OID, name.getBytes());
//      //	      byte[] m = importName(GSSUtils.GSSUP_MECH_OID, externalName);
//      //	      String msg = "dummy_gss_export_sec_context" ;
//      //	      byte[] foo = createMechIndToken(GSSUtils.GSSUP_MECH_OID, msg.getBytes());
//      //	      byte[] msg1 = getMechToken(GSSUtils.GSSUP_MECH_OID, foo);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
}

