package com.sap.security.api.persistence;

import java.util.Properties;
import java.util.Set;

import com.sap.security.api.FeatureNotAvailableException;
import com.sap.security.api.UMException;

/**
 * Externally visible interfaces of the ABAP Persistence adapter (R3Persistence).
 * 
 * @author Juergen Kremp (d026337)
 */
public interface IR3Persistence {

    /**
     * Class name of the R3Persistence implementation.
     * 
     * Only available as String, because any reference from the API to the 
     * Core is not permitted. 
     */
    public static String R3PERSISTENCE_CLASS_NAME = "com.sap.security.core.persistence.datasource.imp.R3Persistence";

    /**
     * Property key for method getProperty() in IDataSourceMetaData 
     * to access user mapping interface.
     */
    public static final String PROPERTY_MAPPING_USREXTID = "PROPERTY_MAPPING_USREXTID";

    /** 
     * Property key for method getProperty() in IDataSourceMetaData
     * to access synchronization interface. 
     * 
     * If getProperty() returns "null", LDAP harmonization is not active, 
     * otherwise an instance of ILDAPSync is returned. 
     */
    public static final String PROPERTY_LDAP_SYNC = "PROPERTY_LDAP_SYNC";

    /**
     * Property key for method getProperty() in IDataSourceMetaData
     * to obtain a list of LDAP servers available in the backend.
     * 
     * The returned object is a TreeMap<String,ILDAPServerInfo> with
     * the customizing key of the LDAP server as key.  
     *   
     * If there are no LDAP servers, an empty map is returned (never "null").
     */
    public static final String PROPERTY_LDAP_SERVERS = "PROPERTY_LDAP_SERVERS";
    
    /**
     * Property key for method getProperty() in IDataSourceMetaData
     * to obtain a list of all administrative groups available in the backend.
     * 
     * The returned object is a IR3GroupList containing all R3 backend groups
     */
    public static final String PROPERTY_ABAP_GROUPS = "PROPERTY_ABAP_GROUPS";


    /**
     * Provides information about the defined administrative groups in the R3
     * backend.
     *
     *  The interface contains metods to read all administrative groups and 
     *  to refresh the list
     */
    public static interface IR3GroupList {

        /**
         * refreshes the list by reading the data from the backend again
         * 
         * @return "false" in case the list hasn't changed "true" in case the
         *         list has changed
         * @throws UMException
         *             in case an error has occurred when accessing the
         *             backendata
         */
        public boolean refresh() throws UMException;

        /**
         * Return a List object containg all group names
         * 
         * @return List containing all group names
         * @throws UMException
         *             in case an error has occurred or if no data is available
         */
        public Set<String> getGroupList() throws UMException;
    }
    
    /**
     * Provides information about an LDAP Server in the ABAP backend system.
     *  
     * This information is available even if the LDAP harmonization feature
     * is not activated. 
     */
    public static interface ILDAPServerInfo {

        /**
         * Indicates whether there was an error during reading data from the 
         * backend. In this case, some of the fields may be filled 
         * even though, but other have empty values.
         * 
         * In case of error a message text is provided by getErrorText().
         * 
         * Most important case is that the password for the LDAP server
         * was entered before release 7.10 so that the function module
         * used for reading the data has no access to the secure storage.
         * 
         * @return
         *   "false" if instance is fully initialized.
         *   "true" if error during backend call. Use getErrorText() to 
         *   get data. Some fields are filled even though. 
         */
        public boolean hasError();

        /**
         * @return
         *   Error text if and only if hasError() == true, otherwise "null".
         */
        public String getErrorText();

        /**
         * @return
         *   LDAP Server ID (ABAP key in customizing)
         */
        public String getServerID();

        /**
         * @return
         *   LDAP Server Host.
         */
        public String getHost();

        /**
         * @return
         *   LDAP Server Port.
         */
        public int getPort();

        /**
         * @return
         *   Base DN. 
         */
        public String getBaseDN();

        /**
         * @return
         *   Array of configured object classes.
         */
        public String[] getObjectClasses();

        /**
         * @return
         *   The attribute declared as filter attribute (for SAP username). 
         */
        public String getFilterAttribute();

        /**
         * @return
         *   The LDAP product key (ABAP domain fixed value)
         */
        public String getLDAPProduct();

        /**
         * @return
         *   Name of the LDAP administrator user, or "null" if there
         *   is no such user configured (anyomous access).  
         */
        public String getAdminUser();

        /**
         * Check whether a presented password matches the correct password
         * for the user. 
         * 
         * Note that over the ILDAPServerInfo interface you cannot read out 
         * the password. This is only possible with the ILDAPSync interface, 
         * which comes into existence only if the solution is active and 
         * the password was read out from the UME service properties. 
         * 
         * @return
         *   "true", if the password is correct, "false" if the password
         *   is not correct. 
         *   
         *   If the ABAP system does not know a password, only the 
         *   empty string or null result in "true", all other content 
         *   in "false".
         *   
         * @throws
         *   UMException
         *     Thrown if the method cannot be used because the ABAP server
         *     could not deliver the password hash. This would have been 
         *     indicated by "hasError()" and the error text.
         */
        public boolean checkAdminPassword(String password) throws UMException;

    }

    /***************************************************************************
     * Interface for LDAP user sync configuration and execution in the 
     * ABAP backend system linked to this instance of R3Persistence. 
     **************************************************************************/
    public static interface ILDAPSync {

        /* Constants for LDAP properties */
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_HOST = "ABAP_LDAP_HOST";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_PORT = "ABAP_LDAP_PORT";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_BASE_DN = "ABAP_LDAP_BASE_DN";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_OBJECTCLASSES = "ABAP_LDAP_OBJECTCLASSES";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_SAP_USERNAME = "ABAP_LDAP_SAP_USERNAME";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_LOGONUID = "ABAP_LDAP_LOGONUID";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_ADMIN_USER = "ABAP_LDAP_ADMIN_USER";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_ADMIN_PASSWORD = "ABAP_LDAP_ADMIN_PASSWORD";
        /** @deprecated Use typed access via getServerInfo() */
        public static final String ABAP_LDAP_PRODUCT = "ABAP_LDAP_PRODUCT";

        /** UME property (without namespace) that switches LDAP harmonization on */
        public static final String PARTNER_LDAP_ACTIVE = "PartnerLDAPActive";

        /** UME property (without namespace) with the ABAP key of the LDAP server */
        public static final String PARTNER_LDAP_SERVERID = "PartnerLDAPServerID";

        /** UME property (without namespace) with the admin password of the LDAP Server */
        public static final String PARTNER_LDAP_PASSWORD = "PartnerLDAPAdminPassword";

        /** UME property (without namespace) with the logon attribute. Can by empty --> use filter attribute */
        public static final String PARTNER_LDAP_LOGON_ATT = "PartnerLDAPLogonAttribute";

        /**
         * Return ABAP ID for the LDAP server.
         * 
         * @return
         *   Customizing key of the LDAP server in ABAP. 
         */
        public String getServerID();

        /**
         * Return the LDAP Server Info object for the currently used 
         * LDAP server. 
         * 
         * @return
         *   ILDAPServerInfo instance. 
         */
        public ILDAPServerInfo getServerInfo();

        /**
         * Return the LDAP properties of the LDAP server that is configured 
         * for this R3Persistence instance. 
         * 
         * If an information should not be available for whatever reason, 
         * the key is not stored in the Properties object. 
         * 
         * @return
         *   A Properties object which contains the key that are present as
         *   public attributes in this interface starting with ABAP_LDAP_...
         * 
         *   ABAP_LDAP_HOST
         *     Hostname or IP address (stored as String) of the LDAP server
         * 
         *   ABAP_LDAP_PORT
         *     Port number, stored as String
         * 
         *   ABAP_LDAP_BASE_DN
         *     Root entry in directory under which all entries are located
         * 
         *   ABAP_LDAP_OBJECTCLASSES
         *     Comma separated list of object classes that any entry must
         *     have to be recognized as user entry. 
         * 
         *   ABAP_LDAP_SAP_USERNAME
         *     Attribute that contains the SAP username in the directory
         * 
         *   ABAP_LDAP_LOGONUID
         *     Attribute that contains the logon ID in the directory
         * 
         *   ABAP_LDAP_ADMIN_USER
         *     Name of administrative user in directory. If anonymous
         *     access is configured, an empty String is returned.
         * 
         *   ABAP_LDAP_ADMIN_PASSWORD
         *     Password of the administrative user in the directory. 
         * 
         *   ABAP_LDAP_PRODUCT
         *     Product key of the LDAP Server (ABAP domain LDAPPROD).
         *     
         * @deprecated
         *   Use the dedicated methods getServerInfo().get...() instead for 
         *   type safe access.
         */
        public Properties getLDAPProperties();

        /**
         * Return the password of the LDAP Admin user from the backend system.
         *  
         * @return
         *   LDAP Password.
         */
        public String getLDAPPassword();

        /**
         * Returns the name of the attribute that contains the user ID
         * for user search upon logon.
         * 
         * The value is either the UME property if filled, or the SAP username
         * attribute of the LDAP server in ABAP. 
         * 
         * @return
         *   The attribute (always filled, always to be used). 
         */
        public String getLogonIDAttribute();

        /**
         * Perform synchronization for a single user.
         * 
         * To reduce the performance impact, the implementation is free to
         * skip the synchronization if the last synchronization for the given 
         * user was within a small amount of time only.  
         * 
         * @param 
         *   username
         *     User name (not uniqueID)
         * 
         * @param
         *   modifyTimestamp
         *     If known, the "modifytimestamp" attribute of the user 
         *     in the directory. ABAP can use this information to decide
         *     without own access to LDAP whether this user needs to be 
         *     synchronized. 
         * 
         *     If the timestamp is not known, use <null>.
         * 
         * @throws 
         *   UMException
         *     Error during synchronization. Reasons vary from connection 
         *     problems to configuration problems in ABAP. As this makes
         *     no difference to the caller, no separation. 
         */
        public void synchronizeUser(String username, String modifyTimestamp)
            throws UMException;

    }

    /***************************************************************************
     *
     * Interface for user name mapping using the USREXTID data of the 
     * ABAP backend system that this data source is connected to. 
     *  
     * This interface can be given out with method getProperty() using
     * key constant PROPERTY_MAPPING_USREXTID.
     * 
     **************************************************************************/
    public static interface IMappingWithUSREXTID {

        /**
         * Returns the logical system that this data source is connected
         * to.
         * 
         * @return
         *   <SID>CLNT<MANDT>
         */
        public String getLogicalSystem();

        /**
         * Perform user name mapping using the USREXTID data of the backend.
         *  
         * @param 
         *   extIDType
         *     The type of the external ID (CHAR2)
         * 
         * @param 
         *   extID 
         *     The external ID (String)
         * 
         * @return
         *   A String array with the user names that this user is mapped to, 
         *   or <code>null</code> if no such mapping exists.
         * 
         * @throws
         *   FeatureNotAvailableException
         *     Backend system does not provide RFC capable mapping function
         *     for access to user mapping data.
         * 
         * @throws 
         *   PersistenceException
         *     Any error during the backend processing (e.g. communication
         *     error).
         */
        public String[] getUserNamesForExtID(String extIDType, String extID)
            throws FeatureNotAvailableException, UMException;

    }

}
