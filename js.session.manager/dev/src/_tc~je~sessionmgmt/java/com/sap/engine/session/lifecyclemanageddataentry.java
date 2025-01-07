package com.sap.engine.session;

import com.sap.engine.session.data.LifecycleManagedData;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.runtime.http.HttpRuntimeSessionModel;
import com.sap.engine.session.usr.ClientContext;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;


class LifecycleManagedDataEntry implements Serializable {

  Set<DomainReference> dependantModules;

  LifecycleManagedData data;
  AppSession session;
  String dataName;
  private boolean isExpired;

  public LifecycleManagedDataEntry(AppSession session, String dataName, LifecycleManagedData data) {
    this.data = data;
    this.session = session;
    this.dataName = dataName;
  }

  public void setDependancy(DomainReference domainRef) {
    synchronized (this) {
    	if (dependantModules == null) {
    		dependantModules = new HashSet<DomainReference>();
    	}
    	this.dependantModules.add(domainRef);
    }
    domainRef.getEnclosedSessionDomain().addDependentObject(this);
  }

   public synchronized void removeDependancy(DomainReference domainRef) {
    if (dependantModules == null) {
      return;
    }
    this.dependantModules.remove(domainRef);
    domainRef.getEnclosedSessionDomain().removeDependentObject(this);
  }

  protected boolean isValid() {
    if (dependantModules != null) {
      for (DomainReference ref : dependantModules) {
        if ( ref.isDomainChanged()){
          return false;
        }
      }
    }
    return !data.isExpired();
  }

  public LifecycleManagedData getData() {
    return data;
  }

  void domainDestroied() {
    ClientContextImpl clientContextApplied = null;
    // if ClientContext is not assigned to the current Thread
    if (session.thisModel instanceof HttpRuntimeSessionModel) {
      ClientContext cc = ((HttpRuntimeSessionModel) session.thisModel).getClientContext();
      if (cc instanceof ClientContextImpl) {
        clientContextApplied = SessionExecContext.applyClientContext((ClientContextImpl) cc);
      }
    }
    try{
      session.removeLifecycleManagedAttribute(dataName);
      expire();
    }finally{
      SessionExecContext.applyClientContext(clientContextApplied);
    }
  }

  void clearDependants() {
    if (dependantModules != null) {
      for(DomainReference currentRef:dependantModules) {
        if (currentRef.getEnclosedSessionDomain() != null) {
          currentRef.getEnclosedSessionDomain().removeDependentObject(this);
        }
      }
      dependantModules = null;
    }      
  }
  
  void expire() { 
    if (isExpired){
      return;
    }else{
      isExpired = true;
    }
    clearDependants();     
    data.expire(session);
  }
}
