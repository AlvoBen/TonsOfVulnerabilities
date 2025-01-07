package com.sap.security.core.server.jaas.spnego.asn1;

import com.sap.tc.logging.Location;

public class TLVParser {
  // currently we support simple identifiers only
  private static final int LEN_IDENTIFIER = 1;
  public static final int AND_CONSTRUCTED_PRIMITIVE = 0x20;
  public static final int AND_CLASS = 0xC0;
  public static final int AND_TAGNUMBER = 0x1F;
  public static final int AND_LONG_FORM_LENGTH = 0x80;
  public static final int CLASS_UNIVERSAL = 0;
  public static final int CLASS_APPLICATION = 1;
  public static final int CLASS_CONTEXTSPECIFIC = 2;
  public static final int CLASS_PRIVATE = 3;

  public static final int TAG_SEQUENCE = 0x10;

  private final static Location LOCATION = Location.getLocation(TLVParser.class);

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   * 
   * @return the offset (<code>offset</code> value) for the first content
   *         octet.
   */
  public static int getOffsetForValueBytes(byte[] b, int offset) {
    int res = -1;
    int lenlen = getLengthOctets(b, offset).length;
    if (isLongFormLength(b[offset + LEN_IDENTIFIER])) {
      lenlen++;
    }
    res = offset + LEN_IDENTIFIER + lenlen;
    return res;
  }

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   * 
   * @return the first octet in next TLV.
   */
  public static int getOffsetForNextTLV(byte[] b, int offset) {
    int numberOfLengthOctets = getLengthOctets(b, offset).length;
    if (isLongFormLength(b[offset + LEN_IDENTIFIER])) {
      numberOfLengthOctets++;
    }
    int res = offset + LEN_IDENTIFIER + numberOfLengthOctets + (int) getNumberOfValueBytes(b, offset);
    return res;
  }

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   * 
   * @return number of Value bytes
   */
  public static long getNumberOfValueBytes(byte[] b, int offset) {
    long res = -1;
    int[] octet = getLengthOctets(b, offset);
    if (9 < octet.length) {
      throw new IllegalArgumentException("No more than 63 bits are suported by current implementation (using type long).");
    }

    res = 0;
    for (int i = 0; i < octet.length; i++) {
      int chunk = octet[i];
      String msg = "Length octets must contain values [0x01;0xFF]. Found " + chunk;
      if (chunk < 0 || 0xFF < chunk) {
        throw new IllegalArgumentException(msg);
      }
      if (0 == chunk) {
        warningT(msg);
      }
      res = (res << 8) + chunk;
    }
    return res;
  }

  /**
   * @param b -
   *          The class tag
   * @return <code>ture</code> if Constructed, <code>false</code> if
   *         Primitive.
   */
  public static boolean isConstructed(byte b) {
    boolean res = (0 != (AND_CONSTRUCTED_PRIMITIVE & b));
    return res;
  }

  /**
   * @param b -
   *          The class tag
   * @return <br>
   *         0 = Universal<br>
   *         1 = Application<br>
   *         2 = Context-specific<br>
   *         3 = Private
   */
  public static byte getClass(byte b) {
    byte res = (byte) ((AND_CLASS & b) >> 6);
    return res;
  }

  public static boolean isUniversal(byte b) {
    boolean res = (CLASS_UNIVERSAL == getClass(b));
    return res;
  }

  public static boolean isApplication(byte b) {
    boolean res = (CLASS_APPLICATION == getClass(b));
    return res;
  }

  public static boolean isContextspecific(byte b) {
    boolean res = (CLASS_CONTEXTSPECIFIC == getClass(b));
    return res;
  }

  public static boolean isPrivate(byte b) {
    boolean res = (CLASS_PRIVATE == getClass(b));
    return res;
  }

  public static int getTagNumber(byte b) {
    int res = (b & AND_TAGNUMBER);
    return res;
  }

  /**
   * Check if byte Array has more than 1 element
   * 
   * @param byteArray
   * @return true, if byteArray has more than 1 element else false
   */
  public static boolean isArrOK(byte[] byteArray) {
    if (byteArray == null || byteArray.length < 2) {
      return false;
    }
    return true;
  }

  public static boolean isArrPtrOK(byte[] b, int offset) {
    if (isArrOK(b) && offset >= 0 && offset < b.length) {
      return true;
    }
    return false;
  }

  public static boolean isLongFormLength(byte b) {
    boolean res = (0 != (b & AND_LONG_FORM_LENGTH));
    return res;
  }

  protected static int getBits(byte b) {
    return (b << 24) >>> 24;
  }

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   * 
   * @return Length octets values (filter first one for the LONG_FORM flag)
   */
  protected static int[] getLengthOctets(byte[] b, int offset) {
    int[] res = null;
    byte octet = b[offset + LEN_IDENTIFIER];
    int bits = getBits(octet);

    boolean bLongForm = isLongFormLength(octet);
    int len = (bits & (~AND_LONG_FORM_LENGTH));

    res = new int[bLongForm ? len : 1];
    if (!bLongForm) {
      res[0] = (int) len;
    } else {
      for (int i = 0; i < len; i++) {
        res[i] = getBits(b[offset + i + 2]);
      }
    }
    return res;
  }

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   */
  public static boolean testFor(byte[] b, int offset, int iClass, boolean bConstructed) {
    boolean res = true;
    int foundClass = getClass(b[offset]);
    if (iClass != foundClass) {
      warningT("Expected to be " + classToString(iClass) + ", found " + classToString(foundClass));
      res = false;
    }
    if (TLVParser.isConstructed(b[offset]) != bConstructed) {
      warningT("Expected " + (bConstructed ? "" : "not") + " to be constructed.");
      res = false;
    }
    return res;
  }

  public static boolean testFor(byte[] b, int offset, int iClass, boolean bConstructed, int tag) {
    boolean res = testFor(b, offset, iClass, bConstructed);
    boolean bTag = testTag(b, offset, tag);
    res &= bTag;
    return res;
  }

  public static boolean testTag(byte[] b, int offset, int tag) {
    boolean res = true;
    int found = getTagNumber(b[offset]);
    if (tag != found) {
      warningT("Expected type " + tag + " found " + found);
      res = false;
    }
    return res;
  }

  private static String classToString(int iClass) {
    String res = null;
    switch (iClass) {
    case CLASS_UNIVERSAL:
      res = "Universal";
      break;
    case CLASS_APPLICATION:
      res = "Application";
      break;
    case CLASS_CONTEXTSPECIFIC:
      res = "Context-specific";
      break;
    case CLASS_PRIVATE:
      res = "Private";
      break;
    default:
      throw new IllegalArgumentException("Invalid value for class. Expected [0;3], found " + iClass);
    }
    return res;
  }

  /**
   * Assumes <code>offset</code> points start of ASN TLV.
   * 
   * @return byte[] for Value of TLV
   */
  public static byte[] getTLV_ValueBytes(byte[] b, int offset) {
    int len = (int) getNumberOfValueBytes(b, offset);
    int offsetValue = getOffsetForValueBytes(b, offset);
    byte[] res = new byte[len];
    for (int i = 0; i < len; i++) {
      res[i] = b[offsetValue + i];
    }
    return res;
  }

  /**
   * 
   * For use when TLV Value is unknown type Return String presentation for
   * TLV.Value.byte[]
   * 
   * @param b
   * @param offset
   * @return
   */

  public static String getSubElementValueBytesToString(byte[] b, int offset) {
    int valueStartIndex = getOffsetForValueBytes(b, offset);// skip ( TYPE and
                                                            // Length ) -> jump
                                                            // to Value
    return getTLV_ValueBytesToString(b, valueStartIndex);
  }

  /**
   * For use when TLV.Value.Type is General String
   * 
   * @param b
   * @param offset
   * @return TLV.Value.StringValue
   */
  public static String getSubElementValueToString(byte[] b, int offset) {
    int valueStartIndex = getOffsetForValueBytes(b, offset);// skip ( TYPE and
                                                            // Length ) -> jump
                                                            // to Value
    return getTLV_Value(b, valueStartIndex);
  }

  /**
   * For use when TLV.Value.Type is INTEGER
   * 
   * @param b
   * @param offset
   * @return TLV.Value.intValue
   */
  public static int getSubElementValueToInt(byte[] b, int offset) {
    int valueStartIndex = getOffsetForValueBytes(b, offset);// skip ( TYPE and
                                                            // Length ) -> jump
                                                            // to Value
    return getTLV_ValueBytes(b, valueStartIndex)[0];
  }

  /**
   * For use when TLV Type is General String
   * 
   * @param b
   * @param offset
   * @return TLV.Value.StringValue
   */
  public static String getTLV_Value(byte[] b, int offset) {
    byte[] valueBytes = getTLV_ValueBytes(b, offset);
    return new String(valueBytes);
  }

  /**
   * For use when TLV Type is unknown
   * 
   * @param b
   * @param offset
   * @return String presentation for TLV.Value
   */
  public static String getTLV_ValueBytesToString(byte[] b, int offset) {
    byte[] valueBytes = getTLV_ValueBytes(b, offset);
    return getStringForBytes(valueBytes);
  }

  /**
   * Returns a string representation of the byte array [] or [ byte0 ] or [
   * byte0,byte1 ,byte2, ... ]
   * 
   * @param byteArr
   * @return
   */

  public static String getStringForBytes(byte[] byteArr) {

    if (byteArr.length == 0) {
      return "[ ]";
    }
    if (byteArr.length == 1) {
      return "[ " + byteArr[0] + " ]";
    }

    StringBuffer buffer = new StringBuffer();
    buffer.append("[" + byteArr[0]);
    for (int i = 1; i < byteArr.length; i++) {
      buffer.append(", " + byteArr[i]);
    }
    buffer.append("]");

    return buffer.toString();
  }

  protected static void infoT(String msg) {
    if (LOCATION.beInfo()) {
      LOCATION.infoT(msg);
    }
  }

  protected static void errorT(String msg) {
    if (LOCATION.beError()) {
      LOCATION.errorT(msg);
    }
  }

  protected static void debugT(String msg) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT(msg);
    }
  }

  protected static void warningT(String msg) {
    if (LOCATION.beWarning()) {
      LOCATION.warningT(msg);
    }
  }

}
