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
package com.sap.engine.services.jndi.shellcmd;

/**
 * LRO test Object
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public class MyLRObject {

  int number;

  //  public com.inqmy.services.ServiceReference c2r = new com.inqmy.server.ServiceReferenceImpl("sdfbkjnfsgbjfg", true);
  /**
   * Constructor
   *
   * @param i Number of the object, also used as data
   */
  public MyLRObject(int i) {
    this.number = i;
  }

  /**
   * Debug method - prints the data
   */
  public void print() {
    System.out.println(this.number); //$JL-SYS_OUT_ERR$
  }

  /**
   * Converts the data to string
   *
   * @return String representation of the data
   */
  public String toString() {
    Integer a = new Integer(number);
    return a.toString();
  }
 

}

