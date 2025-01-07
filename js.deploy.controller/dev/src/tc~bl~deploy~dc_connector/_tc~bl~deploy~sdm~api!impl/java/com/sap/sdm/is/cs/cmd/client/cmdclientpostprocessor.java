package com.sap.sdm.is.cs.cmd.client;

import java.util.HashMap;
import java.util.Map;

import com.sap.sdm.is.cs.cmd.CmdConnectionHandler;
import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * Title: Software Deployment Manager
 * 
 * Description: The class is used for commands post processing. It registers all
 * its successors defines operation, which triggers the post process. Every
 * successor is registered during its creation automaticaly.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-11-19
 * 
 * @author dimitar-d
 * @version 1.0
 * @since 6.40
 * 
 */
public abstract class CmdClientPostProcessor {

	private static final Map postProcessorsRegistry = new HashMap();

	/**
	 * Register a post processor for the specified command.
	 * 
	 * @param cmdName
	 *            specifies the command with which the post processor will be
	 *            mapped.
	 */
	public CmdClientPostProcessor(String cmdName) {
		postProcessorsRegistry.put(cmdName, this);
	}

	/**
	 * The operation receives a command which has been processed (request), a
	 * command, which is the result of the requested command processing
	 * (response) and depending from their type the post processor has to decide
	 * how the business logic will continue.
	 * 
	 * @param request
	 *            <code>CmdIF</code> specifies the requested command.
	 * @param response
	 *            <code>CmdIF</code> specifies the response command generated
	 *            after requested command processing.
	 * @param connHandler
	 *            <code>CmdConnectionHandler</code> specifies the handler which
	 *            consists of operations for receiving and sending command
	 *            through the network.
	 * @return <code>CmdIF</code> the result of post processing. If there is no
	 *         post processor registered for the specified request command the
	 *         specified command as a response is returned.
	 */
	public static CmdIF process(CmdIF request, CmdIF response,
			CmdConnectionHandler connHandler) {
		final CmdClientPostProcessor postProcessor = (CmdClientPostProcessor) postProcessorsRegistry
				.get(request.getMyName());
		if (postProcessor != null) {
			return postProcessor.doProcess(request, response, connHandler);
		}
		return response;
	}

	/**
	 * This operation is invoked by the
	 * <code>CmdClientPostProcessor.process()</code> after the post processor is
	 * get.
	 * 
	 * @param request
	 *            <code>CmdIF</code> specifies the requested command.
	 * @param response
	 *            <code>CmdIF</code> specifies the response command generated
	 *            after requested command processing.
	 * @param connHandler
	 *            <code>CmdConnectionHandler</code> specifies the handler which
	 *            consists of operations for receiving and sending command
	 *            through the network.
	 * @return <code>CmdIF</code> the result of post processing.
	 */
	protected abstract CmdIF doProcess(CmdIF request, CmdIF response,
			CmdConnectionHandler connHandler);

}
