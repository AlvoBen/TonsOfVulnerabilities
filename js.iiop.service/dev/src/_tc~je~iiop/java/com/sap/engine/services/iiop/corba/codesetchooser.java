/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CodeSetChooser {

  public static final int CODESET_UTF8 = 0x05010001;
  public static final int CODESET_UTF16 = 0x00010109;
  public static final int CODESET_ISO8859_1 = 0x00010001;
  public static final int CODESET_ISO646_IRV = 0x00010020;
  public static final int CODESET_ISO10646_UCS2 = 0x00010100;

  public static final String CODESET_ISO8859_1_STR = "ISO8859_1";
  public static final String CODESET_UTF8_STR = "UTF8";
  public static final String CODESET_UTF16_STR = "UTF16";

  // fallback code sets
  private int fallback_loc_c = CODESET_UTF8; // UTF-8
  private int fallback_loc_wc = CODESET_UTF16; // UTF-16

  /*clients code sets*/
  // local_char_native -> default:ISO 8859-1
  private static final int loc_c_n;
  // local_wchar_native -> default: the wchar fallback code set
  private static final int loc_wc_n = CODESET_UTF16;
  // local_char_conversions
  private static final int[] loc_c_cv;
  // local_wchar_conversions
  private static final int[] loc_wc_cv = {CODESET_UTF16};

  /* server code sets*/
  private int c_n = CODESET_ISO8859_1; // char_native -> default:ISO 8859-1
  private int wc_n = 0; // wchar_native -> no default
  private int[] c_cv; // char_conversions
  private int[] wc_cv; // wchar_conversions

  private int c_tsc = CODESET_ISO8859_1; // char transmission code set,  default:ISO 8859-1 for backward compatability
  private int wc_tsc = 0; // wchar transmission code set,  no default

  static {
    OutputStreamWriter outputstreamwriter = new OutputStreamWriter(new ByteArrayOutputStream());
    String defaultEncoding = outputstreamwriter.getEncoding();
    try {
      outputstreamwriter.close();
    } catch(IOException ioexception) {
      //$JL-EXC$ does not matter
    }

    if (defaultEncoding.equals(CODESET_ISO8859_1_STR)) {
      loc_c_n = CODESET_ISO8859_1;
      loc_c_cv = new int[] {CODESET_UTF8};
    } else if(defaultEncoding.equals(CODESET_UTF8_STR)) {
      loc_c_n = CODESET_UTF8;
      loc_c_cv = new int[] {CODESET_ISO8859_1};
    } else {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("CodeSetChooser static", "Warning - unknown codeset (" + defaultEncoding + ") - defaulting to ISO-8859-1");
      }
      loc_c_n = CODESET_ISO8859_1;
      loc_c_cv = new int[] {CODESET_UTF8};
    }

    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("Native CodeSet set to " + csName(loc_c_n));
    }
  }

  //used for service context codeset
  public CodeSetChooser() {
  }

  public CodeSetChooser(CORBAInputStream cis) {
    cis.beginEncapsulation();
    c_n = cis.read_long();
    int convCnt = cis.unaligned_read_long();

    if (convCnt > 0) {
      c_cv = new int[convCnt];

      for (int i = 0; i < convCnt; i++) {
        c_cv[i] = cis.unaligned_read_long();
      }
    }

    wc_n = cis.unaligned_read_long();
    convCnt = cis.unaligned_read_long();

    if (convCnt > 0) {
      wc_cv = new int[convCnt];

      for (int i = 0; i < convCnt; i++) {
        wc_cv[i] = cis.unaligned_read_long();
      }
    }
    cis.endEncapsulation();
  }

  /**
   * Sets the char code set.
   *
   * @param   chn  The local char native code set.
   */
  public void setCharCodeset(int chn) {
    c_tsc = chn;
  }

  /**
   * Sets the wchar code set.
   *
   * @param   wchn  The local wchar native code set.
   */
  public void setWCharCodeset(int wchn) {
    wc_tsc = wchn;
  }

  /**
   * Accesor method.
   *
   * @return     The char code set.
   */
  public int charCodeSet() {
    return c_tsc;
  }

    /**
   * Accesor method.
   *
   * @return     The native char code set.
   */
  public static int charNativeCodeSet() {
    return loc_c_n;
  }

  /**
   * Accesor method.
   *
   * @return     The wchar code set.
   */
  public int wcharCodeSet() {
    return wc_tsc;
  }

  /**
   * Accesor method.
   *
   * @return     The native wchar code set.
   */
  public static int wcharNativeCodeSet() {
    return loc_wc_n;
  }


  /**
   * Accesor method.
   *
   * @return     The char convertions.
   */
  public static int[] charConvertions() {
    return loc_c_cv;
  }

  /**
   * Accesor method.
   *
   * @return     The wchar convertions.
   */
  public static int[] wcharConvertions() {
    return loc_wc_cv;
  }

  public static String csName(int codeSet) {
    switch(codeSet) {
      case CODESET_ISO8859_1:
          return CODESET_ISO8859_1_STR;
      case CODESET_UTF16 :
          return CODESET_UTF16_STR;
      case CODESET_UTF8 :
          return CODESET_UTF8_STR;
    }
    return "Unknown CodeSet: " + Integer.toHexString(codeSet);
  }

  /**
   * Verifies if the code sets of the server and client side ORBs
   * are compatible.
   *
   * @return     TRUE if compatible. FALSE if not.
   */
  public int verifyCodesets() {

    try {
      if (charChoosed() && wcharChoosed()) { // negotiation succeeded !
        return 0; // ok
      } else { // negotiation fails...
        return 2;
      }
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CodeSetChooser.verifyCodesets()", LoggerConfigurator.exceptionTrace(e));
      }
      return 2;
    }
  }

  // private methods ====================================================
  // checks if the char code sets are compatible
  private boolean charChoosed() {
    int temp_loc_c;

    if (loc_c_n == c_n) { // native match
      c_tsc = loc_c_n;
      return true;
    } else if (elementOf(c_n, loc_c_cv)) { // client converts char
      c_tsc = c_n; // convert before send
      return true;
    } else if (elementOf(loc_c_n, c_cv)) { // server converts char
      c_tsc = loc_c_n;
      return true;
    } else if ((temp_loc_c = findMatch(c_cv, loc_c_cv)) > -1) { // find first(most preferable) conversion of server that matchs on a client conversion
      c_tsc = temp_loc_c;
      return true; // convert before send and server converts
    } else if (charFallbackCompatible()) { // fallback char code set
      c_tsc = fallback_loc_c;
      return true;
    } else {
      throw new CODESET_INCOMPATIBLE();
    }
  }

  // checks if the wchar code sets are compatible
  private boolean wcharChoosed() {
    int temp_loc_wc;

    if (loc_wc_n == wc_n) { // native match
      wc_tsc = loc_wc_n;
      return true;
    } else if (elementOf(wc_n, loc_wc_cv)) { // client converts char
      wc_tsc = wc_n; // convert before send
      return true;
    } else if (elementOf(loc_wc_n, wc_cv)) { // server converts char
      wc_tsc = loc_wc_n;
      return true;
    } else if ((temp_loc_wc = findMatch(wc_cv, loc_wc_cv)) > -1) { // find first(most preferable) conversion of server that matchs on a client conversion
      wc_tsc = temp_loc_wc;
      return true; // convert before send and server converts
    } else if (wcharFallbackCompatible()) { // fallback wchar code set
      wc_tsc = fallback_loc_wc;
      return true;
    } else {
      return false; // exception
    }
  }

  // checks if 'n' matches one of 'all'
  private boolean elementOf(int n, int[] all) {
    if (all != null) {
      for (int anAll : all) {
        if (n == anAll) {
          return true;
        }
      }
    }

    return false;
  }

  // finds first code set of 'one' matches with other from 'two'
  private int findMatch(int[] one, int[] two) {
    if ((one != null) && (two != null)) {
      for (int anOne : one) {
        for (int aTwo : two) {
          if (anOne == aTwo) { // match found
            return anOne;
          }
        }
      }
    }

    return -1; // no match
  }

  // check if the char fallback code set is compatible
  private boolean charFallbackCompatible() {
    // TODO - char fallback compatibility
    return true;
  }

  // check if the wchar fallback code set is compatible
  private boolean wcharFallbackCompatible() {
    // TODO - wchar fallback compatibility
    return true;
  }

}

