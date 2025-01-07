package com.sap.engine.services.dc.sapcontrol.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.io.IOException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.sapcontrol.SapControl;
import com.sap.engine.services.dc.sapcontrol.SapControlException;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.process.PSExecutor;
import com.sap.engine.services.dc.util.process.PSResult;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class SapControlImpl implements SapControl {
	
	private Location location = getLocation(this.getClass());

	private final ThreadSystem threadSystem;
	private final String sapcontrol;

	// if threadSystem is null PSExecutor will create it itself; null means that
	// the restart is triggered out of the engine
	SapControlImpl(ThreadSystem threadSystem, String runDir, String host,
			String user, String pass, String instNum) {
		this.threadSystem = threadSystem;
		this.sapcontrol = String
				.format(
						"%s"
								+ File.separator
								+ "sapcontrol -nr %s -host %s -format script -user %s %s -function ",
						runDir, instNum, host, user, pass);
		if (location.beDebug()) {
			traceDebug(
					location,
					"The [{0}] threadSystem, [{1}] sapcontrol, will be used in [{2}].",
					new Object[] { threadSystem, sapcontrol, this });
		}
	}

	public void restartInstance() throws SapControlException {
		final String cmd = sapcontrol + "RestartInstance";
		PSResult psResult;
		try {
			psResult = PSExecutor.exec(threadSystem, cmd);
		} catch (IOException ioEx) {
			throw new SapControlException("Cannot " + cmd + ".", ioEx);
		} catch (InterruptedException interEx) {
			throw new SapControlException("Cannot " + cmd + ".", interEx);
		}
		if (psResult.getExitValue() != 0) {
			throw new SapControlException("The execution of " + cmd
					+ " command failed." + Constants.EOL + psResult);
		}
	}

}
