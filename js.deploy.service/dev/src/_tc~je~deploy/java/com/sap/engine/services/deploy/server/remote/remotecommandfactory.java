package com.sap.engine.services.deploy.server.remote;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.server.DTransaction;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.LocalDeployment;
import com.sap.engine.services.deploy.server.application.ParallelAdapter;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.system.ThreadWrapper;

/**
 * This factory is used to create serializable remote commands, which are used
 * for remote communications.
 * 
 * @author Emil Dinchev
 */
public final class RemoteCommandFactory {

	/**
	 * Serializable abstract remote command.
	 */
	public static abstract class RemoteCommand implements Serializable {
		private static final long serialVersionUID = 1L;

		// Thread transaction ID.
		private final String transactionId;
		
		public RemoteCommand() {
			transactionId = ThreadWrapper.getTransactionId();
		}
		
		/**
		 * The result of execution of every remote command is a message 
		 * response which will be sent back to the initiator (creator) of the
		 * command.
		 * @param localDeployment local deployment context used to execute the 
		 * command.
		 * @param currentId current server ID.
		 * @return the result of execution.
		 * @throws Exception
		 */
		public abstract MessageResponse execute(
			LocalDeployment localDeployment, int currentId) throws Exception;
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName()).append(DSConstants.EOL_TAB)
				.append("transactionId=").append(transactionId)
				.append(CAConstants.EOL);
			return sb.toString();
		}

		public String getTransactionId() {
			return transactionId;
		}
	}
	
	/**
	 * Remote command used to execute a transaction on a list of remote server
	 * nodes.
	 */
	private static class MakeTransactionRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;
		
		private final Map<String, Object> cmdTable;
		private final int initiatorId;
		
		public MakeTransactionRemoteCommand(final Map<String, Object> cmdTable,
			final int initiatorId) {
			assert cmdTable != null;
			assert initiatorId > 0;

			this.cmdTable = cmdTable;
			this.initiatorId = initiatorId;
		}
		
		@Override
		public MessageResponse execute(final LocalDeployment localDeployment, 
			final int currentId) throws Exception {
			// TODO: move the code here and split it 
			// to have a command for every transaction type.
			return localDeployment.beginLocalTransaction(cmdTable, initiatorId);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(super.toString());
			sb.append(DSConstants.TAB).append("initiatorId=")
				.append(initiatorId).append(DSConstants.EOL_TAB)
				.append("cmdTable=").append(cmdTable).append(CAConstants.EOL);
			return sb.toString();
		}
	}
		
	/**
	 * Remote command used to list the deployed applications on a list of 
	 * remote server nodes. This command is sent with request-response message.
	 */
	private static class ListAppRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;

		protected final String containerName;
		protected final boolean onlyJ2ee;
		
		/**
		 * @param containerName the name of the container for which we want to
		 * list the deployed applications. If this parameter is <tt>null</tt>
		 * we want to list all deployed applications independently from the
		 * containers.
		 * @param onlyJ2ee flag indicating that we want to list only JEE 
		 * applications.
		 */
		public ListAppRemoteCommand(final String containerName, 
			final boolean onlyJ2ee) {
			this.containerName = containerName;
			this.onlyJ2ee = onlyJ2ee;
		}

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) { 
			final String[] names = onlyJ2ee ?
				localDeployment.listJ2EEApplications(containerName) :
				localDeployment.listApplications(containerName);
			return new MessageResponse(
				currentId, null, null, CAConvertor.asSet(names));
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(super.toString());
			if(containerName != null) {
				sb.append(DSConstants.TAB).append("containerName=")
					.append(containerName).append(DSConstants.EOL_TAB);
			}
			sb.append("onlyJ2ee=").append(onlyJ2ee).append(CAConstants.EOL);
			return sb.toString();
		}		
	}
	
	/**
	 * Remote command used to obtain the status of a given application. This
	 * command is sent with request-response message.
	 */
	private static class AppStatusRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;
		protected final String appName;

		/**
		 * @param appName the name of the application for which we want to 
		 * obtain the status. Not null.
		 */
		public AppStatusRemoteCommand(final String appName) {
			this.appName = appName;
			assert appName != null;
		}

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) throws Exception {
			return new MessageResponse(currentId, null, null, 
				localDeployment.getApplicationStatus(appName));
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(super.toString());
			sb.append(DSConstants.TAB).append("appName=")
				.append(appName).append(CAConstants.EOL);
			return sb.toString();
		}
	}

	/**
	 * Remote command used to obtain the status description of a given 
	 * application. This command is sent with request-response message.
	 */
	private static class AppStatusDescrRemoteCommand 
		extends AppStatusRemoteCommand {
		private static final long serialVersionUID = 1L;
		
		/**
		 * @param appName the name of the application for which we want to 
		 * obtain the status description. Not null.
		 */
		public AppStatusDescrRemoteCommand(final String appName) {
			super(appName);
		}

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment, 
			final int currentId) throws Exception {
			return new MessageResponse(currentId, null, null,
				localDeployment.getApplicationStatusDescription(appName));
		}		
	}

	/**
	 * Remote command used to list the elements of a given application. This 
	 * command is sent with request-response message.
	 */
	private static class ListElementsRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;
		private final String appName;
		private final String containerName;

		/**
		 * @param appName The application which elements will be listed. If this
		 * parameter is <tt>null</tt> we will list elements for all 
		 * applications.
		 * @param containerName The container which elements will be listed. If
		 * this parameter is <tt>null</tt> we will list elements for all 
		 * containers. 
		 */
		public ListElementsRemoteCommand(final String appName,
			final String containerName) {
			this.appName = appName;
			this.containerName = containerName;
		}

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) throws Exception {
			return new MessageResponse(currentId, null, null, 
				localDeployment.listElements(containerName, appName));
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(super.toString());
			if(appName != null) {
				sb.append(DSConstants.TAB).append("appName=")
					.append(appName).append(CAConstants.EOL);
			}
			if(containerName != null) {
				sb.append(DSConstants.TAB).append("containerName=")
					.append(containerName).append(CAConstants.EOL);
			}
			return sb.toString();
		}
	}
	
	/**
	 * Remote command used to list containers. This command is sent with 
	 * request-response message.
	 */
	private static class ListContainersRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) throws IOException {
			return new MessageResponse(currentId, null, null,
				localDeployment.listContainers());
		}
	}
	
	/**
	 * Remote command used to return the result from the execution of an 
	 * parallel transaction (start or stop).
	 */
	private static class RespondRemoteCommand extends RemoteCommand {
		private static final long serialVersionUID = 1L;

		private final String appName;
		private final String txType;
		private final String[] warnings;
		private final String[] errors;
		private final int senderId;

		/**
		 * @param senderId The ID of the remote server where the corresponding
		 * parallel transaction was executed. Must be valid server ID.
		 * @param appName the name of the started/stopped application.
		 * @param txType start or stop.
		 * @param warnings can be null.
		 * @param errors can be null.
		 */
		public RespondRemoteCommand(final int senderId, final String appName,
			final String txType, final String[] warnings,
			final String[] errors) {
			assert senderId > 0;
			assert appName != null;
			assert DeployConstants.startApp.equals(txType) ||
				DeployConstants.stopApp.equals(txType);
			this.senderId = senderId;
			this.appName = appName;
			this.txType = txType;
			this.errors = errors;
			this.warnings = warnings;
		}
		
		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) throws IOException {
			final DTransaction tx = 
				localDeployment.getTransaction(appName, txType);
			if (tx != null) {
				((ParallelAdapter) tx).serverFinished(
					senderId, warnings, errors);
			}
			return null;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(super.toString());
			sb.append(DSConstants.TAB).append("senderId=")
				.append(senderId).append(DSConstants.EOL_TAB)
				.append("appName=").append(appName).append(DSConstants.EOL_TAB)
				.append("txType=").append(txType).append(DSConstants.EOL_TAB)
				.append("warnings=").append(CAConvertor.toString(
					warnings, "")).append(DSConstants.EOL_TAB)
				.append("errors=").append(CAConvertor.toString(
					errors, "")).append(CAConstants.EOL);
			return sb.toString();
		}
	}

	/**
	 * Remote command used to list applications deployed on a given container.
	 * Applications are listed together with their statuses. 
	 */
	private static class ListAppAndStatusesRemoteCommand 
		extends ListAppRemoteCommand {
		private static final long serialVersionUID = 1L;
		private final boolean withStatusDescr;
		
		/**
		 * Creates a remote command used to list applications deployed on a 
		 * given container together with their statuses. This command is sent 
		 * with request-response message.
		 * @param containerName the name of the container for which we want to 
		 * list applications, together with their statuses. If this parameter 
		 * is <tt>null</tt> we want to list all deployed applications 
		 * independently from the containers.
		 * @param onlyJ2ee only JEE applications will be listed.
		 * @param withStatusDescription list and status descriptions too. 
		 */
		public ListAppAndStatusesRemoteCommand(final String containerName,
			final boolean onlyJ2ee, final boolean withStatusDescr) {
			super(containerName, onlyJ2ee);
			this.withStatusDescr = withStatusDescr;
		}

		@Override
		public MessageResponse execute(final LocalDeployment localDeployment,
			final int currentId) {
			final String[] names;
			if (onlyJ2ee) {
				names = localDeployment.listJ2EEApplications(containerName);
			} else {
				names = localDeployment.listApplications(containerName);
			}
			assert names != null;
			
			final Map<String, Object> appMap = 
				new HashMap<String, Object>();
			StatusDescription statusDescription = null;
			for(final String appName : names) {
				String status;
				try {
					status = localDeployment.getApplicationStatus(appName);
				} catch (RemoteException rex) {
					status = "NOT DEPLOYED";
				}
				if (withStatusDescr) {
					try {
						statusDescription = localDeployment
							.getApplicationStatusDescription(appName);
					} catch (RemoteException rex) {
						statusDescription = null;
					}
					appMap.put(appName, new Object[] {
						status, statusDescription });
				} else {
					appMap.put(appName, status);
				}
			}
			return new MessageResponse(currentId, null, null, appMap);
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder(super.toString());
			sb.append(DSConstants.TAB).append("withStatusDescr=")
				.append(withStatusDescr).append(CAConstants.EOL);
			return sb.toString();
		}
	}
	
	private RemoteCommandFactory() {
		// Private constructor to prevent the instantiation.
	}
	
	/**
	 * Creates a remote command used to execute a transaction on a list of 
	 * remote server nodes.
	 * @param cmdMap the corresponding command map.
	 * @param currentId the server node ID of the creator.
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createMakeTransactionCmd(
		final Map<String, Object> cmdMap, final int currentId) {
		return new MakeTransactionRemoteCommand(cmdMap, currentId);
	}

	/**
	 * Creates a remote command used to list applications deployed on a given
	 * container. 
	 * @param containerName the name of the container for which we want to list
	 * applications.
	 * @param onlyJ2ee only JEE applications will be listed. 
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createListAppsCmd(
		final String containerName, final boolean onlyJ2ee) {
		return new ListAppRemoteCommand(containerName, onlyJ2ee);
	}
	
	/**
	 * Creates a remote command used to obtain the status of a given 
	 * application. This command is sent with request-response message.
	 * @param appName the name of the application, which status we want to
	 * check.
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createAppStatusCmd(final String appName) {
		return new AppStatusRemoteCommand(appName);
	}

	/**
	 * Creates a remote command used to obtain the status description of a 
	 * given application. This command is sent with request-response message.
	 * @param appName the name of the application which status we want to 
	 * obtain. Not null.
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createAppStatusDescrCmd(
		final String appName) {
		return new AppStatusDescrRemoteCommand(appName);
	}

	/**
	 * Creates a remote command used to obtain the elements of a given 
	 * application. This command is sent with request-response message.
	 * @param containerName the name of the container, for which we want to
	 * list the application elements. If this parameter is <tt>null</tt> then
	 * all components for the specified application on all registered 
	 * containers will be listed.
	 * @param application the name of the application which elements will be 
	 * listed. If this parameter is <tt>null</tt> we will list elements for all
	 * applications.
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createListElementsCmd(
		final String containerName, final String application) {
		return new ListElementsRemoteCommand(application, containerName);
	}

	/**
	 * Creates a remote command used to list applications deployed on the given
	 * container. Applications are listed together with their statuses. 
	 * @param containerName the name of the container for which we want to list
	 * applications, together with their statuses.
	 * @param onlyJ2ee only JEE applications will be listed.
	 * @param withStatusDescription list and status descriptions. 
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createListAppsAndStatusCmd(
		final String containerName, final boolean onlyJ2ee, 
		final boolean withStatusDescription) {
		return new ListAppAndStatusesRemoteCommand(
			containerName, onlyJ2ee, withStatusDescription);
	}

	/**
	 * Creates a remote command used to list containers. This command is sent
	 * with request-response message.
	 * @return the remote command. Not null.
	 */
	public static RemoteCommand createListContainersCmd() {
		return new ListContainersRemoteCommand();
	}
	
	/**
	 * Creates a remote command used to return the result from the execution of
	 * an parallel transaction (start or stop).
	 * @param appName the name of the application.
	 * @param txType the transaction type (start or stop).
	 * @param errors errors during the execution.
	 * @param warnings warnings during the execution.
	 * @return the remote command. Not null.
	 */
	public static final RemoteCommand createRespondCmd(
		final int senderId, final String appName, final String txType,
		final String[] warnings, final String[] errors) {
		return new RespondRemoteCommand(
			senderId, appName, txType, warnings, errors);
	}
}