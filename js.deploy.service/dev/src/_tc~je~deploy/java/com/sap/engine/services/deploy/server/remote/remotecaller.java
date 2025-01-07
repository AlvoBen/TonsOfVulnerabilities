/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.server.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MessageContext;
import com.sap.engine.frame.cluster.message.MessageListener;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.cluster.message.PartialResponseException;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.LocalDeployment;
import com.sap.engine.services.deploy.server.ObjectSerializer;
import com.sap.engine.services.deploy.server.remote.RemoteCommandFactory.RemoteCommand;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.engine.system.ThreadWrapperExt;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class used by Deploy service for remote communication between different
 * server nodes. It also implements MessageListener interface, to be able to
 * receive messages from other server nodes.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Monika Kovachka, Rumiana Angelova
 * @version
 */
public final class RemoteCaller implements MessageListener {
	private static final int SEND_TIMEOUT = 600000;
	private static final int RESEND = 5;
	private static final long SLEEP_BEFORE_RESEND = 60 * 1000;
	private static final int SEND_TO_ALL_EXCEPT_CURRENT = -1;

	public static final String COMMAND = "command";
	public static final String APP_NAME = "application_name";

	private static final Location location = 
		Location.getLocation(RemoteCaller.class);

	private final LocalDeployment localDeployment;
	private final ClusterMonitorHelper cmHelper;
	private final MessageContext messageCtx;

	/**
	 * The constructor.
	 * @param cmHelper cluster monitor helper. Not null.
	 * @param localDeployment local deployment. Not null.
	 * @param messageCtx message context. Not null.
	 */
	public RemoteCaller(final ClusterMonitorHelper cmHelper,
		final LocalDeployment localDeployment,
		final MessageContext messageCtx) {
		this.cmHelper = cmHelper;
		this.localDeployment = localDeployment;
		this.messageCtx = messageCtx;
	}
	
	/**
	 * Lists applications from a container on a list of remote server nodes.
	 * @param containerName container name.
	 * @param serverIDs int array of server IDs.
	 * @param onlyJ2ee lists only J@EE applications when this flag is true.
	 * @return array of application names. Not null, but can be empty.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	@SuppressWarnings("unchecked")
	public String[] listAppsRemotely(final String containerName,
		final int[] serverIDs, final boolean onlyJ2ee) 
			throws DeploymentException {
		final RemoteCommand cmd = 
			RemoteCommandFactory.createListAppsCmd(containerName, onlyJ2ee);
		final MessageResponse[] responses = sendAndWait(cmd, serverIDs);
		final Set<String> result = new HashSet<String>();
		for(final MessageResponse response : responses) {
			final Set<String> apps = (Set<String>) response.getResponse();  
			if(apps != null) {
				result.addAll(apps);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Lists applications from a container on a list of remote server nodes,
	 * together with theirs statuses and eventually status descriptions.
	 * @param containerName container name.
	 * @param serverIDs int array of server IDs.
	 * @param onlyJ2ee 
	 * @return array of responses, where every response contains map of 
	 * application names to their statuses.
	 * @see RemoteCommandFactory#
	 * 		createListAppsAndStatusCmd(String, boolean, boolean)
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	public MessageResponse[] listAppsAndStatusesRemotely(
		final String containerName, final int[] serverIDs,
		final boolean onlyJ2ee, final boolean withStatusDescription)
		throws DeploymentException {

		final RemoteCommand cmd = 
			RemoteCommandFactory.createListAppsAndStatusCmd(
				containerName, onlyJ2ee, withStatusDescription);
		return sendAndWait(cmd, serverIDs);
	}

	/**
	 * Lists application elements from a container on a list of remote server
	 * nodes.
	 * @param container container name.
	 * @param application application name.
	 * @param serverIDs server IDs of the servers where we need to perform this
	 * operation. 
	 * @return array elements. Not null.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	public String[] listElementsRemotely(final String container,
		final String application, final int[] serverIDs) 
		throws DeploymentException {
		
		final RemoteCommand cmd =
			RemoteCommandFactory.createListElementsCmd(container, application);
		final MessageResponse[] responses = 
			sendAndWait(cmd, serverIDs);
		final List<String> allElements = new ArrayList<String>();
		for(final MessageResponse response : responses) {
			final String[] elements = (String[])response.getResponse();
			if(elements != null) {
				for(String element : elements) {
					allElements.add(element);
				}
			}
		}
		return allElements.toArray(new String[allElements.size()]);
	}

	/**
	 * Lists containers on a list of remote server nodes.
	 * @param serverIDs int array of server IDs.
	 * @return array of container names. Not null.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	public String[] listContainersRemotely(int[] serverIDs)
		throws DeploymentException {
		final RemoteCommand cmd = 
			RemoteCommandFactory.createListContainersCmd();
		final MessageResponse[] responses = sendAndWait(cmd, serverIDs);
		final Set<String> containers = new HashSet<String>();
		for(final MessageResponse response : responses) {
			for(final String containerName : (String[])response.getResponse()) {
				containers.add(containerName);
			}
		}
		return containers.toArray(new String[containers.size()]);
	}

	/**
	 * Returns application status on a remote server node.
	 * @param appName application name. Not null.
	 * @param id valid server ID.
	 * @return application status. Can be null if there was exception during
	 * the invocation of the command on the remote server node. 
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	public String getApplicationStatusRemotely(final String appName,
		final int id) throws DeploymentException {
		assert appName != null;
		final RemoteCommand cmd =
			RemoteCommandFactory.createAppStatusCmd(appName);
		final MessageResponse[] res = sendAndWait(cmd, new int[] { id });
		return (res != null && res.length == 1) ? 
			(String)res[0].getResponse() : null;
	}

	/**
	 * Returns application status description on a remote server node.
	 * @param appName application name. Not null.
	 * @param serverId server ID of a node in the cluster.
	 * @return the status description.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	@SuppressWarnings("boxing")
	public
	StatusDescription getApplicationStatusDescriptionRemotely(
		final String appName, final int serverId) throws DeploymentException {
		assert appName != null;
		final RemoteCommand cmd =
			RemoteCommandFactory.createAppStatusDescrCmd(appName);
		StatusDescription res = null;
		try {
			final byte[] msg = ObjectSerializer.getByteArray(cmd);
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"SendAndWaitForAnswer ID = [{0}] cmd [{1}]", serverId, cmd);
			}
			final MessageAnswer answer = messageCtx.sendAndWaitForAnswer(
				serverId, 1, msg, 0, msg.length, SEND_TIMEOUT);
			MessageResponse response = extractResponse(answer, serverId);
			res = (StatusDescription)response.getResponse();
		} catch (Exception ex) {
			processCannotSendMessage(ex, "(ID=" + serverId + ")",
				"error occurred while sending message to other cluster elements");
		}
		return res;
	}

	/**
	 * Sends one-way message to remote server nodes with the remote command 
	 * which has to be executed. Currently only parallel commands are sent in
	 * this way.
	 * @param cmd remote command to be executed on the remote server nodes.
	 * @param receiverIDs server IDs, where the command has to be sent.
	 * @throws ServerDeploymentException
	 */
	@SuppressWarnings("boxing")
	private void send(final RemoteCommand cmd, final int[] receiverIDs) 
		throws ServerDeploymentException {
		final int[] eligibles = cmHelper.filterEligibleReceivers(receiverIDs);
		if (eligibles.length == 0) {
			return;
		}
		final String to = "(ALL)";
		final byte[] msg;
		try {
			msg = ObjectSerializer.getByteArray(cmd);
		} catch (IOException ex) {
			processCannotSendMessage(ex, to,
				"convertion into byte array threw exception");
			return;
			// Ends with error, because cannot fail over.
		}
		if (eligibles.length == cmHelper.findServers().length - 1) {
			// We have to sent the command to all server nodes.
			try {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Send (ALL) cmd [{0}]", cmd);
				}
				messageCtx.send(SEND_TO_ALL_EXCEPT_CURRENT,
					ClusterElement.SERVER, 1, msg, 0, msg.length);
			} catch (ClusterException clEx) {
				processCannotSendMessage(clEx, to,
					"the send method of message context threw exception");
			}
		} else {
			for (int i = 0; i < eligibles.length; i++) {
				for (int j = 1; j <= RESEND; j++) {
					// For fail over.
					try {
						if (location.beDebug()) {
							SimpleLogger.trace(Severity.DEBUG, location, null,
								"Send [{0}] ID =[{1}] cmd [{2}]", j,
								eligibles[i], cmd);
						}
						messageCtx.send(eligibles[i], 1, msg, 0, msg.length);
						break;
						// Successfully sent.
					} catch (ClusterException clEx) {
						processCannotSendMessage(clEx,
							"(ID=" + eligibles[i] + ")",
							"the send method of message context threw exception");
						sleepBeforeResend();
					}
				}
			}
		}
	}

	/**
	 * Sends a remote command regarding an application to remote server nodes.
	 * @param cmd the remote command to be executed on the remote server node.
	 * Not null.
	 * @param receiverIDs server node IDs of the receivers of the given remote
	 * command.
	 * @param appName
	 *            application name.
	 * 
	 * @return array of transaction statistics.
	 * 
	 * @throws DeploymentException
	 *             if a problem occurs during the process.
	 */
	@SuppressWarnings("boxing")
	public MessageResponse[] sendAndWait(
		final RemoteCommand cmd, final int[] receiverIDs) 
		throws DeploymentException {
		assert cmd != null;

		final int[] eligibles = cmHelper.filterEligibleReceivers(receiverIDs);
		if (eligibles.length == 0) {
			return new MessageResponse[0];
		}
		
		if (eligibles.length == cmHelper.findServers().length - 1) {
			return sendToAllAndWait(cmd);
		}
		final byte[] msg;
		try {
			msg = ObjectSerializer.getByteArray(cmd);
		} catch (IOException ex) {			
			throw new ServerDeploymentException(
				ExceptionConstants.CANNOT_SEND_MESSAGE, new String[] {
					Arrays.toString(eligibles),
					"cannot convert " + cmd + " into byte array" }, ex);
		}
		MessageAnswer answer = null;
		final MessageResponse[] responses = 
			new MessageResponse[eligibles.length];

		for (int i = 0; i < eligibles.length; i++) {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"SendAndWaitForAnswer ID = [{0}] cmd [{1}]",
					eligibles[i], cmd);
			}
			try {
				long start = System.currentTimeMillis();
				long cpuStartTime = SystemTime.currentCPUTimeUs();
				final String tagName = "Cluster Communication Duration";
				try {
					Accounting.beginMeasure(tagName, messageCtx.getClass());
					answer = messageCtx.sendAndWaitForAnswer(
						eligibles[i], 1, msg, 0, msg.length, SEND_TIMEOUT);
				} finally {
					Accounting.endMeasure(tagName);
					TransactionTimeStat.addDeploySubOperation(
						"Cluster Communication Duration",
						new DeployOperationTimeStat("Send to "
							+ eligibles[i], start, System.currentTimeMillis(), 
							cpuStartTime, SystemTime.currentCPUTimeUs()));
				}
				responses[i] = extractResponse(answer, eligibles[i]);
			} catch (ClusterException clex) {
				final ServerDeploymentException sde = 
					new ServerDeploymentException(
						ExceptionConstants.CANNOT_SEND_MESSAGE,
						new String[] { Arrays.toString(eligibles),
							"of general error" }, clex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
					sde.getLocalizedMessage(), sde);
				responses[i] = new MessageResponse(
					eligibles[i], sde.getLocalizedMessage());
			}
		}
		return responses;
	}

	private MessageResponse[] sendToAllAndWait(final RemoteCommand cmd)
		throws DeploymentException {
		final byte[] msg;
		try {
			msg = ObjectSerializer.getByteArray(cmd);
		} catch (IOException ex) {
			throw new ServerDeploymentException(
				ExceptionConstants.CANNOT_SEND_MESSAGE, new String[] {
				"(ALL)", "cannot convert " + cmd + " into byte array" },
				ex);
		}
		MultipleAnswer m_answer = null;
		try {
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"SendAndWaitForAnswer (ALL) table [{0}]", cmd);
			}
			long start = System.currentTimeMillis();
			long cpuStartTime = SystemTime.currentCPUTimeUs();
			// Send to all, excluding the current server.
			m_answer = messageCtx.sendAndWaitForAnswer(
				SEND_TO_ALL_EXCEPT_CURRENT, ClusterElement.SERVER, 1, 
				msg, 0, msg.length, SEND_TIMEOUT);
			TransactionTimeStat.addDeploySubOperation(
				"Cluster Communication Duration",
				new DeployOperationTimeStat("Send to All", start, 
					System.currentTimeMillis(), cpuStartTime, 
					SystemTime.currentCPUTimeUs()));
		} catch (PartialResponseException ex) {
			m_answer = ex.getPartialResponse();
		} catch (ClusterException ex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_SEND_MESSAGE, new String[] {
					"(ALL)", "of general error" }, ex);
			sde.setMessageID("ASJ.dpl_ds.005025");
			throw sde;
		}
		if (m_answer == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_SEND_MESSAGE,
				"(ALL)", "of general error");
			sde.setMessageID("ASJ.dpl_ds.005025");
			throw sde;
		}
		int[] participants = m_answer.participants();
		MessageResponse[] responses = new MessageResponse[participants.length];
		MessageAnswer answer = null;
		for (int i = 0; i < participants.length; i++) {
			try {
				answer = m_answer.getAnswer(participants[i]);
				responses[i] = extractResponse(answer, participants[i]);
			} catch (ClusterException clex) {
				final ServerDeploymentException sde = 
					new ServerDeploymentException(
						ExceptionConstants.CANNOT_SEND_MESSAGE, 
						new String[] { "(ALL)", "of general error" }, clex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
					sde.getLocalizedMessage(), sde);
				responses[i] = new MessageResponse(
					participants[i], sde.getLocalizedMessage());
			}
		}
		return responses;
	}

	@SuppressWarnings( { "boxing"})
	private MessageResponse extractResponse(final MessageAnswer answer, 
		final int senderId) {
		assert answer != null;
		MessageResponse response;
		try {
			response = (MessageResponse) ObjectSerializer.getObject(
				answer.getMessage(), answer.getOffset(), answer.getLength());
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Received response from server node [{0}] : [{1}]",
				senderId, response);
		} catch (Exception ex) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_READ_ANSWER,
				new Object[] { senderId }, ex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
				sde.getLocalizedMessage(), sde);
			response = new MessageResponse(senderId, DUtils.getStackTrace(ex));
		}
		return response;
	}

	/**
	 * Notifies remote server nodes about a transaction.
	 * @param cmd the command to be sent.
	 * @param wait shows the way of performing the notification.
	 * @param receivers the server IDs of the receivers.
	 * @return array of transaction statistics.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	public MessageResponse[] notifyRemotely(
		final Map<String, Object> cmdMap, final int[] receivers,
		final boolean wait) throws DeploymentException {
		assert receivers != null;
		if (receivers.length == 0) {
			return null;
		}
		if (location.beDebug()) {
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"Transaction [{0}] with application [{1}], " +
				"will notify remotely [{2}] for [{3}]",
				cmdMap.get(COMMAND), cmdMap.get(APP_NAME), 
				CAConvertor.toString(receivers, ""), 
				CAConvertor.toString(cmdMap, ""));
		}
		final RemoteCommand cmd = 
			RemoteCommandFactory.createMakeTransactionCmd(
				cmdMap, cmHelper.getCurrentServerId());
		if (wait) {
			return sendAndWait(cmd, receivers);
		}
		send(cmd, receivers);
		return null;
	}

	/**
	 * Sends response to the initiator of an parallel transaction (start or
	 * stop).
	 * @param appName application name.
	 * @param transactionType the transaction type.
	 * @param toClusterID server ID.
	 * @param warnings String array of warnings.
	 * @param errors String array of errors.
	 * @throws ServerDeploymentException
	 */
	@SuppressWarnings("boxing")
	public void sendRespond(String appName, String txType,
		int toClusterId, String[] warnings, String[] errors)
			throws ServerDeploymentException {

		final RemoteCommand cmd = RemoteCommandFactory.createRespondCmd(
			cmHelper.getCurrentServerId(), appName, txType, warnings, errors);
		if (cmHelper.isCommunicationDisabled()) {
			return;
		}

		final String to = "(ID=" + toClusterId + ")";
		final byte[] msg;
		try {
			msg = ObjectSerializer.getByteArray(cmd);
		} catch (IOException ex) {
			// Ends with error, because cannot fail over.
			processCannotSendMessage(ex, to,
				"convertion into byte array threw exception");
			return;
		}

		for (int i = 1; i <= RESEND; i++) {
			// For fail over.
			try {
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"Send [{0}] [{1}] command [{2}]", i, to, cmd);
				}
				messageCtx.send(toClusterId, 1, msg, 0, msg.length);
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						" * sent [{0}] OK", to);
				}
				break;
				// Ends with OK, because successfully sent.
			} catch (ClusterException clEx) {
				processCannotSendMessage(clEx, to,
					"the send method of message context threw exception");
				sleepBeforeResend();
			}
		}
	}

	
	private void processCannotSendMessage(final Throwable th, 
		final String to, final String because) 
		throws ServerDeploymentException {
		final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_SEND_MESSAGE, new String[] { to,
						because }, th);
		throw sde;
	}

	private void sleepBeforeResend() {
		try {
			Thread.sleep(SLEEP_BEFORE_RESEND);
		} catch (InterruptedException e) {
			// $JL-EXC$ - continue
		}
	}

	/**
	 * Receives one-way message from a sender, which contains the remote
	 * command to be executed. The command is executed locally. It is
	 * responsibility of the command to send back his response in case of
	 * exception.
	 * @param senderId the id of the server node which sent the message.
	 * @param messageType the type of the message. Currently this parameter is
	 * not used.
	 * @param message the body of the message.
	 * @param offset the start offset of the message body.
	 * @param length the length of the message body.
	 * 
	 * @see com.sap.engine.frame.cluster.message.MessageListener#
	 * 		receive(int, int, byte[], int, int)
	 */
	public void receive(final int senderId, final int messageType, 
		final byte[] message, final int offset, final int length) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
			getClass().getClassLoader());
		try {
			RemoteCommand cmd;
			try {
				cmd = retrieveRemoteCommand(message, offset, length, senderId);
			} catch (ServerDeploymentException ex) {
				// Exception is already logged in retrieveRemoteCommand().
				return;
			}
			ThreadWrapperExt.setTransactionId(cmd.getTransactionId());
			try {
				cmd.execute(localDeployment, cmHelper.getCurrentServerId());
			} catch (Exception ex) {
				final ServerDeploymentException sdex =
					new ServerDeploymentException(
						ExceptionConstants.COMPLEX_ERROR,
						new String[] { "Cannot execute command: " + cmd }, ex);
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					sdex.getLocalizedMessage(), ex);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	/**
	 * Receives send-response message.
	 * @param senderId the id of the server node which send the message.
	 * @param messageId the type of the message.
	 * @param message the body of the message.
	 * @param offset the start offset of the message body.
	 * @param length the length of the message body.
	 * 
	 * @return a <code>MessageAnswer</code> object representing the serialized
	 * answer. It MUST NOT be <code>null</code>.
	 * @see com.sap.engine.frame.cluster.message.MessageListener
	 * 			#receiveWait(int, int, byte[], int, int)
	 */
	public MessageAnswer receiveWait(int senderId, int messageId,
		byte[] message, int offset, int length) {
		assert message != null;
		final int currentId = cmHelper.getCurrentServerId();
		final ClassLoader loader = 
			Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
			getClass().getClassLoader());
		try {
			final RemoteCommand cmd;
			try {
				cmd = retrieveRemoteCommand(message, offset, length, senderId); 
			} catch (ServerDeploymentException ex) {
				return prepareAnswer(new MessageResponse(
					currentId, ex.getLocalizedMessage()), senderId);
			}
			try {
				return prepareAnswer(
					cmd.execute(localDeployment, currentId), senderId);
			} catch (Exception ex) {
				return prepareAnswer(new MessageResponse(currentId, 
					"Error during execution of " + cmd + CAConstants.EOL + 
					ex.getLocalizedMessage()), senderId);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	@SuppressWarnings("boxing")
	private MessageAnswer prepareAnswer(final MessageResponse response,
		final int senderId) {
		final MessageAnswer answ = new MessageAnswer();
		byte[] msg;
		try {
			msg = ObjectSerializer.getByteArray(response);
			SimpleLogger.trace(Severity.DEBUG, location, null,
				"New message response [{0}] is created " +
				"on server node {1} and will be returned to the caller {2}",
				response, cmHelper.getCurrentServerId(), senderId);
		} catch (IOException ex) {
			final ServerDeploymentException sdex = 
				new ServerDeploymentException(
					ExceptionConstants.COMPLEX_ERROR, new String[] { 
						"Fatal error - no way to recover, because " +
						"cannot serialize message response."}, ex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, 
				sdex.getLocalizedMessage(), ex);
			msg = new byte[0];
		}
		answ.setMessage(msg);
		answ.setOffset(0);
		answ.setLength(msg.length);
		return answ;
	}
	/**
	 * Retrieves the remote command. 
	 * @param message
	 * @param offset
	 * @param length
	 * @param senderId
	 * @return the retrieved remote command. Not null. 
	 * @throws ServerDeploymentException in case of deserialization exception - 
	 * IOException or ClassNotFoundException.
	 */
	@SuppressWarnings("boxing")
	private RemoteCommand retrieveRemoteCommand(final byte[] message, 
		final int offset, final int length, final int senderId) 
		throws ServerDeploymentException {
		try {
			final RemoteCommand cmd = (RemoteCommand)ObjectSerializer.getObject(
				message, offset, length);
			if(location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Remote command {0} from {1} is received on {2}", cmd, 
					senderId, cmHelper.getCurrentServerId());
			}
			return cmd;
		} catch (IOException ex) {
			throw handleDeserializationException(offset, length, ex);
		} catch (ClassNotFoundException ex) {
			throw handleDeserializationException(offset, length, ex);
		}
	}


	private ServerDeploymentException handleDeserializationException(
		final int offset, final int length, final Exception ex) {
		final ServerDeploymentException sdex = new ServerDeploymentException(
			ExceptionConstants.COMPLEX_ERROR,
			new String[] { "Cannot retrieve the remote command from message " +
				"with offset " + offset + " and length " + length }, 
			ex);
		SimpleLogger.traceThrowable(Severity.ERROR, location, 
			sdex.getLocalizedMessage(), ex);
		return sdex;
	}
}
