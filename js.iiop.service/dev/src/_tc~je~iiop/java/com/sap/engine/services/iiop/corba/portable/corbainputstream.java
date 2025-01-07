/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA.portable;

import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.TypeCodeImpl;
import com.sap.engine.services.iiop.CORBA.CORBAObject;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.tc.logging.Location;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;

import java.util.Stack;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/* This is the implementation of the org.omg.CORBA.portable.InputStream abstract
 class. The CORBAInputStream class reads data written through the  IIOP protocol.
 The methods in this class such as read_short, read_long etc. take care of the
 alignment and the encoding (little or big endian) required by the IIOP protocol.
 *
 * @author Georgy Stanev, Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public class CORBAInputStream extends org.omg.CORBA_2_3.portable.InputStream {

  protected byte[] data;
  private int index = 0;
  protected boolean littleEndian = false;
  private int offset = 0;
  protected int endTag = 0;
  protected int chunkedRecursionLevel = 0;
  protected long endBlockIndex = 0x7fffff00L;
  protected boolean isChunked = false;
  protected int size = 0;
  protected org.omg.CORBA.ORB orb;
  protected boolean isEncapsulated = false;
  protected int minorVersion = 0;
  private int marker = -1;
  private int codeSetChar;
  private int codeSetWChar;
  private boolean codesetEnabled = true;

  boolean debug = LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug();
  Location location = LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW);


  //Attention!! Here the array is't copy, only reference to him are setted.
  public CORBAInputStream(org.omg.CORBA.ORB orb0, byte[] data) {
    this(orb0, data, data.length);
  }

  public CORBAInputStream(org.omg.CORBA.ORB orb0, byte[] data, int size) {
    super();
    this.size = size;
    this.data = data;
    this.orb = orb0;
    codeSetChar = CodeSetChooser.charNativeCodeSet();
    codeSetWChar = CodeSetChooser.wcharNativeCodeSet();
  }

  public CORBAInputStream(org.omg.CORBA.ORB orb0, byte[] data, boolean le) {
    this(orb0, data, data.length);
    littleEndian = le;
  }

  public void setORB(org.omg.CORBA.ORB orb0) {
    this.orb = orb0;
  }

  public void setCodeSets(int charCodeSet, int wcharCodeSet) {
    this.codeSetChar = charCodeSet;
    this.codeSetWChar = wcharCodeSet;
  }


  public byte[] getData() {
    return data;
  }

  protected int getSize() {
    return size;
  }

  protected void setSize(int newSize) {
    this.size = newSize;
  }

  public org.omg.CORBA.ORB getORB() {
    return orb;
  }

  public void set_encapsulation() {
    isEncapsulated = true;
  }

  public void set_minor_version(int version) {
    minorVersion = version;
  }

  public byte[] toByteArray() {
    byte[] temp = new byte[size - index];
    System.arraycopy(data, index, temp, 0, size - index);
    return temp;
  }

  public void setEndian(boolean b) {
    littleEndian = b;
  }

  public boolean getEndian() {
    return littleEndian;
  }

  public CORBAInputStream dup() {
    return new CORBAInputStream(orb, data, littleEndian);
  }

  public boolean isAvailable() {
    return (index < size);
  }

  public void setOffset(int i) {
    offset = i;
  }

  // added for testing reset method
  public int getPos() {
    return index;
  }

  public void setPos(int index) {
    this.index = index;
  }

  public void reset(int pos) {
    if (pos >= 0 && pos < size) {
      index = pos;
    }
  }

  // end reset method
  protected void align(int i) {
    if(endBlockIndex == (long) index) {
      endBlockIndex = 0x7fffff00L;
      startBlock();
    } else if(endBlockIndex < (long) index) {
      throw new MARSHAL("Chunk overflow at offset " + index);
    }
    int cnt;
    cnt = ((index - 1) + i + offset) & (~(i - 1));
    index = cnt - offset;
  }

  protected void startBlock() {
    if (debug) {
      location.debugT("CORBAInputStream.startBlock()", "START BLOCK: " + index);
      location.debugT("CORBAInputStream.startBlock()", "  chunked: " + isChunked);
      location.debugT("CORBAInputStream.startBlock()", "  endBlockIndex: " + endBlockIndex);
    }
    if ( (!isChunked) || ((endBlockIndex < 0x7fffff00L) && (index < endBlockIndex))){
      if (debug) {
        location.debugT("CORBAInputStream.startBlock()", "  skip start block... ");
      }
      return;
    }
    if (debug) {
      location.debugT("CORBAInputStream.startBlock()", "  data.length : " + data.length);
    }
    if (index == size) {
      index -= 4;
      return;
    }
    endBlockIndex = 0x7fffff00L;
    endBlockIndex = read_long();
    if (debug) {
      location.debugT("CORBAInputStream.startBlock()", "  new endBlockIndex: " + endBlockIndex);
    }
    if ((endBlockIndex > 0L) && (endBlockIndex < 0x7fffff00L)) {
      endBlockIndex += index;
      if (debug) {
        location.debugT("CORBAInputStream.startBlock()", "  endBlockIndex: " + endBlockIndex);
      }
    } else {
      endBlockIndex = 0x7fffff00L;
      index -= 4;
      if (debug) {
        location.debugT("CORBAInputStream.startBlock()", "  fix endBlockIndex: " + endBlockIndex);
        location.debugT("CORBAInputStream.startBlock()", "  index: " + index);
      }
    }
    if (debug) {
      location.debugT("CORBAInputStream.startBlock()", "  start chunk index : " + index);
    }
  }

  protected void endBlock() {
    if (debug) {
      location.debugT("CORBAInputStream.endBlock()", "END BLOCK: " + index);
      location.debugT("CORBAInputStream.endBlock()", "  endBlockIndex: " + endBlockIndex);
    }
    if(endBlockIndex != 0x7fffff00L) {
      if(endBlockIndex == (long) index) {
        endBlockIndex = 0x7fffff00L;
      } else {
        if (debug) {
          location.debugT("CORBAInputStream.endBlock()", "skip end block... ");
        }
      }
    }
  }

  protected void readEndTag() {
    if (debug) {
      location.debugT("CORBAInputStream.readEndTag()", "READ END TAG at index : " + index);
      location.debugT("CORBAInputStream.readEndTag()", "Is chunked=" + isChunked + ", chunkedRecursionLevel=" + chunkedRecursionLevel + ", endTag=" + endTag);
    }
    if (isChunked) {
      int possitionBeforeEndTag = index;
      int readedEndTag = read_long();
      if (debug) {
        location.debugT("CORBAInputStream.readEndTag()", "Read end tag: " + readedEndTag);
      }

      if (readedEndTag > 0) {
        throw new MARSHAL("Unexpected possitive end tag", endTag, CompletionStatus.COMPLETED_MAYBE);
      } else if (readedEndTag > chunkedRecursionLevel) { //skipped any number of continuing end tags
        reset(possitionBeforeEndTag);
      }
      
      chunkedRecursionLevel++;
    }
    endTag++;
    if (debug) {
      location.debugT("CORBAInputStream.readEndTag()", "New endTag=" + endTag + ", new chunkedRecursionLevel=" + chunkedRecursionLevel);
    }
  }

  ////////////////////////// Implementation //////////////////////////
  public boolean read_boolean() {
    align(1);
    return (data[index++] != 0);
  }

  public void read_boolean_array(boolean[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_boolean();
    }
  }

  public byte unaligned_read_octet() {
    return data[index++];
  }

  public byte read_octet() {
    align(1);
    return data[index++];
  }

  public void read_octet_array(byte[] val, int off, int len) {
    System.arraycopy(data, index, val, off, len);
    index += len;
  }

  public char read_char() {
    return (char) (data[index++] & 0xFF);
  }

  public void read_char_array(char[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_char();
    }
  }

  public short read_short() {
    align(2);
    int b1, b2;

    if (littleEndian) {
      b2 = (data[index++] << 0) & 0x000000FF;
      b1 = (data[index++] << 8) & 0x0000FF00;
    } else {
      b1 = (data[index++] << 8) & 0x0000FF00;
      b2 = (data[index++] << 0) & 0x000000FF;
    }

    return (short) (b1 | b2);
  }

  public int read_uShort() {
    align(2);
    int b1, b2;

    if (littleEndian) {
      b2 = (data[index++] << 0) & 0x000000FF;
      b1 = (data[index++] << 8) & 0x0000FF00;
    } else {
      b1 = (data[index++] << 8) & 0x0000FF00;
      b2 = (data[index++] << 0) & 0x000000FF;
    }

    return (b1 | b2);
  }

  public void read_short_array(short[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_short();
    }
  }

  public short read_ushort() {
    return read_short();
  }

  public void read_ushort_array(short[] val, int off, int len) {
    read_short_array(val, off, len);
  }

  public int unaligned_read_long() {
    int b1, b2, b3, b4;

    if (littleEndian) {
      b4 = (data[index++] <<  0) & 0x000000FF;
      b3 = (data[index++] <<  8) & 0x0000FF00;
      b2 = (data[index++] << 16) & 0x00FF0000;
      b1 = (data[index++] << 24) & 0xFF000000;
    } else {
      b1 = (data[index++] << 24) & 0xFF000000;
      b2 = (data[index++] << 16) & 0x00FF0000;
      b3 = (data[index++] <<  8) & 0x0000FF00;
      b4 = (data[index++] <<  0) & 0x000000FF;
    }

    return (b1 | b2 | b3 | b4);
  }

  public int read_long() {
    align(4);
    return unaligned_read_long();
  }

  public void read_long_array(int[] val, int off, int len) {
    val[off] = read_long();
    for (int i = 1; i < len; i++) {
      val[off + i] = unaligned_read_long();
    }
  }

  public int read_ulong() {
    return read_long();
  }

  public void read_ulong_array(int[] val, int off, int len) {
    read_long_array(val, off, len);
  }

  public long read_longlong() {
    align(8);
    long i1, i2;

    if (littleEndian) {
      i2 = read_long() & 0xFFFFFFFFL;
      i1 = (long) unaligned_read_long() << 32;
    } else {
      i1 = (long) read_long() << 32;
      i2 = unaligned_read_long() & 0xFFFFFFFFL;
    }

    return (i1 | i2);
  }

  public void read_longlong_array(long[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_longlong();
    }
  }

  public long read_ulonglong() {
    return read_longlong();
  }

  public void read_ulonglong_array(long[] val, int off, int len) {
    read_longlong_array(val, off, len);
  }

  public float read_float() {
    return Float.intBitsToFloat(read_long());
  }

  public void read_float_array(float[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_float();
    }
  }

  public double read_double() {
    return Double.longBitsToDouble(read_longlong());
  }

  public void read_double_array(double[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_double();
    }
  }

  public String read_string() {
    int written_length = read_long();
    int str_length = written_length;
    String result;

    if ((str_length > 0) && (data[(index + written_length) - 1] == 0)) {
      str_length--;
    }
    if (str_length == 0) {  //only Null terminated byte
      result = "";
    } else {
      if (codesetEnabled) {
        String codeSetSTR = CodeSetChooser.csName(codeSetChar);
        try {
          result = new String(data, index, str_length, codeSetSTR);
        } catch(UnsupportedEncodingException unsupportedencodingexception) {
          if (location.beError()) {
            location.errorT("CORBAInputStream.read_string()", "Charset " + codeSetSTR + " is not supported");
          }
          result = "";
        }
      } else {  //previous version
        result = new String(data, index, str_length);
      }
    }

    index += written_length;
    return result;

  }

  public char read_wchar() {
    boolean isLittleEndian = littleEndian;
    if (minorVersion == 2) {
      read_octet(); // read size of wchar-a
      isLittleEndian = read_BOM();
    } else {
      align(2);
    }

    return read_wchar(isLittleEndian);
  }

  public void read_wchar_array(char[] val, int off, int len) {
    for (int i = 0; i < len; i++) {
      val[off + i] = read_wchar();
    }
  }

  public String read_wstring() {
    String result;

    if (isEncapsulated || (minorVersion == 2)) {
      int bytesLength = read_long(); //length in bytes
      if (bytesLength == 0) {
        return "";
      }
      char[] wstringAsChars = new char[bytesLength];
      int indexEndPoint = index + bytesLength;
      boolean isLittleEndian = read_BOM();
      int count = 0;
      while (index < indexEndPoint) {
        wstringAsChars[count++] = read_wchar(isLittleEndian);
      }
      result = new String(wstringAsChars, 0, count);
    } else {      // GIOP 1.1; In common for 1.0 MARSHAL exception should be rised .
      int wcharsLength = read_long(); //length in bytes or unsigned integers
      char[] wstringAsChars = new char[wcharsLength];
      int numBytesInWchar;
      if ((codeSetWChar == CodeSetChooser.CODESET_UTF16) || (codeSetWChar == CodeSetChooser.CODESET_ISO10646_UCS2)) { //string length is in bytes
        numBytesInWchar = 2;
      } else { //default //string length is in wchars
        numBytesInWchar = 1;
      }
      int indexEndPoint = index + (wcharsLength * numBytesInWchar);
      int count = 0;
      while (index < indexEndPoint) {
        wstringAsChars[count++] = read_wchar(littleEndian);
      }

      if(count != 0 && wstringAsChars[count - 1] == 0) {  //terminating NULL character
        result = new String(wstringAsChars, 0, count - 1);
      } else  {
        result = new String(wstringAsChars, 0, count);
      }

    }
    return result;
  }

  public synchronized void mark(int readlimit) {
    marker = index;
  }

  public synchronized void reset() throws IOException {
    if (marker >= 0) {
      index = marker;
      marker = -1;
    }
  }

  public boolean markSupported() {
    return true;
  }

  public org.omg.CORBA.Object read_Object() {
    return read_Object(null);
  }

  public org.omg.CORBA.Object read_Object(Class clas) {
    IOR ior = new IOR(orb, this);
    if (debug) {
      location.debugT("CORBAInputStream.read_Object(Class)", "READ OBJECT at : " + index);
    }
    if (ior.is_nil()) {
      return null;
    }

    org.omg.CORBA.portable.ObjectImpl object = new CORBAObject(ior);

    if (clas != null && clas.isInterface()) {
      return (org.omg.CORBA.Object) javax.rmi.PortableRemoteObject.narrow(object, clas);
    } else if (clas != null && org.omg.CORBA.portable.ObjectImpl.class.isAssignableFrom(clas)) {
      try {
        org.omg.CORBA.portable.ObjectImpl inst = (org.omg.CORBA.portable.ObjectImpl) clas.newInstance();
        inst._set_delegate(object._get_delegate());
        if (debug) {
          location.debugT("CORBAInputStream.read_Object(Class)", "OBJECT READ 1 at : " + index);
        }
        return inst;
      } catch (Exception e) {
        if (debug) {
          location.debugT("CORBAInputStream.read_Object(Class)", "OBJECT READ 2 at : " + index);
        }
        return object;
      }
    } else {
      if (debug) {
        location.debugT("CORBAInputStream.read_Object(Class)", "OBJECT READ 3 at : " + index);
      }
      return object;
    }
  }

  public org.omg.CORBA.TypeCode read_TypeCode() {
    TypeCodeImpl tc = new TypeCodeImpl(orb);
    tc.read_value(this);
    return tc;
  }

  public org.omg.CORBA.Any read_any() {
    org.omg.CORBA.Any any;
    any = orb.create_any();
    TypeCodeImpl tc = new TypeCodeImpl(orb);
    tc.read_value(this);
    any.read_value(this, tc);
    return any;
  }

    /*return is little endian wchar*/
  private boolean read_BOM() {
    int BOM1 = (unaligned_read_octet() & 0xFF);
    int BOM2 = (unaligned_read_octet() & 0xFF);
    if ((BOM1 == 0xFF) && (BOM2 == 0xFE)) {
      return true;
    } else if ((BOM1 == 0xFE) && (BOM2 == 0xFF)){
      return false;
    } else {
      index -= 2; //there is no BOM
      return false;
    }
  }

  private char read_wchar(boolean isLittleEndian) {
    switch (codeSetWChar) {
      case CodeSetChooser.CODESET_ISO10646_UCS2 :
      case CodeSetChooser.CODESET_UTF16 :
        if (isLittleEndian) {
          return (char) ((data[index++] & 0xFF) | (data[index++] << 8));
        } else {
          return (char) ((data[index++] << 8) | (data[index++] & 0xFF));
        }
      case CodeSetChooser.CODESET_UTF8 :
        short word0 = (short) (0xFF & data[index++]);
        if ((word0 & 0x80) == 0) {
          return (char) word0;
        } else if((word0 & 0xE0) == 0xC0) {
          return (char)((word0 & 0x1F) << 6 | data[index++] & 0x3F);
        } else {
          short word1 = (short)(0xFF & data[index++]);
          return (char)((word0 & 0xF) << 12 | (word1 & 0x3F) << 6 | data[index++] & 0x3F);
        }
      default :
        throw new MARSHAL("Bad codeset - hex: " + Integer.toHexString(codeSetWChar) + " dec: " + codeSetWChar);
    }
  }

  private class StackElement {
    public boolean endian;
    public int length;

    public StackElement(boolean endian, int length) {
      this.endian = endian;
      this.length = length;
    }

    public String toString() {
      return "endian: " + endian + "   end at: " + length;
    }

  }

  private Stack<StackElement> encapsulationStack;


  public int beginSequence() {
    return read_long(); // sequence length
  }

  public int beginEncapsulation() {
    if (encapsulationStack == null) {
      encapsulationStack = new Stack<StackElement>();
    }
    int encapsLen = read_long();
    StackElement se = new StackElement(littleEndian, index + encapsLen);
    encapsulationStack.push(se);
    setEndian(read_boolean());
    return encapsLen;
  }

  public void endEncapsulation() {
    StackElement se = encapsulationStack.pop();
    setEndian(se.endian);
    if (index != se.length) {
      if (debug) {
        location.debugT("CORBAInputStream.endEncapsulation()", "ENCAPSULATION ERROR : " + encapsulationStack);
      }
    }
  }

}

