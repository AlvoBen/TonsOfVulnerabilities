package com.sap.security.core.codesecurity.policy;

import java.security.GeneralSecurityException;

public class PortalPolicyException extends GeneralSecurityException
{
    public PortalPolicyException()
    {
        super();
    }

    public PortalPolicyException(String message)
    {
        super(message);
    }
}