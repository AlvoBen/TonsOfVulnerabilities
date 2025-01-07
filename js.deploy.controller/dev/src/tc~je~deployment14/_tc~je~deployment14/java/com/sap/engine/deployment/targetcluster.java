package com.sap.engine.deployment;

import java.util.ArrayList;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

/**
 * @author Mariela Todorova
 */
public class TargetCluster {
	private static final Location location = Location
			.getLocation(TargetCluster.class);
	private String name = null;
	private ArrayList targets = new ArrayList();

	public void setName(String clName) {
		this.name = clName;
		Logger.trace(location, Severity.DEBUG, "Cluster name " + name);
	}

	public String getName() {
		return this.name;
	}

	public SAPTarget[] getTargets() {
		return (SAPTarget[]) this.targets.toArray(new SAPTarget[0]);
	}

	// synchronization?
	// if a target dies? or a new one connects to the cluster?
	// information will be polled before operation start
	public void addTarget(SAPTarget target) {
		Logger.trace(location, Severity.DEBUG, "Adding target " + target);

		if (target == null) {
			return;
		}

		if (targets == null) {
			targets = new ArrayList();
		}

		if (!targets.contains(target)) {
			targets.add(target);
		}
	}

	public void removeTarget(SAPTarget target) {
		Logger.trace(location, Severity.DEBUG, "Removing target " + target);

		if (target == null) {
			return;
		}

		if (targets != null) {
			targets.remove(target);
		}
	}

	public boolean containsTarget(SAPTarget target) {
		if (target == null) {
			return false;
		}

		return targets.contains(target);
	}

}
