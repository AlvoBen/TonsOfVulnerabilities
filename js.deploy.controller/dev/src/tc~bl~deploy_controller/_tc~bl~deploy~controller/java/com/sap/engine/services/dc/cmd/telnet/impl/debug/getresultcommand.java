/*
 * Created on 2006-1-13 by radoslav-i
 */
package com.sap.engine.services.dc.cmd.telnet.impl.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeployResultNotFoundException;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.session_id.SessionID;
import com.sap.engine.services.dc.cm.session_id.SessionIDException;
import com.sap.engine.services.dc.cm.session_id.SessionIDFactory;
import com.sap.engine.services.dc.cm.undeploy.UndeployResult;
import com.sap.engine.services.dc.cm.undeploy.UndeployResultNotFoundException;
import com.sap.engine.services.dc.cmd.telnet.impl.DCCommand;
import com.sap.engine.services.dc.cmd.telnet.impl.util.Argument;
import com.sap.engine.services.dc.cmd.telnet.impl.util.TelnetConstants;
import com.sap.engine.services.dc.cmd.telnet.impl.util.param.ParamConvertor;
import com.sap.engine.services.dc.manage.DCManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.repo.explorer.RepositoryExploringException;
import com.sap.engine.services.dc.util.Constants;

/**
 * Gets last active result from offline part of deploy controller repository
 * <p>
 * NOTE: This read operation doesn't need to use <code>DCChangeLog</code>
 * 
 * @author Radoslav Ivanov
 */
public class GetResultCommand extends DCCommand {
			
	private static final String CMD_NAME = "GET_RESULT";
	private static final String CMD_PARAM_HIDDEN = "-hidden";
	private static final String CMD_PARAM_STATISTICS = "-s";

	private static final String CMD_HELP_TEXT;
	static {
		CMD_HELP_TEXT = "   This command can be used for printing deployment or undeployment result by"
				+ Constants.EOL
				+ "session id, if the result for the specified session id was not removed by the garbage collector."
				+ Constants.EOL
				+ Constants.EOL
				+ CMD_NAME
				+ " "
				+ TelnetConstants.BRACKET_OPEN
				+ "sessionId"
				+ TelnetConstants.BRACKET_CLOSE
				+ Constants.EOL
				+ "WHERE"
				+ Constants.EOL
				+ "   "
				+ TelnetConstants.BRACKET_OPEN
				+ "sessionId"
				+ TelnetConstants.BRACKET_CLOSE
				+ " Specifies the session id for a particular deployment or "
				+ Constants.EOL
				+ "undeployment process except the active one, if the Deploy Controller is in process in order to "
				+ Constants.EOL
				+ "prevent failures oof the operation. If no sessionId is specified then the active one is used."
				+ Constants.EOL
				+ "   "
				+ CMD_PARAM_STATISTICS
				+ " "
				+ TelnetConstants.EQUATION + " shows deployment statistics.";
	}

	public GetResultCommand(CM cm) {
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

			final Argument[] args = ParamConvertor.getArguments(params);

			exec(args);
		} catch (RepositoryExploringException ree) {
			println(ree);
		} catch (CMException cme) {
			println(cme);
		} catch (Exception e) {
			println(e);
		}
	}

	private void exec(Argument[] arguments) throws CMException, IOException,
			SessionIDException, ConfigurationException, NullPointerException {

		String sessionId = null;

		final SessionID activeSessionID = SessionIDFactory.getInstance()
				.getSessionIDStorageManager().loadActiveSessionID(
						ServiceConfigurer.getInstance()
								.getConfigurationHandler());

		boolean isHiddenOptionUsed = false;
		boolean isStatisticsOptionUsed = false;

		if ((arguments == null || arguments.length < 1)) {
			sessionId = activeSessionID.toString();
		} else {
			for (int i = 0; i < arguments.length; i++) {
				if (CMD_PARAM_HIDDEN.equalsIgnoreCase(arguments[i].getValue())) {
					isHiddenOptionUsed = true;
				} else if (CMD_PARAM_STATISTICS.equalsIgnoreCase(arguments[i]
						.getValue())) {
					isStatisticsOptionUsed = true;
				}
			}
		}

		if (isHiddenOptionUsed) {
			sessionId = activeSessionID.toString();
		} else if (sessionId == null) {
			sessionId = arguments[0].getValue();
		}

		if (!(isHiddenOptionUsed || DCManager.getInstance().isInWorkingMode())
				&& activeSessionID.getID().equals(sessionId)) {
			printlnAndTraceDebug(
					"Active session id must not be specified during work - [{0}]. For more details use -h option for help.",
					new Object[] { sessionId });
			return;
		}

		DeployResult deployResult = null;
		UndeployResult undeployResult = null;

		try {
			deployResult = cm.getDeployer().getDeployResult(sessionId);
		} catch (DeployResultNotFoundException drnfe) {// $JL-EXC$
		}

		if (deployResult != null) {
			if (isStatisticsOptionUsed) {
				printStatistics(deployResult);
			} else {
				printReslut(deployResult);
			}
			return;
		}

		try {
			undeployResult = cm.getUndeployFactory().createUndeployer()
					.getUndeployResult(sessionId);
		} catch (UndeployResultNotFoundException drnfe) {// $JL-EXC$
		}

		if (undeployResult != null) {
			printReslut(undeployResult);
			return;
		}

		printlnAndTraceDebug(
				"No result found for session id [{0}].",
				new Object[] { sessionId });
	}

	private void printStatistics(DeployResult result) {
		final StringBuilder sb = new StringBuilder();
		buildStatistics(sb.append(Constants.EOL + "DeploymentItems = "), result
				.getDeploymentItems());
		buildStatistics(sb.append(Constants.EOL + Constants.EOL
				+ "SortedDeploymentBatchItems = "), result
				.getSortedDeploymentBatchItems());

		printlnAndTraceDebug("{0}", new Object[] { sb.toString() });
	}

	private void buildStatistics(StringBuilder sb,
			Collection<DeploymentBatchItem> dbis) {
		if (dbis == null) {
			return;
		}
		final Iterator<DeploymentBatchItem> dbisIter = dbis.iterator();
		final Map<SoftwareType, Integer> softwareType2Integer = new HashMap<SoftwareType, Integer>();
		while (dbisIter.hasNext()) {
			processDBI(softwareType2Integer, (DeploymentBatchItem) dbisIter
					.next());
		}

		sb.append(softwareType2Integer.toString());
	}

	private void processDBI(Map<SoftwareType, Integer> softwareType2Integer,
			DeploymentBatchItem dbi) {
		SoftwareType st = null;
		Integer i = null;
		if (dbi instanceof DeploymentItem) {
			final DeploymentItem di = (DeploymentItem) dbi;
			st = di.getSda().getSoftwareType();
			i = softwareType2Integer.get(st);
			if (i == null) {
				i = new Integer(0);
			}
			i += 1;
			softwareType2Integer.put(st, i);
		} else if (dbi instanceof CompositeDeploymentItem) {
			final CompositeDeploymentItem cdi = (CompositeDeploymentItem) dbi;
			final Collection cdiIds = cdi.getDeploymentItems();
			final Iterator cdiIdsIter = cdiIds.iterator();
			while (cdiIdsIter.hasNext()) {
				processDBI(softwareType2Integer,
						(DeploymentBatchItem) cdiIdsIter.next());
			}
		}
	}

	/**
	 * @param result
	 */
	private void printReslut(DeployResult result) {
		printlnAndTraceDebug("{0}", new Object[] { result.toString() });
		printlnAndTraceDebug("{0}", new Object[] { result.getSortedDeploymentBatchItems() });
	}

	private void printReslut(UndeployResult result) {
		printlnAndTraceDebug("{0}", new Object[] { result.toString() });
		printlnAndTraceDebug("{0}", new Object[] { result.getUndeployItems() });
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
		return CMD_HELP_TEXT;
	}
}
