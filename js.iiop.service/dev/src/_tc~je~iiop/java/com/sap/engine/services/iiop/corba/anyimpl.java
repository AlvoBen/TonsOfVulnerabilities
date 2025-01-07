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
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

/* The following two classes represent an Input_ and an Output_ Streams
 created by a streamable Any object (struct, union etc.).
 The usage of such classes prevents us from using an unnecessary call
 to the TypeCode.copy() method and consequently saves time.
 */
/**
 * @author Georgy Stanev
 * @version 4.0
 */
final class AnyInputStream extends CORBAInputStream {

  AnyInputStream(org.omg.CORBA.ORB orb0, byte[] data, boolean littleEndian) {
    super(orb0, data, littleEndian);
  }

}




final class AnyOutputStream extends CORBAOutputStream {

  public AnyOutputStream(org.omg.CORBA.ORB orb0) {
    super(orb0);
  }

  public InputStream create_input_stream() {
    return new AnyInputStream(orb, data, littleEndian);
  }

}




/* This is the implementation of the org.omg.CORBA.Any abstract class, which
 can represent any data that can be described in IDL or any IDL primitive type.
 An Any object is used as a component of a NamedValue object, which provides
 information about arguments or return values in requests, and which is used to
 define name/value pairs in Context objects.
 */
public final class AnyImpl extends Any {

  private TypeCodeImpl typeCode;
  private transient CORBAInputStream stream;
  private long value;
  private transient java.lang.Object object;
//  private static final int DEFAULT_BUFFER_SIZE = 32;
  static boolean isStreamed[] = {
  //null   void   short  long   ushort ulong  float  double boolean
    false, false, false, false, false, false, false, false, false,
  //char   octet  any   TypeCode Principal objref struct union enum
    false, false, true, false,   true,     false, true,  true, true,
  //string sequence array alias except longlong ulonglong longdouble
    false, true,    true, true, true,  false,   false,    false,
  //wchar  wstring fixed  value  value_box nativ  abstract_interface
    false, false,  false, false, false,    false, false

  };
  transient org.omg.CORBA.ORB orb;

  public AnyImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_null);
  }

  AnyImpl(org.omg.CORBA.ORB orb0, Any obj) {
    this(orb0);
    if ((obj instanceof AnyImpl)) {
      AnyImpl objImpl = (AnyImpl) obj;
      typeCode = objImpl.typeCode;
      value = objImpl.value;
      object = objImpl.object;

      if (objImpl.stream != null) {
        stream = objImpl.stream.dup();
      }
    } else {
      read_value(obj.create_input_stream(), obj.type());
    }
  }

  public TypeCode type() {
    return typeCode;
  }

  public void type(TypeCode tc) {
    typeCode = TypeCodeImpl.convertToNative(orb, tc);
    stream = null;
    value = 0;
    object = null;
  }

  public boolean equal(Any otherAny) {
    if (!typeCode.equal(otherAny.type())) {
      return false;
    }

    switch (typeCode.kind().value()) {
      // handle primitive types
      case TCKind._tk_null:
      case TCKind._tk_void: {
        return true;
      }
      case TCKind._tk_short: {
        return (extract_short() == otherAny.extract_short());
      }
      case TCKind._tk_long: {
        return (extract_long() == otherAny.extract_long());
      }
      case TCKind._tk_ushort: {
        return (extract_ushort() == otherAny.extract_ushort());
      }
      case TCKind._tk_ulong: {
        return (extract_ulong() == otherAny.extract_ulong());
      }
      case TCKind._tk_float: {
        return (extract_float() == otherAny.extract_float());
      }
      case TCKind._tk_double: {
        return (extract_double() == otherAny.extract_double());
      }
      case TCKind._tk_boolean: {
        return (extract_boolean() == otherAny.extract_boolean());
      }
      case TCKind._tk_char: {
        return (extract_char() == otherAny.extract_char());
      }
      case TCKind._tk_octet: {
        return (extract_octet() == otherAny.extract_octet());
      }
      case TCKind._tk_any: {
        return extract_any().equal(otherAny.extract_any());
      }
      case TCKind._tk_TypeCode: {
        return extract_TypeCode().equal(otherAny.extract_TypeCode());
      }
      case TCKind._tk_string: {
        return extract_string().equals(otherAny.extract_string());
      }
      case TCKind._tk_longlong: {
        return (extract_longlong() == otherAny.extract_longlong());
      }
      case TCKind._tk_ulonglong: {
        return (extract_ulonglong() == otherAny.extract_ulonglong());
      }
      case TCKind._tk_enum: {
        return (create_input_stream().read_long() == otherAny.create_input_stream().read_long());
      }

        // extended types
      case TCKind._tk_wchar: {
        return (extract_wchar() == otherAny.extract_wchar());
      }
      case TCKind._tk_wstring: {
        return extract_wstring().equals(otherAny.extract_wstring());
      }
      // [TODO] implement
      case TCKind._tk_longdouble: {
        throw new org.omg.CORBA.NO_IMPLEMENT("ID019000");
      }


      // [TODO] not yet implemented for complex types
      case TCKind._tk_objref:
      case TCKind._tk_Principal:
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_sequence:
      case TCKind._tk_array:
      case TCKind._tk_alias:
      case TCKind._tk_value:
      case TCKind._tk_value_box:
      case TCKind._tk_except: {
        throw new org.omg.CORBA.NO_IMPLEMENT("ID019001");
      }
    }

    return false;
  }

  public OutputStream create_output_stream() {
    return new AnyOutputStream(orb);
  }

  public InputStream create_input_stream() {
    if (stream != null) {
      return stream.dup();
    }

    CORBAOutputStream os = new CORBAOutputStream(orb);
    TCUtility.marshalIn(os, typeCode.kind().value(), value, object);
    return os.create_input_stream();
  }

  public void read_value(InputStream in, TypeCode tc) {
    typeCode = TypeCodeImpl.convertToNative(orb, tc);
    int type = typeCode.kind().value();

    if (AnyImpl.isStreamed[type]) {
      if (in instanceof AnyInputStream) {
        stream = (CORBAInputStream) in;
        //        if (stream.getORB() == orb) {
        //          return;
        //        }
      } else {
        CORBAOutputStream out = new CORBAOutputStream(orb);
        typeCode.copy(in, out);
        stream = (CORBAInputStream) out.create_input_stream();
      }
    } else {
      java.lang.Object[] objholder = new java.lang.Object[1];
      objholder[0] = object;
      long[] longholder = new long[1];
      TCUtility.unmarshalIn(in, type, longholder, objholder);
      value = longholder[0];
      object = objholder[0];
      stream = null;
    }
  }

  public void write_value(OutputStream out) {
    if (stream != null) {
      typeCode.copy(stream.dup(), out);
    } else {
      TCUtility.marshalIn(out, typeCode.kind().value(), value, object);
    }
  }

  public void insert_Streamable(Streamable s) {
    AnyOutputStream os = new AnyOutputStream(orb);
    s._write(os);
    read_value(os.create_input_stream(), s._type());
  }

  ///////////////////////////////////////////////////////////////////////////
  // insertion/extraction/replacement for all basic types
  public void insert_short(short s) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_short);
    value = s;
  }

  public short extract_short() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_short))) {
      throw new BAD_OPERATION("ID019002");
    }

    return (short) value;
  }

  public void insert_long(int l) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_long);
    value = l;
  }

  public int extract_long() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_long))) {
      throw new BAD_OPERATION("ID019003");
    }

    return (int) value;
  }

  public void insert_ushort(short s) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ushort);
    value = s;
  }

  public short extract_ushort() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_ushort))) {
      throw new BAD_OPERATION("ID019004");
    }

    return (short) value;
  }

  public void insert_ulong(int l) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ulong);
    value = l;
  }

  public int extract_ulong() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_ulong))) {
      throw new BAD_OPERATION("ID019005");
    }

    return (int) value;
  }

  public void insert_float(float f) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_float);
    value = Float.floatToIntBits(f);
  }

  public float extract_float() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_float))) {
      throw new BAD_OPERATION("ID019006");
    }

    return Float.intBitsToFloat((int) value);
  }

  public void insert_double(double d) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_double);
    value = Double.doubleToLongBits(d);
  }

  public double extract_double() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_double))) {
      throw new BAD_OPERATION("ID019007");
    }

    return Double.longBitsToDouble(value);
  }

  public void insert_longlong(long l) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_longlong);
    value = l;
  }

  public long extract_longlong() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_longlong))) {
      throw new BAD_OPERATION("ID019008");
    }

    return value;
  }

  public void insert_ulonglong(long l) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_ulonglong);
    value = l;
  }

  public long extract_ulonglong() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_ulonglong))) {
      throw new BAD_OPERATION("ID019009");
    }

    return value;
  }

  public void insert_boolean(boolean b) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_boolean);
    value = (b) ? 1 : 0;
  }

  public boolean extract_boolean() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_boolean))) {
      throw new BAD_OPERATION("ID019010");
    }

    return (value != 0);
  }

  public void insert_char(char c) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_char);
    value = c;
  }

  public char extract_char() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_char))) {
      throw new BAD_OPERATION("ID019011");
    }

    return (char) value;
  }

  public void insert_wchar(char c) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_wchar);
    value = c;
  }

  public char extract_wchar() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_wchar))) {
      throw new BAD_OPERATION("ID019012");
    }

    return (char) value;
  }

//  public void insert_wchar(char c) {
//    throw new org.omg.CORBA.NO_IMPLEMENT("ID019012");
//  }
//
//  public char extract_wchar() {
//    throw new org.omg.CORBA.NO_IMPLEMENT("ID019013");
//  }

  public void insert_octet(byte b) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_octet);
    value = b;
  }

  public byte extract_octet() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_octet))) {
      throw new BAD_OPERATION("ID019014");
    }

    return (byte) value;
  }

  public void insert_string(String s) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_string);
    object = s;
  }

  public String extract_string() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_string))) {
      throw new BAD_OPERATION("ID019015");
    }

    return (String) object;
  }

  public void insert_wstring(String s) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_wstring);
    object = s;
  }

  public String extract_wstring() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_wstring))) {
      throw new BAD_OPERATION("ID019016");
    }

    return (String) object;
  }

//  public void insert_wstring(String s) {
//    throw new org.omg.CORBA.NO_IMPLEMENT("ID019016");
//  }
//
//  public String extract_wstring() {
//    throw new org.omg.CORBA.NO_IMPLEMENT("ID019017");
//  }

  public void insert_any(Any a) {
    AnyOutputStream os = new AnyOutputStream(orb);
    os.write_any(a);
    read_value(os.create_input_stream(), TypeCodeImpl.get_primitive_tc(TCKind.tk_any));
  }

  public Any extract_any() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_any))) {
      throw new BAD_OPERATION("ID019018");
    }

    return stream.dup().read_any();
  }

  public void insert_Object(org.omg.CORBA.Object o) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_objref);
    object = o;
  }

  public void insert_Object(org.omg.CORBA.Object o, TypeCode tc) {
    try {
      if (tc.id().equals("IDL:omg.org/CORBA/Object:1.0") || o._is_a(tc.id())) {
        typeCode = TypeCodeImpl.convertToNative(orb, tc);
        object = o;
      } else {
        throw new BAD_OPERATION("ID019019");
      }
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("AnyImpl.insert_Object(org.omg.CORBA.Object, TypeCode)", LoggerConfigurator.exceptionTrace(ex));
      }
      throw new BAD_OPERATION("ID019020");
    }
  }

  public org.omg.CORBA.Object extract_Object() {
    // Check if the object contained here is of the type in typeCode
    try {
      org.omg.CORBA.Object obj = (org.omg.CORBA.Object) object;

      if (typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") || obj._is_a(typeCode.id())) {
        return obj;
      } else {
        throw new BAD_OPERATION("ID019021");
      }
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("AnyImpl.extract_Object", LoggerConfigurator.exceptionTrace(ex));
      }
      throw new BAD_OPERATION("ID019022");
    }
  }

  public void insert_Value(java.io.Serializable v) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_value);
    if (v == null) {
      return;
    }

    object = v;
  }

  public void insert_Value(java.io.Serializable v, TypeCode tc) throws MARSHAL {
    typeCode = TypeCodeImpl.convertToNative(orb, tc);
    if (v == null) {
      return;
    }

    object = v;
  }

  public java.io.Serializable extract_Value() throws org.omg.CORBA.BAD_OPERATION {
//    int _tk = typeCode.kind().value();
    //    if (_tk != TCKind.tk_value.value() && _tk != TCKind.tk_value_box.value() && _tk != TCKind.tk_null.value()) {
    //      throw new BAD_OPERATION("ID019023");
    //    } else {
    return (java.io.Serializable) object;
    //    }
    //    return null;
  }

  public void insert_TypeCode(TypeCode tc) {
    typeCode = TypeCodeImpl.get_primitive_tc(TCKind.tk_TypeCode);
    object = tc;
  }

  public TypeCode extract_TypeCode() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_TypeCode))) {
      throw new BAD_OPERATION("ID019024");
    }

    return (TypeCode) object;
  }

  public void insert_Principal(Principal p) {
    //
    // Anys are mutable so we need to copy the data by marshaling.
    //
    AnyOutputStream os = new AnyOutputStream(orb);
    os.write_Principal(p);
    read_value(os.create_input_stream(), TypeCodeImpl.get_primitive_tc(TCKind.tk_Principal));
  }

  public Principal extract_Principal() {
    if (!typeCode.equal(TypeCodeImpl.get_primitive_tc(TCKind.tk_Principal))) {
      throw new BAD_OPERATION("ID019025");
    }

    return stream.dup().read_Principal();
  }

}

