package com.sap.security.core.codesecurity.policy;

import java.io.*;
import java.net.*;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.UnresolvedPermission;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class PortalPolicy extends Policy
{
/*
    public static void main(String args[])
        throws Exception
    {
        System.out.println("I am " +PortalPolicy.class.getName() + ", my source is " + PortalPolicy.class.getProtectionDomain().getCodeSource().getLocation().toString());
        System.out.println("policy file grants me the following permissions collection");
        System.out.println();
        PortalPolicy p = new PortalPolicy();
        p.init();
        PermissionCollection perms = p.getPermissions(PortalPolicy.class.getProtectionDomain().getCodeSource());
        System.out.println(perms.toString());
    }
*/

    /**
     * abstraction of the underlying persistence
     */
    private PersistenceAdapter persistenceAdapter = null;

    /**
     * to be able to handle different styles of codesources, we canonicalize their
     * locations. This is expensive, so we save the canonlicalized ones.
     */
    private Hashtable canonicalizedCodeSources = new Hashtable();

    public PortalPolicy()
    {
        init();
    }

    public void refresh()
    {
        init();
    }

    private void init()
    {
        try
        {
            persistenceAdapter = PersistenceAdapter.getPersistenceAdapter(true);
        }
        catch( PersistenceAdapterException pae )
        {
            pae.printStackTrace();
        }
    }


    private CodeSource canonicalizeCodebase(CodeSource rawCS, boolean allowRemote)
    {
        if( this.canonicalizedCodeSources.containsKey(rawCS) )
            return (CodeSource) this.canonicalizedCodeSources.get(rawCS);

	CodeSource canonicalizedCS = rawCS;
	if(rawCS.getLocation() != null && rawCS.getLocation().getProtocol().equalsIgnoreCase("file"))
	{
            try
            {
                String path = rawCS.getLocation().getFile().replace('/', File.separatorChar);
                URL url = null;
		if(path.endsWith("*"))
                {
                    path = path.substring(0, path.length() - 1);
                    boolean flag1 = false;
                    if(path.endsWith(File.separator))
                    {
                        flag1 = true;
                    }
                    if(path.equals(""))
                    {
                        path = (String) AccessController.doPrivileged(
                                new PrivilegedAction()
                                {
                                    public Object run()
                                    {
                                        return System.getProperty("user.dir");
                                    }
                                }
                            );
                    }
                    File file = new File(path);
                    path = file.getCanonicalPath();
                    StringBuffer stringbuffer = new StringBuffer(path);
                    if(!path.endsWith(File.separator) && (flag1 || file.isDirectory()))
                    {
                            stringbuffer.append(File.separatorChar);
                    }
                    stringbuffer.append('*');
                    path = stringbuffer.toString();
                }
                else
                {
                    path = (new File(path)).getCanonicalPath();
                }
                url = (new File(path)).toURL();
		if(allowRemote)
                {
                    canonicalizedCS = new CodeSource(url, (java.security.cert.Certificate[])null);
                }
                else
                {
                    canonicalizedCS = new CodeSource(url, rawCS.getCertificates());
                }
            }
            catch(IOException ioexception)
            {
                if(allowRemote)
                {
                    canonicalizedCS = new CodeSource(rawCS.getLocation(), (java.security.cert.Certificate[])null);
                }
            }
        }
        else if(allowRemote)
        {
                canonicalizedCS = new CodeSource(rawCS.getLocation(), (java.security.cert.Certificate[])null);
        }

        this.canonicalizedCodeSources.put(rawCS, canonicalizedCS);
        return canonicalizedCS;
    }



    public PermissionCollection getPermissions(CodeSource thatCodesource)
    {
        Permissions outPerms = new Permissions();
        thatCodesource = canonicalizeCodebase(thatCodesource, true);

        Enumeration enumeration = this.persistenceAdapter.protectionDomainElements();

        while (enumeration.hasMoreElements())
        {
            ProtectionDomainEntry pdEntry = (ProtectionDomainEntry) enumeration.nextElement();
            try
            {
                // if we have a protectiondomain without codebase, its permissions
                // must be given to all incoming codesources
                boolean mustAdd = pdEntry.mm_codeBase == null;
                if( !mustAdd )
                {
                    URL entryUrl = new URL(pdEntry.mm_codeBase);
                    CodeSource thisCodesource = canonicalizeCodebase(new CodeSource(entryUrl, (java.security.cert.Certificate[])null), true );
                    mustAdd = thisCodesource.implies(thatCodesource);
                }
                if( mustAdd )
                {
                    // must add all permissions of this entry to the returned PermissionCollection
                    Iterator iter = pdEntry.mm_permissionEntries.iterator();
                    while (iter.hasNext())
                    {
                        PermissionEntry permentry = (PermissionEntry) iter.next();
                        Permission perm = getPermissionObject(
                                                permentry.mm_permissiontype,
                                                permentry.mm_name,
                                                permentry.mm_action);

                        //if( !outPerms.implies(perm) ) //performance?
                        outPerms.add(perm);
                    }
                }
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        return outPerms;
    }

    /**
     * construct instance of subclass of Permission from classname, name and action
     * note that name and action can both be null
     */
    private static final Permission getPermissionObject(String classname, String name, String action)
                    throws Exception
    {
        // try to find it in the hashtable first
        Constructor permissionConstructor = (Constructor) permissionConstructors.get(classname);
        if( permissionConstructor != null )
        {
            try
            {
                return (Permission) permissionConstructor.newInstance(new String[] {name, action});
            }
            catch( Exception e )
            {
                // we should never get here because we did this before...
                e.printStackTrace();
                return null;
            }
        }

        Class permissionclass = null;

        try
        {
            permissionclass = Class.forName(classname);
        }
        catch( ClassNotFoundException cnfe )
        {
            // don't know the permission class construct an UnresolvedPermission
            /**
             * @todo certificates
             */
            return new UnresolvedPermission(classname, name, action, null );
        }

        class doGetConstructor implements PrivilegedExceptionAction
        {
            private Class cl = null;
            public doGetConstructor( Class cl )
            {
                this.cl = cl;
            }
            public Object run() throws NoSuchMethodException
            {
                return this.cl.getConstructor(stringArr);
            }
        };

        Constructor c = null;
        try
        {
            c = (Constructor) AccessController.doPrivileged(new doGetConstructor(permissionclass));
        }
        catch( PrivilegedActionException pae )
        {
            throw pae.getException();
        }

        permissionConstructors.put(classname, c);
        return (Permission) c.newInstance( new String[] {name, action} );
    }

    private static final Class[] stringArr = new Class[]{String.class, String.class};
    private static Hashtable permissionConstructors = new Hashtable();
}

