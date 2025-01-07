package com.sap.engine.services.dc.cm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.validate.DeployValidationBatchResult;
import com.sap.engine.services.dc.cm.validate.UndeployValidationBatchResult;
import com.sap.engine.services.dc.cm.validate.ValidationBatchResult;
import com.sap.engine.services.dc.util.Constants;

/**
 * Generates summaries from deploy, undeploy and validate results and in general
 * is the same like <code>DAResultUtils</code>
 * 
 * @author I024090
 * @since 7.20
 */
public class ResultUtils {

	private static final String DEPLOY = "Deploy";
	private static final String UNDEPLOY = "Undeploy";
	private static final String VALIDATE = "Validate";

	public static String logSummary4Validate(ValidationBatchResult[] vRes) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vRes.length; i++) {
			if (vRes[i] instanceof DeployValidationBatchResult) {
				sb.append(logSummary(((DeployValidationBatchResult) vRes[i])
						.getDeploymentBatchItems(), VALIDATE + "(" + (i + 1)
						+ "/" + vRes.length + ")"));
			} else if (vRes[i] instanceof UndeployValidationBatchResult) {
				sb.append(logSummary(((UndeployValidationBatchResult) vRes[i])
						.getUndeployItems(), VALIDATE + "(" + (i + 1) + "/"
						+ vRes.length + ")"));
			} else {
				throw new IllegalStateException("Cannot build summary for "
						+ vRes + ".");
			}
		}
		return sb.toString();
	}

	public static String logSummary4Deploy(Collection<?> items) {
		return logSummary(items, DEPLOY);
	}

	public static String logSummary4Undeploy(Collection<?> items) {
		return logSummary(items, UNDEPLOY);
	}

	private static String logSummary(Collection<?> items, String type) {
		final Map<Object, List<CommonItem>> status2Items4Sca = new HashMap<Object, List<CommonItem>>();
		final Map<Object, List<CommonItem>> status2Items4Sda = new HashMap<Object, List<CommonItem>>();
		split(items, status2Items4Sca, status2Items4Sda);

		final StringBuilder sb = new StringBuilder();
		sb.append("===== Summary - ");
		sb.append(type);
		sb.append(" Result - Start =====");
		sb.append(Constants.EOL);
		traceStatuses2Count(sb, status2Items4Sca, status2Items4Sda);
		traceStatuses2Id(sb, status2Items4Sca, status2Items4Sda);
		sb.append("===== Summary - ");
		sb.append(type);
		sb.append(" Result - End =====");
		sb.append(Constants.EOL);
		return sb.toString();
	}

	private static void split(Collection<?> items,
			Map<Object, List<CommonItem>> status2Items4Sca,
			Map<Object, List<CommonItem>> status2Items4Sda) {
		if (items == null) {
			return;
		}
		final Iterator<?> iter = items.iterator();
		CommonItem cItem = null;
		List<CommonItem> cItems = null;
		while (iter.hasNext()) {
			cItem = new CommonItem(iter.next());
			if (cItem.isSca()) {
				cItems = status2Items4Sca.get(cItem.getStatus());
				if (cItems == null) {
					status2Items4Sca.put(cItem.getStatus(),
							new ArrayList<CommonItem>());
					cItems = status2Items4Sca.get(cItem.getStatus());
				}
			} else {
				cItems = status2Items4Sda.get(cItem.getStatus());
				if (cItems == null) {
					status2Items4Sda.put(cItem.getStatus(),
							new ArrayList<CommonItem>());
					cItems = status2Items4Sda.get(cItem.getStatus());
				}
			}
			cItems.add(cItem);
			// add childs, if any
			split(cItem.getChilds(), status2Items4Sca, status2Items4Sda);
		}
	}

	private static void traceStatuses2Count(StringBuilder sb,
			Map<Object, List<CommonItem>> status2Items4Sca,
			Map<Object, List<CommonItem>> status2Items4Sda) {
		sb.append("------------------------");
		sb.append(Constants.EOL);
		sb.append("Type | Status  : Count");
		sb.append(Constants.EOL);
		sb.append("------------------------");
		sb.append(Constants.EOL);
		traceStatuses2Count(sb, status2Items4Sca, "SCA(s)");
		traceStatuses2Count(sb, status2Items4Sda, "SDA(s)");
		sb.append("------------------------");
		sb.append(Constants.EOL);
	}

	private static void traceStatuses2Count(StringBuilder sb,
			Map<Object, List<CommonItem>> status2Items4Sca, String componetType) {
		sb.append(" > ");
		sb.append(componetType);
		sb.append(Constants.EOL);
		final Iterator<Object> iter = status2Items4Sca.keySet().iterator();
		Object status = null;
		List<CommonItem> cItems = null;
		while (iter.hasNext()) {
			status = iter.next();
			cItems = status2Items4Sca.get(status);
			sb.append("   - [");
			sb.append(status);
			sb.append("] : [");
			sb.append(cItems.size());
			sb.append("]");
			sb.append(Constants.EOL);
		}
	}

	private static void traceStatuses2Id(StringBuilder sb,
			Map<Object, List<CommonItem>> status2Items4Sca,
			Map<Object, List<CommonItem>> status2Items4Sda) {
		sb.append("------------------------");
		sb.append(Constants.EOL);
		sb.append("Type | Status  : Id");
		sb.append(Constants.EOL);
		sb.append("------------------------");
		sb.append(Constants.EOL);
		traceStatuses2Id(sb, status2Items4Sca, "SCA(s)");
		traceStatuses2Id(sb, status2Items4Sda, "SDA(s)");
		sb.append("------------------------");
		sb.append(Constants.EOL);
	}

	private static void traceStatuses2Id(StringBuilder sb,
			Map<Object, List<CommonItem>> status2Items4Sca, String componetType) {
		sb.append(" > ");
		sb.append(componetType);
		sb.append(Constants.EOL);
		final Iterator<Object> iter = status2Items4Sca.keySet().iterator();
		Object status = null;
		Object parent = null;
		Iterator<CommonItem> cItems = null;
		CommonItem cItem = null;
		while (iter.hasNext()) {
			status = iter.next();
			sb.append("   - [");
			sb.append(status);
			sb.append("] : ");
			cItems = status2Items4Sca.get(status).iterator();
			while (cItems.hasNext()) {
				cItem = cItems.next();
				{// parent handling
					parent = cItem.getParent();
					if (parent != null) {
						sb.append(parent);
						sb.append("/");
					}
				}
				sb.append(cItem.getId());
				sb.append(", ");
			}
			sb.append(Constants.EOL);
		}
	}

	public static class CommonItem {
		private final Object item;

		public CommonItem(Object item) {
			this.item = item;
		}

		private boolean isSca() {
			if (item instanceof GenericUndeployItem) {
				if (item instanceof ScaUndeployItem) {
					return true;
				} else if (item instanceof UndeployItem) {
					return false;
				}
			} else if (item instanceof DeploymentBatchItem) {
				if (item instanceof CompositeDeploymentItem) {
					return true;
				} else if (item instanceof DeploymentItem) {
					return false;
				}
			}
			throw new IllegalStateException("Cannot decide whether " + item
					+ " is sca or not.");
		}

		private Object getStatus() {
			if (item instanceof GenericUndeployItem) {
				return ((GenericUndeployItem) item).getUndeployItemStatus();
			} else if (item instanceof DeploymentBatchItem) {
				return ((DeploymentBatchItem) item).getDeploymentStatus();
			}
			throw new IllegalStateException("Cannot return the status of "
					+ item + ".");
		}

		private Object getId() {
			if (item instanceof GenericUndeployItem) {
				return ((GenericUndeployItem) item).getId();
			} else if (item instanceof DeploymentBatchItem) {
				return ((DeploymentBatchItem) item).getBatchItemId();
			}
			throw new IllegalStateException("Cannot return the id of " + item
					+ ".");
		}

		/**
		 * 
		 * @return null or an <code>Collection</code> with
		 *         <code>UndeployItem</code> or <code>DeployItem</code>
		 */
		private Collection getChilds() {
			if (item instanceof GenericUndeployItem) {
				if (item instanceof ScaUndeployItem) {
					// only empty sca can be undeployed
					return null;
				} else if (item instanceof UndeployItem) {
					return null;
				}
			} else if (item instanceof DeploymentBatchItem) {
				if (item instanceof CompositeDeploymentItem) {
					return ((CompositeDeploymentItem) item)
							.getDeploymentItems();
				} else if (item instanceof DeploymentItem) {
					return null;
				}
			}
			throw new IllegalStateException("Cannot decide whether " + item
					+ " has childs or not.");
		}

		/**
		 * 
		 * @return null or <code>Object</code>
		 */
		private Object getParent() {
			if (item instanceof GenericUndeployItem) {
				if (item instanceof ScaUndeployItem) {
					// only empty sca can be undeployed
					return null;
				} else if (item instanceof UndeployItem) {
					return null;
				}
			} else if (item instanceof DeploymentBatchItem) {
				if (item instanceof CompositeDeploymentItem) {
					return null;
				} else if (item instanceof DeploymentItem) {
					return ((DeploymentItem) item).getParentId();
				}
			}
			throw new IllegalStateException("Cannot decide whether " + item
					+ " has childs or not.");
		}
	}

}
