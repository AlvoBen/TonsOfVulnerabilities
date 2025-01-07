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
package com.sap.engine.lib.refgraph.impl;

import java.io.PrintWriter;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.util.BooleanList;

/**
 * @author Luchesar Cekov
 */
public class ReferencePrinterHandler<N> extends ComponentNodeHandler<N> {
	private PrintWriter out;
	private int level;
	private BooleanList offsetElements = new BooleanList();
  
	private String refPrefix ="+-->";

	public ReferencePrinterHandler(PrintWriter aOut) {
		out = aOut;
	}
  
	public ReferencePrinterHandler(PrintWriter aOut, boolean showFromReferences) {
		out = aOut;
		if (showFromReferences) {
			refPrefix = "+<--";
		}
	}

	public boolean startRoot() {
		out.println("+Application References");
		return true;
	}

	public void endRoot() {//
	}

	public boolean startApplication(String aName, String aReferenceType, boolean aLastSybling) {
		startLevel(aLastSybling);
		printTreePrefix();

		out.print("+");
		out.print(level > 1 ? aReferenceType + " to " : "");
		out.print("application:");
		out.print(aName);
		out.println();
		out.flush();
		return true;
	}

	public void endApplication(String aName) {
		endLevel();
	}

	public boolean startInterface(String aName, String aReferenceType, boolean aLastSybling) {
		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to interface:");
		out.print(aName);
		out.println();
		out.flush();
		return true;
	}

	public void endInterface(String aName) {
		endLevel();
	}

	public boolean startLibrary(String aName, String aReferenceType, boolean aLastSybling) {
		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to library:");
		out.print(aName);
		out.println();
		out.flush();
		return true;
	}

	public void endLibrary(String aName) {
		endLevel();
	}

	public boolean startService(String aName, String aReferenceType, boolean aLastSybling) {
		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(aReferenceType);
		out.print(" to service:");
		out.print(aName);
		out.println();
		out.flush();
		return true;
	}

	public void endService(String aName) {
		endLevel();
	}

	public boolean startResourceReference(final Object nestedObject, 
		final String refType, boolean aLastSybling) {
		startLevel(aLastSybling);
		printTreePrefix();
		out.print("+");
		out.print(refType);
		out.print(" to resource:");
		out.print(nestedObject.toString());
		out.println();
		out.flush();
		return true;
	}

	public void endResourceReference() {
		endLevel();
	}

	public void cycle(N aName, Edge<N> aReferenceType,boolean aLastSybling) {
		startLevel(true);
		try {
			printTreePrefix();
			out.print("+ERROR--");
			out.print(aReferenceType == null ? Edge.Type.HARD.toString() : aReferenceType.getType().toString());
			out.print(" to application:");
			out.print(aName);
			out.println();
			out.flush();
		} finally {
			endLevel();
		}
	}

  
	public void selfCycle(N aNode, Edge<N> aFromEdge, boolean isLastCybling) {
		final Object resource = aFromEdge.getNestedObject();
		if (resource != null) {
			startResourceReference(resource, aFromEdge.getType().toString(), isLastCybling);
		}
		try {
			startLevel(true);
			try {
				printTreePrefix();
				out.print("+");
				if(resource != null) {
					out.print("Resource provided by " + aNode);
					if (aNode.equals(aFromEdge.getFirst()))  {
						out.println();
						out.print("+Self Cycle");
					}
				} else {
					out.print("Self Cycle");
				}
				out.print("+");
				out.println();
				out.flush();
			} finally {
				endLevel();
			}
		} finally {
			if (resource != null) {
				endResourceReference();
			}
		}
	}

	protected void printTreePrefix() {
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
			out.write(refPrefix);
			out.flush();
		}
	}

	public int getLevel() {
		return level;
	}

	private void startLevel(boolean lastCybling) {
		level++;
		offsetElements.add(lastCybling);
	}

	private void endLevel() {
		level--;
		offsetElements.removeLast();
	}
}