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

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.boot.SystemProperties;

/**
 * Server's shell command-line utilities
 *
 * @version 6.30 Oct 2002
 * @author Hristo S. Iliev
 */
public class ServCLUtils {

  /**
   * Stores DirContext
   */
  public static DirContext ctx = null;

  /**
   * Stores the relative path (initially - the root)
   */
  public static String relativePath = new String("/");

  /**
   * Stores the attributes
   */
  public static BasicAttributes attributes = new BasicAttributes();

  /**
   * Stores the new line separator
   */
  public static String newLineSeparator = SystemProperties.getProperty("line.separator");

  /**
   * Counts number of directories
   *
   * @param s Path to count
   * @return Number of directories
   */
  public int countDirNum(String s) {
    int i = 0;
    int indx = 0;

    while (indx != -1) {
      indx = s.indexOf("/", indx + 1);
      i++;
    }

    return i;
  }

  /**
   * Determines if a context is found
   *
   * @param nm Name of the clss to check
   * @return true if it's a context, false - elsewhere
   */
  public boolean isContext(String nm) {
    if ((nm.indexOf("javax.naming.Context") != -1) || (nm.indexOf("javax.naming.directory.DirContext") != -1)) {
      return true;
    }

    return false;
  }

  /**
   * Checks if relative or absolute path was requested and modifies it accordingly
   *
   * @param loc The path to check
   * @return The modified path
   */
  public String modifyPath(String loc) throws NamingException {
    String retStr = new String(loc);

    if (!loc.startsWith("/")) {
      //Check if the path ends with '/'
      if (loc.endsWith("/")) {
        retStr = relativePath.concat(loc);
      } else {
        //if there is no relative path - leave the location as is
        if ((relativePath.length() != 0) && (!relativePath.equals("/"))) {
          retStr = relativePath.concat("/").concat(loc);
        }
      }
    }

    //Update the path, removing the ".."s
    if (retStr.indexOf("..") != -1) {
      //Extract valid tokens
      StringTokenizer strTknzr = new StringTokenizer(retStr, "/", false);
      ArrayObject arr = new ArrayObject(strTknzr.countTokens());
      int num = 0;

      while (strTknzr.hasMoreTokens()) {
        String token = strTknzr.nextToken();

        if (token.equals("..")) {
          num--;

          if (num < 0) {
            throw new javax.naming.NamingException("Incorrect path.");
          }
        } else {
          arr.add(num, token);
          num++;
        }
      }

      //Construct the path
      retStr = new String();

      for (int i = 0; i < num; i++) {
        retStr = retStr.concat("/").concat((String) arr.elementAt(i));
      }
    }

    //Put starting "/" if needed
    if (!retStr.startsWith("/")) {
      retStr = "/".concat(retStr);
    }

    return retStr;
  }

}

