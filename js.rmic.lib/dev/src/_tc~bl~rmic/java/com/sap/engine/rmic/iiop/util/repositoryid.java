/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.rmic.iiop.util;

import com.sap.engine.rmic.log.RMICLogger;

import java.io.Serializable;

/**
 * This class is aimed to generate RepositoryID's which is helpful
 * in marshaling and unmarshaling the RMI value.
 * RepositoryID's are used to establish the identity of information
 * in the repository.
 * As specified in the CORBA Specification, a RepositoryID is
 * a string in a specific format, allowing programs to store,
 * copy and compare objects, without regard to the structure of the
 * value. There is a kind of freedom in defining this specific
 * format, though RepositoryID's are managed by certain conventions.
 * The specification defines four formats:
 * <p/>
 * 1) the one derived from OMG IDl names,it has the format:
 * IDL: <list of identifiers, separated by '/'> : <major and minor version numbers, separated by '.'>
 * <p/>
 * 2) the one that uses Java class names and Java serialization version UID's,
 * it is the RMI hashed format, thet is computed, based upon the structural
 * information of the original Java definition:
 * RMI: <class name> : <hash code> [ : <serialization version UID> ]
 * <p/>
 * 3) the one that uses DCE UUID's, in the format:
 * DCE: <printable form of the UUID> : <decimal minor version>
 * <p/>
 * 4) the one intended for short term use, the local format:
 * LOCAL: <an arbitrary string, by no convention>
 * <p/>
 * This class defines the first two.
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class RepositoryID {

  private Class cls;
  private String repositoryID;
  private String specialRepID;
  private byte[] repositoryIDbyte;
  static int index;
  private boolean processed;
  private String IDLrepID;
  private byte[] IDLrepIDbyte;
  private RepositoryIDDescriptor descriptor;

  public RepositoryID(Class cls) {
    this.cls = cls;

    if (!cls.isArray()) {
      descriptor = RepositoryIDDescriptor.get(cls);
    }
  }

  /**
   * This method returns the string to be obtained, i.e.
   * the RepositoryID as a string, in its final format
   *
   * @param cls - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static String getRMIRepositoryID(Class cls) {
    RepositoryID repID = new RepositoryID(cls);
    repID.process();

    if (cls == java.util.Date.class) {
      return "RMI:java.util.Date:2B112DD1F3049E5C:686A81014B597419";
    }

    return repID.repositoryID;
  }

  /**
   * This method returns the string to be obtained, i.e.
   * the RepositoryID as a string, in its final format.
   * A Serializable object is passed as a parameter
   *
   * @param serial - Serializable object for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static String getRMIRepositoryID(Serializable serial) {
    return getRMIRepositoryID(serial.getClass());
  }

  /**
   * This method returns the RepositoryID in its final format
   * as a byte array
   *
   * @param cls - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static byte[] getRMIRepIDByte(Class cls) {
    RepositoryID repID = new RepositoryID(cls);
    repID.process();
    return repID.repositoryIDbyte;
  }

  /**
   * This method returns the RepositoryID in its final format
   * as a byte array. A Serializable object is passed as a parameter
   *
   * @param serial - Serializable object for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static byte[] getRMIRepIDByte(Serializable serial) {
    return getRMIRepIDByte(serial.getClass());
  }

  /**
   * This method returns the string to be obtained, i.e.
   * the RepositoryID as a string, in its final format
   *
   * @param cls - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static String getIDLRepositoryID(Class cls) {
    RepositoryID repID = new RepositoryID(cls);
    repID.process();
    return repID.IDLrepID;
  }

  /**
   * This method returns the string to be obtained, i.e.
   * the RepositoryID as a string, in its final format.
   * A Serializable object is passed as a parameter
   *
   * @param serial - Serializable object for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static String getIDLRepositoryID(Serializable serial) {
    return getIDLRepositoryID(serial.getClass());
  }

  /**
   * This method returns the RepositoryID in its final format
   * as a byte array
   *
   * @param cls - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static byte[] getIDLRepIDByte(Class cls) {
    RepositoryID repID = new RepositoryID(cls);
    repID.process();
    return repID.IDLrepIDbyte;
  }

  /**
   * This method returns the RepositoryID in its final format
   * as a byte array. A Serializable object is passed as a parameter
   *
   * @param serial - Serializable object for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static byte[] getIDLRepIDByte(Serializable serial) {
    return getIDLRepIDByte(serial.getClass());
  }

  /**
   * This method processes Class cls, i.e. calls the various
   * methods in accordance with  the type of the class
   */
  private final void process() {
    if (!processed) {
      try {
        specialRepID = SpecialRepIDTable.getID(cls);

        if (cls.isArray()) {
          repositoryIDbyte = createSequenceRepID(cls);
          repositoryID = byteArrToAString(repositoryIDbyte);
        } else if (specialRepID != null && !cls.isArray()) {
          repositoryIDbyte = new byte[specialRepID.length()];

          for (int i = 0; i < specialRepID.length(); i++) {
            repositoryIDbyte[i] = (byte) specialRepID.charAt(i);
          }

          repositoryID = specialRepID;
        } else if (specialRepID == null && java.lang.Exception.class.isAssignableFrom(cls)) {
          IDLrepIDbyte = createForIDLTypes(cls, 1, 0);
          IDLrepID = byteArrToAString(IDLrepIDbyte);
          repositoryIDbyte = createForRMITypes(cls);
          repositoryID = byteArrToAString(repositoryIDbyte);
        } else {
          repositoryIDbyte = createForRMITypes(cls);
          repositoryID = byteArrToAString(repositoryIDbyte);
        }

        processed = true;
      } catch (Exception ex) {
        RMICLogger.throwing(ex);
        repositoryID = byteArrToAString(createForIDLTypes(cls, 1, 0));
      }
    }

    return;
  }

  /**
   * This method creates the hash code of the class
   *
   * @param clas - the class for which the ID is created
   * @return the computed hash code a byte array
   */
  private static byte[] createHash(Class clas, byte[] b, int off) {
    RepositoryIDDescriptor descriptor = RepositoryIDDescriptor.get(clas);
    String hashC = null;
    String uid = null;
    byte[] hashCode = b;
    try {
      long hash = descriptor.hashCode;
      long id = 0L;

      // For classes that do not implement java.io.Serializable, and for interfaces,
      // the hash code is always zero, and the RepositoryID does not
      // contain a serial version UID.
      if (clas.isInterface() || java.rmi.Remote.class.isAssignableFrom(clas) || !(java.io.Serializable.class.isAssignableFrom(clas))) {
        for (int i = 0; i < 16; i++) {
          hashCode[i + off] = 48; // '0'
        }

        return hashCode;
      }

      // Convert the computed hash code
      // (transcribed as a 16 digit upper-case hex string)
      if (hash == 0L) {
        for (int i = 0; i < 16; i++) {
          hashCode[i + off] = 48; // '0'
        }

        return hashCode;
      } else {
        if (hash == 1L) {
          for (int i = 0; i < 15; i++) {
            hashCode[i + off] = 48; // '0'
          }

          hashCode[15 + off] = 49; // '1'
          //        return hashCode;
        }

        //      else {
        id = descriptor.sUID;
        hashC = Long.toHexString(hash).toUpperCase();
        int lenHash = hashC.length();
        int dimHashC = 16 - lenHash;

        for (int i = 0; i < 16; i++) {
          if (dimHashC > 0) {
            hashCode[i + off] = 48; // '0'
            dimHashC--;
          } else {
            hashCode[i + off] = (byte) hashC.charAt(i - 16 + lenHash);
          }
        }
      }

      // If the actual serialization version UID for the Java class
      // is different from the hash code, a colon and the actual serialization
      // version UID (transcribed as a 16 digit upper-case hex string)
      // shall be appended to the RepositoryId after the hash code.
      if (hash != id) {
        hashCode[16 + off] = 58; // ':'

        if (id == 0L) {
          for (int i = 17; i < 33; i++) {
            hashCode[i + off] = 48; // '0'
          }
        } else if (id == 1L) {
          for (int i = 17; i < 32; i++) {
            hashCode[i + off] = 48; // '0'
          }

          hashCode[32 + off] = 49; // '1'
        } else {
          uid = Long.toHexString(id).toUpperCase();
          int lenUID = uid.length();
          int dimUID = 16 - lenUID;

          for (int i = 0; i < 16; i++) {
            if (dimUID > 0) {
              hashCode[i + off + 17] = 48; // '0'
              dimUID--;
            } else {
              hashCode[i + off + 17] = (byte) uid.charAt(i - 16 + lenUID);
            }
          }
        }
      }
    } catch (Exception e) {//$JL-EXC$
      RMICLogger.throwing(e);
    }
    return hashCode;
  }

  /**
   * The method that returns the RepositoryID for RMI types
   * in its final format
   *
   * @param clas - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a byte array
   */
  public static byte[] createForRMITypes(Class clas) {
    RepositoryIDDescriptor descriptor = RepositoryIDDescriptor.get(clas);
    //String hashC = null;
    //String uid = null;
    int len = clas.getName().length();
    byte[] repID = null;

    if ((java.io.Serializable.class.isAssignableFrom(clas)) && descriptor.sUID != descriptor.hashCode) {
      repID = new byte[len + 38];
    } else {
      repID = new byte[len + 21];
    }

    //    int index = 4;
    repID[0] = 82; // 'R'
    repID[1] = 77; // 'M'
    repID[2] = 73; // 'I'
    repID[3] = 58; // ':'
    repID = convertToISOLatin1(clas.getName(), repID, 4);
    repID[index] = 58; // ':'
    repID = createHash(clas, repID, index + 1);
    return repID;
  }

  /**
   * This method creates RepositoryId's for java arrays(IDL sequences)
   *
   * @param clas - the class for which the RepositoryID is created
   * @return the computed RMIRepositoryID as a string
   */
  public static byte[] createSequenceRepID(Class clas) {
    RepositoryIDDescriptor descriptor = RepositoryIDDescriptor.get(clas);
    byte[] repID = null;
    int k = 0;
    Class cls2 = clas;
    Class cls3 = null;

    while ((cls3 = clas.getComponentType()) != null) {
      k++;
      clas = cls3;
    }

    descriptor = RepositoryIDDescriptor.get(clas);

    if (clas.isPrimitive()) {
      String name = cls2.getName();
      int len = name.length();
      repID = new byte[21 + len];
      // RMIRepositoryID for primitive types as array members
      repID[0] = 82; // 'R'
      repID[1] = 77; // 'M'
      repID[2] = 73; // 'I'
      repID[3] = 58; // ':'

      for (int i = 4; i < 4 + len; i++) {
        repID[i] = (byte) name.charAt(i - 4);
      }

      repID[4 + len] = 58; // ':'

      for (int i = 5 + len; i < 21 + len; i++) {
        repID[i] = 48; // '0'
      }
    } else {
      String name = clas.getName();
      int len = name.length();

      if ((java.io.Serializable.class.isAssignableFrom(clas)) && descriptor.sUID != descriptor.hashCode) {
        repID = new byte[len + k + 40];
      } else {
        repID = new byte[len + k + 23];
      }

      // RMIRepositoryID of an array of objects
      //      int index = 4;
      repID[0] = 82; // 'R'
      repID[1] = 77; // 'M'
      repID[2] = 73; // 'I'
      repID[3] = 58; // ':'
      int i = 0;

      for (; k > 0; k--, i++) {
        repID[i + 4] = 91; // '['
      }

      repID[i + 4] = 76; // 'L'
      repID = convertToISOLatin1(clas.getName(), repID, i + 5);
      repID[index] = 59; // ';'
      repID[index + 1] = 58; // ':'
      repID = createHash(clas, repID, index + 2);
    }

    return repID;
  }

  /**
   * This method creates the RepositoryId in the IDL format.
   * The OMG IDL format defined above does not include any structural
   * information. Identity of IDL types determined for this format depends
   * on the fact that the names used in the RepositoryID being correct.
   *
   * @param clas - the class for which the RepositoryID is created
   * @param i    - major version number
   * @param j    - minor version number
   * @return the RepositoryID as a string
   */
  public static byte[] createForIDLTypes(Class clas, int i, int j) {
    String nameIDL = clas.getName();
    byte[] repID = new byte[nameIDL.length() + 8];
    //    int index = 4;
    try {
      repID[0] = 73; // 'I'
      repID[1] = 68; // 'D'
      repID[2] = 76; // 'L'
      repID[3] = 58; // ':'
      repID = convertToISOLatin1(nameIDL, repID, 4);

      if (nameIDL.endsWith("Exception")) {
        repID = replaceForEx(nameIDL, "Exception", "Ex", repID, 4);
      }

      repID[index] = 58; // ':'
      repID[index + 1] = (byte) (48 + i);
      repID[index + 2] = 46; // '.'
      repID[index + 3] = (byte) (48 + j);
    } catch (Exception e) {// $JL-EXC$
      RMICLogger.throwing(e);
    }
    return repID;
  }

  public static String signature(Class clas) {
    String sign = null;

    if (clas.isArray()) {
      int k = 0;
      Class cls3 = null;

      while ((cls3 = clas.getComponentType()) != null) {
        k++;
        clas = cls3;
      }

      StringBuffer buffer = new StringBuffer();

      for (int j = 0; j < k; j++) {
        buffer.append("[");
      }

      buffer.append(signature(clas));
      sign = buffer.toString();
    } else if (clas.isPrimitive()) {
      if (clas == Integer.TYPE) {
        sign = "I";
      } else if (clas == Byte.TYPE) {
        sign = "B";
      } else if (clas == Long.TYPE) {
        sign = "J";
      } else if (clas == Float.TYPE) {
        sign = "F";
      } else if (clas == Double.TYPE) {
        sign = "D";
      } else if (clas == Short.TYPE) {
        sign = "S";
      } else if (clas == Character.TYPE) {
        sign = "C";
      } else if (clas == Boolean.TYPE) {
        sign = "Z";
      } else if (clas == Void.TYPE) {
        sign = "V";
      }
    } else {
      StringBuffer buffer = new StringBuffer();
      buffer.append("L");
      buffer.append(clas.getName().replace('.', '/'));
      buffer.append(";");
      sign = buffer.toString();
    }

    return sign;
  }

  /**
   * A helper method for converting the class name in ISO Latin1 format.
   * Any characters not in ISO Latin1 are replaced by '\U'followed by the 4
   * hexadecimal characters (in upper case) representing the Unicode
   * value.
   *
   * @param s - the string to be converted
   * @return the string in ISO Latin1 format
   */
  public static byte[] convertToISOLatin1(String s, byte[] b, int off) {
    int len = s.length();

    if (len == 0) {
      return new byte[0];
    }

    byte[] temp = b;
    int pos = off;
    int k = 0;

    for (int i = 0; i < len; i++) {
      int c = (int) s.charAt(i);

      // Any characters not in ISO Latin 1 are replaced by '\U'
      // followed by the 4 hexadecimal characters (in upper case)
      // representing the Unicode value.
      if (k == 0 && (c > 255 || IDL_IDENTIFIER_CHARS[c] == 0)) {
        for (int j = i; j < len; j++) {
          int no = (int) s.charAt(j);

          if (no > 255 || IDL_IDENTIFIER_CHARS[no] == 0) {
            k++;
            b[off + j] = 33;
          } else {
            b[off + j] = (byte) no;
          }
        }

        temp = new byte[b.length + k * 6];
        System.arraycopy(b, 0, temp, 0, b.length);
      }

      if (b[off + i] == 33) {
        temp[pos + i] = 92; // '\'
        temp[pos + i + 1] = 92; // '\'   \\ add second '\' because the compiler doesn't like single '\'
        temp[pos + i + 2] = 85; // 'U'
        // the 4 hexadecimal characters (in upper case)
        // representing the Unicode value.
        temp[pos + i + 3] = ASCII_HEX[(c & 0xf000) >>> 12];
        temp[pos + i + 4] = ASCII_HEX[(c & 0xf00) >>> 8];
        temp[pos + i + 5] = ASCII_HEX[(c & 0xf0) >>> 4];
        temp[pos + i + 6] = ASCII_HEX[c & 0xf];
        pos += 6;
      } else {
        temp[pos + i] = (byte) c;
      }
    }

    index = len + pos;
    return temp;
  }

  /**
   * A helper method for replacing a substring of a
   * given string with another substring
   *
   * @param s  - the initial string
   * @param s1 - the string to be replaced
   * @param s2 - the string to replace s1 with
   * @return the new string, where s1 is replaced by s2
   */
  private static byte[] replaceForEx(String s, String s1, String s2, byte[] b, int off) {
    byte[] temp = b;
    int lenS1 = s1.length();
    int lenS2 = s2.length();
    int i = s.indexOf(s1);

    if (lenS1 != lenS2) {
      temp = new byte[b.length - lenS1 + lenS2];
      System.arraycopy(b, 0, temp, 0, Math.min(b.length, temp.length));
    }

    int j = 0;

    for (; j < s.length() - lenS1 + lenS2; j++) {
      if (j == i) {
        for (int k = j; k < lenS2; k++) {
          temp[off + k] = (byte) s2.charAt(k);
        }

        j += lenS2 - 1;
      } else if (b[off + j] == 46) {
        temp[off + j] = 47; // replace '.' with '/'
      } else {
        temp[off + j] = (byte) s.charAt(j);
      }
    }

    index = off + j;
    return temp;
  }

public static String convertFromISOLatin1 (String name) {
    int index = -1;
    StringBuffer buf = new StringBuffer(name);

    while ((index = buf.toString().indexOf("\\U")) != -1) {
      String str = buf.toString().substring(index + 2, index + 6);

      // convert to hexadecimal
      byte[] buffer = new byte[str.length() / 2];
      for (int i = 0, j = 0; i < str.length(); i += 2, j++) {
        buffer[j] = (byte) ((charToAscii(str.charAt(i)) << 4) & 0xF0);
        buffer[j] |= (byte) ((charToAscii(str.charAt(i + 1)) << 0) & 0x0F);
      }

      String tempStr = buf.toString();
      buf = new StringBuffer(tempStr.substring(0, index) + tempStr.substring(index + 6, tempStr.length()));
      buf.insert(index, (char) buffer[1]);
    }

    return buf.toString();
  }


  public static final String codeTable = "8859_1";

  /**
   * Char to ascii
   *
   * @param   c
   * @return
   */
  public static final byte charToAscii(char c) {
    String temp = String.valueOf(c);
    byte[] arr = nativeToAscii(temp);
    return arr[0];
  }

  /**
   * Native ot ascii
   *
   * @param   s
   * @return
   */
  public static final byte[] nativeToAscii(String s) {
    byte[] res = null;
    try {
      res = s.getBytes(codeTable);
    } catch (java.io.UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
    return res;
  }

  // This byte array serves as a container for labels to symbols,
  // thus excluding the alternative alphabets
  private static final byte[] IDL_IDENTIFIER_CHARS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1};
  // The ASCII codes of the hex symbols:
  // 0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F
  private static final byte[] ASCII_HEX = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

  /**
   * Convert byte array to ascii string
   *
   * @param b byte array
   * @return
   */
  public static final String byteArrToAString(byte[] b) {
    return new String(b);
  }

}

