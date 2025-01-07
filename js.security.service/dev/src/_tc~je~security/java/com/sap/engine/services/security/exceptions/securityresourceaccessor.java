package com.sap.engine.services.security.exceptions;

import com.sap.localization.ResourceAccessor;

public class SecurityResourceAccessor extends ResourceAccessor {

  private static final long serialVersionUID = -4183425546803847845L;

  private static ResourceAccessor defaultAccessor = new SecurityResourceAccessor("com.sap.engine.services.security.exceptions.LogonMessages");

  private SecurityResourceAccessor(String resourceBundleName) {
    super(resourceBundleName);
  }

  public static final ResourceAccessor getResourceAccessor() {
    return defaultAccessor;
  }

}