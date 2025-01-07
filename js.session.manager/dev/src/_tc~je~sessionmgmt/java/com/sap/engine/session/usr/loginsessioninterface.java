package com.sap.engine.session.usr;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;
import java.io.Serializable;

public interface LoginSessionInterface extends Serializable {

    /**
   * Setter of the authentication configuration
   * @param authConf the authentication configuration
   */
  public void setAuthConfig(String authConf);

  /**
   * Getter of the authentication configuration
   * @return the authentication configuration
   */
  public String getAuthConfig();

  /**
   * Setter of the Principal
   * @param principal the principal
   * @deprecated to be removed
   */
  public void setPrincipal(Principal principal);

  /**
   * Getter of the principal
   * @return the principal
   */
  public Principal getPrincipal();

  /**
   * Setter of the subject
   * @param subject the subject
   * @deprecated
   */
  public void setSubject(Subject subject);

  /**
   * Sets the subject wrapper holder
   * @param holder the holder
   */  
  public void setSubjectHolder(SubjectHolder holder);

  /**
   * Getter of the subject wrapper holder
   * @return the holder
   */
  public SubjectHolder getSubjectHolder();

  /**
   * Getter of the subject
   * @return the subject
   */
  public Subject getSubject();

  /**
   * Setter of the security policy domains
   * @param secPD the security policy domains
   */
  public void setSecurityPolicyDomains(Set<String> secPD);

  /**
   * Adds a security policy domain to the set
   * @param secPD the security policy domain
   */
  public void addSecurityPolicyDomain(String secPD);

  /**
   * Getter of the security policy domains
   * @return the set of security policy domains
   */
  public Set<String> getSecurityPolicyDomains();
  
  /**
   * Persist Login Session
   */
  public void persist();

  /**
   * Checks if the session is anonymous
   * @return TRUE if the session is anonymous, false if not
   */
  public boolean isAnonymous();

  /**
   * Returns the session number
   * @return the session number
   */
  public long getSessionNumber();

  /**
   *  Returns this session creation time
   * @return the creation time
   */
  public long getCreationTime();

  /**
   * Returns the clientID of the assigned client context
   * @return the client id
   */
  public String getClientId();

}
