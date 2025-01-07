/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.login;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SessionListener;
import com.sap.engine.interfaces.security.auth.SecuritySessionExtention;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.session.exec.LoginAccessor;
import com.sap.engine.session.usr.LoginSessionInterface;
import com.sap.engine.session.usr.SubjectHolder;
import com.sap.engine.session.usr.UserContextException;
import com.sap.tc.logging.Location;

/**
 * @author  Stephan Zlatarev, Lyubomir Assenov
 * @version 7.10
 */
public class SecuritySession implements Serializable, SecuritySessionExtention {

  static final long serialVersionUID = -3311240517087251050L;
  private final static Location TRACER = SecurityContext.TRACER;
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  
  private transient LoginAccessor loginAccessor;
  private transient Subject runAsSubject;
  
  
  /**
   * Constructor
   */
  SecuritySession(LoginAccessor loginAccessor) {
    if (IN_SERVER && loginAccessor == null) {
      throw new IllegalStateException("Can not create a security session without a login accessor");
    }
    
    this.loginAccessor = loginAccessor;
  }
  
  SecuritySession(SecuritySession parent) {
    this(parent.loginAccessor);
    runAsSubject = parent.runAsSubject;
  }
  
  /**
   * Constructor
   */
  SecuritySession(LoginAccessor loginAccessor, Subject subject) {
    this(loginAccessor);
    runAsSubject = subject;
  }

  /**
   * Getter
   */
  private LoginSessionInterface getLoginSession() {
    return loginAccessor == null ? null : loginAccessor.getThreadLoginSession();
  }
  
  /**
   * Getter
   */
  public boolean isAnonymous() {
    if (!IN_SERVER) {
      return isSubjectAnonymous(runAsSubject);
    }
    
    if (runAsSubject == null) {
      return getLoginSession().isAnonymous();
    }
    
    return isSubjectAnonymous(runAsSubject);
  }
  
  private boolean isSubjectAnonymous(Subject subject) {
    if (subject == null) {
      return true;
    }
    
    Principal anonPrincipal = SecurityContext.getAnonymousPrincipal();
    
    if (anonPrincipal == null) {
      return true;
    }
    
    for (Iterator iter = subject.getPrincipals().iterator(); iter.hasNext();) {
      Principal principal = (Principal) iter.next();
      
      if(principal.getName().equals(anonPrincipal.getName())) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Getter
   */
  public long getCreationTime() {
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getCreationTime();
    }
    
    return 0L;
  }

  /**
   * Getter
   * @deprecated
   */
  public long getLastAccessed() {
    return 0L;
  }

  /**
   * Getter
   */
  public long getSessionNumber() {
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getSessionNumber();
    }
    
    return 0L;
  }

  /**
   * Getter
   */
  public String getAuthenticationConfiguration() {
    if (runAsSubject != null) {
      return "run_as";
    }
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getAuthConfig();
    }
    
    return null;
  }

  /**
   * Setter
   */
  void setAuthenticationConfiguration(String authConfig) {
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      loginSession.setAuthConfig(authConfig);
    }
  }

  /**
   * Getter
   */
  public Subject getSubject() {
    if (runAsSubject != null) {
      return runAsSubject;
    }
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getSubject();
    }
    
    return null;
  }

  /**
   * Getter
   */
  SubjectHolder getSubjectHolder() {
    if (runAsSubject != null) {
      Principal runAsPrincipal = null;
      
      if (!runAsSubject.getPrincipals().isEmpty()) {
        runAsPrincipal = (Principal) runAsSubject.getPrincipals().iterator().next();
      }
      
      return new SubjectWrapper(runAsSubject, runAsPrincipal);
    }
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getSubjectHolder();
    }
    
    return null;
  }

  /**
   * Getter
   */
  public java.security.Principal getPrincipal() {
    if (runAsSubject != null && !runAsSubject.getPrincipals().isEmpty()) {
      return (Principal) runAsSubject.getPrincipals().iterator().next();
    }
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return loginSession.getPrincipal();
    }
    
    return null;
  }
  
  /**
   * Setter
   * @param subjectWrapper
   */
  void setSubjectHolder(SubjectHolder subjectWrapper) {
    if (runAsSubject != null) {
      runAsSubject = subjectWrapper.getSubject();
    } 
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      loginSession.setSubjectHolder(subjectWrapper);
    }
  }
  
  /**
   * Getter
   */
  String getUserName() {
    Principal principal = getPrincipal();
    
    if (principal != null) {
      return principal.getName();
    }
    
    return null;
  }

  /**
   * Getter
   */
  public HashSet<String> getSecurityPolicyDomains() {
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      return new HashSet<String> (loginSession.getSecurityPolicyDomains());
    }
    
    return new HashSet<String>();
  }

  /**
   * Setter
   */
  public void addSecurityPolicyDomain(String policyDomain) {
    if (policyDomain == null) {
      return;
    }
    
    LoginSessionInterface loginSession = getLoginSession();
    
    if (loginSession != null) {
      if (IN_SERVER && TRACER.beInfo()) {
        TRACER.infoT("Adding security policy domain '{0}' to {1}", new Object[] {policyDomain, this});
      }
      
      loginSession.addSecurityPolicyDomain(policyDomain);
    }
  }
  
  public Object getAttribute(String attrName) throws IOException, UserContextException {
    return loginAccessor == null ? null : loginAccessor.getCurrentClientAttribute(attrName);
  }
  
  public void setAttribute(String attrName, Object attrValue) throws IOException, UserContextException {
    if (loginAccessor != null) {
      loginAccessor.setCurrentClientAttribute(attrName, attrValue);
    }
  }
  
  /**
   *  Invalidates the current security session.
   *
   * @param authStack  The authentication stack name of the application.
   * @param handler  The callback handler to be used in the logout process.
   *
   * @throws javax.security.auth.login.LoginException  If the invalidation is not successful.
   * @throws BaseIllegalStateException  If the method is called from a remote client.
   */
  public void logout(String authStack, CallbackHandler handler) throws LoginException {
    if (!IN_SERVER) {
      throw new IllegalStateException("Calling SecuritySession.logout() at client side is not allowed");
    }    
    
    AuthenticationContext authenticationContext = 
      SecurityServerFrame.getSecurityContext().getPolicyConfigurationContext(authStack).getAuthenticationContext();
    
    FastLoginContext loginContext = (FastLoginContext) authenticationContext.getLoginContext(getSubject(), handler);
    loginContext.logoutSession();
  }
  
  void logout() {
    runAsSubject = null;
    
    if (loginAccessor != null) {
      if (TRACER.beInfo()) {
        TRACER.infoT("Logout {0} and detach it from the current thread", new Object[] {this});
      }
      
      loginAccessor.logout();
    }
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Current session now is {0}", new Object[] {this});
    }
  }
  
  void mergeSubject(SubjectWrapper newSubjectWrapper) {
    LoginSessionInterface clientLoginSession = loginAccessor.getClientLoginSession();
    SubjectWrapper subjectWrapper = (SubjectWrapper) clientLoginSession.getSubjectHolder();
    subjectWrapper.merge(newSubjectWrapper);
    clientLoginSession.setSubjectHolder(subjectWrapper);
    loginAccessor.applyClientContextToCurrentThreadById(loginAccessor.getClientId());
  }
  
  void clear() {
    runAsSubject = null;
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("[");
    
    if (IN_SERVER && runAsSubject != null) {
      result.append("Run-As ");
    }
    
    result.append("Security Session ");
    
    if (IN_SERVER) {
      LoginSessionInterface loginSession = getLoginSession();
      result.append("(session number: ").append(getSessionNumber()).append(") (");
      
      if (loginSession != null) {
        String clientId = loginSession.getClientId();
        
        if (clientId != null) {
          result.append("client id: ").append(clientId);
        } else if (isAnonymous()) {
          result.append("anonymous");
        } else {
          result.append("client id: ").append("null");
        }
      } else {
        result.append("client id: ").append("null");
      }
      
      result.append(") ");
    }
    
    result.append("(user name: ").append(getUserName()).append(")");
    
    if (IN_SERVER) {
      result.append(" (created at: ");
      result.append(new Date(getCreationTime()).toString());
      result.append(")");
      Set<String> domains = getSecurityPolicyDomains();
      
      if (!domains.isEmpty()) {
        result.append(" with security policy domains: ");
        result.append(domains);
      }
    }
    
    result.append("]");
    return result.toString();
  }

  
  //================================ deprecated stuff =================================
  
  /**
   * @deprecated
   */
  public void setPriority(int priority) {
    //do nothing
  }

  /**
   * @deprecated
   */
  public int getPriority() {
    return 0;
  }
  
  /**
   * @deprecated Session expiration on security session level is deprecated.
   *             Only expiration on application session level is considered.
   */
  public long getExpirationPeriod() {
    return 100000000000L;
  }

  /**
   * @deprecated Session expiration on security session level is deprecated.
   *             Only expiration on application session level is considered.
   */
  public void setExpirationPeriod(long expirationPeriod) {
    //do nothing
  }

  /**
   * @deprecated
   */
  public synchronized void registerListener(SessionListener listener) {
    //do nothing
  }

  /**
   * @deprecated
   */
  public synchronized void unregisterListener(SessionListener listener) {
    //do nothing
  }
  
}
