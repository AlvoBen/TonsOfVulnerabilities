package com.sap.security.core.codesecurity.policy;

public class PolicyParserException extends PersistenceAdapterException
{

    public PolicyParserException(String message)
    {
        super(message);
    }

    public PolicyParserException(int lineno, String message)
    {
        super("line " + lineno + ": " + message);
    }

    public PolicyParserException(int lineno, String expected, String found)
    {
        super("line " + lineno + ": expected '" + expected + "', found '" + found + "'");
    }
}
