package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.conn.jco.ext.SessionReferenceProvider;
import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.scope.exception.NoSuchScopeTypeException;
import com.sap.engine.session.scope.Scope;
import com.sap.engine.session.scope.ScopeManagedResource;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SessionReferenceProviderImpl implements SessionReferenceProvider {

  private Location loc = Location.getLocation(SessionReferenceProviderImpl.class);

  public static final String JCO_SESSION_REF_KEY = "JCO_SESSION_REF_KEY";

  /**
   * This method is invoked by JCo in order to find out, in which session it is running. This
   * is necessary in case of a JCoContext has been started, so that from now on the same
   * physical connection should be used for keeping alive the user context in the application
   * server ABAP.
   *
   * @return a JCoSessionReference that identifies the current session in which the method is
   *         invoked
   * @deprecated getCurrentSessionReference(StringscopeType) will be used instead
   */
  public JCoSessionReference getCurrentSessionReference() {
    return getReferenceByScope(Scope.CLIENT_CONTEXT_SCOPE_NAME);
  }

  public boolean isSessionAlive(String sessionID) {
    throw new UnsupportedOperationException();
  }

  public void jcoServerSessionContinued(String string) throws SessionException {
    //TODO
  }

  public void jcoServerSessionPassivated(String string) throws SessionException {
    //TODO
  }

  public void jcoServerSessionFinished(String string) {
    //TODO
  }

  public JCoSessionReference jcoServerSessionStarted() throws SessionException {
    return null;  //TODO
  }

  /**
   * This method is invoked by JCo in order to find out, in which session it is running. This
   * is necessary in case of a JCoContext has been started, so that from now on the same
   * physical connection should be used for keeping alive the user context in the application
   * server ABAP. If the scope type is <code>null</code> or the empty String, the default scope
   * should be used. Runtime environments that do not provide a scope management within sessions,
   * can simply ignore the scope type parameter.
   *
   * @param scopeType defines the scope for which the session reference is needed
   * @return a JCoSessionReference that identifies the current session in which the method is
   *         invoked
   */

  public JCoSessionReference getCurrentSessionReference(String scopeType) {
    if(scopeType == null){
      return getCurrentSessionReference(Scope.CLIENT_CONTEXT_SCOPE_NAME);
    }
    return getReferenceByScope(scopeType);
  }

  private JCoSessionReference getReferenceByScope(String scopeType) {
    if (loc.bePath()) {
      loc.entering("getReferenceByScope(ScopeType<" + scopeType + ">)");
    }

    JCoSessionReferenceImpl ref = null;
    UserContext userContext = UserContext.getCurrentUserContext();

    /* log data */
    if (loc.bePath()) {
      String msg = "Current UserContext:" + userContext;
      loc.pathT(msg);
      if (loc.beDebug()) {
        loc.traceThrowableT(Severity.PATH, msg, new Exception());
      }
    }

    if (userContext != null) {
      Scope uc_scope = null;
      try {
        uc_scope = Scope.getContextScope(scopeType);
        ScopeManagedResource[] res = ScopeManagedResource.getResources(uc_scope, JCoScopeManagedResource.class);

        /* log data */
        if (loc.bePath()) {
          String msg = "UserContext Scope :" + uc_scope;
          loc.pathT(msg);
          if (res != null) {
            msg = "";
            for (int i = 0; i < res.length; i++) {
              msg += "JCO Resource[" + i + "]:" + res[i] + "\r\n";
            }
          } else {
            msg = "There are not configured resources for this scope.";
          }
          loc.pathT(msg);
        }

        if (res == null) {
          /* there is not registered managed resource -> create and register */
          JCoScopeManagedResource jcoRes = new JCoScopeManagedResource(userContext.toString());
          uc_scope.addScopeManagedResource(jcoRes);

          /* log data */
          if (loc.bePath()) {
            String msg = "Created JCoResource:" + jcoRes;
            loc.pathT(msg);
            msg = "Created JCO Ref:" + jcoRes.getJCoSessionReference();
            loc.pathT(msg);
          }
          return jcoRes.getJCoSessionReference();
        }

        /* get first element and restore the ref if it is already persisted */
        ref = ((JCoSessionReferenceImpl) ((JCoScopeManagedResource) res[0]).getJCoSessionReference());
        if (ref.isPersisted()) {
          ref.restoreConnections();

          /* log data */
          if (loc.bePath()) {
            loc.pathT("Restore JCo Connection :" + ref);
          }
        }
        return ref;

      } catch (NoSuchScopeTypeException e) {
        loc.warningT("There is not registered User Context Scope Type");  //todo ? severity
        loc.throwing(e);
        return null;
      }

//          userContext.addAttribute(JCO_SESSION_REF_KEY, ref);

    } else {
      loc.warningT("The Current UserContext is null.");
    }

    if (loc.bePath()) {
      loc.entering("getReferenceByScope(ScopeType<" + scopeType + ">)");
    }

    return ref;
  }
}
