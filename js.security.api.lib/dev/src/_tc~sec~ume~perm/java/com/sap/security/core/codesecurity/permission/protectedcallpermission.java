package com.sap.security.core.codesecurity.permission;

import java.security.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;


/** <b>ProtectedCallPermission</b>
 *  <a name="khead"/>Permission to protect calls from unauthorized callers.
 *  <p>
 *  The semantics are as follows: We have two constructors. The
 *  <i>class_name</i> argument denotes the class to be protected
 *  from calls, the <i>method_names</i> denotes the method name or
 *  a comma-separated list of methods (different signatures are
 *  not taken into account).<p>
 *  Examples:
 *  <pre>
 *  Permission p = new ProtectedCallPermission ("com.sap.security.SecretService");
 *  Permission q = new ProtectedCallPermission ("com.sap.security.SecretService", "*");
 *  Permission r = new ProtectedCallPermission ("com.sap.security.SecretService", "call1, call2");
 *  </pre>
 *  Permission <i>p</i> represents the permission to call every method of
 *  class com.sap.security.SecretService. <i>p</i> and <i>q</i> are equivalent.
 *  <i>r</i> represents the permission to call methods <i>call1</i> and <i>call2</i> of
 *  this class.
 *  The <i>implies</i> method checks whether the <i>class_name</i> is the same (i.e. whether
 *  the two permissions protect the same class) and whether <i>method_names</i>
 *  includes all method names of the argument permission object.
 *
 */
public class ProtectedCallPermission extends BasicPermission
{
    // wildcard
    private static final String WILDCARD = "*";

    private HashSet actions = new HashSet ();

    /**
     *  Constructor. Is the same as <i>new ProtectedCallPermission (class_name, "*");</i>
     *  @param class_name Class name. Best is to provide an argument of the form
     *         object.getClass().getName().
     */
    public ProtectedCallPermission (String class_name)
    {
        this (class_name, WILDCARD);
    }

    /** Constructor.
     *  @param class_name Class name. Best is to provide an argument of the form
     *         object.getClass().getName().
     *  @param method names. Can be "*" (stands for permission to call all objects)
     *                       or a comma-separated list of methods.
     */
    public ProtectedCallPermission (String class_name, String method_names)
    {
        super (class_name.trim(), null /* is ignored anyway... */);

        String mn = class_name.trim().equalsIgnoreCase(WILDCARD)?WILDCARD:method_names.trim();
        if( mn.equalsIgnoreCase(WILDCARD) )
        {
            this.actions.add(WILDCARD);
            return;
        }

        StringTokenizer st = new StringTokenizer (method_names, ",");
        while (st.hasMoreElements ()) {
            this.actions.add (st.nextToken().trim());
        }
    }

    public String getActions()
    {
        if( this.actions.isEmpty() ) return "";

        StringBuffer buf = new StringBuffer();
        Iterator iter = this.actions.iterator();
        while (iter.hasNext())
        {
            buf.append((String) iter.next());
            buf.append(',');
        }

        return buf.substring(0, buf.length() - 1 );
    }


    /**
     *  Implies method. For details about the semantics see <a href="#khead">here</a>
     */
    public boolean implies (Permission aPermission)
    {
        ProtectedCallPermission permission;
        if (false==aPermission instanceof ProtectedCallPermission)
            return false;

        permission = (ProtectedCallPermission) aPermission;

        //wildcard in classname
        String myName = this.getName();
        if( !myName.equals(WILDCARD) && !myName.equals(permission.getName()) )
            return false;

        //wildcard in method names
        if (WILDCARD.equals(this.getActions()))
            return true;

        // Now check if all actions in permission
        // are also in this
        Iterator ii = permission.actions.iterator ();
        while (ii.hasNext ()) {
            // if permission contains and action that
            // we don't, then this doesn't include
            // permission
            if (false==this.actions.contains (ii.next()))
                return false;
        }

        return true;
    }


    /**
     *  returns a PermissionCollection for ProtectedCallPermission objects.
     */
    public PermissionCollection newPermissionCollection ()
    {
        return new ProtectedCallPermissionCollection();
    }

    /**
     *  Returns a string describing this ProtectedCallPermission.
     */
    public String toString()
    {
        return '(' + this.getClass().getName() + ' ' + this.getName() + ' ' + this.getActions () + ')';
    }
}

class ProtectedCallPermissionCollection extends PermissionCollection
{
    // permissions of this collection
    private Hashtable table = new Hashtable (10);
    // wildcard
    private static final String WILDCARD = "*";

    // name of the service which needs extra protection
    /**
     * @todo change to ICryptoService
     */
    private static final String CRYPTOSERVICENAME = "com.sapportals.portal.prt.service.security.IDummyService";

    // stop checking stack a this class
    private static final String DISPATCHER = "com.sapportals.portal.prt.dispatcher.Dispatcher$doService";
    // allowed package names on callstack
    private static HashSet allowedStackEntries = new HashSet(10);

    static
    {
        // initialize the allowed classes set
        /**
         * @todo enter all real classes on stack
         */

        allowedStackEntries.add("com.sap.security.core.codesecurity.permission.ProtectedCallPermissionCollection");
        allowedStackEntries.add("com.sap.security.core.codesecurity.permission.ProtectedCallPermissionCollection$1$StackCheckSecurityManager");
        allowedStackEntries.add("com.sapportals.portal.prt.service.security.SecurityService");
        allowedStackEntries.add("com.sap.test.SAPComponent");
        allowedStackEntries.add("com.sapportals.portal.prt.component.AbstractPortalComponent");
        allowedStackEntries.add("com.sapportals.portal.prt.core.PortalRequestManager");
        allowedStackEntries.add("com.sapportals.portal.prt.component.PortalComponentResponse");
        allowedStackEntries.add("com.sapportals.portal.prt.pom.PortalNode");
        allowedStackEntries.add("com.sapportals.portal.prt.service.prtconnection.ServletConnection");
    }

    public void add (Permission permission)
    {
        if (false==permission instanceof ProtectedCallPermission)
            return;

        Permission pp = (Permission) this.table.get (permission.getName());
        if (pp==null)
        {
            this.table.put (permission.getName(), permission);
        }
        else
        {
            if (permission.implies (pp)) {
                this.table.put (permission.getName(), permission);
            }
            // We can omit the case that pp.implies (permission),
            // because in this case nothing needs to be done.
            else if (!pp.implies (permission)) {
                String actions1 = pp.getActions ();
                String actions2 = permission.getActions ();

                StringTokenizer st1 = new StringTokenizer (actions1, ",");
                StringTokenizer st2 = new StringTokenizer (actions2, ",");

                HashSet v = new HashSet (10);

                while (st1.hasMoreElements ()) {
                    v.add (st1.nextToken ());
                }
                while (st2.hasMoreElements ()) {
                    v.add (st2.nextToken ());
                }

                // Now we reconstruct the actions
                // in a comma-separated list
                StringBuffer newAction = new StringBuffer ("");
                Iterator     it        = v.iterator();
                while (it.hasNext()) {
                    newAction.append (it.next ());
                    newAction.append (',');
                }
                // remove last komma
                newAction.deleteCharAt (newAction.length()-1);

                permission = new ProtectedCallPermission (permission.getName (), newAction.toString ());
                this.table.put (permission.getName(), permission);
//                System.out.println("__adding permission " + permission);
            }
        }
    }

    /**
     * includes a callstack check for the cryptoservice
     */
    public boolean implies (Permission permission)
    {
        if (false==permission instanceof ProtectedCallPermission)
            return false;

        String permName = permission.getName();
        // do we contain a permission with that name?
        Permission p = (Permission) this.table.get (permName);
        // if yes, do we imply it?
        boolean implied = (p==null? false: p.implies(permission));
        // do we contain a wildcard thing?
        if( !implied )
        {
            p = (Permission) this.table.get (WILDCARD);
            implied = (p==null? false: p.implies(permission));
        }

        if( !implied )
        {
            return false;
        }
        else if( permName.equalsIgnoreCase( CRYPTOSERVICENAME ) )
        {
            return callStackOkay();
        }
        else
        {
            return true;
        }
    }

    private boolean callStackOkay()
    {
        // a simple security manager just for getting the callstack
        class StackCheckSecurityManager extends SecurityManager
        {
            public Class[] getCallStack()
            {
                return this.getClassContext();
            }
        }

        // get such a security manager privileged
        class doGetSecurityManager implements PrivilegedAction
        {
            public Object run()
            {
                return new StackCheckSecurityManager();
            }
        };

        StackCheckSecurityManager secman = (StackCheckSecurityManager)
                        AccessController.doPrivileged( new doGetSecurityManager() );

        Class[] callstack = secman.getCallStack();
        for (int i = 0; i < callstack.length; i++)
        {
            String className = callstack[i].getName();

            // we'vre reached the dispatcher, okay
            if( className.equals(DISPATCHER) )
                return true;

            // this class below the dispatcher is not okay...
            if( !allowedStackEntries.contains(className) )
                return false;
        }

        // we've not found the dispatcher, something is wrong...
        return false;
    }

    public Enumeration elements ()
    {
        return this.table.elements ();
    }
}
