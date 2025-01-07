package com.sap.security.api.umap;
import java.util.Properties;

import com.sap.security.api.UMException;
import com.sap.security.api.umap.system.ISystemLandscapeObject;

/**
 * <p>This exception is thrown when user mapping data for a combination of
 *   principal and backend system is requested, but there is no user mapping
 *   data available (which fits the system's configured logon method).
 * </p>
 * 
 * <p>You can use {@link #getReason()} to distinguish between different
 *   reasons that led to this exception and react accordingly.
 * </p>
 */
public class NoLogonDataAvailableException extends UMException
{
	private static final long serialVersionUID = -7942898995330829425L;
	
    /**
     * User mapping data is not available because of an unknown reason.
     * You should have a look at log and trace files for details.
     */
	public static final int REASON_UNKNOWN                   = 0;

    /**
     * No user mapping data has been saved for the current user / system
     * combination (so this is no error, just an information).
     */
	public static final int REASON_NO_DATA                   = 1;

    /**
     * <p>The encryption modes used to save and now read the user mapping data are
     *   incompatible.
     * </p>
     * 
     * <p>Example: User mapping data has been saved with weak encryption. In the
     *   meanwhile, the administrator switched to strong encryption, but didn't
     *   use the conversion facility to migrate all existing user mapping data.
     *   Now the user mapping data is still weakly encrypted, but a system
     *   configured to use strong encryption does not accept weakly encrypted user
     *   mapping data for security reasons.
     * </p>
     */
	public static final int REASON_CRYPTO_MISMATCH           = 2;

    /**
     * <p>There are several local users mapped to the same backend user.
     * </p>
     * 
     * <p>Only relevant for
     *   {@link com.sap.security.api.umap.IUserMapping#getInverseMappingData(String, ISystemLandscapeObject)}
     *   and overloaded methods with the same name.
     * </p>
     */
	public static final int REASON_MULTIPLE_MAPPING          = 3;

    /**
     * <p>Strongly encrypted user mapping data can not be decrypted on this machine
     *   because the so-called "JCE policy files for unlimited strength encryption"
     *   are missing.
     * </p>
     * 
     * <p>These files need to be installed in every JDK installation used by the
     *   SAP AS Java cluster, i.e. on every cluster node. See SAP note 796540 for
     *   further details.
     * </p>
     */
	public static final int REASON_POLICY_FILES_MISSING      = 4;

    /**
     * Strongly encrypted user mapping data can not be decrypted because the main
     * key used for user mapping encryption has been changed after saving the 
     * user mapping data.
     */
	public static final int REASON_KEY_CHANGED               = 5;

    /**
     * <p>here is no existing SAP logon ticket for the user for which the request
     *   for user mapping data has been issued.
     * </p>
     * 
     * <p>Only relevant for
     *   {@link com.sap.security.api.umap.IUserMappingData#enrich(Properties)}
     *   and systems with logon method <code>"SAPLogonTicket"</code>
     *   ({@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_TICKET}).
     * </p>
     */
	public static final int REASON_NO_TICKET                 = 6;

    /**
     * <p>There is no existing certificate for the user for which the request for
     * user mapping data has been issued.
     * </p>
     * 
     * <p>Only relevant for
     *   {@link com.sap.security.api.umap.IUserMappingData#enrich(Properties)}
     *   and systems with logon method <code>"X509CERT"</code>
     *   ({@link com.sap.security.api.logon.ILoginConstants#SSO_JCO_LOGON_METHOD_X509CERT}).
     * </p>
     */
	public static final int REASON_NO_CERTIFICATE            = 7;

    /**
     * Strongly encrypted user mapping data can not be decrypted because the main
     * key used for user mapping encryption is not available.
     */
	public static final int REASON_NO_KEY                    = 8;

    /**
     * <p>User mapping data has been saved for a system that was not the SAP
     * reference system at the time of saving, but now the system is the SAP
     * reference system.
     * </p>
     * 
     * <p>Background: User mapping data for the SAP reference system needs to be
     *   verified when being saved for security reasons. Verification is not done
     *   for other systems, so "normal" user mappings can not be used for the SAP
     *   reference system, but need to be "refreshed" (entered again).
     * </p>
     */
    public static final int REASON_NORMAL_MAPPING_BUT_REFSYS = 9;

    /**
     * <p>Access to sensitive information of the user mapping data has been denied.</p>
     */
    public static final int REASON_ACCESS_DENIED = 10;

    /**
     * The "digital signature" protecting user mappings for the SAP reference system from
     * unauthorized changes is missing (SAP note 1107795).
     */
    public static final int REASON_REFSYS_MAPPING_SIGNATURE_MISSING = 11;

	private int _reason = REASON_UNKNOWN;

    /**
     * Create a <code>NoLogonDataAvailableException</code> without further
     * information.
     */
    public NoLogonDataAvailableException ()
    {
        super ();
    }

    /**
     * Create a <code>NoLogonDataAvailableException</code> with a specific error
     * message.
     * 
     * @param message The message text for the exception.
     */
    public NoLogonDataAvailableException(String message)
    {
        super(message);
    }
    
    /**
     * <p>Create a <code>NoLogonDataAvailableException</code> with a specific
     *   error message and an error code.
     * </p>
     *
     * <p>The code can be retrieved by {@link #getReason()} and can be used e.g.
     *   to programmatically handle different reasons for user mapping data
     *   being missing.
     * </p>
     * 
     * @param message The message text for the exception.
     * @param reason The reason for the exception. One of the constants starting
     *        by <code>REASON_</code>.
     */
	public NoLogonDataAvailableException(String message, int reason)
	{
		super(message);
		_reason = reason;
	}

    /**
     * Get the error code of the reason that caused this exception.
     * 
     * @return Any of the constants starting by <code>REASON_</code>.
     */
	public int getReason() {
		return _reason;
	}
    
}
