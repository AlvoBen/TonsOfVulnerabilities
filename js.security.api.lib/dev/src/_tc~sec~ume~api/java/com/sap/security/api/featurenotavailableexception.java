package com.sap.security.api;

/**
 * This exception indicates a feature that is not available in the respective
 * implementation of a user management interface. Implementations must
 * consistently employ this exception type for unavailable features instead
 * of showing arbitrary behaviour (e.g. return <code>null</code> for unsupported
 * get-methods).
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class FeatureNotAvailableException
    extends UMRuntimeException {

	private static final long serialVersionUID = 8472212230559450191L;
	
    /**
     * Constructs a new FeatureNotAvailableException.
     */
    public FeatureNotAvailableException () {
    }


    /**
     * Constructs a new FeatureNotAvailableException with a descriptive
     * <code>message</code>.
     *
     * @param   message   the detail message. 
     */
    public FeatureNotAvailableException (String message) {
        super(message);
    }
}
