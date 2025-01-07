package com.sap.ip.j2eeengine.consistency;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.ip.basecomps.consistency.ConsistencyDomain;
import com.sap.ip.basecomps.consistency.impl.DomainSupport;
import com.sap.ip.basecomps.util.WeakHashTable;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class J2EEConsistency extends DomainSupport {

	private static Location myLoc = Location.getLocation(J2EEConsistency.class);
	Listener listener;

	public J2EEConsistency(Properties props) {
		super(props);
		String mn = "J2EEConsistency(Properties props)";
		listener = new Listener();
		if (myLoc.beInfo()) {
			myLoc.infoT(mn, "J2EEConsistency Domain Support: " + getName());
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// notification listener
	///////////////////////////////////////////////////////////////////////////

	static private String MESSAGE = "msg.";

	private class Listener implements Topic.Listener {
		Topic topic;

		public Listener() {
			topic = Topic.getTopic("ConsistencyDomain/" + getName());
			topic.register(this);
		}

		public Topic getTopic() {
			return topic;
		}

		public String getTopicName() {
			return topic.getName();
		}

		public void receive(Message message) {
			String mn = "Listener.receive(Message message)";
			Properties p = message.getProperties();
			Enumeration e = p.propertyNames();
			while (e.hasMoreElements()) {
				String id = (String) e.nextElement();
				if (myLoc.beInfo()) {
					myLoc.infoT(mn, "  message id: " + id);
				}
				if (id.startsWith(MESSAGE)) {
					String v = p.getProperty(id);
					if (myLoc.beInfo()) {
						myLoc.infoT(mn, "  message value: " + v);
					}
					id = id.substring(MESSAGE.length());
					LocalLockImpl lock = getLock(id);
					if (lock != null)
						lock.getLockable().notify(v);
				}
			}
		}
	};

	String getTopic() {
		return listener.getTopicName();
	}

	private void broadcast(Properties p) {
		listener.getTopic().send(p);
	}

	///////////////////////////////////////////////////////////////////////////
	// lock handling
	///////////////////////////////////////////////////////////////////////////

	private WeakHashTable locks = new WeakHashTable();

	private LocalLockImpl getLock(String id) {
		return (LocalLockImpl) locks.get(id);
	}

	public void assertConsistency() { // all propagations are active
	}

	private String buildId(String namespace, Lockable o) {
		return namespace + ":" + o.getId();
	}

	synchronized public Lock createLock(String namespace, Lockable o) {
		String id = buildId(namespace, o);
		LocalLockImpl lock = getLock(id);
		if (lock == null) {
			deb.out("new lock for " + id);
			lock = new LocalLockImpl(namespace, o);
		} else
			lock.setLockable(o);
		return lock;
	}

	public LockGroup createLockGroup(String id) {
		return new LocalLockGroupImpl(id);
	}

	///////////////////////////////////////////////////////////////////////////
	// lock implementation
	///////////////////////////////////////////////////////////////////////////

	private class LocalLockImpl extends LockSupport {
		private String id;

		public LocalLockImpl(String ns, Lockable o) {
			super(ns, o);
			this.id = buildId(namespace, o);
			locks.put(id, this);
		}

		public ConsistencyDomain getDomain() {
			return J2EEConsistency.this;
		}

		public String getId() {
			return id;
		}

		public void assertConsistency() { // no propgations at all
		}

		public synchronized boolean lock(boolean nonblocking, LockGroup grp, long timeout)
			throws IOException {
			String mn = "lock(boolean nonblocking, LockGroup grp, long timeout)";
			Object o = buildOwner(grp);

			if (locked > 0 && (nonblocking && o == owner))
				return false;

			deb.out("lock " + object);
			while (locked > 0 && owner != o) {
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					LoggingHelper.traceThrowable(Severity.WARNING, myLoc, mn, e);
				}
				if (timeout > 0)
					return false;
			}
			locked++;
			owner = o;
			group = grp;
			if (group != null)
				group.addLock(this);
			return true;
		}

		synchronized public Confirmation unlock(
			LockGroup grp,
			boolean confirm) {
			deb.out("unlock " + object);
			if (locked > 0 && owner == buildOwner(grp)) {
				if (--locked == 0) {
					if (group != null) {
						String info =
							((LocalLockGroupImpl) group).getPropagationInfo(
								this);
						if (info != null)
							doPropagate(info, false);
					}
					notifyUnlock();
				}
			}
			if (confirm)
				return new DummyConfirmation();
			return null;
		}

		public boolean isLocalLock() {
			return true;
		}

		public boolean isLocked() {
			return isLocallyLocked();
		}

		public Confirmation propagate(
			String info,
			boolean confirm) { // only local
			deb.out("propagate " + info + " for " + object);
			if (group != null) {
				((LocalLockGroupImpl) group).propagate(this, info);
			} else {
				return doPropagate(info, confirm);
			}
			if (confirm)
				return new DummyConfirmation();
			return null;
		}

		private Confirmation doPropagate(String info, boolean confirm) {
			String mn =
				"LocalLockImpl.doPropagate(String info, boolean confirm)";
			Properties c = new Properties();
			c.setProperty(MESSAGE + getId(), info);
			if (myLoc.beInfo()) {
				myLoc.infoT(mn, "Sending broadcast: " + getId() + ": " + info);
			}
			broadcast(c);

			if (confirm)
				return new DummyConfirmation();
			return null;
		}

		protected void _clearLock() {
			deb.out("release lock " + object);
			notify();
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// Lock Groups
	////////////////////////////////////////////////////////////////////////////

	private class LocalLockGroupImpl extends LockGroupSupport {
		HashMap propagations = new HashMap();

		LocalLockGroupImpl(String id) {
			super(id);
		}

		public ConsistencyDomain getDomain() {
			return J2EEConsistency.this;
		}

		void propagate(LocalLockImpl lock, String info) {
			propagations.put(lock.getId(), info);
		}

		String getPropagationInfo(LocalLockImpl lock) {
			return (String) propagations.get(lock.getId());
		}

		synchronized protected Confirmation _unlock(
			boolean confirm,
			boolean aborted) {

			String mn =
				"LocalLockGroupImpl.Confirmation _unlock(boolean confirm,boolean aborted)";
			Iterator i;
			if (aborted)
				propagations.clear();

			if (!this.locks.isEmpty()) {
				deb.out("unlock group " + id);
				if (!propagations.isEmpty()) {
					Properties c = new Properties();
					i = propagations.keySet().iterator();
					while (i.hasNext()) {
						String id = (String) i.next();
						String info = (String) propagations.get(id);
						c.setProperty(MESSAGE + id, info);
						if (myLoc.beInfo()) {
							myLoc.infoT(
								mn,
								"adding broadcast: " + id + ": " + info);
						}
					}
					broadcast(c);
					propagations.clear();
				}
			}
			if (confirm)
				return new DummyConfirmation();
			return null;
		}
	}
}
