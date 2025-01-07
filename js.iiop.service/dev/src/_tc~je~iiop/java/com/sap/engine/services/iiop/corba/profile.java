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
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.CORBA.portable.CORBAOutputStream;
import com.sap.engine.interfaces.csiv2.SimpleProfileInterface;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TAG_MULTIPLE_COMPONENTS;
import org.omg.IOP.TAG_CODE_SETS;
import org.omg.IOP.TAG_JAVA_CODEBASE;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

/**
 * Implementation of CORBA Profile used by IOR class.
 * The structure of Profile is:
 * <PRE>
 *   module IIOP { // IDL extended for version 1.1
 *     struct Version {
 *       octet major;
 *       octet minor;
 *     };
 *     struct ProfileBody_1_0 { // renamed from ProfileBody
 *       Version iiop_version;
 *       string host;
 *       unsigned short port;
 *       sequence <octet> object_key;
 *     };
 *     struct ProfileBody_1_1 {
 *       Version iiop_version;.13
 *       string host;
 *       unsigned short port;
 *       sequence <octet> object_key;
 *       // Added in 1.1
 *       sequence <IOP::TaggedComponent> components;
 *     };
 *   };
 * </PRE>
 *
 * @author Georgy Stanev, Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public final class Profile {

  private byte versionMinor = GIOPMessageConstants.VERSION_MINOR;
  private byte versionMajor = GIOPMessageConstants.VERSION_MAJOR;
  private int tag;
  private String host;
  private int port;
  private byte[] object_key;
  private SimpleProfileInterface[] components = new SimpleProfileInterface[0];
  private CodeSetChooser csChooser = null;
  private boolean builtCSC = false;
  private String codebase = null;
  int length = 0;
  org.omg.CORBA.ORB orb;

  public Profile(org.omg.CORBA.ORB orb0, int tag0, String host0, int port0, byte[] objkey) {
    orb = orb0;
    tag = tag0;
    host = host0;
    port = port0;
    object_key = objkey;
  }

  public Profile(org.omg.CORBA.ORB orb0, int tag0, byte[] data) {
    orb = orb0;
    tag = tag0;
    CORBAInputStream is = new CORBAInputStream(orb0, data);
    is.setEndian(is.read_boolean());
    loadComponents(tag, is);
  }

  public Profile(org.omg.CORBA.ORB orb0, int tag0, CORBAInputStream is) {
    orb = orb0;
    tag = tag0;
    is.beginEncapsulation();
    loadComponents(tag, is);
    is.endEncapsulation();
  }

  private SimpleProfile buildCSComponent() {
    CORBAOutputStream os = new CORBAOutputStream(orb, 128);
    os.write_boolean(os.getEndian());

    os.write_long(CodeSetChooser.charNativeCodeSet()); // char native code set
    int[] charConvertions = CodeSetChooser.charConvertions();
    os.write_long(charConvertions.length);
    for (int aCharConvertion : charConvertions) {
      os.write_long(aCharConvertion);
    }

    os.write_long(CodeSetChooser.wcharNativeCodeSet()); // wchar native code set
    int[] wcharConvertions = CodeSetChooser.wcharConvertions();
    os.write_long(wcharConvertions.length);
    for (int aWCharConvertion : wcharConvertions) {
      os.write_long(aWCharConvertion);
    }

    builtCSC = true;
    return new SimpleProfile(1, os.toByteArray_forSend(), 0, os.byteArray_forSend_length());
  }

  private void loadComponents(int tag, CORBAInputStream is) {
    switch (tag) {
      case TAG_INTERNET_IOP.value :
        versionMajor = is.read_octet();
        versionMinor = is.unaligned_read_octet();
        host = is.read_string();
        port = is.read_uShort();
        int len = is.read_long();
        object_key = new byte[len];
        is.read_octet_array(object_key, 0, len);
        if ((versionMajor < 1) || (versionMinor < 1)) {
          break;
        }
      case TAG_MULTIPLE_COMPONENTS.value :
      {
        int count = is.read_long();
        components = new SimpleProfile[count];
        for (int i = 0; i < count; i++) {
          int id = is.read_long();

          if (id == TAG_JAVA_CODEBASE.value) { // CODEBASE COMPONENT
            int length = is.beginSequence();
            byte[] codebase_bytes = new byte[length];
            is.read_octet_array(codebase_bytes, 0, length);
            components[i] = new SimpleProfile(id, codebase_bytes);
            codebase = new String(codebase_bytes, 5, length - 5 - 1); // -5 : endian & string length ; -1 : the terminating null
          } else if (id == TAG_CODE_SETS.value) { // CODE SET COMPONENT
            csChooser = new CodeSetChooser(is);
            switch (csChooser.verifyCodesets()) {
              case 0: { // OK
                components[i] = buildCSComponent();
                break;
              }
              case 1: { // INV_OBJREF    TODO
                break;
              }
              case 2: {
               throw new CODESET_INCOMPATIBLE();
              }
            }
          } else {
            components[i] = new SimpleProfile(id, is);
          }
        }
        break;
      }
      default :
        throw new IMP_LIMIT("Profile unknown : " + tag, 1, CompletionStatus.COMPLETED_NO);
    }
  }

  public String getCodebase() {
    return codebase;
  }

  public void setVersion(byte major, byte minor) {
    versionMajor = major;
    versionMinor = minor;
  }

  public byte getVersionMinor() {
    return versionMinor;
  }

  public byte getVersionMajor() {
    return versionMajor;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public int getTAG() {
    return tag;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getObjectKeyLength() {
    return object_key.length;
  }

  public byte[] getObjectKey_ForSend() {
    return object_key;
  }

  public byte[] getObjectKey() {
    byte[] b_res = new byte[object_key.length];
    System.arraycopy(object_key, 0, b_res, 0, b_res.length);
    return b_res;
  }

  public SimpleProfileInterface[] getComponents() {
    return components;
  }

  public byte[] toByteArray() {
    CORBAOutputStream os = new CORBAOutputStream(orb, 256);
    os.write_long(tag);
    write_to_stream(os);
    byte[] array = os.toByteArray_forSend();
    length = os.byteArray_forSend_length();
    return array;
  }

  public void write_to_stream(CORBAOutputStream os) {
    os.beginEncapsulation(os.getEndian());

    os.write_octet(versionMajor);
    os.write_octet(versionMinor);
    os.write_string(host);
    os.write_short((short) (port & 0x0000FFFF));
    os.write_long(object_key.length);
    os.write_octet_array(object_key, 0, object_key.length);

    if ((versionMajor >= 1) && (versionMinor > 0)) {
      if (!builtCSC) {
        if (components.length == 0) {
          components = new SimpleProfile[1];
          components[0] = buildCSComponent();
        } else {
          SimpleProfile[] temp = new SimpleProfile[components.length + 1];
          System.arraycopy(components, 0, temp, 0, components.length);
          temp[components.length] = buildCSComponent();
          components = temp;
        }
      }

      os.write_long(components.length); // COMPONENT COUNT

      if (components.length > 0) {
        for (SimpleProfileInterface aComponent : components) { // writing other components
          os.write_long(aComponent.getTag()); // write component tag ID
          os.write_long(aComponent.getData().length); // write component data length
          os.write_octet_array(aComponent.getData(), 0, aComponent.getData().length);
        }
      }
    }

    os.endEncapsulation();
  }

  public int getByteArrayLength() {
    return length;
  }

  public boolean isEquivalent(Profile p) {
    return ((versionMajor == p.versionMajor) && (versionMinor == p.versionMinor) && (tag == p.tag) && (port == p.port) && host.equals(p.host) && areArrEquals(object_key, p.object_key));
  }

  boolean areArrEquals(byte[] b1, byte[] b2) {
    if (b1 == null) {
      return (b2 == null);
    }

    if (b2 == null) {
      return false;
    }

    if (b1.length != b2.length) {
      return false;
    }

    for (int i = 0; i < b1.length; i++) {
      if (b1[i] != b2[i]) {
        return false;
      }
    }

    return true;
  }

  public void addSimpleProfile(SimpleProfileInterface simple) {
    if (components == null) {
      components = new SimpleProfileInterface[0];
    }

    SimpleProfileInterface[] newSimple = new SimpleProfile[components.length + 1];
    System.arraycopy(components, 0, newSimple, 0, components.length);
    newSimple[components.length] = simple;
    components = newSimple;
  }

  public CodeSetChooser getCsChooser() {
    return csChooser;
  }

  public static String toString(byte[] bytes, int begin, int length) {
    String hex = "0123456789ABCDEF";
    if (bytes == null) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    length = Math.min(length, bytes.length);
    int end = begin + length;

    for (int c = begin; c < end; c += 16) {
      int count = 16;
      StringBuffer text = new StringBuffer();

      for (int j = c; --count >= 0 && j < end; j++) {
        int charAsInt = ((int) bytes[j]) & 0x00FF;
        sb.append(" ").append(hex.charAt(charAsInt >> 4)).append(hex.charAt(charAsInt & 0x000F));
        char ch = charAsInt > 31 ? (char) charAsInt : '.';
        text.append(ch);
      }

      for (; --count >= -2;) {
        sb.append("   ");
      }
      sb.append(text);
      if (c < end - 16) {
        sb.append("\r\n");
      }
    }

    return sb.toString();
  }

}// Profile

