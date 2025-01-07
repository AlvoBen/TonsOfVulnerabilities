package com.sap.engine.services.security.login;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;
import javax.security.auth.login.LoginContext;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.TransferableExt;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecurityContextObjectListener;
import com.sap.engine.interfaces.security.auth.LoginContextReference;
import com.sap.engine.session.exec.LoginAccessor;
import com.sap.engine.session.exec.LoginSessionImpl;
import com.sap.engine.session.usr.SubjectHolder;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class SecurityContext implements ContextObject, TransferableExt, com.sap.engine.lib.security.SecurityContext, SecurityContextObject {
  
  public final static String SESSION_LOCATION_NAME = "com.sap.engine.services.security.sessionmanagement";
  public final static String SESSION_LOCATION_NAME_CALLERS = SESSION_LOCATION_NAME + ".SecurityContext_callers";
  public final static String GET_SESSION_LOCATION_NAME = SESSION_LOCATION_NAME_CALLERS + ".getSession";
  public final static String SET_SESSION_LOCATION_NAME = SESSION_LOCATION_NAME_CALLERS + ".setSession";
  
  final static Location TRACER = Location.getLocation(SESSION_LOCATION_NAME);
  final static Location TRACER_GET_SESSION = Location.getLocation(GET_SESSION_LOCATION_NAME);
  final static Location TRACER_SET_SESSION = Location.getLocation(SET_SESSION_LOCATION_NAME);
  
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  
  private static LoginAccessor loginAccessor;
  private static SubjectHolder anonymousSubjectHolder;
  
  private SecuritySession session;
  
  private byte[] ticket = null;
  private Map<Object, byte[]> clusterNodeTickets = new HashMap<Object, byte[]>();
  private Map<WeakReferenceKey<SubjectDomainCombiner>, WeakReference<SecuritySession>> runAsTable = new HashMap<WeakReferenceKey<SubjectDomainCombiner>, WeakReference<SecuritySession>>();
  
  private String securityPolicyDomain = null;
  
  /**
   * Constructor
   */
  public SecurityContext() {
    if (IN_SERVER && loginAccessor == null) {
      init();
    }
    
    session = new SecuritySession(loginAccessor);
  }
  
  /**
   * @return Returns the anonymous subject.
   */
  static Subject getAnonymousSubject() {
    return anonymousSubjectHolder.getSubject();
  }
  
  /**
   * @return Returns the anonymous principal.
   */
  static Principal getAnonymousPrincipal() {
    return anonymousSubjectHolder.getPrincipal();
  }
  
  /**
   * Initializer
   */
  private static void init() {
    if (TRACER.beDebug()) {
      TRACER.debugT("Initializing security context objects");
    }
    
    try {
      String anonymousPrincipalName = UMFactory.getAnonymousUserFactory().getAnonymousUser().getName();
      Principal anonymousPrincipal = new com.sap.engine.lib.security.Principal(anonymousPrincipalName);
      
      Subject anonymousSubject = new Subject();
      anonymousSubject.getPrincipals().add(anonymousPrincipal);
      
      anonymousSubjectHolder = new SubjectWrapper(anonymousSubject, anonymousPrincipal);
      
      if (IN_SERVER && TRACER.beDebug()) {
        TRACER.debugT("Created anonymous {0}", new Object[] {anonymousSubjectHolder});
      }
      
      LoginSessionImpl anonymousLoginSession = new LoginSessionImpl();
      anonymousLoginSession.setSubjectHolder(anonymousSubjectHolder);
      
      TicketGenerator.setAnonymousPrincipal(anonymousPrincipal.getName());
      
      if (TRACER.beDebug()) {
        TRACER.debugT("Initializing anonymous login session {0}", new Object[] {anonymousLoginSession});
      }
      
      loginAccessor = LoginAccessor.setAnonymousLoginSession(anonymousLoginSession);
    } catch (Exception e) {
      if (TRACER.beError()) {
        SimpleLogger.traceThrowable(Severity.ERROR, TRACER, "ASJ.secsrv.000201", "Could not initialize security context objects", e);
      }
      
      throw new SecurityException("Anonymous principal not configured", e);
    }
    
    if (TRACER.beDebug()) {
      TRACER.debugT("Security context objects initialized");
    }
  }
  
  static void setCurrentThreadAnonymous() {
    if (IN_SERVER) {
      loginAccessor.setCurrentThreadAnonymous();
    }
  }
  
  public boolean isClientAnonymous() {
    if (!IN_SERVER) {
      return this.session.isAnonymous();
    }
    
    return loginAccessor.getClientLoginSession().isAnonymous();
  }
  
  public void emptySessionContextObjects() {
    if (IN_SERVER) {
      setSession(null);
      loginAccessor.emptyCurrentContextObject();
    }
  }
  
  //this method is used on client side when the first response arrives - it caries the anonymous subject and principal,
  //and that is the moment when the client side security context objects are being initialized with them.
  static void setAnonymousSubjectHolder(SubjectHolder subjectHolder) {
    anonymousSubjectHolder = subjectHolder;
  }
  
  static LoginAccessor getLoginAccessor() {
    return loginAccessor;
  }
  
  static boolean isInSubjectDoAs() {
    return AccessController.getContext().getDomainCombiner() instanceof SubjectDomainCombiner;
  }
  
  /**
   * Invoked when a new child thread is being created 
   */
  public ContextObject childValue(ContextObject parent, ContextObject child) {
    if (child == null) {
      child = new SecurityContext();
    }
    
    SecurityContext parentSco = (SecurityContext) parent;
    SecurityContext childSco = (SecurityContext) child;
    
    childSco.session = new SecuritySession((SecuritySession) parentSco.getSession());
    
    childSco.ticket = parentSco.ticket;
    childSco.securityPolicyDomain = parentSco.securityPolicyDomain;
    
    childSco.runAsTable.clear();
    childSco.runAsTable.putAll(parentSco.runAsTable);
    
    childSco.clusterNodeTickets.clear();
    childSco.clusterNodeTickets.putAll(parentSco.clusterNodeTickets);
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Parent {0} copied to child {1}", new Object[] {parent, child});
    }

    return child;
  }
  
  /**
   * Create new context object (or get it from the pool).
   */
  public ContextObject getInitialValue() {
    return new SecurityContext();
  }
  
  /**
   * Empty this context object on returning the thread to the pool
   */
  public void empty() {
    ticket = null;
    securityPolicyDomain = null;
    
    session.clear();
    runAsTable.clear();
    clusterNodeTickets.clear();
  }
  
  /**
   * Getter
   */
  public long getCreationTime() {
    return getSession().getCreationTime();
  }

  /**
   * Getter
   */
  public long getLastAccessed() {
    return getSession().getLastAccessed();
  }

  /**
   * Getter
   */
  public java.security.Principal getPrincipal() {
    return getSession().getPrincipal();
  }

  /**
   * Getter
   */
  public javax.security.auth.Subject getSubject() {
    return getSession().getSubject();
  }

  /**
   * Getter
   */
  public SecuritySession getSession() {
    if (!IN_SERVER) {
      return getSessionWithoutDomainCombiner();
    }
    
    if (TRACER_GET_SESSION.beDebug()) {
      TRACER_GET_SESSION.traceThrowableT(Severity.DEBUG, "Get Session Callers ", new Exception("Stack Trace"));
    }
    
    if (isInSubjectDoAs()) {
      final SubjectDomainCombiner subjectCombiner = (SubjectDomainCombiner) AccessController.getContext().getDomainCombiner();
      WeakReferenceKey<SubjectDomainCombiner> key = new WeakReferenceKey<SubjectDomainCombiner>(subjectCombiner);
      WeakReference<SecuritySession> ref = runAsTable.get(key);
      SecuritySession runAsSession = null;
      
      if (ref != null) {
        runAsSession = ref.get();
      }
      
      if (runAsSession == null) {
        try {
          Subject subject = AccessController.doPrivileged(new PrivilegedExceptionAction<Subject>() {
            public Subject run() {
              return subjectCombiner.getSubject();
            }
          });

          runAsSession = new SecuritySession(loginAccessor, subject);
          
          if (TRACER.beDebug()) {
            TRACER.debugT("In Run-As call, the current session is {0}", new Object[] {runAsSession});
          }
          
          runAsTable.put(new WeakReferenceKey<SubjectDomainCombiner>(subjectCombiner), new WeakReference<SecuritySession>(runAsSession));
        } catch (PrivilegedActionException e) {
          throw new SecurityException("Insufficient privileges to obtain Subject from SubjectDomainCombiner.", e);
        }
      } else if (!runAsTable.isEmpty()) {
        clearRunAsTableEmptyEntries();
      }

      SessionMonitor.setCurrentUser(runAsSession.getUserName());
      return runAsSession;
    } else if (!runAsTable.isEmpty()) {
      clearRunAsTableEmptyEntries();
      SessionMonitor.setCurrentUser(getSessionWithoutDomainCombiner().getUserName());
      
      if (TRACER.beDebug()) {
        TRACER.debugT("Not in Run-As call, the current session is {0}", new Object[] {session});
      }
    }
    
    return getSessionWithoutDomainCombiner();
  }
  
  private void clearRunAsTableEmptyEntries() {
    for (Iterator<Entry<WeakReferenceKey<SubjectDomainCombiner>, WeakReference<SecuritySession>>> iter = runAsTable.entrySet().iterator(); iter.hasNext();) {
      WeakReference<SecuritySession> val = iter.next().getValue();
      
      if (val == null || val.get() == null) {
        iter.remove();
      }
    }
  }
  
  /** 
   * @deprecated use getSession() instead
   */
  public SecuritySession getLoginSession() {
//  Left for backwards compatibility
//  todo remove together with a fix in UMEServiceFrame.getSecuritySession()
    return session;
  }

  public SecuritySession getSessionWithoutDomainCombiner() {
    return session;
  }
  
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("[Security Context : ");
    result.append(getSession());
    result.append("]");
    
    return result.toString();
  }
  
  
  /**
   * Setter
   */
  public void setSession(com.sap.engine.interfaces.security.SecuritySession session) {
    if (TRACER_SET_SESSION.beDebug()) {
      TRACER_SET_SESSION.traceThrowableT(Severity.DEBUG, "Set Session Callers ", new Exception("Stack Trace"));
    }
    
    if (session == null) {
      setCurrentThreadAnonymous();
    } else {
      this.session = (SecuritySession) session;
      SessionMonitor.setCurrentUser(getSession().getUserName());
    }

    this.ticket = null;
    this.securityPolicyDomain = null;
    
    runAsTable.clear();
    clusterNodeTickets.clear();
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("{0} set to this thread", new Object[] {this.session});
    }
  }
  
  /**
   * Return size of the object if it will be stored into a byte array
   */
  public int size() {
    if (ticket == null) {
      ticket = TicketGenerator.getAnonymousTicket();
    }
    
    ticket = TicketGenerator.generateTicket(ticket, getSession());
    return ticket.length;
  }

  /**
   * Return size of the object if it will be stored into a byte array
   * This method is used when the transfer is between different clusters.
   *
   * @param clusterNodeId - identifier of the requesting cluster.
   */
  public int size(Object clusterNodeId) {
    byte[] tempTicket = getTicketForClusterNode(clusterNodeId);
    return tempTicket.length;
  }

  /**
   * Stores a ticket generated .
   *
   * @param to     - the byte array where to store the object
   * @param offset - position into byte array from where to store the object
   */
  public void store(byte[] to, int offset) {
    System.arraycopy(ticket, 0, to, offset, ticket.length);
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Security session ticket {0} stored in P4 response", new Object[] {Arrays.toString(ticket)});
    }
  }
  
  private byte[] getTicketForClusterNode(Object clusterNodeId) {
    byte[] ticket = (byte[]) clusterNodeTickets.get(clusterNodeId);
    
    if (ticket == null) {
      ticket = TicketGenerator.getEmptyTicket();
      clusterNodeTickets.put(clusterNodeId, ticket);
    }
    
    return ticket;
  }

  /**
   * Store object into byte array.
   * This method is used when the transfer is between different clusters.
   *
   * @param to        - the byte array where to store the object
   * @param offset    - position into byte array from where to store the object
   * @param clusterId - identifier of the requesting cluster.
   */
  public void store(Object clusterId, byte[] to, int offset) {
    byte[] tempTicket = getTicketForClusterNode(clusterId);
    System.arraycopy(tempTicket, 0, to, offset, tempTicket.length);
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Security session ticket {0} stored in P4 request", new Object[] {Arrays.toString(tempTicket)});
    }
  }

  /**
   * Load internal structure of the object from a byte array
   *
   * @param from   - byte array from where to load internal data
   * @param offset - the offset in byte array where is located the data
   */
  public void load(byte[] from, int offset) {
    byte[] newticket = TicketGenerator.getTicket(from, offset);

    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Security session ticket {0} extracted from P4 request", new Object[] {Arrays.toString(newticket)});
    }
    
    if (!equalArrays(ticket, newticket)) {
      SecuritySession newSession = TicketGenerator.getSecuritySession(newticket);

      if (newSession == null) {
        SecurityContext.setCurrentThreadAnonymous();
      } else {
        this.ticket = newticket;
        this.session = newSession;
      }
    }
  }

  static boolean equalArrays(byte[] data1, byte[] data2) {
    if (data1 == null && data2 == null) {
      return true;
    }

    if (data1 == null || data2 == null) {
      return false;
    }

    if (data1.length != data2.length) {
      return false;
    }

    for (int i = 0; i < data1.length; i++) {
      if (data1[i] != data2[i]) {
        return false;
      }
    }

    return true;
  }
  
  /**
   * Load internal structure of the object from a byte array.
   * This method is used when the transfer is between different clusters.
   *
   * @param from      - byte array from where to load internal data
   * @param offset    - the offset in byte array where is located the data
   * @param clusterId - identifier of the requesting cluster.
   */
  public void load(Object clusterId, byte[] from, int offset) {
    byte[] newticket = TicketGenerator.getTicket(from, offset);

    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Security session ticket {0} extracted from P4 response", new Object[] {Arrays.toString(newticket)});
    }
    
    if (!equalArrays(ticket, newticket)) {
      clusterNodeTickets.put(clusterId, newticket);
      
      if (!IN_SERVER) {
        SecuritySession newSession = TicketGenerator.getSecuritySession(newticket);
        
        if (anonymousSubjectHolder == null) {
          setAnonymousSubjectHolder(newSession.getSubjectHolder());
          TicketGenerator.setAnonymousPrincipal(newSession.getUserName());
        }

        if (newSession != null) {
          this.ticket = newticket;
          this.session = newSession;
        } else if ((this.session == null) || (!this.session.isAnonymous())) {
          this.ticket = null;
          this.session = new SecuritySession(loginAccessor, anonymousSubjectHolder.getSubject());
        }
      }
    }
  }

  public String getSecurityPolicyDomain() {
    return securityPolicyDomain;
  }
  
  public void setSecurityPolicyDomain(String policyDomain) {
    this.securityPolicyDomain = policyDomain;
  }

  
  //================================ deprecated stuff =================================
  
  /**
   * @deprecated
   */
  public long getExpirationPeriod() {
    return getSession().getExpirationPeriod();
  }

  /**
   * @deprecated
   */
  public boolean isAuthenticationPending() {
    return false;
  }
  
  /**
   * @deprecated
   */
  public void delayAuthentication(LoginContext loginContext) {
    //do nothing
  }
  
  /**
   * @deprecated
   */
  public void delayAuthentication(LoginContextReference loginContextReference) {
    //do nothing
  }
  
  /**
   * @deprecated
   */
  public void addListener(SecurityContextObjectListener listener, boolean removeOnEmpty) {
    //do nothing
  }
  
  /**
   * @deprecated
   */
  public void removeListener(SecurityContextObjectListener listener) {
    //do nothing
  }
  
  /**
   * @deprecated
   */
  public void runAs(Subject subject) {
    //do nothing
  }
  
  /**
   * @deprecated
   */
  public void useAsDefault() {
    //do nothing
  }

}
