package com.sap.security.core.codesecurity.policy;

import java.io.File;
import java.io.PrintWriter;

public class KeyStoreEntry
{
    // keystore entry: url and type
    public String mm_keyStoreUrlString;
    public String mm_keyStoreType;

    private boolean mm_replaceProps;

    public KeyStoreEntry( boolean replaceProps)
    {
        mm_keyStoreUrlString = null;
        mm_keyStoreType = null;
        mm_replaceProps = replaceProps;
    }

    public boolean isEmpty()
    {
        return mm_keyStoreUrlString == null && mm_keyStoreType == null;
    }

    public void print(PrintWriter printwriter)
    {
        if( isEmpty() )
            return;

        printwriter.print("keystore \"");
        printwriter.print(getKeyStoreUrl());
        printwriter.print('"');
        if(mm_keyStoreType != null && mm_keyStoreType.length() > 0)
            printwriter.print(", \"" + mm_keyStoreType + "\"");
        printwriter.println(";");
        printwriter.println();
    }

    /**
     * need to call this to have system properties replaced
     */
    public String getKeyStoreUrl()
    {
        try
        {
            if(!isEmpty())
                return PropertyReplacer.replace(mm_keyStoreUrlString).replace(File.separatorChar, '/');
        }
        catch( PropertyReplacer.PropertyReplacerException re )
        {
            return null;
        }
        return null;
    }
}