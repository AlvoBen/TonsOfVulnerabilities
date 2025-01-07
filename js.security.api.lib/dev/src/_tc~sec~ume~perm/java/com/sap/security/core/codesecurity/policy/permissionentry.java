package com.sap.security.core.codesecurity.policy;

import java.io.PrintWriter;

public class PermissionEntry
{
    public String mm_permissiontype;    // e.g. "java.util.PropertyPermission"
    public String mm_name;              // e.g. "portal.test"
    public String mm_action;            // e.g. "read"

    public PermissionEntry(String permissiontype, String name, String action)
    {
        mm_permissiontype = permissiontype;
        mm_name = name;
        mm_action = action;
    }

    public PermissionEntry()
    {
        mm_permissiontype = null;
        mm_name = null;
        mm_action = null;
    }

    public int hashCode()
    {
        // build a hashcode from type, name and action
        int i = mm_permissiontype.hashCode();
        if(mm_name != null)
            i ^= mm_name.hashCode();
        if(mm_action != null)
            i ^= mm_action.hashCode();
        return i;
    }

    public boolean equals(Object obj)
    {
        // obj is me
        if(obj == this)
            return true;

        // different class
        if(!(obj instanceof PermissionEntry))
            return false;

        PermissionEntry permissionentry = (PermissionEntry)obj;

        // check the type
        if(mm_permissiontype == null)
        {
            if(permissionentry.mm_permissiontype != null)
                return false;
        }
        else
        {
            if(!mm_permissiontype.equals(permissionentry.mm_permissiontype))
                return false;
        }

        // type is equal, check the name
        if(mm_name == null)
        {
            if(permissionentry.mm_name != null)
                return false;
        }
        else
        {
            if(!mm_name.equals(permissionentry.mm_name))
                return false;
        }

        // type and name are equal, check action
        if(mm_action == null)
        {
            if(permissionentry.mm_action != null)
                return false;
        }
        else
        {
            if(!mm_action.equals(permissionentry.mm_action))
                return false;
        }

        return true;
    }

    public void print(PrintWriter printwriter)
    {
        printwriter.print("permission ");
        printwriter.print(mm_permissiontype);
        if(mm_name != null)
        {
            printwriter.print(" \"");
            printwriter.print(mm_name);
            printwriter.print('"');
        }
        if(mm_action != null)
        {
            printwriter.print(", \"");
            printwriter.print(mm_action);
            printwriter.print('"');
        }
        printwriter.println(";");
    }



}
