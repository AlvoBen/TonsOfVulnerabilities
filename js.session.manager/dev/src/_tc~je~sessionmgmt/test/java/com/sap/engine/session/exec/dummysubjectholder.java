package com.sap.engine.session.exec;

import java.security.Principal;

import javax.security.auth.Subject;

import com.sap.engine.session.usr.SubjectHolder;

public class DummySubjectHolder implements SubjectHolder {

  Principal prinicpal;
  
  public DummySubjectHolder() {
    prinicpal = new DummyPrincipal();
  }
  
  public Principal getPrincipal() {
    return prinicpal;
  }

  public Subject getSubject() {
    return null;
  }
  
}
