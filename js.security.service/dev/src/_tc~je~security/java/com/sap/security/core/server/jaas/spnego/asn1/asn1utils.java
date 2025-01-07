/*
 * Created on 05.01.2005
 *
 */
package com.sap.security.core.server.jaas.spnego.asn1;

import iaik.asn1.ASN1Object;
import iaik.asn1.ConstructedType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;

public class ASN1Utils {
  /**
   * decodes a length encoding of a DER sequence. The DER length sequence is
   * supposed to start at the first byte of the input stream
   * 
   * @param is
   *          InputStream to read from
   * @return length
   */
  public static long getDERLength(InputStream is) throws IOException {
    long count = 0;
    int ibyte = is.read();
    int countBytes = 0;
    byte b;

    // first handle the case of simple length encoding
    if (ibyte == -1)
      return 0;

    b = (byte) ibyte;
    if (b >= 0) { // highest bit is not set
      return (long) ibyte;
    }

    // see how many bytes we must read

    // unmark the highest bit
    b &= (byte) 0x7f;
    countBytes = (int) b;

    while (countBytes > 0) {
      ibyte = is.read();
      if (ibyte == -1)
        throw new IOException("No data in input stream available although promised.");

      count <<= 8;
      count |= ibyte;

      countBytes--;
    }

    return count;
  }

  public static void main(String[] args) throws IOException {
    byte[] bb = readBytesFromArgs(args);
    ByteArrayInputStream bais = new ByteArrayInputStream(bb);

    System.out.println(getDERLength(bais));
  }

  /**
   * @param args
   * @return
   */
  private static byte[] readBytesFromArgs(String[] args) {
    byte[] br = new byte[args.length];

    for (int i = 0; i < args.length; i++) {
      br[i] = getByteFromTwoDigitHex(args[i]);
    }

    return br;
  }

  /**
   * @param string
   * @return
   */
  private static byte getByteFromTwoDigitHex(String string) {
    byte b = 0;

    if (string.length() != 2)
      throw new RuntimeException(string + " is not a correct 2 digit hex string.");

    String s = string.substring(0, 1);
    b = Byte.parseByte(s, 0x10);
    b <<= 4;
    b |= Byte.parseByte(string.substring(1), 0x10);

    return b;
  }

  public static void dumpASN1Object(ASN1Object asn, int level, PrintStream out) throws Exception {
    int i = 0;

    // Einruecken
    if (asn.isConstructed() == false) {
      for (i = 0; i < level; i++)
        out.print("  ");
      out.println("" + asn);
      return;
    }

    Enumeration e = ((ConstructedType) asn).getComponents();
    for (i = 0; i < level; i++)
      out.print("  ");
    out.println("" + asn + " {");

    while (e.hasMoreElements()) {
      dumpASN1Object((ASN1Object) e.nextElement(), level + 1, out);
    }
    for (i = 0; i < level; i++)
      out.print("  ");
    out.println('}');

    out.flush();
  }
}
