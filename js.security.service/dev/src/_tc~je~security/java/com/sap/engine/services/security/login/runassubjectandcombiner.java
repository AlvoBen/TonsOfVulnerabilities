package com.sap.engine.services.security.login;

import javax.security.auth.Subject;
import java.security.DomainCombiner;

class RunAsSubjectAndCombiner {
  private Subject         subject  = null;
  private DomainCombiner  combiner = null;
  private SecuritySession session  = null;

  public RunAsSubjectAndCombiner(Subject subject, DomainCombiner combiner, SecuritySession session) {
    this.subject  = subject;
    this.combiner = combiner;
    this.session = session;
  }

  public Subject getSubject() {
    return subject;
  }

  public DomainCombiner getCombiner() {
    return combiner;
  }

  public SecuritySession getSession() {
    return session;
  }
}
