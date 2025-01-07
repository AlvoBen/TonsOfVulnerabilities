/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
/**
 * @author diyan-y
 * @version 7.10
 */
public class PortletOutputStream extends ServletOutputStream {

  /**
   * The PrintWriter that is wrapped on top of the base input stream
   */
  PrintWriter wrappedPrintWriter = null;

  /**
   * Construct a ServletOutputStream that coordinates output using a base
   * ServletOutputStream and a PrintWriter that is wrapped on top of that
   * OutputStream.
   * @param printWriter the PrintWriter to be wrapped. 
   */
  public PortletOutputStream(PrintWriter printWriter) {
    super();
    wrappedPrintWriter = printWriter;
  }

  /**
   * Writes an array of bytes.
   * @param pBuf the array to be written.
   * @exception IOException if an I/O error occurred.
   */
  public void write(byte[] pBuf) throws IOException {
    char[] cbuf = new char[pBuf.length];
    for (int i = 0; i < cbuf.length; i++) {
      cbuf[i] = (char)(pBuf[i] & 0xff);
    }
    wrappedPrintWriter.write(cbuf, 0, pBuf.length);
  }

  /**
   * Writes a single byte to the output stream.
   */
  public void write(int pVal) throws IOException {
    wrappedPrintWriter.write(pVal);
  }

  /**
   * Writes a subarray of bytes.
   * @param pBuf the array to be written.
   * @param pOffset the offset into the array.
   * @param pLength the number of bytes to write.
   * @exception IOException if an I/O error occurred.
   */
  public void write(byte[] pBuf, int pOffset, int pLength) throws IOException {
    char[] cbuf = new char[pLength];
    for (int i = 0; i < pLength; i++) {
      cbuf[i] = (char)(pBuf[i + pOffset] & 0xff);
    }
    wrappedPrintWriter.write(cbuf, 0, pLength);
  }

  /**
   * Flushes the stream, writing any buffered output bytes.
   * @exception IOException if an I/O error occurred.
   */
  public void flush() throws IOException {
    wrappedPrintWriter.flush();
  }

  /**
   * Closes the stream.
   * @exception IOException if an I/O error occurred.
   */
  public void close() throws IOException {
    wrappedPrintWriter.close();
  }

  /**
   * Prints a string.
   * @param pVal the String to be printed.
   * @exception IOException if an I/O error has occurred.
   */
  public void print(String pVal) throws IOException {
    wrappedPrintWriter.print(pVal);
  }

  /**
   * Prints an string followed by a CRLF.
   * @param pVal the String to be printed.
   * @exception IOException if an I/O error has occurred.
   */
  public void println(String pVal) throws IOException {
    wrappedPrintWriter.println(pVal);
  }

  /**
   * Prints a CRLF.
   * @exception IOException if an I/O error has occurred.
   */
  public void println() throws IOException {
    wrappedPrintWriter.println();
  }

}
