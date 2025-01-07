package com.sap.security.core.codesecurity.policy;

import java.security.GeneralSecurityException;

public class PersistenceAdapterException extends GeneralSecurityException
{
    public PersistenceAdapterException() {
        super();
    }

    public PersistenceAdapterException(String msg) {
        super(msg);
    }
}