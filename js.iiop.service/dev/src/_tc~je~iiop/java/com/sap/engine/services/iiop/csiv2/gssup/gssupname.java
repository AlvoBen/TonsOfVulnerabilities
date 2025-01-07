package com.sap.engine.services.iiop.csiv2.GSSUP;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;

import java.util.*;

public class GSSUPName {

  public static final char AT_CHAR = '@';
  public static final String AT_STRING = "@";
  public static final char ESCAPE_CHAR = '\\';
  public static final String ESCAPE_STRING = "\\";
  private String username; // username
  private String realm; // realmname

  public GSSUPName(String username, String realm) {
    this.username = username;
    this.realm = realm;
  }

  /* Construct a GSSUPName from an exported name. This constructor
   * is for use on the server side.
   */
  public GSSUPName(byte[] GSSExportedName) {
    StringBuffer strbuf = new StringBuffer("");
    StringTokenizer strtok;
    int realm_index = 0; // start of realm 
    int user_index = -1; // start of user
    String expname = "";
    String name_value = "";
    String name_scope = "";
    byte[] exportedname = {};
    try {
      exportedname = GSSUtils.importName(GSSUtils.GSSUP_MECH_OID, GSSExportedName);
      expname = new String(exportedname, "UTF8");
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPName(byte[])", LoggerConfigurator.exceptionTrace(e));
      }
    }
    int at_index = expname.indexOf(AT_CHAR);
    int esc_index = expname.indexOf(ESCAPE_CHAR);

    if (at_index == -1) {
      name_value = expname;
    } else if (esc_index == -1) {
      if (at_index != 0) {
        name_value = expname.substring(0, at_index);
      }

      name_scope = expname.substring(at_index + 1);
    } else {
      user_index = 0;
      realm_index = 0;
      int i = 0;

      while ((i = expname.indexOf(AT_CHAR, i)) != -1) {
        if (expname.charAt(i - 1) != ESCAPE_CHAR) {
          realm_index = i;
          break;
        }

        i += 1;
      }

      name_value = expname.substring(user_index, realm_index);
      name_scope = expname.substring(realm_index + 1);
    }

    if ((name_value.length() > 0) && (at_index != -1)) {
      strbuf = new StringBuffer("");
      int starti = 0; // start index
      int endi = 0; // end index

      while ((endi = name_value.indexOf(ESCAPE_CHAR, starti)) != -1) {
        strbuf.append(name_value.substring(starti, endi));
        starti = endi + 1;
      }

      strbuf.append(name_value.substring(starti));
      name_value = strbuf.toString();
    }

    username = name_value;
    realm = name_scope;
  }

  public byte[] getExportedName() {
    byte[] expname = {};
    byte[] expname_utf8 = {};
    StringTokenizer strtok;
    StringBuffer strbuf = new StringBuffer("");
    int at_index = username.indexOf(AT_CHAR);
    int esc_index = username.indexOf(ESCAPE_CHAR);

    if ((at_index == -1) && (esc_index == -1)) {
      strbuf = new StringBuffer(username); // just copy - no processing required.
    } else {
      if (esc_index != -1) {
        strtok = new StringTokenizer(username, ESCAPE_STRING);

        while (strtok.hasMoreTokens()) {
          strbuf.append(strtok.nextToken());
          strbuf.append(ESCAPE_CHAR).append(ESCAPE_CHAR);
        }
      }

      if (at_index != -1) {
        strtok = new StringTokenizer(username, AT_STRING);

        while (strtok.hasMoreTokens()) {
          strbuf.append(strtok.nextToken());
          strbuf.append(ESCAPE_CHAR).append(AT_CHAR);
        }
      }
    }

    try {
      expname_utf8 = strbuf.toString().getBytes("UTF8");
      expname = GSSUtils.createExportedName(GSSUtils.GSSUP_MECH_OID, expname_utf8);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPName.getExportedName()", LoggerConfigurator.exceptionTrace(e));
      }
    }
    return expname;
  }

  public String getRealm() {
    return realm;
  }

  public String getUser() {
    return username;
  }

  public boolean equals(Object o) {
    if (o instanceof GSSUPName) {
      GSSUPName nm = (GSSUPName) o;
      if (nm.getUser().equals(username) && nm.getRealm().equals(realm)) {
        return true;
      }
    }

    return false;
  }

  /* Return the hashCode. */
  public int hashCode() {
    return username.hashCode() + realm.hashCode();
  }

  /* String representation of the GSSUPname */
  public String toString() {
    String s = "Username = " + username;
    s = s + " Realm = " + realm;
    return s;
  }

  // used locally by this file for test purposes
  private static void testGSSUP(String user, String realm) {
    GSSUPName gssname;
    GSSUPName gssname1;
    // Creating a GSSUPName instance
    gssname = new GSSUPName(user, realm);
    // Obtaining an exported name form
    byte[] expname = gssname.getExportedName();
    // Creating a GSSUPName instance from exported name");
    gssname1 = new GSSUPName(expname);
  }

//  public static void main(String[] args) {
//    testGSSUP("sekhar@vajjha@la@", "sun.com");
//    testGSSUP("sekhar", "sun.com");
//    testGSSUP("sekhar", "");
//    testGSSUP("", "sun.com");
//  }
}

