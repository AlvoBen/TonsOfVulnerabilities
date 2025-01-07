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

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.NodeHandler;
import com.sap.engine.lib.refgraph.impl.util.BooleanStack;

/**
 * @author Luchesar Cekov
 */
public abstract class ComponentNodeHandler<N> implements NodeHandler<N> {
	private BooleanStack shouldEndResource = new BooleanStack();

	public final boolean startNode(N node, Edge<N> edge, boolean aLastSybling) {
		final String edgeType = edge == null ? Edge.Type.HARD.toString() : edge.getType().toString();
		if(edge != null && edge.getNestedObject() != null) {
			if (startResourceReference(edge.getNestedObject(), edgeType, aLastSybling) == false){
				shouldEndResource.push(false);
				return false;
			}			
			shouldEndResource.push(true);
			return getNodeType(node).start(this, getComponentName(node), edgeType, true);
		}
		shouldEndResource.push(false);
		return getNodeType(node).start(this, getComponentName(node), edgeType, aLastSybling);
	}

	private NodeType getNodeType(N node) {
		StringBuilder componentName = new StringBuilder("" +  node);
		int colonIndex = componentName.indexOf(":");
		if (colonIndex < 0) {
			return NodeType.APPLICATION;
		}
		String componentType = componentName.substring(0, colonIndex).toUpperCase();
		return NodeType.valueOf(componentType);
	}

	private String getComponentName(N node) {
    StringBuilder componentName = new StringBuilder("" +  node);
    int colonIndex = componentName.indexOf(":");
    if (colonIndex < 0) {
      return componentName.toString();
    }
    return componentName.substring(colonIndex + 1);
  }

  public final void endNode(N node) {
    getNodeType(node).end(this, getComponentName(node));
    if (shouldEndResource.pop()) {
      endResourceReference();
    }
  }

  public abstract boolean startApplication(String name, String referenceType, boolean lastSybling);

  public abstract void endApplication(String name);

  public abstract boolean startInterface(String name, String referenceType, boolean lastSybling);

  public abstract void endInterface(String name);

  public abstract boolean startLibrary(String name, String referenceType, boolean lastSybling);

  public abstract void endLibrary(String name);

  public abstract boolean startService(String name, String referenceType, boolean lastSybling);

  public abstract void endService(String name);

  public abstract boolean startResourceReference(Object nestedObject, String refType, boolean lastSybling);

  public abstract void endResourceReference();
  
  public enum NodeType {
    APPLICATION {
	public boolean start(ComponentNodeHandler handler, String name, String referenceType, boolean lastSybling) {
        return handler.startApplication(name, referenceType, lastSybling);
      }

      public void end(ComponentNodeHandler handler, String name) {
        handler.endApplication(name);
      }
    },
    INTERFACE {
      public boolean start(ComponentNodeHandler handler, String name, String referenceType, boolean lastSybling) {
        return handler.startInterface(name, referenceType, lastSybling);
      }

      public void end(ComponentNodeHandler handler, String name) {
        handler.endInterface(name);
      }
    },
    LIBRARY {
      public boolean start(ComponentNodeHandler handler, String name, String referenceType, boolean lastSybling) {
        return handler.startLibrary(name, referenceType, lastSybling);
      }

      public void end(ComponentNodeHandler handler, String name) {
        handler.endLibrary(name);
      }
    },
    SERVICE {
      public boolean start(ComponentNodeHandler handler, String name, String referenceType, boolean lastSybling) {
        return handler.startService(name, referenceType, lastSybling);
      }

      public void end(ComponentNodeHandler handler, String name) {
        handler.endService(name);
      }
    };

    public abstract boolean start(ComponentNodeHandler handler, String name, String referenceType, boolean lastSybling);

    public abstract void end(ComponentNodeHandler handler, String name);
    
    public String toString() {
      return name().toLowerCase();
    }
  }
  
  }
