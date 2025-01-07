package com.sap.engine.session.data;

import org.junit.Test;

import com.sap.engine.session.data.share.HashtableFactory;
import com.sap.engine.session.data.share.exceptions.NoSuchHashtableException;
import com.sap.engine.session.data.share.exceptions.NullClassLoaderException;
import com.sap.engine.session.data.share.exceptions.TooManyHashtablesException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class HashtableTest {

	static Location l1 = Location.getLocation(com.sap.engine.session.data.share.Hashtable.class);
	static Location l2 = Location.getLocation(com.sap.engine.session.data.share.HashtableFactory.class);
	static Location l3 = Location.getLocation(com.sap.engine.session.data.share.HashtableImpl.class);

	static {

		l1.setEffectiveSeverity(Severity.ALL);
		l2.setEffectiveSeverity(Severity.ALL);
		l3.setEffectiveSeverity(Severity.ALL);

	}

	@Test
	public void testCreateFactory() {
		new HashtableFactory();
		HashtableFactory.getHashtablesThreshold();
	}

	@Test
	public void testCreateHashtable() {
		try {
			createHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest : testCreateHashtable : Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetHashtable() {
		try {
			createHashtable(DataClass.class);
			HashtableFactory.getHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testGetHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullHashtable() {
		try {
			HashtableFactory.getHashtable(String.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testGetNullHashtable1: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			HashtableFactory.getHashtable(null);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testGetNullHashtable2: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveHashtables() {
		try {
			createHashtable(DataClass.class);
			removeHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testRemoveHashtables: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveStringHashtables() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
			removeStringHashtable(this.getClass().getClassLoader());
			removeStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("HashtableTest: testRemoveStringHashtables: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testRemoveNullHashtables() {
		try {
			removeStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testRemoveNullHashtables1: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			removeHashtable(String.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testRemoveNullHashtables2: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullHashtable() {
		try {
			createHashtable(null);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateNullHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateHashtableWithTresHold() {
		try {
			createTrHashtable(DataClass.class, 5);
			createTrHashtable(DataClass.class, 6);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateHashtableWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullHashtableWithTresHold() {
		try {
			createTrHashtable(null, 5);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateNullHashtableWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringHashtable() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateStringHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetStringHashtable() {
		try {
			createStringHashtable(this.getClass().getClassLoader());
			HashtableFactory.getStringHashtable(this.getClass().getClassLoader());
		} catch (Throwable e) {
			System.out.println("HashtableTest: testGetStringHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testGetNullStringHashtable() {
		try {
			HashtableFactory.getStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testGetNullStringHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringHashtable() {
		try {
			createStringHashtable(null);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateNullStringHashtable: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateStringHashtableWithTresHold() {
		try {
			createStringTrHashtable(this.getClass().getClassLoader(), 5);
			createStringTrHashtable(this.getClass().getClassLoader(), 6);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateStringHashtableWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testCreateNullStringHashtableWithTresHold() {
		try {
			createStringTrHashtable(null, 5);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testCreateNullStringHashtableWithTresHold: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	@Test
	public void testEditHashtable() {
		com.sap.engine.session.data.share.Hashtable hash = null;
		try {
			hash = createHashtable(DataClass.class);
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable1: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.put("S1", new DataClass());

		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable2: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.put("S2", new DataClass());
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable3: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}

		try {
			hash.remove("S2");
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable4: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.size();
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable5: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.keys();
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable6: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
		try {
			hash.values();
		} catch (Throwable e) {
			System.out.println("HashtableTest: testEditHashtable7: Exception : " + e.getMessage()); // $JL-SYS_OUT_ERR$
		}
	}

	private com.sap.engine.session.data.share.Hashtable createHashtable(Class cl) throws NullClassLoaderException,
			TooManyHashtablesException {
		return HashtableFactory.createHashtable(cl);
	}

	private void removeHashtable(Class cl) throws NullClassLoaderException, NoSuchHashtableException {
		HashtableFactory.removeHashtable(cl);
	}

	private void removeStringHashtable(ClassLoader cl) throws NullClassLoaderException, NoSuchHashtableException {
		HashtableFactory.removeStringHashtable(cl);
	}

	private com.sap.engine.session.data.share.Hashtable createTrHashtable(Class cl, int tr)
			throws NullClassLoaderException, TooManyHashtablesException {
		return HashtableFactory.createHashtable(cl, tr);
	}

	private com.sap.engine.session.data.share.Hashtable createStringHashtable(ClassLoader cl)
			throws NullClassLoaderException, TooManyHashtablesException {
		return HashtableFactory.createStringHashtable(cl);
	}

	private com.sap.engine.session.data.share.Hashtable createStringTrHashtable(ClassLoader cl, int tr)
			throws NullClassLoaderException, TooManyHashtablesException {
		return HashtableFactory.createStringHashtable(cl, tr);
	}
	//
	// public void testHashtableImpl() {
	// HashtableImpl hash = new HashtableImpl(HashtableTest.class.getName(), 10,
	// 0);
	// try {
	// hash.put("Test1", this);
	// hash.size();
	// hash.get("Test1");
	// hash.keys();
	// hash.values();
	// hash.remove("Test1");
	// } catch (Throwable e) {
	// System.out.println("HashtableTest: testHashtableImpl1: Exception : " +
	// e.getMessage()); //$JL-SYS_OUT_ERR$
	// }
	//
	// try {
	// hash.put("Test2", DataClass.class);
	// } catch (Throwable e) {
	// System.out.println("HashtableTest: testHashtableImpl2: Exception : " +
	// e.getMessage()); //$JL-SYS_OUT_ERR$
	// }
	// }

}
