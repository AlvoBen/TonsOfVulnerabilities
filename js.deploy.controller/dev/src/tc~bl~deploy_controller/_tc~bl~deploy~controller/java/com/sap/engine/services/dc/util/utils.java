package com.sap.engine.services.dc.util;

public class Utils {

	private static ThreadLocal<Boolean> ONLINE_DEPLOYMENT_OF_CORE_COMPONENTS = new ThreadLocal<Boolean>();

	public static void setOnlineDeploymentOfCoreComponents(boolean value) {

		if (value == false) {
			ONLINE_DEPLOYMENT_OF_CORE_COMPONENTS.set(null);

		} else {
			ONLINE_DEPLOYMENT_OF_CORE_COMPONENTS.set(true);
		}

	}

	public static boolean getOnlineDeploymentOfCoreComponents() {

		Boolean value = ONLINE_DEPLOYMENT_OF_CORE_COMPONENTS.get();
		if (value == null) {
			return false;
		}
		return value;

	}

}
