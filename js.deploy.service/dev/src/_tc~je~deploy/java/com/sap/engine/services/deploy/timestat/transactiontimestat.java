/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.timestat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.deploy.container.TimeStatisticEvent;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.localization.LocalizationException;
import com.sap.tc.logging.Location;
import com.sap.tools.memory.trace.AllocationStatisticRecord;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;

public class TransactionTimeStat implements ITimeStatConstants {
	private static final Location location = 
		Location.getLocation(TransactionTimeStat.class);

	// space for the table tree level separation
	private static final String TREE_LEVEL_SPACE = "    ";
	private static final String INIT_OP = "Initial operation";

	// a optimization hash map storing all registered
	// statistics for faster access; key is the absolute
	// path to the statistic node, value is a <code>OperationTimeStat</code>
	// object;
	// an alternative will be to store only a reference to specific node(s)
	// and parse tree structure for given path at 'add'.
	private HashMap<String, TimeStatisticNode> registeredOpTimeStat = new HashMap<String, TimeStatisticNode>();

	// this map stores a path to an expected parent operation
	// as key and a list with child operations as value; at
	// finish an attempt is made to add the children to the
	// specified parent operations if they exist.
	private TreeMap<String, List<TimeStatisticNode>> subOpsAwaitingParent = new TreeMap<String, List<TimeStatisticNode>>();

	// member variables storing the
	// names of the known nodes
	private String sRootNodeName;
	private String sAllContainersNodeName;
	private String sDeployServiceNodeName;
	private String sJLinEENodeName;
	private String sProtDomNodeName;
	private String sAppMngObjNodeName;
	private String sIOsNodeName;
	private String sCfgMngNodeName;
	private String sEarReaderNodeName;
	private String sClearFSNodeName;

	// member variables storing the
	// absolute path to the known nodes
	private String sAllContainersNodePath;
	private String sDeployServiceNodePath;
	private String sJLinEENodePath;
	private String sProtDomNodePath;
	private String sAppMngObjNodePath;
	private String sIOsNodePath;
	private String sCfgMngNodePath;
	private String sEarReaderNodePath;
	private String sClearFSNodePath;

	private String transactionName;
	private String applicationName;
	private long start;
	private long end;
	private long cpuStartTime = -1;
	private long cpuEndTime = -1;
	private Map<String, AllocationStatisticRecord> string2ASRecord = null;

	private boolean isExecutable = true;

	private final int additionalDebug;
	private final int size;

	private static final int SIZE_ALL = 7;
	private static final int SIZE_TIME = 3;

	public static final ThreadLocal transactionStat = new ThreadLocal();

	public TransactionTimeStat(String aTransactionName,
			String aApplicationName, int additionalDebug) {
		start = System.currentTimeMillis();
		this.additionalDebug = additionalDebug;
		switch (this.additionalDebug) {
		case PropManager.ADDITIONAL_DEBUG_INFO_DEFAULT:
			;
		case PropManager.ADDITIONAL_DEBUG_INFO_TIME_STATS: {
			size = SIZE_TIME;
			break;
		}
		case PropManager.ADDITIONAL_DEBUG_INFO_ALL_STATS: {
			size = SIZE_ALL;
			break;
		}
		default: {
			size = SIZE_TIME;
			break;
		}
		}
		if (!isExecutable()) {
			return;
		}
		if (size == SIZE_ALL) {
			cpuStartTime = SystemTime.currentCPUTimeUs();
			AllocationStatisticRegistry.pushThreadTag(DEPLOY_SERVICE + ":"
					+ aTransactionName + ":" + INIT_OP, false);
		}
		transactionName = aTransactionName;
		applicationName = aApplicationName;
		// initialization
		init();

		transactionStat.set(this);
	}

	public TransactionTimeStat(String aTransactionName, int additionalDebug) {
		this(aTransactionName, null, additionalDebug);
	}

	public TransactionTimeStat(TransactionTimeStat stat) {
		this(stat.getTransactionName(), stat.getApplicationName(), stat
				.getAdditionalDebug());
	}

	/**
	 * Initialize the basic known nodes.
	 * 
	 * @author Todor Stoitsev
	 */
	private void init() {
		// initialize the basic node names/paths that are
		// required for the initial nodes creation
		sRootNodeName = FULL_OPERATION_TIME;
		sDeployServiceNodeName = DEPLOY_SERVICE;
		sJLinEENodeName = JLIN_EE;
		sProtDomNodeName = PROT_DOM;
		sAppMngObjNodeName = APP_MNG_OBJ;
		sIOsNodeName = IOs;
		sCfgMngNodeName = CFG_MNG;
		sEarReaderNodeName = EAR_READ;
		sClearFSNodeName = CLEAR_FS;
		sAllContainersNodeName = ALL_CONTAINERS_DURATION;

		{
			sDeployServiceNodePath = sRootNodeName
					+ TimeStatisticNode.TREE_PATH_SEP + sDeployServiceNodeName;
			{
				sJLinEENodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sJLinEENodeName;
				sProtDomNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sProtDomNodeName;
				sAppMngObjNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sAppMngObjNodeName;
				sIOsNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sIOsNodeName;
				sCfgMngNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sCfgMngNodeName;
				sEarReaderNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sEarReaderNodeName;
				sClearFSNodePath = sDeployServiceNodePath
						+ TimeStatisticNode.TREE_PATH_SEP + sClearFSNodeName;

			}
			sAllContainersNodePath = sRootNodeName
					+ TimeStatisticNode.TREE_PATH_SEP + sAllContainersNodeName;
		}

		// create the basic structure; the operation time statistics
		// here have no start and end time, because these will be calculated;
		// these nodes serve as basis for the time statistic tree
		TimeStatisticNode oRootStat = new CalculatedTimeStatisticNode(
				sRootNodeName);
		TimeStatisticNode oDeployServiceTimeStat = new CalculatedTimeStatisticNode(
				sDeployServiceNodeName);
		TimeStatisticNode oAllContainersTimeStat = new CalculatedTimeStatisticNode(
				sAllContainersNodeName);
		// add the basic nodes to the root configuration
		oRootStat.addSubOpStat(oDeployServiceTimeStat);
		oRootStat.addSubOpStat(oAllContainersTimeStat);
		// register the basic deploy service and all containers operation time
		// statistics for faster access in the add phase
		registeredOpTimeStat
				.put(sDeployServiceNodePath, oDeployServiceTimeStat);
		registeredOpTimeStat
				.put(sAllContainersNodePath, oAllContainersTimeStat);
	}

	/**
	 * Adds a container.
	 * 
	 * @param containerName
	 * @param stat
	 * @author Todor Stoitsev
	 */
	private void addContainerOperation(String containerName,
			TimeStatisticNode stat) {
		if (!isExecutable())
			return;
		synchronized (registeredOpTimeStat) {
			// container node path
			String sContainerPath = sAllContainersNodePath
					+ TimeStatisticNode.TREE_PATH_SEP + containerName;
			// if the container node has not ben created yet,
			// create a container node and register it in the added nodes map
			if (registeredOpTimeStat.get(sContainerPath) == null) {
				TimeStatisticNode oContainerTimeStat = new CalculatedTimeStatisticNode(
						containerName);
				addSubOperationStatistic(sAllContainersNodePath,
						oContainerTimeStat, false);
			}
			// add the container operation as child to the given container
			addSubOperationStatistic(sAllContainersNodePath
					+ TimeStatisticNode.TREE_PATH_SEP + containerName, stat,
					true);
		}
	}

	/**
	 * Accessor method for a container operation.
	 * 
	 * @param sContainerName
	 *            - name of container
	 * @param sOpRelativePath
	 *            - the relative path of the required container operation, to
	 *            the calculated container parent; may be only a name of a
	 *            container parent direct sub operation.
	 * @return - the time statistic node representing the container operation or
	 *         <code>null</code> if not available.
	 * @author Todor Stoitsev
	 */
	private TimeStatisticNode getContainerOperation(String sContainerName,
			String sOpRelativePath) {
		synchronized (registeredOpTimeStat) {
			// container operaton node path
			String sContainerPath = sAllContainersNodePath
					+ TimeStatisticNode.TREE_PATH_SEP + sContainerName;
			// get the container operation node or null if not available
			return getOperationTimeStatistic(sContainerPath, sOpRelativePath);
		}
	}

	/**
	 * Adds a JLinEE statistic node.
	 * 
	 * @param stat
	 * @author Todor Stoitsev
	 */
	private void addJLinEEOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sJLinEENodePath, sJLinEENodeName);
	}

	private void addProtDomOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sProtDomNodePath, sProtDomNodeName);
	}

	private void addAppMngObjOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sAppMngObjNodePath, sAppMngObjNodeName);
	}

	private void addIOsOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sIOsNodePath, sIOsNodeName);
	}

	private void addCfgMngOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sCfgMngNodePath, sCfgMngNodeName);
	}

	private void addEarReaderOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sEarReaderNodePath, sEarReaderNodeName);
	}

	private void addClearFSOp(TimeStatisticNode stat) {
		addDSSubOp(stat, sClearFSNodePath, sClearFSNodeName);
	}

	private void addDSSubOp(TimeStatisticNode stat, String dsSubOpPath,
			String dsSubOpName) {
		if (!isExecutable())
			return;
		synchronized (registeredOpTimeStat) {
			// if the DSSub node has not ben created yet,
			// create one and register it in the added nodes map
			if (registeredOpTimeStat.get(dsSubOpPath) == null) {
				TimeStatisticNode oJLinEETimeStat = new CalculatedTimeStatisticNode(
						dsSubOpName);
				addSubOperationStatistic(sDeployServiceNodePath,
						oJLinEETimeStat, false);
			}
			// add the container operation as child to the given container
			addSubOperationStatistic(dsSubOpPath, stat, true);
		}
	}

	/**
	 * Retrieves the time statistic for a given operation with a given parent
	 * operation path.
	 * 
	 * @param sParentPath
	 *            - the parent path for the operation.
	 * @param sOpRelativePath
	 *            - the relative path of the operation to the given parent - may
	 *            be the operation name.
	 * @return - the time statistic object for the given operation or
	 *         <code>null</code> if not available.
	 * @author Todor Stoitsev
	 */
	private TimeStatisticNode getOperationTimeStatistic(String sParentPath,
			String sOpRelativePath) {
		synchronized (registeredOpTimeStat) {
			// operaton node path
			String sOpPath = sParentPath + TimeStatisticNode.TREE_PATH_SEP
					+ sOpRelativePath;
			// get the operation node or null if not available
			return (TimeStatisticNode) registeredOpTimeStat.get(sOpPath);
		}
	}

	/**
	 * Method processes the current sub operations awaiting a parent node using
	 * the current state of the registered operations map. If a matching parent
	 * is found, the current sub operations are added to it. If not an error is
	 * logged.
	 * 
	 * @author Todor Stoitsev
	 */
	private void handleSubOpsAwaitingParent() {
		// if sub operations to the given operation are waiting to be added
		// add them to the just added operation
		synchronized (subOpsAwaitingParent) {
			if (subOpsAwaitingParent.size() > 0) {
				StringBuffer sbError = new StringBuffer();
				Iterator subOps = subOpsAwaitingParent.keySet().iterator();
				while (subOps.hasNext()) {
					String sParentOpPath = (String) subOps.next();
					// check if the expected parent node has been added
					// (registered)
					TimeStatisticNode oParentNode = (TimeStatisticNode) registeredOpTimeStat
							.get(sParentOpPath);
					List<TimeStatisticNode> subOpsList = subOpsAwaitingParent
							.get(sParentOpPath);
					if (oParentNode != null) {
						for (int i = 0; i < subOpsList.size(); i++) {
							addSubOperationStatistic(sParentOpPath,
									(TimeStatisticNode) subOpsList.get(i),
									false);
							// oParentNode.addSubOpStat((TimeStatisticNode)
							// subOpsList.get(i));
						}
					}
					// expected parent does not exist
					else {
						sbError
								.append("Time statistic: Error - some sub operations were not added to a parent and will be removed:\n");
						sbError.append(TREE_LEVEL_SPACE + sParentOpPath + "\n");

						// check if the expected parent node has been added
						// (registered)
						for (int i = 0; i < subOpsList.size(); i++) {
							sbError.append(TREE_LEVEL_SPACE
									+ TREE_LEVEL_SPACE
									+ ((TimeStatisticNode) subOpsList.get(i))
											.getNodeName() + "\n");
						}
					}
				}
				// log errors
				if (sbError.length() > 1) {
					// last char is '\n' and logging appends yet another new
					// line
					sbError.deleteCharAt(sbError.length() - 1);
					DSLog.traceWarning(location, "ASJ.dpl_ds.004405", "{0}", sbError
							.toString());
				}
				// the operations awaiting parent are added to the
				// first registered parent matching their parent path;
				// as duplicates are allowed, keeping the registered
				// sub operations may result in adding them to a duplicate
				// parent operation with the same path at a later moment,
				// which is wrong;
				subOpsAwaitingParent.clear();
			}
		}
	}

	/**
	 * Adds a sub operation time statistic to a given operation time statistic
	 * entry.
	 * 
	 * @param sParentTimeStatAbsPath
	 * @param stat
	 * @author Todor Stoitsev
	 */
	private void addSubOperationStatistic(String sParentTimeStatAbsPath,
			TimeStatisticNode stat, boolean handleSubOpsAwaitingParent) {
		if (!isExecutable())
			return;
		synchronized (registeredOpTimeStat) {
			TimeStatisticNode oParentTimeStat = (TimeStatisticNode) registeredOpTimeStat
					.get(sParentTimeStatAbsPath);
			// such time statistic was registered
			if (oParentTimeStat != null) {
				// add to parent
				oParentTimeStat.addSubOpStat(stat);

				// if the statistic already exists log a warning
				// as adding of duplicate statistic is allowed but
				// must be tracable
				if (registeredOpTimeStat.get(stat.getPath()) != null) {
					if (location.beDebug()) {
						DSLog
								.traceDebug(
										location, 
										"Time statistic: Sub statistic [{0}{1}{2}] already exist. Duplicate sub operation statistics will be added!",
										sParentTimeStatAbsPath,
										TimeStatisticNode.TREE_PATH_SEP, stat
												.getNodeName());
					}
				}
				// register the just added time statistic for faster access
				// if sub operation statistics will be added to it in a future
				// moment;
				// in case of a duplicate the last added statistic is currently
				// registered
				registeredOpTimeStat.put(stat.getPath(), stat);
				// if a handling of the current operations awaiting a parent is
				// relevant, process it.
				if (handleSubOpsAwaitingParent)
					handleSubOpsAwaitingParent();
			}
			// no time statistic with the given parent path was registered
			else {
				registerSubOperationAwaitingParent(sParentTimeStatAbsPath, stat);
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"Time statistic: Warning - the specified parent time statistic [{0}] has not been added to the table! Registering child [{1}] as waiting for parent!",
									sParentTimeStatAbsPath, stat.getNodeName());
				}
			}
		}
	}

	public void finish() {
		end = System.currentTimeMillis();
		cpuEndTime = SystemTime.currentCPUTimeUs();
		transactionStat.set(null);

		if (!isExecutable()) {
			return;
		}

		AllocationStatisticRegistry.popThreadTag();
		if (size == SIZE_ALL) {
			string2ASRecord = AllocationStatisticRegistry
					.getAllocationStatistic(DEPLOY_SERVICE + ":" + ".*", true,
							true, true);
		}

		disable();
		DSLog.traceTimeStat(toString());
	}

	private String getTransactionName() {
		return transactionName;
	}

	private String getApplicationName() {
		return applicationName;
	}

	private void setApplicationName(String aApplicationName) {
		applicationName = aApplicationName;
	}

	private int getAdditionalDebug() {
		return additionalDebug;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getCpuStart() {
		return cpuStartTime;
	}

	public long getCpuEnd() {
		return cpuEndTime;
	}

	public long getDuration() {
		return end - start;
	}

	public float getCpuDuration() {
		if (cpuStartTime == -1 || cpuEndTime == -1) {
			DSLog
					.traceDebug(
							location, 
							"Time statistic: The complete transaction time stat has CPU start time [{0}] and CPU end time [{1}]. The information is insufficient to calculate the cpu duration.",
							cpuStartTime, cpuEndTime);
			return -1;
		}
		return SystemTime.calculateTimeStampDeltaInMicros(cpuStartTime,
				cpuEndTime) / 1000f;
	}

	public static void setAppName(String aApplicationName) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			stat.applicationName = aApplicationName;
		}
	}

	public static void addContainerOp(String aContainerName,
			TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			stat.addContainerOperation(aContainerName, aTimeStat);
		}
	}

	/**
	 * Adds a deploy operation. The operation node will be added as a subnode to
	 * the deploy service node.
	 * 
	 * @param aTimeStat
	 */
	public static void addDeployOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addSubOperationStatistic(stat.sDeployServiceNodePath,
					aTimeStat, true);
		}
	}

	/**
	 * Adds a deploy sub operation. The operation node will be added as a
	 * subnode to the specified parent node under the deploy service node.
	 * 
	 * @param aTimeStat
	 */
	public static void addDeploySubOperation(String sParentRelPath,
			TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addSubOperationStatistic(stat.sDeployServiceNodePath + "/"
					+ sParentRelPath, aTimeStat, true);
		}
	}

	/**
	 * Adds a JLinEE operation statistic. The statistic node will be added as a
	 * subnode to the JLinEE node.
	 * 
	 * @param aTimeStat
	 */
	public static void addJLinEEOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addJLinEEOp(aTimeStat);
		}
	}

	/**
	 * Adds a JLinEE operation statistic. The statistic node will be added as a
	 * subnode to the JLinEE node.
	 * 
	 * @param aTimeStat
	 */
	public static void addProtDomOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addProtDomOp(aTimeStat);
		}
	}

	public static void addAppMngObjOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addAppMngObjOp(aTimeStat);
		}
	}

	public static void addIOsOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addIOsOp(aTimeStat);
		}
	}

	public static void addCfgMngOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addCfgMngOp(aTimeStat);
		}
	}

	public static void addEarReaderOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addEarReaderOp(aTimeStat);
		}
	}

	public static void addClearFSOperation(TimeStatisticNode aTimeStat) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			stat.addClearFSOp(aTimeStat);
		}
	}

	/**
	 * Adds a sub operation to the node identified by the given time statistic
	 * parent path.
	 * 
	 * @param sParentTimeStatAbsPath
	 * @param subNode
	 * @author Todor Stoitsev
	 */
	public static void addSubOperation(String sParentTimeStatAbsPath,
			TimeStatisticNode subNode) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			stat
					.addSubOperationStatistic(sParentTimeStatAbsPath, subNode,
							true);
		}
	}

	/**
	 * Retrieves a container operation.
	 * 
	 * @param sContainerName
	 *            - the name of the parent container.
	 * @param sOpRelativePath
	 *            - the path of the operation relative to the calculated
	 *            container parent.
	 * 
	 * @return - the specified time statistic or <code>null</code> if not
	 *         available.
	 */
	public static TimeStatisticNode getContainerOp(String sContainerName,
			String sOpRelativePath) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			// add the deploy operation as child of the deploy service node
			return stat.getContainerOperation(sContainerName, sOpRelativePath);
		}
		return null;
	}

	/**
	 * Handling of progress events for registerning sub operations. Note! The
	 * request for adding time statistic for a sub operation will come before
	 * the adding of the parent operation because requests for parent operations
	 * are added after the operations have completed. If the parent operation is
	 * not added, no sub operations will be added.
	 * 
	 * @param event
	 * @author Todor Stoitsev
	 */
	private void handleProgressEvt(ProgressEvent event) {
		// only process time statistic events
		if (event instanceof TimeStatisticEvent) {
			TimeStatisticEvent timeStatEvt = (TimeStatisticEvent) event;
			String sContainerName = timeStatEvt.getSourceName();
			String sContainerOpPath = timeStatEvt.getMessage();
			long start = timeStatEvt.getStart();
			long end = timeStatEvt.getEnd();
			long cpuStart = timeStatEvt.getCpuStart();
			long cpuEnd = timeStatEvt.getCpuEnd();
			String[] sOperationNodeAndPath = getContainerOperationRelativePathAndName(sContainerOpPath);
			String sOperationName = sOperationNodeAndPath[0];
			// the given operation relative path to the container node
			String sOpRelativePath = sOperationNodeAndPath[1];
			// do not set application name for sub operations;
			// it should be set to their parents
			ContainerOperationTimeStat stat = new ContainerOperationTimeStat(
					sOperationName, null, start, end, cpuStart, cpuEnd);
			String sAboluteParentPath = sAllContainersNodePath
					+ TimeStatisticNode.TREE_PATH_SEP + sContainerName
					+ TimeStatisticNode.TREE_PATH_SEP + sOpRelativePath;
			registerSubOperationAwaitingParent(sAboluteParentPath, stat);
		}
	}

	/**
	 * Register a sub operaton for a given parent. As sub operations are nested
	 * in parent, they will be appended, when the parent operation finishes.
	 * 
	 * @param sAboluteParentPath
	 * @param stat
	 */
	private void registerSubOperationAwaitingParent(String sAboluteParentPath,
			TimeStatisticNode stat) {
		synchronized (subOpsAwaitingParent) {
			List<TimeStatisticNode> subOps = subOpsAwaitingParent
					.get(sAboluteParentPath);
			if (subOps == null) {
				subOps = new ArrayList<TimeStatisticNode>();
			}
			subOps.add(stat);
			subOpsAwaitingParent.put(sAboluteParentPath, subOps);
		}
	}

	/**
	 * Method initiates the actual processing of a progress event.
	 * 
	 * @return
	 * @author Todor Stoitsev
	 */
	public static void handleProgressEvent(ProgressEvent evt) {
		TransactionTimeStat stat = (TransactionTimeStat) transactionStat.get();
		if (stat != null) {
			stat.handleProgressEvt(evt);
		}
	}

	public static TransactionTimeStat substitute(TransactionTimeStat oldStat) {
		TransactionTimeStat result = null;
		if (oldStat != null) {
			result = new TransactionTimeStat(oldStat.transactionName,
					oldStat.applicationName, oldStat.getAdditionalDebug());

			result.start = oldStat.start;
			result.registeredOpTimeStat = oldStat.registeredOpTimeStat;
			oldStat.disable();
		} else {
			result = new TransactionTimeStat(null, null,
					PropManager.ADDITIONAL_DEBUG_INFO_DEFAULT);
			transactionStat.set(null);
			result.disable();
		}
		return result;
	}

	public static TransactionTimeStat createIfNotAvailable(
			String aTransactionName) {
		return createIfNotAvailable(aTransactionName, null);
	}

	public static TransactionTimeStat createIfNotAvailable(
			String aTransactionName, String aApplicationName) {
		TransactionTimeStat current = (TransactionTimeStat) transactionStat
				.get();
		if (current != null) {
			return current;
		}

		return new TransactionTimeStat(aTransactionName, aApplicationName,
				PropManager.getInstance().getAdditionalDebugInfo());
	}

	/**
	 * Retrieves the deploy operation duration.
	 * 
	 * @return
	 * @author Todor Stoitsev
	 */
	private long getDeployOperationsDuration() {
		return getTimeStatisticNodeDuration(sDeployServiceNodePath);
	}

	private String getDeployOperationsDurationPercent() {
		return getPercent(getDeployOperationsDuration(), getDuration());
	}

	/**
	 * Retrieves the duration of all containers.
	 * 
	 * @return
	 * @author Todor Stoitsev
	 */
	private long getContainersDuration() {
		return getTimeStatisticNodeDuration(sAllContainersNodePath);
	}

	/**
	 * Retrieves the time statistic duration for a node with the specfied path.
	 * 
	 * @param sNodePath
	 * @return
	 * @author Todor Stoitsev
	 */
	private long getTimeStatisticNodeDuration(String sNodePath) {
		TimeStatisticNode oNode = (TimeStatisticNode) registeredOpTimeStat
				.get(sNodePath);
		if (oNode == null) {
			DSLog.traceWarning(location, "ASJ.dpl_ds.002050",
					"Time statistic: Error - no node found for path [{0}]!",
					sNodePath);
			return TimeStatisticNode.UNKNOWN_TIME;
		}
		return oNode.getDuration();
	}

	private String getContainersDurationPercent() {
		return getPercent(getContainersDuration(), getDuration());
	}

	/**
	 * Retrieves a container duration.
	 * 
	 * @param containerName
	 * @return
	 */
	private long getContainerDuration(String containerName) {
		return getTimeStatisticNodeDuration(sAllContainersNodePath
				+ TimeStatisticNode.TREE_PATH_SEP + containerName);
	}

	private String getContainerDurationPercent(String containerName) {
		return getPercent(getContainerDuration(containerName), getDuration());
	}

	private boolean isExecutable() {
		return (getAdditionalDebug() == PropManager.ADDITIONAL_DEBUG_INFO_TIME_STATS || getAdditionalDebug() == PropManager.ADDITIONAL_DEBUG_INFO_ALL_STATS)
				&& isExecutable;
	}

	private void disable() {
		isExecutable = false;
	}

	public String toString() {
		final StringBuffer result = new StringBuffer();
		result.append(System.getProperty("line.separator"));
		try {
			renderTable(result);
		} catch (LocalizationException e) {
			DSLog
					.logErrorThrowable(
							location, 
							"ASJ.dpl_ds.006403",
							"Localization exception while rendering time statistic table",
							e);
		}
		if (string2ASRecord != null && !string2ASRecord.isEmpty()) {
			result.append(AllocationStatisticRegistry
					.generateTextReport(string2ASRecord));
		}
		return result.toString();
	}

	private void renderTable(StringBuffer out) throws LocalizationException {
		ArrayList tableLines = makeTableLines();
		String tableHeader = TIME_STATISTICS_HEADER + getTransactionName();
		String[] header = (size == SIZE_ALL) ? new String[] { tableHeader, "",
				"", "", "", "", "" } : new String[] { tableHeader, "", "" };
		String[] separator = (size == SIZE_ALL) ? new String[] { "", "", "",
				"", "", "", "" } : new String[] { "", "", "" };
		tableLines.add(0, header);
		int[] columnWidths = columnsMaxWith(tableLines);
		tableLines.remove(0);
		printTableLine(header, out, columnWidths, '-', '+');
		for (int i = 0; i < tableLines.size(); i++) {
			printTableLine((String[]) tableLines.get(i), out, columnWidths,
					' ', '|');
			printTableLine(separator, out, columnWidths, '-', '+');
		}
	}

	private void printTableLine(String[] columns, StringBuffer out,
			int[] columnWidths, char fill, char separator) {
		out.append(separator);
		for (int i = 0; i < size; i++) {
			printStartingText(columns[i], columnWidths[i], out, fill);
			out.append(separator);
		}
		out.append(DSConstants.EOL);
	}

	private int[] columnsMaxWith(ArrayList tableLines) {
		int[] result = new int[size];
		for (int i = 0; i < tableLines.size(); i++) {
			String[] strings = (String[]) tableLines.get(i);
			for (int c = 0; c < result.length; c++) {
				result[c] = Math.max(result[c], strings[c].length());
			}
		}
		return result;
	}

	private void printCenteredText(String text, int length, StringBuffer out,
			char fill) {
		out.append(fill);
		int difference = length - text.length();
		int prefix, suffix;
		if (difference % 2 == 0) {
			prefix = suffix = difference / 2;
		} else {
			suffix = difference / 2;
			prefix = suffix + 1;
		}

		for (int i = 0; i < prefix; i++) {
			out.append(fill);
		}

		out.append(text);

		for (int i = 0; i < suffix; i++) {
			out.append(fill);
		}
		out.append(fill);
	}

	private void printStartingText(String text, int length, StringBuffer out,
			char fill) {
		out.append(fill);
		int difference = length - text.length();

		out.append(text);

		for (int i = 0; i < difference; i++) {
			out.append(fill);
		}
		out.append(fill);
	}

	/**
	 * Generate the table lines array.
	 * 
	 * @return
	 * @throws LocalizationException
	 */
	private ArrayList makeTableLines() throws LocalizationException {
		final String ms = MS;
		ArrayList<String[]> result = new ArrayList<String[]>();
		// deploy service time - some specific handling is done here
		long containersDuration = getContainersDuration();
		long dsDuration = (getDuration() - containersDuration);
		String dsRatio = getPercent(dsDuration, getDuration());

		if (size == SIZE_ALL) {
			// table header
			result.add(new String[] { APP_NAME + getApplicationName(),
					DURATION, RATIO, CPU_DURATION, ALLOCATED_MEMORY,
					FREED_MEMORY, HOLD_MEMORY });
			// root node - full operations time
			final String cpuDurationDesc = formatCpuDuration(getCpuDuration(),
					ms);
			result.add(new String[] { sRootNodeName, getDuration() + " " + ms,
					"100.00 %", cpuDurationDesc, " - ", " - ", " - " });
			// deploy service time
			final AllocationStatisticRecord asRecord = getAllocationStatisticRecord(
					getTransactionName(), DEPLOY_SERVICE, null);
			final String allocatedMemory = formatAllocatedMemory(asRecord);
			final String freedMemory = formatFreedMemory(asRecord);
			final String holdMemory = formatHoldMemory(asRecord);
			result.add(new String[] {
					TREE_LEVEL_SPACE + sDeployServiceNodeName,
					dsDuration + " " + ms, dsRatio, " - ", allocatedMemory,
					freedMemory, holdMemory });
			// traverse the deploy service sub tree to create the rows
			TimeStatisticNode oDeployServiceNode = (TimeStatisticNode) registeredOpTimeStat
					.get(sDeployServiceNodePath);
			traverseNodeAndFillTableRows(oDeployServiceNode, result, ms);
			// other deploy operations
			if (oDeployServiceNode.getSubOpStat() != null) {
				long otherDsOperations = dsDuration
						- getDeployOperationsDuration();
				result.add(new String[] {
						TREE_LEVEL_SPACE + TREE_LEVEL_SPACE + OTHER,
						otherDsOperations + " " + ms,
						getPercent(otherDsOperations, getDuration()), " - ",
						" - ", " - ", " - " });
			}
			// all containers time
			result
					.add(new String[] {
							TREE_LEVEL_SPACE + sAllContainersNodeName,
							containersDuration + " " + ms,
							getContainersDurationPercent(), " - ", " - ",
							" - ", " - " });
		} else {
			// table header
			result.add(new String[] { APP_NAME + getApplicationName(),
					DURATION, RATIO });
			// root node - full operations time
			result.add(new String[] { sRootNodeName, getDuration() + " " + ms,
					"100.00 %" });
			// deploy service time
			result.add(new String[] {
					TREE_LEVEL_SPACE + sDeployServiceNodeName,
					dsDuration + " " + ms, dsRatio });
			// traverse the deploy service sub tree to create the rows
			TimeStatisticNode oDeployServiceNode = (TimeStatisticNode) registeredOpTimeStat
					.get(sDeployServiceNodePath);
			traverseNodeAndFillTableRows(oDeployServiceNode, result, ms);
			// other deploy operations
			if (oDeployServiceNode.getSubOpStat() != null) {
				long otherDsOperations = dsDuration
						- getDeployOperationsDuration();
				result.add(new String[] {
						TREE_LEVEL_SPACE + TREE_LEVEL_SPACE + OTHER,
						otherDsOperations + " " + ms,
						getPercent(otherDsOperations, getDuration()) });
			}
			// all containers time
			result.add(new String[] {
					TREE_LEVEL_SPACE + sAllContainersNodeName,
					containersDuration + " " + ms,
					getContainersDurationPercent() });
		}
		// traverse all containers sub tree
		TimeStatisticNode oAllContainersNode = (TimeStatisticNode) registeredOpTimeStat
				.get(sAllContainersNodePath);
		traverseNodeAndFillTableRows(oAllContainersNode, result, ms);

		return result;
	}

	/**
	 * Method traverses the complete sub tree of the given node and builds the
	 * table.
	 * 
	 * @param oNode
	 * @param result
	 * @param ms
	 * @author Todor Stoitsev
	 */
	private void traverseNodeAndFillTableRows(TimeStatisticNode oNode,
			ArrayList<String[]> result, String ms) {
		List subNodes = oNode.getSubOpStat();
		if (subNodes == null)
			return;

		for (int i = 0; i < subNodes.size(); i++) {
			TimeStatisticNode subNode = (TimeStatisticNode) subNodes.get(i);
			// precaution
			if (subNode == null)
				continue;
			long duration = subNode.getDuration();
			// get sub node name
			String sSubNodeName = subNode.getNodeName();
			if (subNode.getAppName() != null
					&& subNode.getAppName().length() > 0) {
				sSubNodeName += " (" + subNode.getAppName() + ")";
			}
			if (size == SIZE_ALL) {
				final String cpuDurationDesc = formatCpuDuration(subNode
						.getCpuDuration(), ms);
				final AllocationStatisticRecord asRecord = getAllocationStatisticRecord(
						oNode.getNodeName(), subNode.getNodeName(), subNode
								.getAppName());
				final String allocatedMemory = formatAllocatedMemory(asRecord);
				final String freedMemory = formatFreedMemory(asRecord);
				final String holdMemory = formatHoldMemory(asRecord);
				result.add(new String[] {
						getTreeLevelSpace(subNode) + sSubNodeName,
						duration + " " + ms,
						getPercent(duration, getDuration()), cpuDurationDesc,
						allocatedMemory, freedMemory, holdMemory });
			} else {
				result.add(new String[] {
						getTreeLevelSpace(subNode) + sSubNodeName,
						duration + " " + ms,
						getPercent(duration, getDuration()) });
			}
			// recurse to sub statistic
			traverseNodeAndFillTableRows(subNode, result, ms);
		}
	}

	/**
	 * This method formats the passed duration for the CPU time statistics
	 * output.
	 * 
	 * @param cpuDuration
	 *            - the calculated CPU duration
	 * @param ms
	 *            - the milliseconds notation to use as suffix
	 * @return
	 */
	private String formatCpuDuration(float cpuDuration, String ms) {
		if (cpuDuration == -1) {
			return " - ";
		}
		if (cpuDuration == 0) {
			return "0 " + ms;
		}
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format("%.3f", cpuDuration);
		while (sb.charAt(sb.length() - 1) == '0') {
			sb.deleteCharAt(sb.length() - 1);
			if (sb.charAt(sb.length() - 1) == ','
					|| sb.charAt(sb.length() - 1) == '.') {
				sb.deleteCharAt(sb.length() - 1);
				break;
			}
		}
		return sb.toString() + " " + ms;
	}

	private AllocationStatisticRecord getAllocationStatisticRecord(
			String parentNodeName, String nodeName, String appName) {
		final Iterator<String> keyIter = string2ASRecord.keySet().iterator();
		String key = null;
		AllocationStatisticRecord asRecord = null;
		while (keyIter.hasNext()) {
			key = keyIter.next();
			if ((key.endsWith(parentNodeName + ":" + nodeName) || (key
					.endsWith(nodeName + ":" + transactionName + ":" + INIT_OP)))
					&& (appName == null || key.indexOf(appName) != -1)) {
				asRecord = string2ASRecord.remove(key);
				return asRecord;
			}
		}
		return null;
	}

	private String formatAllocatedMemory(AllocationStatisticRecord asRecord) {
		return asRecord != null ? asRecord.getAllocatedObjects() + " " + OBJ
				+ " = " + asRecord.getAllocatedBytes() + " " + B : " - ";
	}

	private String formatFreedMemory(AllocationStatisticRecord asRecord) {
		return asRecord != null ? asRecord.getFreedObjects() + " " + OBJ
				+ " = " + asRecord.getFreedBytes() + " " + B : " - ";
	}

	private String formatHoldMemory(AllocationStatisticRecord asRecord) {
		return asRecord != null ? asRecord.getHoldObjects() + " " + OBJ + " = "
				+ asRecord.getHoldBytes() + " " + B : " - ";
	}

	/**
	 * Creates space at tree level node start.
	 * 
	 * @param sSingleLevelSpace
	 * @param oNode
	 * @return
	 * @author Todor Stoitsev
	 */
	private String getTreeLevelSpace(TimeStatisticNode oNode) {
		if (oNode == null)
			return TREE_LEVEL_SPACE;

		String sPath = oNode.getPath();
		StringTokenizer tokenizer = new StringTokenizer(sPath,
				TimeStatisticNode.TREE_PATH_SEP, false);
		StringBuffer sb = new StringBuffer();
		// add space from top level
		// onwards - meaning skip one path separator
		boolean bPassedOnce = false;
		while (tokenizer.hasMoreTokens()) {
			tokenizer.nextToken();
			if (bPassedOnce)
				sb.append(TREE_LEVEL_SPACE);
			else
				bPassedOnce = true;
		}
		return sb.toString();
	}

	/**
	 * Retrieves the name of an operation with the given path.
	 * 
	 * @param sOperationAbsPath
	 * @return - an array with the node name at 0 index and relative node path
	 *         at 1 index.
	 */
	private String[] getContainerOperationRelativePathAndName(
			String sContainerOperationPathInclName) {
		if (sContainerOperationPathInclName == null)
			return new String[] { "<UNKNOWN>", "<UNKNOWN>" };
		int idx = sContainerOperationPathInclName
				.lastIndexOf(TimeStatisticNode.TREE_PATH_SEP);
		if (idx != -1) {
			String sName = sContainerOperationPathInclName.substring(idx + 1);
			String sRelPath = sContainerOperationPathInclName.substring(0, idx);
			return new String[] { sName, sRelPath };
		}
		return new String[] { sContainerOperationPathInclName,
				sContainerOperationPathInclName };
	}

	private String getPercent(long nominator, long denominator) {
		String result = Double.toString(100.0 * nominator
				/ Math.max(denominator, 1));
		int dotIndex = result.indexOf('.');
		return result.substring(0, (dotIndex + 3 > result.length() ? result
				.length() : dotIndex + 3))
				+ " %";
	}
}
