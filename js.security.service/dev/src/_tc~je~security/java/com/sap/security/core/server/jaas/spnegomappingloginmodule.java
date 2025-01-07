package com.sap.security.core.server.jaas;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.Principal;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.UMException;
import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.security.core.server.jaas.spnego.SPNegoProtocolException;
import com.sap.security.core.server.jaas.spnego.util.Utils;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SPNegoMappingLoginModule implements LoginModule {
  
  private static Location LOCATION = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION + ".SPNegoMappingLoginModule");

  private Map _sharedState = null;
  private String _uid_res_attr = null;
  String _user_name = null;
  private Subject _subject = null;
  private boolean _bDone = false;

  public boolean commit() throws LoginException{
    this.getUserFromSubject();
    if ( _user_name != null ) {
      _subject.getPrincipals().add( new Principal( _user_name ) );
    }
    return ( _user_name != null );
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#login()
   */
  public boolean login() throws LoginException{
    Object obj = _sharedState.get( IConstants.LOGIN_NAME );
    if ( obj != null && ( obj instanceof String ) ) {
      _user_name = (String) obj;
    }
    this.getUserFromSubject();
    return ( _user_name != null );
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#logout()
   */
  public boolean logout() throws LoginException{
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
   *      javax.security.auth.callback.CallbackHandler, java.util.Map,
   *      java.util.Map)
   */
  public void initialize( Subject subject,
                         CallbackHandler callbackHandler,
                         Map sharedState,
                         Map options ){
    Properties p = new Properties();
    p.putAll( options );
    _uid_res_attr = p.getProperty( IConstants.CONF_UID_RESOLUTION_ATTR );
    _sharedState = sharedState;
    _subject = subject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.security.auth.spi.LoginModule#abort()
   */
  public boolean abort() throws LoginException{
    return true;
  }

  //////////////////////////////////////////////////////////////////////
  //
  //        H E L P E R F U N C T I O N S
  // 
  //////////////////////////////////////////////////////////////////////
  private void getUserFromSubject() throws SPNegoProtocolException{
    Iterator it;
    if ( _user_name == null ) {
      it = _subject.getPrincipals().iterator();
      if ( it != null && it.hasNext() ) {
        java.security.Principal p = (java.security.Principal) it.next();
        _user_name = p.getName();
        _subject.getPrincipals().remove( p );
      }
    }
    if ( _user_name != null ) {
      this.getUserFromAttribute();
      _sharedState.put( IConstants.LOGIN_NAME, _user_name );
    }
  }

  /**
   * 
   */
  private void getUserFromAttribute() throws SPNegoProtocolException{
    IUser iuser = null;
    IUserAccount uacc = null;
    if ( _user_name == null )
      return;
    if ( _uid_res_attr == null )
      return;
    if ( _bDone == true ) // we map only once within one cycle
      return;
    if ( LOCATION.beInfo() ) {
      LOCATION.infoT( "Searching UME for user by attribute {0} = {1}",
                      new Object[] { _uid_res_attr, _user_name } );
    }
    try {
      iuser = Utils.getUserForAttribute( IPrincipal.DEFAULT_NAMESPACE,
                                         _uid_res_attr,
                                         _user_name);
      uacc = iuser.getUserAccounts()[0];
      _user_name = uacc.getLogonUid();
      _bDone = true;
    } catch ( UMException exc ) {
      if ( LOCATION.beError() ) {
        LOCATION.errorT( "Error during user repository access." );
        LOCATION.traceThrowableT( Severity.ERROR,
                                  "Error during user repository access.",
                                  exc );
      }
      throw new SPNegoProtocolException( "User Resolution not possible.", LoginExceptionDetails.SPNEGO_AUTHENTICATION_FAILED );
    }
  }
}