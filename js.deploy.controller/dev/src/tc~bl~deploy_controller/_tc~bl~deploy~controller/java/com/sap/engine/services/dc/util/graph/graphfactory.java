package com.sap.engine.services.dc.util.graph;

import com.sap.engine.services.dc.util.Constants;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public abstract class GraphFactory {

	private static GraphFactory INSTANCE;
	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.util.graph.impl.GraphFactoryImpl";

	public static synchronized GraphFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static GraphFactory createFactory() {

		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (GraphFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003372 An error occurred while creating an instance of "
					+ "class GraphFactory! " + Constants.EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	protected GraphFactory() {
	}

	public abstract Node createNode(String name, Object userObject);

	public abstract DiEdge createEdge(Node startNode, Node endNode);

	public abstract DiGraph createGraph(Node[] nodes, DiEdge[] edges);

}
