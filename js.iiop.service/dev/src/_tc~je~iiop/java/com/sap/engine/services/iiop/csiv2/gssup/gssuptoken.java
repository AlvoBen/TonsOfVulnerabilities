package com.sap.engine.services.iiop.csiv2.GSSUP;

import java.io.IOException;
import org.omg.CORBA.*;
import org.omg.IOP.Codec;
import com.sap.engine.interfaces.csiv2.*;
import java.util.*;
import javax.resource.spi.security.PasswordCredential;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

public class GSSUPToken {

  public static final String DELIMITER = "@";

    public static final String DEFAULT_REALM = "default";
  /**
   * cdr_encoded_token is the GSSAPI mechanism specific token
   * for the user, password mechanism. The mechanism specific
   * token is stored in the CDR encoded form.
   */
  private byte[] cdr_encoded_token = {};
  /* PasswordCredential that contains the username, password and realm */
  PasswordCredential pwdcred = null;

  /**
   * Constructor used to construct a mechansim token from a
   * PasswordCredential. This is used by a context initiator.
   *
   */
  public GSSUPToken(ORB orb, AS_ContextSec asContext, Codec codec, PasswordCredential pwdcred) {
    byte[] name_utf8 = {};// username in UTF8 format
    byte[] password_utf8 = {};// password in UTF8 format
    try {
      name_utf8 = (pwdcred.getUserName() + DELIMITER + DEFAULT_REALM).getBytes("UTF8");
      password_utf8 = new String(pwdcred.getPassword()).getBytes("UTF8");
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPToken(ORB, AS_ContextSec, Codec, PasswordCredential)", LoggerConfigurator.exceptionTrace(e));
      }
    }
    /* Get the target name from the IOR. The IOR is stored in the
     * ConnectionContext object
     */
    CompoundSecMech mech = null; //to get it from iora
    byte[] target_name = asContext.target_name;
    /* Create an InitialContextToken */
    InitialContextToken inctxToken = new InitialContextToken(name_utf8, password_utf8, target_name);
    /* Generate a CDR encoding */
    Any a = orb.create_any();
    InitialContextTokenHelper.insert(a, inctxToken);
    try {
      cdr_encoded_token = codec.encode_value(a);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPToken(ORB, AS_ContextSec, Codec, PasswordCredential)", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

  /* Constructor used to construct a mechansim token from a CDR encoded
   * mechanism specific token. This is used by a context acceptor.
   */
  public GSSUPToken(ORB orb, Codec codec, byte[] authtok) throws SecurityException {
    byte[] name_utf8 = {};// username  in UTF8 format
    byte[] password_utf8 = {};// password  in UTF8 format
    byte[] target_name = {};// target name 
    String username = "";
    String userpwd = "";
    String realm = "";
    byte[] encoded_token = {};
    /* get CDR encoded mechanism specific token */
    encoded_token = GSSUtils.getMechToken(GSSUtils.GSSUP_MECH_OID, authtok);
    /* create a GSSUPToken from the authentication token */
    /* Decode the cdr encoded token */
    Any a = orb.create_any();
    try {
      a = codec.decode_value(encoded_token, InitialContextTokenHelper.type());
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPToken(ORB, Codec, byte[])", LoggerConfigurator.exceptionTrace(e));
      }
    }
    InitialContextToken inctxToken = InitialContextTokenHelper.extract(a);
    /* get UTF8 encodings from initial context token */
    password_utf8 = inctxToken.password;
    name_utf8 = inctxToken.username;
    target_name = inctxToken.target_name;
    /* Construct a PasswordCredential */
    try {
      username = new String(name_utf8, "UTF8");
      userpwd = new String(password_utf8, "UTF8");
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("GSSUPToken(ORB, Codec, byte[])", LoggerConfigurator.exceptionTrace(e));
      }
    }
    /**
     * decode the username and realm as specified by CSIV2
     */
    String name;
    int index = username.indexOf(DELIMITER);

    if (index == -1) {
      name = username;
    } else if (index == 0) {
      // username is of the form "@realm"
      throw new SecurityException("No name_value in username");
    } else {
      // parse the name and realm tokens
      StringTokenizer strtok = new StringTokenizer(username, DELIMITER);
      name = strtok.nextToken();

      // this checking is neccessary if the username="name@"
      if (strtok.hasMoreTokens()) {
        realm = strtok.nextToken();

        if (!realm.equals("default")) {
          throw new SecurityException("Unknown realm");
        }
      }
    }

    pwdcred = new PasswordCredential(name, userpwd.toCharArray());
  }

  public byte[] getGSSToken() throws IOException {
    /* construct a GSSAPI token ( hdr + mechanism token ) */
    byte[] gsstoken = GSSUtils.createMechIndToken(GSSUtils.GSSUP_MECH_OID, cdr_encoded_token);
    return gsstoken;
  }

  public PasswordCredential getPwdcred() {
    return pwdcred;
  }

}

