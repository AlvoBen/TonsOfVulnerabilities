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
package com.sap.engine.services.dc.cmd.telnet.impl.deploy;

import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipException;

import org.xml.sax.SAXException;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.lib.deploy.sda.SDAProducer;
import com.sap.engine.lib.deploy.sda.SDUChecker;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.dc.cm.CM;
import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.ErrorStrategyAction;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.cmd.telnet.impl.DCCommand;
import com.sap.engine.services.dc.cmd.telnet.impl.WrongParameterException;
import com.sap.engine.services.dc.cmd.telnet.impl.util.Argument;
import com.sap.engine.services.dc.cmd.telnet.impl.util.TelnetConstants;
import com.sap.engine.services.dc.cmd.telnet.impl.util.param.ParamConvertor;
import com.sap.engine.services.dc.event.DeploymentEvent;
import com.sap.engine.services.dc.event.DeploymentEventAction;
import com.sap.engine.services.dc.event.DeploymentListener;
import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.ListenerMode;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.engine.services.rmi_p4.P4IOException;

/**
 * Represents the deploy command implementation.
 * <p>
 * NOTE: This write operation needs to use <code>DCChangeLog</code>
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DeployerCommand extends DCCommand {

	// Optional params
	private final static String LIFE_CYCLE = "life_cycle";
	private final static String ON_PREREQUISITE_ERROR = "on_prerequisite_error";
	private final static String ON_DEPLOY_ERROR = "on_deploy_error";
	private final static String VERSION_RULE = "version_rule";
	private final static String WORKFLOW = "workflow";
	private final static String CORE_COMPONENTS = "core_components";

	private final static String CMD_NAME = "DEPLOY";

	private final static String SDA_EXTENSION = ".sda";
	private final static String SCA_EXTENSION = ".sca";

	private final static String helpMessage;
	static {
		helpMessage = Constants.EOL
				+ "   This command can be used for SDA and SCA deployment."
				+ Constants.EOL
				+ "The SDAs, the software type of which is "
				+ getServerOfflineSet()
				+ " will be deployed offline, but ones, the software type of which is "
				+ getServerOnlineSet()
				+ " will be deployed online."
				+ Constants.EOL
				+ "   The command can not be used with archives which do not comply with the SC/DC model"
				+ Constants.EOL
				+ Constants.EOL
				+ CMD_NAME
				+ " "
				+ TelnetConstants.VALUE_ENTRY
				+ TelnetConstants.OR
				+ TelnetConstants.LIST
				+ TelnetConstants.EQUATION
				+ TelnetConstants.VALUE_ENTRY
				+ Constants.EOL
				+ getSpaces(CMD_NAME)
				+ "["
				+ LIFE_CYCLE
				+ TelnetConstants.EQUATION
				+ getLifeCycleDeployStrategies()
				+ "]"
				+ Constants.EOL
				+ getSpaces(CMD_NAME)
				+ "["
				+ ON_DEPLOY_ERROR
				+ TelnetConstants.EQUATION
				+ getErrorStrategies()
				+ "]"
				+ Constants.EOL
				+ getSpaces(CMD_NAME)
				+ "["
				+ ON_PREREQUISITE_ERROR
				+ TelnetConstants.EQUATION
				+ getErrorStrategies()
				+ "]"
				+ Constants.EOL
				+ getSpaces(CMD_NAME)
				+ "["
				+ VERSION_RULE
				+ TelnetConstants.EQUATION
				+ getComponentVersionHandlingRules()
				+ "]"
				+ Constants.EOL
				+ getSpaces(CMD_NAME)
				+ "["
				+ WORKFLOW
				+ TelnetConstants.EQUATION
				+ getDeployWorkflowStrategies()
				+ "]"
				+ Constants.EOL
				+ Constants.EOL
				+ "WHERE"
				+ Constants.EOL
				+

				"   "
				+ TelnetConstants.VALUE_ENTRY
				+ TelnetConstants.EQUATION
				+ "Absolute pathname of an SDA or SCA for deployment or the absolute pathname of a"
				+ Constants.EOL
				+ "directory, which contains SDAs or SCAs. If an archive is specified then the"
				+ Constants.EOL
				+ "command with this parameter deploys exactly one archive. If a directory is"
				+ Constants.EOL
				+ "specified, all the files which are in this directory will be deployed except the"
				+ Constants.EOL
				+ "files which are in a subdirectory of this one."
				+ Constants.EOL
				+

				"   "
				+ TelnetConstants.LIST
				+ TelnetConstants.EQUATION
				+ "Absolute pathname of a file, which contains the absolute pathnames of SDAs"
				+ Constants.EOL
				+ "and/or SCAs for deployment, listed on separate lines. The command with this "
				+ Constants.EOL
				+ "parameter deploys one or many SDAs and/or SCAs simultaneously."
				+ Constants.EOL
				+ Constants.EOL
				+

				// version_rule
				"   "
				+ VERSION_RULE
				+ TelnetConstants.EQUATION
				+ "Determines the comparison of versions between a deployed SDA/SCA"
				+ Constants.EOL
				+ "and the new SDA/SCA. Possible values are:"
				+ Constants.EOL
				+ "     - lower - indicates that a deployed SDA/SCA can be updated only if its"
				+ Constants.EOL
				+ "version is lower than the SDA/SCA specified in the command. 'lower' is the"
				+ Constants.EOL
				+ "default value of this parameter, which means it is used automatically if the"
				+ Constants.EOL
				+ "parameter is not specified with the deploy command"
				+ Constants.EOL
				+ "     - same_lower - lets you deploy a version that has already been deployed"
				+ Constants.EOL
				+ "     - all - permits an update regardless of the version of the deployed SDA/SCA"
				+ Constants.EOL
				+ Constants.EOL
				+

				// on_deploy_error
				"   "
				+ ON_DEPLOY_ERROR
				+ TelnetConstants.EQUATION
				+ "An optional attribute that controls the response to an error. Possible values are: "
				+ Constants.EOL
				+ "     - stop - (default) stops the deployment after the deployment error. Any"
				+ Constants.EOL
				+ "missing SDAs/SCAs are not deployed "
				+ Constants.EOL
				+ "     - skip_depending - continue deployment after a deployment error but only"
				+ Constants.EOL
				+ "SDAs/SCAs that do not have dependencies on the deployment with the error."
				+ Constants.EOL
				+ "Currently the attribute is used for errors in actual deployment. For example, if"
				+ Constants.EOL
				+ "a specified component cannot be found, or if the dependencies cannot be resolved,"
				+ Constants.EOL
				+ "then the deployment component will be skipped"
				+ Constants.EOL
				+ Constants.EOL
				+

				// on_prerequisite_error
				"   "
				+ ON_PREREQUISITE_ERROR
				+ TelnetConstants.EQUATION
				+ "An optional attribute that controls the response to an error in a prerequisite check. Possible values are: "
				+ Constants.EOL
				+ "     - stop - (default) stops the deployment after the prerequisite check and"
				+ Constants.EOL
				+ "before the actual deployment"
				+ Constants.EOL
				+ "     - skip_depending - continue deployment after an error but only"
				+ Constants.EOL
				+ "SDAs/SCAs that do not have dependencies on that with the error. Currently the"
				+ Constants.EOL
				+ "attribute is used for errors in prerequisite check"
				+ Constants.EOL
				+ Constants.EOL
				+

				// life_cycle
				"   "
				+ LIFE_CYCLE
				+ TelnetConstants.EQUATION
				+ "An optional attribute that determines the way the Deploy "
				+ Constants.EOL
				+ "Controller deploys the components. Possible values are: "
				+ Constants.EOL
				+ "     - bulk - first delivers the components to the containers and after all the"
				+ Constants.EOL
				+ "components are delivered they are started in the same order they have been"
				+ Constants.EOL
				+ "delivered"
				+ Constants.EOL
				+ "     - sequential - delivers sequentially a component to the containers and then"
				+ Constants.EOL
				+ "starts it"
				+ Constants.EOL
				+ "     - disable - delivers the components to the containers without starting"
				+ Constants.EOL
				+ "the components"
				+ Constants.EOL
				+ Constants.EOL
				+

				// workflow
				"   "
				+ WORKFLOW
				+ TelnetConstants.EQUATION
				+ "An optional attribute that controls the behavior of the AS Java"
				+ Constants.EOL
				+ "during the deployment. Possible values are: "
				+ Constants.EOL
				+ "      - normal - the AS Java is restarted only once in case of offline"
				+ Constants.EOL
				+ "deployment"
				+ Constants.EOL
				+ "      - safety - the AS Java is restarted two times in case of offline "
				+ Constants.EOL
				+ "deployment. First, the AS Java is stopped and started in 'safe' mode with "
				+ Constants.EOL
				+ "action 'DEPLOY' and then after all the deployments have been performed, "
				+ Constants.EOL
				+ "it is restarted in 'normal' mode with action NONE"
				+ Constants.EOL
				+ "      - rolling - deprecated"
				+ Constants.EOL
				+

				Constants.EOL
				+

				// examples
				" Examples:"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " C:\\my\\app\\App1.sda on_deploy_error=stop"
				+ Constants.EOL
				+ "   "
				+ CMD_NAME
				+ " list=C:\\my\\list\\DeployItems.txt version_rule=all on_prerequisite_error=stop"

		;
	}

	public DeployerCommand(CM cm) {
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
		} catch (WrongParameterException ipEx) {
			println(ipEx);
			println(getDefaultHelpMessage());
		} catch (IOException ioEx) {
			println(ioEx);
		} catch (SAXException saxe) {
			println(saxe);
		} catch (DeployLibException dle) {
			println(dle);
		}
	}

	public void exec(Argument[] arguments) throws WrongParameterException,
			IOException, SAXException, DeployLibException {

		validate(arguments);

		final String sessionId = cm.generateSessionId();
		final String uploadDirName = cm.getUploadDirName(sessionId);

		final String sdusForDeploy[] = initSdusForDeploy(arguments,
				uploadDirName);

		final ComponentVersionHandlingRule verHandleRule = initComponentVersionHandlingRule(arguments);
		final ErrorStrategy deplHaldling = initErrorStrategy(arguments,
				ON_DEPLOY_ERROR);
		final ErrorStrategy preHandling = initErrorStrategy(arguments,
				ON_PREREQUISITE_ERROR);
		final DeployWorkflowStrategy workflowStrategy = initDeployWorkflowStrategy(
				arguments, WORKFLOW);
		final LifeCycleDeployStrategy lifeCycleDeployStrategy = initLifeCycleDeployStrategy(
				arguments, LIFE_CYCLE);

		final boolean onlineDeployOfCoreComponents = initCoreComponentsStrategy(arguments);

		if (onlineDeployOfCoreComponents && sdusForDeploy.length != 1) {
			throw new WrongParameterException(
					DCExceptionConstants.CMD_WRONG_ARG_VALUE,
					new String[] { "only single core component could be deployed online" });
		}

		final Deployer deployer = cm.getDeployer();

		deployer
				.setOnlineDeployemtOfCoreComponents(onlineDeployOfCoreComponents);
		DeploymentListener dplListener = new DeploymentListener() {

			public void deploymentPerformed(DeploymentEvent event)
					throws P4IOException, P4ConnectionException {
				final StringBuilder idStr = new StringBuilder();
				if (event.getDeploymentBatchItem().getSdu() != null) {
					SduId id = event.getDeploymentBatchItem().getSdu().getId();
					idStr.append(id.toString());
					if (id instanceof SdaId) {
						idStr.append(PROGRESS_SDA);
					} else if (id instanceof ScaId) {
						idStr.append(PROGRESS_SCA);
					} else {
						idStr.append(PROGRESS_NO_SDU);
					}
				} else {
					idStr.append(event.getDeploymentBatchItem()
							.getSduFilePath());
					idStr.append(PROGRESS_NO_SDU);
				}

				if (event.getDeploymentEventAction() == DeploymentEventAction.DEPLOYMENT_TRIGGERED) {
					printAndTraceDebug("Deploying [{0}] ...{1}", new Object[] {
							idStr, Constants.EOL });
				} else {
					printAndTraceDebug("Deployment of [{0}] finished.{1}",
							new Object[] { idStr, Constants.EOL });
				}
			}

			public int getId() {
				return this.hashCode();
			}

		};
		deployer.addDeploymentListener(dplListener, ListenerMode.LOCAL,
				EventMode.SYNCHRONOUS);

		if (verHandleRule != null) {
			deployer.setComponentVersionHandlingRule(verHandleRule);
		}
		if (deplHaldling != null) {
			deployer.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
					deplHaldling);
		}
		if (preHandling != null) {
			deployer
					.setErrorStrategy(
							ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
							preHandling);
		}
		if (workflowStrategy != null) {
			deployer.setDeployWorkflowStrategy(workflowStrategy);
		}
		if (lifeCycleDeployStrategy != null) {
			deployer.setLifeCycleDeployStrategy(lifeCycleDeployStrategy);
		}

		printDeployerSettings(deployer);
		printlnAndTraceDebug("Processing deployment operation, wait...");

		Collection dbItems4Result;
		try {
			printAndTraceDebug("{0}===== PROGRESS START ====={0}{0}",
					new Object[] { Constants.EOL });

			DeployResult dResult = dcChangeLog.deploy(deployer, sdusForDeploy,
					sessionId, VIA_TELNET);
			printAndTraceDebug("{0}===== PROGRESS END ====={0}",
					new Object[] { Constants.EOL });

			dbItems4Result = dResult.getDeploymentItems();
			printDeployResult(dbItems4Result);
			printlnAndTraceDebug("{0}",
					new Object[] { logSummary4Deploy(dResult
							.getDeploymentItems()) });

		} catch (DeploymentException dEx) {
			println(dEx);
			dbItems4Result = dEx.getDeploymentBatchItems();
			if (dbItems4Result != null && dbItems4Result.size() > 0) {
				printDeployResult(dbItems4Result);
				printlnAndTraceDebug("{0}",
						new Object[] { logSummary4Deploy(dbItems4Result) });

			} else {
				throw dEx;
			}
		}

	}

	private boolean initCoreComponentsStrategy(Argument[] arguments) {

		for (int i = 0; i < arguments.length; i++) {

			if (CORE_COMPONENTS.equalsIgnoreCase(arguments[i].getKey())) {

				final String strategy = arguments[i].getValue();
				if (strategy.equalsIgnoreCase("online")) {
					return true;
				} else {
					return false;
				}

			}
		}
		return false;

	}

	private void printDeployResult(Collection dbItems) {
		printAndTraceDebug("{0}===== DEPLOY RESULT ====={0}",
				new Object[] { Constants.EOL });

		printDeployResultLog(dbItems);

		printAndTraceDebug("{0}===== END DEPLOY RESULT ====={0}{0}",
				new Object[] { Constants.EOL });
	}

	private void printDeployResultLog(Collection dbItems) {
		final Iterator dbiIter = dbItems.iterator();
		DeploymentBatchItem dbItem;
		while (dbiIter.hasNext()) {
			dbItem = (DeploymentBatchItem) dbiIter.next();

			printlnAndTraceDebug("{0} {1}", new Object[] { Constants.EOL,
					dbItem.toString() });

			if (dbItem instanceof CompositeDeploymentItem) {
				printDeployResultLog(((CompositeDeploymentItem) dbItem)
						.getDeploymentItems());
			}
		}
	}

	private void printDeployerSettings(Deployer deployer) {
		printlnAndTraceDebug("{0}Deploy settings:",
				new Object[] { Constants.EOL });

		printlnAndTraceDebug("'   '{0}{1}{2}", new Object[] {
				LIFE_CYCLE,
				TelnetConstants.EQUATION,
				TelnetConstants.getTelnetConstantByDcConstant(deployer
						.getLifeCycleDeployStrategy().getName()) });

		printlnAndTraceDebug("'   '{0}{1}{2}", new Object[] {
				ON_DEPLOY_ERROR,
				TelnetConstants.EQUATION,
				TelnetConstants
						.getTelnetConstantByDcConstant(deployer
								.getErrorStrategy(
										ErrorStrategyAction.DEPLOYMENT_ACTION)
								.getName()) });

		printlnAndTraceDebug("'   '{0}{1}{2}", new Object[] {
				ON_PREREQUISITE_ERROR,
				TelnetConstants.EQUATION,
				TelnetConstants.getTelnetConstantByDcConstant(deployer
						.getErrorStrategy(
								ErrorStrategyAction.PREREQUISITES_CHECK_ACTION)
						.getName()) });

		printlnAndTraceDebug("'   '{0}{1}{2}", new Object[] {
				VERSION_RULE,
				TelnetConstants.EQUATION,
				TelnetConstants.getTelnetConstantByDcConstant(deployer
						.getComponentVersionHandlingRule().getName()) });

		printlnAndTraceDebug("'   '{0}{1}{2}", new Object[] {
				WORKFLOW,
				TelnetConstants.EQUATION,
				TelnetConstants.getTelnetConstantByDcConstant(deployer
						.getDeployWorkflowStrategy().getName()) });

		printlnAndTraceDebug(
				"{0}If there is an offline deployment, Telnet connection to host may be lost, but the result can be seen using [get_result] command{1}",
				new Object[] { Constants.EOL, Constants.EOL });
	}

	private String[] initSdusForDeploy(Argument[] arguments,
			String uploadDirName) throws WrongParameterException, IOException,
			SAXException, DeployLibException {
		String archivesForDeploy[] = initDeployItems(arguments);

		if (archivesForDeploy == null || archivesForDeploy.length == 0) {
			handleMissingArgument(TelnetConstants.FILE + TelnetConstants.OR
					+ TelnetConstants.LIST, getName());
		}

		ArrayList<String> sdusToBeCopied = new ArrayList<String>();
		ArrayList<String> sdusForDeploy = new ArrayList<String>();

		for (int i = 0; i < archivesForDeploy.length; i++) {
			if (archivesForDeploy[i].toLowerCase(Locale.ENGLISH).endsWith(
					SDA_EXTENSION)
					|| archivesForDeploy[i].toLowerCase(Locale.ENGLISH)
							.endsWith(SCA_EXTENSION)
					|| SDUChecker.check(archivesForDeploy[i])) {
				sdusToBeCopied.add(archivesForDeploy[i]);
			} else {
				String convertedSdu = this.convertJavaEEArchive2Sdu(
						archivesForDeploy[i], getTargetDir(uploadDirName));
				if (null != convertedSdu) {
					sdusForDeploy.add(convertedSdu);
				}
			}
		}

		String[] sdusToBeCopiedArray = new String[sdusToBeCopied.size()
				+ sdusForDeploy.size()];
		sdusToBeCopied.toArray(sdusToBeCopiedArray);
		copySdusForDeploy(sdusToBeCopiedArray, uploadDirName);

		int copiedSduFileCount = sdusToBeCopied.size();
		for (int i = 0; i < sdusForDeploy.size(); i++) {
			sdusToBeCopiedArray[copiedSduFileCount + i] = sdusForDeploy.get(i);
		}

		return sdusToBeCopiedArray;
	}

	/**
	 * Returns a SDU file path for a given archive. The archive may be of any
	 * standard JavaEE type - ear, war, jar, in which case it is converted to an
	 * SDA. If the given file path is not a valid JavaEE archiive, null is
	 * returned
	 * 
	 * @param archiveForDeploy
	 *            deployment archive which is converted to an SDA
	 * @param workDir
	 *            target folder into which the converted SDA is produced
	 * @return sdusForDeploy file path to a converted SDA, if
	 *         <code>archiveForDeploy</code> is a standard JavaEE archive, or
	 *         <code>null</code> in all other cases.
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws DeployLibException
	 * @throws SAPIllegalArgumentsException
	 * @throws DeployLibException
	 */
	private String convertJavaEEArchive2Sdu(String archiveForDeploy,
			String workDir) throws IOException, SAXException,
			DeployLibException {
		this.println("Converting " + archiveForDeploy + " to sda");
		SDAProducer maker = new SDAProducer(archiveForDeploy);
		maker.setWorkDir(workDir);
		try {
			maker.produce();
		} catch (ZipException ze) {
			println("WARNING: " + archiveForDeploy
					+ " is not a valid JavaEE archive and will be ignored.");
			return null;
		}
		this.println(archiveForDeploy
				+ " has been successfully converted to sda");
		return new String(maker.getDestinationFile());
	}

	/**
	 * Copies an array of SDU files to uploadDirName The method is mutator, i.e.
	 * it changes the given sdusForDeploy parameter
	 * 
	 * @param sdusForDeploy
	 *            an array of SDU files; it can contains null values at the end
	 * @param uploadDirName
	 * @return
	 * @throws IOException
	 */
	private void copySdusForDeploy(String[] sdusForDeploy, String uploadDirName)
			throws IOException {

		final ArrayList<File> filesToBeCopied = new ArrayList<File>();
		final ArrayList<File> filesCopied = new ArrayList<File>();

		for (int i = 0; i < sdusForDeploy.length; i++) {
			final String sduForDeploy = sdusForDeploy[i];
			if (null == sduForDeploy) {
				break;
			}
			checkFile(sduForDeploy);

			final File sduFileForDeploy = new File(sduForDeploy);
			filesToBeCopied.add(sduFileForDeploy);

			final String targetDir = getTargetDir(uploadDirName);
			sdusForDeploy[i] = targetDir + File.separator
					+ sduFileForDeploy.getName();
			filesCopied.add(new File(sdusForDeploy[i]));
		}

		// this is check for the case when the provided array
		// <code>sdusForDeploy</code> consists only of <code>null</code> values
		if (sdusForDeploy.length > 0 && null != sdusForDeploy[0]) {
			FileUtils.copyFile((File[]) filesToBeCopied
					.toArray(new File[filesToBeCopied.size()]),
					(File[]) filesCopied.toArray(new File[filesCopied.size()]));
		}
	}

	private void checkFile(String filePath) throws IOException {
		if (filePath == null) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003253 The file with path NULL cannot be processed.");
		}
		final File file = new File(filePath);
		if (!file.exists()) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003254 The file with path "
							+ filePath
							+ " does not exist or the server user has not rights to access it.");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException(
					"ASJ.dpl_dc.003255 The file with path " + filePath
							+ " is directory.");
		}
	}

	private String getTargetDir(String uploadDirName) {
		String targerDir = null;
		File target = null;
		do {
			targerDir = uploadDirName + File.separator + System.nanoTime();
			target = new File(targerDir);
		} while (target.exists() && target.isFile());
		return targerDir;
	}

	private void validate(Argument[] arguments) throws WrongParameterException {

		if (arguments == null || arguments.length < 1) {
			// no file or list arguments are specified
			throw new WrongParameterException(
					DCExceptionConstants.CMD_NO_ARGUMENT_SPECIFIED);
		}

		final boolean hasFirstElementFile = (arguments[0].getKey() == null);

		Set sArguments = new HashSet() {
			public boolean add(Object o) {
				if (contains(o)) {
					// duplicate element
					throw new IllegalArgumentException(
							"ASJ.dpl_dc.003257 Duplicate attribute specified: "
									+ o);
				}
				return super.add(o);
			}
		};

		for (int i = 0; i < arguments.length; i++) {
			String key = arguments[i].getKey();
			if (key != null) {
				sArguments.add(key.toLowerCase());
			}
		}

		// no file or list arguments are specified
		if (!(hasFirstElementFile || sArguments.contains(TelnetConstants.LIST))) {
			throw new WrongParameterException(
					DCExceptionConstants.CMD_NO_FILE_OR_LIST_ARG_SPEC);
		}

		// file and list arguments are specified
		if (hasFirstElementFile && sArguments.contains(TelnetConstants.LIST)) {
			throw new WrongParameterException(
					DCExceptionConstants.CMD_CANNOT_USER_TWO_PARAMS_TOGETHER,
					new String[] { TelnetConstants.VALUE_ENTRY,
							TelnetConstants.LIST, CMD_NAME });
		}
	}

	private void validateFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(
					"ASJ.dpl_dc.003260"
							+ filePath
							+ "' file does not exist or server user has no access to it."
							+ "\r\nHint: 1). Check given file path.");
		} else if (!file.canRead()) {
			throw new IOException("ASJ.dpl_dc.003261 '" + filePath
					+ "' file cannot be read by AS Java process."
					+ "\r\nHint: 1). Check AS Java permissions to read given file path.");
		}
	}

	private String[] initDeployItems(Argument[] arguments) throws IOException {
		// file to deploy
		if (arguments[0].getKey() == null) {
			validateFile(arguments[0].getValue());

			File file = new File(arguments[0].getValue());

			// directory to deploy
			if (file.isDirectory()) {
				File[] filesToDeploy = FileUtils.listFiles(file);
				String[] result = new String[filesToDeploy.length];
				for (int i = 0; i < filesToDeploy.length; i++) {
					result[i] = filesToDeploy[i].getAbsolutePath();
				}
				return result;
			}
			// single file to deploy
			return new String[] { arguments[0].getValue() };
		}

		// list of file to deploy
		Collection result = new ArrayList();
		for (int i = 0; i < arguments.length; i++) {
			if (TelnetConstants.LIST.equalsIgnoreCase(arguments[i].getKey())) {

				BufferedReader bufReader = null;
				try {
					bufReader = new BufferedReader(new InputStreamReader(
							new FileInputStream(arguments[i].getValue())));

					String sduFilePath = null;
					while ((sduFilePath = bufReader.readLine()) != null) {
						sduFilePath = sduFilePath.trim();
						if ("".equals(sduFilePath)) {
							continue;
						}

						validateFile(sduFilePath);

						result.add(sduFilePath);
					}
					return (String[]) result.toArray(new String[0]);
				} finally {
					if (bufReader != null) {
						bufReader.close();
					}
				}
			}
		}
		return null;
	}

	private ComponentVersionHandlingRule initComponentVersionHandlingRule(
			Argument[] arguments) throws WrongParameterException {
		for (int i = 0; i < arguments.length; i++) {
			if (VERSION_RULE.equalsIgnoreCase(arguments[i].getKey())) {
				final String versionHandling = TelnetConstants
						.getDcConstantByTelnetConstant(arguments[i].getValue());
				ComponentVersionHandlingRule verHandleRule = null;
				if (versionHandling != null) {
					verHandleRule = ComponentVersionHandlingRule
							.getComponentVersionHandlingRuleByName(versionHandling);
					if (verHandleRule == null) {
						handleWrongArgumentValue(VERSION_RULE, versionHandling,
								getComponentVersionHandlingRules());
					}
				}
				return verHandleRule;
			}
		}
		return null;
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
