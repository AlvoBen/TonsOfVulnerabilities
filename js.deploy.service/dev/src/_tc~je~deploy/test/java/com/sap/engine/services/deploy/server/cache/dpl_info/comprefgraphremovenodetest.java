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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.refgraph.Util;

/**
 *@author Luchesar Cekov
 */
public class CompRefGraphRemoveNodeTest extends TestCase {
	private DeploymentInfo[] apps;
	
    /*
     * +Application References
    |
    +->+application:node0
    |  |
    |  +->+hard to application:node2
    |  |  |
    |  |  +->+weak to application:node6
    |  |  |
    |  |  +->+hard to application:node5
    |  |
    |  +->+hard to application:node1
    |     |
    |     +->+weak to application:node4
    |     |
    |     +->+hard to application:node3
    |
    |
    +->+application:node7
       |
       +->+weak to application:node9
       |
       +->+hard to application:node8
             |
             +->+hard to application:node2
     */
	@Override
	public void setUp() {
		apps = new DeploymentInfo[] {
			Util.createDI("node0", new ReferenceObject[] {
				Util.makeHardReftoApplicaiton("node1"),
				Util.makeHardReftoApplicaiton("node2")}),
			Util.createDI("node1", new ReferenceObject[] {
				Util.makeHardReftoApplicaiton("node3"),
				Util.makeWeakRefToApplicaiton("node4")}),
			Util.createDI("node2", new ReferenceObject[] {
				Util.makeHardReftoApplicaiton("node5"),
				Util.makeWeakRefToApplicaiton("node6")}),
			new DeploymentInfo("node3"),
			new DeploymentInfo("node4"),
			new DeploymentInfo("node5"),
			new DeploymentInfo("node6"),
			Util.createDI("node7", new ReferenceObject[] {
				Util.makeHardReftoApplicaiton("node8"),
				Util.makeWeakRefToApplicaiton("node9")}),
			new DeploymentInfo("node8"),
			new DeploymentInfo("node9")
		};

		Applications.clear();
		for (int i = 0; i < apps.length; i++) {
			Applications.add(apps[i]);
		}
	}
  
	/**
	 * If an application is removed  but it is still referenced, it will stay 
	 * in the graph. It will be removed when it is not more referenced.
	 */
	public void testRemoveReferencedApplication() throws Exception {
		final Graph<Component> refGraph = Applications.getReferenceGraph();
		Applications.remove(apps[5].getApplicationName());
		// Must be here because is still referenced by node2
		Assert.assertTrue(refGraph.containsNode(
			Component.create(apps[5].getApplicationName())));
		Applications.remove(apps[2].getApplicationName());
		// node2 must be here because is still referenced by node0.
		Assert.assertTrue(refGraph.containsNode(
			Component.create(apps[2].getApplicationName())));
		// node5 must be released here.
		Assert.assertFalse(refGraph.containsNode(
			Component.create(apps[5].getApplicationName())));
		Applications.remove(apps[0].getApplicationName());
		// Now node2 has to disappear.
		Assert.assertFalse(refGraph.containsNode(
			Component.create(apps[2].getApplicationName())));
		final Component node6 = Component.create(apps[6].getApplicationName());
		Assert.assertTrue(refGraph.containsNode(node6));
		Assert.assertEquals(0, refGraph.getReferencesFromOthersTo(node6).size());
		Assert.assertEquals(0, refGraph.getReferencesToOthersFrom(node6).size());
	}
// TODO: new test for ResourceRefereces via resources
	public void testAddRemovedNodeWithReferencesFrom() throws Exception {
		final Graph<Component> refGraph = Applications.getReferenceGraph();
		Applications.remove(apps[2].getApplicationName());
		Component node2 = Component.create(apps[2].getApplicationName());
		Assert.assertTrue(refGraph.containsNode(node2));
		Assert.assertEquals(1, refGraph.getReferencesFromOthersTo(node2).size());
		Assert.assertEquals(0, refGraph.getReferencesToOthersFrom(node2).size());
		Applications.add(apps[2]);
		Assert.assertEquals(1, refGraph.getReferencesFromOthersTo(node2).size());
		Assert.assertEquals(2, refGraph.getReferencesToOthersFrom(node2).size());
	}
}
