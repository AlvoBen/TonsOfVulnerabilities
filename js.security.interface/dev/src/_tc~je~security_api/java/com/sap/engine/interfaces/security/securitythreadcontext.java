package com.sap.engine.interfaces.security;

import java.security.Principal;

import javax.security.auth.Subject;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.tc.logging.Location;


/**
 * Provides runtime information about the currently logged in user.
 *
 * @author Svetlana Stancheva
 * @version 6.40
 */
public class SecurityThreadContext {

  private static ThreadSystem threadSystem = null;
  private static com.sap.engine.interfaces.security.SecurityContext securityContext = null;
  private static Location LOCATION = Location.getLocation(SecurityThreadContext.class);

  public static final void initialize(ApplicationServiceContext serviceContext, com.sap.engine.interfaces.security.SecurityContext context) {
    if (threadSystem == null) {
      threadSystem = serviceContext.getCoreContext().getThreadSystem();
    }

    if (securityContext == null) {
      securityContext = context;
    }
  }

  /**
   * Gets the principal of the currently logged in user.
   *
   * @return  the principal of the currently logged in user.
   */
  public static Principal getCallerPrincipal() {
    final String METHOD = "getCallerPrincipal";

    com.sap.engine.lib.security.SecurityContext securityContext = SecurityThreadContext.getSecurityContext();

    if (securityContext != null) {
      return securityContext.getPrincipal();
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD, "The security context in the thread is null.");
      }
    }

    return null;
  }

  /**
   * Gets all the principals of the currently logged in user.
   *
   * @return  all the principals of the currently logged in user.
   */
  public static Principal[] getAllPrincipals() {
    final String METHOD = "getAllPrincipals";

    com.sap.engine.lib.security.SecurityContext securityContext = SecurityThreadContext.getSecurityContext();

    if (securityContext != null) {
      Subject subject = securityContext.getSubject();

      if (subject != null) {
        return (Principal[]) subject.getPrincipals().toArray(new Principal[0]);
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD, "The subject in the thread is null.");
        }
      }
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD, "The security context in the thread is null.");
      }
    }

    return null;
  }

  /**
   * Checks if the current thread is authenticated.
   *
   * @return  true if there is authenticated user in the current thread, false otherwise.
   */
  public static boolean isAuthenticated() {
    final String METHOD = "isAuthenticated";

    String anonymousUserName = securityContext.getUserStoreContext().getActiveUserStore().getUserContext().getAnonymousUserName();
    Principal principal = getCallerPrincipal();

    if (principal != null) {
      String localUser = principal.getName();

      if ((anonymousUserName != null) && (localUser != null)) {
        if (!anonymousUserName.equalsIgnoreCase(localUser)) {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD, "The current user is " + localUser);
          }

          return true;
        } else {
          if (LOCATION.beDebug()) {
            LOCATION.debugT(METHOD, "The current user is anonymous.");
          }
        }
      } else {
        if (LOCATION.beDebug()) {
          LOCATION.debugT(METHOD, "The anonymous user name or the current user name is null.");
        }
      }
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD, "The caller principal is null.");
      }
    }

    return false;
  }

  private static com.sap.engine.lib.security.SecurityContext getSecurityContext() {
    final String METHOD = "getSecurityContext";

    ThreadContext threadContext = threadSystem.getThreadContext();

    if (threadContext != null) {
      return (com.sap.engine.lib.security.SecurityContext) threadContext.getContextObject("security");
    } else {
      if (LOCATION.beDebug()) {
        LOCATION.debugT(METHOD, "The thread context is null.");
      }
    }

    return null;
  }

}
