package com.sap.sdm.is.cs.session.common;

import java.util.ArrayList;
import java.util.List;

import com.sap.sdm.is.cs.cmd.CmdIF;
import com.sap.sdm.is.cs.cmd.CmdReconstructionException;
import com.sap.sdm.is.cs.cmd.CmdXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizer;
import com.sap.sdm.is.stringxml.StringXMLizerFactory;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
abstract public class CmdLoginRequestXMLizer implements CmdXMLizer {

	protected static final String roleAttr = "r";
	protected static final String passwordAttr = "p";
	protected static final String descriptionAttr = "d";
	protected static final String guiVersionAttr = "v";
	protected static final String hashedAttr = "h";
	protected static final String pwExceptionWantedAttr = "ew";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.is.cs.cmd.CmdXMLizerNew#toXMLString(com.sap.sdm.is.cs.cmd
	 * .CmdIFNew)
	 */
	public String toXMLString(CmdIF cmd) {
		CmdLoginRequest cmdLR = (CmdLoginRequest) cmd;
		StringXMLizerFactory factory = StringXMLizerFactory.getInstance();
		List attributes = new ArrayList();
		List values = new ArrayList();
		if (cmdLR.getRole() != null) {
			attributes.add(roleAttr);
			values.add(cmdLR.getRole());
		}
		if (cmdLR.getPassword() != null) {
			attributes.add(passwordAttr);
			values.add(cmdLR.getPassword());
		}
		if (cmdLR.getDescription() != null) {
			attributes.add(descriptionAttr);
			values.add(cmdLR.getDescription());
		}
		if (cmdLR.getGuiVersion() != null) {
			attributes.add(guiVersionAttr);
			values.add(cmdLR.getGuiVersion());
		}
		attributes.add(hashedAttr);
		values.add((new Boolean(cmdLR.isHashed())).toString());
		attributes.add(pwExceptionWantedAttr);
		values.add((new Boolean(cmdLR.isPasswordExceptionWanted())).toString());
		String[] attributeArr = new String[attributes.size()];
		String[] valueArr = new String[values.size()];
		attributes.toArray(attributeArr);
		values.toArray(valueArr);
		StringXMLizer sXML = factory.createStringXMLizer(CmdLoginRequest.NAME,
				attributeArr, valueArr);

		sXML.endRootElem();
		return sXML.getString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.is.cs.cmd.CmdXMLizerNew#fromXMLString(java.lang.String)
	 */
	abstract public CmdIF fromXMLString(String xmlString)
			throws CmdReconstructionException;

}
