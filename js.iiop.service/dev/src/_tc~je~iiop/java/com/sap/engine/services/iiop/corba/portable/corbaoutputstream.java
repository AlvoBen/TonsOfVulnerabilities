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

import com.sap.engine.services.iiop.CORBA.GIOPMessageConstants;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.CORBA.TypeCodeImpl;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.util.Stack;

/* This is the implementation of the org.omg.CORBA.portable.OutputStream abstract
 class. The CORBAOutputStream class writes data through the  IIOP protocol.
 The methods in this class such as write_short, write_long etc. take care of the
 alignment and the encoding (little or big endian) required by the IIOP protocol.
 *
 * @author Georgy Stanev, Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public class CORBAOutputStream extends org.omg.CORBA_2_3.portable.OutputStream {

  protected int index;
  protected byte[] data;
  protected boolean littleEndian;
  protected int endTag = 0;
  protected boolean inBlock = false;
  protected int startBlockIndex = -1;
  protected final static int VALUE_NO_CODEBASE = 0;
  protected final static int VALUE_CODEBASE = 1;
  protected final static int VALUE_NO_TYPE_INFORMATION = 0;
  protected final static int SINGLE_TYPE_INFORMATION = 2;
  protected final static int MULTIPLE_TYPE_INFORMATION = 6;
  protected final static int CHUNK = 8;
  protected final static int NO_CHUNK = 0;
  static final int DEFAULT_SIZE = 1024;
  protected org.omg.CORBA.ORB orb;
  public int exType = 0;
  private boolean isEncapsulated = false;
  private int minorVersion = 0;
  private int codeSetChar;
  private int codeSetWChar;
  private boolean codesetEnabled = true;
  private int end_contexts_index = 0;


  public CORBAOutputStream(org.omg.CORBA.ORB orb0) {
    this(orb0, DEFAULT_SIZE);
  }

  public CORBAOutputStream(org.omg.CORBA.ORB orb0, int size) {
    orb = orb0;
    data = new byte[size];
    index = 0;
    littleEndian = false;
    codeSetChar = CodeSetChooser.charNativeCodeSet();
    codeSetWChar = CodeSetChooser.wcharNativeCodeSet();
  }

  public void set_encapsulation() {
    isEncapsulated = true;
  }

  public void set_minor_version(int version) {
    minorVersion = version;
  }

  public void makeGIOPHeader(byte type) {
    data[0] = 0x47;
    data[1] = 0x49;
    data[2] = 0x4f;
    data[3] = 0x50;
    data[4] = GIOPMessageConstants.VERSION_MAJOR;
    data[5] = GIOPMessageConstants.VERSION_MINOR;
    data[6] = GIOPMessageConstants.LITTLE_ENDIAN ? 1 : 0;
    data[7] = type;
    index = 12;
  }

  public void prepareGIOPHeader(byte type, byte major, byte minor) {
    data[0] = 0x47;
    data[1] = 0x49;
    data[2] = 0x4f;
    data[3] = 0x50;
    data[4] = major; //GIOPMessageConstants.VERSION_MAJOR;
    data[5] = minor; //GIOPMessageConstants.VERSION_MINOR;

    if (littleEndian) {
      data[6] = (byte) 0x01; //GIOPMessageConstants.LITTLE_ENDIAN ? 1 : 0;
    } else {
      data[6] = (byte) (0); //GIOPMessageConstants.LITTLE_ENDIAN ? 1 : 0;
    }

    data[7] = type;
    index = 12;
  }

  public byte[] toByteArray() {
    byte[] temp = new byte[index];
    System.arraycopy(data, 0, temp, 0, index);
    return temp;
  }

  public byte[] toByteArray_forSend() {
    return data;
  }

  public int byteArray_forSend_length() {
    return index;
  }

  public void setEndian(boolean b) {
    littleEndian = b;
  }

  public boolean getEndian() {
    return littleEndian;
  }

  public void setByte(int idx, byte b) throws IndexOutOfBoundsException {
    if (idx > data.length || idx > index) {
      String messageWithId = "ID010036: CORBAOutputStream:setByte - index out of bounds";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.setByte(int, byte)", messageWithId);
      }
      throw new IndexOutOfBoundsException(messageWithId);
    }

    data[idx] = b;
  }

  protected void align(int length) { //length = 1,2,4 or 8
    int cnt;
    cnt = ((index - 1) + length) & (~(length - 1));
    index = cnt;

    if (cnt + length < data.length) {
      return;
    }

    resizeStream();
  }

  public int getPos() {
    return index;
  }

  public void writeRepositoryId(String repositoryIDstr) {
    byte[] repositoryID = repositoryIDstr.getBytes();
    if (repositoryID == null) {
      String messageWithId = "ID010037: Error while writing a null string";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.writeRepositoryId(String)", messageWithId);
      }
      throw new org.omg.CORBA.BAD_PARAM(messageWithId);
    }

    write_long(repositoryID.length + 1); // +1 for terminating NULL

    if (index + repositoryID.length <= data.length) {
      System.arraycopy(repositoryID, 0, data, index, repositoryID.length);
      index+=repositoryID.length;
    } else {
      resizeStream();
      System.arraycopy(repositoryID, 0, data, index, repositoryID.length);
      index+=repositoryID.length;
    }
    write_octet((byte) 0); // terminating NULL
  }

  public void writeValueTag(boolean chunk, String url) {
    int tag = 0x7fffff00;

    if (url == null) {
      tag += VALUE_NO_CODEBASE;
    } else {
      tag += VALUE_CODEBASE;
    }

    tag += SINGLE_TYPE_INFORMATION;

    if (chunk) {
      tag += CHUNK;
    } else {
      tag += NO_CHUNK;
    }

    write_long(tag);
  }

  protected void startBlock() {
    write_long(0);
    startBlockIndex = index;
    inBlock = true;
  }

  protected void endBlock() {
    if (!inBlock) {
      return;
    }

    if (index == startBlockIndex) {
      index = startBlockIndex - 4;
      startBlockIndex = -1;
    } else {
      int i = index;
      index = startBlockIndex - 4;
      write_long(i - startBlockIndex);
      index = i;
    }

    inBlock = false;
  }

  protected void writeEndTag(boolean flag) {
    if (flag) {
      write_long(endTag);
    }

    endTag++;
  }

  public void setException(int exType) {
    this.exType = exType;
  }

  /************************ Implementation ObjectOutput****************/
  public void write(byte[] b) throws java.io.IOException {

  }

  public void write(byte[] b, int offset, int len) throws java.io.IOException {

  }

  public void write(int b) throws java.io.IOException {

  }

  ///////////////////////// Implementation /////////////////////////
  public org.omg.CORBA.portable.InputStream create_input_stream() {
    return new CORBAInputStream(orb, data, littleEndian);
  }

  public org.omg.CORBA.ORB orb() {
    return orb;
  }

  public void write_boolean(boolean b) {
    write_octet(b ? (byte) 1 : (byte) 0);
  }

  public void write_boolean_array(boolean[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_boolean(arr[off + i]);
    }
  }

  public void write_octet(byte b) {
    align(1);
    data[index++] = b;
  }

  public void write_octet_array(byte[] arr, int off, int len) {
    int n = off;

    while (n < len + off) {
      int avail;
      int bytes;
      int wanted;
      align(1);
      avail = data.length - index;
      wanted = (len + off) - n;
      bytes = (wanted < avail) ? wanted : avail;
      System.arraycopy(arr, n, data, index, bytes);
      index += bytes;
      n += bytes;
    }
  }

  public void write_char(char c) {
    align(1);
    data[index++] = (byte) (c & 0xFF);
  }

  public void write_char_array(char[] arr, int off, int len) {
    align(1, len);
    for (int i = 0; i < len; i++) {
      data[index++] = (byte) (arr[off + i] & 0xFF);
    }
  }

  public void unaligned_write_short(short s) {
    if (littleEndian) {
      data[index++] = (byte) ((s >>> 0) & 0xFF);
      data[index++] = (byte) ((s >>> 8) & 0xFF);
    } else {
      data[index++] = (byte) ((s >>> 8) & 0xFF);
      data[index++] = (byte) ((s >>> 0) & 0xFF);
    }
  }

  public void write_short(short s) {
    align(2);

    if (littleEndian) {
      data[index++] = (byte) ((s >>> 0) & 0xFF);
      data[index++] = (byte) ((s >>> 8) & 0xFF);
    } else {
      data[index++] = (byte) ((s >>> 8) & 0xFF);
      data[index++] = (byte) ((s >>> 0) & 0xFF);
    }
  }

  public void write_short_array(short[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_short(arr[off + i]);
    }
  }

  public void write_ushort(short i) {
    write_short(i);
  }

  public void write_ushort_array(short[] arr, int off, int len) {
    write_short_array(arr, off, len);
  }

  public void write_long(int i) {
    align(4);

    if (littleEndian) {
      data[index++] = (byte) ((i >>> 0) & 0xFF);
      data[index++] = (byte) ((i >>> 8) & 0xFF);
      data[index++] = (byte) ((i >>> 16) & 0xFF);
      data[index++] = (byte) ((i >>> 24) & 0xFF);
    } else {
      data[index++] = (byte) ((i >>> 24) & 0xFF);
      data[index++] = (byte) ((i >>> 16) & 0xFF);
      data[index++] = (byte) ((i >>> 8) & 0xFF);
      data[index++] = (byte) ((i >>> 0) & 0xFF);
    }
  }

  public void write_long_array(int[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_long(arr[off + i]);
    }
  }

  public void write_ulong(int i) {
    write_long(i);
  }

  public void write_ulong_array(int[] arr, int off, int len) {
    write_long_array(arr, off, len);
  }

  public void write_longlong(long l) {
    align(8);

    if (littleEndian) {
      data[index++] = (byte) ((l >>> 0) & 0xFF);
      data[index++] = (byte) ((l >>> 8) & 0xFF);
      data[index++] = (byte) ((l >>> 16) & 0xFF);
      data[index++] = (byte) ((l >>> 24) & 0xFF);
      data[index++] = (byte) ((l >>> 32) & 0xFF);
      data[index++] = (byte) ((l >>> 40) & 0xFF);
      data[index++] = (byte) ((l >>> 48) & 0xFF);
      data[index++] = (byte) ((l >>> 56) & 0xFF);
    } else {
      data[index++] = (byte) ((l >>> 56) & 0xFF);
      data[index++] = (byte) ((l >>> 48) & 0xFF);
      data[index++] = (byte) ((l >>> 40) & 0xFF);
      data[index++] = (byte) ((l >>> 32) & 0xFF);
      data[index++] = (byte) ((l >>> 24) & 0xFF);
      data[index++] = (byte) ((l >>> 16) & 0xFF);
      data[index++] = (byte) ((l >>> 8) & 0xFF);
      data[index++] = (byte) ((l >>> 0) & 0xFF);
    }
  }

  public void write_longlong_array(long[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_longlong(arr[off + i]);
    }
  }

  public void write_ulonglong(long i) {
    write_longlong(i);
  }

  public void write_ulonglong_array(long[] arr, int off, int len) {
    write_longlong_array(arr, off, len);
  }

  public void write_float(float f) {
    write_long(Float.floatToIntBits(f));
  }

  public void write_float_array(float[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_float(arr[off + i]);
    }
  }

  public void write_double(double d) {
    write_longlong(Double.doubleToLongBits(d));
  }

  public void write_double_array(double[] arr, int off, int len) {
    for (int i = 0; i < len; i++) {
      write_double(arr[off + i]);
    }
  }

  public void write_string(String s) throws org.omg.CORBA.BAD_PARAM {
    if (s == null) {
      String messageWithId = "ID010037: Error while writing a null string";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.write_string(String)", messageWithId);
      }
      throw new org.omg.CORBA.BAD_PARAM(messageWithId);
    }

    int len = s.length();
    align(4);
    int lengthIndex = index;
    index += 4; //reserve 4 bytes for length

    if (codesetEnabled && (codeSetChar != CodeSetChooser.CODESET_ISO8859_1)) {  //UTF8;
      align(1, 3*len + 1);   //resize with max number of bytes coded the string
      for (int i = 0; i < len; i++) {
        unaligned_write_string_char(s.charAt(i), false, true, codeSetChar);
      }
    } else {    //default ISO8859_1
      align(1, len + 1); //resize with number of bytes coded the string
      for (int i = 0; i < len; i++) {
        unaligned_write_string_char(s.charAt(i), false, true, CodeSetChooser.CODESET_ISO8859_1);
      }
    }

    int currentIndex = index;
    index = lengthIndex;
    write_long(currentIndex - (lengthIndex + 4) + 1); //length of written string + 1 for terminating NULL
    index = currentIndex;

    write_octet((byte) 0); // terminating NULL
  }

  public void write_Object(org.omg.CORBA.Object o) {
    if (o == null) {
      IOR.NULL_IOR(orb).write_object(this);
      return;
    }

    IOR ior;
    //    if (o instanceof StubBase) { //Da se napravi s try catch vmesto s if za byrzina
    //      StubBaseInfo stubInfo= (StubBaseInfo)((StubBase) o).getObjectInfo();
    //      ClusterRemoteReference ref = (ClusterRemoteReference) stubInfo.getClusterRemoteInfo();
    //      byte[] info = ref.getCommunicationInfo("Corba");
    //      byte[] key = new byte[12];
    //      System.arraycopy(info, 0, key, 0, key.length);
    //      String ids = new String(info, 12, info.length - 12);
    //
    //      //seeItLater - abe hosta ne e taka
    //      String host = communicationLayer.getURL()[0];    //check for null pointer exception?!?
    //      int idx = host.lastIndexOf(":");
    //      int port = (idx >= 0) ? Integer.parseInt(host.substring(idx)) : 3333;
    //
    //      ior = new IOR(orb, ids, host, port, key);
    //    } else {
    try {
      ior = ((DelegateImpl) ((ObjectImpl) o)._get_delegate()).getIOR();
    } catch (Exception e) {
      orb.connect(o);
      ior = ((DelegateImpl) ((ObjectImpl) o)._get_delegate()).getIOR();
    }
    //    }
    ior.write_object(this, ((ObjectImpl) o)._ids()[0]);
  }

  public void write_TypeCode(org.omg.CORBA.TypeCode tc) {
    if (tc == null) {
      String messageWithId = "ID019040: Error while writing a null type code";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.write_TypeCode(TypeCode)", messageWithId);
      }
      throw new org.omg.CORBA.BAD_PARAM(messageWithId);
    }

    TypeCodeImpl tci;

    if (tc instanceof TypeCodeImpl) {
      tci = (TypeCodeImpl) tc;
    } else {
      tci = new TypeCodeImpl(orb, tc);
    }

    tci.write_value(this);
  }

  public void write_any(org.omg.CORBA.Any any) {
    if (any == null) {
      String messageWithId = "ID010041: Error while writing null struct, union or other";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.write_any(Any)", messageWithId);
      }
      throw new org.omg.CORBA.BAD_PARAM(messageWithId);
    }

    write_TypeCode(any.type());
    any.write_value(this);
  }

  public void write_value(java.io.Serializable value) {
    if (value == null) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("CORBAOutputStream.write_value(Serializable)", "Writing null value.");
      }
      write_long(0);
    }

    /*
     1. Is array?
     2. Is ValueBase
     */
  }

  public void write_wchar(char c) {
    if (index + 3 >= data.length) {
      resizeStream();
    }

    unaligned_write_string_char(c, true, true, codeSetWChar);
  }

  public void write_wchar_array(char val[], int off, int len) {
    align(1, len*3);

    if (len > 0) {
      unaligned_write_string_char(val[off], true, true, codeSetWChar);
    }
    for (int i = 1; i < len; i++) {
      unaligned_write_string_char(val[off + i], true, false, codeSetWChar);
    }
  }

  public void write_wstring(String s) {
    if (s == null) {
      String messageWithId = "ID010042: Error while writing a null WString";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CORBAOutputStream.write_wstring(String)", messageWithId);
      }
      throw new org.omg.CORBA.BAD_PARAM(messageWithId);
    }

    align(4, 4 + 3*s.length() + 3); //reserve -- long(wstring.length) + wstring.length * 3 + NullChar)
    int lengthIndex = index;
    index += 4; //reserve 4 bytes for length

    if (s.length() > 0) {
      unaligned_write_string_char(s.charAt(0), false, true, codeSetWChar);
    }
    for(int i = 1; i < s.length(); i++) {
      unaligned_write_string_char(s.charAt(i), false, false, codeSetWChar);
    }

    int length;
    if (isEncapsulated || (minorVersion == 2)) {
      length = index - (lengthIndex + 4);
    } else {  // 1.1 version
      unaligned_write_string_char('\0', false, true, codeSetWChar); //write null char

      if (codeSetWChar == CodeSetChooser.CODESET_UTF8) {
        length = index - (lengthIndex + 4);
      } else if(codeSetWChar == CodeSetChooser.CODESET_UTF16) {
        length = s.length() + 1;
      } else {
        // This is not supported code set so previous writing of null char should throw an exception
        length = 1; //this should be not reachable
      }
    }

    int currentIndex = index;
    index = lengthIndex;
    write_long(length); //length of written string
    index = currentIndex;
  }

  public void write_Principal(org.omg.CORBA.Principal p) {
    String messageWithId = "ID019043: write_Principal is not implemented";
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("CORBAOutputStream.write_Principal(Principal)", messageWithId);
    }
    throw new org.omg.CORBA.NO_IMPLEMENT(messageWithId);
  }

  private void align(int length, int grow) { //length = 1,2,4 or 8
    int cnt;
    cnt = ((index - 1) + length) & (~(length - 1));
    index = cnt;

    if (cnt + length + grow < data.length) {
      return;
    }

    int l;

    for (l = data.length * 2; index + grow >= l; l *= 2) {
    }
    byte[] temp = new byte[l];
    System.arraycopy(data, 0, temp, 0, data.length);
    data = temp;
  }

  public void setPos(int index) {
    this.index = index;
  }

  private class StackElement {
    public boolean endian;
    public int start;
    public int lenIndex;


    public StackElement(boolean endian, int start, int lenIndex) {
      this.endian = endian;
      this.start = start;
      this.lenIndex = lenIndex;
    }

    public String toString() {
      return "endian: " + endian + "   start at: " + start  + "  length at: " + lenIndex;
    }

  }

  private Stack<StackElement> encapsulationStack;

  public void beginEncapsulation(boolean newEndian) {
    if (encapsulationStack == null) {
      encapsulationStack = new Stack<StackElement>();
    }
    int lenIndex = index; // encapsulation length will be written here
    align(4);
    index += 4; // length of encapsulation will be written later
    int start = index; // start of encapsulation
    StackElement se = new StackElement(littleEndian, start, lenIndex);
    encapsulationStack.push(se);
    setEndian(newEndian);
    write_boolean(newEndian);
  }

  public int endEncapsulation() {
    StackElement se = encapsulationStack.pop();
    int end = index; // store real index
    int length = index - se.start; // the encapsulation length
    setPos(se.lenIndex); // go to encapsulation length position
    write_long(length);
    setPos(end); // set the correct position
    setEndian(se.endian); // restore endian
    return length;
  }


  private void resizeStream() {
    byte[] temp = new byte[data.length * 2];
    System.arraycopy(data, 0, temp, 0, data.length);
    data = temp;
  }

  public int getEnd_contexts_index() {
    return end_contexts_index;
  }

  public void setEnd_contexts_index(int end_contexts_index) {
    this.end_contexts_index = end_contexts_index;
  }

  public void setCodeSets(int codeSet, int codeSetW) {
    this.codeSetChar = codeSet;
    this.codeSetWChar = codeSetW;
  }

  /*Implemented without use or BOM.*/
  private void unaligned_write_string_char(char c, boolean isWChar, boolean doAlignment, int codeSet) {
    switch(codeSet) {
      case CodeSetChooser.CODESET_ISO8859_1 :  //string
        data[index++] = (byte) (c & 0xFF);
        break;

      case CodeSetChooser.CODESET_UTF8 :   //string; wchar; wstring
        if (c <= '\177') {
          if(((minorVersion == 2) || isEncapsulated) && isWChar) {
            data[index++] = 1;     //size of wchar
          }
          data[index++] = (byte)c;
        } else if (c > '\u07FF') {
          if(((minorVersion == 2) || isEncapsulated) && isWChar) {
            data[index++] = 3;     //size of wchar
          }
          data[index++] = (byte)(0xe0 | c >> 12 & 0xf);
          data[index++] = (byte)(0x80 | c >> 6 & 0x3f);
          data[index++] = (byte)(0x80 | c >> 0 & 0x3f);
        } else {
          if(((minorVersion == 2) || isEncapsulated) && isWChar) {
            data[index++] = 2;     //size of wchar
          }
          data[index++] = (byte)(0xc0 | c >> 6 & 0x1f);
          data[index++] = (byte)(0x80 | c >> 0 & 0x3f);
        }
        break;

    case CodeSetChooser.CODESET_UTF16 :  //wchar; wstring
      if ((minorVersion == 2) || isEncapsulated) {
        if (isWChar) {
          data[index++] = 2;     //size of wchar
        }

        //no BOM => big endian by default
        data[index++] = (byte) (c >> 8 & 0xff);
        data[index++] = (byte) (c & 0xff);
      } else {
        if (doAlignment) {
          align(2);
        }
        unaligned_write_short((short) c);
      }
      break;

    default:
        throw new CODESET_INCOMPATIBLE("Bad codeset - hex: " + Integer.toHexString(codeSet) + " dec: " + codeSet);
    }
  }


}

