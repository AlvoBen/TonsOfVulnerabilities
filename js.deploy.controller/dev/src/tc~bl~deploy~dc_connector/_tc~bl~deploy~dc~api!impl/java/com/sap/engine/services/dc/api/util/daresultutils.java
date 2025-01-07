package com.sap.engine.services.dc.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.validate.DeployValidationResult;
import com.sap.engine.services.dc.api.validate.UndeployValidationResult;
import com.sap.engine.services.dc.api.validate.ValidationResult;

/**
 * Generates summaries from deploy, undeploy and validate results and in general
 * is the same like <code>ResultUtils</code> without generics
 * 
 * @author I024090
 * @since 7.20
 */
public class DAResultUtils {

	private static final String DEPLOY = "Deploy";
	private static final String UNDEPLOY = "Undeploy";
	private static final String VALIDATE = "Validate";

	public static String logSummary4Validate(ValidationResult[] vRes) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vRes.length; i++) {
			if (vRes[i] instanceof DeployValidationResult) {
				sb.append(logSummary(((DeployValidationResult) vRes[i])
						.getDeploymentBatchItems(), VALIDATE + "(" + (i + 1)
						+ "/" + vRes.length + ")"));
			} else if (vRes[i] instanceof UndeployValidationResult) {
				sb.append(logSummary(((UndeployValidationResult) vRes[i])
						.getUndeploymentItems(), VALIDATE + "(" + (i + 1) + "/"
						+ vRes.length + ")"));
			} else {
				throw new IllegalStateException("Cannot build summary for "
						+ vRes + ".");
			}
		}
		return sb.toString();
	}

	public static String logSummary4Deploy(Object[] items) {
		return logSummary(items, DEPLOY);
	}

	public static String logSummary4Undeploy(Object[] items) {
		return logSummary(items, UNDEPLOY);
	}

	private static String logSummary(Object[] items, String type) {
		final Map status2Items4Sca = new HashMap();
		final Map status2Items4Sda = new HashMap();
		split(items, status2Items4Sca, status2Items4Sda);

		final StringBuffer sb = new StringBuffer(DAConstants.EOL);
		sb.append("===== Summary - ");
		sb.append(type);
		sb.append(" Result - Start =====");
		sb.append(DAConstants.EOL);
		traceStatuses2Count(sb, status2Items4Sca, status2Items4Sda);
		traceStatuses2Id(sb, status2Items4Sca, status2Items4Sda);
		sb.append("===== Summary - ");
		sb.append(type);
		sb.append(" Result - End =====");
		sb.append(DAConstants.EOL);
		return sb.toString();
	}

	private static void split(Object[] items, Map status2Items4Sca,
			Map status2Items4Sda) {
		if (items == null) {
			return;
		}
		CommonItem cItem = null;
		List cItems = null;
		for (int i = 0; i < items.length; i++) {
			cItem = new CommonItem(items[i]);
			if (cItem.isSca()) {
				cItems = (List) status2Items4Sca.get(cItem.getStatus());
				if (cItems == null) {
					status2Items4Sca.put(cItem.getStatus(), new ArrayList());
					cItems = (List) status2Items4Sca.get(cItem.getStatus());
				}
			} else {
				cItems = (List) status2Items4Sda.get(cItem.getStatus());
				if (cItems == null) {
					status2Items4Sda.put(cItem.getStatus(), new ArrayList());
					cItems = (List) status2Items4Sda.get(cItem.getStatus());
				}
			}
			cItems.add(cItem);
			// add childs, if any
			split(cItem.getChilds(), status2Items4Sca, status2Items4Sda);
		}
	}

	private static void traceStatuses2Count(StringBuffer sb,
			Map status2Items4Sca, Map status2Items4Sda) {
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
		sb.append("Type | Status  : Count");
		sb.append(DAConstants.EOL);
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
		traceStatuses2Count(sb, status2Items4Sca, "SCA(s)");
		traceStatuses2Count(sb, status2Items4Sda, "SDA(s)");
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
	}

	private static void traceStatuses2Count(StringBuffer sb,
			Map status2Items4Sca, String componetType) {
		sb.append(" > ");
		sb.append(componetType);
		sb.append(DAConstants.EOL);
		final Iterator iter = status2Items4Sca.keySet().iterator();
		Object status = null;
		List cItems = null;
		while (iter.hasNext()) {
			status = iter.next();
			cItems = (List) status2Items4Sca.get(status);
			sb.append("   - [");
			sb.append(status);
			sb.append("] : [");
			sb.append(cItems.size());
			sb.append("]");
			sb.append(DAConstants.EOL);
		}
	}

	private static void traceStatuses2Id(StringBuffer sb, Map status2Items4Sca,
			Map status2Items4Sda) {
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
		sb.append("Type | Status  : Id");
		sb.append(DAConstants.EOL);
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
		traceStatuses2Id(sb, status2Items4Sca, "SCA(s)");
		traceStatuses2Id(sb, status2Items4Sda, "SDA(s)");
		sb.append("------------------------");
		sb.append(DAConstants.EOL);
	}

	private static void traceStatuses2Id(StringBuffer sb, Map status2Items4Sca,
			String componetType) {
		sb.append(" > ");
		sb.append(componetType);
		sb.append(DAConstants.EOL);
		final Iterator iter = status2Items4Sca.keySet().iterator();
		Object status = null;
		Iterator cItems = null;
		Object parent = null;
		CommonItem cItem = null;
		while (iter.hasNext()) {
			status = iter.next();
			sb.append("   - [");
			sb.append(status);
			sb.append("] : ");
			cItems = ((List) status2Items4Sca.get(status)).iterator();
			while (cItems.hasNext()) {
				cItem = (CommonItem) cItems.next();
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
			sb.append(DAConstants.EOL);
		}
	}

	public static class CommonItem {
		private final Object item;

		public CommonItem(Object item) {
			this.item = item;
		}

		private boolean isSca() {
			if (item instanceof UndeployItem) {
				if (item instanceof ScaUndeployItem) {
					return true;
				} else if (item instanceof UndeployItem) {
					return false;
				}
			} else if (item instanceof DeployItem) {
				final Sdu sdu = ((DeployItem) item).getSdu();
				if (sdu instanceof Sca) {
					return true;
				} else if (sdu instanceof Sda) {
					return false;
				} else {
					//in case of corrupted sdu file
					return false;
				}
			}
			throw new IllegalStateException("Cannot decide whether " + item
					+ " is sca or not.");
		}

		private Object getStatus() {
			if (item instanceof UndeployItem) {
				return ((UndeployItem) item).getUndeployItemStatus();
			} else if (item instanceof DeployItem) {
				return ((DeployItem) item).getDeployItemStatus();
			}
			throw new IllegalStateException("Cannot return the status of "
					+ item + ".");
		}

		private Object getId() {
			if (item instanceof UndeployItem) {
				return ((UndeployItem) item).getVendor() + "_"
						+ ((UndeployItem) item).getName();
			} else if (item instanceof DeployItem) {
				final DeployItem di = ((DeployItem) item);
				if (di.getSdu() != null) {
					return di.getSdu().getId();					
				} else {
					//in case of corrupted sdu file
					return di.getArchive();
				}
			}
			throw new IllegalStateException("Cannot return the id of " + item
					+ ".");
		}

		/**
		 * 
		 * @return null or an <code>Collection</code> with
		 *         <code>UndeployItem</code> or <code>DeployItem</code>
		 */
		private Object[] getChilds() {
			if (item instanceof UndeployItem) {
				if (item instanceof ScaUndeployItem) {
					// only empty sca can be undeployed
					return null;
				} else if (item instanceof UndeployItem) {
					return null;
				}
			} else if (item instanceof DeployItem) {
				final Sdu sdu = ((DeployItem) item).getSdu();
				if (sdu instanceof Sca) {
					return ((DeployItem) item).getContainedDeployItems();
				} else if (sdu instanceof Sda) {
					return null;
				}else {
					//in case of corrupted sdu file
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
			if (item instanceof UndeployItem) {
				if (item instanceof ScaUndeployItem) {
					// only empty sca can be undeployed
					return null;
				} else if (item instanceof UndeployItem) {
					return null;
				}
			} else if (item instanceof DeployItem) {
				final Sdu sdu = ((DeployItem) item).getSdu();
				if (sdu instanceof Sca) {
					return null;
				} else if (sdu instanceof Sda) {
					// there is not information for its parent
					return null;
				}else {
					//in case of corrupted sdu file
					return null;
				}
			}
			throw new IllegalStateException("Cannot decide whether " + item
					+ " has childs or not.");
		}
	}

}
