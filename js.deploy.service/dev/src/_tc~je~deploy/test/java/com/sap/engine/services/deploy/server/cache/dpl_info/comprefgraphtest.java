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
package com.sap.engine.services.deploy.server.cache.dpl_info;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.refgraph.GraphTest;
import com.sap.engine.services.deploy.server.refgraph.Util;

/**
 * @author Luchesar Cekov
 */
public class CompRefGraphTest extends TestCase {  
	private static final String SAP_COM_APPLICATION_NAME1 = "sap.com/applicationName1";
	private static final String SAP_COM_APPLICATION6NAME = "sap.com/application6Name";
	private static final String SAP_COM_APPLICATION5NAME = "sap.com/application5Name";
	private static final String SAP_COM_APPLICATION3NAME = "sap.com/application3Name";
	private static final String SAP_COM_APPLICATION2NAME = "sap.com/application2Name";
	private static final String SAP_COM_APPLICATION1NAME = "sap.com/application1Name";
	private static final String SAP_COM_APPLICATION_NAME = "sap.com/applicationName";
	private static final String SAP_COM_APPLICATION4NAME = "sap.com/application4Name";
	private static final String TEST_REF_APPLICATION_NAME_PREFIX = "test.ref/applicationName_";
	private static final int REFERENCES_COUNT = 50;
	private static final int APPLICATIONS_COUNT = 2000;

	public void setUp() {
		PropManagerFactory.initInstance("appWorkDir", 0, "ClElementBlaBla");
	}

	public static void constructReferences() {
		Applications.clear();

		DeploymentInfo[] previousRefs = new DeploymentInfo[REFERENCES_COUNT];
		for (int i = 0; i < APPLICATIONS_COUNT; i++) {
			String name = TEST_REF_APPLICATION_NAME_PREFIX + i;

			List<ReferenceObject> applicationRefs = new LinkedList<ReferenceObject>();
			List<ResourceReference> resourceRefs = new LinkedList<ResourceReference>();
			for (int j = 0; j < previousRefs.length; j++) {
				if (previousRefs[j] != null) {
					if (j % 2 == 0) {
						applicationRefs.add(Util.makeHardReftoApplicaiton(
								Component.create(previousRefs[j].getApplicationName())));
					} else {
						resourceRefs.add(Util.makeResourceApplicationRef(previousRefs[j].getApplicationName()));
					}
				}
			}

			DeploymentInfo di = Util.createDI(name, (ReferenceObject[]) 
				applicationRefs.toArray(new ReferenceObject[0]),
				(ResourceReference[]) resourceRefs.toArray(new ResourceReference[0]),
				new Resource[] {});
			previousRefs[i % previousRefs.length] = di;
		}
	}

	public void testTopoSort() throws Exception {
		constructReferences();
		String lastAppName = TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1);

		long start = System.currentTimeMillis();
		List<Component> result = Applications.getReferenceGraph()
			.sort(Component.create(lastAppName));
		long end = System.currentTimeMillis();
		System.out.println("TopSort took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$

		int previous = -1;
		int current = 0;
		for(Component comp : result) {
			current = getApplicationIndex(comp.getName());
			Assert.assertEquals(current, previous + 1);
			previous = current;
		}
	}

	public void testCycleCheckWithNoCycles() throws Exception {
		constructReferences();
		String lastAppName = TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1);
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));

		long start = System.currentTimeMillis();
		Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
		long end = System.currentTimeMillis();
		System.out.println("CycleCheck took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
	}

	public void testCycleCheckWithCycles() throws Exception {
		constructReferences();
		DeploymentInfo di = Applications.get(TEST_REF_APPLICATION_NAME_PREFIX + 0);
		Applications.getReferenceGraph().add(
			new Edge<Component>(Component.create(di.getApplicationName()), 
				Component.create(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1)),
				Edge.Type.HARD, null));
		di = Applications.get(TEST_REF_APPLICATION_NAME_PREFIX + 1);
		Applications.getReferenceGraph().add(new Edge<Component>(
			Component.create(di.getApplicationName()), 
			Component.create(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 2)),
			Edge.Type.HARD, null));

		di = Applications.get(TEST_REF_APPLICATION_NAME_PREFIX + 2);
		Applications.getReferenceGraph().add(new Edge<Component>(
			Component.create(di.getApplicationName()),
			Component.create(TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 3)),
			Edge.Type.HARD, null));

		String lastAppName = TEST_REF_APPLICATION_NAME_PREFIX + (APPLICATIONS_COUNT - 1);

		try {

			long start = System.currentTimeMillis();
			try {
				Applications.getReferenceGraph().cycleCheck(Component.create(lastAppName));
			} finally {
				long end = System.currentTimeMillis();
				System.out.println("CycleCheck took " + (end - start) + "ms");//$JL-SYS_OUT_ERR$
			}
			Assert.fail("There should have bean cycles");
		} catch (com.sap.engine.lib.refgraph.CyclicReferencesException e) {
			// $JL-EXC$
			e.printStackTrace();
		}
	}

	private int getApplicationIndex(String applicationName) {
		return Integer.parseInt(applicationName.substring(TEST_REF_APPLICATION_NAME_PREFIX.length()));
	}

	public static void main(String args[]) {
		TestRunner.run(GraphTest.class);
	}

	private static final Resource RESOURCE1 = Util.makeProvidedResource("test1", "javax.sql.DataSourse");
	private static final Resource RESOURCE2 = Util.makeProvidedResource("test2", "javax.sql.DataSourse2");
	private static final Resource RESOURCE3 = Util.makeProvidedResource("test3", "javax.sql.DataSourse3");
  
	private static final Resource RESOURCE4 = Util.makeProvidedResource("test4", "javax.sql.DataSourse4");
	private static final Resource RESOURCE5 = Util.makeProvidedResource("test5", "javax.sql.DataSourse5");
  
	public void testAddAgainWithDifferentReferences() throws Exception {
		Applications.clear();
		Util.createDI(SAP_COM_APPLICATION4NAME, new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});
		Util.createDI(SAP_COM_APPLICATION5NAME, new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});
		Util.createDI(SAP_COM_APPLICATION6NAME, new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});

		Util.createDI(SAP_COM_APPLICATION_NAME,
			new ReferenceObject[] {
				new ReferenceObject(SAP_COM_APPLICATION1NAME,
					ReferenceObjectIntf.REF_TYPE_HARD),
				new ReferenceObject(SAP_COM_APPLICATION2NAME,
					ReferenceObjectIntf.REF_TYPE_HARD),
				new ReferenceObject(SAP_COM_APPLICATION3NAME,
					ReferenceObjectIntf.REF_TYPE_HARD), },

			new ResourceReference[] { 
				Util.makeResourceApplicationRef(SAP_COM_APPLICATION4NAME),
				Util.makeResourceApplicationRef(SAP_COM_APPLICATION5NAME), },

			new Resource[] { RESOURCE1, RESOURCE2, RESOURCE3});

		Util.createDI(SAP_COM_APPLICATION_NAME1,
			new ReferenceObject[] {
				new ReferenceObject(SAP_COM_APPLICATION1NAME,
					ReferenceObjectIntf.REF_TYPE_HARD),
				new ReferenceObject(SAP_COM_APPLICATION2NAME,
					ReferenceObjectIntf.REF_TYPE_HARD), },

			new ResourceReference[] { 
				Util.makeResourceApplicationRef(SAP_COM_APPLICATION6NAME), },

			new Resource[] { RESOURCE4, RESOURCE5 });

		Assert.assertNotNull(Applications.get(SAP_COM_APPLICATION_NAME));
		Assert.assertNotNull(Applications.get(SAP_COM_APPLICATION_NAME1));

		String applicationProvidingResource1 = Applications.getResourceProvider(RESOURCE1).toString();
		Assert.assertEquals(applicationProvidingResource1, SAP_COM_APPLICATION_NAME);
    
		Set<Resource> providedResources = Applications.get(SAP_COM_APPLICATION_NAME).getAllProvidedResources();
		Assert.assertEquals(4, providedResources.size());
		Assert.assertTrue(providedResources.contains(new Resource(SAP_COM_APPLICATION_NAME, Util.APPLICATION)));
		Assert.assertTrue(providedResources.contains(RESOURCE1));
		Assert.assertTrue(providedResources.contains(RESOURCE2));
		Assert.assertTrue(providedResources.contains(RESOURCE3));

		Set<Edge<Component>> referencesTo = Applications.getReferenceGraph()
			.getReferencesToOthersFrom(Component.create(SAP_COM_APPLICATION_NAME));
		Applications.getReferenceGraph().print(new PrintWriter(System.out));//$JL-SYS_OUT_ERR$
		Assert.assertEquals(5, referencesTo.size());

		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME), 
			Component.create(SAP_COM_APPLICATION1NAME),
			Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME), 
			Component.create(SAP_COM_APPLICATION2NAME),
			Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME), 
			Component.create(SAP_COM_APPLICATION3NAME),
			Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME), 
			Component.create(SAP_COM_APPLICATION4NAME),
			Edge.Type.HARD, new Resource(SAP_COM_APPLICATION4NAME, Util.APPLICATION))));
		Assert.assertTrue(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME),
			Component.create(SAP_COM_APPLICATION5NAME),
			Edge.Type.HARD, new Resource(SAP_COM_APPLICATION5NAME, Util.APPLICATION))));

		// Update sap.com/applicationName with new references and resources and 
		// check whether they are properly updated.
		Util.createDI("sap.com/application234", new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});
		Util.createDI(SAP_COM_APPLICATION_NAME, 
			new ReferenceObject[] { 
				new ReferenceObject("sap.com/app", ReferenceObjectIntf.REF_TYPE_HARD), },

			new ResourceReference[] { 
				Util.makeResourceApplicationRef("sap.com/application234") },

			new Resource[] { Util.makeProvidedResource("test12", "javax.sql.DataSourse112") });
    
		Util.createDI(SAP_COM_APPLICATION_NAME1,
			new ReferenceObject[] {
				new ReferenceObject(SAP_COM_APPLICATION1NAME, ReferenceObjectIntf.REF_TYPE_HARD),
				new ReferenceObject(SAP_COM_APPLICATION2NAME, ReferenceObjectIntf.REF_TYPE_HARD), },

			new ResourceReference[] { Util.makeResourceApplicationRef(SAP_COM_APPLICATION6NAME), },

			new Resource[] { RESOURCE1, RESOURCE2});

		Assert.assertEquals(Component.create(SAP_COM_APPLICATION_NAME1), Applications.getResourceProvider(RESOURCE1));
		Assert.assertEquals(Component.create(SAP_COM_APPLICATION_NAME1), Applications.getResourceProvider(RESOURCE2));

		providedResources = Applications.get(SAP_COM_APPLICATION_NAME).getAllProvidedResources();
		Assert.assertEquals(2, providedResources.size());
		Assert.assertTrue(providedResources.contains(new Resource(SAP_COM_APPLICATION_NAME, Util.APPLICATION)));
		Assert.assertTrue(providedResources.contains(new Resource("test12", "javax.sql.DataSourse112")));
    
		referencesTo = Applications.getReferenceGraph().getReferencesToOthersFrom(Component.create(SAP_COM_APPLICATION_NAME));
		Applications.getReferenceGraph().print(new PrintWriter(System.out));//$JL-SYS_OUT_ERR$
		Assert.assertEquals(2, referencesTo.size());

		Assert.assertTrue(referencesTo.contains(
			new Edge<Component>(Component.create(SAP_COM_APPLICATION_NAME), 
				Component.create("sap.com/app"), Edge.Type.HARD, null)));
		Assert.assertTrue(referencesTo.contains(
			new Edge<Component>(Component.create(SAP_COM_APPLICATION_NAME), 
				Component.create("sap.com/application234"),
				Edge.Type.HARD, new Resource("sap.com/application234", Util.APPLICATION))));

		Applications.remove(SAP_COM_APPLICATION_NAME);
		Assert.assertNull(Applications.getResourceProvider(
			Util.makeProvidedResource("test12", "javax.sql.DataSourse112")));

		referencesTo = Applications.getReferenceGraph()
			.getReferencesToOthersFrom(Component.create(SAP_COM_APPLICATION_NAME));
		Assert.assertEquals(0, referencesTo.size());

		Set<Edge<Component>> referencesFrom = Applications
			.getReferenceGraph().getReferencesFromOthersTo(
				Component.create(SAP_COM_APPLICATION_NAME));
		Assert.assertEquals(0, referencesFrom.size());
	}

	public void testAddMissingResourceReferences() throws Exception {
		Applications.clear();
		Util.createDI(SAP_COM_APPLICATION_NAME, new ReferenceObject[] {},
			new ResourceReference[] { Util.makeResourceApplicationRef(SAP_COM_APPLICATION4NAME),
				Util.makeResourceApplicationRef(SAP_COM_APPLICATION5NAME), },
			new Resource[] {});
		Util.createDI(SAP_COM_APPLICATION4NAME, new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});
		Util.createDI(SAP_COM_APPLICATION5NAME, new ReferenceObject[] {}, new ResourceReference[] {},
			new Resource[] {});

		Set<Edge<Component>> referencesTo = Applications
			.getReferenceGraph().getReferencesToOthersFrom(Component.create(SAP_COM_APPLICATION_NAME));
		Assert.assertEquals(2, referencesTo.size());
		Assert.assertTrue(referencesTo.contains(
			new Edge<Component>(Component.create(SAP_COM_APPLICATION_NAME), 
				Component.create(SAP_COM_APPLICATION4NAME),
				Edge.Type.HARD, new Resource(SAP_COM_APPLICATION4NAME, Util.APPLICATION))));
		Assert.assertTrue(referencesTo.contains(
			new Edge<Component>(Component.create(SAP_COM_APPLICATION_NAME), 
				Component.create(SAP_COM_APPLICATION5NAME),
				Edge.Type.HARD, new Resource(SAP_COM_APPLICATION5NAME, Util.APPLICATION))));
	}

	private static final String DS1 = "DS1";
	private static final String JAVAX_SQL_DATA_SOURCE = "javax.sql.DataSource";
	private static final String SAP_COM_APPLICATION_NAME_1 = "sap.com/applicationName_1";
  
	public void testAddResourceReferenceToPrivateResource() throws Exception {
		Applications.clear();
    
		Resource privateResource = new Resource(DS1, JAVAX_SQL_DATA_SOURCE, Resource.AccessType.PRIVATE);

		Util.createDI(SAP_COM_APPLICATION_NAME, new ReferenceObject[] {},
			new ResourceReference[] { 
				new ResourceReference(DS1, JAVAX_SQL_DATA_SOURCE, ReferenceObjectIntf.REF_TYPE_HARD) },
			new Resource[] { privateResource });

		Util.createDI(SAP_COM_APPLICATION_NAME_1, new ReferenceObject[] {},
			new ResourceReference[] { 
				new ResourceReference(DS1, JAVAX_SQL_DATA_SOURCE, ReferenceObjectIntf.REF_TYPE_HARD) },
			new Resource[] {});
		Set<Edge<Component>> referencesTo = Applications
			.getReferenceGraph().getReferencesToOthersFrom(Component.create(SAP_COM_APPLICATION_NAME));
		// Self cycles are not allowed. Private resources are handled before to build the graph. 
		Assert.assertEquals(0, referencesTo.size());
		referencesTo = Applications.getReferenceGraph().getReferencesToOthersFrom(
			Component.create(SAP_COM_APPLICATION_NAME_1));
		Assert.assertEquals(1, referencesTo.size());
		Assert.assertFalse(referencesTo.contains(new Edge<Component>(
			Component.create(SAP_COM_APPLICATION_NAME_1), Component.create(SAP_COM_APPLICATION_NAME),
			Edge.Type.HARD, new Resource(DS1, JAVAX_SQL_DATA_SOURCE))));
		Assert.assertTrue(referencesTo.contains(
			new Edge<Component>(Component.create(SAP_COM_APPLICATION_NAME_1),
				CompRefGraph.RESOURCE_NOT_PROVIDED, 
				Edge.Type.HARD, new Resource(DS1, JAVAX_SQL_DATA_SOURCE))));
	}
}