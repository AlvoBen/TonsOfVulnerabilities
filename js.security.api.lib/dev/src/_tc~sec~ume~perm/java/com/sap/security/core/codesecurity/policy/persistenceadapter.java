package com.sap.security.core.codesecurity.policy;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.security.Security;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.Enumeration;
import java.util.Date;
import java.util.Vector;

/**
 * used by the {@link PortalPolicy} implementation of the {@link Policy} interface.
 * Before calling <code>getPersistenceAdapter</code>, system properties need to
 * be set.
 * <p><code>com.sap.security.policy.adapter</code><br>
 * value <i>fileadapter</i>: use standard policy file<br>
 * value <i>databaseadapter</i>: use policy table in database</p>
 *
 * <p>for fileadapter, java.security.policy is additionally needed<br>
 * for databaseadapter, com.sap.security.policy.dburl, jdbc.drivers, com.sap.security.policy.dbuser
 * (default 'sa'), com.sap.security.policy.dbpass (default ''), com.sap.security.policy.policyname
 * (default 'default') are needed.
 */
 public abstract class PersistenceAdapter
{
    public static void main(String args[])
        throws Exception
    {
/*
        // testing
        System.setProperty("com.sap.security.policy.adapter", PersistenceAdapter.ADAPTERDATABASE);
        System.setProperty("com.sap.security.policy.dburl", "jdbc:inetdae7:p45459:1433?database=mpw_pcd");
        System.setProperty("jdbc.drivers", "com.inet.tds.TdsDriver:my.WurstDriver");
        System.setProperty("com.sap.security.policy.policyname", "wurstpol");

        theAdapter.getPersistenceAdapter(true);
        PrintWriter pw = new PrintWriter(System.out);
        theAdapter.print(pw);
        System.setProperty("com.sap.security.policy.adapter", PersistenceAdapter.ADAPTERFILE);
        System.setProperty("java.security.policy", System.getProperty("user.dir") + java.io.File.separator + "hallo.policy");
        theAdapter.getPersistenceAdapter(true);
        theAdapter.print(pw);
        System.setProperty("com.sap.security.policy.dburl", "jdbc:inetdae7:p45459:1433?database=mpw_pcd");
        System.setProperty("jdbc.drivers", "com.inet.tds.TdsDriver:my.WurstDriver");
*/
        if( args.length != 2 || args[0].length() == 0 ||  args[1].length() == 0 )
        {
            System.err.println("usage: java... " + PersistenceAdapter.class.getName() + " <policyfile> <policyname>!");
            return;
        }
        System.out.println("start uploading policy file " + args[0] +
                                " with policyname \"" + args[1] + "\"...");
        System.setProperty( "java.security.policy", args[0] );

        try
        {
            // create the two adapters with NO property replacement
            PersistenceAdapter inFileAdapter = new PolicyFileParser(false);
            PersistenceAdapter outDBAdapter = new PolicyDBConnector(false);

            // read data into the inFileAdapter
            inFileAdapter.initAdapterImpl();

            System.out.println("policy file at " + args[0] + " read successfully...");

            // transfer the data from one to the other
            outDBAdapter.mm_keystore = inFileAdapter.mm_keystore;
            outDBAdapter.mm_pdEntries = inFileAdapter.mm_pdEntries;
            //outDBAdapter.print(new PrintWriter(System.out));

            // upload the data
            outDBAdapter.upload(args[1]);
            System.out.println("finished uploading policy file " + args[0] +
                                " with policyname \"" + args[1] + "\"!");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    private static PersistenceAdapter theAdapter = null;

    public static final String ADAPTERFILE     = "fileadapter";
    public static final String ADAPTERDATABASE = "databaseadapter";

    public static final PersistenceAdapter getPersistenceAdapter(boolean reload)
        throws PersistenceAdapterException
    {
        if( reload )
            theAdapter = null;

        if( theAdapter != null )
            return theAdapter;

        String adapter = getPropertyPrivileged("com.sap.security.policy.adapter", PersistenceAdapter.ADAPTERFILE);

        // do we expand properties that we read from policy persistence?
        String propexprops = Security.getProperty("policy.expandProperties");
        boolean exprops = propexprops == null ? false : propexprops.equalsIgnoreCase("true");

        if( adapter.equalsIgnoreCase(PersistenceAdapter.ADAPTERFILE) )
            theAdapter = new PolicyFileParser(exprops);

        if( adapter.equalsIgnoreCase(PersistenceAdapter.ADAPTERDATABASE) )
            theAdapter = new PolicyDBConnector(exprops);

        if( theAdapter != null )
        {
            theAdapter.initAdapterImpl();
            return theAdapter;
        }

        throw new PersistenceAdapterException(
            PersistenceAdapter.class.getName() + ": requested adapter null or not known");
    }

    protected PersistenceAdapter()
    {
        this(false);
    }

    protected PersistenceAdapter(boolean flag)
    {
        mm_replaceProp = flag;
        mm_pdEntries = new Vector();
        mm_keystore = new KeyStoreEntry(flag);
    }

    /**
     * returns all protectiondomain entries what could be read from the
     * persistence
     */
    public final Enumeration protectionDomainElements()
    {
        return this.mm_pdEntries.elements();
    }

    public final void add(ProtectionDomainEntry pdEntry)
    {
        mm_pdEntries.addElement(pdEntry);
    }

    public final void replace(ProtectionDomainEntry pdNew, ProtectionDomainEntry pdOld)
    {
        mm_pdEntries.setElementAt(pdNew, mm_pdEntries.indexOf(pdOld));
    }

    public final boolean remove(ProtectionDomainEntry pdEntry)
    {
        return mm_pdEntries.removeElement(pdEntry);
    }

    public final KeyStoreEntry getKeyStore()
    {
        return mm_keystore;
    }

    public final void setKeyStore(KeyStoreEntry keystore)
    {
        mm_keystore = keystore;
    }

    public final void print(Writer writer)
    {
        PrintWriter printwriter = new PrintWriter(new BufferedWriter(writer));
        Enumeration enumeration = protectionDomainElements();
        printwriter.println("/**");
        printwriter.println(" * AUTOMATICALLY GENERATED ON " + new Date());
        printwriter.println(" * by " + this.getClass().getName());
        printwriter.println(" * DO NOT EDIT");
        printwriter.println(" */");
        printwriter.println();

        mm_keystore.print(printwriter);

        for(; enumeration.hasMoreElements(); printwriter.println())
        {
            ProtectionDomainEntry pdentry = (ProtectionDomainEntry)enumeration.nextElement();
            pdentry.print(printwriter);
        }

        printwriter.flush();
    }

    protected static final String getPropertyPrivileged( final String key, final String defaultval )
    {
        return (String) AccessController.doPrivileged
                            (
                                new PrivilegedAction()
                                {
                                    public Object run()
                                    {
                                        return System.getProperty(key, defaultval);
                                    }
                                }
                            );
    }

    protected static final String getPropertyPrivileged( final String key)
    {
        return (String) AccessController.doPrivileged
                            (
                                new PrivilegedAction()
                                {
                                    public Object run()
                                    {
                                        return System.getProperty(key);
                                    }
                                }
                            );
    }

    protected String replaceProps(String stringwithprops)
        throws PropertyReplacer.PropertyReplacerException
    {
        // shall system properties be replaced?
        if(mm_replaceProp)
            return PropertyReplacer.replace(stringwithprops);
        else
            return stringwithprops;
    }

    /**
     * initialize the adapter implementation
     */
    protected abstract void initAdapterImpl() throws PersistenceAdapterException;

    /**
     * upload the policy
     */
    protected abstract void upload( String policyName ) throws PersistenceAdapterException;

    // entries in the policy file
    protected Vector mm_pdEntries;
    // replace system properties?
    protected boolean mm_replaceProp;
    protected KeyStoreEntry mm_keystore;
}