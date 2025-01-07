/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic;

import java.io.*;

/**
 * This class is used to parse errors of external compiler in a text file,
 * with sources where these errors are occured.
 */

/**
 * @author Peter Petrov, Mladen Droshev
 * @version 7.0
 */
class ErrorParser {//$JL-LOG_CONFIG$    //$JL-SYS_OUT_ERR$

  private String errorFileName = "RMICCompileErrors.log";
  private String path = ".";

  public ErrorParser(String errorFileName, String path){
    this.errorFileName = errorFileName;
  }
  //TODO check it!!!!!!   change it!!!!!
  private void writeErrors(String message) throws IOException {
    File f;
    File dir = new File("log");

    if (dir.isDirectory()) {
      f = new File("log", "Errors.txt");
    } else {
      dir.mkdirs();
      f = new File("log", "Errors.txt");
    }

    FileWriter fout = new FileWriter(f);
    fout.write(message);
    fout.close();
  }

  /**
   * Parse errors that are recieved as stream. If debug flag is set, print errors to the screen.
   *
   * @param errorsStream stream of errors, thrown from java compiler
   * @param print        flag
   */
  public void parse(InputStream errorsStream, boolean print) throws IOException {
    String buf;
    String fileName;
    String fileNameBuf = "";
    String errorsMessage = "";
    File f = new File(path, errorFileName);
    FileOutputStream fout = new FileOutputStream(f);
    int c;

    while ((c = errorsStream.read()) != -1) {
      if (print) {
        System.out.print((char) c);
      }
      fout.write(c);
    }

    fout.close();
    errorsStream.close();
    RandomAccessFile raf = new RandomAccessFile(f, "r");
    int pos;
    raf.seek(0);

    while ((buf = raf.readLine()) != null) {
      if ((pos = buf.indexOf(":")) != -1) {
        fileName = buf.substring(0, pos);

        if (fileName.indexOf(".java") == -1) {
          continue;
          //break; //if we don't want notes.
        }

        if (!fileName.equals(fileNameBuf)) {
          //print source of file 
          fileNameBuf = fileName;
          RandomAccessFile rafs = new RandomAccessFile(fileName, "r");
          errorsMessage += "\r\n";
          errorsMessage += "\r\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
          errorsMessage += "\r\nSource of file: " + fileName;
          errorsMessage += "\r\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
          String sbuf;

          while ((sbuf = rafs.readLine()) != null) {
            errorsMessage += "\r\n" + sbuf;
          }

          errorsMessage += "\r\n*****************************************************************************";
          errorsMessage += "\r\nErrors of file: " + fileName;
          errorsMessage += "\r\n*****************************************************************************";
        }
      }

      errorsMessage += "\r\n" + buf;

      while ((buf = raf.readLine()) != null) {
        errorsMessage += "\r\n" + buf;

        if (buf.indexOf("^") != -1) {
          break;
        }
      }
    }

    raf.close();
    writeErrors(errorsMessage);
  }

}

