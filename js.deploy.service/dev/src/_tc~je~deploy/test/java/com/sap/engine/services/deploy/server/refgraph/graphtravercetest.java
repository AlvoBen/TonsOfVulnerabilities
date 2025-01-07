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
package com.sap.engine.services.deploy.server.refgraph;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.lib.refgraph.CyclicReferencesException;
import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;

/**
 * @author Luchesar Cekov
 */
public class GraphTraverceTest extends TestCase {
	private static final Component application0 = 
		Component.create("sap.com/APPLICATION0");
	private static final Component application01 = 
		Component.create("sap.com/APPLICATION01");
	private static final Component application011 = 
		Component.create("sap.com/APPLICATION011");
	private static final Component application012 = 
		Component.create("sap.com/APPLICATION012");
	private static final Component application02 = 
		Component.create("sap.com/APPLICATION02");
	private static final Component application021 = 
		Component.create("sap.com/APPLICATION021");
	private static final Component application022 = 
		Component.create("sap.com/APPLICATION022");
	private static final Component application1 = 
		Component.create("sap.com/APPLICATION1");
	private static final Component application2 = 
		Component.create("sap.com/APPLICATION2");
	private final Component[] applications = { application0, application01,
		application011, application012, application02, application021,
		application022, application1, application2 };

	@Override
	public void setUp() throws Exception {
		PropManagerFactory.initInstance("appWorkDir", 0, "ClElementBlaBla");
	}

	private static StringWriter strr = new StringWriter();
	final static PrintWriter out = new PrintWriter(strr);

	public void testBuildTreeCycles() throws Exception {
		Applications.clear();
		Util.createDI(application0.toString(), new ReferenceObject[] {
			Util.makeHardReftoApplicaiton(application011),
			Util.makeHardReftoApplicaiton(application1),
			Util.makeHardReftoApplicaiton(application2) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application01.toString(), new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application0) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application011.toString(), new ReferenceObject[] {
			Util.makeHardReftoApplicaiton(application01),
			Util.makeHardReftoApplicaiton(application1),
			Util.makeHardReftoApplicaiton(application2) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application012, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application01) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application02, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application0) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application021, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application02) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application022, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application02) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application1, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application011) },
			new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application2, new ReferenceObject[] { 
			Util.makeHardReftoApplicaiton(application01) },
			new ResourceReference[] {}, new Resource[] {});

		teztToRefs(new Component[] { application011 });
		teztFromRefs(new Component[] { application011 });

		int size = Applications.getReferenceGraph().size();
		System.out.println("Nodes in graph = " + size);// $JL-SYS_OUT_ERR$

		for (int i = 0; i < applications.length; i++) {
			Set<Edge<Component>> referencesTo = Applications
				.getReferenceGraph().getReferencesToOthersFrom(
						applications[i]);
			for (Edge<Component> edge : referencesTo) {
				Component element = edge.getSecond();
				switch (i) {
				case 0:
					Assert.assertTrue(element.equals(application011)
						|| element.equals(application1)
						|| element.equals(application2));
					break;
				case 1:
					Assert.assertTrue(element.equals(application0));
					break;
				case 2:
					Assert.assertTrue(element.equals(application01)
						|| element.equals(application1)
						|| element.equals(application2));
					break;
				case 3:
					Assert.assertTrue(element.equals(application01));
					break;
				case 4:
					Assert.assertTrue(element.equals(application0));
					break;
				case 5:
					Assert.assertTrue(element.equals(application02));
					break;
				case 6:
					Assert.assertTrue(element.equals(application02));
					break;
				case 7:
					Assert.assertTrue(element.equals(application011));
					break;
				case 8:
					Assert.assertTrue(element.equals(application01));
					break;
				}
			}
		}

		Set<Edge<Component>> referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application0);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application01, application0, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application02, application0, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application01);
		Assert.assertEquals(3, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application01, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application012, application01, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application2, application01, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application011);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application0, application011, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application1, application011, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application012);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application02);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application021, application02, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application022, application02, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application021);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application022);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application1);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application1, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application0, application1, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application2);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application0, application2, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application2, Edge.Type.HARD, null)));

	}

	public void testSelfCycleReference() throws Exception {
		// No self cycles are allowed.
		Applications.clear();
		Util.createDI(application0, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});
		teztToRefs(new Component[] { application0 });
		Set<Edge<Component>> referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application0);
		Assert.assertEquals(0, referencesTo.size());
	}

	public void testBuildTreeRef() throws Exception {
		Applications.clear();
		Util.createDI(application0, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});
		Util.createDI(application01, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});
		Util.createDI(application011, new ReferenceObject[] {},
				new ResourceReference[] {
						Util.makeResourceApplicationRef(application01),
						Util.makeResourceApplicationRef(application1),
						Util.makeResourceApplicationRef(application2),
						Util.makeResourceApplicationRef(application02) },
				new Resource[] {});
		Util.createDI(application012, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application01) },
				new Resource[] {});
		Util.createDI(application02, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});
		Util.createDI(application021, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application02) },
				new Resource[] {});
		Util.createDI(application022, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application02) },
				new Resource[] {});
		Util.createDI(application1, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});
		Util.createDI(application2, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application0) },
				new Resource[] {});

		teztToRefs(new Component[] { application022 });
		teztFromRefs(new Component[] { application0 });

		for (int i = 0; i < applications.length; i++) {
			Set<Edge<Component>> referencesTo = Applications
					.getReferenceGraph().getReferencesToOthersFrom(
							applications[i]);
			for (Edge<Component> edge : referencesTo) {
				Component element = edge.getSecond();
				// System.out.println("Edge from " + applications[i] + " to " +
				// element);//$JL-SYS_OUT_ERR$
				switch (i) {
				case 0:
					Assert.assertTrue(element.equals(application0));
					break;
				case 1:
					Assert.assertTrue(element.equals(application0));
					break;
				case 2:
					Assert.assertTrue(element.equals(application01)
							|| element.equals(application1)
							|| element.equals(application2)
							|| element.equals(application02));
					break;
				case 3:
					Assert.assertTrue(element.equals(application01));
					break;
				case 4:
					Assert.assertTrue(element.equals(application0));
					break;
				case 5:
					Assert.assertTrue(element.equals(application02));
					break;
				case 6:
					Assert.assertTrue(element.equals(application02));
					break;
				case 7:
					Assert.assertTrue(element.equals(application0));
					break;
				case 8:
					Assert.assertTrue(element.equals(application0));
					break;
				}
			}
		}

		Set<Edge<Component>> referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application0);
		Assert.assertEquals(4, referencesFrom.size());
		// no self cycles
		Assert.assertFalse(referencesFrom.contains(new Edge<Component>(
				application0, application0, Edge.Type.HARD, new Resource(
						application0.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application01, application0, Edge.Type.HARD, new Resource(
						application0.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application02, application0, Edge.Type.HARD, new Resource(
						application0.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application1, application0, Edge.Type.HARD, new Resource(
						application0.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application2, application0, Edge.Type.HARD, new Resource(
						application0.toString(), Util.APPLICATION))));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application01);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application01, Edge.Type.HARD, new Resource(
						application01.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application012, application01, Edge.Type.HARD, new Resource(
						application01.toString(), Util.APPLICATION))));

		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application011);
		Assert.assertEquals(0, referencesFrom.size());

		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application012);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application02);
		Assert.assertEquals(3, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application02, Edge.Type.HARD, new Resource(
						application02.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application021, application02, Edge.Type.HARD, new Resource(
						application02.toString(), Util.APPLICATION))));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application022, application02, Edge.Type.HARD, new Resource(
						application02.toString(), Util.APPLICATION))));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application021);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application022);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application1);
		Assert.assertEquals(1, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application1, Edge.Type.HARD, new Resource(
						application1.toString(), Util.APPLICATION))));

		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application2);
		Assert.assertEquals(1, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application011, application2, Edge.Type.HARD, new Resource(
						application2.toString(), Util.APPLICATION))));

	}

	public void testBuildMultyResRefTree_providedByAppRef() throws Exception {
		Applications.clear();
		Resource resSelfCycleTest = Util.makeProvidedResource(
				"resSelfCycleTest", "javax.sql.DataSourse");
		ResourceReference refSelfCycleTest = makeResourceRef(resSelfCycleTest);
		Resource resRefProvidedByApplicationRef = Util.makeProvidedResource(
				"res2", "javax.sql.DataSourse");
		ResourceReference refRefProvidedByApplicationRef = makeResourceRef(resRefProvidedByApplicationRef);
		Util.createDI(application0, new ReferenceObject[] {
				Util.makeHardReftoApplicaiton(application01),
				Util.makeHardReftoApplicaiton(application02), },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application01, new ReferenceObject[] {
				Util.makeHardReftoApplicaiton(application011),
				Util.makeHardReftoApplicaiton(application012) },
				new ResourceReference[] { refSelfCycleTest,
						refRefProvidedByApplicationRef },
				new Resource[] { resSelfCycleTest });
		Util.createDI(application011, new ReferenceObject[] {},
				new ResourceReference[] {},
				new Resource[] { resRefProvidedByApplicationRef });
		Util.createDI(application012, new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application02, new ReferenceObject[] {
				Util.makeHardReftoApplicaiton(application021),
				Util.makeHardReftoApplicaiton(application022), },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application021, new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application022, new ReferenceObject[] {},
				new ResourceReference[] { Util
						.makeResourceApplicationRef(application02) },
				new Resource[] {});
		Util.createDI(application1, new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application2, new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});

		teztToRefs(new Component[] { application0 });
		teztFromRefs(new Component[] { application011 });

		Set<Edge<Component>> referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application0);
		Assert.assertEquals(2, referencesTo.size());
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application0, application01, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application0, application02, Edge.Type.HARD, null)));
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application01);
		Assert.assertEquals(3, referencesTo.size());
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application01, application011, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application01, application012, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application01, application011, Edge.Type.HARD,
				resRefProvidedByApplicationRef)));
		// No self cycles
		Assert.assertFalse(referencesTo
				.contains(new Edge<Component>(application01, application01,
						Edge.Type.HARD, resSelfCycleTest)));
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application011);
		Assert.assertEquals(0, referencesTo.size());
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application012);
		Assert.assertEquals(0, referencesTo.size());
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application02);
		Assert.assertEquals(2, referencesTo.size());
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application02, application021, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application02, application022, Edge.Type.HARD, null)));
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application021);
		Assert.assertEquals(0, referencesTo.size());
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application022);
		Assert.assertEquals(1, referencesTo.size());
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
				application022, application02, Edge.Type.HARD, new Resource(
						application02.toString(), Util.APPLICATION))));
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application1);
		Assert.assertEquals(0, referencesTo.size());
		referencesTo = Applications.getReferenceGraph()
				.getReferencesToOthersFrom(application2);
		Assert.assertEquals(0, referencesTo.size());

		Set<Edge<Component>> referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application0);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application01);
		Assert.assertEquals(1, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application0, application01, Edge.Type.HARD, null)));
		// no self cycles
		Assert.assertFalse(referencesFrom
				.contains(new Edge<Component>(application01, application01,
						Edge.Type.HARD, resSelfCycleTest)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application011);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application01, application011, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application01, application011, Edge.Type.HARD,
				resRefProvidedByApplicationRef)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application012);
		Assert.assertEquals(1, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application01, application012, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application02);
		Assert.assertEquals(2, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application0, application02, Edge.Type.HARD, null)));
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application022, application02, Edge.Type.HARD, new Resource(
						application02.toString(), Util.APPLICATION))));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application021);
		Assert.assertEquals(1, referencesFrom.size());
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application02, application021, Edge.Type.HARD, null)));
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application022);
		Assert.assertTrue(referencesFrom.contains(new Edge<Component>(
				application02, application022, Edge.Type.HARD, null)));
		Assert.assertEquals(1, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application1);
		Assert.assertEquals(0, referencesFrom.size());
		referencesFrom = Applications.getReferenceGraph()
				.getReferencesFromOthersTo(application2);
		Assert.assertEquals(0, referencesFrom.size());

	}

	/**
	 * @param resSelfCycleTest
	 * @return
	 */
	private ResourceReference makeResourceRef(Resource resSelfCycleTest) {
		return Util.makeResourceRef(resSelfCycleTest.getName(),
				resSelfCycleTest.getType());
	}

	/**
	 * @param applicationDeployInfoes
	 * @throws CyclicReferencesException
	 * @throws CyclicReferenceException
	 */
	public static void teztToRefs(Component[] comps)
			throws com.sap.engine.lib.refgraph.CyclicReferencesException {
		Applications.getReferenceGraph().print(comps,
				new PrintWriter(System.out));// $JL-SYS_OUT_ERR$
	}

	public static void teztFromRefs(Component[] comps)
			throws com.sap.engine.lib.refgraph.CyclicReferencesException {
		Applications.getReferenceGraph().printBackward(comps,
				new PrintWriter(System.out));// $JL-SYS_OUT_ERR$
	}

	/**
	 * @return
	 */

	public void testTraversor() throws Exception {
		Applications.clear();
		Util.createDI(application0, new ReferenceObject[] {
				Util.makeHardReftoApplicaiton(application011),
				Util.makeHardReftoApplicaiton(application1),
				Util.makeHardReftoApplicaiton(application2) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application01, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application0) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application011, new ReferenceObject[] {
				Util.makeHardReftoApplicaiton(application01),
				Util.makeHardReftoApplicaiton(application1),
				Util.makeHardReftoApplicaiton(application2) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application012, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application01) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application02, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application0) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application021, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application02) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application022, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application02) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application1, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application011) },
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI(application2, new ReferenceObject[] { Util
				.makeHardReftoApplicaiton(application01) },
				new ResourceReference[] {}, new Resource[] {});

		try {
			Applications.getReferenceGraph().cycleCheck(application011);
			fail();
		} catch (CyclicReferencesException e) {
			e.printStackTrace();
		}
	}
}
