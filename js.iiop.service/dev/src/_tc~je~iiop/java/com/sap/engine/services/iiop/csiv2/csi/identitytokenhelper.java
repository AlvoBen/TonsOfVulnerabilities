package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class IdentityTokenHelper {

  public IdentityTokenHelper() {

  }

  public static void insert(Any any, IdentityToken identitytoken) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, identitytoken);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static IdentityToken extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      TypeCode typecode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
      //            typecode = ORB.init().create_alias_tc(IdentityTokenTypeHelper.id(), "IdentityTokenType", typecode);
      UnionMember aunionmember[] = new UnionMember[6];
      Any any = ORB.init().create_any();
      any.insert_ulong(0);
      TypeCode typecode1 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
      aunionmember[0] = new UnionMember("absent", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_ulong(1);
      typecode1 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
      aunionmember[1] = new UnionMember("anonymous", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_ulong(2);
      typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      typecode1 = ORB.init().create_sequence_tc(0, typecode1);
      //            typecode1 = ORB.init().create_alias_tc(GSS_NT_ExportedNameHelper.id(), "GSS_NT_ExportedName", typecode1);
      aunionmember[2] = new UnionMember("principal_name", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_ulong(4);
      typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      typecode1 = ORB.init().create_sequence_tc(0, typecode1);
      //            typecode1 = ORB.init().create_alias_tc(X509CertificateChainHelper.id(), "X509CertificateChain", typecode1);
      aunionmember[3] = new UnionMember("certificate_chain", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_ulong(8);
      typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      typecode1 = ORB.init().create_sequence_tc(0, typecode1);
      //            typecode1 = ORB.init().create_alias_tc(X501DistinguishedNameHelper.id(), "X501DistinguishedName", typecode1);
      aunionmember[4] = new UnionMember("dn", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_octet((byte) 0);
      typecode1 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      typecode1 = ORB.init().create_sequence_tc(0, typecode1);
      //            typecode1 = ORB.init().create_alias_tc(IdentityExtensionHelper.id(), "IdentityExtension", typecode1);
      aunionmember[5] = new UnionMember("id", any, typecode1, null);
      __typeCode = ORB.init().create_union_tc(id(), "IdentityToken", typecode, aunionmember);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static IdentityToken read(InputStream inputstream) {
    IdentityToken identitytoken = new IdentityToken();
    int i = 0;
    i = inputstream.read_ulong();

    switch (i) {
      case 0: // '\0'
      {
        boolean flag = false;
        flag = inputstream.read_boolean();
        identitytoken.absent(flag);
        break;
      }
      case 1: // '\001'
      {
        boolean flag1 = false;
        flag1 = inputstream.read_boolean();
        identitytoken.anonymous(flag1);
        break;
      }
      case 2: // '\002'
      {
        byte abyte0[] = null;
        abyte0 = GSS_NT_ExportedNameHelper.read(inputstream);
        identitytoken.principal_name(abyte0);
        break;
      }
      case 4: // '\004'
      {
        byte abyte1[] = null;
        abyte1 = X509CertificateChainHelper.read(inputstream);
        identitytoken.certificate_chain(abyte1);
        break;
      }
      case 8: // '\b'
      {
        byte abyte2[] = null;
        abyte2 = X501DistinguishedNameHelper.read(inputstream);
        identitytoken.dn(abyte2);
        break;
      }
      case 3: // '\003'
      case 5: // '\005'
      case 6: // '\006'
      case 7: // '\007'
      default: {
        byte abyte3[] = null;
        abyte3 = IdentityExtensionHelper.read(inputstream);
        identitytoken.id(abyte3);
        break;
      }
    }

    return identitytoken;
  }

  public static void write(OutputStream outputstream, IdentityToken identitytoken) {
    outputstream.write_ulong(identitytoken.discriminator());

    switch (identitytoken.discriminator()) {
      case 0: // '\0'
      {
        outputstream.write_boolean(identitytoken.absent());
        break;
      }
      case 1: // '\001'
      {
        outputstream.write_boolean(identitytoken.anonymous());
        break;
      }
      case 2: // '\002'
      {
        GSS_NT_ExportedNameHelper.write(outputstream, identitytoken.principal_name());
        break;
      }
      case 4: // '\004'
      {
        X509CertificateChainHelper.write(outputstream, identitytoken.certificate_chain());
        break;
      }
      case 8: // '\b'
      {
        X501DistinguishedNameHelper.write(outputstream, identitytoken.dn());
        break;
      }
      case 3: // '\003'
      case 5: // '\005'
      case 6: // '\006'
      case 7: // '\007'
      default: {
        IdentityExtensionHelper.write(outputstream, identitytoken.id());
        break;
      }
    }
  }

  private static String _id = "IDL:omg.org/CSI/IdentityToken:1.0";
  private static TypeCode __typeCode = null;

}

