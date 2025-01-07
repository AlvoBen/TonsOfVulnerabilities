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
package com.sap.engine.lib.refgraph;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import org.junit.Test;

import com.sap.engine.lib.refgraph.Edge.Type;
/**
 * @author Assia Djambazova
 */

public class EdgeTest {
	/**
	 * The test examines whether the <code>equals</code> and <code>hashCode</code>
	 * methods work properly by creating several <code>Edge</code> objects, comparing
	 * them and using them as keys in a <code>HashMap</code>.
	 */
	@SuppressWarnings("unchecked")
	@Test
//TODO test constructors passing null arguments for exceptions 
	public void testEqualsHashCode() {
		Edge<String>[] edges = new Edge[8];
		edges[0] = new Edge<String>("A", "B", Edge.Type.WEAK, "String");
		edges[1] = new Edge<String>("A", "C", Edge.Type.WEAK, null);
		edges[2] = new Edge<String>("A", "B", Edge.Type.HARD, null);
		edges[3] = new Edge<String>("A", "C", Edge.Type.HARD, "String");
		edges[4] = new Edge<String>("B", "C", Edge.Type.WEAK, null);
		edges[5] = new Edge<String>("B", "D", Edge.Type.WEAK, null);
		edges[6]= new Edge<String>("B", "C", Edge.Type.WEAK, "String");
		edges[7]= new Edge<String>("B", "D", Edge.Type.WEAK, "String");
		
		Map<Edge<String>, Integer> map = new HashMap<Edge<String>, Integer>();
		
		for (int i = 0; i < edges.length; i++){
			map.put(edges[i], i);
		}
		
		for (int i=0;i<edges.length;i++){
			for (int j=0;j<edges.length;j++){
				if (i==j){
					assertTrue(edges[i].equals(edges[j]));
					assertTrue(edges[i].hashCode()==edges[j].hashCode());
					assertTrue(map.get(edges[i]).equals(map.get(edges[j])));
				} else {
					assertFalse(edges[i].equals(edges[j]));
					assertFalse(edges[i].hashCode()==edges[j].hashCode());
					assertFalse(map.get(edges[i]).equals(map.get(edges[j])));
				}
			}
		}
						
		map.put(edges[2], 12);
		map.put(edges[5], 55);
		
		assertTrue(map.get(edges[2]).equals(12));
		assertTrue(map.get(edges[5]).equals(55));
		assertFalse(map.get(edges[5]).equals(5));
		
		Integer i = new Integer(5);
		assertFalse(edges[4].equals(i));
		assertFalse(edges[0].equals(null));
		
		Enum.valueOf(Type.class, "HARD");
		Type.values();
	}
	
	/**
	 * Creates an <code>Edge</code> object using the four argument constructor and determines 
	 * whether the get methods return relevant values.
	 */
	@Test
	public void testEdgeInit() {
		Edge<String> e = new Edge<String>("from", "to", Edge.Type.HARD, "dummy String");
		assertEquals("from", e.getFirst());
		assertEquals("to", e.getSecond());
		assertEquals(Edge.Type.HARD, e.getType());
		assertEquals("dummy String", e.getNestedObject());
		
		assertNotSame("to", e.getFirst());
		assertNotSame("something", e.getSecond());
		
	}
}
