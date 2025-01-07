package com.sap.engine.services.dc.gd.impl;

import static com.sap.engine.services.dc.util.RollingUtils.createTestInfo;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.cluster.monitor.ClusterMonitor;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ClusterStatus;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.ClusterInfo;
import com.sap.engine.services.dc.util.ClusterUtils;

public abstract class AbstractRollingResultAnalyzer {

	protected static final String UNDETERMINED_INSTANCE_STATUS = "Instance status is undetermined after deployment.";

	protected ClusterStatus analyseUpdateClusterStatus(
			InstanceStatus instanceStatus) {
		ClusterStatus clusterStatus;
		if (instanceStatus
				.equals(InstanceStatus.NOT_PRODUCTIVE_AND_FAILED_TO_UPDATE)) {
			clusterStatus = ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK;
		} else if (instanceStatus
				.equals(InstanceStatus.NOT_PRODUCTIVE_AND_NEED_VALIDATION)) {
			clusterStatus = ClusterStatus.PRODUCTIVE_BUT_NEED_VALIDATION;
		} else {
			clusterStatus = ClusterStatus.PRODUCTIVE_BUT_NEED_ROLL_BACK;
		}
		return clusterStatus;
	}

}
