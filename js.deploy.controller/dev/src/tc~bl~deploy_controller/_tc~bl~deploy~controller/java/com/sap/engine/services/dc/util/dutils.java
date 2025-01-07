/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.dc.util;

import java.util.ArrayList;

public class DUtils {

	/**
	 * Concatenates two int arrays and returns the resultant array.
	 * 
	 * @param s1
	 *            int array.
	 * @param s2
	 *            int array.
	 * @return the resultant int array.
	 */
	public static int[] concatArrays(int[] s1, int[] s2) {
		if (s1 == null) {
			return s2;
		}

		if (s2 == null) {
			return s1;
		}

		if (s1 == null && s2 == null) {
			return null;
		}
		int[] temp = new int[s1.length + s2.length];
		int counter = 0;
		boolean found = false;
		for (int i = 0; i < s1.length; i++) {
			found = false;
			for (int j = 0; j < counter; j++) {
				if (s1[i] == temp[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				temp[counter++] = s1[i];
			}
		}
		for (int i = 0; i < s2.length; i++) {
			found = false;
			for (int j = 0; j < counter; j++) {
				if (s2[i] == temp[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				temp[counter++] = s2[i];
			}
		}
		int[] res = new int[counter];
		System.arraycopy(temp, 0, res, 0, counter);
		return res;
	}

	public static int[] removeElements(int[] from, int[] remove) {
		if (remove == null || remove.length == 0 || from == null
				|| from.length == 0) {
			return from;
		}
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < from.length; i++) {
			if (findElement(from[i], remove) == -1) {
				res.add(new Integer(from[i]));
			}
		}
		int result[] = new int[res.size()];
		for (int i = 0; i < res.size(); i++) {
			result[i] = ((Integer) res.get(i)).intValue();
		}
		return result;
	}

	public static int findElement(int target, int source[]) {
		if (source != null) {
			for (int i = 0; i < source.length; i++) {
				if (source[i] == target) {
					return i;
				}
			}
		}
		return -1;
	}

}
