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

import org.omg.CORBA.TCKind;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

/* This class, which only contains two static methods marshalIn() and unmarshalIn(),
 serves as a helper when marshaling the AnyImpl class.
 The TCUtility class is used only in the read_value (which uses the unmarshalIn()
 method) and write_value (which uses the marshalIn() method) methods of the AnyImpl
 class. The marshalIn method is used once again in the crate_input_stream() method
 of the AnyImpl class, but the usasge is in a certain way the same as in write_vlaue().
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public final class TCUtility {

  static void marshalIn(OutputStream s, int type, long l, Object o) {
    switch (type) {
      case TCKind._tk_null: {
        // nothing to write
        break;
      }
      case TCKind._tk_void: {
        // nothing to write
        break;
      }
      case TCKind._tk_short: {
        s.write_short((short) (l & 0xFFFFL));
        break;
      }
      case TCKind._tk_ushort: {
        s.write_ushort((short) (l & 0xFFFFL));
        break;
      }
      case TCKind._tk_long: {
        s.write_long((int) (l & 0xFFFFFFFFL));
        break;
      }
      case TCKind._tk_ulong: {
        s.write_ulong((int) (l & 0xFFFFFFFFL));
        break;
      }
      case TCKind._tk_float: {
        s.write_float(Float.intBitsToFloat((int) (l & 0xFFFFFFFFL)));
        break;
      }
      case TCKind._tk_double: {
        s.write_double(Double.longBitsToDouble(l));
        break;
      }
      case TCKind._tk_boolean: {
        if (l == 0) {
          s.write_boolean(false);
        } else {
          s.write_boolean(true);
        }

        break;
      }
      case TCKind._tk_char: {
        s.write_char((char) (l & 0xFFFFL));
        break;
      }
      case TCKind._tk_octet: {
        s.write_octet((byte) (l & 0xFFL));
        break;
      }
      case TCKind._tk_any: {
        s.write_any((Any) o);
        break;
      }
      case TCKind._tk_TypeCode: {
        s.write_TypeCode((TypeCode) o);
        break;
      }
      case TCKind._tk_Principal: {
        s.write_Principal((Principal) o);
        break;
      }
      case TCKind._tk_objref: {
        s.write_Object((org.omg.CORBA.Object) o);
        break;
      }
      case TCKind._tk_longlong: {
        s.write_longlong(l);
        break;
      }
      case TCKind._tk_ulonglong: {
        s.write_ulonglong(l);
        break;
      }
      case TCKind._tk_wchar: {
        s.write_wchar((char) (l & 0xFFFFL));
        break;
      }
      case TCKind._tk_string: {
        s.write_string((String) o);
        break;
      }
      case TCKind._tk_wstring: {
        s.write_wstring((String) o);
        break;
      }
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum:
      case TCKind._tk_sequence:
      case TCKind._tk_array:
      case TCKind._tk_alias:
      case TCKind._tk_except:
      case TCKind._tk_longdouble: {
        ((Streamable) o)._write(s);
        break;
      }
      case TCKind._tk_value_box:
      case TCKind._tk_native:   // TODO ??
      case TCKind._tk_value: {
        ((org.omg.CORBA_2_3.portable.OutputStream) s).write_value((java.io.Serializable) o);
        break;
      }
      case TCKind._tk_abstract_interface: {
        ((org.omg.CORBA_2_3.portable.OutputStream) s).write_abstract_interface(o);
      }
    }
  }

  static void unmarshalIn(InputStream s, int type, long[] la, Object[] oa) {
    long l = 0;
    Object o = oa[0];

    switch (type) {
      case TCKind._tk_null: {
        // Nothing to read
        break;
      }
      case TCKind._tk_void: {
        // Nothing to read
        break;
      }
      case TCKind._tk_short: {
        l = s.read_short() & 0xFFFFL;
        break;
      }
      case TCKind._tk_ushort: {
        l = s.read_ushort() & 0xFFFFL;
        break;
      }
      case TCKind._tk_long: {
        l = s.read_long() & 0xFFFFFFFFL;
        break;
      }
      case TCKind._tk_ulong: {
        l = s.read_ulong() & 0xFFFFFFFFL;
        break;
      }
      case TCKind._tk_float: {
        l = Float.floatToIntBits(s.read_float()) & 0xFFFFFFFFL;
        break;
      }
      case TCKind._tk_double: {
        l = Double.doubleToLongBits(s.read_double());
        break;
      }
      case TCKind._tk_char: {
        l = s.read_char() & 0xFFFFL;
        break;
      }
      case TCKind._tk_octet: {
        l = s.read_octet() & 0xFFL;
        break;
      }
      case TCKind._tk_boolean: {
        if (s.read_boolean()) {
          l = 1;
        } else {
          l = 0;
        }

        break;
      }
      case TCKind._tk_any: {
        o = s.read_any();
        break;
      }
      case TCKind._tk_TypeCode: {
        o = s.read_TypeCode();
        break;
      }
      case TCKind._tk_Principal: {
        o = s.read_Principal();
        break;
      }
      case TCKind._tk_objref: {
        if (o != null && o instanceof Streamable) {
          ((Streamable) o)._read(s);
        } else {
          o = s.read_Object();
        }

        break;
      }
      case TCKind._tk_longlong: {
        l = s.read_longlong();
        break;
      }
      case TCKind._tk_ulonglong: {
        l = s.read_ulonglong();
        break;
      }
      case TCKind._tk_wchar: {
        l = s.read_wchar() & 0xFFFFL;
        break;
      }
      case TCKind._tk_string: {
        o = s.read_string();
        break;
      }
      case TCKind._tk_wstring: {
        o = s.read_wstring();
        break;
      }
      case TCKind._tk_value_box:
      case TCKind._tk_native:  // TODO ??
      case TCKind._tk_value: {
        o = ((org.omg.CORBA_2_3.portable.InputStream) s).read_value();
        break;
      }
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum:
      case TCKind._tk_sequence:
      case TCKind._tk_array:
      case TCKind._tk_alias:
      case TCKind._tk_except:
      case TCKind._tk_longdouble: {
        ((Streamable) o)._read(s);
        break;
      }
      case TCKind._tk_abstract_interface: {
        o = ((org.omg.CORBA_2_3.portable.InputStream) s).read_abstract_interface();
      }
    }

    oa[0] = o;
    la[0] = l;
  }

}

