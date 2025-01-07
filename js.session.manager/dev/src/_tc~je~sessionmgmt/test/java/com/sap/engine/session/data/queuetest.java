package com.sap.engine.session.data;

import org.junit.Test;

import com.sap.engine.session.data.share.QueueFactory;
import com.sap.engine.session.data.share.QueueImpl;
import com.sap.engine.session.data.share.exceptions.NoSuchQueueException;
import com.sap.engine.session.data.share.exceptions.NullClassLoaderException;
import com.sap.engine.session.data.share.exceptions.TooManyQueuesException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class QueueTest {

	static Location l4 = Location.getLocation(com.sap.engine.session.data.share.Queue.class);
	static Location l5 = Location.getLocation(com.sap.engine.session.data.share.QueueFactory.class);
	static Location l6 = Location.getLocation(com.sap.engine.session.data.share.QueueImpl.class);

	static {

		l4.setEffectiveSeverity(Severity.ALL);
		l5.setEffectiveSeverity(Severity.ALL);
		l6.setEffectiveSeverity(Severity.ALL);

	}

	@Test
	public void testCreateFactory() {
		new QueueFactory();
		QueueFactory.getQueuesThreshold();
	}

	@Test
	public void testCreateQueue() throws NullClassLoaderException, TooManyQueuesException {
		createQueue(DataClass.class);
	}

	@Test
	public void testGetQueue() {
		try {
			createQueue(DataClass.class);
			QueueFactory.getQueue(DataClass.class);
		} catch (Throwable e) {
			System.out.println("QueueTest: testGetQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullQueue() {
		try {
			QueueFactory.getQueue(null);
		} catch (Throwable e) {
			System.out.println("QueueTest: testGetNullQueue1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			QueueFactory.getQueue(String.class);
		} catch (Throwable e) {
			System.out.println("QueueTest: testGetNullQueue2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveQueues() {
		try {
			createQueue(DataClass.class);
			removeQueue(DataClass.class);
		} catch (Throwable e) {
			System.out.println("QueueTest: testRemoveQueues:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveStringQueues() {
		try {
			createStringQueue(this.getClass().getClassLoader());
			removeStringQueue(this.getClass().getClassLoader());
			removeStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("QueueTest: testRemoveStringQueues: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}

	@Test
	public void testRemoveNullQueues() {
		try {
			removeStringQueue(null);
		} catch (Throwable e) {
			System.out.println("QueueTest: testRemoveNullQueues1: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			removeQueue(String.class);
		} catch (Throwable e) {
			System.out.println("QueueTest: testRemoveNullQueues2: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullQueue() {
		try {
			createQueue(null);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateNullQueue: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateQueueWithTresHold() {
		try {
			createTrQueue(DataClass.class, 5);
			createTrQueue(DataClass.class, 6);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullQueueWithTresHold() {
		try {
			createTrQueue(null, 5);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateNullQueueWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringQueue() {
		try {
			createStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateStringQueue: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetStringQueue() {
		try {
			createStringQueue(this.getClass().getClassLoader());
			QueueFactory.getStringQueue(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("QueueTest: testGetStringQueue:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringQueue() {
		try {
			createStringQueue(null);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateNullStringQueue: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullStringQueue() {
		try {
			QueueFactory.getStringQueue(null);
		} catch (Throwable e) {
			System.out.println("QueueTest: testGetNullStringQueue: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringQueueWithTresHold() {
		try {
			createStringTrQueue(this.getClass().getClassLoader(), 5);
			createStringTrQueue(this.getClass().getClassLoader(), 6);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateStringQueueWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringQueueWithTresHold() {
		try {
			createStringTrQueue(null, 5);
		} catch (Throwable e) {
			System.out.println("QueueTest: testCreateNullStringQueueWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testEditQueue() {

		com.sap.engine.session.data.share.Queue queue = null;
		try {
			queue = createQueue(DataClass.class);
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue1: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.add(new DataClass());
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue2: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.add(new DataClass());
		} catch (Throwable e) {
			System.out.println("QueueTest : testEditQueue3: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.get();
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue4: ExceptionQueueTest : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.isEmpty();
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue5: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.size();
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue6: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.read();
		} catch (Throwable e) {
			System.out.println("QueueTest: testEditQueue7: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	private com.sap.engine.session.data.share.Queue createQueue(Class cl) throws NullClassLoaderException,
			TooManyQueuesException {
		return QueueFactory.createQueue(cl);
	}

	private void removeQueue(Class cl) throws NullClassLoaderException, NoSuchQueueException {
		QueueFactory.removeQueue(cl);
	}

	private void removeStringQueue(ClassLoader cl) throws NullClassLoaderException, NoSuchQueueException {
		QueueFactory.removeStringQueue(cl);
	}

	private com.sap.engine.session.data.share.Queue createTrQueue(Class cl, int tr) throws NullClassLoaderException,
			TooManyQueuesException {
		return QueueFactory.createQueue(cl, tr);
	}

	private com.sap.engine.session.data.share.Queue createStringQueue(ClassLoader cl) throws NullClassLoaderException,
			TooManyQueuesException {
		return QueueFactory.createStringQueue(cl);
	}

	private com.sap.engine.session.data.share.Queue createStringTrQueue(ClassLoader cl, int tr)
			throws NullClassLoaderException, TooManyQueuesException {
		return QueueFactory.createStringQueue(cl, tr);
	}

	public void testQueueImpl() {
		QueueImpl queue = new QueueImpl(this.getClass().getName(), this.getClass().getClassLoader().toString(), 100, 0);
		try {
			queue.isEmpty();
			queue.add(this);
			queue.add(null);
			queue.isEmpty();
		} catch (Throwable e) {
			System.out.println("QueueTest: testQueueImpl: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.size();
		} catch (Throwable e) {
			System.out.println("QueueTest : testQueueImpl:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.get();
		} catch (Throwable e) {
			System.out.println("QueueTest : testQueueImpl:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.read();
		} catch (Throwable e) {
			System.out.println("QueueTest : testQueueImpl:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			queue.add("Test");
		} catch (Throwable e) {
			System.out.println("QueueTest : testQueueImpl: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

	}
}
