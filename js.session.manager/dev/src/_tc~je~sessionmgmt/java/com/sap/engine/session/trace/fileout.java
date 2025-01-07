/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.trace;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Author: georgi-s
 * Date: Apr 7, 2004
 */
public class FileOut {
  public static PrintStream out = System.out;
  static {
    try {
      out = new PrintStream(new FileOutputStream(".\\system.out"));
      out.println("LOG FILE:" + (new Date(System.currentTimeMillis())));
    } catch (FileNotFoundException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

//  public static void main(String[] args) {
//    while (true)  {
//      byte[]a = new byte[100];
//      try {
//        Thread.sleep(200);
//      } catch (InterruptedException e) {
//        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//      }
//    }
//  }
}
