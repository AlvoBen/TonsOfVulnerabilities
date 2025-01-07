package com.sap.engine.services.dc.api.deploy;

import java.util.HashMap;
import java.util.Map;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>The deploy workflow strategy specifies the behavior of the J2EE Engine in
 * case of offline deployment.
 * <UL>
 * <B>The possible deploy workflow strategies are:</B>
 * <LI>NORMAL - the J2EE Engine is restarted only one time in case of offline
 * deployment( The default value ).</LI>
 * <LI>SAFETY - the J2EE Engine is restarted two times in case of offline
 * deployment. First, the J2EE Engine is stop and started in &quot;safe&quot;
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
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#getDeployWorkflowStrategy()
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#setDeployWorkflowStrategy(DeployWorkflowStrategy)
 * 
 */
public final class DeployWorkflowStrategy {
	/**
	 *&quot;NORMAL&quot; mode.
	 */
	public static final DeployWorkflowStrategy NORMAL = new DeployWorkflowStrategy(
			new Integer(0), "normal");
	/**
	 *&quot;SAFETY&quot; mode.
	 */
	public static final DeployWorkflowStrategy SAFETY = new DeployWorkflowStrategy(
			new Integer(1), "safety");
	/**
	 *&quot;ROLLING&quot; mode.
	 * 
	 * @deprecated The item will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public static final DeployWorkflowStrategy ROLLING = new DeployWorkflowStrategy(
			new Integer(2), "rolling");

	private static final Map STRATEGY_MAP = new HashMap();

	private final Integer id;
	private final String name;
	private final String toString;

	static {
		STRATEGY_MAP.put(NORMAL.getName(), NORMAL);
		STRATEGY_MAP.put(SAFETY.getName(), SAFETY);
		STRATEGY_MAP.put(ROLLING.getName(), ROLLING);
	}

	/**
	 * Returns a deploy workflow strategy by a given strategy name.
	 * 
	 * @param name
	 *            of deploy workflow strategy
	 * @return deploy workflow strategy
	 */
	public static DeployWorkflowStrategy getDeployStrategyByName(String name) {
		return (DeployWorkflowStrategy) STRATEGY_MAP.get(name);
	}

	private DeployWorkflowStrategy(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.toString = name + " deploy strategy";
	}

	private Integer getId() {
		return this.id;
	}

	/**
	 * Returns the name of this deploy workflow strategy.
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

		if (!(obj instanceof DeployWorkflowStrategy)) {
			return false;
		}

		DeployWorkflowStrategy other = (DeployWorkflowStrategy) obj;

		if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

}
