package com.sap.engine.services.dc.api.impl;

import com.sap.engine.lib.util.WaitQueue;
import com.sap.engine.services.dc.api.util.DeployApiMapper;
import com.sap.engine.services.dc.api.util.Executor;

/**
 * This class uses a number of threads to pop runnables from a queue. The number
 * can be adjusted by a dc api property, obtained by
 * DeployApiMapper.getAsyncNotificationThreads();
 * 
 * @author I040924
 * 
 */
class DefaultAsyncExecutor implements Executor {

	int number = DeployApiMapper.getAsyncNotificationThreads();

	// TODO when DC API compiler compliance level is increased to
	// 1.5 replace this with java.util.concurrent
	private WaitQueue queue = new WaitQueue();
	private Thread[] threads = new Thread[number];

	public DefaultAsyncExecutor() {

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					while (true) {
						Runnable r = (Runnable) queue.dequeue();
						r.run();

					}
				}
			};
			threads[i].setName("DC_API-ClientSideAsyncNotification#" + i);
			threads[i].setDaemon(true); // so the client jvm could exit cleanly
			threads[i].start();
		}
	}

	public void execute(Runnable r) {

		this.queue.enqueue(r);

	}

}
