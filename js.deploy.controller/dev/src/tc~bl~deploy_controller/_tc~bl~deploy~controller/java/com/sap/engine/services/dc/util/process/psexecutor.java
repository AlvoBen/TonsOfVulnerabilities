package com.sap.engine.services.dc.util.process;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.IOException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.util.Constants;
import com.sap.tc.logging.Location;

public class PSExecutor {
	
	private static Location location = getLocation(PSExecutor.class);

	public static PSResult exec(ThreadSystem threadSystem, String cmd)
			throws IOException, InterruptedException {
		final Process proc = Runtime.getRuntime().exec(cmd);
		return exec(proc, threadSystem, cmd);
	}

	private static PSResult exec(Process proc, ThreadSystem threadSystem,
			String cmd) throws IOException, InterruptedException {
		final StreamReader srError = new StreamReader(proc.getErrorStream());
		startThread(threadSystem, srError);

		final StreamReader srOutput = new StreamReader(proc.getInputStream());
		startThread(threadSystem, srOutput);

		int exitCode = proc.waitFor();

		final PSResult psResult = new PSResult(exitCode, srError.getResult(),
				srOutput.getResult());
		if (location.beDebug()) {
			traceDebug(
					location, 
					"After executing [{0}] command the result was. {1}[{2}]",
					new Object[] { cmd, Constants.EOL, psResult });
		}

		return psResult;
	}

	private static void startThread(ThreadSystem threadSystem, Thread thread) {
		if (threadSystem != null) {
			threadSystem.startThread(thread, true, true);
		} else {
			thread.start();
		}
	}

}
