package com.sap.security.core.server.jaas.spnego.util;

import iaik.asn1.ASN1Object;
import iaik.asn1.BIT_STRING;
import iaik.asn1.ConstructedType;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.IUserAccountSearchFilter;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserSearchFilter;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.core.server.jaas.DetailedLoginException;
import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.SPNegoProtocolException;
import com.sap.tc.logging.Location;

/**
 * @author d028305
 * 
 */
public class Utils {
  private final static Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION);

  /**
   * Searches a user by a specified attribute. If result not unique,
   * an SPNegoProtocolException is thrown
   * @param namespace UME namespace in which to search for the attribute
   * @param key attribute name to search for
   * @param value value of the attribute
   * @return an IUser object, if successful
   * @throws UMException if UME layer throws a UME exception
   * @throws SPNegoProtocolException if no user or more than one user matches
   *          the search criteria
   */
  public static IUser getUserForAttribute(String namespace, String key, String value) throws UMException, SPNegoProtocolException {
    IUserFactory userFactory = UMFactory.getUserFactory();
    IUserSearchFilter searchFilter = null;
    ISearchResult searchResult = null;
    IUser iuser = null;

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Searching for user by attribute " + key + " = " + value);
    }

    searchFilter = userFactory.getUserSearchFilter();
    searchFilter.setSearchAttribute(namespace, key, value, ISearchAttribute.EQUALS_OPERATOR, false);
    searchResult = userFactory.searchUsers(searchFilter);
    if (searchResult.size() == 0) {
      if (LOCATION.beError()) {
        LOCATION.errorT("Couldn't find user by attribute " + key + " = " + value);
      }
      throw new SPNegoProtocolException("User Resolution not possible.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
    } else if (searchResult.size() > 1) {
      if (LOCATION.beError()) {
        LOCATION.errorT("Found too many matches by attribute " + key + " = " + value);
      }
      throw new SPNegoProtocolException("User Resolution not possible.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
    }
    // else we have exactly one element in the resuls list.
    String uniqueid = (String) searchResult.next();

    iuser = userFactory.getUser(uniqueid);

    return iuser;
  }

  /**
   * @deprecated
   * @return getUserForAttribute (namespace, key, value);
   */
  public static IUser getUserForAttribute(String namespace, String key, String value, boolean b) throws UMException, SPNegoProtocolException {
    return getUserForAttribute(namespace, key, value);
  }

  /**
   *  This method is needed in case the Kerberos principal name of a user
   *  is not itself an attribute of the user account object.
   *  This can happen in ADS in case a UPN suffix in ADS is defined. This 
   *  UPN suffix (UPN stands for User Principal Name) allows to define other
   *  suffixes in the UPN than the domain name. Suppose the domain is 
   *  WDF.SAP.CORP Windows allows you to define a UPN suffix SAP.CORP, for instance.
   *  Whenever you define a new user now, you can choose whether you want to
   *  have WDF.SAP.CORP or SAP.CORP behind the @. The only requirement is that
   *  the result is unique throughout the entire forest.
   *  <p>
   *  On case the customer has a UPN suffix defined and some users have this 
   *  UPN suffix in their UPN the Kerberos Principal Name does not match the
   *  UPN anymore. In this case, we need a refined search to resolve the
   *  user. The algorithm is as follows:
   *  <ul>
   *    <li> Perform a search with the Kerberos Principan Name prefix (KPN prefix)
   *         in the UME. If result unique, stop.
   *    <li> If result not unique, try to use the KPN suffix, which is usually
   *         the ADS domain to find out which match is the correct one.
   *  <ul>
   *  @param kpnPrefix Part of the KPN in front of the @
   *  @param kpnSuffix Part of the KPN behind the @
   *  @param prefixAttr UME attribute to search for with the kpnPrefix
   *  @param internal_logging boolean telling us whether to use internal logging or not
   *  @return
   */
  public static IUser getUserByKerberosPrincipalName(String kpnPrefix, String kpnSuffix, String prefixAttr, String dnAttr) throws UMException, SPNegoProtocolException {
    String uniqueid = null;
    String ldapbase = null;
    String userdn = null;
    IUserFactory userFactory = UMFactory.getUserFactory();
    IUserSearchFilter searchFilter = null;
    ISearchResult searchResult = null;
    IUser iuser = null, resultUser = null; // the actual return value
    int nResults = -1;

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Searching for user with (kpnPrefix,kpnSuffix) = (" + kpnPrefix + "," + kpnSuffix + ")");
    }

    searchFilter = userFactory.getUserSearchFilter();
    searchFilter.setSearchAttribute(IPrincipal.DEFAULT_NAMESPACE, prefixAttr, kpnPrefix, ISearchAttribute.EQUALS_OPERATOR, false);
    searchResult = userFactory.searchUsers(searchFilter);
    nResults = searchResult.size();

    if (nResults == 0) {
      if (LOCATION.beError()) {
        LOCATION.errorT("Couldn't find user with (kpnPrefix,kpnSuffix) = (" + kpnPrefix + "," + kpnSuffix + ")");
      }
      throw new SPNegoProtocolException("User Resolution not possible.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED);
    } else if (nResults == 1) {
      // Search was unique => perfect, we return
      uniqueid = (String) searchResult.next();

      if (LOCATION.beInfo()) {
        LOCATION.infoT("Unique search result " + uniqueid);
      }
      resultUser = userFactory.getUser(uniqueid);
    } else {
      // most difficult case. we need to find out which user is the
      // correct one. Therefore we transform the kpnSuffix into an LDAP
      // base path. Then we check this base path with the distinguished
      // name of the user objects.
      ldapbase = Utils.getLDAPBaseFromDNSDomain(kpnSuffix).toLowerCase();

      if (LOCATION.beInfo()) {
        LOCATION.infoT("Non-unique result. Iterating through results. LDAP base is " + ldapbase);
      }
      // Now iterate through the result set and check if the
      // distinguishedName attribute of the user fits best with
      // ldapbase value.
      while (searchResult.hasNext()) {
        uniqueid = (String) searchResult.next();
        iuser = userFactory.getUser(uniqueid);

        String[] values = iuser.getAttribute(IPrincipal.DEFAULT_NAMESPACE, dnAttr);
        if (values == null || values.length == 0) {
          if (LOCATION.beError()) {
            LOCATION.errorT("Fatal Error! " + dnAttr + " not maintained. Please check datasource configuration.");
          }

          throw new UMException("Could not get distinguished name of user with uniqueid " + uniqueid);
        } // if (values==null || values.length==0)
        userdn = values[0].toLowerCase();

        // ldapbase must be suffix of userdn and first occurence of "dc=" in
        // userdn must be equal to first occurence of ldapbase in userdn.
        // Otherwise we deal with the case of a user in a subdomain.
        // Example:
        // Correct match:
        //             01234567890123456789012345678901234567890          
        //     userdn: cn=d028305,cn=users,dc=wdf,dc=sap,dc=corp
        //   ldapbase: dc=wdf,dc=sap,dc=corp
        // 
        //   index of "dc=" in userdn is 20
        //   index of ldapbase in userdn is also 20.
        //
        // Wrong match:
        //             01234567890123456789012345678901234567890          
        //     userdn: cn=d028305,cn=users,dc=ume,dc=wdf,dc=sap,dc=corp
        //   ldapbase: dc=wdf,dc=sap,dc=corp
        // 
        //   index of "DC=" in userdn is 20
        //   index of ldapbase in userdn is also 27.
        //   In this case user is a root domain (WDF.SAP.CORP)
        //   and we need to do it this way in order not to mix it up with
        //   the user in the sub domain.
        int idx_dcequals = -1;
        int idx_ldapbase = -1;

        idx_ldapbase = userdn.indexOf(ldapbase);
        if (idx_ldapbase == -1) {
          // user is not even in the domain in question,
          // so this one cannot be our guy.
          if (LOCATION.beInfo()) {
            LOCATION.infoT("User in wrong domain found. Search continues. userdn = " + userdn + ", ldapbase = " + ldapbase);
          }
          continue;
        }
        idx_dcequals = userdn.indexOf("dc=");

        if (idx_ldapbase != idx_dcequals) {
          // here we have two users of identical samaccountname,
          // one in a root domain, one in a child domain.
          if (LOCATION.beInfo()) {
            LOCATION.infoT("User in wrong domain (child domain) found. Search continues. userdn = " + userdn + ", ldapbase = " + ldapbase);
          }
          continue;
        } else {
          if (LOCATION.beInfo()) {
            LOCATION.infoT("User found! userdn = " + userdn + ", ldapbase = " + ldapbase);
          }
          resultUser = iuser;
          break;
        } // if (idx_ldapbase!=idx_dcequals) {
      } // while (isr.hasNext ()) {
    } // if (nResults==0) {

    return resultUser;
  }

  public static IUser getUserByPrincipalAndRealm(String principal, String realm) throws UMException, SPNegoProtocolException {

    final String KPN_PREFIX_ATTRIBUTE = "principal";
    final String KPN_SUFFIX_ATTRIBUTE = "realm";
    final String NAMESPACE = "com.sap.security.core.authentication";

    if (LOCATION.beInfo()) {
      LOCATION.infoT("Searching for user account with (" + KPN_PREFIX_ATTRIBUTE + ", " + KPN_SUFFIX_ATTRIBUTE + ") = (" + principal + "," + realm + ")");
    }

    IUserAccountFactory userAccountFactory = UMFactory.getUserAccountFactory();
    IUserAccountSearchFilter searchFilter = userAccountFactory.getUserAccountSearchFilter();
    searchFilter.setSearchAttribute(NAMESPACE, KPN_PREFIX_ATTRIBUTE, principal, ISearchAttribute.EQUALS_OPERATOR, false);
    searchFilter.setSearchAttribute(NAMESPACE, KPN_SUFFIX_ATTRIBUTE, realm, ISearchAttribute.EQUALS_OPERATOR, false);
    ISearchResult searchResult = userAccountFactory.search(searchFilter);
    int nResults = searchResult.size();

    if (nResults == 1) {
      // Search was unique => perfect, we return
      String uniqueid = (String) searchResult.next();
      if (LOCATION.beInfo()) {
        LOCATION.infoT("Unique search result: " + uniqueid);
      }
      IUserAccount userAccount = userAccountFactory.getUserAccount(uniqueid);
      IUser resultUser = userAccount.getAssignedUser();
      return resultUser;
    } else {
      String errorMessage = "No user found with (" + KPN_PREFIX_ATTRIBUTE + ", " + KPN_SUFFIX_ATTRIBUTE + ") = (" + principal + "," + realm + ")";
      if (nResults > 1) {
        errorMessage = "Multiple users found with (" + KPN_PREFIX_ATTRIBUTE + ", " + KPN_SUFFIX_ATTRIBUTE + ") = (" + principal + "," + realm + ")";
      }
      if (LOCATION.beError()) {
        LOCATION.errorT(errorMessage);
      }
      throw new SPNegoProtocolException("User Resolution not possible.", DetailedLoginException.SPNEGO_AUTHENTICATION_FAILED);
    }
  }

  /**
   * @deprecated
   * @return
   * @throws UMException
   * @throws SPNegoProtocolException
   */
  public static IUser getUserByKerberosPrincipalName(String kpnPrefix, String kpnSuffix, String prefixAttr, String dnAttr, boolean internal_logging) throws UMException, SPNegoProtocolException {
    return getUserByKerberosPrincipalName(kpnPrefix, kpnSuffix, prefixAttr, dnAttr);

  }

  public static void blockByteDump(byte[] b, PrintWriter out) {
    blockByteDump(b, 0, b.length, out);
  }

  /**
   * Method blockByteDump.
   * 
   * @param b
   * @param i
   * @param i1
   * @param out
   */
  public static void blockByteDump(byte[] b, int offset, int endIndexToPrint, PrintWriter out) {
    int j = 0;
    String hexAddress;

    // print in blocks of 16 bytes
    for (j = offset; j < endIndexToPrint; j += 16) {

      // print from startIndex = j to endIndex = j+16
      // or endIndex = endIndexToPrint
      int tmpEndIndex = j + 16;
      if (tmpEndIndex > endIndexToPrint) {
        tmpEndIndex = endIndexToPrint;
      }

      hexAddress = Integer.toHexString(j);
      // Fill with leading zeros

      while (hexAddress.length() < 10) {
        hexAddress = "0" + hexAddress;
      }

      // print starting address in hex
      out.print(hexAddress);

      out.print(' ');

      // print the bytes in hex
      dumpByteArray(b, j, tmpEndIndex, out);

      addIndent(out, j + 16 - endIndexToPrint);
      out.print("  ");

      // print the bytes as ASCII characters if printable
      // otherwise print "."
      for (int ii = j; ii < tmpEndIndex; ii++) {
        char c = (char) b[ii];

        if (isCharPrintable(c)) {
          out.print(c);
        } else {
          out.print('.');
        }
      }
      out.print('\n');
    }
    out.flush();
  }

  public static void dumpByteArray(byte[] b, PrintWriter out) {
    dumpByteArray(b, 0, b.length, out);
  }

  /**
   * 
   * For byteArray { 0xAB, 0xCD, 0xEF , ... } prints AB:CD:EF: ...
   * 
   * @param b
   * @param offset
   * @param length
   * @param out
   */
  public static void dumpByteArray(byte[] byteArray, int offset, int length, PrintWriter out) {

    try {
      for (int i = offset; i < length; i++) {
        int byteValue = getRawIntFromByte(byteArray[i]);

        // if byteValue == 0xXY print "XY"
        out.print((byteValue < 16 ? "0" : "") + Integer.toHexString(byteValue));

        // print separator between the bytes
        out.print(":");

      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    out.flush();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //  H  E  L  P  E  R   M  E  T  H  O  D  S
  //
  //////////////////////////////////////////////////////////////////////////////////////////////// 

  /**
   *  Transforms a DNS domain name into an LDAP base path, e.g.
   *  WDF.SAP.CORP into dc=wdf,dc=sap,dc=corp
   * @param  kpnSuffix
   * @return
   */
  private static String getLDAPBaseFromDNSDomain(String kpnSuffix) {
    StringBuffer sb = new StringBuffer(50);
    StringTokenizer tokenizer = new StringTokenizer(kpnSuffix, ".");

    while (tokenizer.hasMoreTokens()) {
      String t = tokenizer.nextToken();
      sb.append("dc=");
      sb.append(t);
      if (tokenizer.hasMoreTokens()) {
        sb.append(',');
      }
    }

    return sb.toString();
  }

  /**
   * Is char printable ( code 16<=c<=127 )
   * 
   * @param c
   * @return
   */
  private static boolean isCharPrintable(char c) {
    byte b = (byte) c;

    if ((0x80 & b) == 0x80) {
      return false;
    }

    if (b > 15) {
      return true;
    }

    return false;
  }

  public static void dumpASN1Object(ASN1Object asn, int level, PrintWriter out) {

    if (asn.isConstructed() == false) {
      addIndent(out, level);
      out.println("" + asn);
      return;
    }

    Enumeration e = ((ConstructedType) asn).getComponents();

    addIndent(out, level);
    out.println("" + asn + " {");

    while (e.hasMoreElements()) {
      dumpASN1Object((ASN1Object) e.nextElement(), level + 1, out);
    }

    addIndent(out, level);
    out.println('}');

    out.flush();
  }

  private static void addIndent(PrintWriter out, int indentLength) {
    for (int i = 0; i < indentLength; i++) {
      out.print("  ");
    }
  }

  /**
   * Converts the raw binary data from one data type to another
   * @param i integer (must be 0<=i<=255)
   * @return the byte having the same binary representation (255 becomes -1, 128 becomes -128)
   */
  public static byte getRawByteFromInt(int i) {
    if (i < 0 || i > 255) {
      throw new IllegalArgumentException("Only values 0<=i<=255 accepted.");
    }

    return (byte) i;
  }

  /**
   * Converts the raw binary data from one data type to another
   * @param some byte
   * @return the integer having the same binary representation
   *         (-128 becomes 255, -1 becomes 255).
   */
  public static int getRawIntFromByte(byte b) {
    if (b > 0) {
      return (int) b;
    } else {
      b &= 0x7f;
      int i = b;
      return i |= 128;
    }
  }

  public static String dumpIntoString(ASN1Object obj, int indent) {
    CharArrayWriter caw = new CharArrayWriter();
    PrintWriter printWriter = new PrintWriter(caw);

    dumpASN1Object(obj, indent, printWriter);
    printWriter.flush();
    return new String(caw.toCharArray());
  }

  public static String dumpIntoString(byte[] bytes, int offset, int length) {
    CharArrayWriter caw = new CharArrayWriter();
    PrintWriter printWriter = new PrintWriter(caw);

    blockByteDump(bytes, offset, length, printWriter);
    printWriter.flush();
    return new String(caw.toCharArray());
  }

  /**
   * For array of 7 booleans generates an array of 1 byte with the bit 1 for
   * true and bit 0 for false in reverse order bitsNotValid is set to 1, the
   * last bit of the byte will not be used
   * 
   * @param booleanArray
   * @param bitsNotValid
   *          is modified !!!
   * @return
   */
  public static byte[] createByteArrayFromBool(boolean[] booleanArray, int[] bitsNotValid) {
    byte b = 0;

    bitsNotValid[0] = 1;

    if (booleanArray.length != 7) {
      throw new IllegalArgumentException("This function is only operational with boolean arrays of length 7.");
    }

    for (int i = 1; i <= booleanArray.length; i++) {
      if (booleanArray[i - 1]) {
        b |= Utils.getRawByteFromInt(1 << i);
      }
    }

    return new byte[] { b };
  }

  // ////////////////////////////////////////////////////////////
  //    
  // H E L P E R M E T H O D S F O R T E S T F U N C T I O N S
  //    
  // ////////////////////////////////////////////////////////////

  /**
   * For "100111010" generates { false, true, false, true, true, true, false,
   * false , true } i.e. true for 1 and false for 0 but in reverse order
   * 
   */
  private static boolean[] createBooleanArrayFromString(String string) {
    boolean[] booleanArray = new boolean[string.length()];
    int jj = 0;

    for (int i = string.length() - 1; i >= 0; i--, jj++) {
      if ('1' == string.charAt(i)) {
        booleanArray[jj] = true;
      } else if ('0' == string.charAt(i)) {
        booleanArray[jj] = false;
      } else {
        throw new IllegalArgumentException("Invalid bit string format");
      }
    }

    return booleanArray;
  }

  /**
   * KPN := principal@DOMAIN
   * 
   * @param kpn
   * @return the KPN prefix (principal), <code>null</code> if not a valid KPN.
   */
  public static String getKPNPrefix(String kpn) {
    int idx = kpn.indexOf('@');
    if (idx == -1 || idx == 0) {
      return null;
    }
    String kpnPrefix = kpn.substring(0, idx);
    return kpnPrefix;
  }

  /**
   * KPN := principal@DOMAIN
   * 
   * @param kpn
   * @return the KPN suffix (DOMAIN), <code>null</code> if not a valid KPN.
   */
  public static String getKPNSuffix(String kpn) {
    int idx = kpn.indexOf('@');
    if (idx == -1 || idx == 0) {
      return null;
    }
    String kpnSuffix = kpn.substring(idx + 1);
    return kpnSuffix;
  }

  /**
   * @param csv
   *          "one, two,three, four five, six"
   * @return array of trimmed values ["one","two","three","four five six"]
   */
  public static String[] stringToArray(String concatenatedString) {
    List list = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer(concatenatedString, ",");
    while (tokenizer.hasMoreTokens()) {
      list.add(tokenizer.nextToken().trim());
    }
    String[] array = new String[list.size()];
    list.toArray(array);
    return array;
  }

  public static String getLogonUidForKPN(String kpn, String mode, String resAttr, String resDNAttr) throws SPNegoProtocolException, UMException {
    String logonID = null;
    IUser user = null;

    if (IConstants.UID_RESOLUTION_MODE_SIMPLE.equals(mode)) {
      user = getUserForAttribute(IPrincipal.DEFAULT_NAMESPACE, resAttr, kpn);
    } else if (IConstants.UID_RESOLUTION_MODE_PREFIXBASED.equals(mode)) {
      String kpnPrefix = getKPNPrefix(kpn);
      String kpnSuffix = getKPNSuffix(kpn);
      user = getUserByKerberosPrincipalName(kpnPrefix, kpnSuffix, resAttr, resDNAttr);
    } else if (IConstants.UID_RESOLUTION_MODE_NONE.equals(mode)) {
      // just checking if user exists
      // here the logonID should be equal to kpn
      user = UMFactory.getUserFactory().getUserByLogonID(kpn);
    } else if (IConstants.UID_RESOLUTION_MODE_KPNBASED.equals(mode)) {
      String kpnPrefix = getKPNPrefix(kpn);
      String kpnSuffix = getKPNSuffix(kpn);
      user = getUserByPrincipalAndRealm(kpnPrefix, kpnSuffix);
    }

    if (user != null) {
      logonID = user.getUserAccounts()[0].getLogonUid();
    }
    if (logonID == null) {
      throw new UMException("Can not get logonUid for user: " + user.toString());
    }

    return logonID;
  }

  // test main functions
  public static void main3(String[] args) {
    String inputbitstring = args[0];

    boolean[] barray = createBooleanArrayFromString(inputbitstring);
    System.out.println("Input Bit String: " + inputbitstring);
    System.out.println("Boolean array:");
    System.out.println(barray);

    int[] d = new int[1];
    byte[] testoutput = Utils.createByteArrayFromBool(barray, d);

    System.out.println("Input : " + inputbitstring);
    System.out.println("Output: " + new BIT_STRING(testoutput, d[0]).getBinaryString());
  }

  public static void main(String[] args) {
    // blockByteDump (new t1().token1, new PrintWriter (System.out));
  }

  public static void main2(String[] args) {
    System.out.println(getRawByteFromInt(Integer.parseInt(args[0])));
  }

}
