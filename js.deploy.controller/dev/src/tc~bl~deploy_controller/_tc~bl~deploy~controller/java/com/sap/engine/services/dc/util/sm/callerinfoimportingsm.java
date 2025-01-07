package com.sap.engine.services.dc.util.sm;

import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.session.exec.ExecutionDetails;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.tc.logging.Location;

/**
 * Wraps <code>ExecutionDetails</code> to get caller related data for
 * <code>CallerInfo</code>
 * <p>
 * NOTE: Package com.sap.engine.session.* is not accessible in offline phase
 * 
 * @author Anton Georgiev
 * @since 7.20
 */
public class CallerInfoImportingSM {
	private static Location location = DCLog.getLocation(CallerInfoImportingSM.class);

	public static String getHost() {
		final SessionExecContext ex = SessionExecContext.getExecutionContext();
		if (ex != null) {
			final ExecutionDetails executionDetails = ex.getDetails();
			if (executionDetails != null) {
				String host = executionDetails.getHost();
				if(host == null){
					DCLog.traceWarningWithCSNComponentAndFaultyDCName(location, "ASJ.dpl_dc.005204", "Remote host name is null; P4 Connection is not configured with a remote host name", executionDetails, null);
					return null;
				}
				return executionDetails.getHost();
			} 
		} 
		
		return Constants.LOCALHOST;
	}

}
