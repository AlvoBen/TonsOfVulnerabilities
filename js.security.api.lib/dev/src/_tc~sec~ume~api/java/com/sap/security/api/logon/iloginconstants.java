// $Id$
// $Change$
// $Author$
// $DateTime$

package com.sap.security.api.logon;

import java.lang.String;
/**
 *  Constants for logon. The undocumented ones are for internal use.
 */
public interface ILoginConstants
{

    public final static int CHECKPWD_OK = 0;

    public final static int CHECKPWD_WRONGPWD = 128;  // output for UI: Authentication failed
    public final static int CHECKPWD_NOPWD = 129; // output for UI: Authentication failed
    public final static int CHECKPWD_PWDLOCKED = 130; // output for UI: Authentication failed
    public final static int CHECKPWD_PWDEXPIRED = 131;


    // Parameters for login configuration in usermanagement.properties
    public final static String SSOTICKET_ALIAS              = "MYSAPSSO2";
    public final static String SSOTICKET_USER_ATTRIBUTE     = "$MYSAPSSO$";
	public final static String X509CERT_USER_ATTRIBUTE      = "$X509CERT$";

    public final static String SSOTICKET_USER_ATTRIBUTE_PURE= "MYSAPSSO2_STRING";
    public final static String SSOTICKET_DOMAIN             = "ume.logon.security.cookie_domain";
    public final static String SSOTICKET_DOMAINRELAXLEVEL   = "ume.logon.security.relax_domain.level";
    public final static String SSOTICKET_R3_USER_IN_CAPITALS= "ume.logon.security.r3user_capitalized";
    public final static String SSOTICKET_PURE_JAVA          = "login.ticket_creation_java";
    public final static String SSOTICKET_LIFETIME           = "login.ticket_lifetime";
    public final static String SSOTICKET_SERVUSER_LIFETIME  = "login.serviceuser.lifetime";
    public final static String SSOTICKET_ALLOWED_SERVUSER   = "login.allowed_service_users";
    public final static String SSOTICKET_ISSUER_NAME        = "login.ticket_issuer";
    public final static String SSOTICKET_ISSUER_CLIENT      = "login.ticket_client";
    public final static String SSOTICKET_KEYSTORE           = "login.ticket_keystore";
    public final static String SSOTICKET_KEYSTORE_PASSWORD  = "login.ticket_keystore_pw";
    public final static String SSOTICKET_KEYSTORE_TYPE      = "login.ticket_keystore_type";
    public final static String SSOTICKET_KEYALIAS           = "login.ticket_keyalias";
	public final static String SSOTICKET_STANDALONE         = "login.ticket_standalone";

    //public final static String SSOTICKET_AUTH_SERVER        = "login.authentication_server";
    public final static String SSOTICKET_INCLUDE_CERT       = "login.ticket_include_cert";
    public final static String SSOTICKET_SERV_TICKETFILE    = "login.serviceuser.ticketfile";
    public final static String SSOTICKET_CERTIFICATE_DN     = "login.ticket_dn";
    public final static String SSOTICKET_SECURE             = "ume.logon.security.enforce_secure_cookie";
    public final static String SSOTICKET_NT_REMOVE_DOMAIN   = "login.nt.remove_domain";
    public final static String SSOTICKET_PORTALID_MODE      = "login.ticket_portalid";
//    public final static String AUTHSCHEME                   = "authScheme";
    // R/3 access for external users
    public final static String SSOTICKET_R3_ACCESS_FOR_EXT  = "login.r3_access_for_ext_users";
    public final static String SSOTICKET_TRY_R3_USER        = "login.try_r3_user";
    // Parameters for JCO-enrich calls and jcodestinations
    public final static String SSO_JCO_LOGON_METHOD         = "LogonMethod";
    public final static String SSO_JCO_LOGON_METHOD_TICKET  = "SAPLOGONTICKET";
    public final static String SSO_JCO_LOGON_METHOD_UIDPW   = "UIDPW";
    public final static String SSO_JCO_LOGON_METHOD_X509CERT= "X509CERT";
    public final static String SSO_JCO_REMOTE_SYSTEM        = "System";

    public final static String JAAS_UME_PREFIX              = "com.sap.security.core";

    public final static String GUEST_USER_LIST              = "guest_user_list";

    public final static String MULTI_DOMAIN_COOKIE_HOSTS    = "ume.login.mdc.hosts";
    public final static String R3_MASTERSYSTEM              = "ume.r3.mastersystem";
    public final static String R3_UID_MODE                  = "ume.r3.mastersystem.uid.mode";
    
    public static final String CERTIFICATE_ENROLL = "ume.logon.client_certificate_enroll";
    public static final String CERTIFICATE_ENROLL_DEFAULT_VALUE = "disabled";
    public static final String CERTIFICATE_ENROLL_ENFORCE_VALUE = "enforced";
    public static final String GLOBAL_AUTH_TEMPLATE = "ume.login.context";
    
    /**
     * The key to the authentication method that is used when the application
     * does not specify its own specific value. 
     */
    public static final String GLOBAL_AUTH_METHOD = "ume.login.auth_method";
    // constants for logon

    /**
     *  Constant for use in {@link ILogonAuthentication}. Please see there for more details.
     *  This is the key the function
     *  {@link ILogonAuthentication#logon(HttpServletRequest,HttpServletResponse,String)} looks
     *  up for for a user name.
     */
    public final static String LOGON_UID_ALIAS              = "j_user";
    public final static String LOGON_USER_ID              = "j_username";

    /**
     * UME attribute for the logon alias.
     *  
     * Currently only different from LOGON_UID_ALIAS for R3Persistence
     * to support alias logon with ABAP.
     */
    public final static String LOGON_ALIAS = "logonalias";
    
	/**
	 *  Constant for use in {@link ILogonAuthentication}. Please see there for more details.
	 *  This is the key the function
	 *  {@link ILogonAuthentication#logon(HttpServletRequest,HttpServletResponse,String)} looks
	 *  up for for a password.
	 */
    public final static String LOGON_PWD_ALIAS              = "j_password";

	public final static String LOGON_REPOSITORIES_ALIAS     = "dataSourceIDs";

    public final static String LOGON_AUTHSCHEME_ALIAS       = "j_authscheme";
    public final static String LOGON_PRINCIPAL_ID_ALIAS     = "uniqueIDOfPrincipal";

    public final static String LOGON_REQUIRED_AUTHSCHEME    = "com.sap.security.logon.authscheme.required";
    public static final String LOGON_CERT_ALIAS             = "javax.servlet.request.X509Certificate";

	public final static String REDIRECT_PARAMETER           = "redirectURL";
    public final static String OLD_PASSWORD                 = "j_sap_current_password";
    public final static String NEW_PASSWORD                 = "j_sap_password";
    public final static String CONFIRM_PASSWORD             = "j_sap_again";
    public final static String COMPANY_ID                   = "orgid";

    public final static String PROP_HTTP_ONLY_COOKIE = "ume.logon.httponlycookie";

    public static final String TICKET_CACHE_MAXWEIGHT= "ume.cache.ticket_cache.maxweight";

}
