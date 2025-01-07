package com.sap.sdm.is.cs.cmd;

import com.sap.sdm.util.log.Logger;
import com.sap.sdm.util.log.Trace;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Thomas Brodkorb
 * @version 1.0
 */
public class CmdNoResponseTest implements NoResponseCmdIF {
	private final static Logger log = Logger.getLogger();
	private final static Trace trace = Trace.getTrace(CmdNoResponseTest.class);
	public final static String NAME = "NoResponseTest";

	private boolean reply = false;

	public CmdNoResponseTest(boolean reply) {
		this.reply = reply;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdIFNew#getMyName()
	 */
	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.NoResponseCmdIF#reply()
	 */
	public boolean reply() {
		return this.reply;

	}

}
