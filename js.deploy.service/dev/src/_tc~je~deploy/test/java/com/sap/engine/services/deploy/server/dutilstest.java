package com.sap.engine.services.deploy.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;


public class DUtilsTest {

	@SuppressWarnings("boxing")
	@Test
	public void testRemoveElement() {
		final int[] set = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		// Remove first element
		int[] result = DUtils.removeElement(set, 0);
		assertEquals(9, result.length);
		for(int i = 0; i < result.length; i++) {
			assertEquals(i + 1, result[i]);
		}
		
		// Remove last element
		result = DUtils.removeElement(set, 9);
		assertEquals(9, result.length);
		for(int i = 0; i < result.length; i++) {
			assertEquals(i, result[i]);
		}
		
		// Remove element in the middle
		result = DUtils.removeElement(set, 5);
		assertEquals(9, result.length);
		for(int i = 0; i < 5; i++) {
			assertEquals(i, result[i]);
		}
		for(int i = 5; i < result.length; i++) {
			assertEquals(i + 1, result[i]);
		}
		
		// Remove unexisting element
		result = DUtils.removeElement(set, 10);
		assertSame(set, result);
	}
}
