package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import static com.sap.engine.services.deploy.server.DeployConstants.stopApp;
import static com.sap.engine.services.deploy.server.DeployConstants.update;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.lib.refgraph.Edge;
import com.sap.engine.lib.refgraph.impl.Graph;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockManager;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker;
import com.sap.engine.services.deploy.server.utils.concurrent.LockTracker.LockState;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.SingleNodeLockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.StartLockEvaluator;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.StopLockEvaluator;

/**
 * @author Emil Dinchev
 *
 */
public class LockManagerImplTest {
	private static final char ENQUEUE_LOCK_MODE = (char) 0;
	/**
	 * Status message for failure to obtain shared lock for component A.
	 */
	private static final String FAILURE_A_IS_NOT_LOCKED_SHARED_BY_T1 = 
		"Component A is not locked shared by T1.";
	/**
	 * Status message for failure to obtain exclusive lock for component A.
	 */
	private static final String FAILURE_A_NOT_LOCKED_EXCLUSIVELY_BY_T1 = 
		"Component A is not locked exclusively by T1.";
	/**
	 * Status message for success.
	 */
	private static final String STATUS_OK = "OK";

	private static Graph<Component> graph;
	private static Map<String, Component> components;
	
	private LockManager<Component> lockManager;
	private LockTracker<Component> lockTracker;
	
	private String status;

	@BeforeClass
	public static void setUpOnce() throws Exception {
		PropManagerFactory.initInstance("appWorksDir", 10, "myServerNode");
		components = initComponents();
		graph = initGraph();
	}
	
	@Before
	public void setUp() {
		// Clean up the status for failure before every test.
		status = STATUS_OK;
	}
	
	
	/**
	 * Set up the test environment.
	 * @param failFast Whether we have to fail fast if a child thread has to
	 * wait for exclusive lock.
	 */
	private void setUp(final boolean failFast) {
		lockManager = new LockManagerImpl<Component>(
			new MockThreadSystem(), new EnqueueLockerImplEx(), failFast);
		lockTracker = lockManager.getLockTracker();
	}
	
	/**
	 * Test the locking of component A by two threads, both trying to lock it
	 * exclusively. T1 is started first, and locks exclusively node A for 4s.
	 * A second after that is started T2, which tries to obtain exclusive locks
	 * for A, B, C, D, E and G. This has to fail, because A is already locked
	 * exclusively by T1.
	 */
	@Test
	public void test_X_X_Lock() {
		setUp(true);
		exec_X_X_Lock();
		assertLockSetNotAcquired();
	}

	/**
	 * Test the exclusive lock of component A by three threads from the same 
	 * family.We have to succeed.
	 */
	@Test
	public void testFamily_X_X_Lock() {
		setUp(false);
		exec_Family_X_X_Lock();
		assertEquals(STATUS_OK, status);
	}

	private void exec_Family_X_X_Lock() {
		try {
			// Obtains the lock from the current thread,
			// and child threads will inherit it.   
			final LockSet lockSet = lockManager.lock(
				new SingleNodeLockEvaluator(
					update, components.get("A"), ENQUEUE_LOCK_MODE, 0));
			exec_X_X_Lock();
			lockManager.unlock(lockSet);
		} catch(ConflictingOperationLockException ex) {
			storeExeption(ex);
		} catch(LockSetNotAcquiredException ex) {
			storeExeption(ex);
		} catch (InterruptedException ex) {
			storeExeption(ex);
		}
	}

	/**
	 * Test the exclusive lock of component A by three threads from the same
	 * family. Since <tt>failFast</tt> flag is <tt>true</tt> we expect
	 * ConflictingOperationLockException.
	 */
	@Test
	public void testFailFast_X_X_Lock() {
		setUp(true);
		exec_Family_X_X_Lock();
		assertConflictingOperation();
	}

	private void assertConflictingOperation() {
		final String ex = 
			ConflictingOperationLockException.class.getCanonicalName();
		if(!status.startsWith(ex)) {
			fail("We have expected " + ex + " but status is:\n" + status);
		}
	}
	
	private void exec_X_X_Lock() {
		// T1 is started first and holds the exclusive lock for A for 4s.
		final Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					final Component A = components.get("A");
					final LockSet lockSet = lockManager.lock(
						new SingleNodeLockEvaluator(
							update, A, ENQUEUE_LOCK_MODE, 0));
					if(lockTracker.getLockState(A) != LockState.EXCLUSIVE) {
						status = FAILURE_A_NOT_LOCKED_EXCLUSIVELY_BY_T1;
					}
					Thread.sleep(4000);
					lockManager.unlock(lockSet);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				} catch (ConflictingOperationLockException ex) {
					storeExeption(ex);
				}
			}
		}, "T1");
		
		// T2 is started second and waits for 1s 
		// to lock exclusively the successors of G.
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				try {
					// Wait to be sure that A is already locked by T1.
					Thread.sleep(1000);
					final StopLockEvaluator evaluator = initStopEvaluator();
					final LockSet lockSet = lockManager.lock(evaluator);
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T2");
		execute(th1, th2);
	}

	/**
	 * Test the locking of component A by two threads, T1 is locking it 
	 * exclusively, and T2 tries to lock it shared. T1 is started first, and
	 * locks exclusively node A for 4s. A second after that is started T2, 
	 * which tries to obtain shared locks for A, B, C, D, E and G. This has to
	 * fail, because A is already locked exclusively by T1.
	 */
	@Test
	public void test_X_S_Lock() {
		setUp(true);
		exec_X_S_Lock();
		assertLockSetNotAcquired();	
	}

	/**
	 * Test the locking of component A by three threads from the same family. 
	 * The parent thread locks it exclusively, T1 - exclusively and T2 - 
	 * shared. All threads have to obtain the locks successfully.
	 */
	@Test
	public void testFamily_X_S_Lock() {
		setUp(false);
		exec_Family_X_S_Lock();
		assertEquals(STATUS_OK, status);
	}

	/**
	 * Test the locking of component A by three threads from the same family.
	 * Since <tt>failFast</tt> flag is <tt>true</tt>, we are expecting 
	 * ConflictingOperationLockException.
	 */
	@Test
	public void testFailFast_X_S_Lock() {
		setUp(true);
		exec_Family_X_S_Lock();
		assertConflictingOperation();
	}
	
	private void exec_Family_X_S_Lock() {
		try {
			final LockSet lockSet = lockManager.lock(
				new SingleNodeLockEvaluator(
					update, components.get("A"), ENQUEUE_LOCK_MODE, 0));
			exec_X_S_Lock();
			lockManager.unlock(lockSet);
		} catch(ConflictingOperationLockException ex) {
			storeExeption(ex);
		} catch(LockSetNotAcquiredException ex) {
			storeExeption(ex);
		} catch (InterruptedException ex) {
			storeExeption(ex);
		}
	}
	
	private void exec_X_S_Lock() {
		// T1 is started first and holds the lock for A for 4s.
		final Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					final Component A = components.get("A");
					final LockSet lockSet = lockManager.lock(
						new SingleNodeLockEvaluator(
							update, A, ENQUEUE_LOCK_MODE, 0));
					if(lockTracker.getLockState(A) != LockState.EXCLUSIVE) {
						status = FAILURE_A_NOT_LOCKED_EXCLUSIVELY_BY_T1;
					}
					Thread.sleep(4000);
					lockManager.unlock(lockSet);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch(InterruptedException ex) {
					storeExeption(ex);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				}
			}
		}, "T1");

		// T2 is started second and waits for 1s 
		// to lock shared the successors of G.
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				try {
					// Wait to be sure that A is already locked by T1.
					Thread.sleep(1000);
					final StopLockEvaluator evaluator = new StopLockEvaluator(
						graph, lockTracker,	stopApp, false, 
						components.get("G"), ENQUEUE_LOCK_MODE, 1000);
					final LockSet lockSet = lockManager.lock(evaluator);
					lockManager.unlock(lockSet);
				} catch (ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				}
				catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T2");

		execute(th1, th2);
	}

	/**
	 * Test the locking of component A by two threads, T1 is locking it 
	 * shared, and T2 tries to lock it exclusively. T1 is started first, and
	 * locks shared the node A for 4s. A second after that is started T2, which
	 * tries to obtain exclusive lock for A. This has to fail, because A is 
	 * already locked shared by T1.
	 */	
	@Test
	public void test_S_X_Lock() {
		setUp(false);
		exec_S_X_Lock();
		assertLockSetNotAcquired();
	}

	/**
	 * Test the locking of component A by three threads from the same family.
	 * The parent thread and thread T1 locks shared node A. T2 tries to obtain 
	 * exclusive lock for A. We have to succeed.
	 */
	@Test
	public void testFamily_S_X_Lock() {
		setUp(false);
		exec_Family_S_X_Lock();
		assertEquals(STATUS_OK, status);
	}

	/**
	 * Test the locking of component A by three threads by the same family. 
	 * The parent thread and thread T1 locks shared node A. T2 tries to obtain
	 * exclusive lock for A. Since the <tt>failFast</tt> flag is <tt>true</tt> 
	 * we expect ConflictingOperationLockException.
	 */
	@Test
	public void testFailFast_S_X_Lock() {
		setUp(true);
		exec_Family_S_X_Lock();
		assertConflictingOperation();
	}
	
	private void exec_Family_S_X_Lock() {
		try {
			final Component A = components.get("A");
			final StopLockEvaluator evaluator = new StopLockEvaluator(
				graph, lockTracker, stopApp, 
				false, components.get("G"), ENQUEUE_LOCK_MODE, 0);
			final LockSet lockSet = lockManager.lock(evaluator);
			if(lockTracker.getLockState(A) != LockState.SHARED) {
				status = FAILURE_A_IS_NOT_LOCKED_SHARED_BY_T1;
			}
			exec_S_X_Lock();
			lockManager.unlock(lockSet);
		} catch(ConflictingOperationLockException ex) {
			storeExeption(ex);
		} catch(LockSetNotAcquiredException ex) {
			storeExeption(ex);
		} catch (InterruptedException ex) {
			storeExeption(ex);
		}
	}
	
	private void exec_S_X_Lock() {
		// T1 is started first and holds the lock for A for 4s.
		final Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					final Component A = components.get("A");
					final StopLockEvaluator evaluator = new StopLockEvaluator(
						graph, lockTracker, stopApp, 
						false, components.get("G"), ENQUEUE_LOCK_MODE, 0);
					final LockSet lockSet = lockManager.lock(evaluator);
					if(lockTracker.getLockState(A) != LockState.SHARED) {
						status = FAILURE_A_IS_NOT_LOCKED_SHARED_BY_T1;
					}
					Thread.sleep(4000);
					lockManager.unlock(lockSet);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				} catch (ConflictingOperationLockException ex) {
					storeExeption(ex);
				} 
			}
		}, "T1");

		// T2 is started second and waits for 1s 
		// to lock shared the successors of G.
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				try {
					// Wait to be sure that A is already locked by T1.
					Thread.sleep(1000);
					final Component A = components.get("A");
					final LockSet lockSet = lockManager.lock(
						new SingleNodeLockEvaluator(
							update, A, ENQUEUE_LOCK_MODE, 1000));
					lockManager.unlock(lockSet);
				} catch (ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T2");

		execute(th1, th2);
	}

	/**
	  * Test the locking of component A by two threads from different families.
	  * T1 is stated first and locks shared A, D, F and G for 4s. A second 
	  * after that is started T2, which locks shared A, B, C, D, E and G. We
	  * have to succeed.
	  */	
	@Test
	public void test_S_S_Lock() {
		setUp(false);
		exec_S_S_Lock();
		assertEquals(STATUS_OK, status);		
	}


	/**
	 * Test the locking of component A by three threads from the same family.
	 * The parent thread locks shared nodes A, B, C, D, E and G.
	 * T2 tries to obtain shared locks for A, B, C, D, E and G. We have to 
	 * succeed.
	 */
	@Test
	public void testFamily_S_S_Lock() {
		setUp(false);
		execFamily_S_S_Lock();
		assertEquals(STATUS_OK, status);
	}

	/**
	 * Test the locking of component A by three threads from the same family.
	 * The parent thread locks A shared. T1 locks shared nodes A, B, C, D, E 
	 * and G. T2 tries to obtain shared locks for A, B, C, D, E and G. We have 
	 * to succeed.
	 */
	@Test
	public void testFailFast_S_S_Lock() {
		setUp(true);
		execFamily_S_S_Lock();
		assertEquals(STATUS_OK, status);
	}

	private void execFamily_S_S_Lock() {
		try {
			final Component A = components.get("A");
			final StopLockEvaluator evaluator = new StopLockEvaluator(
				graph, lockTracker, stopApp, 
				false, components.get("G"), ENQUEUE_LOCK_MODE, 0);
			final LockSet lockSet = lockManager.lock(evaluator);
			if(lockTracker.getLockState(A) != LockState.SHARED) {
				status = FAILURE_A_IS_NOT_LOCKED_SHARED_BY_T1;
			}
			exec_S_S_Lock();
			lockManager.unlock(lockSet);
		} catch(ConflictingOperationLockException ex) {
			storeExeption(ex);
		} catch(LockSetNotAcquiredException ex) {
			storeExeption(ex);
		} catch (InterruptedException ex) {
			storeExeption(ex);
		}
	}

	private void exec_S_S_Lock() {
		final Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					final LockSet lockSet = lockManager.lock(
						new StartLockEvaluator(
							graph, lockTracker, components.get("A"), 
							ENQUEUE_LOCK_MODE, 0));
					Thread.sleep(4000);
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T1");

		// Started second and waits 6000ms for lock. 
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				try {
					// Wait to be sure that A, D, F and G are 
					// already locked by T1.
					Thread.sleep(1000);
					final LockSet lockSet = lockManager.lock(
						new StopLockEvaluator(graph, lockTracker, stopApp, 
							false, components.get("G"), ENQUEUE_LOCK_MODE, 0));
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch(InterruptedException ex) {
					storeExeption(ex);
				}
			}			
		}, "T2");
		
		execute(th1, th2);
	}

	/**
	 * T1 is started first and holds the exclusive lock of A for 3s. T2 is 
	 * started 1 second after T1 and holds the exclusive lock of G for 2s. T3 
	 * is started 2s after T1 and gets shared locks for A, B, C, D, E and G.
	 * We have to succeed.
	 */
	@Test 
	public void testSingleNodeLock() {
		setUp(false);
		// T1 is started first and holds the exclusive lock of A for 3s.
		final Thread th1 = new Thread(new Runnable() {
			public void run() {
				try {
					LockSet lockSet = lockManager.lock(
						new SingleNodeLockEvaluator(
							update, components.get("A"), ENQUEUE_LOCK_MODE, 0));
					Thread.sleep(3000);
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T1");
			
		// T2 is started 1 second later and holds the exclusive lock of G for 2s.
		Thread th2 = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
					LockSet lockSet = lockManager.lock(
						new SingleNodeLockEvaluator(
							update, components.get("G"), 
							ENQUEUE_LOCK_MODE, 2000));
					Thread.sleep(2000);
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T2");
			
		// T3 is started last.
		Thread th3 = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
					final LockSet lockSet = lockManager.lock(
						new StopLockEvaluator(
							graph, lockManager.getLockTracker(),
							stopApp, false, components.get("G"), 
							ENQUEUE_LOCK_MODE, 2000));
					lockManager.unlock(lockSet);
				} catch(ConflictingOperationLockException ex) {
					storeExeption(ex);
				} catch(LockSetNotAcquiredException ex) {
					storeExeption(ex);
				} catch (InterruptedException ex) {
					storeExeption(ex);
				}
			}
		}, "T3");

		execute(th1, th2, th3);
		assertEquals(STATUS_OK, status);
	}

	/**
	 * This method is used to increase code-coverage level.
	 */
	@SuppressWarnings({ "boxing", "static-access" })
	@Test
	public void testLockTracker() {
		LockState exclusive = LockState.valueOf(LockState.class, "EXCLUSIVE");
		assertEquals(LockState.EXCLUSIVE, exclusive);
		LockState[] states = LockState.values();
		assertEquals(3, states.length);
	}

	private void assertLockSetNotAcquired() {
		String ex = LockSetNotAcquiredException.class.getCanonicalName();
		if(!status.startsWith(ex)) {
			fail("We have expected " + ex + " but the status is:\n" + status);
		}
	}

	private void execute(Thread... threads) {
		for(Thread th : threads) {
			th.start();
		}
		try {
			for(Thread th : threads) {
				th.join();
			}
		} catch(InterruptedException ex) {
			storeExeption(ex);
		}
	}
	
	private StopLockEvaluator initStopEvaluator() {
		final StopLockEvaluator evaluator = new StopLockEvaluator(
			graph, lockManager.getLockTracker(), 
			stopApp, false, components.get("G"), ENQUEUE_LOCK_MODE, 1000);
		try {
			Field field = evaluator.getClass()
				.getSuperclass().getDeclaredField("targetStatus");
			field.setAccessible(true);
			field.set(evaluator, Status.UNKNOWN);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return evaluator;
	}

	private static Map<String, Component> initComponents() {
		Map<String, Component> components = new HashMap<String, Component>();
		// Add vertexes
		components.put("A", new Component("A", Component.Type.APPLICATION));
		components.put("B", new Component("B", Component.Type.APPLICATION));
		components.put("C", new Component("C", Component.Type.APPLICATION));
		components.put("D", new Component("D", Component.Type.APPLICATION));
		components.put("E", new Component("E", Component.Type.APPLICATION));
		components.put("F", new Component("F", Component.Type.APPLICATION));
		components.put("G", new Component("G", Component.Type.APPLICATION));
		components.put("H", new Component("H", Component.Type.APPLICATION));
		return components;
	}

	
	private static Graph<Component> initGraph() { 
		Graph<Component> graph = new Graph<Component>();
		// Add vertexes
		graph.add(components.get("A"));
		graph.add(components.get("B"));
		graph.add(components.get("C"));
		graph.add(components.get("D"));
		graph.add(components.get("E"));
		graph.add(components.get("F"));
		graph.add(components.get("G"));
		graph.add(components.get("H"));
		
		// Add edges
		Edge<Component> edge = new Edge<Component>(components.get("A"), 
			components.get("D"), Edge.Type.HARD, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("B"), components.get("D"),
			Edge.Type.HARD, null);
		graph.add(edge);		
		edge = new Edge<Component>(components.get("B"), components.get("E"), 
			Edge.Type.HARD, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("C"), components.get("E"), 
			Edge.Type.WEAK, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("D"), components.get("F"), 
			Edge.Type.WEAK, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("D"), components.get("G"), 
			Edge.Type.HARD, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("E"), components.get("G"), 
			Edge.Type.HARD, null);
		graph.add(edge);
		edge = new Edge<Component>(components.get("E"), components.get("H"), 
			Edge.Type.WEAK, null);
		graph.add(edge);
		return graph;
	}
	
	private void storeExeption(Exception ex) {
		status =  ex.getClass().getCanonicalName() + "\n" +
		ex.getMessage();
	}
}