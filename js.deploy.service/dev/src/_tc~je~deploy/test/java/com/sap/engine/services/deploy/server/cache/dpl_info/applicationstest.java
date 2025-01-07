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

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import junit.framework.TestCase;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.refgraph.Util;

/**
 * @author Luchesar Cekov
 */
public class ApplicationsTest extends TestCase {
	private ArrayList<ConcurrentModificationException> exceptions = 
		new ArrayList<ConcurrentModificationException>();

	@Override
	public void setUp() throws Exception {
		PropManagerFactory.initInstance("appWorkDir", 0, "ClElementBlaBla");
	}

	public void testConcurent() throws Exception {
		for (int i = 0; i < 10; i++) {
			startAddThread();
			startRemoveThread();
			startRemoveThread();
			startAddThread();
			startRemoveThread();
		}

		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}
	}

	private void startAddThread() throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					addApplicationInfoes(100, 0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ConcurrentModificationException e) {
					exceptions.add(e);
				}
			}
		};
		t.setDaemon(false);
		t.start();
		Thread.sleep(200);
	}

	private void startRemoveThread() throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					for (final DeploymentInfo dInfo : Applications.getAll()) {
						removeApplicationInfo(dInfo, 0);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ConcurrentModificationException e) {
					exceptions.add(e);
				}
			}
		};
		t.setDaemon(false);
		t.start();
		Thread.sleep(200);
	}

	private void removeApplicationInfo(final DeploymentInfo dInfo,
		final long timeout) throws InterruptedException {
		Applications.remove(dInfo.getApplicationName());
		Thread.sleep(timeout);
	}

	private void addApplicationInfoes(int number, long timeout)
			throws InterruptedException {
		for (int i = 0; i < number; i++) {
			String name = "sap.com/" + Math.random() + ""
					+ System.currentTimeMillis();
			Applications.add(new DeploymentInfo(name));

			Thread.sleep(timeout);
		}
	}

	public void _testMemoryLeaksBecauseOfClear() throws Exception {
		Util.createDI("app4", new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI("app4", new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});

		for (int i = 0; i < 100; i++) {
			createNDI(10000);
			Applications.clear();
		}
	}

	public void _testMemoryLeaksBecauseOfTraverse() throws Exception {
		Util.createDI("app4", new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		Util.createDI("app4", new ReferenceObject[] {},
				new ResourceReference[] {}, new Resource[] {});
		createNDI(10000);
		for (int i = 0; i < 10000; i++) {
			Applications.getReferenceGraph().cycleCheck();
			Applications.getReferenceGraph().sortBackward();
		}
	}

	private void createNDI(int n) {
		for (int i = 0; i < n; i++) {
			Util.createDI("app1", new ReferenceObject[] {
					new ReferenceObject("app2",
							ReferenceObjectIntf.REF_TYPE_HARD),
					new ReferenceObject("app3",
							ReferenceObjectIntf.REF_TYPE_HARD),
					new ReferenceObject("app3",
							ReferenceObjectIntf.REF_TYPE_HARD), },

			new ResourceReference[] { Util.makeResourceApplicationRef("app4"),
					Util.makeResourceApplicationRef("app5"), },

					new Resource[] {
							Util.makeProvidedResource("test1",
									"javax.sql.DataSourse"),
							Util.makeProvidedResource("test2",
									"javax.sql.DataSourse2"),
							Util.makeProvidedResource("test3",
									"javax.sql.DataSourse3") });
		}
	}

}
