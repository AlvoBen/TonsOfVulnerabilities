package com.sap.engine.services.deploy.server.dpl_info.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.sap.engine.services.deploy.server.dpl_info.module.Resource.AccessType;

/**
 * @author Assia Djambazova
 */

public class ResourceTest {

	/**
	 * The test examines whether the <code>equals</code> and <code>hashCode</code>
	 * methods work properly by creating several <code>Resource</code> objects, comparing
	 * them and using them as keys in a <code>HashMap</code>.
	 */
	@SuppressWarnings("boxing")
	@Test
	public void testEqualsHashCode() {
		Resource[] resources = new Resource[4];
		resources[0] = new Resource("name_one", "type_one", Resource.AccessType.PRIVATE);
		resources[1] = new Resource("name_one", "type_two", Resource.AccessType.PRIVATE);
		resources[2] = new Resource("name_two", "type_two", Resource.AccessType.PUBLIC);
		resources[3] = new Resource("name_two", "type_one", Resource.AccessType.PRIVATE);
		
		Resource res_one = new Resource("name_one", "type_one");
		assertTrue(res_one.equals(resources[0]));
		
		for (int i=0; i<resources.length; i++){
			for (int j=0; j<resources.length; j++){
				if (i==j){
					assertTrue(resources[i].equals(resources[j]));
					assertEquals(resources[i].hashCode(), resources[j].hashCode());
				} else {
					assertFalse(resources[i].equals(resources[j]));
					assertFalse(resources[i].hashCode() == resources[j].hashCode());
				}
			}
		}
		
		assertFalse(resources[0].equals(null));
		
		
		HashMap<Resource, Integer> map = new HashMap<Resource, Integer>();
		for (int i=0; i<resources.length;i++){
			map.put(resources[i], i);
		}
		
		assertTrue(map.containsKey(resources[2]));
		map.put(resources[2], 2);
		assertFalse(map.size()==5);
		
		Enum.valueOf(AccessType.class, "PUBLIC");
		AccessType.values();
	}

	/**
	 * Creates <code>Resource</code> objects using different constructors and determines whether the get and
	 * set methods work properly.
	 */
	@Test
	public void testGetters() {
		Resource res = new Resource("testName", "testType", Resource.AccessType.PUBLIC);
		
		assertEquals(res.getName(), "testName");
		assertEquals(res.getType(), "testType");
		assertEquals(res.getAccessType(), Resource.AccessType.PUBLIC);
		assertNotSame(res.getName(), "newName");
		
		Resource res_two = new Resource("testName", "testType");
		assertEquals(res_two.getAccessType(), Resource.AccessType.PUBLIC);
	}

	/**
	 * Following methods test if exception is thrown by the constructor 
	 * when attempt to initialize the fields with null values is made.
	 */
	@Test(expected = IllegalArgumentException.class) 
	public void testResource(){
		new Resource(null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testResourceAgain(){
		new Resource("name", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testResourceOnceAgain(){
		new Resource ("name", "type", null);
	}
	
	/**
	 * Tests the <code>toString</code> method. 
	 */
	@Test
	public void testToString() {
		Resource res = new Resource("name", "type", Resource.AccessType.PRIVATE);
		String toString = "type/name (private)";
		assertEquals(toString, res.toString());
	}
}
