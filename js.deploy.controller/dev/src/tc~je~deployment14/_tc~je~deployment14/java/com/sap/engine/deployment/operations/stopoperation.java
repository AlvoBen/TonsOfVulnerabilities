package com.sap.engine.deployment.operations;

import java.util.ArrayList;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.ProgressEvent;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.proxy.DeploymentProxy;
import com.sap.engine.deployment.status.SAPDeploymentStatus;
import com.sap.engine.deployment.exceptions.SAPRemoteException;

/**
 * @author Mariela Todorova
 */
public class StopOperation extends Operation {
	private static final Location location = Location
			.getLocation(StopOperation.class);

	public StopOperation(DeploymentProxy dProxy, SAPTargetModuleID[] modules) {
		super(dProxy);
		this.targetModules = modules;
		this.status = new SAPDeploymentStatus(CommandType.STOP);
	}

	public void run() {
		Logger.trace(location, Severity.PATH, "Stop operation started for "
				+ targetModules);

		if (targetModules == null || targetModules.length == 0) {
			// should not happen - already checked!
			return;
		}

		ArrayList list = new ArrayList();

		try {
			for (int i = 0; i < targetModules.length; i++) {
				if (targetModules[i].getParentTargetModuleID() != null) {
					Logger.trace(location, Severity.DEBUG,
							"Skipping non-root target module "
									+ targetModules[i].toString());
					continue;
				}

				list.add(targetModules[i]);
			}

			Logger.trace(location, Severity.DEBUG, "Modules to stop: "
					+ list.toString());
			proxy.stop((SAPTargetModuleID[]) list
					.toArray(new SAPTargetModuleID[0]));

			for (int i = 0; i < list.size(); i++) {
				this.fireProgressEvent(new ProgressEvent(this,
						(SAPTargetModuleID) list.get(i), status));
			}
		} catch (SAPRemoteException re) {
			Logger.logThrowable(location, Severity.ERROR, "Could not stop {0}",
					new String[] { list.toString() }, re);
			this.status.setStateType(StateType.FAILED);
			this.status.setMessage(re.getMessage());

			for (int i = 0; i < list.size(); i++) {
				this.fireProgressEvent(new ProgressEvent(this,
						(SAPTargetModuleID) list.get(i), status));
			}

			return;
		}

		this.status.setStateType(StateType.COMPLETED);
		Logger.trace(location, Severity.DEBUG, "Stop operation completed for "
				+ list.toString());

		for (int i = 0; i < list.size(); i++) {
			Logger.log(location, Severity.INFO, "Target module " + list.get(i)
					+ " stopped successfully");
			this.fireProgressEvent(new ProgressEvent(this,
					(SAPTargetModuleID) list.get(i), status));
		}
	}

}