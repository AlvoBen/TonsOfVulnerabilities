package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.engine.session.scope.ScopeManagedResource;
import com.sap.engine.session.scope.Scope;
import com.sap.conn.jco.ext.JCoSessionReference;

import java.io.IOException;

public class JCoScopeManagedResource extends ScopeManagedResource {

  private JCoSessionReferenceImpl ref = null;

  public JCoScopeManagedResource(String sessionId){
    super();
    ref = new JCoSessionReferenceImpl(sessionId);
  }

  public void scopeTerminated(Scope scope){} //todo ? impl

  protected JCoSessionReference getJCoSessionReference(){
    return this.ref;
  }

  public String toString(){
    return "JCoScopeResource<" + this.hashCode() + "> ref : " + ref;
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    if (ref.isActive() && !ref.isPersisted()) {
      ref.passivateConnections();
    }
    out.writeObject(ref);
  }

  private void readObject(java.io.ObjectInputStream in)  throws IOException, ClassNotFoundException{
    this.ref = (JCoSessionReferenceImpl)in.readObject();
  }



}
