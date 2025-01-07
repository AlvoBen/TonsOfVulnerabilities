package com.sap.engine.session.data;

import org.junit.Test;

import com.sap.engine.session.data.share.TimeoutHashtableFactory;
import com.sap.engine.session.data.share.TimeoutQueueFactory;
import com.sap.engine.session.data.share.exceptions.NoSuchHashtableException;
import com.sap.engine.session.data.share.exceptions.NullClassLoaderException;
import com.sap.engine.session.data.share.exceptions.TooManyHashtablesException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class TimeoutHashtableTest {

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
	public void testCreateHashtable() {
		try {
			createHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetHashtable() {
		try {
			createHashtable(DataClass.class);
			TimeoutHashtableFactory.getTimeoutHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testGetHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullHashtable() {
		try {
			TimeoutHashtableFactory.getTimeoutHashtable(String.class);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testGetNullHashtable1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			TimeoutHashtableFactory.getTimeoutHashtable(null);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testGetNullHashtable2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveHashtables() {
		try {
			createHashtable(DataClass.class);
			removeHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testRemoveHashtables:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveStringHashtables() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
			removeStringHashtable(this.getClass().getClassLoader());
			removeStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testRemoveStringHashtables:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveNullHashtables() {
		try {
			removeStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testRemoveNullHashtables1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			removeHashtable(String.class);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testRemoveNullHashtables2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullHashtable() {
		try {
			createHashtable(null);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateNullHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateHashtableWithTresHold() {
		try {
			createTrHashtable(DataClass.class, 5);
			createTrHashtable(DataClass.class, 6);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateHashtableWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullHashtableWithTresHold() {
		try {
			createTrHashtable(null, 5);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateNullHashtableWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringHashtable() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateStringHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetStringHashtable() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
			TimeoutHashtableFactory.getTimeoutStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testGetStringHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullStringHashtable() {
		try {
			TimeoutHashtableFactory.getTimeoutStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testGetNullStringHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringHashtable() {
		try {
			createStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateNullStringHashtable:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringHashtableWithTresHold() {
		try {
			createStringTrHashtable(this.getClass().getClassLoader(), 5);
			createStringTrHashtable(this.getClass().getClassLoader(), 6);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateStringHashtableWithTresHold:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringHashtableWithTresHold() {
		try {
			createStringTrHashtable(null, 5);
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testCreateNullStringHashtableWithTresHold:Exception : "
					+ e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testEditHashtable() {
		com.sap.engine.session.data.share.Hashtable hash = null;

		try {
			hash = createHashtable(DataClass.class);

		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable1:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			hash.put("S1", new DataClass());
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable2:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.put("S2", new DataClass());
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable3:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			hash.remove("S2");
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable4:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.size();
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable5:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.keys();
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable6:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.values();
		} catch (Throwable e) {
			System.out.println("TimeoutHashtableTest : testEditHashtable7:Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	public static long timeout = 0;

	private com.sap.engine.session.data.share.Hashtable createHashtable(Class cl) throws NullClassLoaderException,
			TooManyHashtablesException {
		return TimeoutHashtableFactory.createTimeoutHashtable(cl, timeout);
	}

	private void removeHashtable(Class cl) throws NullClassLoaderException, NoSuchHashtableException {
		TimeoutHashtableFactory.removeTimeoutHashtable(cl);
	}

	private void removeStringHashtable(ClassLoader cl) throws NullClassLoaderException, NoSuchHashtableException {
		TimeoutHashtableFactory.removeTimeoutStringHashtable(cl);
	}

	private com.sap.engine.session.data.share.Hashtable createTrHashtable(Class cl, int tr)
			throws NullClassLoaderException, TooManyHashtablesException {
		return TimeoutHashtableFactory.createTimeoutHashtable(cl, timeout, tr);
	}

	private com.sap.engine.session.data.share.Hashtable createStringHashtable(ClassLoader cl)
			throws NullClassLoaderException, TooManyHashtablesException {
		return TimeoutHashtableFactory.createTimeoutStringHashtable(cl, timeout);
	}

	private com.sap.engine.session.data.share.Hashtable createStringTrHashtable(ClassLoader cl, int tr)
			throws NullClassLoaderException, TooManyHashtablesException {
		return TimeoutHashtableFactory.createTimeoutStringHashtable(cl, timeout, tr);
	}

}
