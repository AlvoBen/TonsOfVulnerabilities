package com.sap.engine.session.scope;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.trace.ThrTrace;

import com.sap.engine.core.Names;
import java.io.Serializable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import java.util.Arrays;

public class ScopeObserver implements Serializable {

  private static final long serialVersionUID = 8147799757308741380L;

  private String scopeType = null;

  public static Location loc = Location.getLocation(ScopeObserver.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private transient Scope currentScope = null;

    // true if a resource is added or removed
  private boolean modified;

  /* keeps registered Resources */
  private Set<ScopeManagedResource> resources = Collections.synchronizedSet(new HashSet<ScopeManagedResource>());

  protected ScopeObserver(Scope scope) {
    this.currentScope = scope;
    this.scopeType = scope.getScopeType();
    scope.setObserver(this);
  }

  /**
   * Check if the scope is modified
   * @return TRUE if a resource is added or removed, FALSE if not
   */
  public boolean isModified() {
    if (loc.beDebug()) {
      loc.pathT("is MODIFIED called for scope observer: " + this);
      loc.traceThrowableT(Severity.DEBUG, "thread stack", new ThrTrace());
    }
    return modified;
  }

  private void modify() {
    if (loc.beDebug()) {
      loc.pathT("MODIFY scope observer: " + this);
      loc.traceThrowableT(Severity.DEBUG, "thread stack", new ThrTrace());
    }
    modified = true;
  }

  /**
   * Marks the scope as not modified 
   */
  public void markNotModified() {
    modified = false;
  }

  /**
   * Return the Scope instance which is kept by this instance of the observer
   * @return current Scope instance
   */
  public Scope getScope() {
    return currentScope;
  }

  protected String scopeType() {
    return this.scopeType;
  }

  /**
   * when the observer is deserialized
   * @param id scopeId
   */
  private void init(String id) {
    this.currentScope = new Scope(id, this);
    if (loc.bePath()) {
      if (this.resources != null && this.resources.size() > 0) {
        loc.pathT("Observer's init with resources:" + resources);
      } else {
        loc.pathT("Observer's init with Empty Resources.");
      }
    }
  }

  /**
   * Terminated the scope and notify all Scope Managed Resources bound to the scope.
   */
  public void terminateScope() {
    if (isScopeAvailable()) {
      clearResources();
      removeRefs();
    }
    modify();
  }

  private void removeRefs() {
    this.currentScope.terminate();
    this.currentScope = null;
    modify();
  }

  private boolean isScopeAvailable() {
    if (this.currentScope != null) {
      this.currentScope.terminate();
      return true;
    } else {
      return false;
    }
  }

  protected void addScopeManagedResource(ScopeManagedResource res) {
    if (loc.bePath()) {
      loc.pathT("add ScopeManagedResource:" + res + " For:" + this);
    }
    this.resources.add(res);
    modify();
  }

  public void removeScopeMangedResource(ScopeManagedResource res) {
    if (loc.bePath()) {
      loc.pathT("remove ScopeMangedResource:" + res + " For:" + this);
    }
    this.resources.remove(res);
    modify();
  }

  /**
   * remove all registered resources in the resource HashSet
   */
  protected void clearResources() {
    ScopeManagedResource[] ar = this.resources.toArray(new ScopeManagedResource[0]);
    if (ar != null && ar.length > 0) {
      for (ScopeManagedResource scopeResource : ar) {
        try {
          scopeResource.scopeTerminated(this.currentScope);
        } catch (Exception e) {
          if (loc.beWarning()) {
            loc.throwing(e);
            loc.warningT("Exception in clearResources():" + e);
          }
        }
        this.resources.remove(scopeResource);
      }
    }
    this.resources = new HashSet<ScopeManagedResource>();
    modify();
  }

  public String toString() {
    String msg = "";
    msg += "ScopeObserver<" + this.hashCode() + "> keeps ref to " + this.currentScope;
    if (resources != null) {
      msg += " Resources:" + resources;
    }
    return msg;
  }

  protected Set getScopeResources() {
    ScopeManagedResource[] res = this.resources.toArray(new ScopeManagedResource[0]);
    if (res != null && res.length > 0) {
      HashSet<ScopeManagedResource> s = new HashSet<ScopeManagedResource>();
      s.addAll(Arrays.asList(res));
      return s;
    }
    return null;
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init(UserContext.getCurrentUserContext().toString());
  }

}
