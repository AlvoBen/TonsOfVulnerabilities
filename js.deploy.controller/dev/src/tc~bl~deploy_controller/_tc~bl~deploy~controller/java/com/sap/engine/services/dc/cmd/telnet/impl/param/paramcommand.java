/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cmd.telnet.impl.param;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.cm.params.ParamNotFoundException;
import com.sap.engine.services.dc.cm.params.ParamsException;
import com.sap.engine.services.dc.cm.params.ParamsFactory;
import com.sap.engine.services.dc.cm.params.ParamsFactoryException;
import com.sap.engine.services.dc.cmd.telnet.impl.DCCommand;
import com.sap.engine.services.dc.cmd.telnet.impl.util.Argument;
import com.sap.engine.services.dc.cmd.telnet.impl.util.TelnetConstants;
import com.sap.engine.services.dc.cmd.telnet.impl.util.param.ParamConvertor;
import com.sap.engine.services.dc.util.Constants;

/**
 * Writes and reads substitution variables
 * <p>
 * NOTE: This write operation needs to use <code>DCChangeLog</code>
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class ParamCommand extends DCCommand {

	// Obligatory params
	private final static String ADD = "-add";
	private final static String UPDATE = "-update";
	private final static String REMOVE = "-remove";
	private final static String LIST = "-list";

	// Optional params
	// TelnetConstants.NAME | TelnetConstants.VALUE

	private final static String CMD_NAME = "PARAM";
	private final static String helpMessage;
	static {
		helpMessage = "   This command can be used to add, list, update and remove a substitution"
				+ Constants.EOL
				+ "variable."
				+ Constants.EOL
				+ Constants.EOL
				+ CMD_NAME
				+ " "
				+ ADD
				+ TelnetConstants.OR
				+ LIST
				+ TelnetConstants.OR
				+ REMOVE
				+ TelnetConstants.OR
				+ UPDATE
				+ " "
				+

				TelnetConstants.BRACKET_OPEN
				+ TelnetConstants.VALUE_ENTRY
				+ TelnetConstants.BRACKET_OPEN
				+ TelnetConstants.EQUATION
				+ TelnetConstants.VALUE_ENTRY
				+ TelnetConstants.BRACKET_CLOSE
				+ TelnetConstants.BRACKET_CLOSE
				+ Constants.EOL
				+ "WHERE"
				+ Constants.EOL
				+ "   For the '"
				+ ADD
				+ "' and '"
				+ UPDATE
				+ "' options '"
				+ TelnetConstants.NAME
				+ "' and '"
				+ TelnetConstants.VALUE
				+ "' must be specified. "
				+ Constants.EOL
				+ "   For '"
				+ LIST
				+ "' option no other parameters must be specified."
				+ Constants.EOL
				+ "   For '"
				+ REMOVE
				+ "' option only the substitution variable's '"
				+ TelnetConstants.NAME
				+ "' must be"
				+ Constants.EOL
				+ "included."
				+ Constants.EOL
				+

				Constants.EOL
				+ " Examples:"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " "
				+ ADD
				+ " MyVar=MyValue"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " "
				+ ADD
				+ " MyVar2=MyValue2 MyVar3=MyValue3"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " "
				+ REMOVE
				+ " MyVar"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " "
				+ UPDATE
				+ " MyVar=MyNewValue";
	}

	public ParamCommand(CM cm) {
		this.cm = cm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.interfaces.shell.Command#exec(com.sap.engine.interfaces
	 * .shell.Environment, java.io.InputStream, java.io.OutputStream,
	 * java.lang.String[])
	 */
	public void exec(Environment environment, InputStream input,
			OutputStream output, String[] params) {
		init(output);

		try {
			logCommand(CMD_NAME, params);

			if (params != null && params.length >= 1) {
				final String action = params[0];
				final String pureParams[] = new String[params.length - 1];
				System.arraycopy(params, 1, pureParams, 0, params.length - 1);
				if (ADD.equals(action)) {
					addParams(pureParams);
				} else if (UPDATE.equals(action)) {
					updateParams(pureParams);
				} else if (REMOVE.equals(action)) {
					removeParams(pureParams);
				} else if (LIST.equals(action)) {
					listParams(pureParams);
				} else {
					println(getHelpMessage());
				}
			} else {
				println(getHelpMessage());
			}
		} catch (ParamNotFoundException pnfe) {
			println(pnfe);
		} catch (ParamsException pe) {
			println(pe);
		} catch (ParamsFactoryException pfe) {
			println(pfe);
		} catch (Exception e) {
			println(e);
		}
	}

	private void addParams(String[] params) throws ParamAlreadyExistsException,
			ParamsException, ParamsFactoryException {
		if (params != null && params.length > 0) {
			final Argument[] args = ParamConvertor.getArguments(params);
			final Param paramsForAdd[] = getParams(args);
			dcChangeLog.addParams(paramsForAdd, VIA_TELNET);
		} else {
			println(getHelpMessage());
		}
	}

	private void updateParams(String[] params) throws ParamNotFoundException,
			ParamsException, ParamsFactoryException {
		if (params != null && params.length > 0) {
			final Argument[] args = ParamConvertor.getArguments(params);
			final Param paramsForUpdate[] = getParams(args);
			dcChangeLog.updateParams(paramsForUpdate, VIA_TELNET);
		} else {
			println(getHelpMessage());
		}

	}

	private Param[] getParams(Argument[] args) {
		final Map nameValueMap = new HashMap();
		for (int i = 0; i < args.length; i++) {
			nameValueMap.put(args[i].getKey(), args[i].getValue());
		}
		return ParamsFactory.getInstance().createParams(nameValueMap);
	}

	private void removeParams(String[] params) throws ParamNotFoundException,
			ParamsException, ParamsFactoryException {
		if (params != null && params.length > 0) {
			Map nameValueMap = new HashMap();
			for (int i = 0; i < params.length; i++) {
				nameValueMap.put(params[i], "");
			}
			final Param paramsForRemove[] = ParamsFactory.getInstance()
					.createParams(nameValueMap);
			dcChangeLog.removeParams(paramsForRemove, VIA_TELNET);
		} else {
			println(getHelpMessage());
		}
	}

	private void listParams(String[] params) throws ParamsException,
			ParamsFactoryException {
		if (params == null || params.length == 0) {
			final Param allParams[] = ParamsFactory.getInstance()
					.createParamManager().getAllParams();
			if (allParams != null && allParams.length > 0) {
				for (int i = 0; i < allParams.length; i++) {
					printlnAndTraceDebug("{0}{1}{2}",
							new Object[] { allParams[i].getName(),
									TelnetConstants.EQUATION,
									allParams[i].getValue() });
				}
			} else {
				printlnAndTraceDebug(
						"There are no registered substitution variables.");
			}
		} else {
			println(getHelpMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getName()
	 */
	public String getName() {
		return CMD_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.interfaces.shell.Command#getHelpMessage()
	 */
	public String getHelpMessage() {
		return helpMessage;
	}

}
