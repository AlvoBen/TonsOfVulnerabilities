/*
 * Created on Nov 10, 2003 by Thomas Brodkorb
 *
 */
package com.sap.sdm.is.cs.cmd;

/**
 * Created on Nov 10, 2003 by Thomas Brodkorb
 * 
 */
public class CmdNoReply implements CmdIF {
	final static String NAME = "NoReply";

	/**
   * 
   */
	public CmdNoReply() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdIF#getMyName()
	 */
	public String getMyName() {
		return NAME;

	}

}
