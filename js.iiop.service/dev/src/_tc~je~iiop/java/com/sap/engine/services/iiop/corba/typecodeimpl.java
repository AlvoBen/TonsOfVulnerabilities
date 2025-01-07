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
import org.omg.CORBA.*;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/* This is the implementation of the org.omg.CORBA.TypeCode abstract class,
 used as a container for information about a specific CORBA data type (such as
 unions, sequences, etc.)...
 This implementation has some additional methods (not required in the TypeCode
 super class) as read_value() and write_value() which carry the TypeCode through
 input and output streams.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public final class TypeCodeImpl extends TypeCode {

  // the predefined typecode constants
  private transient org.omg.CORBA.ORB orb;
  private static final TypeCodeImpl primitiveConstants[] = {
    new TypeCodeImpl(TCKind._tk_null),                // tk_null
    new TypeCodeImpl(TCKind._tk_void),                // tk_void
    new TypeCodeImpl(TCKind._tk_short),               // tk_short
    new TypeCodeImpl(TCKind._tk_long),                // tk_long
    new TypeCodeImpl(TCKind._tk_ushort),              // tk_ushort
    new TypeCodeImpl(TCKind._tk_ulong),               // tk_ulong
    new TypeCodeImpl(TCKind._tk_float),               // tk_float
    new TypeCodeImpl(TCKind._tk_double),              // tk_double
    new TypeCodeImpl(TCKind._tk_boolean),             // tk_boolean
    new TypeCodeImpl(TCKind._tk_char),                // tk_char
    new TypeCodeImpl(TCKind._tk_octet),               // tk_octet
    new TypeCodeImpl(TCKind._tk_any),                 // tk_any
    new TypeCodeImpl(TCKind._tk_TypeCode),            // tk_typecode
    new TypeCodeImpl(TCKind._tk_Principal),           // tk_principal
    new TypeCodeImpl(TCKind._tk_objref),              // tk_objref
    null, // tk_struct
    null, // tk_union
    null, // tk_enum
    new TypeCodeImpl(TCKind._tk_string),              // tk_string
    null, // tk_sequence
    null, // tk_array
    null, // tk_alias
    null, // tk_except
    new TypeCodeImpl(TCKind._tk_longlong),            // tk_longlong
    new TypeCodeImpl(TCKind._tk_ulonglong),           // tk_ulonglong
    new TypeCodeImpl(TCKind._tk_longdouble),          // tk_longdouble
    new TypeCodeImpl(TCKind._tk_wchar),               // tk_wchar
    new TypeCodeImpl(TCKind._tk_wstring),             // tk_wstring
    new TypeCodeImpl(TCKind._tk_fixed),               // tk_fixed
    new TypeCodeImpl(TCKind._tk_value),               // tk_value
    new TypeCodeImpl(TCKind._tk_value_box),           // tk_value_box
    new TypeCodeImpl(TCKind._tk_native),              // tk_native
    new TypeCodeImpl(TCKind._tk_abstract_interface)   // tk__abstract_interface
  };
  // the indirection TCKind, needed for recursive typecodes.
  protected static final int tk_indirect = 0xFFFFFFFF;
  private static final int EMPTY = 0;   // no parameters
  private static final int SIMPLE = 1;  // simple parameters.
  private static final int COMPLEX = 2; // complex parameters.
  // need to use CDR encapsulation for parameters
    // a table storing the encoding category for the various typecodes.
  private static final int typeTable[] = {
    EMPTY,    // tk_null
    EMPTY,    // tk_void
    EMPTY,    // tk_short
    EMPTY,    // tk_long
    EMPTY,    // tk_ushort
    EMPTY,    // tk_ulong
    EMPTY,    // tk_float
    EMPTY,    // tk_double
    EMPTY,    // tk_boolean
    EMPTY,    // tk_char
    EMPTY,    // tk_octet
    EMPTY,    // tk_any
    EMPTY,    // tk_typecode
    EMPTY,    // tk_principal
    COMPLEX,  // tk_objref
    COMPLEX,  // tk_struct
    COMPLEX,  // tk_union
    COMPLEX,  // tk_enum
    SIMPLE,   // tk_string
    COMPLEX,  // tk_sequence
    COMPLEX,  // tk_array
    COMPLEX,  // tk_alias
    COMPLEX,  // tk_except
    EMPTY,    // tk_longlong
    EMPTY,    // tk_ulonglong
    EMPTY,    // tk_longdouble
    EMPTY,    // tk_wchar
    SIMPLE,   // tk_wstring
    SIMPLE,   // tk_fixed
    COMPLEX,  // tk_value
    COMPLEX,  // tk_value_box
    COMPLEX,  // tk_native
    COMPLEX   // tk_abstract_interface
  };
    // the typecode kind
  private int _kind = 0;
  // data members for representing the various kinds of typecodes.
    // the typecode repository id
  private String _id = "";
    // the typecode name
  private String _name = "";
    // member count
  private int _memberCount = 0;
    // names of members
  private String _memberNames[] = null;
    // types of members
  private TypeCodeImpl _memberTypes[] = null;
    // values of union labels
  private AnyImpl _unionLabels[] = null;
    // union discriminator type
  private TypeCodeImpl _discriminator = null;
    // union default index
  private int _defaultIndex = -1;
    // string/seq/array length
  private int _length = 0;
    // seq/array/alias type
  private TypeCodeImpl _contentType = null;

  private short           _type_modifier  = -1;   // VM_NONE, VM_CUSTOM,
// VM_ABSTRACT, VM_TRUNCATABLE
  private TypeCodeImpl    _concrete_base  = null; // concrete base type
  private short           _memberAccess[] = null; // visibility of ValueMember

  ///////////////////////////////////////////////////////////////////////////
  // Constructors...

  public TypeCodeImpl(org.omg.CORBA.ORB orb0, TCKind kind) {
    this(kind.value());
    orb = orb0;
  }

  public TypeCodeImpl(org.omg.CORBA.ORB orb0) {
    orb = orb0;
  }

  public TypeCodeImpl(org.omg.CORBA.ORB orb0, TypeCode tc) {
    _kind = tc.kind().value();
    orb = orb0;
    try {
      // set up parameters
      switch (_kind) {
        case TCKind._tk_except:
        case TCKind._tk_struct:
        case TCKind._tk_union: { //$JL-SWITCH$
          // set up member types
          _memberTypes = new TypeCodeImpl[tc.member_count()];

          for (int i = 0; i < tc.member_count(); i++) {
            _memberTypes[i] = convertToNative(orb, tc.member_type(i));
          }
        }
        case TCKind._tk_enum: { //$JL-SWITCH$
          // set up member names
          _memberNames = new String[tc.member_count()];

          for (int i = 0; i < tc.member_count(); i++) {
            _memberNames[i] = tc.member_name(i);
          }
          // set up member count
          _memberCount = tc.member_count();
        }
        case TCKind._tk_objref:
        case TCKind._tk_value_box:
        case TCKind._tk_native:
        case TCKind._tk_abstract_interface:
        case TCKind._tk_alias: { //$JL-SWITCH$
          _id = tc.id();
          _name = tc.name();
        }
      }

      // set up stuff for unions
      switch (_kind) {
        case TCKind._tk_union: {
          _discriminator = convertToNative(orb, tc.discriminator_type());
          _defaultIndex = tc.default_index();
          _unionLabels = new AnyImpl[_memberCount];

          for (int i = 0; i < _memberCount; i++) {
            _unionLabels[i] = new AnyImpl(orb, tc.member_label(i));
          }

          break;
        }
      }

      // set up length
      switch (_kind) {
        case TCKind._tk_string:
        case TCKind._tk_wstring:
        case TCKind._tk_sequence:
        case TCKind._tk_array: {
          _length = tc.length();
        }
      }

      // set up content type
      switch (_kind) {
        case TCKind._tk_sequence:
        case TCKind._tk_array:
        case TCKind._tk_value_box:
        case TCKind._tk_alias: {
          _contentType = convertToNative(orb, tc.content_type());
        }
      }
    } catch (org.omg.CORBA.TypeCodePackage.Bounds e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.TypeCodeImpl(ORB , TypeCode)", LoggerConfigurator.exceptionTrace(e));
      }
    } catch (BadKind e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.TypeCodeImpl(ORB , TypeCode)", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

  protected TypeCodeImpl(int creationKind) {
    _kind = creationKind;

    // do initialization for special cases
    switch (_kind) {
      case TCKind._tk_objref: {
        // this is being used to create typecode for CORBA::Object
        _id = "IDL:omg.org/CORBA/Object:1.0";
        _name = "Object";
        break;
      }
      case TCKind._tk_string:
      case TCKind._tk_wstring: {
        _length = 0;
        break;
      }
    }
  }

  // for structs & exceptions
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, //for structs & exceptions
  String id, String name, StructMember[] members) {
    orb = orb0;
    if ((creationKind == TCKind._tk_struct) || (creationKind == TCKind._tk_except)) {
      _kind = creationKind;
      _id = id;
      _name = name;
      _memberCount = members.length;
      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      for (int i = 0; i < _memberCount; i++) {
        _memberNames[i] = members[i].name;
        _memberTypes[i] = convertToNative(orb, members[i].type);
      }
    }// else initializes to null
  }

  // for unions
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, String id, String name, TypeCode discriminator_type, UnionMember[] members) {
    orb = orb0;

    if (creationKind == TCKind._tk_union) {
      _kind = creationKind;
      _id = id;
      _name = name;
      _memberCount = members.length;
      _discriminator = convertToNative(orb, discriminator_type);
      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      _unionLabels = new AnyImpl[_memberCount];

      for (int i = 0; i < _memberCount; i++) {
        _memberNames[i] = members[i].name;
        _memberTypes[i] = convertToNative(orb, members[i].type);
        _unionLabels[i] = new AnyImpl(orb, members[i].label);

        if (_unionLabels[i].type().kind() == TCKind.tk_octet) {
          _defaultIndex = i;
        }
      }
    }// else initializes to null
  }

  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, String id, String name, short type_modifier, TypeCode concrete_base, ValueMember[] members) {
    orb = orb0;

    if (creationKind == TCKind._tk_value) {
      _kind = creationKind;
      _id = id;
      _name = name;
      _type_modifier = type_modifier;
      if (_concrete_base != null) {
        _concrete_base = convertToNative(orb0, concrete_base);
      }
      _memberCount = members.length;

      _memberNames = new String[_memberCount];
      _memberTypes = new TypeCodeImpl[_memberCount];
      _memberAccess = new short[_memberCount];

      for (int i = 0 ; i < _memberCount ; i++) {
        _memberNames[i] = members[i].name;
        _memberTypes[i] = convertToNative(orb0, members[i].type);
//        _memberTypes[i]._setParent(this);
        _memberAccess[i] = members[i].access;
      }
    } // else initializes to null
  }

  // for enums
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, String id, String name, String[] members) {
    orb = orb0;

    if (creationKind == TCKind._tk_enum) {
      _kind = creationKind;
      _id = id;
      _name = name;
      _memberCount = members.length;
      _memberNames = new String[_memberCount];

      for (int i = 0; i < _memberCount; i++) {
        _memberNames[i] = members[i];
      }
    }// else initializes to null
  }

  // for aliases
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, String id, String name, TypeCode original_type) {
    orb = orb0;

    if (creationKind == TCKind._tk_alias || creationKind == TCKind._tk_value_box) {
      _kind = creationKind;
      _id = id;
      _name = name;
      _contentType = convertToNative(orb, original_type);
    } // else initializes to null
  }

  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, String id, String name) {
    orb = orb0;
    if (creationKind == TCKind._tk_objref || creationKind == TCKind._tk_native || creationKind == TCKind._tk_abstract_interface) {
      _kind = creationKind;
      _id = id;
      _name = name;
    } // else initializes to null
  }

  // for strings
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, int bound) {
    orb = orb0;
    if ((creationKind == TCKind._tk_string) || (creationKind == TCKind._tk_wstring)) {
      _kind = creationKind;
      _length = bound;
    } // else initializes to null
  }

  // for sequences & arrays
  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, int bound, TypeCode element_type) {
    orb = orb0;
    if ((creationKind == TCKind._tk_sequence) || (creationKind == TCKind._tk_array)) {
      _kind = creationKind;
      _length = bound;
      _contentType = convertToNative(orb, element_type);
    } // else initializes to null
  }

  public TypeCodeImpl(org.omg.CORBA.ORB orb0, int creationKind, int bound, int offset) { // for recursive seqences
    orb = orb0;
    String messageWithId = "ID019057: Recursive seqencese is not implemented yet";
    if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
      LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.TypeCodeImpl(ORB , int, int, int)", messageWithId);
    }

    throw new NO_IMPLEMENT(messageWithId);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Other creation functions...
  public static TypeCodeImpl get_primitive_tc(TCKind tcKind) {
    return primitiveConstants[tcKind.value()];
  }

  protected static TypeCodeImpl convertToNative(org.omg.CORBA.ORB orb0, TypeCode tc) {
    if (tc instanceof TypeCodeImpl) {
      return (TypeCodeImpl) tc;
    } else {
      return new TypeCodeImpl(orb0, tc);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // TypeCode operations
  public boolean equals(java.lang.Object obj) {
    if (super.equals(obj)) {
      return true;
    }

    return this.equal((org.omg.CORBA.TypeCode) obj);
  }

  public int hashCode() {
    return super.hashCode();
  }


  public final boolean equal(TypeCode tc) {
    try {
      if (_kind != tc.kind().value()) {
        return false;
      }

      switch (typeTable[_kind]) {
        case EMPTY: {
          // no parameters to check.
          return true;
        }
        case SIMPLE: {
          switch (_kind) {
            case TCKind._tk_string:
            case TCKind._tk_wstring: {
              // check for bound.
              if (_length == tc.length()) {
                return true;
              } else {
                return false;
              }
            }
          }
        }
        case COMPLEX: {
          switch (_kind) {
            case TCKind._tk_native:
            case TCKind._tk_abstract_interface:
            case TCKind._tk_objref: {
              // check for logical id.
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // ignore name since its optional.
              return true;
            }
            case TCKind._tk_struct: {
              // check for member count
              if (_memberCount != tc.member_count()) {
                return false;
              }

              // check for repository id
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // check for member types.
              for (int i = 0; i < _memberCount; i++) {
                if (!_memberTypes[i].equal(tc.member_type(i))) {
                  return false;
                }
              }

              // ignore id and names since those are optional.
              return true;
            }
            case TCKind._tk_union: {
              // check for member count
              if (_memberCount != tc.member_count()) {
                return false;
              }

              // check for repository id
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // check for default index
              if (_defaultIndex != tc.default_index()) {
                return false;
              }

              // check for discriminator type
              if (!_discriminator.equal(tc.discriminator_type())) {
                return false;
              }

              // check for label types and values
              for (int i = 0; i < _memberCount; i++) {
                if (!_unionLabels[i].equal(tc.member_label(i))) {
                  return false;
                }
              }

              // check for branch types
              for (int i = 0; i < _memberCount; i++) {
                if (!_memberTypes[i].equal(tc.member_type(i))) {
                  return false;
                }
              }

              // ignore id and names since those are optional.
              return true;
            }
            case TCKind._tk_enum: {
              // check for repository id
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // check member count
              if (_memberCount != tc.member_count()) {
                return false;
              }

              // ignore names since those are optional.
              return true;
            }
            case TCKind._tk_sequence:
            case TCKind._tk_array: {
              // check bound/length
              if (_length != tc.length()) {
                return false;
              }

              // check content type
              if (!_contentType.equal(tc.content_type())) {
                return false;
              }

              // ignore id and name since those are optional.
              return true;
            }
            case TCKind._tk_value_box:
            case TCKind._tk_alias: {
              // check for repository id
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // check for equality with the true type
              return _contentType.equal(tc.content_type());
            }
            case TCKind._tk_except: {
              // check for member count
              if (_memberCount != tc.member_count()) {
                return false;
              }

              // check for repository id
              if (_id.compareTo(tc.id()) != 0) {
                return false;
              }

              // check for member types. ignore names since those are
              // optional anyway.
              for (int i = 0; i < _memberCount; i++) {
                if (!_memberTypes[i].equal(tc.member_type(i))) {
                  return false;
                }
              }

              return true;
            }
          }
        }
      }
    } catch (org.omg.CORBA.TypeCodePackage.Bounds e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.equal(TypeCode)", LoggerConfigurator.exceptionTrace(e));
      }
    } catch (BadKind e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.equal(TypeCode)", LoggerConfigurator.exceptionTrace(e));
      }
    }
    return false;
  }

  public TCKind kind() {
    return TCKind.from_int(_kind);
  }

  public String id() throws BadKind {
    switch (_kind) {
      case TCKind._tk_except:
      case TCKind._tk_objref:
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum:
      case TCKind._tk_native:
      case TCKind._tk_abstract_interface:
      case TCKind._tk_value_box:
      case TCKind._tk_alias: {
        return _id;
      }
      default: {
        // all other typecodes throw the BadKind exception.
        String messageWithId = "ID019058: Error typecode";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.id()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public String name() throws BadKind {
    switch (_kind) {
      case TCKind._tk_except:
      case TCKind._tk_objref:
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum:
      case TCKind._tk_native:
      case TCKind._tk_abstract_interface:
      case TCKind._tk_value_box:
      case TCKind._tk_alias: {
        return _name;
      }
      default: {
        String messageWithId = "ID019059: Error typecode";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.name()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public int member_count() throws BadKind {
    switch (_kind) {
      case TCKind._tk_except:
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum: {
        return _memberCount;
      }
      default: {
        String messageWithId = "ID019060: Error member count";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_count()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public String member_name(int index) throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds {
    switch (_kind) {
      case TCKind._tk_except:
      case TCKind._tk_struct:
      case TCKind._tk_union:
      case TCKind._tk_enum: {
        try {
          return _memberNames[index];
        } catch (ArrayIndexOutOfBoundsException e) {
          String messageWithId = "ID019061: Error member name";
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_name()", LoggerConfigurator.exceptionTrace(e));
          }
          throw new org.omg.CORBA.TypeCodePackage.Bounds(messageWithId);
        }
      }
      default: {
        String messageWithId = "ID019062: Error member name";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_name()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public TypeCode member_type(int index) throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds {
    switch (_kind) {
      case TCKind._tk_except:
      case TCKind._tk_struct:
      case TCKind._tk_union: {
        try {
          return _memberTypes[index];
        } catch (ArrayIndexOutOfBoundsException e) {
          String messageWithId = "ID019063: Error member type";
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_type()", LoggerConfigurator.exceptionTrace(e));
          }
          throw new org.omg.CORBA.TypeCodePackage.Bounds(messageWithId);
        }
      }
      default: {
        String messageWithId = "ID019064: Error member type";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_type()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public Any member_label(int index) throws BadKind, org.omg.CORBA.TypeCodePackage.Bounds {
    switch (_kind) {
      case TCKind._tk_union: {
        try {
          return new AnyImpl(orb, _unionLabels[index]);
        } catch (ArrayIndexOutOfBoundsException e) {
          String messageWithId = "ID019065: Error member label";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_label()", LoggerConfigurator.exceptionTrace(e));
        }
          throw new org.omg.CORBA.TypeCodePackage.Bounds(messageWithId);
        }
      }
      default: {
        String messageWithId = "ID019066: Error member label";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.member_label()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public TypeCode discriminator_type() throws BadKind {
    switch (_kind) {
      case TCKind._tk_union: {
        return _discriminator;
      }
      default: {
        String messageWithId = "ID019067: Error discriminator type";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.discriminator_type()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public int default_index() throws BadKind {
    switch (_kind) {
      case TCKind._tk_union: {
        return _defaultIndex;
      }
      default: {
        String messageWithId = "ID019068: Error default index";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.default_index()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public int length() throws BadKind {
    switch (_kind) {
      case TCKind._tk_string:
      case TCKind._tk_wstring:
      case TCKind._tk_sequence:
      case TCKind._tk_array: {
        return _length;
      }
      default: {
        String messageWithId = "ID019069: Incorect length";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.length()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  public TypeCode content_type() throws BadKind {
    switch (_kind) {
      case TCKind._tk_sequence:
      case TCKind._tk_array:
      case TCKind._tk_value_box:
      case TCKind._tk_alias: {
        return _contentType;
      }
      default: {
        String messageWithId = "ID019070: Error content type";
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.content_type()", messageWithId);
        }
        throw new BadKind(messageWithId);
      }
    }
  }

  //TODO
  public TypeCode concrete_base_type() throws BadKind {
    return null;
  }
  //TODO
  public boolean equivalent(TypeCode tc) {
    return false;
  }
  //TODO
  public short fixed_digits() throws BadKind {
    return 0;
  }
  //TODO
  public short fixed_scale() throws BadKind {
    return 0;
  }

  public TypeCode get_compact_typecode() {
    return this;
  }
  //TODO
  public short member_visibility(int index) throws BadKind, Bounds {
    return 0;
  }
  //TODO
  public short type_modifier() throws BadKind {
    return 0;
  }

  public void read_value(InputStream s) {
    CORBAInputStream _encap = null;
    // unmarshal the kind
    _kind = s.read_long();

    // check validity of kind
    if (_kind < 0 || _kind > typeTable.length) {
      String messageWithId = "ID019071: Invalid type kind " + _kind;
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.read_value()", messageWithId);
      }
      throw new MARSHAL(messageWithId);
    }

    switch (typeTable[_kind]) {
      case EMPTY: {
        // nothing to unmarshal
        break;
      }
      case SIMPLE: {
        switch (_kind) {
          case TCKind._tk_string:
          case TCKind._tk_wstring: {
            _length = s.read_long();
            break;
          }
          default: {
            String messageWithId = "ID019072: Incorrect type kind " + _kind;
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.read_value()", messageWithId);
            }
            throw new MARSHAL(messageWithId);
          }
        }

        break;
      }
      case COMPLEX: {
        // pop off the encapsulation length
        int encapLength = s.read_long();
        // read off part of the buffer corresponding to the encapsulation
        byte[] encapBuffer = new byte[encapLength];
        s.read_octet_array(encapBuffer, 0, encapBuffer.length);

        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beInfo()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).infoT("TypeCode length:" + encapLength+" TypeCode :" + new String(encapBuffer));
        }
        // create an encapsulation using the marshal buffer
        _encap = new CORBAInputStream(orb, encapBuffer);
        // read and set the endianness of the encapsulation
        _encap.setEndian(_encap.read_boolean());

        switch (_kind) {
          case TCKind._tk_TypeCode: {
            s.read_TypeCode();
            break;
          }
          case TCKind._tk_objref:
          case TCKind._tk_abstract_interface: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
            }

            break;
          }
          case TCKind._tk_struct: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
              // get the number of members
              _memberCount = _encap.read_long();
              // create arrays for the names and types of members
              _memberNames = new String[_memberCount];
              _memberTypes = new TypeCodeImpl[_memberCount];

              // read off member names and types
              for (int i = 0; i < _memberCount; i++) {
                _memberNames[i] = _encap.read_string();
                _memberTypes[i] = new TypeCodeImpl(orb);
                _memberTypes[i].read_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_union: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
              // discriminant typecode
              _discriminator = new TypeCodeImpl(orb);
              _discriminator.read_value(_encap);
              // default index
              _defaultIndex = _encap.read_long();
              // get the number of members
              _memberCount = _encap.unaligned_read_long();
              // create arrays for the label values, names and types of members
              _unionLabels = new AnyImpl[_memberCount];
              _memberNames = new String[_memberCount];
              _memberTypes = new TypeCodeImpl[_memberCount];

              // read off label values, names and types
              for (int i = 0; i < _memberCount; i++) {
                _unionLabels[i] = new AnyImpl(orb);

                if (i == _defaultIndex) {
                  // for the default case, read off the zero octet
                  _unionLabels[i].insert_octet(_encap.read_octet());
                } else {
                  switch (_discriminator.kind().value()) {
                    case TCKind._tk_short: {
                      _unionLabels[i].insert_short(_encap.read_short());
                      break;
                    }
                    case TCKind._tk_long: {
                      _unionLabels[i].insert_long(_encap.read_long());
                      break;
                    }
                    case TCKind._tk_ushort: {
                      _unionLabels[i].insert_ushort(_encap.read_short());
                      break;
                    }
                    case TCKind._tk_ulong: {
                      _unionLabels[i].insert_ulong(_encap.read_long());
                      break;
                    }
                    case TCKind._tk_float: {
                      _unionLabels[i].insert_float(_encap.read_float());
                      break;
                    }
                    case TCKind._tk_double: {
                      _unionLabels[i].insert_double(_encap.read_double());
                      break;
                    }
                    case TCKind._tk_boolean: {
                      _unionLabels[i].insert_boolean(_encap.read_boolean());
                      break;
                    }
                    case TCKind._tk_char: {
                      _unionLabels[i].insert_char(_encap.read_char());
                      break;
                    }
                    case TCKind._tk_wchar: {
                      _unionLabels[i].insert_wchar(_encap.read_wchar());
                      break;
                    }
                    case TCKind._tk_enum: {
                      int value = _encap.read_long();
                      org.omg.CORBA.portable.OutputStream out = _unionLabels[i].create_output_stream();
                      out.write_long(value);
                      _unionLabels[i].read_value(out.create_input_stream(), _discriminator);
                      break;
                    }
                    case TCKind._tk_longlong: {
                      _unionLabels[i].insert_longlong(_encap.read_longlong());
                      break;
                    }
                    case TCKind._tk_ulonglong: {
                      _unionLabels[i].insert_ulonglong(_encap.read_longlong());
                      break;
                    }
                    default: {
                      String messageWithId = "ID019073: Incorrect label values, names or types";
                      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
                        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.read_value()", messageWithId);
                      }
                      throw new MARSHAL(messageWithId);
                    }
                  }
                }

                _memberNames[i] = _encap.read_string();
                _memberTypes[i] = new TypeCodeImpl(orb);
                _memberTypes[i].read_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_enum: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
              // get the number of members
              _memberCount = _encap.read_long();
              // create arrays for the identifier names
              _memberNames = new String[_memberCount];

              // read off identifier names
              for (int i = 0; i < _memberCount; i++) {
                _memberNames[i] = _encap.read_string();
              }
            }

            break;
          }
          case TCKind._tk_sequence: {
            {
              // get the type of the sequence
              _contentType = new TypeCodeImpl(orb);
              _contentType.read_value(_encap);
              // get the bound on the length of the sequence
              _length = _encap.read_long();
            }

            break;
          }
          case TCKind._tk_array: {
            {
              // get the type of the array
              _contentType = new TypeCodeImpl(orb);
              _contentType.read_value(_encap);
              // get the length of the array
              _length = _encap.read_long();
            }

            break;
          }
          case TCKind._tk_value_box:
          case TCKind._tk_alias: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
              // get the type aliased
              _contentType = new TypeCodeImpl(orb);
              _contentType.read_value(_encap);
            }

            break;
          }
          case TCKind._tk_except: {
            {
              // get the repository id
              _id = _encap.read_string();
              // get the name
              _name = _encap.read_string();
              // get the number of members
              _memberCount = _encap.read_long();
              // create arrays for the names and types of members
              _memberNames = new String[_memberCount];
              _memberTypes = new TypeCodeImpl[_memberCount];

              // read off member names and types
              for (int i = 0; i < _memberCount; i++) {
                _memberNames[i] = _encap.read_string();
                _memberTypes[i] = new TypeCodeImpl(orb);
                _memberTypes[i].read_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_value: {
            _encap.read_string();
            _encap.read_string();
            _encap.read_short();
            (new TypeCodeImpl(0)).read_value(_encap);
            _encap.read_long();
            return;
          }
          default: {
            String messageWithId = "ID019074: Incorrect type kind " + _kind ;
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.read_value()", messageWithId);
            }

            throw new MARSHAL(messageWithId);
          }
        }

        break;
      }
    }
  }

  public void write_value(OutputStream s) {
    CORBAOutputStream _encap = null;
    // marshal the kind
    s.write_long(_kind);

    switch (typeTable[_kind]) {
      case EMPTY: {
        // nothing more to marshal
        break;
      }
      case SIMPLE: {
        switch (_kind) {
          case TCKind._tk_string:
          case TCKind._tk_wstring: {
            // marshal the bound on string length
            s.write_long(_length);
            break;
          }
          default: {
            // unknown typecode kind
            String messageWithId = "ID019075: Unknown typecode kind";
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.write_value()", messageWithId);
            }
            throw new MARSHAL(messageWithId);
          }
        }

        break;
      }
      case COMPLEX: {
        // create an encapsulation
        _encap = new CORBAOutputStream(orb);
        // stick the endianness of the encapsulation
        _encap.write_boolean(false);

        switch (_kind) {
          case TCKind._tk_abstract_interface:
          case TCKind._tk_objref: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
            }

            break;
          }
          case TCKind._tk_struct: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
              // put the number of members
              _encap.write_long(_memberCount);

              // marshal the member names and types
              for (int i = 0; i < _memberCount; i++) {
                _encap.write_string(_memberNames[i]);
                _memberTypes[i].write_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_union: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
              // discriminant typecode
              _discriminator.write_value(_encap);
              // default index
              _encap.write_long(_defaultIndex);
              // put the number of members
              _encap.write_long(_memberCount);

              // marshal label values, names and types
              for (int i = 0; i < _memberCount; i++) {
                // for the default case, marshal the zero octet
                if (i == _defaultIndex) {
                  _encap.write_octet(_unionLabels[i].extract_octet());
                } else {
                  switch (_discriminator.kind().value()) {
                    case TCKind._tk_short: {
                      _encap.write_short(_unionLabels[i].extract_short());
                      break;
                    }
                    case TCKind._tk_long: {
                      _encap.write_long(_unionLabels[i].extract_long());
                      break;
                    }
                    case TCKind._tk_ushort: {
                      _encap.write_short(_unionLabels[i].extract_ushort());
                      break;
                    }
                    case TCKind._tk_ulong: {
                      _encap.write_long(_unionLabels[i].extract_ulong());
                      break;
                    }
                    case TCKind._tk_float: {
                      _encap.write_float(_unionLabels[i].extract_float());
                      break;
                    }
                    case TCKind._tk_double: {
                      _encap.write_double(_unionLabels[i].extract_double());
                      break;
                    }
                    case TCKind._tk_boolean: {
                      _encap.write_boolean(_unionLabels[i].extract_boolean());
                      break;
                    }
                    case TCKind._tk_char: {
                      _encap.write_char(_unionLabels[i].extract_char());
                      break;
                    }
                    case TCKind._tk_wchar: {
                      _encap.write_wchar(_unionLabels[i].extract_wchar());
                      break;
                    }
                    case TCKind._tk_enum: {
                      int value = _unionLabels[i].create_input_stream().read_long();
                      _encap.write_long(value);
                      break;
                    }
                    case TCKind._tk_longlong: {
                      _encap.write_longlong(_unionLabels[i].extract_longlong());
                      break;
                    }
                    case TCKind._tk_ulonglong: {
                      _encap.write_longlong(_unionLabels[i].extract_ulonglong());
                      break;
                    }
                    default: {
                      throw new MARSHAL("ID019076: Not supported discriminator kind " + _discriminator.kind().value());
                    }
                  }
                }

                _encap.write_string(_memberNames[i]);
                _memberTypes[i].write_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_enum: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
              // put the number of members
              _encap.write_long(_memberCount);

              // marshal identifier names
              for (int i = 0; i < _memberCount; i++) {
                _encap.write_string(_memberNames[i]);
              }
            }

            break;
          }
          case TCKind._tk_sequence: {
            {
              // put the type of the sequence
              _contentType.write_value(_encap);
              // put the bound on the length of the sequence
              _encap.write_long(_length);
            }

            break;
          }
          case TCKind._tk_array: {
            {
              // put the type of the array
              _contentType.write_value(_encap);
              // put the length of the array
              _encap.write_long(_length);
            }

            break;
          }
          case TCKind._tk_value_box:
          case TCKind._tk_alias: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
              // put the type aliased
              _contentType.write_value(_encap);
            }

            break;
          }
          case TCKind._tk_except: {
            {
              // put the repository id
              _encap.write_string(_id);
              // put the name
              _encap.write_string(_name);
              // put the number of members
              _encap.write_long(_memberCount);

              // marshal member names and types
              for (int i = 0; i < _memberCount; i++) {
                _encap.write_string(_memberNames[i]);
                _memberTypes[i].write_value(_encap);
              }
            }

            break;
          }
          case TCKind._tk_value: {
            _encap.write_string(_id);
            _encap.write_string(_name);
            _encap.write_short(_type_modifier);

            if (_concrete_base == null) {
              primitiveConstants[TCKind._tk_null].write_value(_encap);
            } else {
              _concrete_base.write_value(_encap);
            }

            _encap.write_long(_memberCount);

            // marshal member names and types
            for (int i=0; i < _memberCount; i++) {
              _encap.write_string(_memberNames[i]);
              _memberTypes[i].write_value(_encap);
              _encap.write_short(_memberAccess[i]);
            }
            break;
          }
          default: {
            throw new MARSHAL("ID019077: Invalid type kind " + _kind);
          }
        }
        s.write_long(_encap.byteArray_forSend_length());
        s.write_octet_array(_encap.toByteArray_forSend(), 0, _encap.byteArray_forSend_length());
        break;
      }
    }
  }

  protected void copy(InputStream src, OutputStream dst) {
    switch (_kind) {
      case TCKind._tk_null:
      case TCKind._tk_void: {
        break;
      }
      case TCKind._tk_short:
      case TCKind._tk_ushort: {
        dst.write_short(src.read_short());
        break;
      }
      case TCKind._tk_long:
      case TCKind._tk_ulong: {
        dst.write_long(src.read_long());
        break;
      }
      case TCKind._tk_float: {
        dst.write_float(src.read_float());
        break;
      }
      case TCKind._tk_double: {
        dst.write_double(src.read_double());
        break;
      }
      case TCKind._tk_longlong:
      case TCKind._tk_ulonglong: {
        dst.write_longlong(src.read_longlong());
        break;
      }
      case TCKind._tk_longdouble: {
        dst.write_double(src.read_double());
        break;
      }
      case TCKind._tk_boolean: {
        dst.write_boolean(src.read_boolean());
        break;
      }
      case TCKind._tk_char: {
        dst.write_char(src.read_char());
        break;
      }
      case TCKind._tk_wchar: {
        dst.write_wchar(src.read_wchar());
        break;
      }
      case TCKind._tk_octet: {
        dst.write_octet(src.read_octet());
        break;
      }
      case TCKind._tk_string: {
        //  case TCKind._tk_wstring:
        {
          String s;
          s = src.read_string();

          // make sure length bound in typecode is not violated
          if ((_length != 0) && (s.length() > _length)) {
            String messageWithId = "ID019078: Length bound in typecode is violated. Length: " + _length + " String: " + s.length();
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.copy(InputStream, OutputStream)", messageWithId);
            }
            throw new MARSHAL(messageWithId);
          }

          dst.write_string(s);
        }

        break;
      }
      case TCKind._tk_wstring: {
        {
          String s;
          s = src.read_wstring();

          // make sure length bound in typecode is not violated
          if ((_length != 0) && (s.length() > _length)) {
            String messageWithId = "ID019078: Length bound in typecode is violated. Length: " + _length + " WString: " + s.length();
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.copy(InputStream, OutputStream)", messageWithId);
            }
            throw new MARSHAL(messageWithId);
          }

          dst.write_wstring(s);
        }
        break;
      }
      case TCKind._tk_any: {
        Any tmp = new AnyImpl(orb);
        TypeCodeImpl t = new TypeCodeImpl(orb);
        t.read_value(src);
        t.write_value(dst);
        tmp.read_value(src, t);
        tmp.write_value(dst);
        break;
      }
      case TCKind._tk_TypeCode: {
        dst.write_TypeCode(src.read_TypeCode());
        break;
      }
      case TCKind._tk_Principal: {
        dst.write_Principal(src.read_Principal());
        break;
      }
      case TCKind._tk_objref: {
        dst.write_Object(src.read_Object());
        break;
      }
      case TCKind._tk_except: {        //$JL-SWITCH$
        // Copy repositoryId
        dst.write_string(src.read_string());

      }


      // Fall into ...
      case TCKind._tk_struct: {
        // copy each element, using the corresponding member type
        for (int i = 0; i < _memberTypes.length; i++) {
          _memberTypes[i].copy(src, dst);
        }

        break;
      }
      case TCKind._tk_union: {
        Any tagValue = new AnyImpl(orb);

        switch (_discriminator.kind().value()) {
          case TCKind._tk_short: {
            short value = src.read_short();
            tagValue.insert_short(value);
            dst.write_short(value);
            break;
          }
          case TCKind._tk_long: {
            int value = src.read_long();
            tagValue.insert_long(value);
            dst.write_long(value);
            break;
          }
          case TCKind._tk_ushort: {
            short value = src.read_short();
            tagValue.insert_ushort(value);
            dst.write_short(value);
            break;
          }
          case TCKind._tk_ulong: {
            int value = src.read_long();
            tagValue.insert_ulong(value);
            dst.write_long(value);
            break;
          }
          case TCKind._tk_float: {
            float value = src.read_float();
            tagValue.insert_float(value);
            dst.write_float(value);
            break;
          }
          case TCKind._tk_double: {
            double value = src.read_double();
            tagValue.insert_double(value);
            dst.write_double(value);
            break;
          }
          case TCKind._tk_boolean: {
            boolean value = src.read_boolean();
            tagValue.insert_boolean(value);
            dst.write_boolean(value);
            break;
          }
          case TCKind._tk_char: {
            char value = src.read_char();
            tagValue.insert_char(value);
            dst.write_char(value);
            break;
          }
          case TCKind._tk_enum: {
            int value = src.read_long();
            tagValue.type(_discriminator);
            org.omg.CORBA.portable.OutputStream out = tagValue.create_output_stream();
            out.write_long(value);
            tagValue.read_value(out.create_input_stream(), _discriminator);
            dst.write_long(value);
            break;
          }
          case TCKind._tk_longlong: {
            long value = src.read_longlong();
            tagValue.insert_longlong(value);
            dst.write_longlong(value);
            break;
          }
          case TCKind._tk_ulonglong: {
            long value = src.read_longlong();
            tagValue.insert_ulonglong(value);
            dst.write_longlong(value);
            break;
          }
          default: {
            throw new MARSHAL("ID019079: Not supported discriminator kind " + _discriminator.kind().value());
          }
        }

        // using the value of the tag, find out the type of the value
        // following.
        int labelIndex;

        for (labelIndex = 0; labelIndex < _unionLabels.length; labelIndex++) {
          // use equality over anys
          if (tagValue.equal(_unionLabels[labelIndex])) {
            _memberTypes[labelIndex].copy(src, dst);
            break;
          }
        }

        if (labelIndex == _unionLabels.length) {
          // check if label has not been found
          if (_defaultIndex == -1) {
            // throw exception if default was not expected;
            String messageWithId = "ID019080: Default label has not been found";
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.copy(InputStream, OutputStream)", messageWithId);
            }
            throw new MARSHAL(messageWithId);
          } else {
            // must be of the default branch type
            _memberTypes[_defaultIndex].copy(src, dst);
          }
        }

        break;
      }
      case TCKind._tk_enum: {
        dst.write_long(src.read_long());
        break;
      }
      case TCKind._tk_sequence: {
        // get the length of the sequence
        int seqLength = src.read_long();

        // check for sequence bound violated
        if ((_length != 0) && (seqLength > _length)) {
          String messageWithId = "ID019081: Sequence bound is violated. Length: " + _length + " Sequence: " + seqLength;
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("TypeCodeImpl.copy(InputStream, OutputStream)", messageWithId);
          }
          throw new MARSHAL(messageWithId);
        }

        // write the length of the sequence
        dst.write_long(seqLength);

        // copy each element of the sequence using content type
        for (int i = 0; i < seqLength; i++) {
          _contentType.copy(src, dst);
        }

        break;
      }
      case TCKind._tk_array: {
        // copy each element of the array using content type
        for (int i = 0; i < _length; i++) {
          _contentType.copy(src, dst);
        }

        break;
      }
      case TCKind._tk_value_box:
      case TCKind._tk_alias: {
        // follow the alias
        _contentType.copy(src, dst);
        break;
      }
      default: {
        throw new MARSHAL("ID019082: Invalid type kind " + _kind);
      }
    }
  }
}