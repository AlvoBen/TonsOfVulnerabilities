package com.sap.engine.session.exec;

import com.sap.engine.session.usr.LoginSessionInterface;
import com.sap.engine.session.usr.LoginSession;
import com.sap.engine.session.usr.ClientContext;
import com.sap.engine.session.usr.SubjectHolder;

import javax.security.auth.Subject;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.security.Principal;

public class LoginSessionImpl implements LoginSessionInterface {

  private static long sessionCounter;

  private long creationTime;

  private String authenticationConfiguration;

  private SubjectHolder subjectHolder;

  private Set<String> securityPolicyDomains;

  private long sessionNumber;

  private LoginSession ls = new LSession();

  private transient ClientContextImpl clientContext;

  public LoginSessionImpl() {
    sessionNumber = getAndCountSession();
    creationTime = System.currentTimeMillis();
  }

  private static synchronized long getAndCountSession() {
    return sessionCounter++;
  }

  private Set<String> securityPolicyDomains() {
    if (securityPolicyDomains == null) {
      synchronized(this) {
        if (securityPolicyDomains == null) {
          securityPolicyDomains = new HashSet<String>(2);
        }
      }
    }
    return securityPolicyDomains;
  }

  public void setAuthConfig(String authConf) {
    assertNotAnonymous();
    this.authenticationConfiguration = authConf;
    if(clientContext != null){
      clientContext.persistLoginSession();
    }
  }

  public String getAuthConfig() {
    return authenticationConfiguration;
  }

  public void setPrincipal(Principal principal) {
  }

  public void setSubject(Subject subject) {
  }

  public void setSubjectHolder(SubjectHolder holder) {
    assertNotAnonymous();
    subjectHolder = holder;
    if(clientContext != null){
      clientContext.persistLoginSession();
    }
  }

  public SubjectHolder getSubjectHolder() {
    return subjectHolder;
  }

  public Principal getPrincipal() {
    if (subjectHolder != null) {
      return subjectHolder.getPrincipal();
    } else {
      return null;
    }
  }

  public Subject getSubject() {
    if (subjectHolder != null) {
      return subjectHolder.getSubject();
    } else {
      return null;
    }
  }

  public void setSecurityPolicyDomains(Set<String> secPD) {
    assertNotAnonymous();
    this.securityPolicyDomains = secPD;
    if(clientContext != null){
      clientContext.persistLoginSession();
    }
  }

  public void addSecurityPolicyDomain(String secPD) {
  	synchronized (securityPolicyDomains()) {
  		securityPolicyDomains().add(secPD);
  	}   
    if(clientContext != null){
      clientContext.persistLoginSession();
    }
  }

  public Set<String> getSecurityPolicyDomains() {
    return securityPolicyDomains();
  }
  
  public void persist(){
    if(clientContext != null){
      clientContext.persistLoginSession();
    }
  }

  public boolean isAnonymous() {
    return (this == ClientContextImpl.anonymousLoginSession);
  }

  public long getSessionNumber() {
    return sessionNumber;
  }

  public long getCreationTime() {
    return creationTime;
  }

  void setClientContext(ClientContextImpl clientContext) {
    assertNotAnonymous();
    this.clientContext = clientContext;
  }

  ClientContext getClientContext() {
    return clientContext;
  }

  public String getClientId() {
    if (clientContext != null) {
      return clientContext.getClientId();
    } else {
      return null;
    }
  }

  private final String CRLF = System.getProperty("line.separator", "/r/n");

  public String toString() {
    StringBuilder builder = new StringBuilder(CRLF);
    builder.append("Login Session : ").append(System.identityHashCode(this)).append(CRLF);
    builder.append(" - session number : ").append(sessionNumber).append(CRLF);
    builder.append(" - creation time  : ").append(new Date(creationTime)).append(CRLF);
    builder.append(" - subjectHolder  : ").append(subjectHolder).append(CRLF);
    builder.append(" - auth config    : ").append(authenticationConfiguration).append(CRLF);
    if (clientContext != null) {
      builder.append(" - client context ID : ").append(clientContext.getClientId()).append(CRLF);
    } else {
      builder.append(" - no client context(anonymous login session)").append(CRLF);
    }
    builder.append(" - policy domains : ").append(securityPolicyDomains).append(CRLF);
    return builder.toString();
  }

  /**
   * The anonymous session is not allowed to be changed
   */
  private void assertNotAnonymous() {
    if (this == ClientContextImpl.anonymousLoginSession) {
      throw new IllegalStateException("Changing the anonymous session is not allowed.");
    }
  }

  LoginSession  getLoginSession() {
    return ls;
  }

  public class LSession implements LoginSession {

    public Object identity() {
      return null;
    }

    public Subject getSubject() {
      return subjectHolder.getSubject();
    }

    public String getUserName() {
      return null;
    }

    public long getExpirationPeriod() {
      return 0;
    }

    public void setExpirationPeriod(long period) {
      throw new IllegalStateException("Setting expiration period is not alowed.");
    }

    public boolean isAnonymous() {
      return false;
    }

    public Principal getPrincipal() {
      return null;
    }
  }

  // ----------------------- DEPRECATED ------------------------------
  public Object identity() {
    return null;
  }

  public String getUserName() {
    return null;
  }

  public long getExpirationPeriod() {
    return 0;
  }

  public void setExpirationPeriod(long period) {
  }

}
