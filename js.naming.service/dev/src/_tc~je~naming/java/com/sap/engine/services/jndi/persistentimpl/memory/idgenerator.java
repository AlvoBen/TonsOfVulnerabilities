/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistentimpl.memory;

public class IDGenerator {

	private int initialSize = 1000;
	private int[] initialIDs = null;
	private int counter = 1000;
	private int numberOfElementsInArray = 1000;
	private int numberOfElementsInExtendedArray = 1000;

	public IDGenerator(int initialSize, int counterValue, int elementsInArray) {
		this.initialSize = initialSize;
		counter = counterValue;
		numberOfElementsInExtendedArray = counterValue;
		initialIDs = new int[initialSize];
		numberOfElementsInArray = elementsInArray;
		initArray();
	}

	private void initArray() {
		for (int i = 0; i < numberOfElementsInArray; i++) {
			initialIDs[i] = i;
		}
	}

	public synchronized int getID() {
		if (numberOfElementsInArray != 0) {
			numberOfElementsInArray = numberOfElementsInArray - 1;
			return initialIDs[numberOfElementsInArray];
		} else {
			if (counter == numberOfElementsInExtendedArray) {
				numberOfElementsInExtendedArray = counter + 500;
				initialIDs = new int[numberOfElementsInExtendedArray];
			}
			return counter++;
		}
	}

	public synchronized void releaseID(int id) {
		if (numberOfElementsInArray < counter) {
			initialIDs[numberOfElementsInArray] = id;
			numberOfElementsInArray = numberOfElementsInArray + 1;
		}
	}
	
}
