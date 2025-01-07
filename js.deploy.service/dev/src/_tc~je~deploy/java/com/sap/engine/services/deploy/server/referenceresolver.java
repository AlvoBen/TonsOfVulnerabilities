package com.sap.engine.services.deploy.server;

import static com.sap.engine.services.deploy.server.ExceptionConstants.CANNOT_RESOLVE_REF_WITH_CAUSE;
import static com.sap.engine.services.deploy.server.ExceptionConstants.CANNOT_RESOLVE_REF_2_INTERFACE_PROVIDED_BY_SERVICE;
import static com.sap.engine.services.deploy.server.ExceptionConstants.CYCLE_REF;
import static com.sap.engine.services.deploy.server.ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.container.monitor.LibraryMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.CyclicReferencesException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.Component.Type;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.application.StopTransaction;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.cache.resources.ComponentsRepository;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * Class used for resolving references.
 * 
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Rumiana Angelova
 * @version
 */
public final class ReferenceResolver {
	private static final Location location = 
		Location.getLocation(ReferenceResolver.class);

	private DeployServiceContext ctx;
	private final ComponentsRepository components;
	private final Graph<Component> refGraph;

	/**
	 * The default constructor.
	 */
	ReferenceResolver() {
		components = new ComponentsRepository();
		refGraph = Applications.getReferenceGraph();
	}

	/**
	 * @param ctx the used deploy service context.
	 */
	public void activate(DeployServiceContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * @return the component repository used to check whether a given component
	 * is online or not.
	 */
	public ComponentsRepository getComponentsRepository() {
		return components;
	}

	/**
	 * <p>Notifies that a component is already available. In this moment we have 
	 * to start all <tt>IMPLICIT_STOPPED</tt> applications, which can be started
	 * without to start their predecessors and have references to newly 
	 * available component (so called consumers). The proper status of the 
	 * consumers is already set during their last stop.
	 * 
	 * @see StopTransaction#setFinalLocalApplicationStatus
	 * 
	 * @param component the newly available component. Not null.
	 */
	public void componentIsAvailable(final Component component) {
		final Set<Edge<Component>> consumerRefs = 
			refGraph.getReferencesFromOthersTo(component);
		for(Edge<Component> ref : consumerRefs) {
			if(ctx.isMarkedForShutdown()) {
				break;
			}
			final Component consumer = ref.getFirst();
			final DeploymentInfo app = Applications.get(consumer.getName());
			assert app != null;
			if(app.getStatus().equals(Status.IMPLICIT_STOPPED) && 
				canBeStarted(consumer)) {
				if(location.bePath()) {
					SimpleLogger.trace(Severity.PATH, location, null,
						"Starting application [{0}], which is in [{1}], " +
						"because the component [{2}] is now available. ",
						app.getApplicationName(), app.getStatus(), component);
				}
				startImplicitStoppedApplication(component, app);
			}
		}
	}
	
	/**
	 * Check whether a given application is referenced hard by any
	 * <tt>IMPLICIT_STOPPED</tt> consumers. This method is called by stop 
	 * transaction in order to set the proper final application status.
	 * 
	 * @param app the application to be checked.
	 * @return <tt>true</tt> if the application is referenced hard by any
	 * <tt>IMPLICIT_STOPPED</tt> consumer.
	 * 
	 * @see StopTransaction#setFinalLocalApplicationStatus
	 */
	public boolean isReferencedHardByImplicitStoppedApps(final Component app) {
		assert app.getType() == Component.Type.APPLICATION;
		for(final Edge<Component> ref :
			refGraph.getReferencesFromOthersTo(app)) {
			if(ref.getType() == Edge.Type.WEAK) {
				continue;
			}
			final DeploymentInfo dInfo = 
				Applications.get(ref.getFirst().getName());
			if(Status.IMPLICIT_STOPPED.equals(dInfo.getStatus())) {
				return true;
			}
        }
		return false;
	}

	/**
	 * Check whether the given application can be started without to start its
	 * predecessors. This is true if it has not hard references to components 
	 * which are not available (started).
	 * @param app application which references we are checking.
	 * @return <tt>true</tt> if the application can be started.
	 */
	private boolean canBeStarted(final Component app) {
		assert app.getType() == Component.Type.APPLICATION;
		final Set<Edge<Component>> refs = 
			refGraph.getReferencesToOthersFrom(app);
		for(final Edge<Component> ref : refs) {
			if( !components.isComponentEnabled(ref.getSecond()) &&
			    ref.getType() == Edge.Type.HARD) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Start an <tt>IMPLICIT_STOPPED</tt> resolved application without to start
	 * any of its predecessors. Note that here we not throw any exceptions, but 
	 * log them in case of failed start.
	 * @param cause the referred component which causes the start of the 
	 * application, because it is already available. Not null.
	 * @param app deployment info for a resolved application which we want to
	 * start. Not null. Must be in <tt>IMPLICIT_STOPPED</tt> status.
	 */
	private void startImplicitStoppedApplication(final Component cause,
	    final DeploymentInfo app) {
		assert Status.IMPLICIT_STOPPED.equals(app.getStatus());
		try {
			ctx.getLocalDeployment().startApplicationLocalAndWait(
				app.getApplicationName(), cause);
		} catch(DeploymentException dex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
				dex.getLocalizedMessage(), dex);
		} catch(RuntimeException ex) {
			final ServerDeploymentException sdex = 
				new ServerDeploymentException(
					UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] {"starting application [" +
						app.getApplicationName() +
						"], which is caused by the activation of component [" +
						cause + "]"}, ex);
			SimpleLogger.traceThrowable(Severity.ERROR, location, null,
				sdex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Make check for cycles, starting the traversing of reference graph from
	 * the application with the given name.
	 * 
	 * @param appName the name of the application.
	 * @throws CyclicReferencesException if there are cycles.
	 */
	public void check4Cycles(String appName) throws CyclicReferencesException {
		if(!PropManager.getInstance().getCheckReferenceCycles()) {
			return;
		}

		try {
			refGraph.cycleCheck(Component.create(appName));
		} catch(com.sap.engine.lib.refgraph.CyclicReferencesException e) {
			throw new CyclicReferencesException(e);
		}
	}

	/**
	 * Make check for cycles, using the new deployment info. After the check,
	 * the old deployment info will be restored in the reference graph.
	 * 
	 * @param newDInfo new deployment info.
	 * @throws CyclicReferencesException if there are cycles.
	 */
	public void check4CyclesAndRestore(DeploymentInfo newDInfo)
	    throws CyclicReferencesException {
		final TransactionCommunicator comm = ctx.getTxCommunicator();
		final String appName = newDInfo.getApplicationName();
		final DeploymentInfo oldDInfo = ctx.getLocalDeployment()
		    .getApplicationInfo(appName);
		try {
			comm.addApplicationInfo(appName, newDInfo);
			check4Cycles(appName);
		} finally {
			ctx.getTxCommunicator().addApplicationInfo(
			    oldDInfo.getApplicationName(), oldDInfo);
		}
	}

	/**
	 * Checks for cyclic references.
	 * 
	 * @param appName application name.
	 * @param refs references.
	 * 
	 * @throws DeploymentException if cyclic references are found.
	 */
	public void checkCycleReferences(String appName, ReferenceObject[] refs)
	    throws DeploymentException {
		DeploymentInfo info = ctx.getLocalDeployment().getApplicationInfo(
		    appName);
		if(info == null || refs == null) {
			return;
		}
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < refs.length; i++) {
			if(ReferenceObjectIntf.REF_TARGET_TYPE_APPLICATION.equals(
				refs[i].getReferenceTargetType()) &&
			    !list.contains(refs[i].toString())) {
				list.add(refs[i].toString());
			}
		}
		refs = info.getReferences();
		if(refs != null) {
			for(int i = 0; i < refs.length; i++) {
				if(ReferenceObjectIntf.REF_TARGET_TYPE_APPLICATION.equals(
					refs[i].getReferenceTargetType()) &&
				    !list.contains(refs[i].toString())) {
					list.add(refs[i].toString());
				}
			}
		}
		String[] refNames = new String[list.size()];
		list.toArray(refNames);
		for(int i = 0; i < refNames.length; i++) {
			if(isSuccessor(refNames[i], appName)) {
				ServerDeploymentException sde = new ServerDeploymentException(
				    CYCLE_REF, appName, refNames[i]);
				sde.setMessageID("ASJ.dpl_ds.005088");
				throw sde;
			}
		}
	}

	private boolean isSuccessor(String parent, String succ) {
		if(parent.equals(succ)) {
			return true;
		}
		DeploymentInfo info = ctx.getLocalDeployment().getApplicationInfo(
		    parent);
		if(info == null) {
			return false;
		}
		ReferenceObject[] refs = info.getReferences();
		if(refs != null) {
			for(int i = 0; i < refs.length; i++) {
				if(ReferenceObjectIntf.REF_TARGET_TYPE_APPLICATION.equals(
					refs[i].getReferenceTargetType())) {
					if(refs[i].toString().equals(succ)) {
						return true;
					}
					if(isSuccessor(refs[i].toString(), succ)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Starts all components referenced by given application.
	 * @param app deployment info of the given application. Not null.
	 * @return string array of warnings. Not null.
	 * @throws DeploymentException if a problem occurs during the starting.
	 */
	public String[] startReferencedComponents(final DeploymentInfo app) 
		throws DeploymentException {
		assert app != null;
		final List<String> warnings = new ArrayList<String>();
		final Set<Edge<Component>> refs = 
			refGraph.getReferencesToOthersFrom(
				new Component(app.getApplicationName(),
					Component.Type.APPLICATION));
		for(Edge<Component> ref : refs) {
			final Component component = ref.getSecond();
			startReferencedComponent(component, app.getApplicationName(), 
				ref.getType() == Edge.Type.HARD, warnings);
		}
		return warnings.toArray(new String[warnings.size()]);
	}

	/**
	 * Try to start the referenced component or assure that referenced library 
	 * is resolved.
	 * @param component referenced component (application, service, or
	 * interface) which we will try to start or library which we will assure 
	 * that is resolved.
	 * @param referrer the name of the application which owns the reference.
	 * @param isHard whether the reference is hard.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException if a problem occurs during the starting.
	 */
	public void startReferencedComponent(final Component component,
	    final String referrer, final boolean isHard, 
	    final List<String> warnings) throws DeploymentException {
		if(component.getType() == Component.Type.INTERFACE) {
			startReferencedInterface(component, referrer, isHard, warnings);
		}  else if(component.getType() == Component.Type.SERVICE) {
			startReferencedService(component, referrer, isHard, warnings);
		} else if(component.getType() == Component.Type.APPLICATION) {
			startReferencedApplication(component, referrer, isHard, warnings);
		}else if(component.getType() == Component.Type.LIBRARY) {
			assureLibraryIsResolved(component, referrer, isHard, warnings);
		} else {
			throw new AssertionError("Unknown reference target type.");
		}
	}

	/**
	 * Resolve a reference to interface.
	 * @param itf referred interface.
	 * @param referrer the name of the application, which owns the reference.
	 * @param isHard whether the reference is hard.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	private void startReferencedInterface(final Component itf,
	    final String referrer, final boolean isHard,
	    final List<String> warnings) throws DeploymentException {

		final String itfName = itf.getName();
		final InterfaceMonitor iMonitor = PropManager.getInstance()
		    .getAppServiceCtx().getContainerContext().getSystemMonitor()
		    .getInterface(itfName);
		
		if (iMonitor == null) {
			// application can be deployed even if it has hard reference to a component which is not deployed
			if (isHard) {
				handleInterfaceNotAvailable(itf, referrer, " interface is not deployed.  Probably application " + referrer + 
					" declares runtime but no deploytime dependency to the interface which is wrong. Deploy the missing interface " + itfName + " as a workaround "
					+ " or contact application owners to check their references.", referrer);
			}
			warnings.add("Interface [" + itf + "] is not deployed," +
					" but resolving of application [" + referrer +
					"] continues, because it has weak reference to it.");
			return;
		}
		
		if (iMonitor.getStatus() != ComponentMonitor.STATUS_LOADED) {
			if (isHard) {
				handleInterfaceNotAvailable(itf, referrer, " interface is deployed but not loaded. Contact component owners for assistance.", itfName);
			}
			warnings.add("Interface [" + itf + "] is not loaded," +
					" but resolving of application [" + referrer +
					"] continues, because it has weak reference to it.");
			return;
		}
		
		if(components.isExplicitlyDisabled(itf)) {
			if (isHard) {
				handleInterfaceNotAvailable(itf, referrer, " interface is getting unavailable at the moment because of conflicting operation. Retry later.", null);
			}
			warnings.add("Interface [" + itf + "] is not loaded," +
					" but resolving of application [" + referrer +
					"] continues, because it has weak reference to it.");
			return;
		}

		final String providingService = iMonitor.getProvidingServiceName();
		if(providingService == null) {
			if(PropManager.getInstance().getInterfacesWithoutProvider()
			    .contains(itfName)) {
				warnings.add("Interface [" + itf + "] is not provided, " +
					"but resolving of application [" + referrer +
					"] continues, because this interface is in the list " +
					"with interfaces without providers.");
				return;
			}
			
			if (isHard) {
				handleInterfaceNotAvailable(itf, referrer, " interface is not provided by any service. Contact component owners for assistance.", itfName);
			}
			warnings.add("Interface [" + itf + "] is not provided by any service," +
					" but resolving of application [" + referrer +
					"] continues, because it has weak reference to it.");
			return;
		}
		
		try {
			
			final ServiceMonitor sMonitor = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext().getSystemMonitor()
				.getService(providingService);
			
			if (sMonitor == null) {
				if (isHard) {
					handleInterfaceServiceNotAvailable(itf, referrer, providingService, itfName, "providing service is not deployed on engine.");
				} else {
					warnings.add("Service [" + providingService +
							"] which provides referred interface " + itfName  + " is not deployed, but resolving of application [" +
							referrer + "] " +
							"continues because it has a weak reference to the interface.");
					return;
				}
			} 
			
			String startErrors = tryToStartServiceAndCollectErrors(
				new Component(providingService, Component.Type.SERVICE), sMonitor);
			
			if (startErrors.length() == 0) {
					return;
			}
			
			if(isHard) {				
				handleInterfaceServiceNotAvailable(itf, referrer,
						providingService, providingService,startErrors);
			}
			
			warnings.add("Service [" + providingService +
				"] which provides referred interface " + itfName  + " cannot be started, but resolving of application [" +
				referrer + "] " +
				"continues because it has a weak reference to the interface.");
			return;
		} catch(ServiceException ex) {
			if(isHard) {
				handleInterfaceServiceNotAvailable(itf, referrer,
							providingService, providingService, "service failed to start with errors");
			}
			warnings.add("Service " + providingService +
				" cannot be started because " + ex.getMessage() +
				", but resolving of application " + referrer +
				" continues because it has weak reference to " + itf +
				", which is provided from this service.");
		}
	}

	/**
	 * Handle a not available service which must provide an interface.
	 * @param itf interface
	 * @param referrer the name of the application which has reference to the interface
	 * @param providingService service that must provide the interface
	 * @throws ServerDeploymentException
	 */
	private void handleInterfaceServiceNotAvailable(final Component itf,
			final String referrer, final String providingService, final String faultyDcName, final String reasonForServiceFailure)
			throws ServerDeploymentException {
		ServerDeploymentException sde = new ServerDeploymentException(
		    CANNOT_RESOLVE_REF_2_INTERFACE_PROVIDED_BY_SERVICE,
		    referrer, itf.getName(), "interface", providingService, reasonForServiceFailure);
		sde.setMessageID("ASJ.dpl_ds.005113");
		DSLog.logErrorThrowableWithFaultyDcName(location, faultyDcName, sde, "ASJ.dpl_ds.005113",
				"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}]," +
				" which is not active on the server, because of issues with service [{3}] that provides it - [{4}]",
				referrer, itf.getName(), "interface", providingService, reasonForServiceFailure);
		throw sde;
	}
	
	/**
	 * Handle a not available interface.
	 * @param itf the not available interface.
	 * @param referrer the name of the application which has reference to it.
	 * @param reason for failure caused by the interface not available
	 * @param dcName DC name of faulty component on whose behalf the error will be logged and traced
	 * @param isHard whether the reference is hard or weak.
	 * @param warnings list of collected warnings.
	 * @throws ServerDeploymentException
	 */
	private void handleInterfaceNotAvailable(final Component itf, 
			final String referrer, final String reason,
			final String dcName) throws ServerDeploymentException {		

			ServerDeploymentException sde = 
				new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
					referrer, itf.getName(), itf.getType(), reason);
			sde.setMessageID("ASJ.dpl_ds.005035");
			if (dcName != null) {				
				DSLog.logErrorThrowableWithFaultyDcName(location, itf.getName(), sde, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						 referrer, itf.getName(), itf.getType(), reason);
				throw sde;
			} else {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						sde, referrer, itf.getName(), itf.getType(), reason);
				throw sde;
			}
	}
		

	/**
	 * Resolve a reference to service.
	 * @param service referred service.
	 * @param referrer the name of the application, which owns the reference.
	 * @param isHard whether the reference is hard.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	@SuppressWarnings("boxing")
    private void startReferencedService(final Component service,
		final String referrer, final boolean isHard,
		final List<String> warnings) throws DeploymentException {
		if(location.bePath()) {
			SimpleLogger.trace(Severity.PATH, location, null,
				"Application [{0}] has [{1}] reference to " +
				"service [{2}] and is going to start it.", 
				referrer, isHard, service.getName());
		}
		try {
			
			final ServiceMonitor sMonitor = PropManager.getInstance()
				.getAppServiceCtx().getContainerContext().getSystemMonitor()
				.getService(service.getName());
			
			if (sMonitor == null) {
				if (isHard) {
					// application can be deployed even if it has hard reference to a component which is not deployed
					handleServiceNotAvailable(service, referrer, referrer,
											" service is not deployed. Probably application " + referrer + 
											" declares runtime but no deploytime dependency to the service which is wrong. Deploy the missing service "
											+ service.getName() + " as a workaround "  +
											" or contact application owners to check their references.");
				}
				warnings.add("Service [" + service + "] is not deployed, " +
						"but resolving of application [" + referrer +
						"] continues because it has weak reference to this service.");
				return;
			} 
		
			String startErrors = tryToStartServiceAndCollectErrors(service, sMonitor);
			
			if(startErrors.length() == 0) {
				// successful start
				return;
			}
			if (isHard) {
				handleServiceNotAvailable(service, referrer, service.getName(), startErrors);
			}
			warnings.add("Service [" + service + "] cannot be started, " +
					"but resolving of application [" + referrer +
					"] continues because it has weak reference to this service.");
		} catch(ServiceException ex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, null,
				ex.getLocalizedMessage(), ex);
			if(isHard) {
				handleServiceNotAvailable(service, referrer, service.getName(),
						" service fails to start with errors. Contact component owners to investigate the problem with service " + service.getName());
			}
			warnings.add("Service [" + service + "] cannot be started " +
				"because " + ex.getMessage() + 
				", but resolving of application [" + referrer +
				"] continues because it has weak reference to this service.");
		}
	}

	/**
	 * Handle not available service.
	 * @param service the service which is not available.
	 * @param referrer the name of the application which has reference to it.
	 * @param isHard whether the reference is hard or weak.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException
	 */
	private void handleServiceNotAvailable(final Component service,
		final String referrer, final String faultyDcName, final String reason) throws DeploymentException {

			ServerDeploymentException sde = new ServerDeploymentException(
				CANNOT_RESOLVE_REF_WITH_CAUSE, referrer, service.getName(), service.getType(), reason);
			sde.setMessageID("ASJ.dpl_ds.005035");
			DSLog.logErrorThrowableWithFaultyDcName(location, faultyDcName, sde, "ASJ.dpl_ds.005035",
					"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
					 referrer, service.getName(), service.getType(), reason);
			throw sde;
				
	}

	/**
	 * Resolve a reference to library.
	 * @param library referred library.
	 * @param referrer the name of the application, which owns the reference.
	 * @param isHard whether the reference is hard.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	@SuppressWarnings("boxing")
	private void assureLibraryIsResolved(final Component library,
		final String referrer, final boolean isHard,
		final List<String> warnings) throws DeploymentException {

		final String libraryName = library.getName();
		final LibraryMonitor lMonitor = PropManager.getInstance()
			.getAppServiceCtx().getContainerContext().getSystemMonitor()
			.getLibrary(libraryName);
		
		final Type componentType = library.getType();

		if(lMonitor == null) {
			// application can be deployed even if it has hard reference to a component which is not deployed
			if(isHard) {				
				String reason = " library is not deployed on server. Probably application " + referrer +
				" declares runtime but no deploytime dependency to the library which is wrong. Deploy the missing library " + libraryName + " as a workaround " +
				" or contact application owners to check their references.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, libraryName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, referrer, sde, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, libraryName, componentType, reason);
				throw sde;
			}
			warnings.add("Library " + libraryName +
					" is not deployed, but resolving of application " +
					referrer + " continues because it has " +
					"weak reference to this library.");
		} else if(lMonitor.getStatus() != ComponentMonitor.STATUS_LOADED) {
			if(isHard) {
				String reason = " library is deployed but cannot be loaded. Contact component owners for assistance.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, libraryName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, libraryName, sde, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, libraryName, componentType, reason);
				throw sde;
			}
			warnings.add("Library " + libraryName +
					" is not loaded, but resolving of application " +
					referrer + " continues because it has " +
					"weak reference to this library.");
		} else if(components.isExplicitlyDisabled(library)) {
			if(isHard) {
				String reason = " library is being unloaded at the moment because of a conflicting operation. Retry later.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, libraryName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						sde, referrer, libraryName, componentType, reason);
				throw sde;
			}
			warnings.add("Library " + libraryName +
					" is not loaded, but resolving of application " +
					referrer + " continues because it has " +
					"weak reference to this library.");
		}
	}

	/**
	 * Resolve reference to application.
	 * @param app referred application.
	 * @param referrer the name of the application, which owns the reference.
	 * @param isHard whether the reference is hard.
	 * @param warnings list of collected warnings.
	 * @throws DeploymentException if a problem occurs during the process.
	 */
	@SuppressWarnings("boxing")
	private void startReferencedApplication(final Component app,
		final String referrer, final boolean isHard, 
		final List<String> warnings) throws DeploymentException {

		final String appName = app.getName();
		DeploymentInfo dInfo = ctx.getLocalDeployment()
			.getApplicationInfo(appName);
		
		final Type componentType = app.getType();
		
		if (dInfo == null) {
			// application can be deployed even if it has hard reference to a component which is not deployed
			if (isHard) {
				String reason = " application is not deployed. Probably refering application " + referrer +
						" declares runtime but no deploy-time dependency to the referred application which is wrong. Deploy the component " + appName +
						" or contact owners of application " + referrer + " to check their references.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, appName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, referrer, sde, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, appName, componentType, reason);
				throw sde;
			}
			warnings.add("Application " + app + " is not deployed, " +
					"but resolving of application " + referrer + " continues " +
					"because it has weak reference to this application.");
			return;
		}
		// the check for explicitly disabled components is not applicable for applications
		/*else if(components.isExplicitlyDisabled(app)) {
			.....
		}*/
		
		if (StartUp.DISABLED == dInfo.getStartUpO()) {
			if(isHard) {
				String reason = " application start is disabled from application settings.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, appName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, appName, sde,
						"ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, appName, componentType, reason);
				throw sde;
			}
			warnings.add("Start of application " + app + " is disabled, " +
				"but resolving of application " + referrer + " continues " +
				"because it has weak reference to this application.");
			return;
		}
		
		if (ApplicationStatusResolver.isApplicationDisabled(appName)) {
			if(isHard) {
				String reason = " application start is disabled through filters. Contact the template owners for assistance.";
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, appName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, appName, sde,
						"ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, appName, componentType, reason);
				throw sde;
			}
			warnings.add("Start of application " + app + " is disabled through filters in the configuration tool, " +
				"but resolving of application " + referrer + " continues " +
				"because it has weak reference to this application.");
			return;
		}
		
		if(Status.STARTED.equals(dInfo.getStatus())) {
			return;
		}
		
		try {
			if(location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"Application [{0}] has [{1}] reference to " +
					"application [{2}] and is starting it.", 
					referrer, isHard, dInfo.getApplicationName());
			}
			final LocalDeployment deploy = ctx.getLocalDeployment();
			// Add (append) reference info for the status description here.
			dInfo.setStatusDescription(
			    StatusDescriptionsEnum.STARTING_AS_REFERRED, new Object[] {
			        referrer, isHard});
			deploy.startApplicationLocalAndWait(
				dInfo.getApplicationName(), null);
		} catch(DeploymentException ex) {
			if(isHard) {
				String reason = " application fails to start with errors. Contact component owners to investigate the problem with application " + appName;
				ServerDeploymentException sde = 
					new ServerDeploymentException(CANNOT_RESOLVE_REF_WITH_CAUSE, 
						referrer, appName, componentType, reason);
				sde.setMessageID("ASJ.dpl_ds.005035");
				DSLog.logErrorThrowableWithFaultyDcName(location, appName, sde, "ASJ.dpl_ds.005035",
						"Application [{0}] cannot be started. Reason: it has hard reference to resource [{1}] with type [{2}], which is not active on the server because {3}",
						referrer, appName, componentType, reason);
				throw sde;
			}
			warnings.add("Application " + dInfo.getApplicationName() +
				" cannot be started, but resolving of application " + referrer +
				" continues because it has weak reference to this application.");
		}
	}

	/**
	 * Method invoked before given component to become unavailable. First we are
	 * stopping all consumers and after that we are restarting these, which can
	 * be started without their predecessors. We need to do that in this way in 
	 * order to avoid repeatedly restarting.
	 * @param component the component which will be unavailable.
	 */
	public void componentIsGettingUnavailable(final Component component) {
		// Stop all consumers. 
		final List<Component> restartList =	stopAllConsumersOf(component);
		
		// Restart resolved from these with weak reference.
		for(final Component consumer : restartList) {
			if(ctx.isMarkedForShutdown()) {
				// Don't restart the application if the server is shutting down.
				break;
			}
			if(canBeStarted(consumer)) {
				startConsumer(consumer, component);
			}
		}
	}

	/**
	 * Stop all consumers of stopping component and return these of them which 
	 * are potentially resolved after the operation. Consumers which are reached
	 * via <tt>HARD</tt> reference are guaranteed unresolved. Only these reached
	 * via <tt>WEAK</tt> reference can be resolved (but this is not enough and 
	 * we have to check whether they are resolved or not).
	 * @param component the stopping component.
	 * @return list of potentially resolved consumers.  
	 */
	private List<Component> stopAllConsumersOf(final Component component) {
		final List<Component> restartList = new ArrayList<Component>();
		// Stop all consumers
		for(Edge<Component> ref : 
			refGraph.getReferencesFromOthersTo(component)) {
			final DeploymentInfo consumer = 
				Applications.get(ref.getFirst().getName());
			assert consumer != null;
			if(consumer.getStatus().equals(Status.STARTED)) {
				stopConsumer(consumer, component);
				if(ref.getType() == Edge.Type.WEAK) {
					// This application will be potentially restarted.
					restartList.add(ref.getFirst());
				}
			}
		}
		return restartList;
	}

	/**
	 * Adds alone resource to the set of provided resources. Alone resources are
	 * those which are provided by services and have not class loaders.
	 * @param resource the provided resource.
	 * @param provider the provider of the resource.
	 */
	public void aloneResourceIsAvailable(final Resource resource, 
		final Component provider) {
		Applications.registerAloneResource(resource, provider);
		componentIsAvailable(provider);
	}

	/**
	 * Returns set of referenced application names which are deployed on the
	 * server.
	 * @param appName the name of the given application.
	 * @return set of referenced application names which are deployed on the
	 * server.
	 */
	public Set<String> getRelatedApplications(String appName) {
		final Component app = Component.create(appName);
		final Set<String> related = new HashSet<String>();
		final Set<Edge<Component>> refs = 
			refGraph.getReferencesToOthersFrom(app);
		for(final Edge<Component> ref : refs) {
			final Component reffered = ref.getSecond();
			if(reffered.getType() != Component.Type.APPLICATION) {
				continue;
			}
			final String referencedApp = reffered.getName();
			if(Applications.isDeployedApplication(referencedApp)) {
				related.add(referencedApp);
			}
		}
		return related;
	}

	/**
	 * Start a consumer of a stopping component, because it is resolved. The 
	 * consumer will be started only if it does not support lazy start or is 
	 * referenced by such applications.
	 * @param consumer a resolved referrer to the stopping component. Not null.
	 * @param component the stopping component. Not null.
	 */
	private void startConsumer(final Component consumer,
		final Component component) {
		final DeploymentInfo app = Applications.get(consumer.getName());
		assert app != null;
		if(Status.STOPPED.equals(app.getStatus())) {
			// This means that the application is lazy and not referred
			// by not lazy applications.
			// See StopTransaction#setFinalLocalApplicationStatus()
			if(location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"The application [{0}] won't be restarted, " +
					"because it supports lazy startup mode and " +
					"is not referenced by always starting applications.",
					app.getApplicationName());
			}
		} else if(Status.IMPLICIT_STOPPED.equals(app.getStatus())) {
			// Not lazy or referenced hard by not lazy.
			// See StopTransaction#setFinalLocalApplicationStatus()
			if(location.bePath()) {
				SimpleLogger.trace(Severity.PATH, location, null,
					"The component [{0}] to which application [{2}] " +
					"has a weak reference, is stopping now " +
					"and will restart the application.", 
					component, app.getApplicationName());
			}
			startImplicitStoppedApplication(component, app);
		}
	}

	/**
	 * Stop a referrer to a stopping component.
	 * @param referrer the deployment info of the referrer. Not null.
	 * @param component the stopping component.
	 */
	private void stopConsumer(final DeploymentInfo referrer, 
		final Component component) {
		SimpleLogger.trace(Severity.PATH, location, null,
			"Application [{0}] will be stopped " +
			"because it has reference to [{1}]" +
			"which is going to be stopped.",
			referrer.getApplicationName(), component);
		try {
			ctx.getLocalDeployment().stopApplicationLocalAndWait(
				referrer.getApplicationName(), component);
		} catch(DeploymentException ex) {
			SimpleLogger.traceThrowable(Severity.ERROR, location, null, 
				ex.getLocalizedMessage(), ex);
		} 
	}

	/**
	 * Start a referred service and return the error descriptions if the start is not possible.
	 * @param service the referred service
	 * @return reason for failure if the service cannot be started or empty String if service is started; cannot return <tt>null</tt> 
	 * @throws ServiceException if the start fails
	 */
	private String tryToStartServiceAndCollectErrors(final Component service, final ServiceMonitor sMonitor)
		throws ServiceException {

		assert service != null;
		final String serviceName = service.getName();
		
		// when the service is stopped manually, it is marked as explicitly disabled
		// by the Deploy Service so only the manual start can fix it 
		if(components.isExplicitlyDisabled(service)) {
			return "service is stopped manually and you have to start it manually before you can proceed.";
		}
		
		if (sMonitor.getStartupMode() ==
			ServiceMonitor.DISABLED) {
			return "service start is disabled in template startup filters.";
		}
		
		switch(sMonitor.getStatus()) {
		case ComponentMonitor.STATUS_LOADED:
			if(sMonitor.getInternalStatus() == 
				ServiceMonitor.INTERNAL_STATUS_START_FAIL) {
				return "service has failed to start. Contact component owners to investigate the problem with service "  + serviceName;
			}
			sMonitor.start();
			return "";
		case ComponentMonitor.STATUS_ACTIVE:
			return "";
		default:
			return "service cannot be started at the moment. Retry the operation later.";
		}
	}
}