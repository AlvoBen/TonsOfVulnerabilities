package com.sap.security.core.codesecurity.policy;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

public class ProtectionDomainEntry
{
    public String mm_signedBy;
    public String mm_codeBase;
    public Vector mm_permissionEntries;

    public ProtectionDomainEntry()
    {
        mm_permissionEntries = new Vector();
    }

    public ProtectionDomainEntry(String signedBy, String codeBase)
    {
        this();
        mm_codeBase = codeBase;
        mm_signedBy = signedBy;
    }

    public void add(PermissionEntry permissionentry)
    {
        mm_permissionEntries.addElement(permissionentry);
    }

    public boolean remove(PermissionEntry permissionentry)
    {
        return mm_permissionEntries.removeElement(permissionentry);
    }

    public boolean contains(PermissionEntry permissionentry)
    {
        return mm_permissionEntries.contains(permissionentry);
    }

    public Enumeration permissionElements()
    {
        return mm_permissionEntries.elements();
    }

    public void print(PrintWriter printwriter)
    {
        printwriter.print("grant");
        if(mm_signedBy != null)
        {
            printwriter.print(" signedBy \"");
            printwriter.print(mm_signedBy);
            printwriter.print('"');
            if(mm_codeBase != null)
                printwriter.print(", ");
        }
        if(mm_codeBase != null)
        {
            printwriter.print(" codeBase \"");
            printwriter.print(mm_codeBase);
            printwriter.print('"');
        }
        printwriter.println();
        printwriter.println("{");
        PermissionEntry permissionentry;
        for(Enumeration enumeration = permissionElements(); enumeration.hasMoreElements(); permissionentry.print(printwriter))
        {
            permissionentry = (PermissionEntry)enumeration.nextElement();
            printwriter.write("  ");
        }

        printwriter.println("};");
    }
}
