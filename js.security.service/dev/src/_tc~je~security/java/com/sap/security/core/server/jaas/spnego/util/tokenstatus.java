package com.sap.security.core.server.jaas.spnego.util;

public class TokenStatus {
  public String userName;
  public String kerberosPrincipalName;
  public boolean isAuthenticated;

  public TokenStatus(String userName, String kerberosPrincipalName, boolean isAuthenticated) {
    this.userName = userName;
    this.kerberosPrincipalName = kerberosPrincipalName;
    this.isAuthenticated = isAuthenticated;
  }
}
