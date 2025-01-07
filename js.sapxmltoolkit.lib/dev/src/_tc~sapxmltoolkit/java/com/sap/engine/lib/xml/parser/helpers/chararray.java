package com.sap.engine.lib.xml.parser.helpers;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Symbols;
import com.sap.engine.lib.xml.util.Convert;
import com.sap.engine.lib.xml.util.SymbolTable;

/**
 * <p>
 * This class attempts to help overcome the difficulties that
 * <tt>java.lang.String</tt> causes when developing applications with
 * intensive string processing.
 * </p>
 * <p>
 * <tt>CharArray</tt> is mutable. The programming freedom which such
 * mutability provides for the sake of higher effectiveness, makes it
 * somewhat error-prone. Special attention has been paid to situations in
 * which unpredicted behaviour might mislead users of this class, and an effort
 * has been made to try to avoid it.
 * </p>
 * <p>
 * The general approach in selecting names for methods was to have the same
 * (or at least similar) names of methods as <tt>java.lang.String</tt>
 * though that often is not the case.
 * </p>
 * <p>
 * A special pooling structure can be attached to instances of <tt>CharArray</tt>
 * in order to save objects. It pools the <tt>CharArray</tt>s themselves,
 * not the primitive <tt>char[]</tt>s.
 * </p>
 * <p>
 * Two or more <tt>CharArray</tt> objects can share a single <tt>char[]</tt>
 * in the same way two or more <tt>java.lang.String</tt>s can be shared. This is the
 * result of performing the <tt>substring</tt> method.
 * One should be careful with mutability in such cases, as changing the inner
 * instance would cause changes in the original instance as well.
 * </p>
 *
 * @author   Vladimir Savtchenko, e-mail: vlast@usa.net
 * @version  1.00
 */
public final class CharArray implements java.io.Serializable {

  /**
   * A zero-length <tt>char[]</tt>
   */
  public static final char[] EMPTYDATA = new char[0];
  /**
   * An empty <tt>CharArray</tt>
   */
  public static final CharArray EMPTY = new CharArray(EMPTYDATA).setStatic();
  /**
   * Has only a getter and a setter.
   */
  private boolean isStatic = false;
  /**
   * Contains the sequence of characters which the <tt>CharArray</tt> represents.
   * The sequence does not necessarily start at index [0], and does not
   * necessarily stretch to the end of the array.
   */
  private char[] data = null;
  /**
   * Offset in <tt>data</tt> from which the sequence begins
   */
  private int offset = 0;
  /**
   * Length of the sequence
   */
  private int size;
  /**
   *
   */
  private char[] orgdata = null;
  /**
   * Resize step, which might grow
   */
  private int ext = 10;
  /**
   * Becomes true when the hash code for this object has been computed and
   * it is not necessary to compute it again. This can be very useful for
   * constant <tt>CharArray</tt>s in hashtables and hash sets, but
   * may cause confusion when the <tt>CharArray</tt> object's data is
   * directly changed (and therefore the hash code is changed).
   */
  public boolean hashReady = false;
  /**
   * Saves the value of the hash code for this object to avoid recomputation
   * @see #hashReady
   */
  private int hash = 0;

  /**
   * Uses default values for the capacity and the resize step.
   */
  public CharArray() {
    size = 0;
    ext = 16;
    orgdata = data = EMPTYDATA;
  }

  /**
   * Takes a copy of the whole parameter as an initial value.
   * @param         buf    initial value
   */
  public CharArray(char[] buf) {
    int l = buf.length;
    size = ext = l;
    orgdata = data = new char[l];
    System.arraycopy(buf, 0, data, 0, l);
  }

  /**
   * Initializer with a <tt>java.lang.String</tt> for compatibility.
   * @param         str    initial value, or null to obtain an empty <tt>CharArray</tt>
   */
  public CharArray(String str) {
    size = 0;
    ext = 16;

    if ((str != null) && (str.length() > 0)) {
      orgdata = data = new char[str.length()];
      append(str);
      strRpr = str;
      strHash = hashCode();
      hash = strHash;      
    } else {
      orgdata = data = EMPTYDATA;
    }
  }

  /**
   * Copy-constructor. The parameter and the object constructed do
   *                   <strong>not</strong> share resources.
   * @param         ca     initial value, or null to obtain an empty <tt>CharArray</tt>
   */
  public CharArray(CharArray ca) {
    this.ext = 16;

    if (ca != null) {
      size = ca.size;
      orgdata = data = new char[size];
      System.arraycopy(ca.data, ca.offset, data, 0, size);
      if (ca.hashReady){
        strRpr = ca.strRpr;
        strHash = ca.hashCode();
        hash = strHash;   
      }
    } else {
      size = 0;
      orgdata = data = EMPTYDATA;
    }
  }

  /**
   * Constructor setting the initial resize step.
   * No checking for valid parameters.
   * @param       ext      resize step
   */
  public CharArray(int ext) {
    size = 0;
    this.ext = ext;
    orgdata = data = EMPTYDATA;
  }

  /**
   * Constructor setting the initial resize step and the initial size.
   * No checking for valid parameters.
   * @param       ext      resize step
   * @param       initSize initial size
   */
  public CharArray(int ext, int initSize) {
    size = 0;
    this.ext = ext;
    orgdata = data = new char[initSize];
  }

  /**
   * Allows re-initialization for the needs of the pooling technique.
   * @return this
   */
  public CharArray reuse() {
    clear();
    return this;
  }

  /**
   * Allows re-initialization, similar to the copy-constructor.
   * @param         str    initial value, or null to clear this <tt>CharArray</tt>
   * @return this
   *
   * @see #CharArray(CharArray)
   */
  public CharArray set(CharArray cr) {
    clear();
    append(cr);
    if ((cr!=null) && (cr.hashReady)){
      strRpr = cr.strRpr;
      strHash = cr.hashCode();
      hash = strHash;
    }
    return this;
  }

  /**
   * Allows re-initialization, similar to the respective constructor.
   * @param         str    initial value, or null to clear this <tt>CharArray</tt>
   * @return this
   *
   * @see #CharArray(String)
   */
  public CharArray set(String str) {
    clear();
    append(str);
    if (str!=null){
      strRpr = str;
      strHash = hashCode();
      hash = strHash;
    }
    return this;
  }

  public CharArray set(StringBuffer b) {
    clear();
    int l = b.length();

    if (data.length < l) {
      data = new char[l + 100];
    }

    b.getChars(0, l, data, 0);
    offset = 0;
    size = l;
    hashReady = false;
    return this;
  }

  public CharArray setNullTerminated(char[] s, int p) {
    clear();
    int i;

    for (i = p; s[i] != 0; i++) {
      append(s[i]);
    } 

    size = i - p;
    return this;
  }

  /**
   * Appends the parameter to the end of the <tt>CharArray</tt>.
   * @param cr to be appended; if null the <tt>CharArray</tt> is not modified
   * @return this
   */
  public CharArray append(CharArray cr) {
    if (cr == null) {
      return this;
    }

    hashReady = false;
    assertDataLen(cr.size);
    System.arraycopy(cr.data, cr.offset, data, size, cr.size);
    resizeWithLen(cr.size);
    return this;
  }

  /**
   * Appends the parameter to the end of the <tt>CharArray</tt>.
   * @param cr to be appended; if null the <tt>CharArray</tt> is not modified
   * @return this
   */
  public CharArray append(String str) {
    if (str == null) {
      return this;
    }

    strRpr = null;
    hashReady = false;
    int l = str.length();
    assertDataLen(l);
    str.getChars(0, l, data, offset + size);
    resizeWithLen(l);
    return this;
  }

  public CharArray set(byte[] array, int offset, int length) {
    clear();
    assertDataLen(length);
    int i = 0;
    int j = offset;

    while (i < length) {
      data[i] = (char) array[j];
      i++;
      j++;
    }
    
    size = length;
    return this;
  }

  /**
   * Appends the parameter as a decimal sequence of characters to the end
   * of the <tt>CharArray</tt>.
   * @param num to be appended
   */
  public void appendInteger(int num) {
    hashReady = false;
    assertDataLen(4);
    Convert.writeIntToCharArr(data, offset + size, num);
    resizeWithLen(4);
  }

//  private int faSize;
//  private int faDataLength;
//  public void startFastAppend() {
//    faSize = offset + size;
//    faDataLength = data.length;
//  }
//  
//  public void fastAppend(char ch) {
//    if (faSize == faDataLength) {
//      resizeDefault();
//      faDataLength = data.length;
//    }
//    data[faSize++] = ch;
//  }
//  
//  public void endFastAppend() {
//    size = faSize - offset;
//    hashReady = false;
//  }
  
  /**
   * Appends the parameter to the end of the <tt>CharArray</tt>.
   * @param ch to be appended
   */
  public CharArray append(char ch) {
    hashReady = false;

    if (offset + size == data.length) {
      resizeDefault();
    }

    data[offset + (size++)] = ch;
    return this;
  }

  // Getters & setters
  public CharArray setStatic() {
    isStatic = true;
    //Thread.dumpStack();
    bufferHash();
    return this;
  }

  public boolean getStatic() {
    return isStatic;
  }

  /**
   * Equivalent to <tt>java.lang.String.charAt(int)</tt>.
   * No bounds checking, might raise an <tt>ArrayIndexOutOfBoundsException</tt>.
   *
   * @param index the (offset-based) position of the character to get
   * @returns the character at (offset-based) position <tt>index</tt>
   */
  public char charAt(int index) {
    return data[index + offset];
  }

  /**
   * Replaces the <tt>index</tt>th character with <tt>ch</tt>.
   * Does bounds checking.
   * If <tt>index</tt> is greater than or equal to the current length,
   * resizing is performed.
   *
   * @param index the (offset-based) position of the character to get
   * @param ch the new value of the <tt>index</tt>th character
   */
  public void setCharAt(int index, char ch) {
    if (index < 0) {
      throw new IllegalArgumentException();
    }

    hashReady = false;
    strRpr = null;

    if (index >= size) {
      resizeDefault(index + 1);
    }

    data[offset + index] = ch;
  }

  public char[] getData() {
    return data;
  }

  public int getOffset() {
    return offset;
  }

  public int getSize() {
    return size;
  }

  /**
   * Sets the length with no bound checking.
   */
  public void setSize(int value) {
    hashReady = false;
    size = value;
  }

  // Miscellaneous
  /**
   * Makes the length of this <tt>CharArray</tt> zero and
   * sets a default value for the resize step.
   */
  public void clear() {
    size = 0;
    offset = 0;
    ext = data.length / 10;
    ext = (ext < 10) ? 10 : data.length;
    data = orgdata;
    hashReady = false;
    strRpr = null;
  }

  public void clear(int start, int end) {
    if (start < 0 || end < 0 || start >= size || end > size || start > end - 1) {
      throw new IllegalArgumentException("Illegal arguments start or end!");
    }

    char[] newData = new char[size + offset - (end - start)];
    System.arraycopy(data, 0, newData, 0, start + offset);
    System.arraycopy(data, end + offset, newData, start + offset, size - end);
    data = newData;
    size -= (end - start);
    strRpr = null;
  }

  /**
   * For compatibility with <tt>java.lang.String</tt>.
   * @return the <tt>java.lang.String</tt> equivalent of this
   * <tt>CharArray</tt>.
   */
   
  private String strRpr = null;
  private int strHash = 0;

  public String getStringFast() {
    strRpr = new String(data, offset, size);
    return strRpr;
  }
  
  public String getString() {
    if (!hashReady || strRpr == null) {
      hash = hashCode();
      hashReady = true;
      strRpr = new String(data, offset, size);    
    }

    return strRpr;
    
    
//    return new String(data, offset, size);
  }

  /**
   * Uses <code>st</code> to cache the created string instance.
   * @param st
   * @return
   */
  public String getString(SymbolTable st) {
    int newHash = hashCode();
    if (strHash != newHash || strRpr == null) {
      strHash = newHash;
      hash = newHash;
      hashReady = true;
//    strRpr = new String(data, offset, size);
      strRpr = st.addSymbol(data, offset, size);
    }

    return strRpr;    
  }

  /**
   * For compatibility with <tt>java.lang.String</tt>.
   * @return the <tt>java.lang.String</tt> equivalent of this
   * <tt>CharArray</tt>.
   */
  public String toString() {
    return getString();
  }

  /**
   * @returns a new <tt>char[]</tt> containing the actual sequence of
   * characters which this <tt>CharArray</tt> represents
   */
  public char[] getChars() {
    char r[] = new char[size];
    System.arraycopy(data, offset, r, 0, size);
    return r;
  }

  /**
   * @returns a new <tt>byte[]</tt> containing the actual sequence of
   * characters (trimmed to bytes) which this <tt>CharArray</tt> represents
   */
  public byte[] getBytes() {
    byte[] r = new byte[size];

    for (int i = offset; i < size; i++) {
      r[i] = (byte) data[i + offset];
    } 

    return r;
  }

  /**
   * @param c a <tt>char[]</tt> in which the actual sequence of
   * characters this <tt>CharArray</tt> represents is returned
   */
  public void getChars(char[] c) {
    System.arraycopy(data, offset, c, 0, size);
  }

  /**
   * @param c a <tt>char[]</tt> in which the actual sequence of
   * characters this <tt>CharArray</tt> represents is returned
   */
  public void getChars(char[] c, int start) {
    System.arraycopy(data, offset, c, start, size);
  }

  /**
   * Resizes if necessary to make sure that at least <tt>len</tt>
   * character cells are available
   * @param len the minimum length required
   */
  public void assertDataLen(int len) {
    if (data.length < len + size + offset) {
      if (data.length + ext > len + size + offset) {
        resizeDefault();
      } else {
        resizeDefault(10 + len - data.length + size - offset);
      }
    }
  }

  /**
   * Resizes with an amount of <tt>ext</tt> (which is the current resize step)
   * <tt>char</tt>s and doubles it.
   */
  public void resizeDefault() {
    resizeDefault(ext);
    //ext *= 2;
  }

  /**
   * Resizes with an amount of <tt>e</tt> <tt>char</tt>s.
   * @param e the number of character cells to be added
   */
  public void resizeDefault(int e) {
    char[] data1 = new char[data.length + e];
    System.arraycopy(data, 0, data1, 0, data.length);
    orgdata = data = data1;
    ext = data.length;
  }

  /**
   * Simply increases the variable containing the length of the <tt>CharArray</tt>
   * without dealing with the capacity of the underlying <tt>char[]</tt>.
   *
   * @deprecated
   */
  public void resizeWithLen(int len) {
    size += len;
  }

  /**
   * Forces re-computation of the hash code.
   * @see #hashCode()
   */
  public void bufferHash() {
    hashReady = false;
    hash = hashCode();
    hashReady = true;
  }

  public void clearHashReady() {
    hashReady = false;
  }

  private boolean uniqueHash = false;

  /**
   * Computes or re-computes the proper hash code for this object so that it
   * is not necessary to compute it again. This can be very useful for
   * constant <tt>CharArray</tt>s in hashtables and hash sets, but
   * may cause confusion when the <tt>CharArray</tt> object's data is
   * directly changed (and therefore the hash code is changed).
   * @returns the hash code
   */
  public int hashCode() {
    if (hashReady) {
      return hash;
    }
    uniqueHash = false;

    int h = 0;
    int end = offset + size;

    if (size < 5) {
      uniqueHash = true;
      for (int i = offset; i < end; i++) {
        if ((data[i] & 0xFF00) == 0) {
          h = (h << 8) + data[i];
        } else {
          uniqueHash = false;
          break;
        }
      }
    }
    
    if (!uniqueHash) {
      for (int i = offset; i < end; i++) {
        h = (h << 5) - h + data[i];
        //h = 31 * h + data[i];
      } 
    }

    return h;
  }

  /**
   * Equality checking.
   * The java specification recommends that this method should be an
   * "equality relation" which means (for all objects a, b and c) :
   * <ul/> a.equals(a)
   * <ul/> if a.equals(b) then b.equals(a)
   * <ul/> if a.equals(b) and b.equals(c) then a.equals(c)
   * Nevertheless this method does not quite comply to that.
   * For the sake of convenience it accepts instances of <tt>java.lang.String</tt>
   * as parameters and performs the comparison correctly.
   * So <tt>CharArray.equals(String)</tt> works but
   * <tt>String.equals(CharArray)</tt> does not.
   *
   * @param anObject to compare with - either <tt>CharArray</tt> or <tt>java.lang.String</tt>
   * @return <tt>true</tt> if <tt>this</tt> and <tt>anObject</tt>
   *           represent the same sequence of characters,
   *           and <tt>false</tt> otherwise
   */
  public boolean equals(Object anObject) {
    if (this == anObject) {
      return true;
    }

    if (anObject instanceof CharArray) {
      return equalsCharArray((CharArray) anObject);
    } else if (anObject instanceof String) {
      String aa = (String) anObject;
      if (getSize() != aa.length()) {
        return false;
      }
      int s = getSize();

      for (int i = 0; i < s; i++) {
        if (charAt(i) != aa.charAt(i)) {
          return false;
        }
      } 

      return true;
    } else {
      return false;
    }
  }

  /**
   * No cast needed.
   */
  public boolean equalsCharArray(CharArray other) {
    if (this == other) {
      return true;
    }

    if (this.size != other.size) {
      return false;
    } else if (size < 5) {
      if (!this.isStatic) {
      this.bufferHash();
      }
      if (!other.isStatic) {
      other.bufferHash();
      }
      if (this.hashReady && other.hashReady && this.uniqueHash && other.uniqueHash) {
        if (this.hash == other.hash) {
          return true;
        }
      }
    }
    
 //   LogWriter.getSystemLogWriter().println("CharArray.equalsCharArray(): this=" + this.getString() + ", other=" + other.getString());


    if (this.hashCode() != other.hashCode()) {
      return false;
    }

    int n = size;
    char otherData[] = other.data;
    int i = offset;
    int j = other.offset;

    while (n-- > 0) {
      if (data[i++] != otherData[j++]) {
        return false;
      }
    }

    return true;
  }

  public boolean equals(char[] c, int start) {
    int end = offset + size;
    int j = start;

    for (int i = offset; i < end; i++, j++) {
      if (data[i] != c[j]) {
        return false;
      }
    } 

    return c[j] == (char) 0;
  }

  public boolean equalsIgnoreCase(Object anObject) {
    if (this == anObject) {
      return true;
    }

    if (anObject instanceof String) {
      String s = (String) anObject;

      if (size != s.length()) {
        return false;
      }

      for (int i = 0; i < size; i++) {
        if (Character.toLowerCase(data[offset + i]) != Character.toLowerCase(s.charAt(i))) {
          return false;
        }
      } 

      return true;
    }

    if (anObject instanceof CharArray) {
      if (size != ((CharArray) anObject).size) {
        return false;
      }
      int n = size;
      char v2[] = ((CharArray) anObject).data;
      int v2off = ((CharArray) anObject).offset;

      for (int i = 0; n-- != 0; i++) {
        if (Character.toLowerCase(data[i + offset]) != Character.toLowerCase(v2[i + v2off])) {
          return false;
        }
      } 

      return true;
    } else {
      return false;
    }
  }

  // Not called during XSLTMark's tests
  public boolean isWhitespace() {
    int end = offset + size;

    for (int i = offset; i < end; i++) {
      if (!Symbols.isWhitespace(data[i])) {
        return false;
      }
    } 

    return true;
  }

  // Not called during XSLTMark's tests
  public boolean isName() {
    if ((size == 0) || (!Symbols.isInitialNameChar(data[offset]))) {
      return false;
    }

    int end = offset + size;

    for (int i = offset + 1; i < end; i++) {
      if (!Symbols.isNameChar(data[i])) {
        return false;
      }
    } 

    return true;
  }

  // Not called during XSLTMark's tests
  public boolean isNmtoken() {
    if (size == 0) {
      return false;
    }

    for (int i = 0; i < size; i++) {
      if (!Symbols.isNameChar(data[i + offset])) {
        return false;
      }
    } 

    return true;
  }

  // Not called during XSLTMark's tests
  public boolean isNmtokens() {
    if (size == 0) {
      return false;
    }

    boolean atLeastOneNmtoken = false;

    for (int i = 0; i < size; i++) {
      if (Symbols.isNameChar(data[i + offset])) {
        atLeastOneNmtoken = true;
        continue;
      }

      if (Symbols.isWhitespace(data[i + offset])) {
        continue;
      }

      return false;
    } 

    return atLeastOneNmtoken;
  }

  // Called a few times during XSLTMark's tests
  public int indexOf(CharArray ca) {
    int l = size - ca.size + 1;

    for (int i = 0; i < l; i++) {
      int flag = 0;

      for (int j = 0; j < ca.size; j++) {
        if (data[i + j + offset] != ca.data[j + ca.offset]) {
          flag = 1;
        }
      } 

      if (flag == 0) {
        return i;
      }
    } 

    return -1;
  }

  // Not called during XSLTMark's tests
  public int indexOf(String ca) {
    int l = size - ca.length();

    LABEL: for (int i = 0; i <= l; i++) {
      for (int j = 0; j < ca.length(); j++) {
        if (data[i + j + offset] != ca.charAt(j)) {
          continue  LABEL;
        }
      } 

      return i;
    } 

    return -1;
  }

  // Bottleneck! Called many times during XSLTMark's tests
  public int indexOf(char ch) {
    int end = offset + size;

    for (int i = offset; i < end; i++) {
      if (data[i] == ch) {
        return i - offset;
      }
    } 

    return -1;
  }

  public int lastIndexOf(char ch) {
    int end = offset + size;

    for (int i = end - 1; i >= 0; i--) {
      if (data[i] == ch) {
        return i - offset;
      }
    } 

    return -1;
  }

  // Bottleneck! Called many times during XSLTMark's tests
  public int indexOfColon() {
    int end = offset + size;

    for (int i = offset; i < end; i++) {
      if (data[i] == ':') {
        return i - offset;
      }
    } 

    return -1;
  }

  public void trim() { // Deletes whitespace from both sides
    hashReady = false;

    while (size > 0 && Symbols.isWhitespace(data[offset])) {
      offset++;
      size--;
    }

    while (size > 0 && Symbols.isWhitespace(data[offset + size - 1])) {
      size--;
    }

    if (size == 0) {
      clear(); // this would happen if the whole CharArray is whitespace
    }
  }

  public void trimNo13() { // Deletes whitespace from both sides, but 0xD is not counted as whiteSpace
    hashReady = false;

    while (size > 0 && Symbols.isWhitespaceNo13(data[offset])) {
      offset++;
      size--;
    }

    while (size > 0 && Symbols.isWhitespaceNo13(data[offset + size - 1])) {
      size--;
    }

    if (size == 0) {
      clear(); // this would happen if the whole CharArray is whitespace
    }
  }

  public boolean equalsTrimIgnoreCase(String s) {
    int last = offset + size - 1;

    while (offset < (offset + size) && Symbols.isWhitespace(data[offset])) {
      offset++;
    }

    while (offset <= last && Symbols.isWhitespace(data[last])) {
      last--;
    }

    int totlen = last - offset;

    if (totlen != s.length()) {
      return false;
    }

    for (int i = 0; i < totlen; i++) {
      if (Character.toLowerCase(s.charAt(i)) != Character.toLowerCase(data[i + offset])) {
        return false;
      }
    } 

    return true;
  }

  /*
   *   Checks for a Vector of CharArrays if its elements are
   * pairwise distinct.
   */
  public static boolean areDistinct(Vector v) {
    HashSet h = new HashSet(3 * v.size());

    for (Enumeration e = v.elements(); e.hasMoreElements();) {
      CharArray ca = (CharArray) e.nextElement();

      if (h.contains(ca)) {
        return false;
      }

      h.add(ca);
    } 

    return true;
  }

  public void setLength(int i) {
    setSize(i);
  }

  public int length() {
    return size;
  }

  public CharArray append(char[] newdata, int offset, int len) {
    if (newdata == null) {
      return this;
    }

    //LogWriter.getSystemLogWriter().println("newdata.len=" + newdata.length + "  offset=" + offset + " len=" + len);
    if (offset < newdata.length && len > 0) {
      hashReady = false;
      assertDataLen(len);
      try {
        System.arraycopy(newdata, offset, data, size, len);
      } catch (ArrayIndexOutOfBoundsException e) {
        //$JL-EXC$
        //performance reasons
        LogWriter.getSystemLogWriter().println(offset + "   " + len + "    " + newdata.length); //???
        e.printStackTrace();
      }
      resizeWithLen(len);
    }

    return this;
  }

  public void substring(CharArray src, int b, int e) {
    hashReady = false;
    strRpr = null;
    data = src.data;
    offset = src.offset + b;
    size = e - b;
  }

  public void substring(CharArray src, int b) {
    hashReady = false;
    strRpr = null;
    data = src.data;
    offset = src.offset + b;
    size = src.size - b;
  }

  public void set(char[] buf) {
    clear();

    if (buf != null) {
      append(buf, 0, buf.length);
    }
    
  }

  public void set(char[] buf, int start, int len) {
    clear();

    if (buf != null) {
      append(buf, start, len);
    }
    strRpr = null;
    
  }

  public void set(CharArray ch, int b, int e) {
    clear();
    e = (e < ch.getSize()) ? e : ch.getSize();
    append(ch.getData(), b + ch.offset, e - b);
  }

  public CharArray replace(char a, char b) {
    hashReady = false;
    strRpr = null;
    int end = offset + size;

    for (int i = offset; i < end; i++) {
      if (data[i] == a) {
        data[i] = b;
      }
    } 

    return this;
  }

  public CharArray replace(CharArray chars, int start, int end) {
    hashReady = false;
    strRpr = null;
    clear(start, end);
    insert(start, chars);
    return (this);
  }

  public boolean startsWith(CharArray a) {
    char[] aData = a.getData();
    int aSize = a.getSize();
    int aOffset = a.getOffset();

    if (aSize == 0 && size > 0) {
      return false;
    } else if (size == 0 && aSize > 0) {
      return false;
    }

    if (size < aSize) {
      return false;
    }

    aSize = (size < aSize) ? size : aSize;
    int end = offset + aSize;

    for (int i = offset, j = aOffset; i < end;) {
      if (data[i++] != aData[j++]) {
        return false;
      }
    } 

    return true;
  }

  public int compareTo(String a) {
    int aLength = a.length();
    aLength = (size < aLength) ? size : aLength;
    int end = offset + aLength;

    for (int i = offset; i < end; i++) {
      if (data[i] - a.charAt(i) != 0) {
        return data[i] - a.charAt(i);
      }
    } 

    if (size > aLength) {
      return data[end];
    } else if (a.length() > aLength) {
      return -a.charAt(end);
    } else {
      return 0;
    }
  }

  public int compareToIgnoreCase(CharArray a) {
    /*
     int aSize = a.getSize();
     char[] aData = a.getData();
     int aOffset = a.getOffset();
     int r;
     int minLen = (size < aSize) ? size : aSize;
     for (int i = 0; i < minLen; i++) {
     char ch  = Character.toLowerCase(data[offset + i]);
     char aCh = Character.toLowerCase(aData[aOffset + i]);
     r = ch - aCh;
     if (r != 0) return r;
     }
     if (size > minLen) return data[offset + minLen];
     else if (aSize > minLen) return -aData[aOffset + minLen];
     else return 0;
     */
    int aSize = a.getSize();
    char[] aData = a.getData();
    int aOffset = a.getOffset();
    int minLen = (size < aSize) ? size : aSize;
    int end = offset + minLen;

    for (int i = offset, j = aOffset; i < end; i++, j++) {
      if (Character.toLowerCase(data[i]) != Character.toLowerCase(aData[j])) {
        return Character.toLowerCase(data[i]) - Character.toLowerCase(aData[j]);
      }
    } 

    if (size > minLen) {
      return data[offset + minLen];
    } else if (aSize > minLen) {
      return -aData[aOffset + minLen];
    } else {
      return 0;
    }
  }

  public int compareTo(CharArray a) {
    /*
     int aSize = a.getSize();
     char[] aData = a.getData();
     int aOffset = a.getOffset();
     int r;
     int minLen = (size < aSize) ? size : aSize;
     for (int i = 0; i < minLen; i++) {
     r = data[offset + i] - aData[aOffset + i];
     if (r != 0) {
     return r;
     }
     }
     if (size > minLen) return data[offset + minLen];
     else if (aSize > minLen) return -aData[aOffset + minLen];
     else return 0;
     */
    int aSize = a.size;
    char[] aData = a.data;
    int aOffset = a.offset;
    int r;
    int minLen = (size < aSize) ? size : aSize;

    for (int i = 0; i < minLen; i++) {
      r = data[offset + i] - aData[aOffset + i];

      if (r != 0) {
        return r;
      }
    } 

    if (size > minLen) {
      return data[offset + minLen];
    } else if (aSize > minLen) {
      return -aData[aOffset + minLen];
    } else {
      return 0;
    }
  }

  /**
   * Similar to String.toUpperCase()
   */
  public CharArray toUpperCase(CharArray buf) {
    buf.clear();
    buf.assertDataLen(size);
    char[] d = buf.getData();

    for (int i = offset, end = offset + size; i < end; i++) {
      d[i] = Character.toUpperCase(data[i]);
    } 

    buf.size = size;
    return buf;
  }

  public CharArray toUpperCase() {
    for (int i = offset, end = offset + size; i < end; i++) {
      data[i] = Character.toUpperCase(data[i]);
    } 

    return this;
  }

  public CharArray toLowerCase() {
    for (int i = offset, end = offset + size; i < end; i++) {
      data[i] = Character.toLowerCase(data[i]);
    } 

    return this;
  }

  /**
   * Clones this <tt>CharArray</tt>, taking into consideration the pool, if specified.
   */
  public CharArray copy() {
    return new CharArray(ext, size).set(this);
  }

  /**
   * Collapses sequences of whitespace characters into single spaces, and then
   * trims spaces from both sides. Modifies the object on which is invoked.
   *
   * @returns this
   */
  public CharArray normalizeSpace() {
    hashReady = false;

    while ((size > 0) && (Symbols.isWhitespace(data[offset]))) {
      offset++;
      size--;
    }

    if (size == 0) {
      return this;
    }

    int j = offset;
    boolean flag = true;
    int end = offset + size;

    for (int i = offset; i < end; i++) {
      if (Symbols.isWhitespace(data[i])) {
        if (!flag) {
          flag = true;
          data[j++] = ' ';
        }
      } else {
        data[j++] = data[i];
        flag = false;
      }
    } 

    if (data[j - 1] == ' ') {
      j--;
    }

    size = j - offset;
    return this;
  }

  /**
   * <p>
   * Searches for the ':' in this <tt>CharArray</tt> and makes
   * <tt>prefix</tt> and <tt>localName</tt> be substrings of this.
   * </p>
   * <p>
   * If there is no ':', clears <tt>prefix</tt> and makes
   * localName a substring of this from 0 to size.
   * </p>
   */
  public void parseNS(CharArray prefix, CharArray localName) {
    int p = indexOfColon();

    if (p != -1) {
      prefix.substring(this, 0, p);
      localName.substring(this, p + 1, size);
    } else {
      prefix.clear();
      localName.substring(this, 0, size);
    }
  }

  public void insert(int p, CharArray x) {
    if ((p < 0) || (p > size) || (x == null)) {
      throw new IllegalArgumentException();
    }

    assertDataLen(size + x.size);
    System.arraycopy(data, offset + p, data, offset + x.size + p, size - p);
    System.arraycopy(x.data, x.offset, data, offset + p, x.size);
    size += x.size;
  }

  public void insert(int p, String x) {
    if ((p < 0) || (p > size) || (x == null)) {
      throw new IllegalArgumentException();
    }

    assertDataLen(size + x.length());
    System.arraycopy(data, offset + p, data, offset + x.length() + p, size - p);
    x.getChars(0, x.length(), data, offset + p);
    size += x.length();
  }

  public void appendEscaped(char[] caData, int caOffset, int caLength) {
    int caEnd = caOffset + caLength;

    for (int i = caOffset; i < caEnd; i++) {
      char ch = caData[i];

      if ((ch > 256) || ((ch < 32) && (ch != '\r') && (ch != '\t') && (ch != '\n'))) {
        append("&#x");
        append(Integer.toHexString(ch));
        append(';');
        continue;
      }

      switch (ch) {
        case '\'': {
          append("&apos;");
          break;
        }
        case '\"': {
          append("&quot;");
          break;
        }
        case '&': {
          append("&amp;");
          break;
        }
        case '<': {
          append("&lt;");
          break;
        }
        case '>': {
          append("&gt;");
          break;
        }
        default: {
          append(ch);
          break;
        }
      }
    } 
  }

  // when writing XML, the 0xD should be escaped. By default if xD exists in the source, it is
  // converted to xA, so the only way for it to exist is that the the xml contains &#xD;
  public void appendEscapedNo13(char[] caData, int caOffset, int caLength) {
    int caEnd = caOffset + caLength;

    for (int i = caOffset; i < caEnd; i++) {
      char ch = caData[i];

      if ((ch > 256) || ((ch < 32) && 
      /*(ch != '\r') &&*/
      (ch != '\t') && (ch != '\n'))) {
        append("&#x");
        append(Integer.toHexString(ch));
        append(';');
        continue;
      }

      switch (ch) {
/* 

CSN: I-0002589847 2008 Reason: fix Desc: sapxmltoolkit - &apos; was generated instead of '

Optional
Add'tl description: When Indent-ing is switched on. This special method is used from CharArray. It simply escapes all characters, but for some of them it does not make sense to escape them. According to the spec when outputting Apostrophes - they do not need to be escaped, when they appear in element text.
Customer related info: none
Responsibles: Vladimir Savchenko

       case '\'': {
          append("&apos;");
          break;
        }
        case '\"': {
          append("&quot;");
          break;
        }

*/


        case '&': {
          append("&amp;");
          break;
        }
        case '<': {
          append("&lt;");
          break;
        }
        case '>': {
          append("&gt;");
          break;
        }
        default: {
          append(ch);
          break;
        }
      }
    } 
  }

  public static void main(String[] args) throws Exception {
    CharArray chars = new CharArray("asdfghjkl");
    chars.replace(new CharArray("&&&"), 0, 8);
    LogWriter.getSystemLogWriter().println(chars.toString());
  }

  public void appendBasicEscaped(char[] caData, int caOffset, int caLength) {
    int caEnd = caOffset + caLength;

    for (int i = caOffset; i < caEnd; i++) {
      char ch = caData[i];

      if ((ch > 256) || ((ch < 32) && 
      /*(ch != '\r') &&*/
      (ch != '\t') && (ch != '\n'))) {
        append("&#x");
        append(Integer.toHexString(ch));
        append(';');
        continue;
      }

      switch (ch) {
        case '&': {
          append("&amp;");
          break;
        }
        case '<': {
          append("&lt;");
          break;
        }
        case '>': {
          append("&gt;");
          break;
        }
        default: {
          append(ch);
          break;
        }
      }
    } 
  }
  
  public void clearStringValue() {
    strRpr = null;
    strHash = 0;
  }

}

