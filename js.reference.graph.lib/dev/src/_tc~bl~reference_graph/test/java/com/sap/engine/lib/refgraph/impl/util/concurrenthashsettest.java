/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph.impl.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;
import org.junit.Test;

import com.sap.engine.lib.refgraph.impl.util.ConcurrentHashSet;

/**
 * @author Assia Djambazova
 */
public class ConcurrentHashSetTest {
	
	@Test
	public void testConcurrentHashSet() {
		
		ConcurrentHashSet<String> cString = new ConcurrentHashSet<String>();
		assertTrue(cString.isEmpty());
			
		ConcurrentHashSet<Integer> cHSString = new ConcurrentHashSet<Integer>(5);
		for (int i=0;i<10;i++){
			cHSString.add(i);
		}
		assertTrue(cHSString.size()==10);
		
		Collection l = new LinkedList<Integer>();
		for (int i=0;i<5;i++){
			l.add(i);
		}
		l.add(2);
		
		ConcurrentHashSet<Integer> cInt = new ConcurrentHashSet<Integer>(l);
		assertFalse(cInt.isEmpty());
		assertTrue(cInt.size()==5);
		assertTrue(cInt.contains(4));
		assertFalse(cInt.add(4));
		assertTrue(cInt.remove(4));
		assertFalse(cInt.contains(4));
		assertTrue(cInt.add(4));
		
		cInt.clear();
		assertTrue(cInt.isEmpty());
	
	}
	
		
	@Test (expected= NullPointerException.class) public void testRemove(){
		ConcurrentHashSet<String> c = new ConcurrentHashSet<String>();
		c.remove(null);
	}

}
