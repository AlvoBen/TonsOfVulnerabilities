package com.sap.engine.services.deploy.server.utils;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.services.deploy.server.DeployConstants;

/**
 * Notifies FailedToStart monitor defined in
 * server\descriptors\monitor-configuration.xml file.
 * 
 * @author Anton Georgiev
 * @version 7.1.1
 */
public class ManagementListenerUtils {

	private final ReadWriteLock rwLock;
	private final SortedSet<String> failed2Start;

	private ManagementListener managementListener;

	public ManagementListenerUtils() {
		rwLock = new ReentrantReadWriteLock();
		failed2Start = new TreeSet<String>();
	}

	public void setManagementListener(ManagementListener managementListener) {
		this.managementListener = managementListener;
	}

	public void notify4Add(String appName) {
		final int size;
		final Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			if (!failed2Start.contains(appName)) {
				failed2Start.add(appName);
			}
			size = failed2Start.size();
		} finally {
			writeLock.unlock();
		}
		notify(size);
	}

	public void notify4Remove(String appName) {
		final int size;
		final Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		try {
			if (failed2Start.contains(appName)) {
				failed2Start.remove(appName);
			}
			size = failed2Start.size();
		} finally {
			writeLock.unlock();
		}
		notify(size);
	}

	public SortedSet<String> getFailed2Start() {
		final Lock readLock = rwLock.readLock();
		readLock.lock();
		try {
			return new TreeSet<String>(failed2Start);
		} finally {
			readLock.unlock();
		}
	}

	private void notify(int size) {
		if (managementListener != null) {
			managementListener.notify(DeployConstants.FAILED_2_START, size);
		}
	}

}
