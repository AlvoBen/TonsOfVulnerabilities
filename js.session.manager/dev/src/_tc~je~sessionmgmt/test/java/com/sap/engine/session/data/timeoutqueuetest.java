package com.sap.engine.session.data;

import org.junit.Test;

import com.sap.engine.session.data.share.TimeoutHashtableFactory;
import com.sap.engine.session.data.share.TimeoutQueueFactory;
import com.sap.engine.session.data.share.exceptions.NoSuchQueueException;
import com.sap.engine.session.data.share.exceptions.NullClassLoaderException;
import com.sap.engine.session.data.share.exceptions.TooManyQueuesException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class TimeoutQueueTest {

	static Location l7 = Location.getLocation(com.sap.engine.session.data.share.TimeoutHashtableElement.class);
	static Location l8 = Location.getLocation(com.sap.engine.session.data.share.TimeoutHashtableFactory.class);
	static Location l9 = Location.getLocation(com.sap.engine.session.data.share.TimeoutQueueElement.class);
	static Location l10 = Location.getLocation(com.sap.engine.session.data.share.TimeoutQueueFactory.class);

	static {
		l7.setEffectiveSeverity(Severity.ALL);
		l8.setEffectiveSeverity(Severity.ALL);
		l9.setEffectiveSeverity(Severity.ALL);
		l10.setEffectiveSeverity(Severity.ALL);

	}

	@Test
	public void testCreateFactory() {
		new TimeoutHashtableFactory();
		TimeoutHashtableFactory.getTimeoutHashtablesThreshold();
		new TimeoutQueueFactory();
		TimeoutQueueFactory.getQueuesThreshold();
	}

	@Test
	public void testCreateQueue() throws NullClassLoaderException, TooManyQueuesException {
		createQueue(DataClass.class);
	}

	@Test
	public void testGetQueue() {
		try {
			createQueue(DataClass.class);
			TimeoutQueueFactory.getTimeoutQueue(DataClass.class);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testGetQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullQueue() {
		try {
			TimeoutQueueFactory.getTimeoutQueue(null);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testGetNullQueue1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TimeoutQueueFactory.getTimeoutQueue(String.class);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testGetNullQueue2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveQueues() {
		try {
			createQueue(DataClass.class);
			removeQueue(DataClass.class);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testRemoveQueues:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveStringQueues() {
		try {
			createStringQueue(this.getClass().getClassLoader());
			removeStringQueue(this.getClass().getClassLoader());
			removeStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testRemoveStringQueues:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}

	@Test
	public void testRemoveNullQueues() {
		try {
			removeStringQueue(null);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testRemoveNullQueues1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			removeQueue(String.class);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testRemoveNullQueues2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullQueue() {
		try {
			createQueue(null);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateNullQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateQueueWithTresHold() {
		try {
			createTrQueue(DataClass.class, 5);
			createTrQueue(DataClass.class, 6);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullQueueWithTresHold() {
		try {
			createTrQueue(null, 5);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateNullQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringQueue() {
		try {
			createStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateStringQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetStringQueue() {
		try {
			createStringQueue(this.getClass().getClassLoader());
			TimeoutQueueFactory.getTimeoutStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testGetStringQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringQueue() {
		try {
			createStringQueue(null);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateNullStringQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullStringQueue() {
		try {
			TimeoutQueueFactory.getTimeoutStringQueue(null);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testGetNullStringQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringQueueWithTresHold() {
		try {
			createStringTrQueue(this.getClass().getClassLoader(), 5);
			createStringTrQueue(this.getClass().getClassLoader(), 6);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateStringQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringQueueWithTresHold() {
		try {
			createStringTrQueue(null, 5);
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testCreateNullStringQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testEditQueue() {
		com.sap.engine.session.data.share.Queue queue = null;
		try {
			queue = createQueue(this.getClass());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.add(new DataClass());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.add(new DataClass());
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue3:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.get();
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue4:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.isEmpty();
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue5:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.size();
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue6:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.read();
		} catch (Throwable e) {
			System.out.println("TimeoutQueueTest : testEditQueue7:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	public static long timeout = 0;

	private com.sap.engine.session.data.share.Queue createQueue(Class cl) throws NullClassLoaderException,
			TooManyQueuesException {
		return TimeoutQueueFactory.createTimeoutQueue(cl, timeout);
	}

	private void removeQueue(Class cl) throws NullClassLoaderException, NoSuchQueueException {
		TimeoutQueueFactory.removeTimeoutQueue(cl);
	}

	private void removeStringQueue(ClassLoader cl) throws NullClassLoaderException, NoSuchQueueException {
		TimeoutQueueFactory.removeTimeoutStringQueue(cl);
	}

	private com.sap.engine.session.data.share.Queue createTrQueue(Class cl, int tr) throws NullClassLoaderException,
			TooManyQueuesException {
		return TimeoutQueueFactory.createTimeoutQueue(cl, timeout, tr);
	}

	private com.sap.engine.session.data.share.Queue createStringQueue(ClassLoader cl) throws NullClassLoaderException,
			TooManyQueuesException {
		return TimeoutQueueFactory.createTimeoutStringQueue(cl, timeout);
	}

	private com.sap.engine.session.data.share.Queue createStringTrQueue(ClassLoader cl, int tr)
			throws NullClassLoaderException, TooManyQueuesException {
		return TimeoutQueueFactory.createTimeoutStringQueue(cl, timeout, tr);
	}

}
