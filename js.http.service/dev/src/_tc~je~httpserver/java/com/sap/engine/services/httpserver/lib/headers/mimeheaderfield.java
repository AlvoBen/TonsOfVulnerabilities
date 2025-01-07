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
 * MimeHeaderField is used to present the MIME headers in HTTP request and response
 *
 * @author Galin Galchev
 * @version 4.0
 */
import java.io.*;
import java.text.*;
import java.util.*;
import com.sap.engine.lib.text.FastDateFormat;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.lib.exceptions.HttpNumberFormatException;
import com.sap.engine.services.httpserver.lib.exceptions.HttpIllegalArgumentException;
import com.sap.engine.services.httpserver.lib.exceptions.HttpCharConversionException;

public class MimeHeaderField {

  private static final Locale loc = Locale.US;
  private final SimpleDateFormat rfc1123FormatSimple = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z", loc);
  private final SimpleDateFormat rfc1036FormatSimple = new SimpleDateFormat("EEEEEEEEE, dd-MMM-yy HH:mm:ss z", loc);
  private final SimpleDateFormat asctimeFormatSimple = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyyy", loc);
  private static final byte charval[] = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
  private FastDateFormat rfc1123Format = null;
  private FastDateFormat rfc1123FormatLM = null;
  private final MessageBytes name = new MessageBytes();
  private final MessageBytes value = new MessageBytes();
  private int intValue = -1;
  private long dateValue = 0L;
  private long dateValueLM = 0L;
  private int type = 0;

  /**
   * Default constructor
   *
   */
  public MimeHeaderField(FastDateFormat rfc1123Format, FastDateFormat rfc1123FormatLM) {
    type = 0;
    this.rfc1123Format = rfc1123Format;
    this.rfc1123FormatLM = rfc1123FormatLM;
  }

  public void init(MimeHeaderField mimeHeaderField) {
    name.setBytes(mimeHeaderField.name.getBytes());
    value.setBytes(mimeHeaderField.value.getBytes());
    intValue = mimeHeaderField.intValue;
    dateValue = mimeHeaderField.dateValue;
    dateValueLM = mimeHeaderField.dateValueLM;
    type = mimeHeaderField.type;
  }

  /**
   * Resets MimeHeaderField
   *
   */
  public void reset() {
    name.reset();
    value.reset();
    type = 0;
  }

  /**
   * Set the name ot the header form byte array
   *
   * @param   abyte0  byte array
   * @param   i  position in array
   * @param   j  length
   */
  public void setName(byte abyte0[], int i, int j) {
    name.setBytes(abyte0, i, j);
  }

  /**
   * set the value of the header
   *
   * @param   abyte0  byte array
   * @param   i  position in array
   * @param   j  length
   */
  public void setValue(byte abyte0[], int i, int j) {
    value.setBytes(abyte0, i, j);
    type = 1;
  }

  /**
   * Set int value of header
   *
   * @param   i  int value
   */
  public void setIntValue(int i) {
    intValue = i;
    type = 2;
  }

  /**
   * Set date value of header
   *
   * @param   l  date value as long
   */
  public void setDateValue(long l) {
    dateValue = l;
    type = 3;
  }

  /**
   * Set last modified date value
   *
   * @param   l  date value as long
   */
  public void setDateValueLM(long l) {
    dateValueLM = l;
    type = 4;
  }

  /**
   * Set date value of header to be current time
   *
   */
  public void setDateValue() {
    dateValue = new Date(System.currentTimeMillis()).getTime();
    type = 3;
  }

  /**
   * Set last modified date value to be current time
   *
   */
  public void setDateValueLM() {
    dateValueLM = new Date(System.currentTimeMillis()).getTime();
    type = 4;
  }

  /**
   * Returns the name ot the header
   *
   * @return     name of the header
   */
  public String getName() {
    return name.toString();
  }

  /**
   * Reaturns the value of the header
   *
   * @return     value of the header
   */
  public String getValue() {
    switch (type) {
      case 1: {
        return value.toString();
      }
      case 2: {
        return String.valueOf(intValue);
      }
      case 3: {
        byte[] res = new byte[rfc1123Format.getLength()];
        rfc1123Format.getDate(res, 0, dateValue);
        return new String(res);
      }
      case 4: {
        byte[] res = new byte[rfc1123FormatLM.getLength()];
        rfc1123FormatLM.getDate(res, 0, dateValueLM);
        return new String(res);
      }
    }

    return null;
  }

  /**
   * Reaturns the value of the header as byte array
   *
   * @return     value of the header as byte array
   */
  public byte[] getByteValue() {
    if (type == 1) {
      return value.getBytes();
    }
    String res = getValue();
    if (res != null) {
      return res.getBytes();
    }
    return null;
  }

  /**
   * Reaturns the value of the header as int
   *
   * @return     int value of the header
   * @exception   java.lang.NumberFormatException
   */
  public int getIntValue() throws NumberFormatException {
    switch (type) {
      case 2: {
        return intValue;
      }
      case 1: {
        return value.toInteger();
      }
    }
    throw new HttpNumberFormatException(HttpNumberFormatException.UNKNOWN_OR_INCOMPATIBLE_FIELD_TYPE);
  }

  /**
   * Reaturns the value of the header as long
   *
   * @return     long value of the header
   * @exception   java.lang.NumberFormatException
   */
  public long getLongValue() throws NumberFormatException {
    if (type != 3 && type != 4){
      return Long.parseLong(new String(value.getBytes()));
    }
    throw new HttpNumberFormatException(HttpNumberFormatException.UNKNOWN_OR_INCOMPATIBLE_FIELD_TYPE);
  }

  /**
   * Parse given string to date format
   *
   * @param   s  date String
   */
  private long parse(String s) {
    try {
      return rfc1123FormatSimple.parse(s).getTime();
    } catch (ParseException ex) {
      try {
        synchronized (rfc1036FormatSimple) {
          return rfc1036FormatSimple.parse(s).getTime();
        }
      } catch (ParseException ex2) {
        try {
          synchronized (asctimeFormatSimple) {
            return asctimeFormatSimple.parse(s).getTime();
          }
        } catch (ParseException ex3) {
          throw new HttpIllegalArgumentException(HttpIllegalArgumentException.UNKNOWN_TIME_FORMAT);
        }
      }
    }
  }

  /**
   * Reaturns the value of the header as long representing date
   *
   * @return     date value of the header
   * @exception   java.lang.IllegalArgumentException
   */
  public long getDateValue() throws IllegalArgumentException {
    switch (type) {
      case 4: {
        return dateValueLM;
      }
      case 3: {
        return dateValue;
      }
      case 1: {
        return parse(value.toString());
      }
      case 2:
      default: {
        break;
      }
    }

    throw new HttpIllegalArgumentException(HttpIllegalArgumentException.UNKNOWN_FIELD_TYPE);
  }

  private int intGetBytes(int i, byte abyte0[], int j) {
    int k = 0x3b9aca00;
    int i1 = 0;

    if (i == 0) {
      abyte0[j] = charval[0];
      return 1;
    }

    for (; k > 0; k /= 10) {
      int l = i / k;

      if (l != 0 || i1 > 0) {
        abyte0[j + i1++] = charval[l];
      }

      i %= k;
    }

    return i1;
  }

  /**
   * Return header name:value/r/n in byte array
   *
   * @param   abyte0  byte array to be returned in
   * @param   i  position in byte array
   * @return     number of bytes added
   */
  public int getBytes(byte abyte0[], int i) {
    int j = i;
    i += name.getBytes(abyte0, i);
    abyte0[i++] = 58;
    abyte0[i++] = 32;

    switch (type) {
      case 1: {
        i += value.getBytes(abyte0, i);
        break;
      }
      case 2: {
        i += intGetBytes(intValue, abyte0, i);
        break;
      }
      case 3: {
        rfc1123Format.getDate(abyte0, i, dateValue);
        i += rfc1123Format.getLength();
        break;
      }
      case 4: {
        rfc1123FormatLM.getDate(abyte0, i, dateValueLM);
        i += rfc1123Format.getLength();
        break;
      }
    }

    abyte0[i++] = 13;
    abyte0[i++] = 10;
    return i - j;
  }

  /**
   * Parse name and value of header
   *
   * @param   abyte0  byte array to be parsed
   * @param   i  start position
   * @param   j  length
   * @exception   java.lang.IllegalArgumentException
   */
  public void parse(byte abyte0[], int i, int j) throws IllegalArgumentException {
    int k = i;
    byte byte0;

    while ((byte0 = abyte0[i++]) != 58 && byte0 != 32) {
      if (byte0 == 10) {
        throw new HttpIllegalArgumentException(HttpIllegalArgumentException.UNKNOWN_FIELD_TYPE);
      }
    }

    setName(abyte0, k, i - k - 1);

    for (; byte0 == 32; byte0 = abyte0[i++]) {
    }

    if (byte0 != 58) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.INCOMPATIBLE_FIELD_CONTENT);
    }

    while (abyte0[i] == 32) {
      i++;
    }
    j = j + k;
    while (abyte0[j - 1] == 32) {
      j--;
    }

    if (i < j) {
      setValue(abyte0, i, j - i);
    } else {
      setValue(abyte0, i, 0);
    }
  }

  /**
   * Writes headers name and value to an outputstream
   *
   * @param   httpoutputstream  outputstream to write in
   * @exception   java.io.IOException
   */
  public void write(OutputStream httpoutputstream) throws IOException {
  	
    name.write(httpoutputstream);
    print(httpoutputstream, ": ");

    switch (type) {
      case 1: {
        // filtering is already done in set/add header methods in servlet_jsp
        value.filterValueForCRLF();
      	value.write(httpoutputstream);
        println(httpoutputstream);
        return;
      }
      case 2: {
        println(httpoutputstream, intValue);
        return;
      }
      case 3: {
        byte[] res = new byte[rfc1123Format.getLength()];
        rfc1123Format.getDate(res, 0, dateValue);
        httpoutputstream.write(res);
        println(httpoutputstream);
        return;
      }
      case 4: {
        byte[] res = new byte[rfc1123FormatLM.getLength()];
        rfc1123FormatLM.getDate(res, 0, dateValueLM);
        httpoutputstream.write(res);
        println(httpoutputstream);
        return;
      }
    }

    println(httpoutputstream);
  }

  /**
   * Prints string to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   s  string
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, String s) throws IOException {
    if (s == null) {
      s = "null";
    }

    int len = s.length();

    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);

      if ((c & 0xff00) != 0) { // high order byte must be zero
        throw new HttpCharConversionException(HttpCharConversionException.FIRST_BYTE_MUST_BE_ZERO);
      }

      httpoutputstream.write(c);
    } 
  }

  /**
   * Prints boolean to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   b
   * @exception   java.io.IOException  boolean
   */
  public void print(OutputStream httpoutputstream, boolean b) throws IOException {
    String msg;

    if (b) {
      msg = "true";
    } else {
      msg = "false";
    }

    print(httpoutputstream, msg);
  }

  /**
   * Prints char to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   c  char
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, char c) throws IOException {
    print(httpoutputstream, String.valueOf(c));
  }

  /**
   * Prints int to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   i  int
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, int i) throws IOException {
    print(httpoutputstream, String.valueOf(i));
  }

  /**
   * Prints long to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   l  long
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, long l) throws IOException {
    print(httpoutputstream, String.valueOf(l));
  }

  /**
   * Prints float to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   f  float
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, float f) throws IOException {
    print(httpoutputstream, String.valueOf(f));
  }

  /**
   * Prints double to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   d  double
   * @exception   java.io.IOException
   */
  public void print(OutputStream httpoutputstream, double d) throws IOException {
    print(httpoutputstream, String.valueOf(d));
  }

  /**
   * Prints \r\n to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream) throws IOException {
    print(httpoutputstream, "\r\n");
  }

  /**
   * Prints new line and string to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   s  string
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, String s) throws IOException {
    print(httpoutputstream, s);
    println(httpoutputstream);
  }

  /**
   * Prints new line and boolean to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   b  boolean
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, boolean b) throws IOException {
    print(httpoutputstream, b);
    println(httpoutputstream);
  }

  /**
   * Prints new line and char to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   c  char
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, char c) throws IOException {
    print(httpoutputstream, c);
    println(httpoutputstream);
  }

  /**
   * Prints new line and int to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   i  int
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, int i) throws IOException {
    print(httpoutputstream, i);
    println(httpoutputstream);
  }

  /**
   * Prints new line and long to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   l  long
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, long l) throws IOException {
    print(httpoutputstream, l);
    println(httpoutputstream);
  }

  /**
   * Prints new line and float to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   f  float
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, float f) throws IOException {
    print(httpoutputstream, f);
    println(httpoutputstream);
  }

  /**
   * Prints new line and double to outputstream
   *
   * @param   httpoutputstream  outputstream
   * @param   d  double
   * @exception   java.io.IOException
   */
  public void println(OutputStream httpoutputstream, double d) throws IOException {
    print(httpoutputstream, d);
    println(httpoutputstream);
  }

  /**
   * Returns true if name is equals to string
   *
   * @param   s  string
   * @return     true if name is equals to string
   */
  public boolean nameEquals(String s) {
    return name.equalsIgnoreCase(s);
  }

  /**
   * Returns true if name is equals to byte array
   *
   * @param   abyte0  byte array
   * @param   i  start position
   * @param   j  length
   * @return     ture if equals
   */
  public boolean nameEquals(byte abyte0[], int i, int j) {
    return name.equalsIgnoreCase(abyte0, i, j);
  }

  /**
   * Resturn string representaion ot header
   *
   * @return     string representaion ot header
   */
  public String toString() {
    StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append(name.toString());
    stringbuffer.append(": ");
    stringbuffer.append(getValue());
    return stringbuffer.toString();
  }
}

