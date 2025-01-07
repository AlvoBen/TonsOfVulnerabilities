/* 
* Copyright (c) 2005 by SAP AG, Walldorf.,
* http://www.sap.com
* All rights reserved.
*
* This software is the confidential and proprietary information
* of SAP AG, Walldorf. You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms
* of the license agreement you entered into with SAP.
*/


package com.sap.engine.lib.refgraph.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeHandler;
import com.sap.engine.lib.refgraph.impl.util.IntStack;

/**
 *@author Luchesar Cekov
 */
public class ReferenceCycleCheckerHandler<N> implements NodeHandler<N> {
    public static final int MAX_FOUND_CYCLE_COUNT = 15;
    private StringWriter path = new StringWriter();
    private List<String> cycles = new LinkedList<String>();
    private ReferencePrinterHandler<N> renderer = new ReferencePrinterHandler<N>(new PrintWriter(path));
    private IntStack levels = new IntStack();

    private boolean onCycleStop = false;
    private HashSet<Object> visited = new HashSet<Object>();

    public void endRoot() {/**/
    }


    public boolean startRoot() {
      renderer.startRoot();
      return true;
    }

    public boolean startNode(N node, Edge<N> edge, boolean aLastSybling) {
        levels.push(path.getBuffer().length());
		renderer.startNode(node, edge, true);
		if (visited.contains(node)) {
			return false;
		}
		visited.add(node);
		return true;
    }

    public void endNode(N node) {
      renderer.endNode(node);
      removeFromPath();
    }

    public void cycle(N node, Edge<N> edge,boolean aLastSybling) throws CyclicReferencesException {
      levels.push(path.getBuffer().length());
      renderer.cycle(node, edge,aLastSybling);
      try {
        if (onCycleStop) {
          throw new CyclicReferencesException(new String[] { path.toString() });
        }
        if (cycles.size() >= MAX_FOUND_CYCLE_COUNT) {
          throw new CyclicReferencesException(cycles.toArray(new String[0]));
        }
        cycles.add(path.toString());
      } finally {
        removeFromPath();
      }
    }

    public void selfCycle(N aNode, Edge<N> aFormEdge,boolean isLastCybling) {//
    }

    private void removeFromPath() {
      path.getBuffer().delete(levels.pop(), path.getBuffer().length());
    }

    public List<String> getCycles() {
      return cycles;
    }

    public boolean isOnCycleStop() {
      return onCycleStop;
    }

    public void setOnCycleStop(boolean aStopOnCycle) {
      onCycleStop = aStopOnCycle;
    }
  }
