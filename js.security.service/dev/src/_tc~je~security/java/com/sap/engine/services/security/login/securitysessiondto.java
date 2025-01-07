/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Apr 8, 2008 by I030797
 *   
 */
 
package com.sap.engine.services.security.login;

import java.security.Principal;
import java.util.HashSet;

import com.sap.engine.session.usr.LoginSessionInterface;


/**
 * @author I030797
 *
 */
public class SecuritySessionDTO extends SecuritySession {
  
  static final long serialVersionUID = -1379928215670664229L;
  
  private long creationTime;
  private long expirationPeriod;
  private long lastAccessed;
  private long sessionNumber;
  
  private String authenticationConfiguration;
  
  private Principal principal;
  private boolean isAnonymous;
  
  private HashSet<String> securityPolicyDomains;
  
  
  public SecuritySessionDTO(LoginSessionInterface loginSession) {
    super(SecurityContext.getLoginAccessor());
    
    creationTime = loginSession.getCreationTime();
    expirationPeriod = 100000000000L;
    lastAccessed = 0L;
    sessionNumber = loginSession.getSessionNumber();
    
    authenticationConfiguration = loginSession.getAuthConfig();
    
    if(loginSession.getPrincipal() != null) {
     principal = new com.sap.engine.lib.security.Principal(loginSession.getPrincipal().getName()); 
    }
    
    isAnonymous = loginSession.isAnonymous();
    
    securityPolicyDomains = new HashSet<String>(loginSession.getSecurityPolicyDomains());
  }
  
  /**
   * @return Returns the authenticationConfiguration.
   */
  public String getAuthenticationConfiguration() {
    return authenticationConfiguration;
  }
  
  /**
   * @return Returns the creationTime.
   */
  public long getCreationTime() {
    return creationTime;
  }
  
  /**
   * @return Returns the expirationPeriod.
   */
  public long getExpirationPeriod() {
    return expirationPeriod;
  }
  
  /**
   * @return Returns the isAnonymous.
   */
  public boolean isAnonymous() {
    return isAnonymous;
  }
  
  /**
   * @return Returns the lastAccessed.
   */
  public long getLastAccessed() {
    return lastAccessed;
  }
  
  /**
   * @return Returns the principal.
   */
  public Principal getPrincipal() {
    return principal;
  }
  
  /**
   * @return Returns the securityPolicyDomains.
   */
  public HashSet getSecurityPolicyDomains() {
    return securityPolicyDomains;
  }
  
  /**
   * @return Returns the sessionNumber.
   */
  public long getSessionNumber() {
    return sessionNumber;
  }
  
  /**
   * @param authenticationConfiguration The authenticationConfiguration to set.
   */
  public void setAuthenticationConfiguration(String authenticationConfiguration) {
    this.authenticationConfiguration = authenticationConfiguration;
  }
  
  /**
   * @param creationTime The creationTime to set.
   */
  public void setCreationTime(long creationTime) {
    this.creationTime = creationTime;
  }
  
  /**
   * @param expirationPeriod The expirationPeriod to set.
   */
  public void setExpirationPeriod(long expirationPeriod) {
    this.expirationPeriod = expirationPeriod;
  }
  
  /**
   * @param isAnonymous The isAnonymous to set.
   */
  public void setAnonymous(boolean isAnonymous) {
    this.isAnonymous = isAnonymous;
  }
  
  /**
   * @param lastAccessed The lastAccessed to set.
   */
  public void setLastAccessed(long lastAccessed) {
    this.lastAccessed = lastAccessed;
  }
  
  /**
   * @param principal The principal to set.
   */
  public void setPrincipal(Principal principal) {
    this.principal = principal;
  }
  
  /**
   * @param securityPolicyDomains The securityPolicyDomains to set.
   */
  public void setSecurityPolicyDomains(HashSet securityPolicyDomains) {
    this.securityPolicyDomains = securityPolicyDomains;
  }
  
  /**
   * @param sessionNumber The sessionNumber to set.
   */
  public void setSessionNumber(long sessionNumber) {
    this.sessionNumber = sessionNumber;
  }
  
}
