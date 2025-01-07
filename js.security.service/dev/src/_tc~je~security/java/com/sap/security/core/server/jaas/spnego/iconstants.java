package com.sap.security.core.server.jaas.spnego;

import iaik.asn1.ObjectID;

/**
 * Constants interface. This interface contains all parameter names for
 * configuration and corresponding default (if they exist). For configuration
 * parameters, there is a pattern that facilitates configuration and helps
 * finding errors in configuration. Each parameter name for the login module is
 * available as a constant in this interface. You can recognize them by the
 * prefix CONF_. If there is a default value for the parameter, then there is an
 * additional constant with the same name except for the prefix, which is
 * DEFAULT_ for the default value. E.g. the default value of parameter
 * {@link #CONF_GSS_MECH} is stored in {@link #DEFAULT_GSS_MECH}.
 * <p>
 * Unless stated otherwise, the CONF_ parameters are options for the
 * {@link  com.sap.security.core.server.jaas.SPNegoLoginModule}.
 */
public interface IConstants {
  public static final Object LOGIN_NAME = "javax.security.auth.login.name";

  /**
   * By default when a user cannot be identified from the Negotiate token a
   * response code 401 is set. This is because when a prgrammatic authentication
   * is used - i.e. in UserAdmin or Portal - setting the status code to 401 is
   * the only way to make browser stop sending a Negotiate token (which causes
   * the POST DATA not to be sent in the HTTP stream, thus preventing user
   * credentials from reaching the server). Stopping Negotiate token is helpful
   * if you need a (basic password for example) fallback in case of a
   * programmatic authentication. This option allows preventing setting the
   * status code. In the same time it leaves a space for future implementations
   * allowing setting the response code to values other than the hardcoded 401.
   */
  public static final String CONF_SET_HTTP_STATUSCODE_ON_FAIL = "com.sap.spnego.set_status_on_fail";

  /**
   * Kerberos name of the J2EE Engine. Can be either of the form HTTP/&lt;host
   * name&gt;@&lt;Kerberos Realm&gt; or as host based service name HTTP@&lt;host
   * name&gt;. The first form is the default. if the second form is desired, the
   * parameter {@link #CONF_GSS_NAME_TYPE} must be set to 0.
   */
  public static final String CONF_GSS_NAME = "com.sap.spnego.jgss.name";

  /**
   * Form of Kerberos name. 1 is default, 0 means host based service name (see
   * {@link #CONF_GSS_NAME}).
   */
  public static final String CONF_GSS_NAME_TYPE = "com.sap.spnego.jgss.name.type";

  /**
   * Under this parameter name the OID for the credential acquisition is stored.
   * Default is 1.2.840.113554.1.2.2 (Kerberos name)
   */
  public static final String CONF_GSS_MECH = "com.sap.spnego.jgss.mech";

  /**
   * Under this parameter the list of supported GSS mechanisms is stored.
   * Default for this parameter is Kerberos V5 and Kerberos V5 Legacy (which
   * turns out to be the same mechanism but only with a different OID for legacy
   * purposes.
   */
  public static final String CONF_SUPPORTED_MECHS = "com.sap.spnego.jgss.supp_mechs";

  /**
   * Mode how the user resolution (Kerberos Principal Name -> IUser object)
   * takes place. Possible values in configuration: simple, prefixbased
   * <p>
   * &quot;simple&quot; means that for the search for a user object is based on
   * a simple attribute search in UME. The attribute to base the search on is
   * krb5principalname by default (which is usually mapped to the physical
   * attribute userprincipalname) and can be overruled by
   * {@link #CONF_UID_RESOLUTION_ATTR}.
   * <p>
   * &quot;prefixbased&quot; means that the KPN (e.g. d028305@WDF.SAP.CORP) is
   * split into two parts, a prefix (d028305) and a suffix (WDF.SAP.CORP). Then
   * we perform a user search for the user that has the attribute specified by
   * {@link #CONF_UID_RESOLUTION_ATTR} equal to the kpn prefix. In this case the
   * default of {@link #CONF_UID_RESOLUTION_ATTR} is the uniquename attribute.
   * The attribute must be mapped to samaccountname. If uniquename is not mapped
   * to samaccountname then {@link #CONF_UID_RESOLUTION_ATTR} must be set to an
   * attribute which is mapped to samaccountname.
   */
  public static final String CONF_UID_RESOLUTION_MODE = "com.sap.spnego.uid.resolution.mode";

  /**
   * See comments on {@link #CONF_UID_RESOLUTION_MODE} for details.
   */
  public static final String CONF_UID_RESOLUTION_ATTR = "com.sap.spnego.uid.resolution.attr";

  /**
   * See comments on {@link #CONF_UID_RESOLUTION_MODE} for details.
   */
  public static final String CONF_UID_RESOLUTION_DN = "com.sap.spnego.uid.resolution.dn";

  /**
   * This parameter specifies whether or not the credential acquisition is to be
   * performed in a separate thread.
   */
  public static final String CONF_CREDS_IN_THREAD = "com.sap.spnego.creds_in_thread";

  /**
   * In J2EE SP9 sometimes the logging doesn't work properly. Therefore you can
   * switch on internal logging, which dumps everything to stderr
   */
  public static final String CONF_INTERNAL_LOGGING = "com.sap.spnego.internal_logging";

  // /////////////////////////////////////////////////////////////////////////////////////
  //
  // C A C H E P A R A M E T E R S
  //    
  // We have two caches in this software module. Both are derived from
  // {@link com.sap.security.core.server.jaas.spnego.util.ShortLifetimeCache}.
  // The purpose is to cache items
  // for a very short lifetime only. To customize the caches, we have two
  // parameters:
  // item lifetime and cleanup. Item lifetime specifies the lifetime of a single
  // item.
  // The cleanup parameter specifies after how much time the cache should sort
  // out
  // out-dated items. For more information on the caches and their purposes, see
  // {@link com.sap.security.core.server.jaas.spnego.util.IpAddressCache} and
  // {@link com.sap.security.core.server.jaas.spnego.util.ThreadTokenCache}
  //
  // /////////////////////////////////////////////////////////////////////////////////////

  /**
   * Specifies the lifetime of an item in the thread token cache in
   * milliseconds.
   */
  public static final String CONF_TTC_ITEM_LIFETIME = "com.sap.spnego.ttc.item.lifetime";

  /**
   * Specifies after how many milliseconds outdated items in the thread token
   * cache should be sorted out.
   */
  public static final String CONF_TTC_CLEANUP = "com.sap.spnego.ttc.cleanup";

  /**
   * Specifies the lifetime of an item in the ip address cache in milliseconds.
   */
  public static final String CONF_ISC_ITEM_LIFETIME = "com.sap.spnego.isc.item.lifetime";

  /**
   * Specifies after how many milliseconds outdated items in the ip address
   * cache should be sorted out.
   */
  public static final String CONF_ISC_CLEANUP = "com.sap.spnego.isc.cleanup";

  /**
   * Probably not used anymore.
   */
  public static final String TEST_MIC_IN_TARG = "test.include.mic.targ";

  public static final int OCTETSTRING_DUMP_LIMIT = 12;
  public static final String AUTH_HEADER_NAME = "Authorization";
  public static final String WWW_AUTHENTICATE_NAME = "WWW-Authenticate";
  public static final String NEGOTIATE = "Negotiate";
  public static final String WWW_AUTHENTICATE_TEMPLATE = NEGOTIATE + " {0}";

  // other values
  public static final String UID_RESOLUTION_MODE_NONE = "none";
  public static final String UID_RESOLUTION_MODE_SIMPLE = "simple";
  public static final String UID_RESOLUTION_MODE_PREFIXBASED = "prefixbased";
  public static final String UID_RESOLUTION_MODE_KPNBASED = "kpnbased";
  
  public static final String[] RESOLUTION_MODES = { UID_RESOLUTION_MODE_PREFIXBASED, 
                                      UID_RESOLUTION_MODE_SIMPLE,
                                      UID_RESOLUTION_MODE_NONE,
                                      UID_RESOLUTION_MODE_KPNBASED };
  
  // Defaults
  public static final String DEFAULT_GSS_NAME_TYPE = "1";
  public static final String DEFAULT_GSS_MECH = "1.2.840.113554.1.2.2";
  public static final String DEFAULT_SUPPORTED_MECHS = "1.2.840.48018.1.2.2,1.2.840.113554.1.2.2";
  public static final String DEFAULT_CREDS_IN_THREAD = "false";
  public static final String DEFAULT_TTC_ITEM_LIFETIME = "1000";
  public static final String DEFAULT_TTC_CLEANUP = "30000";
  public static final String DEFAULT_ISC_ITEM_LIFETIME = "5000";
  public static final String DEFAULT_ISC_CLEANUP = "30000";
  public static final String DEFAULT_UID_RESOLUTION_MODE = UID_RESOLUTION_MODE_SIMPLE;
  public static final String DEFAULT_UID_RESOLUTION_ATTR_KRB5 = "krb5principalname";
  public static final String DEFAULT_UID_RESOLUTION_ATTR_UN = "uniquename";
  public static final String DEFAULT_UID_RESOLUTION_DN = "dn";

  // config parameter names

  /**
   * OID to call GSSManager.createName () with
   */
  // public static final String CONF_JGSS_OIDS_PRINCTYPE =
  // "com.sap.jgss.oids.nametype";
  /**
   * OID parameter name to call GSSManager.createCredentials () with
   */
  // public static final String CONF_JGSS_OIDS_MECHTYPE =
  // "com.sap.jgss.oids.credtype";
  /**
   * OID that identifies an SP Nego token to be one
   */
  public static final ObjectID OID_SP_NEGO = new ObjectID("1.3.6.1.5.5.2", "SPNego Token", "SPNego");

  /**
   * Kerberos Legacy OID (same as Kerberos V5, but off 1 bit required for legacy
   * compatibility
   */
  public static final ObjectID OID_KERBV5_LEGACY = new ObjectID("1.2.840.48018.1.2.2", "Kerberos V5 Legacy", "KerbV5Leg");

  /**
   * Standard Kerberos V5 OID
   */
  public static final ObjectID OID_KERBV5 = new ObjectID("1.2.840.113554.1.2.2", "Kerberos V5", "KerbV5");

  // OIDs for the jgss methods

  /**
   * OID to call GSSManager.createName () with
   */
  public static final String OID_JGSS_KRB5NAME = "1.2.840.113554.1.2.2.1";

  /**
   * OID to call GSSManager.createCredentials () with
   */
  public static final String OID_JGSS_MECHTYPE = "1.2.840.113554.1.2.2";

  /**
   * Corresponds to
   * 
   * @see org.ietf.gss.GSSName#NT_HOSTBASED_SERVICE
   */
  public static final int SPNEGO_NT_HOSTBASED_SERVICE = 0;

  /**
   * Corresponds to
   * 
   * @see org.ietf.gss.GSSName#NT_USER_NAME
   */
  public static final int SPNEGO_NT_USER_NAME = 1;

  public static final byte SPNEGO_TOKEN_TYPE_INIT = 0;
  public static final byte SPNEGO_TOKEN_TYPE_TARG = 1;

  //
  // Bytes needed for ASN.1 encoding
  //

  /**
   * Identifier of the APPLICATION TYPE
   */
  public static final int SPNEGO_TOKEN_IDENTIFIER = 0x60;

  /**
   * Identifier of an Init token
   */
  public static final int SPNEGO_TYPE_ID_INIT = 0xa0;

  /**
   * Identifier of a Targ token
   */
  public static final int SPNEGO_TYPE_ID_TARG = 0xa1;

  //
  // Negotiation result codes (implementation as ASN.1 type ENUMERATION)
  //

  /**
   * initial (variable has just been initialized)
   */
  public static final int SPNEGO_NEG_ACCEPT_INITIAL = -1;

  /**
   * accept completed (handshake finished)
   */
  public static final int SPNEGO_NEG_ACCEPT_COMPLETED = 0;

  /**
   * still incomplete (continuation needed)
   */
  public static final int SPNEGO_NEG_ACCEPT_INCOMPLETE = 1;

  /**
   * handshake failed
   */
  public static final int SPNEGO_NEG_REJECTED = 2;

  //
  // Context flags that will be passed to gss_accept_sec_context()
  //
  public static final int SPNEGO_CTXFLAGS_DELEG = 0;
  public static final int SPNEGO_CTXFLAGS_MUTUAL = 1;
  public static final int SPNEGO_CTXFLAGS_REPLAY = 2;
  public static final int SPNEGO_CTXFLAGS_SEQUENCE = 3;
  public static final int SPNEGO_CTXFLAGS_ANON = 4;
  public static final int SPNEGO_CTXFLAGS_CONF = 5;
  public static final int SPNEGO_CTXFLAGS_INTEG = 6;

  public static final int SPNEGO_CTXFLAGS_MAXIDX = 6;

  /**
   * String key under which the current status is stored in the HttpSession.
   */
  public static final String SPNEGO_SESSION = "com.sap.spnego.session";

}
