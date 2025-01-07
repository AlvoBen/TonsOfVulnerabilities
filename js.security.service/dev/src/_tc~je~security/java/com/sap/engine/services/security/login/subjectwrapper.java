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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.security.Util;
import com.sap.engine.session.usr.SubjectHolder;
import com.sap.security.api.logon.NonDuplicable;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 *  A wrapper of javax.security.auth.Subject. The wrapper is used to encapsulate the subject which
 * class may not be loaded if not present in the system class loader. Loading it with a different
 * class loader does not help.
 *
 * @see javax.security.auth.Subject
 *
 * @author  Stephan Zlatarev
 * @version 7.10
 */
public class SubjectWrapper implements SubjectHolder {

  private static final long serialVersionUID = -2938907067013840517L;
  public final static String SERIALIZATION_LOCATION_NAME = SecurityContext.SESSION_LOCATION_NAME + ".SubjectWrapper_serialization";
  public final static String READ_OBJECT_LOCATION_NAME = SERIALIZATION_LOCATION_NAME + ".readObject";
  public final static String WRITE_OBJECT_LOCATION_NAME = SERIALIZATION_LOCATION_NAME + ".writeObject";
  
  private final static Location TRACER = SecurityContext.TRACER;
  private final static Location TRACER_READ_OBJECT = Location.getLocation(READ_OBJECT_LOCATION_NAME);
  private final static Location TRACER_WRITE_OBJECT = Location.getLocation(WRITE_OBJECT_LOCATION_NAME);
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  private final static String LS = System.getProperty("line.separator");
    
  private static Object anonymousLock = new Object();

  protected static SubjectWrapper ANONYMOUS_SUBJECT = null;

  // Serialization is handled by read/writeObject methods
  private transient boolean isAnonymous = false;
  private transient Object subject = null;
  private transient Principal principal = null;

  /**
   * Constructs a wrapper of a subject
   */
  public SubjectWrapper() {
    if (ANONYMOUS_SUBJECT != null) {
      subject = ANONYMOUS_SUBJECT.getSubject();
    } else {
      subject = new Subject();
    }
    isAnonymous = true;
  }

  public SubjectWrapper(Principal principal) {
    this.principal = principal;
    this.subject = new javax.security.auth.Subject();

    ((javax.security.auth.Subject) subject).getPrincipals().add(this.principal);
    isAnonymous = false;
  }

  /**
   * Constructs a wrapper of a subject
   */
  public SubjectWrapper(javax.security.auth.Subject subject) {
    this.subject = subject;
    isAnonymous = false;
  }

  public SubjectWrapper(javax.security.auth.Subject subject, Principal principal) {
    this.subject = subject;
    this.principal = principal;
    isAnonymous = false;
  }

  /**
   * @return true if this is the anonymous user of the server
   */
  public boolean isAnonymous() {
    return isAnonymous;
  }

  /**
   *  Retrieves the subject from the wrapper.
   *
   * @return a javax.security.auth.Subject instance
   */
  public javax.security.auth.Subject getSubject() {
    return (javax.security.auth.Subject) subject;
  }

  /**
   *  Retrieves the first principal of the subject's principal set.
   *
   * @return  a principal of the subject.
   */
  public Principal getPrincipal() {
    if (principal != null) {
      return principal;
    } else if (subject != null) {
      javax.security.auth.Subject inner = getSubject();

      try {
        Set s = inner.getPrincipals(com.sap.engine.lib.security.Principal.class);
        principal = (Principal) s.iterator().next();
      } catch (Exception e) {
        principal = null;
      }

      if (principal == null) {
        try {
          Set s = inner.getPrincipals();
          principal = (Principal) s.iterator().next();
        } catch (Exception e) {
          principal = null;
        }
      }
    }
    
    if (principal != null) {
      return principal;
    }
    
    if (ANONYMOUS_SUBJECT != null) {
      return ANONYMOUS_SUBJECT.getPrincipal();
    }
    
    return null; 
  }
  
  protected void merge(SubjectWrapper newSubjectWrapper) {
    if (newSubjectWrapper == null) {
      return;
    }
    
    if (isAnonymous()) {
      throw new IllegalStateException("Cannot merge authenticated subject wrapper into anonymous subject wrapper!");
    }

    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Merging new subject wrapper into the current {0}", new Object[] {this});
    }
    
    Principal newPrincipal = newSubjectWrapper.getPrincipal();
    
    if (newPrincipal != null) {
      this.principal = newPrincipal;
    }
    
    Subject newSubject = newSubjectWrapper.getSubject();
    
    if (newSubject == null) {
      return;
    }
    
    Subject jaasSubject = getSubject();
    addOrReplace(jaasSubject.getPrincipals(), newSubject.getPrincipals());
    addOrReplace(jaasSubject.getPrivateCredentials(), newSubject.getPrivateCredentials());
    addOrReplace(jaasSubject.getPublicCredentials(), newSubject.getPublicCredentials());
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Merged subject wrapper is {0}", new Object[] {this});
    }
  }
  
  private <T> void addOrReplace(Set<T> oldValues, Set<T> newValues) {
    for (T value: newValues) {
      if (value instanceof NonDuplicable) {
        removeDuplicables(oldValues, value);
      }
    }
    
    oldValues.addAll(newValues);
  }
  
  private <T> void removeDuplicables(Set<T> oldValues, T newValue) {
    Class<? extends Object> klass = newValue.getClass();
    
    for (Iterator<T> iter = oldValues.iterator(); iter.hasNext();) {
      if (klass.isInstance(iter.next())) {
        iter.remove();
      }
    }
  }
  
  public SubjectWrapper createCopy() {
    SubjectWrapper result = null;
    if (isAnonymous()) {
      result = new SubjectWrapper();
    } else {
      result = new SubjectWrapper(new Subject(), this.principal);
      ((javax.security.auth.Subject) result.subject).getPrincipals().addAll(((javax.security.auth.Subject) this.subject).getPrincipals());
      ((javax.security.auth.Subject) result.subject).getPublicCredentials().addAll(((javax.security.auth.Subject) this.subject).getPublicCredentials());
      ((javax.security.auth.Subject) result.subject).getPrivateCredentials().addAll(((javax.security.auth.Subject) this.subject).getPrivateCredentials());
    }
    
    if (IN_SERVER && TRACER.beDebug()) {
      TRACER.debugT("Copy {0} of {1} created", new Object[] {result, this});
    }
    
    return result;
  }

  /**
  *  Custom serialization is needed for the Subject instance.
  */
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    byte[] data = (byte[]) stream.readObject();
    SubjectWrapper reconstructed = Util.array2subject(data, 0, data.length);
    
    this.isAnonymous = reconstructed.isAnonymous;
    this.subject = reconstructed.subject;
    this.principal = reconstructed.principal;

    if (IN_SERVER && TRACER_READ_OBJECT.beDebug()) {
      TRACER_READ_OBJECT.debugT("{0} deserialized from database", new Object[] {this});
    }
  }

  /**
  *  Custom serialization is needed for the Subject instance.
  */
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(Util.subject2array(this));

    if (IN_SERVER && TRACER_WRITE_OBJECT.beDebug()) {
      TRACER_WRITE_OBJECT.debugT("{0} serialized to database", new Object[] {this});
    }
  }

  static void setAnonymousPrincipal(String anonymous) {
    if (ANONYMOUS_SUBJECT == null) {
      synchronized (anonymousLock) {
        if (ANONYMOUS_SUBJECT == null) {
          com.sap.engine.lib.security.Principal principal = new com.sap.engine.lib.security.Principal(anonymous);
          ANONYMOUS_SUBJECT = new SubjectWrapper();
          ANONYMOUS_SUBJECT.getSubject().getPrincipals().add(principal);
        }
      }
    }
  }

  static void setAnonymousSubject(SubjectWrapper anonymous) {
    ANONYMOUS_SUBJECT = anonymous;
    ANONYMOUS_SUBJECT.isAnonymous = true;
  }
  
  public String toString() {
    StringBuilder res = new StringBuilder();
    res.append("[Subject Wrapper: ").append(LS);
    res.append("Main Principal: ").append((principal == null ? "null" : principal.toString())).append(LS);
    res.append((subject == null ? "Subject: null" : subject.toString()));
    res.append("]");
    
    return res.toString();
  }
}
