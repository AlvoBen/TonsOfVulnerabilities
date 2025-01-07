package com.sap.engine.session.usr;

import javax.security.auth.Subject;
import java.security.Principal;
import java.io.Serializable;

public interface SubjectHolder extends Serializable {

  public Subject getSubject();

  public Principal getPrincipal();

}
