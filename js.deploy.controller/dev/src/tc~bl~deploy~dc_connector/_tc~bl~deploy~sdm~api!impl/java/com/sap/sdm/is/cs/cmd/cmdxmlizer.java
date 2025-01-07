package com.sap.sdm.is.cs.cmd;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public interface CmdXMLizer {
	String toXMLString(CmdIF cmd);

	CmdIF fromXMLString(String xmlString) throws CmdReconstructionException;
}
