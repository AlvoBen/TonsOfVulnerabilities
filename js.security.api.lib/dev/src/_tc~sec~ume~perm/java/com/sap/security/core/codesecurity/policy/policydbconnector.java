package com.sap.security.core.codesecurity.policy;

import java.util.Enumeration;
import java.sql.*;

/**
 * wants to connect to the database with url com.sap.security.policy.dburl with
 * user from system property com.sap.security.policy.dbuser and password from
 * system property com.sap.security.policy.dbpass.
 *
 * Reads from a table PORTALCODEPERMISSION with the columns POLICYNAME, CODEBASE,
 * SIGNER, PERMISSION, NAME, ACTION. CODEBASE, SIGNER, NAME and ACTION may be
 * empty.
 *
 * Reads from a table PORTALKEYSTORE with the columns POLICYNAME, KEYSTOREURL
 * and KEYSTORETYPE. KEYSTORETYPE may be empty.
 *
 * Empty fields means that it contains whitespace characters!
 */
public class PolicyDBConnector extends PersistenceAdapter
{

    public PolicyDBConnector()
    {
        super();
    }

    public PolicyDBConnector(boolean flag)
    {
        super(flag);
    }

    public void initAdapterImpl() throws PersistenceAdapterException
    {
        //String url = "jdbc:odbc:myODBCSource";
        String dburl = getPropertyPrivileged("com.sap.security.policy.dburl");
        String dbuser = getPropertyPrivileged("com.sap.security.policy.dbuser", "sa");
        String dbpass = getPropertyPrivileged("com.sap.security.policy.dbpass", "");
        String policyname = getPropertyPrivileged("com.sap.security.policy.policyname", "default");

        Connection con = null;
        try
        {
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
        }
        catch( SQLException  sqle )
        {
            throw new PolicyDBException("could not connect to policy database: " + sqle.getMessage());
        }

        try
        {
            // we're connected, read the grant entries from table PORTALCODEPERMISSION
            PreparedStatement codeBaseStmt = con.prepareStatement(
                    "SELECT DISTINCT CODEBASE, SIGNER " +
                    "FROM PORTALCODEPERMISSION " +
                    "WHERE POLICYNAME = ? " +
                    "ORDER BY CODEBASE, SIGNER");
            // for each line of this resultset we must
            // create a protectiondomain entry
            codeBaseStmt.setString( 1, policyname );
            ResultSet codeBaseRs = codeBaseStmt.executeQuery();
            while( codeBaseRs.next() )
            {
                String pdecodebase = null;
                String pdesigner = null;

                // loop on all codebases
                String selcodebase = codeBaseRs.getString(1);
                String selsigner = codeBaseRs.getString(2);
                if( selcodebase == null || selcodebase.trim().length() == 0 )
                {
                    pdecodebase = null;
                    selcodebase = "";
                }
                else
                    pdecodebase = selcodebase.trim();

                if( selsigner == null || selsigner.trim().length() == 0 )
                {
                    pdesigner = null;
                    selsigner = "";
                }
                else
                    pdesigner = selsigner.trim();

                try
                {
                    pdesigner = replaceProps(pdesigner);
                    pdecodebase = replaceProps(pdecodebase);
                }
                catch( PropertyReplacer.PropertyReplacerException pe )
                {
                    throw new PolicyDBException(pe.getMessage());
                }
                ProtectionDomainEntry pde = new ProtectionDomainEntry(pdesigner, pdecodebase);
                // get all permissions for this codebase
                PreparedStatement permissionsStmt = con.prepareStatement(
                    "SELECT PERMISSION, NAME, ACTION " +
                    "FROM PORTALCODEPERMISSION " +
                    "WHERE POLICYNAME = ? AND " +
                    "      CODEBASE = ? AND " +
                    "      SIGNER = ? " +
                    "ORDER BY CODEBASE, SIGNER");

                permissionsStmt.setString( 1, policyname );
                permissionsStmt.setString( 2, selcodebase );
                permissionsStmt.setString( 3, selsigner );
                ResultSet permissionsRs = permissionsStmt.executeQuery();

                while( permissionsRs.next() )
                {
                    // loop on all permission entries for this codebase
                    String permission = permissionsRs.getString(1).trim();
                    String name = permissionsRs.getString(2);
                    String action = permissionsRs.getString(3);
                    if( name != null )
                    {
                        name = name.trim();
                        if( name.length() == 0 )
                            name = null;
                    }
                    if( action != null )
                    {
                        action = action.trim();
                        if( action.length() == 0 )
                            action = null;
                    }

                    try
                    {
                        name = replaceProps(name);
                        action = replaceProps(action);
                    }
                    catch( PropertyReplacer.PropertyReplacerException pe )
                    {
                        throw new PolicyDBException(pe.getMessage());
                    }
                    PermissionEntry pe = new PermissionEntry( permission, name, action );
                    pde.add(pe);
                }
                // current protection domain entry is done
                this.add( pde );
            }

            // now read the keystore entry from table PORTALKEYSTORE
            PreparedStatement keystoreStmt = con.prepareStatement(
                    "SELECT KEYSTOREURL, KEYSTORETYPE " +
                    "FROM PORTALKEYSTORE " +
                    "WHERE POLICYNAME = ?");

            keystoreStmt.setString( 1, policyname );
            ResultSet keystoreRs = keystoreStmt.executeQuery();

            //one keystore is enough, more is an error!
            boolean enough = false;
            while( !enough && keystoreRs.next() )
            {
                String keystoreurl = keystoreRs.getString(1).trim();
                String keystoretype = keystoreRs.getString(2);
                if( keystoretype != null )
                {
                    keystoretype = keystoretype.trim();
                    if( keystoretype.length() == 0 )
                        keystoretype = null;
                }
                mm_keystore.mm_keyStoreUrlString = keystoreurl;
                mm_keystore.mm_keyStoreType = keystoretype;
                enough = true;
            }
            if( enough && keystoreRs.next() )
            {
                // there is another keystore entry -> error
                throw new PolicyDBException("too many keystore entries");
            }
        }
        catch( SQLException sqle )
        {
            throw new PolicyDBException("read data from policy database: " + sqle.getMessage());
        }
    }

    /**
     * upload myself to database
     */
    protected void upload( String policyName ) throws PersistenceAdapterException
    {
        String dburl = getPropertyPrivileged("com.sap.security.policy.dburl");
        String dbuser = getPropertyPrivileged("com.sap.security.policy.dbuser", "sa");
        String dbpass = getPropertyPrivileged("com.sap.security.policy.dbpass", "");

        Connection con = null;
        try
        {
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
        }
        catch( SQLException sqle )
        {
            throw new PolicyDBException("could not connect to policy database: " + sqle.getMessage());
        }

        try
        {
            // we're connected, delete this policy from table PORTALCODEPERMISSION
            PreparedStatement deleteStmt = con.prepareStatement(
                    "DELETE FROM PORTALCODEPERMISSION WHERE POLICYNAME = ?");
            deleteStmt.setString(1, policyName);
            deleteStmt.executeUpdate();

            PreparedStatement deleteKeystoreStmt = con.prepareStatement(
                    "DELETE FROM PORTALKEYSTORE WHERE POLICYNAME = ?");
            deleteKeystoreStmt.setString(1, policyName);
            deleteKeystoreStmt.executeUpdate();

            if(  this.mm_keystore.isEmpty() && this.mm_pdEntries.size() == 0 )
            {
                // done
                con.commit();
                return;
            }

            PreparedStatement insertStmt = con.prepareStatement(
                    "INSERT INTO PORTALCODEPERMISSION VALUES ( ?, ?, ?, ?, ?, ? )");

            // set the policyname
            insertStmt.setString( 1, policyName );

            // insert the grant entries
            Enumeration pdenum = this.mm_pdEntries.elements();
            while (pdenum.hasMoreElements())
            {
                ProtectionDomainEntry pditem = (ProtectionDomainEntry) pdenum.nextElement();
                insertStmt.setString( 2, pditem.mm_codeBase == null ? " ": pditem.mm_codeBase );
                insertStmt.setString( 3, pditem.mm_signedBy == null ? " ": pditem.mm_signedBy );
                if( pditem.mm_permissionEntries == null || pditem.mm_permissionEntries.size() == 0 )
                {
                        insertStmt.setString( 4, " " );
                        insertStmt.setString( 5, " " );
                        insertStmt.setString( 6, " " );
                        insertStmt.executeUpdate();
                }
                else
                {
                    Enumeration grenum = pditem.mm_permissionEntries.elements();
                    while (grenum.hasMoreElements())
                    {
                        PermissionEntry peritem = (PermissionEntry) grenum.nextElement();
                        insertStmt.setString( 4, peritem.mm_permissiontype );
                        insertStmt.setString( 5, peritem.mm_name == null ? " ": peritem.mm_name );
                        insertStmt.setString( 6, peritem.mm_action == null ? " ": peritem.mm_action );
                        insertStmt.executeUpdate();
                    }
                }
            }

            // insert the keystore
            if( !this.mm_keystore.isEmpty() )
            {
                PreparedStatement insertKeystoreStmt = con.prepareStatement(
                        "INSERT INTO PORTALKEYSTORE VALUES ( ?, ?, ? )");
                insertKeystoreStmt.setString( 1, policyName );
                insertKeystoreStmt.setString( 2,
                    this.mm_keystore.mm_keyStoreUrlString == null ? " ": this.mm_keystore.mm_keyStoreUrlString);
                insertKeystoreStmt.setString( 3,
                    this.mm_keystore.mm_keyStoreType == null ? " ": this.mm_keystore.mm_keyStoreType);
                insertKeystoreStmt.executeUpdate();
            }


            // done!
            con.commit();
        }
        catch( SQLException  sqle )
        {
            throw new PolicyDBException("could not upload policy: " + sqle.getMessage());
        }

    }







}



