/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.command;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.container.monitor.LibraryMonitor;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.container.monitor.SystemMonitor;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.ComponentNodeHandler;
import com.sap.engine.lib.refgraph.impl.util.BooleanList;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.cache.dpl_info.CompRefGraph;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.properties.PropManager;

/**
 * Special node handler, used during traverse forward, to print the reference
 * graph. It has internal state and therefore cannot be shared between threads.
 */
public final class ConsoleGraphRenderer extends ComponentNodeHandler<Component> {
	private static final String GRAY = "[37;0m";
	private static final String YELLOW = "[33;1m";
	private static final String GREEN = "[32;1m";
	private static final String RED = "[31;1m";
	private static final String BLUE = "[34;1m";
	private static final String AQUA = "[36;1m";

	private static final SystemMonitor serviceContainerContext = PropManager
			.getInstance().getAppServiceCtx().getContainerContext()
			.getSystemMonitor();
	private final PrintWriter out;
	private final boolean noDup;
	private final int endLevel;
	private final BooleanList offsetElements;
	private final Set<String> hasPrintedReferences;
	private final boolean directed_to;

	private int level;

	/**
	 * @param aOut
	 *            text output stream, where to render a given graph.
	 * @param withoutDuplicates
	 *            whether the rendered result can contains duplicates.
	 * @param level
	 *            nesting level.
	 */
	public ConsoleGraphRenderer(PrintWriter aOut, boolean withoutDuplicates,
			int level, boolean directed) {
		out = aOut;
		noDup = withoutDuplicates;
		endLevel = level;
		offsetElements = new BooleanList();
		hasPrintedReferences = new HashSet<String>();
		directed_to = directed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.lib.refgraph.NodeHandler#startRoot()
	 */
	public boolean startRoot() {
		out.println("+Application References");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.lib.refgraph.NodeHandler#endRoot()
	 */
	public void endRoot() {//
	}

	private boolean HasMoreLevels(String aName, Component.Type type) {
		if (directed_to) {
			if (Applications.getReferenceGraph().getReferencesToOthersFrom(
					new Component(aName, type)).iterator().hasNext()) {
				return true;
			}
		} else {
			if (Applications.getReferenceGraph().getReferencesFromOthersTo(
					new Component(aName, type)).iterator().hasNext()) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#startApplication
	 * (java.lang.String, java.lang.String, boolean)
	 */
	@Override
    public boolean startApplication(String aName, String aReferenceType,
			boolean aLastSybling) {

		startLevel(aLastSybling);
		printTreePrefix();

		out.print("+");
		out.print(level > 1 ? aReferenceType + " to " : "");
		out.print("application:");

		if (level < endLevel) {
			if (!noDup || (noDup && !hasPrintedReferences.contains(aName))) {

				out.print(aName);

				if (!aName.equals(CompRefGraph.RESOURCE_NOT_PROVIDED)) {
					hasPrintedReferences.add(aName);
				}

				out.write(" (");
				Status appStatus = getApplicationStatus(aName);
				if (Status.STARTED.equals(appStatus)) {
					renderColorString(out, GREEN, appStatus.toString());
				} else if (Status.STOPPED.equals(appStatus)) {
					renderColorString(out, RED, appStatus.toString() + "!");
				} else {
					renderColorString(out, YELLOW, appStatus.toString());
				}
				out.write(") ");

				out.println();
				out.flush();
				return true;
			} else {
				renderColorString(out, BLUE, aName);
				if (HasMoreLevels(aName, Component.Type.APPLICATION)) {
					renderColorString(out, RED, "...");
				}

				out.println();
				out.flush();
				return false;
			}
		} else {

			out.print(aName);

			if (HasMoreLevels(aName, Component.Type.APPLICATION)) {
				renderColorString(out, AQUA, "...");
			}

			out.println();
			out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#endApplication(
	 * java.lang.String)
	 */
	@Override
    public void endApplication(String aName) {
		endLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#startInterface(
	 * java.lang.String, java.lang.String, boolean)
	 */
	@Override
    public boolean startInterface(String aName, String aReferenceType,
			boolean aLastSybling) {

		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to interface:");
		out.print(aName);
		if (level < endLevel) {
			out.write(" (");
			InterfaceMonitor iMonitor = serviceContainerContext
					.getInterface(aName);
			if (iMonitor == null) {
				renderColorString(out, RED, "not deployed!");
			} else if (iMonitor.getStatus() != ComponentMonitor.STATUS_LOADED) {
				renderColorString(out, RED, "not loaded!");
			} else {
				renderColorString(out, GREEN, "loaded");
			}
			out.write(") ");
			out.println();
			out.flush();
			return true;
		} else {
			if (HasMoreLevels(aName, Component.Type.INTERFACE)) {
				renderColorString(out, AQUA, "...");
			}
			out.println();
			out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#endInterface(java
	 * .lang.String)
	 */
	@Override
    public void endInterface(String aName) {
		endLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#startLibrary(java
	 * .lang.String, java.lang.String, boolean)
	 */
	@Override
    public boolean startLibrary(String aName, String aReferenceType,
			boolean aLastSybling) {

		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to library:");
		out.print(aName);
		if (level < endLevel) {
			out.write(" (");
			LibraryMonitor lMonitor = serviceContainerContext.getLibrary(aName);
			if (lMonitor == null) {
				renderColorString(out, RED, "not deployed!");
			} else if (lMonitor.getStatus() != ComponentMonitor.STATUS_LOADED) {
				renderColorString(out, RED, "not loaded!");
			} else {
				renderColorString(out, GREEN, "loaded");
			}
			out.write(") ");
			out.println();
			out.flush();

			return true;
		} else {
			if (HasMoreLevels(aName, Component.Type.LIBRARY)) {
				renderColorString(out, AQUA, "...");
			}
			out.println();
			out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#endLibrary(java
	 * .lang.String)
	 */
	@Override
    public void endLibrary(String aName) {
		endLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#startService(java
	 * .lang.String, java.lang.String, boolean)
	 */
	@Override
    public boolean startService(String aName, String aReferenceType,
			boolean aLastSybling) {

		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to service:");
		out.print(aName);
		if (level < endLevel) {
			out.write(" (");
			ServiceMonitor sMonitor = serviceContainerContext.getService(aName);
			if (sMonitor == null) {
				renderColorString(out, RED, "not deployed!");
			} else if (sMonitor.getStatus() != ComponentMonitor.STATUS_ACTIVE) {
				renderColorString(out, RED, "not started!");
			} else {
				renderColorString(out, GREEN, "started");
			}
			out.write(")");

			out.println();
			out.flush();
			return true;
		} else {
			if (HasMoreLevels(aName, Component.Type.SERVICE)) {
				renderColorString(out, AQUA, "...");
			}
			out.println();
			out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#endService(java
	 * .lang.String)
	 */
	@Override
    public void endService(String aName) {
		endLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#startResourceReference
	 * (java.lang.Object, java.lang.String, boolean)
	 */
	@Override
    public boolean startResourceReference(final Object nestedObject,
			final String aReferenceType, final boolean aLastSybling) {

		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to resource:");
		out.print(nestedObject.toString());
		if (level < endLevel) {
			out.println();
			out.flush();
			return true;
		} else {
			renderColorString(out, AQUA, "...");

			out.println();
			out.flush();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.lib.refgraph.impl.ComponentNodeHandler#endResourceReference
	 * ()
	 */
	@Override
    public void endResourceReference() {
		endLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.lib.refgraph.NodeHandler#cycle(java.lang.Object,
	 * com.sap.engine.lib.refgraph.Edge, boolean)
	 */
	public void cycle(final Component name, final Edge<Component> fromEdge,
			boolean isLastCybling) {
		startLevel(true);
		try {
			printTreePrefix();
			renderColorString(out, RED, "+ERROR--");
			renderColorString(out, RED, fromEdge.getType().toString());
			renderColorString(out, RED, " to application:");
			renderColorString(out, RED, name.toString());

			out.println();
			out.flush();
		} finally {
			endLevel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.lib.refgraph.NodeHandler#selfCycle(java.lang.Object,
	 * com.sap.engine.lib.refgraph.Edge, boolean)
	 */
	public void selfCycle(final Component node,
			final Edge<Component> aFromEdge, boolean isLastCybling) {
		startLevel(true);
		try {
			printTreePrefix();
			out.print("+");
			if (aFromEdge.getNestedObject() instanceof Resource) {
				out.print("Private Resource provided by " + node);
			} else {
				out.print("Self Cycle");
			}
			out.print("+");

			out.println();
			out.flush();
		} finally {
			endLevel();
		}
	}

	private void printTreePrefix() {
		StringBuffer tmp = new StringBuffer();
		if (level > 0) {
			for (int i = 0; i < level - 1; i++) {
				if (offsetElements.get(i)) {
					tmp.append("   ");
				} else {
					tmp.append("|  ");
				}
			}

			out.write(tmp.toString());
			out.println("|");
			out.write(tmp.toString());
			out.write("+->");
			out.flush();
		}
	}

	private void startLevel(boolean lastCybling) {
		level++;
		offsetElements.add(lastCybling);
	}

	private void endLevel() {
		level--;
		offsetElements.removeLast();
	}

	private static void renderColorString(PrintWriter out, String color,
			String string) {
		out.write(color);
		out.write(string);
		out.write(GRAY);
	}

	private Status getApplicationStatus(String applicationName) {
		DeploymentInfo di = Applications.get(applicationName);
		if (di == null) {
			return Status.UNKNOWN;
		}
		return di.getStatus();
	}
}
