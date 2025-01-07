package com.sap.security.core.server.jaas;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.logon.IAuthScheme;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.imp.TenantFactory;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.security.core.util.imp.SSOUtils;
import com.sap.security.core.util.imp.Util;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;

/**
 * @author d026948
 *
 * Helper class to be used from LoginModules, that are used both in UME and DBMS environment.
 * Depending on main option ume.configuration.active, either the logon module options are used
 * or options from UME configuration (accessed through com.sap.security.api.UMFactory).
 */
public class UMEAdapter {

	/**
	 * statics
	 */
	// main option where to read configuration from
  protected static final String LOGIN_USER = "javax.security.auth.login.name";
  protected static final String AUTHSCHEME = "j_authscheme";
  protected static final String SYSTEM = "system";
  protected static final String TRUE = "true";
  protected static final String FALSE = "false";
  protected static final String RECIPIENT_SID = "recipientSID";
  protected static final String RECIPIENT_CLIENT = "recipientClient";
  private static final String AUTH_NAME = "sap.security.auth.configuration.name";
  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final String HEADER_NAME_SET_COOKIE = "Set-Cookie";
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  private static final Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_TICKET_LOCATION);

  public static int USERID_MODE_PORTAL = 0;
  public static int USERID_MODE_ABAP   = 1;

	/**
	 * members
	 */
	private Map        sharedState             = null;
	private Properties options                 = null;

  /** default keypair alias */
  static final String DEFAULT_KEYPAIR_ALIAS = "SAPLogonTicketKeypair";

  /** default keystore view */
  static final String DEFAULT_KEYSTORE_VIEW = "TicketKeystore";

  /** default ticket validity in hours */
  static final String DEFAULT_CLIENT = "000";

  public UMEAdapter(Map sharedState, Properties poptions) {
    this(sharedState, poptions, false);
  }

	/**
	 * to be called from LoginModules' constructor
	 * mappeduser will contain user for R/3 in any case
	 * user will contain portal user in any case
	 */
  public UMEAdapter(Map sharedState, Properties poptions, boolean isAssertionTicket) {
    final String METHOD = "constructor(Map, Properties, boolean)";

    try {
      if (LOCATION.bePath()) {
        LOCATION.entering(METHOD, new Object[]{sharedState, options});
      }

      this.options = new Properties();
      this.options.putAll(poptions);
      this.sharedState = sharedState;

      String authentication_template = (String) sharedState.get(AUTH_NAME);
      if (authentication_template != null) {
        IAuthScheme ias = InternalUMFactory.getAuthSchemeFactory().getAuthSchemeByAuthTemplate(authentication_template);

        if (ias != null) {
          this.options.setProperty(ILoginConstants.LOGON_AUTHSCHEME_ALIAS, ias.getName());
        } else {
          if (LOCATION.beInfo()) {
            LOCATION.infoT("No authscheme found that has auth template: [{0}]", new Object[] {authentication_template});
          }
        }
      }

      IUMConfiguration umeDynamicProps = InternalUMFactory.getConfiguration();      

			//SID and client
      String sid = (String) this.sharedState.get("System-ID");

      String client = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_ISSUER_CLIENT, UMEAdapter.DEFAULT_CLIENT);

      if (sid != null) {
        this.options.put(SYSTEM, sid);
      }

      this.options.put("client", client);

      String  ksKeyAlias = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_KEYALIAS, UMEAdapter.DEFAULT_KEYPAIR_ALIAS);
      String  ksView = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_KEYSTORE, UMEAdapter.DEFAULT_KEYSTORE_VIEW);

      this.options.put("password", "");
      this.options.put("keystore", ksView);
      this.options.put("alias", ksKeyAlias);

			// include own certificate?
      boolean includeCert = umeDynamicProps.getBooleanDynamic(ILoginConstants.SSOTICKET_INCLUDE_CERT, false);
      this.options.put("inclcert", includeCert ? "1" : "0");

			// ticket lifetime
      String strValid = null;
      if (isAssertionTicket) {
        strValid = umeDynamicProps.getStringDynamic("login.assertion_ticket_lifetime", "0:2");
      } else {
        strValid = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_LIFETIME, "8");
      }
      
      String strValidHour = "0";
      String strValidMin = "0";
      
      int idx = strValid.indexOf(':');
      if (idx > 0) {
        strValidHour = strValid.substring(0, idx);
        strValidMin = strValid.substring(idx + 1);
      } 
      else if(idx == 0 ){
        strValidMin = strValid.substring(1);
      }
      else{
        strValidHour = strValid;
      }
      
      try {
        Integer.parseInt(strValidHour);
      }
      catch(NumberFormatException e){
        String umeValue = new String(strValidHour);
        strValidHour = isAssertionTicket ? "0" : "8";
        LOCATION.traceThrowableT(Severity.ERROR, "Parsing UME property: Invalid ticket lifetime in hours: '{0}'. '" + strValidHour + "' hours will be used.", new Object[]{umeValue}, e);
      }
      
      try {
        Integer.parseInt(strValidMin);
      }
      catch(NumberFormatException e){
        String umeValue = new String(strValidMin);
        strValidMin = "0";
        LOCATION.traceThrowableT(Severity.ERROR, "Parsing UME property: Invalid ticket lifetime in minutes: '{0}'. '" + strValidMin + "' minutes will be used.", new Object[]{umeValue}, e);        
      }
      
      this.options.put("validity", strValidHour);
      this.options.put("validityMin", strValidMin);
      
      
      

	 		// cookie metadata
      String cookieDomain = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_DOMAIN);
      if(cookieDomain != null) {
        this.options.put(ILoginConstants.SSOTICKET_DOMAIN, cookieDomain);
      }

      String secureCookie = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_SECURE);
      if(secureCookie != null) {
        this.options.put(ILoginConstants.SSOTICKET_SECURE, secureCookie);
      }

      String httpOnlyCookie = umeDynamicProps.getStringDynamic(ILoginConstants.PROP_HTTP_ONLY_COOKIE);
      if (httpOnlyCookie != null) {
        this.options.put(ILoginConstants.PROP_HTTP_ONLY_COOKIE, httpOnlyCookie);
      }
    } finally {
      if (LOCATION.bePath()) {
        LOCATION.exiting(METHOD, new Object[] {this.options});
      }
    }
  }

	/**
   * @deprecated
	 * returns whether UME configuration is active or not
	 */
	public boolean isUMEConfigurationActive() {
		return true;
	}

	/**
	 * @return merged opetions
	 */
	public Properties getMergedOptions() {
		final String METHOD="getMergedOptions()";
		try {
			LOCATION.entering(METHOD);
			return this.options;
		}
		finally {
      if (LOCATION.bePath()) {
			  LOCATION.exiting(METHOD, new Object[] {this.options});
      }
		}
	}


	/**
	 * find the mapped R/3 user if possible
	 * returns the user, that is supposed to be put
	 * in user field of ticket
	 */
	private String getMappedUser( String user )
        throws UMException
	{
		final String METHOD = "getMappedUser()";
		String userR3 = null;
        TenantFactory tf = TenantFactory.getInstance ();

		try {
	      if (LOCATION.bePath()) {
				  LOCATION.entering(METHOD, new Object[]{user});
	      }
				if( user == null )
					return null;

	            if (tf.isBPOEnabled ()) {
	                return tf.getSAPSystemLoginName (user);
	            }
	            else {
		        try {
					IUser userUME = UMFactory.getUserFactory()
										.getUserByLogonID(user);
			        userR3 = UMFactory.getUserMapping().getR3UserName (userUME, null, false);
			        if (LOCATION.beDebug()) {
			          LOCATION.debugT("User {0} found in ticket. The mapped user is {1}", new Object[] {userUME, userR3});
			        }
		        }
		        catch (UMException umex) {
		          if (LOCATION.beDebug()) {
		            LOCATION.debugT("Inverse mapping for user [{0}] not possible. Check user mapping configuration.", new Object[] {user});
		          }
					return null;
		        }
				return userR3;
			}
		}
		finally {
      if (LOCATION.bePath()) {
			  LOCATION.exiting(METHOD, new Object[]{userR3});
      }
		}
	}

	public void setTicketAsCookie (CallbackHandler callbackHandler, String ticket, String user)
        throws LoginException
    {
		final String METHOD = "setTicketAsCookie()";
		try {
      if (LOCATION.bePath()) {
			  LOCATION.entering(METHOD, new Object[] {callbackHandler, user});
      }

      String cookieAttributeTicket = "";
      String cookieAttributePath = "";
      String cookieAttributeDomain = "";
      String cookieAttributeSecure = "";
      String cookieAttributeHttpOnly = "";

			// the engine's callbackhandler does not handle cookies as Cookie
			// objects but only the cookie values...
			HttpSetterCallback setCookieCb = new HttpSetterCallback();
			setCookieCb.setType(HttpCallback.SET_HEADER);
			setCookieCb.setName(HEADER_NAME_SET_COOKIE);

			StringBuffer cookieValue = new StringBuffer("MYSAPSSO2=");
			//ticket is cookie data
			cookieValue.append(ticket);
			cookieAttributeTicket = ticket;
			// now cookie metadata
			// expires is optional
			// cookieValue.append(";expires=Wdy, DD-Mon-YYYY HH:MM:SS GMT");
			// Empty value means that ticket cookie must expire
			if ("".equals(ticket)) {
				cookieValue.append("; max-age=0");
				//if max-age attribute is not supported by the browser, "expires" attribute will be used
				cookieValue.append("; expires=Thu, 01-Jan-1970 00:00:00 GMT");
			}

			cookieValue.append(";path=/");
			cookieAttributePath = "/";

			// set cookieDomain if specified in options
			HttpGetterCallback userAgentCb = new HttpGetterCallback();
			userAgentCb.setType(HttpCallback.HEADER);
			userAgentCb.setName("User-Agent");
			HttpGetterCallback serverNameCb = new HttpGetterCallback();
			serverNameCb.setType(HttpCallback.HEADER);
			serverNameCb.setName("Host");

			Callback[] getterArray = new Callback[2];
			getterArray[0] = userAgentCb;
			getterArray[1] = serverNameCb;
			try {
				callbackHandler.handle(getterArray);
			}
			catch( IOException ioe ) {
				throw new DetailedLoginException (ioe.getMessage(), LoginExceptionDetails.IO_EXCEPTION);
			}
			catch( UnsupportedCallbackException uce ) {
				throw new DetailedLoginException(uce.getMessage(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
            }

            String serverName = (String) serverNameCb.getValue();
            String userAgent  = (String) userAgentCb.getValue();

            if (serverName==null) {
                if (LOCATION.beDebug()) {
                    LOCATION.debugT ("HttpGetterCallback (http header \'host\') returns null. Cannot set cookie.");
                }
                throw new LoginException ("Cannot get remote host header field.");
            }

            // according to RFC the user-agent header is not required
//            if (userAgent==null) {
//                if (LOCATION.beDebug ()) {
//                    LOCATION.debugT (CATEGORY, "HttpGetterCallback (http header \'User-Agent\') returns null. Cannot set cookie.");
//                }
//		        throw new LoginException ("Cannot get user-agent header field.");
//            }

			String cookieDomain = UMFactory.getProperties().get(ILoginConstants.SSOTICKET_DOMAIN,
                SSOUtils.getSSOCookieDomain(userAgent, serverName));


			if (cookieDomain != null) {
				cookieValue.append(";domain="+cookieDomain);
				cookieAttributeDomain = cookieDomain;
			}

			// cookie is not scriptable in browser
            String httpOnlyCookie = (String) this.options.get (ILoginConstants.PROP_HTTP_ONLY_COOKIE);
            if (httpOnlyCookie!=null && httpOnlyCookie.equalsIgnoreCase (TRUE)) {
                cookieValue.append(";HttpOnly");
                cookieAttributeHttpOnly = TRUE;
            } else {
              	cookieAttributeHttpOnly = FALSE;
            }


            String secureCookie = (String) this.options.get(ILoginConstants.SSOTICKET_SECURE);
			if( secureCookie != null && secureCookie.equalsIgnoreCase(TRUE)) {
				cookieValue.append(";secure");
				cookieAttributeSecure = TRUE;
			} else {
			  	cookieAttributeSecure = FALSE;
			}

			if (LOCATION.beDebug()) {
				StringBuffer sb = new StringBuffer();
				sb.append("SAPLogonTicket cookie: \n");
				sb.append("MYSAPSSO2: " + cookieAttributeTicket + "\n");
				sb.append("path: " + cookieAttributePath + "\n");
				sb.append("domain: " + cookieAttributeDomain + "\n");
				sb.append("secure: " + cookieAttributeSecure + "\n");
				sb.append("HttpOnly: " + cookieAttributeHttpOnly);
				LOCATION.debugT(sb.toString());
			}

			setCookieCb.setValue(cookieValue.toString());
			Callback[] setterArray = new Callback[1];
			setterArray[0] = setCookieCb;
			try {
				callbackHandler.handle(setterArray);
        	}
			catch( IOException ioe ) {
				throw new DetailedLoginException(ioe.getMessage(), LoginExceptionDetails.IO_EXCEPTION);
			}
			catch( UnsupportedCallbackException uce ) {
				throw new DetailedLoginException(uce.getMessage(), LoginExceptionDetails.UNABLE_TO_PASS_SAP_LOGON_TICKET);
			}
		}
		finally {
			LOCATION.exiting(METHOD);
		}
	}

    /**
     *
     */
    public void setMappedUser ()
        throws UMException
    {

        String umeUser = null;
        String abapUser= null;

        String loginUser = (String) this.sharedState.get (LOGIN_USER);
        if( loginUser != null ) {
            abapUser = getMappedUser(loginUser);

            if (usePortalUserId ()) {
                umeUser = loginUser;
            }
        }

        if (umeUser!=null) {
            this.options.put ("user", umeUser);
        }
        else {
            this.options.remove ("user");
        }
        if (abapUser!=null) {
            this.options.put ("mappeduser", abapUser);
        }
    }

    public boolean usePortalUserId ()
    {
      IUMConfiguration umeDynamicProps = InternalUMFactory.getConfiguration();      
        if (umeDynamicProps.getBooleanDynamic("ume.superadmin.activated", false)) {
          return true;
        }
      
        String mode = umeDynamicProps.getStringDynamic(ILoginConstants.SSOTICKET_PORTALID_MODE, "auto");
        boolean bResult = false;

        if (mode.equalsIgnoreCase ("auto")) {
            // here we check whether there is an ABAP repository and/or a portal
            if (Util.isRepositoryAvailable (Util.REPOSITORY_ABAP_SYSTEM) &&
                    !Util.isRepositoryAvailable (Util.REPOSITORY_PCD)) {
                bResult = false;
            }
            else {
                bResult = true;
            }
        }
        else if (mode.equalsIgnoreCase ("yes")) {
            bResult = true;
        }
        else if (mode.equalsIgnoreCase ("no")) {
            bResult = false;
        }
        else
            throw new IllegalStateException ("Illegal configuration state discovered: UME param " +
                ILoginConstants.SSOTICKET_PORTALID_MODE + "=" + mode);

        return bResult ;
    }
}