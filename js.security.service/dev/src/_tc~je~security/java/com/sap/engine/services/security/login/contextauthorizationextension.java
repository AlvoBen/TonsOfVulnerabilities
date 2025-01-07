package com.sap.engine.services.security.login;

/**
 * @deprecated To be removed
 */
public class ContextAuthorizationExtension {

  private static ContextAuthorizationExtension emptyExtension = new ContextAuthorizationExtension();

  private AuthorizationEntry entry = new AuthorizationEntry();

  private ContextAuthorizationExtension() {
  }

  public AuthorizationEntry getAuthorizationEntry(String policyConfiguration) {
    return entry;
  }

  public static ContextAuthorizationExtension getEmptyAuthorizationExtension() {
    return emptyExtension;
  }

}