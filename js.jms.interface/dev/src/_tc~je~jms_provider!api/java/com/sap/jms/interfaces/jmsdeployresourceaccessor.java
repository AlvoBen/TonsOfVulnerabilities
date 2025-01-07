
package com.sap.jms.interfaces;

import com.sap.localization.ResourceAccessor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * @author D035640
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JMSDeployResourceAccessor extends ResourceAccessor {
    /**
	 * @param resourceBundleName
	 */
	public JMSDeployResourceAccessor(String resourceBundleName) {
		super(BUNDLE_NAME);
	}

	/** The name of the location object used for tracing and logging */
    static String LOCATION_NAME = "com.sap.jms";

    /** Location object used for tracing */
    static Location TRACER = Location.getLocation(LOCATION_NAME);

    /** Category variable used for logging */
    static Category LOGGER = Category.SYS_SERVER;

	/** The logger */
	  public static Category logger = LOGGER;
	  /** The tracer */
	  public static Location tracer = TRACER;
	  /** The name of the bundle */
	  private static final String BUNDLE_NAME = "com.sap.jms.interfaces.JMSDeployResourceBundle";
	  /** The resource accessor */
	  private static ResourceAccessor resourceAccessor  = null;
		
	    /**
	     * Static method  for getting an instance of the ResourceAccessor
	     * @return ResourceAccessor
	     */
		public static synchronized ResourceAccessor getResourceAccessor() {
			 if (resourceAccessor == null)	 {
			 	resourceAccessor = new JMSDeployResourceAccessor(BUNDLE_NAME);
			 }
			 return resourceAccessor;
		}
}
