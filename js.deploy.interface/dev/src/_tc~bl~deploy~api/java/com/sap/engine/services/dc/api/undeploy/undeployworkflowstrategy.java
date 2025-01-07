package com.sap.engine.services.dc.api.undeploy;

import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The undeploy workflow strategy specifies the behavior of the J2EE Engine
 * in case of offline undeployment.
 * <UL>
 * <B>The possible undeploy workflow strategies are:</B>
 * <LI>NORMAL - the J2EE Engine is restarted only one time in case of offline
 * undeployment( The default value ).</LI>
 * <LI>SAFETY - the J2EE Engine is restarted two times in case of offline
 * undeployment. First, the J2EE Engine is stop and started in &quot;safe&quot;
 * mode with action &quot;DEPLOY&quot; and then after all the deployments have
 * been performed it is restarted in &quot;normal&quot; mode with action
 * &quot;NONE&quot;.</LI>
 * </UL>
 * </DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-20</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.undeploy.UndeployProcessor#setUndeployWorkflowStrategy(UndeployWorkflowStrategy)
 */
public final class UndeployWorkflowStrategy {

	/** NORMAL undeploy workflow strategy */
	public static final UndeployWorkflowStrategy NORMAL = new UndeployWorkflowStrategy(
			new Integer(0), "normal");
	/** SAFETY undeploy workflow strategy */
	public static final UndeployWorkflowStrategy SAFETY = new UndeployWorkflowStrategy(
			new Integer(1), "safety");

	private static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(NORMAL.getName(), NORMAL);
		STRATEGY_MAP.put(SAFETY.getName(), SAFETY);
	}

	/**
	 * Returns an undeploy workflow strategy by name.
	 * 
	 * @param name
	 *            of undeploy workflow strategy
	 * @return UndeployWorkflowStrategy object
	 */
	public static UndeployWorkflowStrategy getUndeployWorkflowStrategyByName(
			String name) {
		return (UndeployWorkflowStrategy) STRATEGY_MAP.get(name);
	}

	private UndeployWorkflowStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.toString = name + " deploy strategy";
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this undeploy workflow strategy.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.toString;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof UndeployWorkflowStrategy)) {
			return false;
		}

		UndeployWorkflowStrategy other = (UndeployWorkflowStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
