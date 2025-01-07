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
package com.sap.engine.services.httpserver.lib.headers;

/*
 *
 * @author Galin Galchev
 * @version 4.0
 */
import com.sap.engine.lib.text.FastDateFormat;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaderField;
import com.sap.engine.services.httpserver.lib.headers.MimeHeadersEnumerator;
import com.sap.engine.services.httpserver.lib.exceptions.HttpIOException;

import java.io.*;
import java.util.*;

public class MimeHeaders implements Cloneable {
  private static final Locale loc = Locale.US;
  private static final TimeZone zone = TimeZone.getTimeZone("GMT");
  private final FastDateFormat rfc1123Format = new FastDateFormat("EEE, dd MMM yyyy HH:mm:ss z", zone, loc, false);
  private final FastDateFormat rfc1123FormatLM = new FastDateFormat("EEE, dd MMM yyyy HH:mm:ss z", zone, loc, false);
  private static final byte SP = 32;
  private static final byte HT = 9;
  private MimeHeaderField headers[];
  private int count = 0;
  private static final int DEFAUL_BUFFER_LENGTH = 512;

  /**
   * Default constructor
   *
   */
  public MimeHeaders() {
    headers = new MimeHeaderField[7];
    for (int i = 0; i < headers.length; i++) {
      headers[i] = new MimeHeaderField(rfc1123Format, rfc1123FormatLM);
    }
  }

  public Object clone() {
    MimeHeaders newmMimeHeaders = new MimeHeaders();
    if (headers.length > newmMimeHeaders.headers.length) {
      newmMimeHeaders.headers = new MimeHeaderField[headers.length];
      for (int i = 0; i < headers.length; i++) {
        newmMimeHeaders.headers[i] = new MimeHeaderField(rfc1123Format, rfc1123FormatLM);
      }
    }
    for (int i = 0; i < headers.length; i++) {
      if (headers[i] != null) {
        newmMimeHeaders.headers[i].init(headers[i]);
      }
    }
    return newmMimeHeaders;
  }

  public void getDate(byte[] inByteArr, int off) {
    rfc1123Format.getDate(inByteArr, off);
  }

  public void getDate(byte[] inByteArr, int off, long l) {
    rfc1123Format.getDate(inByteArr, off, l);
  }

  public int getDateLength() {
    return rfc1123Format.getLength();
  }

  /**
   * Clears all headers
   *
   */
  public void clear() {
    for (int i = 0; i < count; i++) {
      headers[i].reset();
    }

    count = 0;
  }

  /**
   * Returns count of headers
   *
   * @return     count of headers
   */
  public int size() {
    return count;
  }

  /**
   * Returns enumeration of headers
   *
   * @return     enumeration of headers
   */
  public Enumeration names() {
    return new MimeHeadersEnumerator(this);
  }

  /**
   * Puts a header.
   * A previous header is replaced by the new header.
   * Where a set of header values exist for the name, 
   * the values are cleared and replaced with the new value.
   *
   * @param   s  header name
   * @param   s1  header value
   */
  public void putHeader(byte[] s, byte[] s1) {
    putHeader(s).setValue(s1, 0, s1.length);
    removeLastHeaders(s);
  }

  /**
   * Adds a header.
   * Adds a header value to the set with a given name. If there 
   * are no headers already associated with the name, a new set 
   * is created.
   *
   * @param   s  header name
   * @param   s1  header value
   */
  public void addHeader(byte[] s, byte[] s1) {
    MimeHeaderField mimeheaderfield = putHeader();
    mimeheaderfield.setName(s, 0, s.length);
    mimeheaderfield.setValue(s1, 0, s1.length);
  }

  /**
   * Puts int header
   *
   * @param   s  header name
   * @param   i  header int value
   */
  public void putIntHeader(byte[] s, int i) {
    putHeader(s).setIntValue(i);
    removeLastHeaders(s);
  }

  /**
   * Adds int header
   *
   * @param   s  header name
   * @param   i  header int value
   */
  public void addIntHeader(byte[] s, int i) {
    MimeHeaderField mimeheaderfield = putHeader();
    mimeheaderfield.setName(s, 0, s.length);
    mimeheaderfield.setIntValue(i);
  }

  /**
   * Puts date header
   *
   * @param   s  header name
   * @param   l  header date value
   */
  public void putDateHeader(byte[] s, long l) {
    putHeader(s).setDateValue(l);
    removeLastHeaders(s);
  }

  /**
   * Adds date header
   *
   * @param   s  header name
   * @param   l  header date value
   */
  public void addDateHeader(byte[] s, long l) {
    MimeHeaderField mimeheaderfield = putHeader();
    mimeheaderfield.setName(s, 0, s.length);
    mimeheaderfield.setDateValue(l);
  }

  /**
   * Puts last modified date header
   *
   * @param   s  header name
   * @param   l  header date value
   */
  public void putDateHeaderLM(byte[] s, long l) {
    putHeader(s).setDateValueLM(l);
    removeLastHeaders(s);
  }

  /**
   * Puts date header the date is current time
   *
   * @param   s  header name
   */
  public void putDateHeader(byte[] s) {
    putHeader(s).setDateValue();
    removeLastHeaders(s);
  }

  /**
   * Returns header value
   *
   * @param   s  header name
   * @return     header value
   */
  public String getHeader(String s) {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getValue();
    } else {
      return null;
    }
  }

  /**
   * Returns header value
   *
   * @param   s  header name
   * @return     header value
   */
  public byte[] getHeader(byte[] s) {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getByteValue();
    } else {
      return null;
    }
  }

  /**
   * Returns header values
   *
   * @param   s  header name
   * @return     array of header values
   */
  public String[] getHeaders(String s) {
    Vector<String> vector = new Vector<String>();
    String as[] = null;

    for (int i = 0; i < count; i++) {
      if (headers[i].nameEquals(s)) {
        vector.addElement(headers[i].getValue());
      }
    }

    if (vector.size() == 1) {
      return new String[] {vector.elementAt(0)};
    } else if (vector.size() > 1) {
      as = new String[vector.size()];
      as = vector.toArray(as);
      //      for(int j = 0; j < as.length; j++) {
      //        as[j] = (String)vector.elementAt(j);
      //      }
    } else {
      as = new String[0];
    }

    return as;
  }

  /**
   * Returns header int value
   *
   * @param   s  header name
   * @return     int header value
   * @exception   java.lang.NumberFormatException
   */
  public int getIntHeader(String s) throws NumberFormatException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getIntValue();
    } else {
      return -1;
    }
  }

  /**
   * Returns header int value
   *
   * @param   s  header name
   * @return     int header value
   * @exception   java.lang.NumberFormatException
   */
  public int getIntHeader(byte[] s) throws NumberFormatException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getIntValue();
    } else {
      return -1;
    }
  }

  /**
   * Returns header long value
   *
   * @param   s  header name
   * @return     lonh header value
   * @exception   java.lang.NumberFormatException
   */
  public long getLongHeader(String s) throws NumberFormatException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getLongValue();
    } else {
      return -1;
    }
  }

  /**
   * Returns header long value
   *
   * @param   s  header name
   * @return     long header value
   * @exception   java.lang.NumberFormatException
   */
  public long getLongHeader(byte[] s) throws NumberFormatException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getLongValue();
    } else {
      return -1;
    }
  }

  /**
   * Returns header date value
   *
   * @param   s  header name
   * @return     date value
   * @exception   java.lang.IllegalArgumentException
   */
  public long getDateHeader(String s) throws IllegalArgumentException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getDateValue();
    } else {
      return -1L;
    }
  }

  /**
   * Returns header date value
   *
   * @param   s  header name
   * @return     date value
   * @exception   java.lang.IllegalArgumentException
   */
  public long getDateHeader(byte[] s) throws IllegalArgumentException {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield != null) {
      return mimeheaderfield.getDateValue();
    } else {
      return -1L;
    }
  }

  /**
   * Returns header name
   *
   * @param   i  headern position
   * @return     header name
   */
  public String getHeaderName(int i) {
    if (i >= 0 && i < count) {
      return headers[i].getName();
    } else {
      return null;
    }
  }

  /**
   * Returns header value
   *
   * @param   i  headern position
   * @return     header value
   */
  public String getHeader(int i) {
    if (i >= 0 && i < count) {
      return headers[i].getValue();
    } else {
      return null;
    }
  }

  protected MimeHeaderField find(String s) {
    for (int i = 0; i < count; i++) {
      if (headers[i].nameEquals(s)) {
        return headers[i];
      }
    }

    return null;
  }

  protected MimeHeaderField find(byte[] s) {
    for (int i = 0; i < count; i++) {
      if (headers[i].nameEquals(s, 0, s.length)) {
        return headers[i];
      }
    }

    return null;
  }

  /**
   * Removes header
   *
   * @param   s  header name
   */
  public void removeHeader(String s) {
    for (int i = 0; i < count; i++) {
      if (headers[i].nameEquals(s)) {
        MimeHeaderField mimeheaderfield = headers[i];
        mimeheaderfield.reset();
        headers[i] = headers[--count];
        headers[count] = mimeheaderfield;
        removeHeader(s);
      }
    }
  }

  public void removeHeader(String name, String value) {
    for (int i = 0; i < count; i++) {
      if (headers[i].nameEquals(name) && headers[i].getValue().equals(value)) {
        MimeHeaderField mimeheaderfield = headers[i];
        mimeheaderfield.reset();
        headers[i] = headers[--count];
        headers[count] = mimeheaderfield;
        removeHeader(name, value);
      }
    }
  }
  
  
  /** Removes all the headers with name 's' after the first. */
  public void removeLastHeaders(byte[] s) {
  	int i = 0;
  	while (i < count && !headers[i].nameEquals(s, 0, s.length)) {
  		i++;
  	}
  	if (i < count) {
  		for (int j = count - 1; j > i; j--) {
  			if (headers[j].nameEquals(s, 0, s.length)) {
  				if (j < count - 1) {
  					headers[j] = headers[count - 1];  
  				}
  				count--;
					headers[count] = null;
  			}
  		}
  	}
  }//removeLastHeaders
  

  /**
   * Checks if contains header
   *
   * @param   s  header name
   * @return     true if contains header
   */
  public boolean containsHeader(String s) {
    return find(s) != null;
  }

  /**
   * Read headers from inputstream
   *
   * @param   httpinputstream  inputstream to read
   * @exception   java.io.IOException
   */
  public int init(byte[] httpinputstream, int offset, int length) throws IOException {
    byte[] buf = new byte[DEFAUL_BUFFER_LENGTH];
    int buf_index = 0;
    int firstByteOfTheLine = -1;
    do {
      int buf_index_before_current_line = buf_index;
      int nextLineLength = -1;
      do {
        int free_bytes_in_buffer = buf.length - buf_index;
        if (firstByteOfTheLine != -1) {
          buf[buf_index] = (byte) firstByteOfTheLine;
          buf_index++;
          free_bytes_in_buffer--;
        }
        if (free_bytes_in_buffer > 0) {
          nextLineLength = readLine(httpinputstream, offset, length, buf, buf_index, free_bytes_in_buffer);
          if (nextLineLength == -1) {
            throw new HttpIOException(HttpIOException.CANNOT_READ_LINE_FROM_STREAM);
          }
          offset += nextLineLength;
        }

        buf_index += nextLineLength;

        if (nextLineLength <= 0) {
          break;
        }

        if (buf[buf_index - 1] == 10) {
          if (nextLineLength == 1 || nextLineLength == 2) {
            firstByteOfTheLine = -1;
            break;
          } else {
            firstByteOfTheLine = httpinputstream[offset++];
            if (firstByteOfTheLine == SP || firstByteOfTheLine == HT) {
              nextLineLength += 2;
              buf_index -= 2;
            } else {
              break;
            }
          }
        }

        if (buf.length - buf_index < 2 * DEFAUL_BUFFER_LENGTH) {
          byte[] tempBuffer = new byte[buf.length + DEFAUL_BUFFER_LENGTH];
          System.arraycopy(buf, 0, tempBuffer, 0, buf.length);
          buf = tempBuffer;
        }

        firstByteOfTheLine = -1;
      } while (true);

      if (--buf_index > buf_index_before_current_line && buf[buf_index - 1] == 13) {
        buf_index--;
      }

      if (buf_index != buf_index_before_current_line) {
        putHeader().parse(buf, buf_index_before_current_line, buf_index - buf_index_before_current_line);
      } else {
        return offset;
      }
    } while (true);
  }

  /**
   * Reads a line from inputstream to byte array
   *
   * @param   httpinputstream  inputstream to read
   * @param   b  byte array to store in
   * @param   off  offset
   * @param   len  length
   * @return     bytes readed
   */
  private int readLine(byte[] httpinputstream, int inputOffset, int inputLength, byte[] b, int off, int len) {
    if (len <= 0) {
      return 0;
    }
    int count = 0;
    for (; count < len && count < inputLength; count++) {
      b[off++] = httpinputstream[inputOffset + count];
      if (httpinputstream[inputOffset + count] == '\n') {
        count++;
        break;
      }
    }
    return count > 0 ? count : -1;
  }

  /**
   * Writes header to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @exception   java.io.IOException
   */
  public void write(OutputStream httpoutputstream) throws IOException {
    for (int i = 0; i < count; i++) {
      headers[i].write(httpoutputstream);
    }
  }

  protected MimeHeaderField putHeader(byte[] s) {
    MimeHeaderField mimeheaderfield = find(s);

    if (mimeheaderfield == null) {
      mimeheaderfield = putHeader();
      mimeheaderfield.setName(s, 0, s.length);
    }
    
    return mimeheaderfield;
  }

  protected MimeHeaderField putHeader() {
    int i = headers.length;

    if (count >= i) {
      MimeHeaderField amimeheaderfield[] = new MimeHeaderField[count * 2];
      System.arraycopy(headers, 0, amimeheaderfield, 0, i);
      headers = amimeheaderfield;
    }

    MimeHeaderField mimeheaderfield;

    if ((mimeheaderfield = headers[count]) == null) {
      headers[count] = mimeheaderfield = new MimeHeaderField(rfc1123Format, rfc1123FormatLM);
    }

    count++;
    return mimeheaderfield;
  }

  /**
   * Returns string representation ot headers
   *
   * @return     string representation ot headers
   */
  public String toString() {
    StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append("{");

    for (int i = 0; i < count; i++) {
      stringbuffer.append("{");
      stringbuffer.append(headers[i].toString());
      stringbuffer.append("}");

      if (i < count - 1) {
        stringbuffer.append(",");
      }
    }

    stringbuffer.append("}");
    return stringbuffer.toString();
  }

}

