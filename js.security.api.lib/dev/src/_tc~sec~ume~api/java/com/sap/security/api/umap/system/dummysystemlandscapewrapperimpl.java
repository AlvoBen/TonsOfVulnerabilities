package com.sap.security.api.umap.system;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.api.umap.IUserMapping;
import com.sap.tc.logging.Location;

/**
 * This dummy system landscape implementation can be used in systems where there
 * is no real system landscape repository (including a respective system landscape
 * wrapper for UME) available but where there is still a need to have different
 * "SAP" (ABAP) and "Portal" (Java) user IDs in SAP logon tickets.
 * 
 * Having those different user IDs in such authentication tickets requires a user
 * mapping for the affected local user. As the general user mapping documentation
 * describes (see {@link com.sap.security.api.umap.IUserMapping}), a user mapping
 * is always bound to a specific backend system. The special user mapping for SAP
 * authentication tickets, too, needs to be bound to a specific system (although
 * the ticket can be used to connect to other systems, too, as long as they are
 * appropriately configured to accept these tickets).
 * 
 * The special system which the "SAP authentication ticket" user mapping is bound
 * to is the so-called "SAP reference system" (or "master system") which can be
 * configured in UME property "ume.r3.mastersystem". When there is no real system
 * landscape implementation available, this dummy implementation can be used to
 * provide a dummy system object ({@link com.sap.security.api.umap.system.ISystemLandscapeObject})
 * for the SAP reference system. This enables maintenance of local user's ABAP
 * backend user IDs as they should be used in SAP authentication tickets. 
 * 
 * Properties of this implementation:
 * <ul>
 *   <li>There is exactly one single (dummy) backend system available.</li>
 *   <li>The backend system's logon method is always "SAPLogonTicket".</li>
 *   <li>The system's unique key is the same as the system's alias.</li>
 *     
 * </ul>
 *
 * To make UME use this <code>ISystemLandscape</code> implementation,
 * set UME property "ume.r3.mastersystem" to "UME Internal Reference System".
 * <b>Note:</b> This should only be used if there is no other system landscape
 * wrapper available, e.g. the one contained in SAP Enterprise Portal, because
 * currently only one system landscape is supported at the same time.
 */
public class DummySystemLandscapeWrapperImpl implements ISystemLandscape {

	private static final Location _loc = Location.getLocation(DummySystemLandscapeWrapperImpl.class);

    static final String TYPE_DUMMY = "Dummy";

    public static final String INTEGRATED_REFERENCE_SYSTEM = "UME Internal Reference System";
    public static final String INTEGRATED_STORAGE_PREFIX   = "ume_internal";

    // Not private to allow access by inner anonymous Enumeration classes
    // (see getEnumerationFromMaster())
    ISystemLandscapeObject _masterSystem;

    private String _storagePrefix;

    public String getType() {
    	if(INTEGRATED_REFERENCE_SYSTEM.equals(_masterSystem.getAlias())) {
    		return TYPE_UME_INTEGRATED;
    	}

    	return TYPE_DUMMY;
    }

    public String getDisplayName(@SuppressWarnings("unused") Locale locale) {
    	return "UME";
    }

    /**
     * Create a new dummy system landscape wrapper.
     * 
     * This constructor implies the following default values:
     * <ul>
     *   <li>Storage prefix: {@link IUserMapping#UMAP_EP6_ALIAS_PREFIX}
     * </ul>
     * 
     * @param masterSystemAlias System alias of the SAP reference (dummy) system.
     */
    public DummySystemLandscapeWrapperImpl(String masterSystemAlias) {
        this(masterSystemAlias, IUserMapping.UMAP_EP6_ALIAS_PREFIX);
    }

    /**
     * Create a new dummy system landscape wrapper.
     * 
     * @param masterSystemAlias System alias of the SAP reference (dummy) system.
     * @param storagePrefix "Storage prefix" to use for user mapping data
     *                      persistence (see {@link ISystemLandscapeObject#getStorageKeyPrefix()})
     */
    public DummySystemLandscapeWrapperImpl(String masterSystemAlias, String storagePrefix) {
        _masterSystem = new DummySystemObjectImpl(
            masterSystemAlias,
            ILoginConstants.SSO_JCO_LOGON_METHOD_TICKET,
            this
        );
        _storagePrefix = storagePrefix;
    }

    /**
     * See {@link ISystemLandscape#getAllAliases()}
     */
    @SuppressWarnings("unused")
	public Enumeration getAllAliases()
    throws ExceptionInImplementationException {
        return getEnumerationFromMaster();
    }

    @SuppressWarnings("unused")
	public Enumeration getAllAliases(@SuppressWarnings("unused") IPrincipal principal)
    throws ExceptionInImplementationException {
        return getEnumerationFromMaster();
    }

    /**
     * See {@link ISystemLandscape#getUniqueKeyForAlias(String)}
     */
    public String getUniqueKeyForAlias(String alias)
    throws ExceptionInImplementationException {
        if(null == alias) {
            String msg = "A 'null' String is not a valid system alias.";
            throw new ExceptionInImplementationException(msg);
        }

        // In this dummy scenario, the system's GUID is the same as the system's alias
        String masterSystemAlias = _masterSystem.getAlias();
        if(alias.equals(masterSystemAlias)) {
            return masterSystemAlias;
        }
        else {
            String msg = MessageFormat.format(
                "There is no system with alias ''{0}''.",
                new Object[] { alias }
            );
            ExceptionInImplementationException e = new ExceptionInImplementationException(msg);
            e.setImplementationException(new Exception(msg));
            throw e;
        }
    }

    /**
     * See {@link ISystemLandscape#getAllUniqueKeys()}
     */
    @SuppressWarnings("unused")
	public Enumeration getAllUniqueKeys()
    throws ExceptionInImplementationException {
        return getEnumerationFromMaster();
    }

    /**
     * See {@link ISystemLandscape#getSystemByUniqueKey(String)}
     */
    public ISystemLandscapeObject getSystemByUniqueKey(String uniqueKey)
    throws ExceptionInImplementationException {
    	final String method = "getSystemByUniqueKey(String)";

        if(null == uniqueKey) {
            String msg = "A 'null' String is not a valid unique key for a system.";
            throw new ExceptionInImplementationException(msg);
        }

        // In this dummy scenario, the system's GUID is the same as the system's alias
        if(uniqueKey.equals(_masterSystem.getAlias())) {
            return _masterSystem;
        }
        else {
        	_loc.debugT(method,
            	"This dummy system landscape does not know a system with unqiue key \"{0}\".",
            	new Object[] { uniqueKey } );

            return null;
        }
    }

    /**
     * See {@link ISystemLandscape#getSystemByAlias(String)}
     */
    public ISystemLandscapeObject getSystemByAlias(String alias)
    throws ExceptionInImplementationException {
    	final String method = "getSystemByAlias(String)";

        if(null == alias) {
            String msg = "A 'null' String is not a valid system alias.";
            throw new ExceptionInImplementationException(msg);
        }

        if(alias.equals(_masterSystem.getAlias()))
            return _masterSystem;
        else {
        	_loc.debugT(method,
        		"This dummy system landscape does not know a system with alias \"{0}\".",
        		new Object[] { alias } );

        	return null;
        }
    }

    /**
     * See {@link ISystemLandscape#getStorageKeyPrefix()}
     */
    public String getStorageKeyPrefix() {
        return _storagePrefix;
    }

    /**
     * Get a minimal Enumeration that only contains the system alias (= unique key)
     * of the dummy SAP reference system.
     */
    private Enumeration getEnumerationFromMaster() {
        return new Enumeration() {
            int i = 0;

            public boolean hasMoreElements() {
                return i == 0;
            }

            public Object nextElement() {
                if(i > 0) {
                    return null;
                }
                else {
                    i++;
                    // In this dummy scenario, the system's GUID is the same as the system's alias
                    return _masterSystem.getAlias();
                }
            }
        };
    }

}
