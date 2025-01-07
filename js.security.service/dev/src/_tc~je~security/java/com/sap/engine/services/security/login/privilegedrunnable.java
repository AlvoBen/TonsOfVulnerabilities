package com.sap.engine.services.security.login;

import javax.security.auth.Subject;
import java.security.PrivilegedAction;

public class PrivilegedRunnable implements Runnable {

  private static final String THREAD_NAME = "security:Do-As-Administrator:";

  private Runnable runnable = null;
  private Subject  adminSubject = null;
  
  public PrivilegedRunnable(Runnable runnable, Subject adminSubject) {
    this.runnable = runnable;
    this.adminSubject = adminSubject;
  }
  
  public void run() {
    Thread thread = Thread.currentThread();
    String threadName = thread.getName();

    try {
      thread.setName(THREAD_NAME + threadName);
      Subject.doAs(adminSubject, new DoAsAdministrator(runnable));
    } finally {
      thread.setName(threadName);
    }
  }
  
  private class DoAsAdministrator implements PrivilegedAction {
    private Runnable runnable = null;
    
    public DoAsAdministrator(Runnable runnable) {
      this.runnable = runnable;
    } 
    
    public Object run() {
      runnable.run();
      return null;
    }
  }
}