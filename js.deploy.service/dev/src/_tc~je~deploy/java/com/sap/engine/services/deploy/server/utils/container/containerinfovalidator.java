package com.sap.engine.services.deploy.server.utils.container;

import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.load.ClassInfo;
import com.sap.engine.frame.core.load.ClassWithLoaderInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.tc.logging.Location;

/**
 * Utility class used to validate the container info.
 * 
 * @author Emil Dinchev
 */
public final class ContainerInfoValidator {
	
	private static final Location location = 
		Location.getLocation(ContainerInfoValidator.class);
	
	private ContainerInfoValidator() {
		// to prevent the creation.
	}

	/**
	 * Method used to validate the container info. This method always succeed.
	 * If there are some problems, a warning message will be traced.
	 * 
	 * @param container
	 *            not null.
	 */
	public static void validateAndFix(ContainerInterface container) {
		final StringBuilder warnings = new StringBuilder();

		validateServiceNameAndFix(container, warnings);
		validateParallelism(warnings, container.getContainerInfo());
		traceWarnings(warnings, container);
	}

	private static void traceWarnings(final StringBuilder warnings,
			final ContainerInterface container) {
		if (warnings.length() != 0) {
			warnings.insert(0, DSConstants.EOL + "Warnings:");
			warnings
					.append(DSConstants.EOL
							+ "Solution: Please contact the container holder to fix these warnings.");
			DSLog.traceWarningWithFaultyComponentCSN(location, container,
					"ASJ-dpl.ds-000463", "{0}", warnings.toString());
		}
	}

	private static void validateParallelism(final StringBuilder warnings,
			final ContainerInfo cInfo) {
		if (!cInfo.isSupportingParallelism()) {
			warnings
					.append(DSConstants.EOL
							+ "   The ["
							+ cInfo.getName()
							+ "] container reports that doesn't support parallel "
							+ "operations, which slows down the deploy, update and remove operations with applications.");
		}
	}

	private static void validateServiceNameAndFix(
			final ContainerInterface container, final StringBuilder warnings) {
		ContainerInfo cInfo = container.getContainerInfo();
		if (cInfo == null) {
			warnings.append(DSConstants.EOL).append("   The [").append(
					container).append("] container has  [").append(cInfo)
					.append("] for container info.");
			return;
		}
		final String realServiceName = extractServiceNameFromLoadContext(container);
		if (realServiceName != null) {
			if (cInfo.getServiceName() == null
					|| !cInfo.getServiceName().equals(realServiceName)
					&& !cInfo.isContentHandler()) {
				warnings.append(DSConstants.EOL).append("   The [").append(
						cInfo.getName()).append(
						"] container reports that is registered from [")
						.append(cInfo.getServiceName()).append(
								"] service, but really it is registered by [")
						.append(realServiceName).append("]. ");
				// we set in this case real service name too
				cInfo.setServiceName(realServiceName);
			}
		} else {
			warnings.append(DSConstants.EOL).append("   The [").append(
					cInfo.getName()).append(
					"] container reports that is registered from [").append(
					cInfo.getServiceName()).append(
					"] service, but there is not service with such name. ");
		}
	}

	/*
	 * Method used to extract the service name. We want to be sure that the
	 * container doesn't pass a false service name in its container info.
	 * 
	 * @param container the corresponding container interface.
	 * 
	 * @return the name of the service, providing the given container. Not null.
	 */
	private static String extractServiceNameFromLoadContext(
			final ContainerInterface container) {
		final ClassInfo classInfo = PropManager.getInstance().getLoadContext()
				.getLoaderComponentInfo(container.getClass().getClassLoader());
		if (classInfo != null && classInfo instanceof ClassWithLoaderInfo) {
			return ((ClassWithLoaderInfo) classInfo).getComponentName();
		}
		return null;
	}

}
